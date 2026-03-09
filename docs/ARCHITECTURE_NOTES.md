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
For document creation fallback: catch `FirebaseFirestoreException` (NOT_FOUND) and call `set()` with the full map.

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

**Invariant:** `deleteAccount()` must clean up ALL 16 subcollections + top-level docs:
`history, word_banks, learning_sheets, quiz_attempts, quiz_stats, generated_quizzes, favorites,
custom_words, sessions, coin_awards, last_awarded_quiz, user_stats, friends, shared_inbox,
favorite_sessions, blocked_users,
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

## 16. Onboarding Screen — Version-Based Re-Show

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

## 17. sanitizeInput() — Encoding Order

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

## 18. Username Validation — Consistent Regex Across Codebase

**Files:** `core/security/SecurityUtils.kt`, `screens/settings/ProfileViewModel.kt`

**Invariant:** Username validation must use the shared `validateUsername()` function from `SecurityUtils.kt` everywhere. The canonical regex is `^[a-zA-Z0-9_-]+$` (letters, numbers, underscores, hyphens).

**Rule:** Never define inline username validation regex in ViewModels or other classes. Always delegate to `validateUsername()` for consistent rules.

---

## 19. NetworkRetry — Standard Exponential Backoff

**File:** `core/connectivity/NetworkRetry.kt`

**Invariant:** The backoff formula must be `currentDelay = currentDelay * factor` (simple multiplicative), NOT `currentDelay * factor^attempt` (double-exponential). The latter grows super-exponentially and reaches the cap much faster than intended.

**Rule:** The delay sequence for default parameters (initial=500ms, factor=2.0) should be: 500ms → 1000ms → 2000ms → 4000ms → 5000ms (capped).

---

## 20. SpeechViewModel — Synchronized Pending Saves

**File:** `screens/speech/SpeechViewModel.kt`

**Invariant:** `pendingContinuousSaves` must be accessed under `synchronized(pendingLock)` because the debounce coroutine and `onCleared()` can race to read and clear the list simultaneously. Without synchronization, the same records could be flushed to Firestore twice (duplicate history entries).

**Rule:** In `onCleared()`, cancel the debounce job FIRST, then call `flushPendingSaves()`. Never flush and then cancel — the debounce coroutine could fire between the flush and the cancel.

---

## 21. Language Count Cache — Non-Negative Invariant

**File:** `data/history/FirestoreHistoryRepository.kt`

**Invariant:** Firestore `FieldValue.increment(-1)` can drive language counts below zero if the cache document is stale or was rebuilt. Negative counts confuse generation eligibility logic and display.

**Rule:** After every decrement operation, call `clampNegativeCounts()` to read back the document and set any negative values to 0. When reading counts in `getLanguageCounts()`, filter out non-positive values.

---

## 22. CoinEligibility — Client Must Match Server

**Files:** `domain/learning/CoinEligibility.kt`, `fyp-backend/functions/src/index.ts` (awardQuizCoins)

**Invariant:** The client-side `isEligibleForCoins()` check must match the server-side `awardQuizCoins` Cloud Function. Both must:
1. Check score > 0 (1 correct answer = 1 coin)
2. Verify quiz version (`generatedHistoryCount`) equals the **learning sheet's** `historyCountAtGenerate` — NOT the user's live history count
3. Require 10+ more records than the last awarded quiz count
4. Allow first quiz for a language pair without minimum threshold

**Rule:** The third parameter to `isEligibleForCoins()` is named `currentSheetHistoryCount` (the sheet version), not `currentHistoryCount` (the live count). This prevents false rejections when users translate new sentences between quiz generation and completion.

---

## 23. Username Change — 30-Day Cooldown

**Files:** `model/user/UserSettings.kt`, `data/settings/FirestoreUserSettingsRepository.kt`, `screens/settings/ProfileViewModel.kt`, `screens/settings/ProfileScreen.kt`

**Invariant:** Username changes follow the same 30-day cooldown pattern as primary language changes:
1. `UserSettings.canChangeUsername(lastChangeMs, now)` checks elapsed time
2. `ProfileViewModel.updateUsername()` fetches settings and checks cooldown before proceeding
3. If rejected, a cooldown dialog shows remaining days/hours
4. If allowed, a confirmation dialog warns about the 30-day cooldown
5. On success, `setLastUsernameChangeMs()` records the timestamp via set-merge

**Rule:** First-time username changes (when `lastUsernameChangeMs == 0`) are always allowed. The cooldown timestamp is stored in the user's settings document alongside `lastPrimaryLanguageChangeMs`.

---

## 24. Camera OCR — Language Hint

**Files:** `screens/speech/ImageCaptureComponents.kt`, `screens/speech/SpeechRecognitionScreen.kt`

**Invariant:** The `ImageSourceDialog` shows a language hint (`CameraLanguageHint`) below the accuracy warning, reminding users to set the "From" language to match the text they're scanning. This is important because ML Kit OCR uses different recognizers for Latin vs Chinese vs Japanese vs Korean scripts, and accuracy degrades when the wrong recognizer is used.

---

## 25. setVoiceForLanguage — Must Use set-merge

**Files:** `data/settings/FirestoreUserSettingsRepository.kt`

**Invariant:** `setVoiceForLanguage()` must use `set(mapOf("voiceSettings" to mapOf(...)), SetOptions.merge())` instead of `.update("voiceSettings.langCode", value)`. The `.update()` method fails with `NOT_FOUND` if the settings document doesn't exist yet (e.g., a new user). All other settings methods use set-merge for this reason.

---

## 26. updatePublicProfile — Must Propagate All Searchable Fields

**Files:** `data/friends/FirestoreFriendsRepository.kt`

**Invariant:** `updatePublicProfile()` writes to both `profile/public` (via set-merge) AND `user_search/{uid}` (the searchable index). The search update block must propagate ALL fields that appear in `user_search`: `username`, `avatarUrl`, `isDiscoverable`, `lastActiveAt`, **and `primaryLanguage`**. If a new field is added to `user_search` in `createOrUpdatePublicProfile()`, it must also be added to the propagation block in `updatePublicProfile()`.

---
