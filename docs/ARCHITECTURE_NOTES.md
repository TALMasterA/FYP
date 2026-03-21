# Architecture & Complex Logic Notes

> **Purpose:** This document protects complex, non-obvious business logic from accidental modification.
> Read this before making changes to the listed areas.

---

## 1. UiText System — Enum / List Alignment

**Files:** `model/ui/UiTextCore.kt`, `model/ui/UiTextScreens.kt`

**Invariant:** The `UiTextKey` enum ordinals map 1-to-1 to entries in `BaseUiTexts = CoreUiTexts + ScreenUiTexts`.
An off-by-one causes `ArrayIndexOutOfBoundsException` at runtime.

**Guard:** `UiTextAlignmentTest` will fail if the counts diverge.  
**Rule:** Always add new `UiTextKey` entries AND the corresponding string in the same commit.  
Never reorder existing enum entries.

---

## 1.1 Mode Copy Consistency — Discrete vs Continuous

**Files:** `model/ui/UiTextScreens.kt`, `model/ui/strings/translations/*`

**Invariant:** Mode labels and help text must keep the same intent across locales:
- Quick Translate = discrete / short-phrase / single-turn translation
- Live Conversation = continuous / multi-turn dialogue

**Rule:** When updating mode wording in one locale, update equivalent strings in other maintained locale maps in the same change set to avoid mixed-mode guidance.

---

## 1.2 Quick Translate Auto-Detect Fallback

**Files:** `screens/speech/SpeechViewModel.kt`, `screens/speech/SpeechRecognitionScreen.kt`

**Invariant:** In Quick Translate, source `auto` mode must still work when translation responses omit inline detected language metadata.

**Rule:**
- First use translation inline detection when available.
- If missing, fallback to `DetectLanguageUseCase` using the current source text.
- Reuse the resolved language for history sourceLang and original-text TTS (never pass `"auto"` into TTS).
- Keep `Detected: ...` status transient (auto-clear after a short delay) so stale detection labels do not linger.
- Provide a user-triggered refresh/reset path in Quick Translate to clear stale detected-language UI state before retry.

This prevents regressions where typed input in auto mode translates but cannot be spoken, or fails with a false "could not detect" error when fallback detection would succeed.

---

## 2. Firestore Nested Map Writes — Set-Merge vs Update

**Files:** `data/friends/FirestoreChatRepository.kt` (`updateChatMetadata`, `markAllMessagesAsRead`)

**Invariant:** Firestore `set(..., SetOptions.merge())` with a nested `Map` **overwrites the entire nested map**, losing sibling keys.

**Rule:** Always use `update()` with dot-notation for nested fields:
```kotlin
// CORRECT — only updates one key
db.document(path).update("unreadPerFriend.$friendId", count)

// WRONG — overwrites entire unreadPerFriend map
db.document(path).set(mapOf("unreadPerFriend" to mapOf(friendId to count)), SetOptions.merge())
```
For document creation fallback: catch `FirebaseFirestoreException`, only fallback when `code == NOT_FOUND`, and call `set()` with the full map.
For all other Firestore error codes, rethrow so permission/config/network problems are not silently masked.

---

## 2.1 Error Visibility — Auto-Scroll To Error Banner

**File:** `screens/learning/LearningScreen.kt`

**Invariant:** When `uiState.error` becomes non-null on list-based screens, the visible viewport should move to the error banner so users immediately see actionable feedback.

**Rule:** For `LazyColumn` screens, use `rememberLazyListState()` + `LaunchedEffect(error)` + `animateScrollToItem()` before any timed auto-dismiss.

For non-list screens, surface transient errors via a visible status/snackbar area and auto-clear with `UiConstants.ERROR_AUTO_DISMISS_MS`.

---

## 2.2 Word Bank Error Lifecycle

**Files:** `screens/wordbank/WordBankScreen.kt`, `screens/wordbank/WordBankViewModel.kt`

**Invariant:** Word Bank errors should be short-lived and user-visible without requiring manual dismissal.

**Rule:** When `uiState.error` is set, auto-dismiss in the screen via `LaunchedEffect(error)` and clear through `WordBankViewModel.clearError()`.

---

## 3. Username Propagation — Cache Consistency

**Files:** `data/friends/FirestoreFriendsRepository.kt` (`propagateUsernameChange`, `syncFriendUsernames`)

