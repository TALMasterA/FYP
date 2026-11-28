/**
 * Translation & Language Detection Cloud Functions.
 *
 * Handles single/batch text translation and language detection
 * via the Azure Translator API.
 */
import {HttpsError} from "firebase-functions/v2/https";
import fetch from "node-fetch";
import {
  requireAuth,
  requireString,
  safeParseJson,
  validateLanguageCode,
  buildTranslateUrl,
  checkWriteRateLimit,
  AZURE_TRANSLATOR_KEY,
  AZURE_TRANSLATOR_REGION,
  AZURE_SPEECH_KEY,
  AZURE_SPEECH_REGION,
  ENDPOINT,
  API_VERSION,
  MAX_TRANSLATE_TEXT_LENGTH,
} from "./helpers.js";
import {onAppCheckCall} from "./functionWrappers.js";
import {logger} from "./logger.js";
import {reserveProviderAllowance} from "./providerAllowance.js";

// ── Batch content-translation rate limits ────────────────────────────
// Each translateTexts call = 1 Cloud Function call (server-side Azure chunking).
const CONTENT_TRANSLATE_AUTH_MAX = 20;
const CONTENT_TRANSLATE_AUTH_WINDOW_MS = 10 * 60 * 1000; // 10 minutes

// App-level bound to keep Translator requests and retries small.
const AZURE_CHUNK_SIZE = 100;
const MAX_BATCH_CHARACTERS = 50_000;
const PROVIDER_TIMEOUT_MS = 15_000;
const DEMO_SCALING = {maxInstances: 2, concurrency: 4, timeoutSeconds: 90};

// ── Azure chunk throttling ───────────────────────────────────────────
// Delay between consecutive Azure Translator API calls within a single
// batch to avoid hitting Azure's per-second request rate limit.
// This smooths each invocation. Azure separately applies resource-wide quotas.
const AZURE_INTER_CHUNK_DELAY_MS = 350;

// If an individual Azure chunk is throttled (HTTP 429), retry that chunk
// up to this many times with exponential backoff (or Azure's Retry-After
// value when provided) before failing the entire request. This absorbs
// transient Azure throttling so the client rarely sees the error.
const AZURE_CHUNK_MAX_RETRIES = 3;
const AZURE_CHUNK_RETRY_BASE_MS = 750;
const AZURE_CHUNK_RETRY_MAX_MS = 6000;

function sleep(ms: number): Promise<void> {
  return new Promise((resolve) => setTimeout(resolve, ms));
}

function parseRetryAfterMs(value: string | null | undefined): number | null {
  const trimmed = value?.trim();
  if (!trimmed) return null;

  const seconds = Number(trimmed);
  if (Number.isFinite(seconds) && seconds >= 0) {
    return Math.max(250, Math.round(seconds * 1000));
  }

  const dateMs = Date.parse(trimmed);
  if (!Number.isNaN(dateMs)) {
    return Math.max(250, dateMs - Date.now());
  }

  return null;
}

function getChunkRetryDelayMs(
  response: {headers?: {get?: (name: string) => string | null}},
  attempt: number
): number {
  const retryAfterMs = parseRetryAfterMs(response.headers?.get?.("retry-after"));
  if (retryAfterMs != null) {
    return Math.min(retryAfterMs, AZURE_CHUNK_RETRY_MAX_MS);
  }

  return Math.min(
    AZURE_CHUNK_RETRY_BASE_MS * Math.pow(2, attempt),
    AZURE_CHUNK_RETRY_MAX_MS
  );
}

