import {setGlobalOptions} from "firebase-functions";
import {defineSecret} from "firebase-functions/params";
import {onCall, HttpsError} from "firebase-functions/v2/https";
import fetch from "node-fetch";
import * as admin from "firebase-admin";

// Lazy initialization to reduce cold start time
let _firestoreDb: admin.firestore.Firestore | undefined;

function getFirestore(): admin.firestore.Firestore {
  if (!admin.apps.length) {
    admin.initializeApp();
  }
  if (!_firestoreDb) {
    _firestoreDb = admin.firestore();
  }
  return _firestoreDb;
}

setGlobalOptions({maxInstances: 10});

const AZURE_SPEECH_KEY = defineSecret("AZURE_SPEECH_KEY");
const AZURE_SPEECH_REGION = defineSecret("AZURE_SPEECH_REGION");
const AZURE_TRANSLATOR_KEY = defineSecret("AZURE_TRANSLATOR_KEY");
const AZURE_TRANSLATOR_REGION = defineSecret("AZURE_TRANSLATOR_REGION");
const GENAI_BASE_URL = defineSecret("GENAI_BASE_URL");
const GENAI_API_VERSION = defineSecret("GENAI_API_VERSION");
const GENAI_API_KEY = defineSecret("GENAI_API_KEY");

const ENDPOINT = "https://api.cognitive.microsofttranslator.com";
const API_VERSION = "3.0";


function requireAuth(auth: unknown) {
  if (!auth) {
    throw new HttpsError(
      "unauthenticated", "Login required."
    );
  }
}

// ============ Reusable Validation Helpers ============

/**
 * Validates that a required string parameter is present and non-empty.
 * @param value The value to validate
 * @param paramName The parameter name for error messages
 * @returns The trimmed string value
 */
function requireString(value: unknown, paramName: string): string {
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
function optionalString(value: unknown): string {
  return value ? String(value).trim() : "";
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
      console.error("Speech token API error", {
        status: resp.status,
        errorPreview: token.substring(0, 200)
      });
      throw new HttpsError(
        "internal", "Speech service unavailable. Please try again."
      );
    }

    return {token, region};
  }
);

function buildTranslateUrl(params: { to: string; from?: string }) {
  const url = new URL(`${ENDPOINT}/translate`);
  url.searchParams.set("api-version", API_VERSION);
  url.searchParams.set("to", params.to);
  if (params.from) url.searchParams.set("from", params.from);
  return url.toString();
}

export const translateText = onCall(
  {secrets: [AZURE_TRANSLATOR_KEY, AZURE_TRANSLATOR_REGION]},
  async (request) => {
    requireAuth(request.auth);

    const text = requireString(request.data?.text, "text");
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
      console.error("Single translation API error", {
        status: resp.status,
        errorPreview: bodyText.substring(0, 200)
      });
      throw new HttpsError(
        "internal", "Translation service unavailable. Please try again."
      );
    }

    const json = JSON.parse(bodyText);
    const translated = json?.[0]?.translations?.[0]?.text ?? "";
    return {translatedText: translated};
  }
);

export const translateTexts = onCall(
  {secrets: [AZURE_TRANSLATOR_KEY, AZURE_TRANSLATOR_REGION]},
  async (request) => {
    // No auth required - allows UI language translation for all users
    // Non-logged-in users can only call this once (enforced client-side)
    const to = requireString(request.data?.to, "to");
    const from = optionalString(request.data?.from);
    const texts = request.data?.texts ?? [];

    const key = AZURE_TRANSLATOR_KEY.value();
    const region = AZURE_TRANSLATOR_REGION.value();

    const url = buildTranslateUrl({to, from: from || undefined});
    const reqBody = texts.map((t: any) => ({Text: String(t ?? "")}));

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
      console.error("Batch translation API error", {
        status: resp.status,
        errorPreview: bodyText.substring(0, 200)
      });
      throw new HttpsError(
        "internal", "Translation service unavailable. Please try again."
      );
    }

    const json = JSON.parse(bodyText);
    const translatedTexts: string[] = Array.isArray(json) ?
      json.map((item: any) => item?.translations?.[0]?.text ?? "") :
      [];

    return {translatedTexts};
  }
);

// ============ Rate Limiting ============

const RATE_LIMIT_MAX_REQUESTS = 10; // max requests per window
const RATE_LIMIT_WINDOW_MS = 60 * 60 * 1000; // 1 hour window