**Invariant:** `FriendRelation.friendUsername` is a **cached copy** of the friend's display name. It must be kept in sync via:
1. `propagateUsernameChange()` — called by `ProfileViewModel.updateUsername()` when a user renames themselves
2. `syncFriendUsernames()` — called on pull-to-refresh to heal any stale caches

**Rule:** Never update a username without calling `propagateUsernameChange()`.  
The `setUsername()` method only updates the global `usernames/` registry — it does NOT update friend caches.

---

## 4. Account Deletion — Complete Cleanup Required

**Files:** `data/user/FirestoreProfileRepository.kt` (`deleteAccount`)

**Invariant:** `deleteAccount()` must clean up ALL 17 subcollections + top-level docs:
`history, word_banks, learning_sheets, quiz_attempts, quiz_stats, generated_quizzes, favorites,
custom_words, sessions, coin_awards, last_awarded_quiz, user_stats, friends, shared_inbox,
favorite_sessions, blocked_users, fcm_tokens,
profile/settings, profile/info, profile/public, usernames/{username}, user_search/{uid}`

**Rule:** If you add a new Firestore collection for a user, add its cleanup to `deleteAccount()`.  
Write a test that asserts the cleanup path count stays in sync.

---

## 5. Coin System — Anti-Cheat Logic

**Files:** `domain/learning/GenerationEligibility.kt`, `LearningViewModel.kt`

**Invariant:** Coins are only awarded when:
1. The quiz was generated from at least `MIN_RECORDS_FOR_LEARNING_SHEET` records
2. The history count has increased since the last coin award
3. The score meets the minimum threshold

**Rule:** Do NOT add coin award calls outside `awardCoinsForQuiz()`. The `lastAwardedQuizCountByLanguage` map is the anti-cheat guard; never reset it without cause.

---

## 6. SharedFriendsDataSource — Single Listener Pattern

**Files:** `data/friends/SharedFriendsDataSource.kt`

**Invariant:** This `@Singleton` holds exactly **one** Firestore listener per collection (friends, incoming requests, shared inbox). Multiple ViewModels share these flows instead of creating their own listeners.

**Rule:** Do NOT add additional `observeFriends()` / `observeIncomingRequests()` calls in ViewModels — use `sharedFriendsDataSource.friends` / `.incomingRequests` instead. Creating extra listeners multiplies Firestore read costs.

---

## 7. History Limit — Firestore Cost Boundary

**Files:** `model/user/UserSettings.kt`, `HistoryViewModel.kt`, `FirestoreHistoryRepository.kt`

**Invariant:** History is capped at `UserSettings.historyViewLimit` (30–60). Expanding beyond requires explicit coin purchase.  
`BASE_HISTORY_LIMIT = 30`, `MAX_HISTORY_LIMIT = 60`, expansion increment = 10.

**Rule:** Never increase `DEFAULT_HISTORY_LIMIT` in `DataConstants` — it controls a Firestore read quota boundary.

---

## 8. FCM Token Management

**Files:** `core/FcmNotificationService.kt`, `fyp-backend/functions/src/index.ts`

**Invariant:** Each device token is stored at `users/{uid}/fcm_tokens/{tokenId}` with an `updatedAt` timestamp. The `pruneStaleTokens` Cloud Function deletes tokens older than 60 days.

**Rule:** Always use `FcmNotificationService.storeToken()` to register new tokens (not direct Firestore writes). Never delete tokens on the client side — let the Cloud Function handle cleanup.

---

## 9. Value Type Validation — Compile-Time Type Safety

**File:** `model/ValueTypes.kt`

**Invariant:** All domain identifiers (UserId, LanguageCode, Username, etc.) are inline value classes with validation in `init`. Passing a raw `String` where a `UserId` is expected will fail at compile time.

**Rule:** Never bypass value classes by adding `@JvmInline value class Foo(val value: String)` without an `init { require(...) }` block. Keep validation centralized here, not scattered across repositories.

---

## 10. Firestore Security Rules — Key Path Dependencies

**File:** `fyp-backend/firestore.rules`

**Critical paths that must stay consistent with code:**
- `users/{userId}/friends/{friendId}` — read by FriendsViewModel, written by acceptFriendRequest
- `users/{userId}.unreadPerFriend` — counter updated by FirestoreChatRepository with dot-notation
- `friend_requests/{id}` — PENDING status checked before allowing new requests
- `user_search/{userId}` — must include `isDiscoverable` for search filter to work