function throwTranslationApiError(
  operation: "Single" | "Batch" | "Detection",
  status: number,
  bodyText: string
): never {
  const serviceName = operation === "Detection" ? "Language detection service" : "Translation service";
  logger.error(`${operation} translation API error`, {
    status,
    errorPreview: bodyText.substring(0, 200),
  });

  if (status === 400) {
    throw new HttpsError(
      "invalid-argument",
      `${serviceName} request is invalid. Please check language selection and try again.`
    );
  }

  if (status === 401 || status === 403) {
    throw new HttpsError(
      "failed-precondition",
      `${serviceName} authentication failed. Please contact support.`
    );
  }

  if (status === 429) {
    throw new HttpsError(
      "resource-exhausted",
      `${serviceName} rate limit exceeded. Please try again in a few minutes.`
    );
  }

  if (status >= 500) {
    throw new HttpsError(
      "unavailable",
      `${serviceName} is temporarily unavailable. Please try again later.`
    );
  }

  throw new HttpsError(
    "internal",
    `${serviceName} unavailable. Please try again.`
  );
}

export const getSpeechToken = onAppCheckCall(
  {
    ...DEMO_SCALING,
    secrets: [AZURE_SPEECH_KEY, AZURE_SPEECH_REGION],
  },
  async (request) => {
    requireAuth(request.auth);

    const key = AZURE_SPEECH_KEY.value();
    const region = AZURE_SPEECH_REGION.value();

    const url = `https://${region}.api.cognitive.microsoft.com/sts/v1.0/issueToken`;

    await reserveProviderAllowance({
      uid: request.auth.uid,
      operation: "getSpeechToken",
      requestId: request.data?.requestId,
      charges: [{unit: "speechTokens", quantity: 1}],
    });

    const resp = await fetch(url, {
      timeout: PROVIDER_TIMEOUT_MS,
      redirect: "error",
      method: "POST",
      headers: {
        "Ocp-Apim-Subscription-Key": key,
        "Content-Length": "0",
      },
    });

    const token = await resp.text();
    if (!resp.ok) {
      logger.error("Speech token API error", {
        status: resp.status,
        errorPreview: token.substring(0, 200),
      });
      throw new HttpsError(
        "internal", "Speech service unavailable. Please try again."
      );
    }

    return {token, region};
  }
);

export const translateText = onAppCheckCall(
  {
    secrets: [AZURE_TRANSLATOR_KEY, AZURE_TRANSLATOR_REGION],
    ...DEMO_SCALING,
    cpu: 1,
    memory: "512MiB",
  },
  async (request) => {
    requireAuth(request.auth);

    const text = requireString(request.data?.text, "text");
    if (text.length > MAX_TRANSLATE_TEXT_LENGTH) {
      throw new HttpsError(
        "invalid-argument",
        `Text exceeds maximum length of ${MAX_TRANSLATE_TEXT_LENGTH} characters`
      );
    }
    const to = validateLanguageCode(request.data?.to, "to");
    const from = request.data?.from ? validateLanguageCode(request.data.from, "from") : "";

    const key = AZURE_TRANSLATOR_KEY.value();
    const region = AZURE_TRANSLATOR_REGION.value();

    const url = buildTranslateUrl({to, from: from || undefined});

    await reserveProviderAllowance({
      uid: request.auth.uid,
      operation: "translateText",
      requestId: request.data?.requestId,
      charges: [{unit: "translatorCharacters", quantity: text.length}],
    });

    let resp;
    try {
      resp = await fetch(url, {
        timeout: PROVIDER_TIMEOUT_MS,
        redirect: "error",
        method: "POST",
        headers: {
          "Content-Type": "application/json; charset=utf-8",
          "Ocp-Apim-Subscription-Key": key,
          "Ocp-Apim-Subscription-Region": region,
        },
        body: JSON.stringify([{Text: text}]),
      });
    } catch (fetchError: any) {
      logger.error("Single translation network error", {
        error: fetchError?.message,
      });
      throw new HttpsError(
        "unavailable",
        "Unable to reach translation service. Please check your internet connection and try again."
      );
    }

    const bodyText = await resp.text();
    if (!resp.ok) throwTranslationApiError("Single", resp.status, bodyText);

    const json = safeParseJson(bodyText, "translation");
    const translated = json?.[0]?.translations?.[0]?.text ?? "";

    // When "from" was omitted, Azure auto-detects and returns the source language.
    // Forward it so the client can skip a separate detectLanguage call.
    const detectedLang = json?.[0]?.detectedLanguage;
    const result: Record<string, unknown> = {translatedText: translated};
    if (detectedLang) {
      result.detectedLanguage = {
        language: detectedLang.language ?? "",
        score: detectedLang.score ?? 0,
      };
    }
    return result;
  }
);

