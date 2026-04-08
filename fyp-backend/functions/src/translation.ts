/**
 * Translation & Language Detection Cloud Functions.
 *
 * Handles single/batch text translation and language detection
 * via the Azure Translator API.
 */
import {onCall, HttpsError} from "firebase-functions/v2/https";
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
import {logger} from "./logger.js";

// ── UI-language batch translation rate limits ────────────────────────
// Each language change = 1 Cloud Function call (server-side Azure chunking).
// Authenticated users get a generous allowance; guests are strict.
const UI_TRANSLATE_AUTH_MAX = 20;
const UI_TRANSLATE_AUTH_WINDOW_MS = 10 * 60 * 1000; // 10 minutes
const UI_TRANSLATE_GUEST_MAX = 1;
const UI_TRANSLATE_GUEST_WINDOW_MS = 60 * 60 * 1000; // 1 hour

// Azure Translator API limits: max 100 elements per request.
const AZURE_CHUNK_SIZE = 100;

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

export const getSpeechToken = onCall(
  {secrets: [AZURE_SPEECH_KEY, AZURE_SPEECH_REGION]},
  async (request) => {
    requireAuth(request.auth);

    const key = AZURE_SPEECH_KEY.value();
    const region = AZURE_SPEECH_REGION.value();

    const url = `https://${region}.api.cognitive.microsoft.com/sts/v1.0/issueToken`;

    const resp = await fetch(url, {
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

export const translateText = onCall(
  {secrets: [AZURE_TRANSLATOR_KEY, AZURE_TRANSLATOR_REGION]},
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

    let resp;
    try {
      resp = await fetch(url, {
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
 * No requireAuth — guests may call this once.
 * Server-side rate limiting:
 *   - Authenticated users: 20 requests per 10 minutes (per uid)
 *   - Guests: 1 request per hour (per raw IP)
 * Server-side Azure chunking: splits large batches into 100-element Azure
 * API calls so the client only needs ONE Cloud Function invocation per
 * language change.
 */
export const translateTexts = onCall(
  {secrets: [AZURE_TRANSLATOR_KEY, AZURE_TRANSLATOR_REGION]},
  async (request) => {
    // ── Rate-limit check (before any heavy work) ──────────────────────
    const uid = request.auth?.uid;
    if (uid) {
      const allowed = await checkWriteRateLimit(
        uid, "ui_translate", UI_TRANSLATE_AUTH_MAX, UI_TRANSLATE_AUTH_WINDOW_MS
      );
      if (!allowed) {
        throw new HttpsError(
          "resource-exhausted",
          `UI language translation is rate-limited. Please wait a moment and try again (max ${UI_TRANSLATE_AUTH_MAX} changes per 10 minutes).`
        );
      }
    } else {
      // Guest: rate-limit by raw IP (best-effort).
      const rawIp = request.rawRequest?.ip ?? "unknown";
      const guestKey = `guest_${rawIp.replace(/[^a-zA-Z0-9]/g, "_")}`;
      const allowed = await checkWriteRateLimit(
        guestKey, "ui_translate", UI_TRANSLATE_GUEST_MAX, UI_TRANSLATE_GUEST_WINDOW_MS
      );
      if (!allowed) {
        throw new HttpsError(
          "resource-exhausted",
          "Guest UI language translation is limited to once per hour. Please log in for more frequent changes."
        );
      }
    }

    const MAX_BATCH_TEXTS = 800;
    const MAX_TEXT_LENGTH = 5000; // Azure limit per text element

    const to = validateLanguageCode(request.data?.to, "to");
    const from = request.data?.from ? validateLanguageCode(request.data.from, "from") : "";
    const texts: string[] = Array.isArray(request.data?.texts)
      ? request.data.texts.map((t: unknown) => String(t ?? ""))
      : [];

    if (texts.length > MAX_BATCH_TEXTS) {
      throw new HttpsError(
        "invalid-argument",
        `Too many texts (max ${MAX_BATCH_TEXTS})`
      );
    }

    // Validate each text's length
    for (let i = 0; i < texts.length; i++) {
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

    // ── Server-side Azure chunking ──────────────────────────────────
    // Azure Translator limits each request to 100 elements.
    // We chunk here so the client sends ONE Cloud Function call per
    // language change, keeping the rate-limit count at 1.
    const translatedTexts: string[] = [];

    for (let i = 0; i < texts.length; i += AZURE_CHUNK_SIZE) {
      const chunk = texts.slice(i, i + AZURE_CHUNK_SIZE);
      const reqBody = chunk.map((t: string) => ({Text: t}));

      let resp;
      try {
        resp = await fetch(url, {
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
          chunkIndex: i,
        });
        throw new HttpsError(
          "unavailable",
          "Unable to reach translation service. Please check your internet connection and try again."
        );
      }

      const bodyText = await resp.text();
      if (!resp.ok) throwTranslationApiError("Batch", resp.status, bodyText);

      const json = safeParseJson(bodyText, "batch translation");
      const chunkTranslated: string[] = Array.isArray(json)
        ? json.map((item: any) => item?.translations?.[0]?.text ?? "")
        : [];
      translatedTexts.push(...chunkTranslated);
    }

    return {translatedTexts};
  }
);

export const detectLanguage = onCall(
  {secrets: [AZURE_TRANSLATOR_KEY, AZURE_TRANSLATOR_REGION]},
  async (request) => {
    requireAuth(request.auth);

    const text = requireString(request.data?.text, "text");

    const key = AZURE_TRANSLATOR_KEY.value();
    const region = AZURE_TRANSLATOR_REGION.value();

    const url = new URL(`${ENDPOINT}/detect`);
    url.searchParams.set("api-version", API_VERSION);

    let resp;
    try {
      resp = await fetch(url.toString(), {
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