**Rule:** If you rename a Firestore field in code, update the security rules too. Run `firebase deploy --only firestore:rules` after any rule change.

---

## 11. Standard UI Components — Consistency Across Screens

**Files:** `ui/components/StandardButtons.kt`, `StandardTextFields.kt`, `StandardDialogs.kt`, `StandardComponents.kt`

**Invariant:** All interactive UI elements (buttons, text fields, dialogs) should use standardized components to ensure consistent styling, spacing, and behavior across the entire app.

**Rule:** When adding new UI elements:
1. Use `StandardPrimaryButton`, `StandardSecondaryButton`, or `StandardTextButton` instead of raw `Button`, `OutlinedButton`, or `TextButton`
2. Use `StandardTextField`, `StandardPasswordField`, or `StandardSearchField` instead of raw `OutlinedTextField`
3. Use `StandardAlertDialog`, `StandardConfirmDialog`, or `StandardInfoDialog` instead of raw `AlertDialog`
4. Use `StandardEmptyState`, `StandardErrorCard`, or `StandardInfoCard` for empty/error states
5. Always use `AppSpacing` constants instead of hardcoded dp values
6. Always use `AppCorners` for shape definitions
7. Always use `MaterialTheme.colorScheme` instead of hardcoded colors

**Benefits:**
- Consistent button heights (48dp), text field styling, and dialog layouts
- Easier to update global styles (change once, applies everywhere)
- Better accessibility (consistent content descriptions and semantic roles)
- Reduced code duplication

---

## 11.1 Screen Help UX — Top-Right Info Dialog Pattern

**Files:** `screens/speech/SpeechRecognitionScreen.kt`, `screens/speech/ContinuousConversationScreen.kt`, `screens/settings/*`

**Invariant:** Long instructional text should be shown through a top-right info action dialog instead of inline body text when the screen already has action-heavy controls.

**Rule:** Keep inline copy short and task-focused; move explainers to the `Info` dialog to reduce visual clutter and keep control rows visible.

---

## 12. Performance Optimization — Debouncing and Throttling

**File:** `core/performance/PerformanceUtils.kt`

**Invariant:** Expensive operations triggered by user input or rapid events should be debounced or throttled to prevent redundant API calls and improve responsiveness.

**Patterns:**

**Debouncing (delay emission until input stops):**
```kotlin
val searchQuery by remember { mutableStateOf("") }
val debouncedQuery = rememberDebouncedValue(searchQuery, delayMillis = 300L)

LaunchedEffect(debouncedQuery) {
    // Only triggers 300ms after user stops typing
    searchUsers(debouncedQuery)
}
```

**Throttling (enforce minimum interval between executions):**
```kotlin
ThrottledLaunchedEffect(key = refreshTrigger, intervalMillis = 1000L) {
    // Only executes once per second even if refreshTrigger changes rapidly
    refreshData()
}
```

**Rule:** Apply debouncing to:
- Search fields (300ms delay)
- Text input that triggers translations or API calls (300-500ms delay)
- Auto-save operations (1-2s delay)

Apply throttling to:
- Scroll-triggered data loading (500ms-1s interval)
- Refresh operations (1s interval)
- Analytics events (5s interval)

**Benefits:**
- Reduces API calls by 80-90% during typing
- Improves UI responsiveness
- Saves Firestore read quota and costs

---

## 13. Input Validation and Sanitization — Security Guards

**File:** `core/security/SecurityUtils.kt`

**Invariant:** All user input must be validated and sanitized before processing, storage, or display to prevent injection attacks and ensure data integrity.

**Rule:** Always validate user input at entry points:
1. **Email fields** - Use `validateEmail()` before authentication
2. **Password fields** - Use `validatePassword()` to enforce strength requirements
3. **Username fields** - Use `validateUsername()` for format and length
4. **Text fields** - Use `validateTextLength()` to enforce bounds
5. **Display user content** - Use `sanitizeInput()` to escape HTML entities
6. **URLs** - Use `validateUrl()` to ensure safe protocols

**Pattern:**
```kotlin
when (val result = validateUsername(username)) {
    is ValidationResult.Valid -> proceedWithUsername(username)
    is ValidationResult.Invalid -> showError(result.message)
}

val safeText = sanitizeInput(userInput) // Escape HTML entities
```

**Rate Limiting:**
```kotlin
val loginLimiter = RateLimiter(maxAttempts = 5, windowMillis = 60_000L)

if (!loginLimiter.isAllowed(userId)) {
    showError("Too many attempts. Try again later.")
    return
}
```