/**
 * Check and enforce per-user rate limiting for AI generation.
 * Uses Firestore to track request timestamps per user.
 * @param uid The authenticated user's UID
 * @throws HttpsError if rate limit is exceeded
 */
async function enforceRateLimit(uid: string): Promise<void> {
  const rateLimitRef = getFirestore()
    .collection("rate_limits")
    .doc(uid);

  const now = Date.now();
  const windowStart = now - RATE_LIMIT_WINDOW_MS;

  const doc = await rateLimitRef.get();
  let timestamps: number[] = [];

  if (doc.exists) {
    const data = doc.data();
    timestamps = (data?.timestamps ?? []) as number[];
  }

  // Filter to only timestamps within the current window
  timestamps = timestamps.filter((ts: number) => ts > windowStart);

  if (timestamps.length >= RATE_LIMIT_MAX_REQUESTS) {
    // Persist the filtered timestamps to prevent unbounded storage growth
    await rateLimitRef.set({timestamps, updatedAt: admin.firestore.FieldValue.serverTimestamp()});
    throw new HttpsError(
      "resource-exhausted",
      `Rate limit exceeded. Maximum ${RATE_LIMIT_MAX_REQUESTS} AI generation requests per hour.`
    );
  }

  // Add current timestamp and save
  timestamps.push(now);
  await rateLimitRef.set({timestamps, updatedAt: admin.firestore.FieldValue.serverTimestamp()});
}

export const generateLearningContent = onCall(
  {
    secrets: [GENAI_BASE_URL, GENAI_API_VERSION, GENAI_API_KEY],
    timeoutSeconds: 300, // 5 minutes timeout for AI generation
  },
  async (request) => {
    requireAuth(request.auth);

    // Enforce per-user rate limiting for AI generation
    const uid = (request.auth as {uid: string}).uid;
    await enforceRateLimit(uid);

    const deployment = requireString(request.data?.deployment, "deployment");
    const prompt = requireString(request.data?.prompt, "prompt");

    const baseUrl = GENAI_BASE_URL.value();
    const apiVersion = GENAI_API_VERSION.value();
    const apiKey = GENAI_API_KEY.value();

    const url = new URL(
      baseUrl.replace(/\/+$/, "") +
        `/deployments/${encodeURIComponent(deployment)}/chat/completions`
    );
    url.searchParams.set("api-version", apiVersion);

    const body = {
      messages: [
        { role: "system", content: "You are a helpful language learning assistant." },
        { role: "user", content: prompt },
      ],
      temperature: 1,
    };

    const resp = await fetch(url.toString(), {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        "api-key": apiKey,
      },
      body: JSON.stringify(body),
    });

    const text = await resp.text();
    if (!resp.ok) {
      console.error("GenAI API error", {
        status: resp.status,
        errorPreview: text.substring(0, 200)
      });
      throw new HttpsError(
        "internal", "AI content generation service unavailable. Please try again."
      );
    }

    const json = JSON.parse(text);
    const content = json?.choices?.[0]?.message?.content ?? "";
    return { content };
  }
);

/**
 * Detect language of given text using Azure Translator API.
 * Returns detected language code and confidence score.
 */
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
      console.error("Language detection API error", {
        status: resp.status,
        errorPreview: bodyText.substring(0, 200)
      });
      throw new HttpsError(
        "internal", "Language detection service unavailable. Please try again."
      );
    }

    const json = JSON.parse(bodyText);
    const detected = json?.[0];

    return {
      language: detected?.language ?? "",
      score: detected?.score ?? 0,
      isTranslationSupported: detected?.isTranslationSupported ?? false,
      alternatives: detected?.alternatives ?? []
    };
  }
);

// ============ Quiz Coin Award (Server-Side Anti-Cheat) ============

const MIN_INCREMENT_FOR_COINS = 10;

interface QuizAttemptData {
  attemptId: string;
  primaryLanguageCode: string;
  targetLanguageCode: string;
  generatedHistoryCountAtGenerate: number;
  totalScore: number;
}

/**
 * Award coins for a quiz attempt with server-side verification.
 *
 * Anti-Cheat Rules (all verified server-side):
 * 1. 1 coin per correct answer on first attempt only
 * 2. Quiz version must match current learning sheet version (read from Firestore)
 * 3. Quiz count must be at least 10 HIGHER than previous awarded quiz count
 * 4. First quiz for a language pair is always eligible
 * 5. Each quiz version can only be awarded once
 */