/**
 * Batch translation for chat messages. Requires authentication.
 * Server-side rate limiting: 20 requests per 10 minutes (per uid).
 * Server-side Azure chunking: splits large batches into 100-element Azure
 * API calls so the client only needs ONE Cloud Function invocation per
 * language change.
 */
export const translateTexts = onAppCheckCall(
  {
    secrets: [AZURE_TRANSLATOR_KEY, AZURE_TRANSLATOR_REGION],
    ...DEMO_SCALING,
    cpu: 1,
    memory: "512MiB",
  },
  async (request) => {
    requireAuth(request.auth);

    const uid = request.auth.uid;

    const MAX_BATCH_TEXTS = 800;
    const MAX_TEXT_LENGTH = 5000; // App limit per text element

    const to = validateLanguageCode(request.data?.to, "to");
    const from = request.data?.from ? validateLanguageCode(request.data.from, "from") : "";
    if (Array.isArray(request.data?.texts) && request.data.texts.length > MAX_BATCH_TEXTS) {
      throw new HttpsError("invalid-argument", `Too many texts (max ${MAX_BATCH_TEXTS})`);
    }
    const texts: string[] = Array.isArray(request.data?.texts)
      ? request.data.texts.map((t: unknown) => String(t ?? ""))
      : [];

    let characters = 0;

    // Validate each text's length
    for (let i = 0; i < texts.length; i++) {
      characters += texts[i].length;
      if (texts[i].length > MAX_TEXT_LENGTH) {
        throw new HttpsError(
          "invalid-argument",
          `Text at index ${i} exceeds maximum length (${MAX_TEXT_LENGTH} chars)`
        );
      }
    }

    const key = AZURE_TRANSLATOR_KEY.value();
    const region = AZURE_TRANSLATOR_REGION.value();

    const url = buildTranslateUrl({to, from: from || undefined});

    if (characters > MAX_BATCH_CHARACTERS) {
      throw new HttpsError("invalid-argument", "Batch exceeds the maximum total character count.");
    }
    if (characters === 0) return {translatedTexts: texts};
    const allowed = await checkWriteRateLimit(
      uid, "content_translate", CONTENT_TRANSLATE_AUTH_MAX, CONTENT_TRANSLATE_AUTH_WINDOW_MS
    );
    if (!allowed) {
      throw new HttpsError("resource-exhausted", "Rate limit hit. Please wait and try again.");
    }
    await reserveProviderAllowance({
      uid,
      operation: "translateTexts",
      requestId: request.data?.requestId,
      charges: [{unit: "translatorCharacters", quantity: characters * (AZURE_CHUNK_MAX_RETRIES + 1)}],
    });

    // ── Server-side Azure chunking (throttled + per-chunk retry) ────
    // Azure Translator limits each request to 100 elements.
    // We chunk here so the client sends ONE Cloud Function call per
    // language change, keeping the rate-limit count at 1.
    //
    // Spacing and bounded retries reduce the impact of transient throttling.
    const translatedTexts: string[] = [];
    let chunkIndex = 0;

    for (let i = 0; i < texts.length; i += AZURE_CHUNK_SIZE) {
      // Throttle: pause between chunks (skip delay before the first one).
      if (chunkIndex > 0) {
        await sleep(AZURE_INTER_CHUNK_DELAY_MS);
      }
      chunkIndex++;

      const chunk = texts.slice(i, i + AZURE_CHUNK_SIZE);
      const reqBody = chunk.map((t: string) => ({Text: t}));

      let resp;
      let bodyText = "";
      let succeeded = false;

      for (let attempt = 0; attempt <= AZURE_CHUNK_MAX_RETRIES; attempt++) {
        try {
          resp = await fetch(url, {
            timeout: PROVIDER_TIMEOUT_MS,
            redirect: "error",
            method: "POST",
            headers: {
              "Content-Type": "application/json; charset=utf-8",
              "Ocp-Apim-Subscription-Key": key,
              "Ocp-Apim-Subscription-Region": region,
            },
            body: JSON.stringify(reqBody),
          });
        } catch (fetchError: any) {
          logger.error("Batch translation network error", {
            error: fetchError?.message,
            chunkStart: i,
            attempt,
          });
          // Network errors are not retryable within this loop.
          throw new HttpsError(
            "unavailable",
            "Unable to reach translation service. Please check your internet connection and try again."
          );
        }

        bodyText = await resp.text();

        if (resp.ok) {
          succeeded = true;
          break;
        }

        // Azure 429 — retry this chunk with exponential back-off.
        if (resp.status === 429 && attempt < AZURE_CHUNK_MAX_RETRIES) {
          const retryAfterHeader = resp.headers?.get?.("retry-after") ?? null;
          const backoff = getChunkRetryDelayMs(resp, attempt);
          logger.warn("Azure 429 on chunk, retrying", {
            chunkStart: i,
            attempt: attempt + 1,
            backoffMs: backoff,
            retryAfterHeader,
          });
          await sleep(backoff);
          continue;
        }

        // Non-retryable error or retries exhausted — surface to client.
        throwTranslationApiError("Batch", resp.status, bodyText);
      }

      if (!succeeded) {
        const status = resp ? resp.status : 500;
        throwTranslationApiError("Batch", status, bodyText);
      }

      const json = safeParseJson(bodyText, "batch translation");
      const chunkTranslated: string[] = Array.isArray(json)
        ? json.map((item: any) => item?.translations?.[0]?.text ?? "")
        : [];
      translatedTexts.push(...chunkTranslated);
    }

    return {translatedTexts};
  }
);