**Rule:** Apply rate limiting to:
- Login attempts (5 per minute)
- Friend requests (10 per hour)
- API calls (100 per minute)
- Password reset (3 per hour)

**Benefits:**
- Prevents XSS attacks (cross-site scripting)
- Prevents SQL injection
- Prevents brute force attacks
- Enforces data integrity

---
## 14. Bottom Navigation Bar — System Bar Insets Protection

**File:** `navigation/AppNavigation.kt`

**Invariant:** The bottom NavigationBar must include `windowInsets = WindowInsets.navigationBars` to ensure it adds proper padding above device system navigation keys (home, back, recent apps buttons).

**Rule:** Without this parameter, the NavigationBar will be drawn behind the system navigation bar, making bottom icons difficult to tap on devices with gesture navigation or hardware buttons.

**Pattern:**
```kotlin
NavigationBar(
    windowInsets = WindowInsets.navigationBars  // REQUIRED
 ) {
    // navigation items
 }
```

**Benefits:**
- Bottom navigation items remain fully accessible on all devices
- Proper spacing above system gesture bar on gesture navigation devices
- No overlapping with physical navigation buttons on older devices

---

## 14.1 Friends Search Discoverability Guard

**Files:** `data/friends/FirestoreFriendsRepository.kt`, `domain/friends/EnsurePublicProfileExistsUseCase.kt`, `fyp-backend/firestore.rules`

**Invariant:** A profile with blank username must never be discoverable. Search-index docs in `user_search/{uid}` must only contain `username_lowercase` when username is non-blank and valid.

**Rule:**
1. New users start with `isDiscoverable = false` until they set a valid username.
2. Any update that blanks username must force `isDiscoverable = false` and remove `username_lowercase`.
3. Firestore rules enforce that discoverable profiles/search docs have username format `[A-Za-z0-9_]` and length 3–20.

This prevents malformed index rows and empty-name search results.

---

## 15. Red Dot Notification Persistence — Seen Items Storage

**Files:** `data/friends/SeenItemsStorage.kt`, `data/friends/SharedFriendsDataSource.kt`, `screens/friends/SharedInboxViewModel.kt`

**Invariant:** When a user views shared inbox items, those item IDs must be persisted to SharedPreferences so the red dot notification badge does NOT reappear on app restart for already-seen items.

**Implementation:**
1. `SeenItemsStorage` stores/loads seen item IDs per user in SharedPreferences
2. `SharedFriendsDataSource.startObserving()` loads persisted seen IDs on app start
3. `SharedFriendsDataSource.markSharedItemsSeen()` saves seen IDs to persistent storage
4. `SharedFriendsDataSource.stopObserving()` clears persisted IDs on logout

**Rule:**
- NEVER store seen item IDs only in memory (MutableStateFlow/private var)
- ALWAYS call `SeenItemsStorage.saveSeenItemIds()` after marking items as seen
- ALWAYS load persisted IDs in `startObserving()` to restore state across app restarts
- ALWAYS clear persisted IDs in `stopObserving()` to prevent leaking data between user accounts

**Pattern:**
```kotlin
// In startObserving()
scope.launch(Dispatchers.IO) {
    val persistedSeenIds = SeenItemsStorage.loadSeenItemIds(context, userId)
    _seenSharedItemIds.value = persistedSeenIds
}

// In markSharedItemsSeen()
_seenSharedItemIds.value = _seenSharedItemIds.value + currentIds
scope.launch(Dispatchers.IO) {
    SeenItemsStorage.saveSeenItemIds(context, userId, _seenSharedItemIds.value)
}
```

**Benefits:**
- Consistent badge behavior: red dots only appear for truly new items
- Better UX: users don't see stale notifications on app restart
- Multi-account safe: each user has separate seen items storage
- Simple implementation: uses existing SharedPreferences pattern

---

## 16. Friend Request Rate Limiting — Persisted Hourly Window

**Files:** `data/friends/FriendRequestRateLimiter.kt`, `screens/friends/FriendsViewModel.kt`

**Invariant:** Friend-request send limits must survive app restarts. The client enforces a rolling one-hour window of 10 sends per user by persisting timestamps in SharedPreferences.

**Implementation:**

---

## 17. Translation Performance Fast Paths — Preserve Latency Guards

