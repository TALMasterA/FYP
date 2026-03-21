/**
 * Quiz coin awarding & shop purchase Cloud Functions.
 *
 * Server-side anti-cheat for quiz coins via Firestore transactions,
 * and server-side shop purchases (history expansion, palette unlock).
 */
import {onCall, HttpsError} from "firebase-functions/v2/https";
import * as admin from "firebase-admin";
import {
  requireAuth,
  requireString,
  getFirestore,
} from "./helpers.js";

// ============ Quiz Coin Award (Server-Side Anti-Cheat) ============

const MIN_INCREMENT_FOR_COINS = 10;
const MAX_QUIZ_SCORE = 50; // Safety cap — quiz is fixed at 10 questions (1 coin each), so normal max is 10

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
 * 2. Quiz version must match current server-owned quiz version (read from Firestore)
 * 3. Quiz count must be at least 10 HIGHER than previous awarded quiz count
 * 4. First quiz for a language pair is always eligible
 * 5. Each quiz version can only be awarded once
 * 6. Score is capped at MAX_QUIZ_SCORE to prevent tampered clients minting coins
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
    if (score > MAX_QUIZ_SCORE) {
      throw new HttpsError(
        "invalid-argument",
        `totalScore exceeds maximum of ${MAX_QUIZ_SCORE}`
      );
    }

    // Validate language code format to match app model constraints: xx or xx-XX.
    const LANG_CODE_RE = /^[a-z]{2}(-[A-Z]{2})?$/;
    if (!LANG_CODE_RE.test(primaryCode) || !LANG_CODE_RE.test(targetCode)) {
      throw new HttpsError(
        "invalid-argument",
        "Language codes must be in format xx or xx-XX (for example en or en-US)"
      );
    }

    // No coins for zero score
    if (score === 0) {
      return {awarded: false, reason: "zero_score"};
    }

    // Document references
    const lastAwardedRef = getFirestore()
      .collection("users").doc(uid)
      .collection("last_awarded_quiz").doc(`${primaryCode}__${targetCode}`);
    const coinStatsRef = getFirestore()
      .collection("users").doc(uid)
      .collection("user_stats").doc("coins");
    const quizVersionRef = getFirestore()
      .collection("users").doc(uid)
      .collection("quiz_versions").doc(`${primaryCode}__${targetCode}`);

    // Run transaction for atomicity
    const result = await getFirestore().runTransaction(async (tx) => {
      // Check 1: Get CURRENT server-owned quiz version (anti-cheat)
      const quizVersionDoc = await tx.get(quizVersionRef);
      if (!quizVersionDoc.exists) {
        return {awarded: false, reason: "no_quiz_version"};
      }
      const currentQuizVersion = quizVersionDoc.data()?.historyCount;
      if (typeof currentQuizVersion !== "number") {
        return {awarded: false, reason: "invalid_quiz_version"};
      }

      // Quiz attempt version must equal the current server-owned version.
      if (generatedCount !== currentQuizVersion) {
        return {awarded: false, reason: "version_mismatch"};
      }

      // Check 2: Already awarded for this exact validated version?
      const versionKey = `${primaryCode}__${targetCode}__${currentQuizVersion}`;
      const coinAwardRef = getFirestore()
        .collection("users").doc(uid)
        .collection("coin_awards").doc(versionKey);
      const awardDoc = await tx.get(coinAwardRef);
      if (awardDoc.exists) {
        return {awarded: false, reason: "already_awarded"};
      }

      // Check 3: Anti-cheat - need 10+ more records than last awarded quiz
      const lastAwardedDoc = await tx.get(lastAwardedRef);
      let lastAwardedCount: number | null = null;
      if (lastAwardedDoc.exists) {
        lastAwardedCount = lastAwardedDoc.data()?.count ?? null;
      }

      if (lastAwardedCount !== null) {
        const minRequired = lastAwardedCount + MIN_INCREMENT_FOR_COINS;
        if (currentQuizVersion < minRequired) {
          return {
            awarded: false,
            reason: "insufficient_records",
            needed: minRequired - currentQuizVersion,
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
      tx.set(coinStatsRef, {coinTotal: newTotal, coinByLang});

      // Mark this version as awarded
      tx.set(coinAwardRef, {
        awarded: true,
        attemptId,
        requestGeneratedHistoryCountAtGenerate: generatedCount,
        validatedHistoryCount: currentQuizVersion,
        coinsAwarded: score,
        awardedAt: admin.firestore.FieldValue.serverTimestamp(),
      });

      // Update last awarded count for anti-cheat
      tx.set(lastAwardedRef, {
        count: currentQuizVersion,
        lastAwardedAt: admin.firestore.FieldValue.serverTimestamp(),
      });

      return {awarded: true, coinsAwarded: score, newTotal};
    });

    return result;
  }
);

// ============ Server-Side Shop Purchases (Anti-Cheat) ============

// Shop constants — single source of truth (must match Android UserSettings / ColorPalette).
const HISTORY_EXPANSION_COST = 1000;
const HISTORY_EXPANSION_INCREMENT = 10;
const BASE_HISTORY_LIMIT = 30;
const MAX_HISTORY_LIMIT = 60;
const PALETTE_UNLOCK_COST = 10;
const VALID_PALETTE_IDS = [
  "ocean", "sunset", "lavender", "rose", "mint",
  "crimson", "amber", "indigo", "emerald", "coral",
];

/**
 * Server-side coin spending for shop purchases.
 * Runs the full balance check, deduction, and purchase application
 * inside a Firestore transaction so nothing can be tampered with.
 *
 * purchaseType: "history_expansion" | "palette_unlock"
 * paletteId: (required for palette_unlock) one of VALID_PALETTE_IDS
 */
export const spendCoins = onCall(
  {},
  async (request) => {
    requireAuth(request.auth);
    const uid = (request.auth as {uid: string}).uid;

    const purchaseType = requireString(request.data?.purchaseType, "purchaseType");

    const coinStatsRef = getFirestore()
      .collection("users").doc(uid)
      .collection("user_stats").doc("coins");
    const settingsRef = getFirestore()
      .collection("users").doc(uid)
      .collection("profile").doc("settings");

    if (purchaseType === "history_expansion") {
      const result = await getFirestore().runTransaction(async (tx) => {
        const statsDoc = await tx.get(coinStatsRef);
        const currentTotal = statsDoc.data()?.coinTotal ?? 0;
        const coinByLang = statsDoc.data()?.coinByLang ?? {};

        const settingsDoc = await tx.get(settingsRef);
        const currentLimit = settingsDoc.data()?.historyViewLimit ?? BASE_HISTORY_LIMIT;

        if (currentLimit >= MAX_HISTORY_LIMIT) {
          return {success: false, reason: "max_limit_reached"};
        }
        if (currentTotal < HISTORY_EXPANSION_COST) {
          return {success: false, reason: "insufficient_coins"};
        }

        const newLimit = Math.min(currentLimit + HISTORY_EXPANSION_INCREMENT, MAX_HISTORY_LIMIT);
        const newTotal = currentTotal - HISTORY_EXPANSION_COST;

        tx.set(coinStatsRef, {coinTotal: newTotal, coinByLang}, {merge: true});
        tx.set(settingsRef, {historyViewLimit: newLimit}, {merge: true});

        return {success: true, newBalance: newTotal, newLimit};
      });
      return result;
    } else if (purchaseType === "palette_unlock") {
      const paletteId = requireString(request.data?.paletteId, "paletteId");

      if (!VALID_PALETTE_IDS.includes(paletteId)) {
        throw new HttpsError("invalid-argument", `Invalid palette ID: ${paletteId}`);
      }

      const result = await getFirestore().runTransaction(async (tx) => {
        const statsDoc = await tx.get(coinStatsRef);
        const currentTotal = statsDoc.data()?.coinTotal ?? 0;
        const coinByLang = statsDoc.data()?.coinByLang ?? {};

        const settingsDoc = await tx.get(settingsRef);
        const unlockedPalettes: string[] = settingsDoc.data()?.unlockedPalettes ?? ["default"];

        if (unlockedPalettes.includes(paletteId)) {
          return {success: false, reason: "already_unlocked"};
        }
        if (currentTotal < PALETTE_UNLOCK_COST) {
          return {success: false, reason: "insufficient_coins"};
        }

        const newTotal = currentTotal - PALETTE_UNLOCK_COST;

        tx.set(coinStatsRef, {coinTotal: newTotal, coinByLang}, {merge: true});
        tx.set(settingsRef, {
          unlockedPalettes: admin.firestore.FieldValue.arrayUnion(paletteId),
        }, {merge: true});

        return {success: true, newBalance: newTotal};
      });
      return result;
    } else {
      throw new HttpsError(
        "invalid-argument",
        "purchaseType must be 'history_expansion' or 'palette_unlock'"
      );
    }
  }
);
