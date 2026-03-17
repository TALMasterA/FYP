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
  optionalString,
  safeParseJson,
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
    const to = requireString(request.data?.to, "to");
    const from = optionalString(request.data?.from);

    const key = AZURE_TRANSLATOR_KEY.value();
    const region = AZURE_TRANSLATOR_REGION.value();

    const url = buildTranslateUrl({to, from: from || undefined});

    const resp = await fetch(url, {
      method: "POST",
      headers: {
        "Content-Type": "application/json; charset=utf-8",
        "Ocp-Apim-Subscription-Key": key,
        "Ocp-Apim-Subscription-Region": region,
      },
      body: JSON.stringify([{Text: text}]),
    });

    const bodyText = await resp.text();
    if (!resp.ok) {
      logger.error("Single translation API error", {
        status: resp.status,
        errorPreview: bodyText.substring(0, 200),
      });
      throw new HttpsError(
        "internal", "Translation service unavailable. Please try again."
      );
    }

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

    const to = requireString(request.data?.to, "to");
    const from = optionalString(request.data?.from);
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

    const resp = await fetch(url, {
      method: "POST",
      headers: {
        "Content-Type": "application/json; charset=utf-8",
        "Ocp-Apim-Subscription-Key": key,
        "Ocp-Apim-Subscription-Region": region,
      },
      body: JSON.stringify(reqBody),
    });

    const bodyText = await resp.text();
    if (!resp.ok) {
      logger.error("Batch translation API error", {
        status: resp.status,
        errorPreview: bodyText.substring(0, 200),
      });
      throw new HttpsError(
        "internal", "Translation service unavailable. Please try again."
      );
    }

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

    const resp = await fetch(url.toString(), {
      method: "POST",
      headers: {
        "Content-Type": "application/json; charset=utf-8",
        "Ocp-Apim-Subscription-Key": key,
        "Ocp-Apim-Subscription-Region": region,
      },
      body: JSON.stringify([{Text: text}]),
    });

    const bodyText = await resp.text();
    if (!resp.ok) {
      logger.error("Language detection API error", {
        status: resp.status,
        errorPreview: bodyText.substring(0, 200),
      });
      throw new HttpsError(
        "internal", "Language detection service unavailable. Please try again."
      );
    }

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
