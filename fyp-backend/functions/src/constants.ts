/**
 * Centralized validation constants for the FYP backend.
 *
 * This file serves as the single source of truth for validation rules,
 * limits, and patterns used across Cloud Functions. These constants must
 * be kept in sync with the Android app frontend validation.
 *
 * NOTE: When updating constants here, also update corresponding values
 * in the Android app (see app/src/main/java/com/example/fyp/).
 */

// ============ Language Code Validation ============

/**
 * Language code format validation regex.
 * Matches ISO 639-1 two-letter codes with optional ISO 3166-1 country codes.
 * Examples: "en", "ja", "en-US", "ja-JP"
 *
 * Frontend counterpart: app/src/main/java/com/example/fyp/model/ValueTypes.kt (LanguageCode)
 */
export const LANG_CODE_RE = /^[a-z]{2}(-[A-Z]{2})?$/;

// ============ Quiz & Coin System ============

/**
 * Minimum history record increment required between quiz coin awards.
 * Anti-cheat measure: users must add at least 10 new records before earning
 * coins from the next quiz for the same language pair.
 *
 * Frontend counterpart: app/src/main/java/com/example/fyp/domain/learning/CoinEligibility.kt
 */
export const MIN_INCREMENT_FOR_COINS = 10;

/**
 * Maximum allowed quiz score for coin awards.
 * Normal quiz max is 10 (10 questions × 1 coin each), but this buffer allows
 * up to 50 to account for potential quiz variations while preventing
 * unauthorized coin generation from tampered clients.
 */
export const MAX_QUIZ_SCORE = 50;

// ============ Spam Detection ============

/**
 * Maximum number of recent messages to check for spam patterns.
 * Balances detection accuracy with Firestore read costs.
 */
export const SPAM_RECENT_MESSAGES_WINDOW = 5;

/**
 * Number of identical consecutive messages that triggers spam detection.
 */
export const SPAM_DUPLICATE_THRESHOLD = 3;

/**
 * Number of links in a single message that triggers spam detection.
 */
export const SPAM_LINK_FLOOD_THRESHOLD = 3;

/**
 * Regex pattern for detecting URLs in messages.
 */
export const LINK_PATTERN = /https?:\/\/[^\s]+/gi;

// ============ Rate Limiting ============

/**
 * Maximum number of friend requests a user can send per hour.
 * Prevents spam and abuse of the friend request system.
 */
export const MAX_FRIEND_REQUESTS_PER_HOUR = 3;

/**
 * Maximum length for message preview in notifications.
 * Keeps notification payloads small and prevents exposing excessive content.
 */
export const MAX_MESSAGE_PREVIEW_LENGTH = 100;

// ============ Shop System ============

/**
 * Coin cost for expanding history view limit by 10 records.
 */
export const HISTORY_EXPANSION_COST = 1000;

/**
 * Number of records added per history expansion purchase.
 */
export const HISTORY_EXPANSION_INCREMENT = 10;

/**
 * Default history view limit for new users.
 */
export const BASE_HISTORY_LIMIT = 30;

/**
 * Maximum history view limit (after all expansions).
 */
export const MAX_HISTORY_LIMIT = 60;

/**
 * Coin cost for unlocking a color palette.
 */
export const PALETTE_UNLOCK_COST = 10;

/**
 * Valid color palette IDs that can be unlocked in the shop.
 * Must match ColorPalette enum in Android app.
 *
 * Frontend counterpart: app/src/main/java/com/example/fyp/model/UserSettings.kt (ColorPalette)
 */
export const VALID_PALETTE_IDS: string[] = [
  "ocean", "sunset", "lavender", "rose", "mint",
  "crimson", "amber", "indigo", "emerald", "coral",
];