**Files:** `data/clients/CloudTranslatorClient.kt`, `data/clients/CloudSpeechTokenClient.kt`, `data/repositories/FirebaseTranslationRepository.kt`, `data/cloud/TranslationCache.kt`, `core/ui/CommonUi.kt`

**Invariant:** Translation and auto-detect performance depend on several coordinated fast paths. Removing any of them can increase UI wait time and cloud-call volume.

**Required guards:**
1. Reuse Firebase callable instances in `CloudTranslatorClient` and `CloudSpeechTokenClient` (do not recreate callables per request).
2. Keep retry logic enabled for detect-language cloud calls, aligned with translation retry behavior.
3. Preserve repository short-circuits:
    - blank source text returns immediately;
    - same-language translation returns source text without network calls.
4. Keep `TranslationCache.getBatchCached()` memory-first lookup flow to avoid unnecessary DataStore reads.
5. Keep batch-cache writes updating both persistent cache and in-memory LRU cache.
6. In app UI language selection, skip work when selected language is unchanged.

**Rule:** Any translation refactor must keep these guards or replace them with equivalent behavior and re-verify with:

```bash
./gradlew :app:testDebugUnitTest
./gradlew :app:assembleDebug
```

**Benefits:**
- Lower translation round-trip latency
- Fewer cloud function invocations
- Reduced DataStore read overhead on repeated batch translations
- Faster app UI language switching responsiveness
1. `SharedPreferencesFriendRequestRateLimiter` stores per-user send timestamps in `friend_request_rate_limit_prefs`
2. `canSend()` prunes expired timestamps before deciding whether another send is allowed
3. `recordSend()` is only called after `SendFriendRequestUseCase` succeeds
4. `FriendsViewModel.sendFriendRequest()` surfaces retry timing to the UI when the limit is hit

**Rule:**
- NEVER keep friend-request rate limiting in memory only
- ALWAYS prune expired timestamps before both reads and writes
- ONLY record a send after a successful request; failed requests must not consume quota
- KEEP the limit aligned with the documented 10-per-hour rule in README and tests

**Benefits:**
- Prevents users from bypassing the limit by restarting the app
- Reduces accidental request spam during repeated retries
- Keeps client behaviour aligned with the existing server-side guardrail

---

## 17. Onboarding Screen — Version-Based Re-Show

**File:** `screens/onboarding/OnboardingScreen.kt`

**Invariant:** The onboarding screen must be shown on both:
1. First launch on a device (no prefs stored)
2. After an app update (stored version differs from current `BuildConfig.VERSION_NAME`)

**Implementation:**
- `SharedPreferences` key `"onboarding_complete"` (boolean) tracks whether onboarding has been completed
- `SharedPreferences` key `"onboarding_version"` (string) stores the app version at which onboarding was completed
- `isOnboardingComplete()` returns `true` only when both the flag is `true` AND the stored version matches the current `BuildConfig.VERSION_NAME`
- `markOnboardingComplete()` writes both the boolean flag and the current version name

**Rule:** Never remove the version check. Users must see updated onboarding content after an app update. The version comparison uses `BuildConfig.VERSION_NAME` (not `VERSION_CODE`) so it triggers on name changes only.

**Guard:** `OnboardingLogicTest` verifies all four cases: first launch, complete with matching version, version mismatch (update), and null version (legacy prefs).

---

## 18. sanitizeInput() — Encoding Order

**File:** `core/security/SecurityUtils.kt`

**Invariant:** The ampersand (`&`) replacement MUST be the first operation in `sanitizeInput()`. If `<` is replaced with `&lt;` first, and then `&` is replaced with `&amp;`, the already-encoded `&lt;` becomes `&amp;lt;` (double-encoded).

**Rule:** The replacement order must always be:
1. `&` → `&amp;` (FIRST — before any other entity is introduced)
2. `<` → `&lt;`
3. `>` → `&gt;`
4. `"` → `&quot;`
5. `'` → `&#x27;`
6. `/` → `&#x2F;`

**Guard:** `SanitizeInputExtendedTest` verifies that double-encoding does not occur.

---

## 19. Username Validation — Consistent Regex Across Codebase

**Files:** `core/security/SecurityUtils.kt`, `screens/settings/ProfileViewModel.kt`

**Invariant:** Username validation must use the shared `validateUsername()` function from `SecurityUtils.kt` everywhere. The canonical regex is `^[a-zA-Z0-9_-]+$` (letters, numbers, underscores, hyphens).

