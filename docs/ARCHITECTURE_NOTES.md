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

**Invariant:** `deleteAccount()` must clean up ALL 14 subcollections + top-level docs:
`history, word_banks, learning_sheets, quiz_attempts, quiz_stats, generated_quizzes, favorites,
custom_words, sessions, coin_awards, last_awarded_quiz, user_stats, friends, shared_inbox,
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

**Invariant:** History is capped at `UserSettings.historyViewLimit` (50–100). Expanding beyond requires explicit coin purchase.  
`BASE_HISTORY_LIMIT = 50`, `MAX_HISTORY_LIMIT = 100`, expansion increment = 10.

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