export const detectLanguage = onAppCheckCall(
  {
    ...DEMO_SCALING,
    secrets: [AZURE_TRANSLATOR_KEY, AZURE_TRANSLATOR_REGION],
  },
  async (request) => {
    requireAuth(request.auth);

    const text = requireString(request.data?.text, "text");

    const key = AZURE_TRANSLATOR_KEY.value();
    const region = AZURE_TRANSLATOR_REGION.value();

    const url = new URL(`${ENDPOINT}/detect`);
    url.searchParams.set("api-version", API_VERSION);

    if (text.length > MAX_TRANSLATE_TEXT_LENGTH) {
      throw new HttpsError("invalid-argument", "Text exceeds the maximum length for language detection.");
    }
    await reserveProviderAllowance({
      uid: request.auth.uid,
      operation: "detectLanguage",
      requestId: request.data?.requestId,
      charges: [{unit: "translatorCharacters", quantity: text.length}],
    });

    let resp;
    try {
      resp = await fetch(url.toString(), {
        timeout: PROVIDER_TIMEOUT_MS,
        redirect: "error",
        method: "POST",
        headers: {
          "Content-Type": "application/json; charset=utf-8",
          "Ocp-Apim-Subscription-Key": key,
          "Ocp-Apim-Subscription-Region": region,
        },
        body: JSON.stringify([{Text: text}]),
      });
    } catch (fetchError: any) {
      logger.error("Language detection network error", {
        error: fetchError?.message,
      });
      throw new HttpsError(
        "unavailable",
        "Unable to reach language detection service. Please check your internet connection and try again."
      );
    }

    const bodyText = await resp.text();
    if (!resp.ok) throwTranslationApiError("Detection", resp.status, bodyText);

    const json = safeParseJson(bodyText, "language detection");
    const detected = json?.[0];

    return {
      language: detected?.language ?? "",
      score: detected?.score ?? 0,
      isTranslationSupported: detected?.isTranslationSupported ?? false,
      alternatives: detected?.alternatives ?? [],
    };
  }
);
