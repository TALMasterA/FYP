# App Improvement Suggestions

Suggestions ordered by impact on app logic (none → huge).
Items that do not affect app logic have been implemented and marked ✅.
Items that affect app logic are marked 🔍 for review — not implemented.

---

## No Impact on App Logic

### 1. ✅ Fix `treeOfImportantfiles.txt` encoding and content

**Problem:** The file was encoded in UTF-16 LE, causing every character to display with spaces between them. Content also had errors:
- `FirestoreFavoritesRepository.kt` was listed under `data/repositories/` but actually lives in `data/user/`
- `UiTextCore.kt`, `UiTextHelpers.kt`, `UiTextScreens.kt` were listed twice (under both `model/` and `model/ui/`)
- `ui/components/Theme.kt` was listed but the actual file is `ui/components/AnimatedComponents.kt`

**Fix:** Rewrote with UTF-8 encoding and corrected all file paths to match the actual codebase.

---

### 2. ✅ Update README.md Firestore collection paths

**Problem:** The README listed incorrect Firestore collection paths:
- `users/{uid}/settings` → actual path is `users/{uid}/profile/settings`
- `users/{uid}/learning` → actual path is `users/{uid}/learning_sheets/{primary}_{target}`
- `users/{uid}/quizzes` → actual path is `users/{uid}/quiz_attempts`
- `users/{uid}/coins` → actual path is `users/{uid}/user_stats/coins`
- Missing collections: `sessions`, `quiz_stats`, `generated_quizzes`, `coin_awards`, `last_awarded_quiz`, `word_banks`, `custom_words`, `user_stats/language_counts`

**Fix:** Updated all Firestore collection paths in README.md to match the actual repository implementations.

---

### 3. ✅ Update README.md history limit description

**Problem:** README stated "Translation history (100 records, expandable to 150)" but the actual code shows:
- Default `historyViewLimit` = 50 (in `UserSettings.kt`)
- `BASE_HISTORY_LIMIT` = 100
- `MAX_HISTORY_LIMIT` = 150
- Expansion cost: 1000 coins per 10-record increment

**Fix:** Updated to "default 50 records displayed, base limit 100, expandable up to 150".

---

### 4. ✅ Update README.md data models section

**Problem:** README listed models that don't match actual class names:
- `LearningSheet` → no such class; actual storage is `LearningSheetDoc` (in `FirestoreLearningSheetsRepository.kt`)
- `Quiz` → actual classes are `QuizQuestion`, `QuizAttempt`, `QuizAttemptDoc`
- `CoinStats` → actual class is `UserCoinStats`
- Missing models: `FavoriteRecord`, `CustomWord`, `HistorySession`, `SpeechResult`, `UserProfile`

**Fix:** Updated data models section with correct class names and descriptions.

---

### 5. ✅ Update README.md tech stack and features

**Problem:** Tech stack was incomplete — missing Jetpack Compose, Hilt, Kotlin Coroutines, Coil, DataStore, Firebase Crashlytics, Firebase Performance, Azure OpenAI. Features section was missing custom word bank and account management details.

**Fix:** Added all key libraries, expanded feature descriptions, added app version info section.

---

### 6. ✅ Update README.md project structure

**Problem:** Project structure only listed 5 top-level directories. Missing details about subdirectories, screen modules, and data layer organization.

**Fix:** Added detailed subdirectory descriptions for all packages under `screens/`, `data/`, `domain/`, and `model/`.

---

### 7. ✅ Remove `(Reviewing)` from README title

**Problem:** The README title said "FYP - Translation & Learning App (Reviewing)" which is a temporary status marker, not part of the app name.

**Fix:** Changed to "FYP - Translation & Learning App".

---

### 8. ✅ Remove outdated disclaimer from README

**Problem:** The README had a disclaimer: "(Some content is by github copilot agent and may contain error)". Since the content has now been manually verified against the codebase, this disclaimer is no longer needed.

**Fix:** Removed the disclaimer. The "Manual checked" note remains.

---

## Small Impact on App Logic

### 9. 🔍 Account deletion does not clean up all Firestore subcollections

**Problem:** In `FirestoreProfileRepository.kt`, the `deleteAccount()` method deletes these subcollections:
```
history, word_banks, learning_sheets, quizzes, favorites, custom_words, sessions
```
However, the actual quiz data is stored in `quiz_attempts` (not `quizzes`), and these subcollections are also not cleaned up:
- `quiz_stats/{primary}_{target}`
- `generated_quizzes/{primary}_{target}`
- `coin_awards/{versionKey}`
- `last_awarded_quiz/{primary}_{target}`
- `user_stats` (contains `coins` and `language_counts` documents)

**Impact:** Orphaned data remains in Firestore after account deletion. This is a data cleanup issue, not a functional one, but it affects storage costs and data privacy compliance.

**Suggested fix:** Update the deletion list in `FirestoreProfileRepository.kt` to include all subcollections, and change `"quizzes"` to `"quiz_attempts"`.

---

### 10. 🔍 Duplicate app version check and sign-out logic

