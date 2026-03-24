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
  AZURE_TRANSLATOR_KEY,
  AZURE_TRANSLATOR_REGION,
  AZURE_SPEECH_KEY,
  AZURE_SPEECH_REGION,
  ENDPOINT,
  API_VERSION,
  MAX_TRANSLATE_TEXT_LENGTH,
} from "./helpers.js";
import {logger} from "./logger.js";

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
 * No requireAuth — this function must be callable without authentication (one-time use).
 */
export const translateTexts = onCall(
  {secrets: [AZURE_TRANSLATOR_KEY, AZURE_TRANSLATOR_REGION]},
  async (request) => {
    const MAX_BATCH_TEXTS = 800;

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

    const key = AZURE_TRANSLATOR_KEY.value();
    const region = AZURE_TRANSLATOR_REGION.value();

    const url = buildTranslateUrl({to, from: from || undefined});
    const reqBody = texts.map((t: string) => ({Text: t}));

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
      });
      throw new HttpsError(
        "unavailable",
        "Unable to reach translation service. Please check your internet connection and try again."
      );
    }

    const bodyText = await resp.text();
    if (!resp.ok) throwTranslationApiError("Batch", resp.status, bodyText);

    const json = safeParseJson(bodyText, "batch translation");
    const translatedTexts: string[] = Array.isArray(json) ?
      json.map((item: any) => item?.translations?.[0]?.text ?? "") :
      [];

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
