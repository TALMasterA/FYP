/**
 * Cloud Functions entry point.
 *
 * Initialises Firebase Admin and re-exports every callable / triggered
 * function from its domain module.
 */
import {setGlobalOptions} from "firebase-functions/v2";
import * as admin from "firebase-admin";

// Initialize Firebase Admin at module load time (required for Cloud Functions v2)
admin.initializeApp();

setGlobalOptions({maxInstances: 10});

// ── Translation & Speech ─────────────────────────────────────────────
export {
  getSpeechToken,
  translateText,
  translateTexts,
  detectLanguage,
} from "./translation.js";

// ── AI Learning Content ──────────────────────────────────────────────
export {generateLearningContent, syncQuizVersionFromLearningSheet} from "./learning.js";

// ── Coins & Shop ─────────────────────────────────────────────────────
export {awardQuizCoins, spendCoins} from "./coins.js";

// ── Push Notifications ───────────────────────────────────────────────
export {
  sendChatNotification,
  sendFriendRequestNotification,
  sendRequestAcceptedNotification,
  sendSharedInboxNotification,
} from "./notifications.js";

// ── Scheduled Maintenance ────────────────────────────────────────────
export {pruneStaleTokens, pruneStaleRateLimits, repairFriendsData} from "./maintenance.js";

// ── Health Checks ─────────────────────────────────────────────────────
export {healthcheck} from "./health.js";