**Problem:** Both `FYPApplication.kt` and `MainActivity.kt` independently check for app version updates and sign out the user. They use different SharedPreferences files (`PreferenceManager.getDefaultSharedPreferences` vs `"app_update_prefs"`) and the logic runs twice on every launch.

**Impact:** The dual logic could cause race conditions or unexpected behavior. The `FYPApplication.logoutUserOnUpdate()` only clears preferences without calling `FirebaseAuth.signOut()`, while `MainActivity` does call `auth.signOut()`. This means the Application-level check doesn't actually sign out from Firebase.

**Suggested fix:** Consolidate into a single version-check in `MainActivity.kt` (which properly calls `FirebaseAuth.signOut()`), and remove the redundant check from `FYPApplication.kt`.

---

### 11. 🔍 `Logger.kt` has unused Crashlytics integration stub

**Problem:** `AppLogger.kt` has a comment `// You could log to Crashlytics here` but the app already has Firebase Crashlytics integrated (dependency in `build.gradle.kts` and plugin applied). Non-fatal errors in the `e()` method could be forwarded to Crashlytics for production monitoring.

**Impact:** Non-fatal errors logged via `AppLogger.e()` are only visible in logcat, not in the Crashlytics dashboard.

**Suggested fix:** Add `Firebase.crashlytics.recordException(throwable)` in the `AppLogger.e()` method when a throwable is provided, so non-fatal errors appear in the Crashlytics console.

---

## Medium Impact on App Logic

### 12. 🔍 No input validation on custom word entries

**Problem:** `AddCustomWordDialog.kt` and `FirestoreCustomWordsRepository.kt` do not validate input beyond checking if fields are empty. Users can add very long strings, special characters, or whitespace-only entries (after trimming).

**Impact:** Potential for storing unexpected data in Firestore. No character length limits are enforced.

**Suggested fix:** Add maximum length limits (e.g., 200 characters for word, 500 for example) and basic sanitization in the `CustomWord` save flow.

---

### 13. 🔍 No offline handling for Firestore operations

**Problem:** The app does not explicitly handle offline states. Firestore has built-in offline persistence, but the UI does not inform users when they are offline or when operations are queued.

**Impact:** Users may not realize their changes are only cached locally and not yet synced. If they uninstall the app before syncing, data is lost.

**Suggested fix:** Add a connectivity-aware UI indicator and consider showing pending sync status for operations performed offline.

---

### 14. 🔍 `SharedHistoryDataSource` language records cache has no size limit

**Problem:** In `SharedHistoryDataSource.kt`, the `_languageRecordsCache` is a `MutableMap` with no eviction policy. If a user switches between many language pairs, the cache grows unbounded.

**Impact:** Low memory risk in practice (since users typically work with 2-3 language pairs), but violates good caching practice. The LearningViewModel's sheet metadata cache already uses LRU with a 50-entry limit, which is a better pattern.

**Suggested fix:** Convert `_languageRecordsCache` to an LRU cache with a reasonable limit (e.g., 10 entries).

---

### 15. 🔍 Translation cache entry limit could use LRU eviction

**Problem:** `TranslationCache.kt` has a `MAX_ENTRIES = 1000` limit but when the limit is reached, it removes the oldest single entry. Over time, frequently-used translations could be evicted while rarely-used ones remain.

**Impact:** Minor impact on cache hit rate. The 30-day TTL handles most staleness, but LRU would be more efficient.

**Suggested fix:** Replace the oldest-first eviction with an LRU strategy using `lastAccessedAt` timestamps.

---

## Large Impact on App Logic

### 16. 🔍 Cloud Function `maxInstances` set to 50 could cause cost spikes

**Problem:** In `fyp-backend/functions/src/index.ts`, some Cloud Functions have `maxInstances: 50`. Under high load, this could spin up 50 concurrent instances, each incurring Firebase billing.

**Impact:** Unexpected cost increases during usage spikes. For a student project, this is especially important to monitor.

**Suggested fix:** Consider lowering `maxInstances` to 5-10 for non-critical functions, and add Firebase budget alerts. Keep higher limits only for the speech token function which needs low latency.

---

### 17. 🔍 Quiz coin award anti-cheat relies on client-side history count

**Problem:** The coin eligibility check in `CoinEligibility.kt` and `LearningSheetViewModel.kt` compares the current history count against the count at quiz generation time. However, the history count is read client-side. A sophisticated user could manipulate local state or timing to earn undeserved coins.

**Impact:** The existing system is reasonable for most users, but moving the verification to a Cloud Function would make it tamper-proof.

**Suggested fix:** Move the coin award eligibility check to a Firebase Cloud Function that reads the history count server-side before awarding coins.

---

### 18. 🔍 No rate limiting on AI content generation requests

**Problem:** `CloudGenAiClient.kt` calls Firebase Cloud Functions to generate learning materials and quizzes via Azure OpenAI. While there is a 2-second debounce in `WordBankViewModel`, there is no server-side rate limiting per user.

**Impact:** A user could trigger many generation requests, consuming Azure OpenAI API quota and increasing costs.

**Suggested fix:** Add per-user rate limiting in the Cloud Functions (e.g., max 10 generations per hour per user) and return a rate-limit error to the client.

---