**Rule:** Never define inline username validation regex in ViewModels or other classes. Always delegate to `validateUsername()` for consistent rules.

---

## 20. NetworkRetry — Standard Exponential Backoff

**File:** `core/connectivity/NetworkRetry.kt`

**Invariant:** The backoff formula must be `currentDelay = currentDelay * factor` (simple multiplicative), NOT `currentDelay * factor^attempt` (double-exponential). The latter grows super-exponentially and reaches the cap much faster than intended.

**Rule:** The delay sequence for default parameters (initial=500ms, factor=2.0) should be: 500ms → 1000ms → 2000ms → 4000ms → 5000ms (capped).

---

## 21. SpeechViewModel — Synchronized Pending Saves

**File:** `screens/speech/SpeechViewModel.kt`

**Invariant:** `pendingContinuousSaves` must be accessed under `synchronized(pendingLock)` because the debounce coroutine and `onCleared()` can race to read and clear the list simultaneously. Without synchronization, the same records could be flushed to Firestore twice (duplicate history entries).

**Rule:** In `onCleared()`, cancel the debounce job FIRST, then call `flushPendingSaves()`. Never flush and then cancel — the debounce coroutine could fire between the flush and the cancel.

---

## 22. Language Count Cache — Non-Negative Invariant

**File:** `data/history/FirestoreHistoryRepository.kt`

**Invariant:** Firestore `FieldValue.increment(-1)` can drive language counts below zero if the cache document is stale or was rebuilt. Negative counts confuse generation eligibility logic and display.

**Rule:** After every decrement operation, call `clampNegativeCounts()` to read back the document and set any negative values to 0. When reading counts in `getLanguageCounts()`, filter out non-positive values.

---

## 23. CoinEligibility — Client Must Match Server

**Files:** `domain/learning/CoinEligibility.kt`, `fyp-backend/functions/src/index.ts` (awardQuizCoins)

**Invariant:** The client-side `isEligibleForCoins()` check must match the server-side `awardQuizCoins` Cloud Function. Both must:
1. Check score > 0 (1 correct answer = 1 coin)
2. Verify quiz version (`generatedHistoryCount`) equals the **learning sheet's** `historyCountAtGenerate` — NOT the user's live history count
3. Require 10+ more records than the last awarded quiz count
4. Allow first quiz for a language pair without minimum threshold

**Rule:** The third parameter to `isEligibleForCoins()` is named `currentSheetHistoryCount` (the sheet version), not `currentHistoryCount` (the live count). This prevents false rejections when users translate new sentences between quiz generation and completion.

---

## 24. Username Change — 30-Day Cooldown

**Files:** `model/user/UserSettings.kt`, `data/settings/FirestoreUserSettingsRepository.kt`, `screens/settings/ProfileViewModel.kt`, `screens/settings/ProfileScreen.kt`

**Invariant:** Username changes follow the same 30-day cooldown pattern as primary language changes:
1. `UserSettings.canChangeUsername(lastChangeMs, now)` checks elapsed time
2. `ProfileViewModel.updateUsername()` fetches settings and checks cooldown before proceeding
3. If rejected, a cooldown dialog shows remaining days/hours
4. If allowed, a confirmation dialog warns about the 30-day cooldown
5. On success, `setLastUsernameChangeMs()` records the timestamp via set-merge

**Rule:** First-time username changes (when `lastUsernameChangeMs == 0`) are always allowed. The cooldown timestamp is stored in the user's settings document alongside `lastPrimaryLanguageChangeMs`.

---

## 25. Camera OCR — Language Hint

**Files:** `screens/speech/ImageCaptureComponents.kt`, `screens/speech/SpeechRecognitionScreen.kt`

**Invariant:** The `ImageSourceDialog` shows a language hint (`CameraLanguageHint`) below the accuracy warning, reminding users to set the "From" language to match the text they're scanning. This is important because ML Kit OCR uses different recognizers for Latin vs Chinese vs Japanese vs Korean scripts, and accuracy degrades when the wrong recognizer is used.

---

## 26. setVoiceForLanguage — Must Use set-merge

**Files:** `data/settings/FirestoreUserSettingsRepository.kt`

**Invariant:** `setVoiceForLanguage()` must use `set(mapOf("voiceSettings" to mapOf(...)), SetOptions.merge())` instead of `.update("voiceSettings.langCode", value)`. The `.update()` method fails with `NOT_FOUND` if the settings document doesn't exist yet (e.g., a new user). All other settings methods use set-merge for this reason.

