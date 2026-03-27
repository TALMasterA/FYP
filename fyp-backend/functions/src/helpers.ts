/**
 * Shared helper functions and constants used across all Cloud Function modules.
 */
import {HttpsError} from "firebase-functions/v2/https";
import {defineSecret} from "firebase-functions/params";
import * as admin from "firebase-admin";
import {logger} from "./logger.js";

// ============ Firebase Initialisation ============

// Ensure initializeApp is called exactly once (index.ts handles this).
let _firestoreDb: admin.firestore.Firestore | null = null;

export function getFirestore(): admin.firestore.Firestore {
  if (!_firestoreDb) {
    _firestoreDb = admin.firestore();
  }
  return _firestoreDb;
}

// ============ Secrets ============

export const AZURE_SPEECH_KEY = defineSecret("AZURE_SPEECH_KEY");
export const AZURE_SPEECH_REGION = defineSecret("AZURE_SPEECH_REGION");
export const AZURE_TRANSLATOR_KEY = defineSecret("AZURE_TRANSLATOR_KEY");
export const AZURE_TRANSLATOR_REGION = defineSecret("AZURE_TRANSLATOR_REGION");
export const GENAI_BASE_URL = defineSecret("GENAI_BASE_URL");
export const GENAI_API_VERSION = defineSecret("GENAI_API_VERSION");
export const GENAI_API_KEY = defineSecret("GENAI_API_KEY");

// ============ Constants ============

export const ENDPOINT = "https://api.cognitive.microsofttranslator.com";
export const API_VERSION = "3.0";
export const MAX_TRANSLATE_TEXT_LENGTH = 5000;

// ============ Validation Helpers ============

export function requireAuth(auth: unknown): void {
  if (!auth) {
    throw new HttpsError("unauthenticated", "Login required.");
  }
}

/**
 * Validates that a required string parameter is present and non-empty.
 * @param value The value to validate
 * @param paramName The parameter name for error messages
 * @returns The trimmed string value
 */
export function requireString(value: unknown, paramName: string): string {
  const str = String(value ?? "").trim();
  if (!str) {
    throw new HttpsError("invalid-argument", `${paramName} is required`);
  }
  return str;
}

/**
 * Validates an optional string parameter, returning empty string if not provided.
 * @param value The value to validate
 * @returns The trimmed string value or empty string
 */
export function optionalString(value: unknown): string {
  return value ? String(value).trim() : "";
}

/**
 * Safely parse a JSON response body, throwing a user-friendly HttpsError on failure.
 * @param body The raw response text
 * @param context A short label for the log message (e.g. "translation", "detection")
 * @returns The parsed JSON value
 */
export function safeParseJson(body: string, context: string): any {
  try {
    return JSON.parse(body);
  } catch (e: any) {
    logger.error(`Failed to parse ${context} response`, {
      error: e?.message,
      responsePreview: body.substring(0, 200),
    });
    throw new HttpsError(
      "internal",
      `Invalid response from ${context} service. Please try again.`
    );
  }
}

/**
 * Supported language codes for translation.
 * Do not modify without syncing with app/src/main/assets/azure_languages.json.
 */
export const SUPPORTED_LANGUAGES = [
  "en-US",
  "zh-HK",
  "zh-TW",
  "zh-CN",
  "ja-JP",
  "fr-FR",
  "de-DE",
  "ko-KR",
  "es-ES",
  "id-ID",
  "vi-VN",
  "th-TH",
  "fil-PH",
  "ms-MY",
  "pt-BR",
  "it-IT",
  "ru-RU",
];

/**
 * Backward-compatible aliases for legacy/non-canonical inputs.
 * Keep in sync with CustomWordsViewModel.normalizeLanguageCode() on Android.
 */
const LANGUAGE_CODE_ALIASES: Record<string, string> = {
  "en": "en-US",
  "zh": "zh-CN",
  "yue": "zh-HK",
  "yue-hk": "zh-HK",
  "ja": "ja-JP",
  "fr": "fr-FR",
  "de": "de-DE",
  "ko": "ko-KR",
  "es": "es-ES",
  "id": "id-ID",
  "vi": "vi-VN",
  "th": "th-TH",
  "fil": "fil-PH",
  "ms": "ms-MY",
  "pt": "pt-BR",
  "it": "it-IT",
  "ru": "ru-RU",
};

/**
 * Normalize app language codes to Azure Translator API codes.
 */
export function toTranslatorCode(code: string): string {
  const mapping: Record<string, string> = {
    "zh-HK": "yue",
    "zh-TW": "zh-Hant",
    "zh-CN": "zh-Hans",
  };
  return mapping[code] ?? code;
}

/**
 * Validates that a language code is supported.
 * @param code The language code to validate
 * @param paramName The parameter name for error messages
 * @returns The code if valid
 */