export const awardQuizCoins = onCall(
  {},
  async (request) => {
    requireAuth(request.auth);

    const uid = (request.auth as {uid: string}).uid;
    const data = request.data as QuizAttemptData;

    // Validate input
    const attemptId = requireString(data?.attemptId, "attemptId");
    const primaryCode = requireString(data?.primaryLanguageCode, "primaryLanguageCode");
    const targetCode = requireString(data?.targetLanguageCode, "targetLanguageCode");
    const generatedCount = data?.generatedHistoryCountAtGenerate;
    const score = data?.totalScore;

    if (typeof generatedCount !== "number" || generatedCount <= 0) {
      throw new HttpsError("invalid-argument", "generatedHistoryCountAtGenerate must be a positive number");
    }
    if (typeof score !== "number" || score < 0) {
      throw new HttpsError("invalid-argument", "totalScore must be a non-negative number");
    }

    // No coins for zero score
    if (score === 0) {
      return { awarded: false, reason: "zero_score" };
    }

    const versionKey = `${primaryCode}__${targetCode}__${generatedCount}`;

    // Document references
    const coinAwardRef = getFirestore()
      .collection("users").doc(uid)
      .collection("coin_awards").doc(versionKey);
    const lastAwardedRef = getFirestore()
      .collection("users").doc(uid)
      .collection("last_awarded_quiz").doc(`${primaryCode}__${targetCode}`);
    const coinStatsRef = getFirestore()
      .collection("users").doc(uid)
      .collection("user_stats").doc("coins");
    const sheetRef = getFirestore()
      .collection("users").doc(uid)
      .collection("learning_sheets").doc(`${primaryCode}__${targetCode}`);

    // Run transaction for atomicity
    const result = await getFirestore().runTransaction(async (tx) => {
      // Check 1: Already awarded for this exact version?
      const awardDoc = await tx.get(coinAwardRef);
      if (awardDoc.exists) {
        return { awarded: false, reason: "already_awarded" };
      }

      // Check 2: Get CURRENT learning sheet version from server (anti-cheat)
      const sheetDoc = await tx.get(sheetRef);
      if (!sheetDoc.exists) {
        return { awarded: false, reason: "no_sheet" };
      }
      const currentSheetVersion = sheetDoc.data()?.historyCountAtGenerate;
      if (typeof currentSheetVersion !== "number") {
        return { awarded: false, reason: "invalid_sheet" };
      }

      // Quiz version must EQUAL current sheet version
      if (generatedCount !== currentSheetVersion) {
        return { awarded: false, reason: "version_mismatch" };
      }

      // Check 3: Anti-cheat - need 10+ more records than last awarded quiz
      const lastAwardedDoc = await tx.get(lastAwardedRef);
      let lastAwardedCount: number | null = null;
      if (lastAwardedDoc.exists) {
        lastAwardedCount = lastAwardedDoc.data()?.count ?? null;
      }

      if (lastAwardedCount !== null) {
        const minRequired = lastAwardedCount + MIN_INCREMENT_FOR_COINS;
        if (generatedCount < minRequired) {
          return {
            awarded: false,
            reason: "insufficient_records",
            needed: minRequired - generatedCount
          };
        }
      }

      // All checks passed - award coins
      const statsDoc = await tx.get(coinStatsRef);
      let currentTotal = 0;
      let coinByLang: Record<string, number> = {};

      if (statsDoc.exists) {
        const statsData = statsDoc.data();
        currentTotal = statsData?.coinTotal ?? 0;
        coinByLang = statsData?.coinByLang ?? {};
      }

      const newTotal = currentTotal + score;
      coinByLang[targetCode] = (coinByLang[targetCode] ?? 0) + score;

      // Update coin stats
      tx.set(coinStatsRef, { coinTotal: newTotal, coinByLang });

      // Mark this version as awarded
      tx.set(coinAwardRef, {
        awarded: true,
        attemptId,
        coinsAwarded: score,
        awardedAt: admin.firestore.FieldValue.serverTimestamp()
      });

      // Update last awarded count for anti-cheat
      tx.set(lastAwardedRef, {
        count: generatedCount,
        lastAwardedAt: admin.firestore.FieldValue.serverTimestamp()
      });

      return { awarded: true, coinsAwarded: score, newTotal };
    });

    return result;
  }
);