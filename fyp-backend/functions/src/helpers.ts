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

export function buildTranslateUrl(params: { to: string; from?: string }): string {
  const url = new URL(`${ENDPOINT}/translate`);
  url.searchParams.set("api-version", API_VERSION);
  url.searchParams.set("to", toTranslatorCode(params.to));
  if (params.from) url.searchParams.set("from", toTranslatorCode(params.from));
  return url.toString();
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

  let doc: admin.firestore.DocumentSnapshot;
  try {
    doc = await rateLimitRef.get();
  } catch (fsError: any) {
    logger.error("enforceRateLimit: Firestore read failed", {uid, error: fsError?.message});
    throw new HttpsError("internal", "Rate limit check failed (Firestore read error). Please try again.");
  }

  let timestamps: number[] = [];

  if (doc.exists) {
    const data = doc.data();
    timestamps = (data?.timestamps ?? []) as number[];
  }

  timestamps = timestamps.filter((ts: number) => ts > windowStart);

  if (timestamps.length >= RATE_LIMIT_MAX_REQUESTS) {
    try {
      await rateLimitRef.set({timestamps, updatedAt: admin.firestore.FieldValue.serverTimestamp()});
    } catch (fsWriteError: any) {
      logger.warn("enforceRateLimit: Firestore write failed during rate limit enforcement", {uid, error: fsWriteError?.message});
    }
    throw new HttpsError(
      "resource-exhausted",
      `Rate limit exceeded. Maximum ${RATE_LIMIT_MAX_REQUESTS} AI generation requests per hour.`
    );
  }

  timestamps.push(now);
  try {
    await rateLimitRef.set({timestamps, updatedAt: admin.firestore.FieldValue.serverTimestamp()});
  } catch (fsWriteError: any) {
    logger.error("enforceRateLimit: Firestore write failed", {uid, error: fsWriteError?.message});
    throw new HttpsError("internal", "Rate limit update failed (Firestore write error). Please try again.");
  }
}