export function validateLanguageCode(code: string, paramName: string): string {
  const trimmed = String(code ?? "").trim();
  if (!trimmed) {
    throw new HttpsError("invalid-argument", `${paramName} is required`);
  }

  const aliased = LANGUAGE_CODE_ALIASES[trimmed] ?? LANGUAGE_CODE_ALIASES[trimmed.toLowerCase()] ?? trimmed;
  const canonical = SUPPORTED_LANGUAGES.find((lang) => lang.toLowerCase() === aliased.toLowerCase()) ?? aliased;

  if (!SUPPORTED_LANGUAGES.includes(canonical)) {
    throw new HttpsError(
      "invalid-argument",
      `${paramName} must be one of: ${SUPPORTED_LANGUAGES.join(", ")}`
    );
  }
  return canonical;
}

export function buildTranslateUrl(params: { to: string; from?: string }): string {
  const url = new URL(`${ENDPOINT}/translate`);
  url.searchParams.set("api-version", API_VERSION);
  url.searchParams.set("to", toTranslatorCode(params.to));
  if (params.from) url.searchParams.set("from", toTranslatorCode(params.from));
  return url.toString();
}

/**
 * Validate Azure OpenAI (GenAI) configuration loaded from secrets.
 */
export function validateGenAiConfig(config: {
  baseUrl: string;
  apiVersion: string;
  apiKey: string;
}): { baseUrl: string; apiVersion: string; apiKey: string } {
  const baseUrl = config.baseUrl.trim();
  const apiVersion = config.apiVersion.trim();
  const apiKey = config.apiKey.trim();

  if (!baseUrl || !apiVersion || !apiKey) {
    throw new HttpsError(
      "failed-precondition",
      "AI service is not configured. Please contact support."
    );
  }

  let parsedBaseUrl: URL;
  try {
    parsedBaseUrl = new URL(baseUrl);
  } catch {
    throw new HttpsError(
      "failed-precondition",
      "AI service URL is misconfigured. Please contact support."
    );
  }

  if (parsedBaseUrl.protocol !== "https:") {
    throw new HttpsError(
      "failed-precondition",
      "AI service URL must use HTTPS. Please contact support."
    );
  }

  if (!/^\d{4}-\d{2}-\d{2}(-preview)?$/.test(apiVersion)) {
    throw new HttpsError(
      "failed-precondition",
      "AI service API version is misconfigured. Please contact support."
    );
  }

  return {baseUrl, apiVersion, apiKey};
}

// ============ Rate Limiting ============

export const RATE_LIMIT_MAX_REQUESTS = 10;
export const RATE_LIMIT_WINDOW_MS = 60 * 60 * 1000; // 1 hour

/**
 * Check and enforce per-user rate limiting for AI generation.
 */
export async function enforceRateLimit(uid: string): Promise<void> {
  const rateLimitRef = getFirestore()
    .collection("rate_limits")
    .doc(uid);

  const now = Date.now();
  const windowStart = now - RATE_LIMIT_WINDOW_MS;

  try {
    await getFirestore().runTransaction(async (tx) => {
      const doc = await tx.get(rateLimitRef);

      let timestamps: number[] = [];
      if (doc.exists) {
        const data = doc.data();
        const rawTimestamps = data?.timestamps;
        if (rawTimestamps == null) {
          timestamps = [];
        } else if (!Array.isArray(rawTimestamps) || rawTimestamps.some((ts) => typeof ts !== "number" || !Number.isFinite(ts))) {
          logger.error("enforceRateLimit: malformed timestamps payload", {uid});
          throw new HttpsError("internal", "Rate limit data is invalid. Please try again.");
        } else {
          timestamps = rawTimestamps;
        }
      }

      // Keep only the active window to cap document growth and enforce rolling limits.
      const activeWindow = timestamps.filter((ts: number) => ts > windowStart);

      if (activeWindow.length >= RATE_LIMIT_MAX_REQUESTS) {
        // Persist trimmed timestamps even when blocked so stale data ages out naturally.
        tx.set(rateLimitRef, {
          timestamps: activeWindow,
          updatedAt: admin.firestore.FieldValue.serverTimestamp(),
        });
        throw new HttpsError(
          "resource-exhausted",
          `Rate limit exceeded. Maximum ${RATE_LIMIT_MAX_REQUESTS} AI generation requests per hour.`
        );
      }

      // Transaction keeps read+write atomic under concurrent requests from the same user.
      tx.set(rateLimitRef, {
        timestamps: [...activeWindow, now],
        updatedAt: admin.firestore.FieldValue.serverTimestamp(),
      });
    });
  } catch (error: any) {
    if (error instanceof HttpsError) {
      throw error;
    }
    logger.error("enforceRateLimit: Firestore transaction failed", {uid, error: error?.message});
    throw new HttpsError("internal", "Rate limit check failed. Please try again.");
  }
}
