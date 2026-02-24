import {setGlobalOptions} from "firebase-functions";
import {defineSecret} from "firebase-functions/params";
import {onCall, HttpsError} from "firebase-functions/v2/https";
import {onDocumentCreated, onDocumentUpdated} from "firebase-functions/v2/firestore";
import {onSchedule} from "firebase-functions/v2/scheduler";
import fetch from "node-fetch";
import * as admin from "firebase-admin";

// Initialize Firebase Admin at module load time (required for Cloud Functions v2)
admin.initializeApp();
const _firestoreDb: admin.firestore.Firestore = admin.firestore();

function getFirestore(): admin.firestore.Firestore {
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

  let doc: admin.firestore.DocumentSnapshot;
  try {
    doc = await rateLimitRef.get();
  } catch (fsError: any) {
    console.error("enforceRateLimit: Firestore read failed", {uid, error: fsError?.message});
    throw new HttpsError("internal", "Rate limit check failed (Firestore read error). Please try again.");
  }

  let timestamps: number[] = [];

  if (doc.exists) {
    const data = doc.data();
    timestamps = (data?.timestamps ?? []) as number[];
  }

  // Filter to only timestamps within the current window
  timestamps = timestamps.filter((ts: number) => ts > windowStart);

  if (timestamps.length >= RATE_LIMIT_MAX_REQUESTS) {
    // Persist the filtered timestamps to prevent unbounded storage growth
    try {
      await rateLimitRef.set({timestamps, updatedAt: admin.firestore.FieldValue.serverTimestamp()});
    } catch (fsWriteError: any) {
      console.warn("enforceRateLimit: Firestore write failed during rate limit enforcement", {uid, error: fsWriteError?.message});
    }
    throw new HttpsError(
      "resource-exhausted",
      `Rate limit exceeded. Maximum ${RATE_LIMIT_MAX_REQUESTS} AI generation requests per hour.`
    );
  }

  // Add current timestamp and save
  timestamps.push(now);
  try {
    await rateLimitRef.set({timestamps, updatedAt: admin.firestore.FieldValue.serverTimestamp()});
  } catch (fsWriteError: any) {
    console.error("enforceRateLimit: Firestore write failed", {uid, error: fsWriteError?.message});
    throw new HttpsError("internal", "Rate limit update failed (Firestore write error). Please try again.");
  }
}