---

## 27. updatePublicProfile — Must Propagate All Searchable Fields

**Files:** `data/friends/FirestoreFriendsRepository.kt`

**Invariant:** `updatePublicProfile()` writes to both `profile/public` (via set-merge) AND `user_search/{uid}` (the searchable index). The search update block must propagate ALL fields that appear in `user_search`: `username`, `avatarUrl`, `isDiscoverable`, `lastActiveAt`, **and `primaryLanguage`**. If a new field is added to `user_search` in `createOrUpdatePublicProfile()`, it must also be added to the propagation block in `updatePublicProfile()`.

---

## 28. Username Enforcement Gate — ViewModel Layer

**Files:** `screens/friends/FriendsViewModel.kt`, `screens/friends/FriendsScreen.kt`

**Invariant:** All friend-mutating actions (send request, accept request, accept all) must pass through the single canonical gate `requireUsernameForFriendActions()` before reaching domain use cases. The domain layer intentionally does NOT enforce usernames — this is a ViewModel responsibility.

**Implementation:**
1. `requireUsernameForFriendActions()` checks `_uiState.value.currentUserHasUsername`
2. Returns `false` and sets an error message if username is blank
3. `requireUsernameForAddFriends()` is a legacy alias that delegates to the canonical gate
4. `sendFriendRequest()`, `acceptFriendRequest()`, and `acceptAllRequests()` all guard via this method
5. `rejectFriendRequest()` and `rejectAllRequests()` do NOT require a username (rejecting is always allowed)

**Rule:** Never add username enforcement to domain use cases (e.g., `SendFriendRequestUseCase`, `AcceptFriendRequestUseCase`). The gate belongs in the ViewModel to keep domain logic reusable.

**Guard:** `UsernameRequirementIntegrationTest` (11 tests) verifies ViewModel gate behavior. `UsernameEnforcementIntegrationTest` (12 tests) verifies domain layer does not enforce.

---

## 29. Centralized Validation Constants — Frontend-Backend Alignment

**Files:** `fyp-backend/functions/src/constants.ts`, `app/src/main/java/com/example/fyp/model/ValueTypes.kt`, `app/src/main/java/com/example/fyp/domain/learning/CoinEligibility.kt`, `app/src/main/java/com/example/fyp/model/UserSettings.kt`

**Invariant:** Critical validation constants (language code format, coin anti-cheat limits, shop pricing, spam detection) are centralized in `constants.ts` on the backend. The Android app frontend must maintain equivalent values in sync.

**Backend single source of truth (`constants.ts`):**
- `LANG_CODE_RE` — Language code validation regex (`/^[a-z]{2}(-[A-Z]{2})?$/`)
- `MIN_INCREMENT_FOR_COINS` — Anti-cheat: 10 new records required between quiz coin awards
- `MAX_QUIZ_SCORE` — Anti-cheat: cap at 50 coins per quiz (normal max is 10)
- `MAX_FRIEND_REQUESTS_PER_HOUR` — Rate limiting: 3 friend requests per hour
- `SPAM_RECENT_MESSAGES_WINDOW`, `SPAM_DUPLICATE_THRESHOLD`, `SPAM_LINK_FLOOD_THRESHOLD` — Spam detection thresholds
- `HISTORY_EXPANSION_COST`, `PALETTE_UNLOCK_COST`, `BASE_HISTORY_LIMIT`, `MAX_HISTORY_LIMIT` — Shop system pricing and limits
- `VALID_PALETTE_IDS` — List of unlockable color palettes

**Frontend counterparts:**
- `LanguageCode.kt` — Same regex pattern in `init` block validation
- `CoinEligibility.kt` — `MIN_INCREMENT_FOR_COINS` constant
- `UserSettings.kt` — `ColorPalette` enum, history limits, shop costs

**Rule:** When updating validation logic on the backend:
1. Update the constant in `constants.ts`
2. Update the equivalent constant/validation in the Android app
3. Document the change in this section
4. Verify backend tests pass (`cd fyp-backend/functions && npm test`)
5. Verify frontend tests pass (`./gradlew :app:testDebugUnitTest`)

**Benefits:**
- Eliminates scattered magic numbers across backend Cloud Functions
- Provides single source of truth with JSDoc documentation
- Makes frontend-backend alignment explicit and reviewable
- Reduces risk of validation mismatches between client and server

---