export const generateLearningContent = onCall(
  {
    secrets: [GENAI_BASE_URL, GENAI_API_VERSION, GENAI_API_KEY],
    timeoutSeconds: 300, // 5 minutes timeout for AI generation
  },
  async (request) => {
    try {
    requireAuth(request.auth);

    // Enforce per-user rate limiting for AI generation
    const uid = (request.auth as {uid: string}).uid;
    await enforceRateLimit(uid);

    const deployment = requireString(request.data?.deployment, "deployment");
    const prompt = requireString(request.data?.prompt, "prompt");

    const baseUrl = GENAI_BASE_URL.value();
    const apiVersion = GENAI_API_VERSION.value();
    const apiKey = GENAI_API_KEY.value();

    // IMPORTANT: Validate secrets are configured before using them.
    // If any secret is missing, new URL() below would throw a TypeError
    // which surfaces as an opaque INTERNAL error to the client.
    if (!baseUrl || !apiVersion || !apiKey) {
      console.error("GenAI secrets not configured", {
        hasBaseUrl: !!baseUrl,
        hasApiVersion: !!apiVersion,
        hasApiKey: !!apiKey,
        uid
      });
      throw new HttpsError(
        "failed-precondition",
        "AI service is not configured. Please contact support."
      );
    }

    let url: URL;
    try {
      url = new URL(
        baseUrl.replace(/\/+$/, "") +
          `/openai/deployments/${encodeURIComponent(deployment)}/chat/completions`
      );
      url.searchParams.set("api-version", apiVersion);
    } catch (urlError: any) {
      console.error("Invalid GENAI_BASE_URL", {
        baseUrl,
        error: urlError?.message,
        uid
      });
      throw new HttpsError(
        "failed-precondition",
        "AI service URL is misconfigured. Please contact support."
      );
    }

    console.log("generateLearningContent: calling Azure OpenAI", {
      uid,
      deployment,
      promptLength: prompt.length,
      url: url.toString().replace(apiKey, "***")
    });

    const body = {
      messages: [
        { role: "system", content: "You are a helpful language learning assistant." },
        { role: "user", content: prompt },
      ],
      temperature: 1,
    };

    let resp;
    try {
      resp = await fetch(url.toString(), {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          "api-key": apiKey,
        },
        body: JSON.stringify(body),
      });
    } catch (fetchError: any) {
      console.error("Network error calling GenAI API", {
        error: fetchError?.message,
        deployment
      });
      throw new HttpsError(
        "unavailable",
        "Unable to reach AI service. Please check your internet connection and try again."
      );
    }

    const text = await resp.text();
    if (!resp.ok) {
      console.error("GenAI API error", {
        status: resp.status,
        statusText: resp.statusText,
        errorPreview: text.substring(0, 500),
        deployment
      });

      // Provide more specific error messages based on status code
      if (resp.status === 401 || resp.status === 403) {
        throw new HttpsError(
          "failed-precondition",
          "AI service authentication failed. Please contact support."
        );
      } else if (resp.status === 429) {
        throw new HttpsError(
          "resource-exhausted",
          "AI service rate limit exceeded. Please try again in a few minutes."
        );
      } else if (resp.status === 404) {
        console.error("Azure OpenAI deployment not found", { deployment, baseUrl });
        throw new HttpsError(
          "not-found",
          `AI deployment '${deployment}' not found in Azure. Check the deployment name in Azure Portal.`
        );
      } else if (resp.status >= 500) {
        throw new HttpsError(
          "unavailable",
          "AI service is temporarily unavailable. Please try again later."
        );
      }

      throw new HttpsError(
        "internal",
        "AI content generation failed. Please try again."
      );
    }

    let json;
    try {
      json = JSON.parse(text);
    } catch (parseError: any) {
      console.error("Failed to parse GenAI response", {
        error: parseError?.message,
        responsePreview: text.substring(0, 200)
      });
      throw new HttpsError(
        "internal",
        "Invalid response from AI service. Please try again."
      );
    }

    const content = json?.choices?.[0]?.message?.content ?? "";

    if (!content) {
      console.error("Empty content from GenAI", {
        hasChoices: !!json?.choices,
        choicesLength: json?.choices?.length,
        firstChoice: json?.choices?.[0]
      });
      throw new HttpsError(
        "internal",
        "No content generated. Please try again."
      );
    }

    return { content };
    } catch (e: any) {
      // Re-throw HttpsErrors as-is (they already have code + message)
      if (e instanceof HttpsError) throw e;
      // Wrap any unexpected error with its message so it appears in the client
      // instead of an opaque INTERNAL error with no details.
      console.error("generateLearningContent: unexpected error", {
        message: e?.message,
        stack: e?.stack?.substring(0, 500),
      });
      throw new HttpsError(
        "internal",
        `Generation failed: ${e?.message ?? "Unknown error"}. Please try again.`
      );
    }
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

// ============ Push Notification Triggers ============

const MAX_MESSAGE_PREVIEW_LENGTH = 100;

/**
 * Sends an FCM push notification to the recipient when a new chat message is created.
 *
 * Trigger: Firestore document create at chats/{chatId}/messages/{messageId}
 *
 * Flow:
 *  1. Extract senderId and receiverId from the new message document.
 *  2. Fetch the sender's public profile to get their username.
 *  3. Fetch all FCM tokens registered for the receiver
 *     (stored at users/{receiverId}/fcm_tokens/{token}).
 *  4. Send a data-only FCM multicast message to all of the receiver's tokens.
 *  5. Remove any tokens that have become invalid (registration_id_not_found).
 *
 * Security: Runs with admin privileges — bypasses Firestore security rules.
 */
export const sendChatNotification = onDocumentCreated(
  {
    document: "chats/{chatId}/messages/{messageId}",
    region: "us-central1",
  },
  async (event) => {
    const data = event.data?.data();
    if (!data) return;

    const senderId: string = data.senderId ?? "";
    const receiverId: string = data.receiverId ?? "";
    const content: string = data.content ?? "";
    const type: string = data.type ?? "TEXT";

    if (!senderId || !receiverId) return;

    // Don't notify for shared-item messages (they have their own inbox flow)
    if (type !== "TEXT") return;

    try {
      // 1. Fetch sender's username from their public profile
      const senderProfileSnap = await getFirestore()
        .collection("users").doc(senderId)
        .collection("profile").doc("public")
        .get();
      const senderUsername: string = senderProfileSnap.data()?.username ?? "Friend";

      // 2. Fetch all FCM tokens for the receiver
      const tokensSnap = await getFirestore()
        .collection("users").doc(receiverId)
        .collection("fcm_tokens")
        .get();

      if (tokensSnap.empty) {
        console.log(`sendChatNotification: no FCM tokens for receiver=${receiverId}`);
        return;
      }

      const tokens: string[] = tokensSnap.docs
        .map((doc) => doc.data().token as string)
        .filter((t) => !!t);

      if (tokens.length === 0) return;

      // 3. Build data-only FCM message (handled by FcmNotificationService on Android)
      const messagePreview = content.length > MAX_MESSAGE_PREVIEW_LENGTH
        ? content.substring(0, MAX_MESSAGE_PREVIEW_LENGTH) + "…"
        : content;
      const fcmMessage: admin.messaging.MulticastMessage = {
        tokens,
        data: {
          type: "new_message",
          senderId,
          senderUsername,
          messagePreview,
        },
        android: {
          priority: "high",
        },
      };

      // 4. Send the multicast message
      const response = await admin.messaging().sendEachForMulticast(fcmMessage);
      console.log(
        `sendChatNotification: sent to ${tokens.length} tokens, ` +
        `success=${response.successCount}, failure=${response.failureCount}`
      );

      // 5. Clean up invalid/expired tokens
      const invalidTokens: string[] = [];
      response.responses.forEach((resp, idx) => {
        if (!resp.success) {
          const errCode = resp.error?.code ?? "";
          if (
            errCode === "messaging/registration-token-not-registered" ||
            errCode === "messaging/invalid-registration-token"
          ) {
            invalidTokens.push(tokens[idx]);
          } else {
            console.warn(`sendChatNotification: token[${idx}] error: ${resp.error?.message}`);
          }
        }
      });

      if (invalidTokens.length > 0) {
        const batch = getFirestore().batch();
        for (const token of invalidTokens) {
          const ref = getFirestore()
            .collection("users").doc(receiverId)
            .collection("fcm_tokens").doc(token);
          batch.delete(ref);
        }
        await batch.commit();
        console.log(`sendChatNotification: removed ${invalidTokens.length} invalid tokens`);
      }
    } catch (err: any) {
      console.error("sendChatNotification: unexpected error", {
        message: err?.message,
        chatId: event.params.chatId,
      });
    }
  }
);

/**
 * Sends an FCM push notification to the sender when their friend request is accepted.
 *
 * Trigger: Firestore document update at friend_requests/{requestId}
 * Only fires when status changes to "ACCEPTED".
 */
export const sendRequestAcceptedNotification = onDocumentUpdated(
  {
    document: "friend_requests/{requestId}",
    region: "us-central1",
  },
  async (event) => {
    const before = event.data?.before?.data();
    const after = event.data?.after?.data();
    if (!before || !after) return;

    // Only notify when status transitions to ACCEPTED
    if (before.status === after.status || after.status !== "ACCEPTED") return;

    const senderId: string = after.fromUserId ?? "";
    const accepterUsername: string = after.toUsername ?? "your friend";

    if (!senderId) return;

    try {
      const tokensSnap = await getFirestore()
        .collection("users").doc(senderId)
        .collection("fcm_tokens")
        .get();

      if (tokensSnap.empty) return;

      const tokens: string[] = tokensSnap.docs
        .map((doc) => doc.data().token as string)
        .filter((t) => !!t);

      if (tokens.length === 0) return;

      const fcmMessage: admin.messaging.MulticastMessage = {
        tokens,
        data: {
          type: "request_accepted",
          friendUsername: accepterUsername,
        },
        android: { priority: "normal" },
      };

      const response = await admin.messaging().sendEachForMulticast(fcmMessage);
      console.log(
        `sendRequestAcceptedNotification: sent to ${tokens.length} tokens, ` +
        `success=${response.successCount}, failure=${response.failureCount}`
      );
    } catch (err: any) {
      console.error("sendRequestAcceptedNotification: error", { message: err?.message });
    }
  }
);

/**
 * Sends an FCM push notification to the recipient when a new friend request is created.
 *
 * Trigger: Firestore document create at friend_requests/{requestId}
 */
export const sendFriendRequestNotification = onDocumentCreated(
  {
    document: "friend_requests/{requestId}",
    region: "us-central1",
  },
  async (event) => {
    const data = event.data?.data();
    if (!data) return;

    // Only notify for new PENDING requests
    if (data.status !== "PENDING") return;

    const receiverId: string = data.toUserId ?? "";
    const senderUsername: string = data.fromUsername ?? "Someone";

    if (!receiverId) return;

    try {
      const tokensSnap = await getFirestore()
        .collection("users").doc(receiverId)
        .collection("fcm_tokens")
        .get();

      if (tokensSnap.empty) return;

      const tokens: string[] = tokensSnap.docs
        .map((doc) => doc.data().token as string)
        .filter((t) => !!t);

      if (tokens.length === 0) return;

      const fcmMessage: admin.messaging.MulticastMessage = {
        tokens,
        data: {
          type: "friend_request",
          senderUsername,
        },
        android: { priority: "normal" },
      };

      const response = await admin.messaging().sendEachForMulticast(fcmMessage);
      console.log(
        `sendFriendRequestNotification: sent to ${tokens.length} tokens, ` +
        `success=${response.successCount}, failure=${response.failureCount}`
      );
    } catch (err: any) {
      console.error("sendFriendRequestNotification: error", { message: err?.message });
    }
  }
);
/**
 * Prune FCM tokens older than 60 days to prevent unbounded growth.
 * Runs daily at 3 AM UTC.
 * Users are processed in cursor-based pages of 500 to avoid loading the entire
 * users collection into memory at once.
 */
export const pruneStaleTokens = onSchedule(
  {
    schedule: "0 3 * * *",
    timeZone: "UTC",
    region: "us-central1",
  },
  async () => {
    const SIXTY_DAYS_MS = 60 * 24 * 60 * 60 * 1000;
    const cutoff = admin.firestore.Timestamp.fromDate(
      new Date(Date.now() - SIXTY_DAYS_MS)
    );
    const firestore = getFirestore();
    const PAGE_SIZE = 500;
    let pruned = 0;
    let lastDoc: admin.firestore.QueryDocumentSnapshot | null = null;

    // Paginate through users to avoid loading all at once
    while (true) {
      let query = firestore.collection("users").select().limit(PAGE_SIZE);
      if (lastDoc) query = query.startAfter(lastDoc);

      const usersSnap = await query.get();
      if (usersSnap.empty) break;
      lastDoc = usersSnap.docs[usersSnap.docs.length - 1];

      for (const userDoc of usersSnap.docs) {
        const tokensSnap = await firestore
          .collection("users").doc(userDoc.id)
          .collection("fcm_tokens")
          .where("updatedAt", "<", cutoff)
          .get();

        if (!tokensSnap.empty) {
          const batch = firestore.batch();
          tokensSnap.docs.forEach((doc) => batch.delete(doc.ref));
          await batch.commit();
          pruned += tokensSnap.size;
        }
      }

      if (usersSnap.size < PAGE_SIZE) break;
    }

    console.log(`pruneStaleTokens: removed ${pruned} stale tokens`);
  }
);
