# Architecture & Complex Logic Notes

> **Purpose:** Document complex, non-obvious business logic to prevent accidental regression.

---

## 1. UiText System — Enum / List Alignment

**Invariant:** `UiTextKey` enum ordinals must 1-to-1 map to `BaseUiTexts` list entries.

**Rule:** Add new `UiTextKey` entries and corresponding strings in same commit. Never reorder existing entries.  
**Guard:** `UiTextAlignmentTest` enforces count equality.

---

## 1.1 Mode Copy Consistency

**Invariant:** Quick Translate (discrete) vs Live Conversation (continuous) intent must be consistent across locales.

**Rule:** Update equivalent strings in all maintained locales simultaneously.

---

## 1.2 Quick Translate Auto-Detect Fallback

**Invariant:** Auto mode must work even if translation omits detected language metadata.

**Rule:** (1) Use inline detection if available; (2) Fallback to `DetectLanguageUseCase`; (3) Never pass `"auto"` to TTS; (4) Auto-clear detected-language status after short delay; (5) Provide user-triggered refresh to clear stale state.

---

## 1.3 Repository Task Completion Policy (Agents)

**Invariant:** For any prompt that changes repository files, completion requires tree maintenance, docs audit/update, and Android verification.

**Rule:** Follow `.github/copilot-instructions.md` before finalizing:
1. Update `docs/treeOfImportantfiles.txt` when files/structure/important entries change
2. Update impacted files in `docs/` and `README.md`
3. Run `.\\gradlew.bat :app:testDebugUnitTest`
4. Run `.\\gradlew.bat :app:assembleDebug`
5. Report outcomes in summary

---

## 2. Firestore Nested Map Writes — Set-Merge vs Update

**Invariant:** `set(..., SetOptions.merge())` with nested `Map` **overwrites the entire map**, losing sibling keys.

**Rule:** Use `update()` with dot-notation for nested fields:
```kotlin
// CORRECT
db.document(path).update("unreadPerFriend.$friendId", count)

// WRONG — overwrites entire unreadPerFriend map
db.document(path).set(mapOf("unreadPerFriend" to mapOf(...)), SetOptions.merge())
```

For document creation fallback: catch `FirebaseFirestoreException`, only fallback on `NOT_FOUND`, call `set()` with full map.

---

## 2.1 Error Visibility — Auto-Scroll To Error Banner

**Invariant:** When `uiState.error` is set on list screens, viewport should scroll to error banner immediately.

**Rule:** Use `rememberLazyListState()` + `LaunchedEffect(error)` + `animateScrollToItem()` before auto-dismiss.

---

## 2.2 Word Bank Error Lifecycle

**Invariant:** Errors should be short-lived and user-visible without manual dismissal.

**Rule:** Auto-dismiss via `LaunchedEffect(error)` and clear through `ViewModel.clearError()`.

---

## 3. Username Propagation — Cache Consistency

**Invariant:** `FriendRelation.friendUsername` is cached and must stay in sync via `propagateUsernameChange()` (when user renames) and `syncFriendUsernames()` (on refresh).

**Rule:** Never update username without calling `propagateUsernameChange()`. The `setUsername()` method only updates the global registry, not friend caches.

---

## 4. Account Deletion — Complete Cleanup Required

**Invariant:** `deleteAccount()` must clean up ALL 17 subcollections.

**Rule:** If adding a new collection, add its cleanup to `deleteAccount()`. Write a test asserting cleanup count stays in sync.

---

## 5. Coin System — Anti-Cheat Logic

**Invariant:** Coins only awarded when: (1) quiz from ≥`MIN_RECORDS_FOR_LEARNING_SHEET` records, (2) history count increased since last award, (3) score meets minimum.

**Rule:** Only call coins via `awardCoinsForQuiz()`. Never reset `lastAwardedQuizCountByLanguage` without cause.

---

## 6. SharedFriendsDataSource — Single Listener Pattern

**Invariant:** Holds exactly **one** Firestore listener per collection. Multiple ViewModels share flows instead of creating listeners.

**Rule:** Do NOT add additional `observeFriends()` calls in ViewModels — use `sharedFriendsDataSource.friends` instead. Extra listeners multiply read costs.

---

## 7. History Limit — Firestore Cost Boundary

**Invariant:** Capped at `UserSettings.historyViewLimit` (30–60). Expansion requires coin purchase.

**Rule:** Never increase `DEFAULT_HISTORY_LIMIT` — it controls a Firestore read quota boundary.

---

## 7.1 Chat Mark-Read Cost Guard

**Invariant:** `markAllMessagesAsRead()` must not read individual messages and should avoid pre-reading the user document.

**Rule:** Read `chats/{chatId}/metadata/info` once to get per-chat unread count, then batch:
1. Reset `unreadCount.{uid}` to `0`
2. Decrement `users/{uid}.totalUnreadMessages` with `FieldValue.increment(-chatUnread)`
3. Delete `users/{uid}.unreadPerFriend.{friendId}` when applicable

**Fallback:** If user doc update returns `NOT_FOUND`, recreate user counters with `set(..., merge=true)` and `totalUnreadMessages=0` to avoid negative bootstrap values.

This keeps common-path mark-read at 1 read + 2 writes, independent of message volume.

---

## 8. FCM Token Management

**Invariant:** Tokens stored at `users/{uid}/fcm_tokens/{tokenId}` with `updatedAt`. Pruned by Cloud Function after 60 days.

**Rule:** Always use `FcmNotificationService.storeToken()`. Never delete tokens on client — let Cloud Function handle cleanup.

---

## 9. Value Type Validation — Compile-Time Type Safety

**Invariant:** Domain identifiers (UserId, LanguageCode, etc.) are inline value classes with `init` validation.

**Rule:** All value classes must have `init { require(...) }`. Keep validation centralized in `ValueTypes.kt`.

---

## 10. Firestore Security Rules — Key Path Dependencies

**Critical paths:**
- `users/{userId}/friends/{friendId}` — read by FriendsViewModel, written by acceptFriendRequest
- `users/{userId}.unreadPerFriend` — counter updated with dot-notation
- `friend_requests/{id}` — PENDING status checked before new requests
- `user_search/{userId}` — must include `isDiscoverable` for search filter

**Friend mirror-delete invariant:** Client-side batch deletes both `users/A/friends/B` and `users/B/friends/A` atomically. Rules allow counterpart `delete` only; counterpart `update` is forbidden.

**Rule:** Update rules after field renames. Run `firebase deploy --only firestore:rules` after changes.

---

## 11. Standard UI Components — Consistency Across Screens

**Rule:** Always use `StandardPrimaryButton`, `StandardTextField`, `StandardDialog` instead of raw Compose components. Use `AppSpacing` and `AppCorners` constants. Use `MaterialTheme.colorScheme` for colors.

**Benefits:** Consistent styling, easier global updates, better accessibility.

---

## 11.1 Screen Help UX — Top-Right Info Dialog Pattern

**Invariant:** Long instructional text should be in top-right info dialog, not inline body text.

**Rule:** Keep inline copy short; move explainers to Info dialog to reduce visual clutter.

---

## 12. Performance Optimization — Debouncing and Throttling

**Debouncing (delay until input stops):**
```kotlin
val debouncedQuery = rememberDebouncedValue(searchQuery, delayMillis = 300L)
LaunchedEffect(debouncedQuery) { searchUsers(debouncedQuery) }
```

**Throttling (enforce minimum interval):**
```kotlin
ThrottledLaunchedEffect(key = refreshTrigger, intervalMillis = 1000L) { refreshData() }
```

**Rule:** Debounce search (300ms), text input (300-500ms), auto-save (1-2s). Throttle scroll-loading (500ms-1s), refresh (1s), analytics (5s).

---

## 13. Input Validation and Sanitization — Security Guards

**Rule:** Validate all user input at entry points using `validateEmail()`, `validatePassword()`, `validateUsername()`, `validateTextLength()`. Sanitize with `sanitizeInput()`. Apply rate limiting (login: 5/min, friend requests: 10/hr, API: 100/min, password reset: 3/hr).

**Encoding order in `sanitizeInput()`:** `&` → `&amp;` (FIRST), then `<`, `>`, `"`, `'`, `/`.

---

## 14. Bottom Navigation Bar — System Bar Insets

**Invariant:** NavigationBar must include `windowInsets = WindowInsets.navigationBars` to avoid being drawn behind system navigation.

**Rule:** Never omit this parameter.

---

## 14.1 Friends Search Discoverability Guard

**Invariant:** Profiles with blank username must never be discoverable. `user_search/{uid}` must only contain `username_lowercase` when username is non-blank and valid.

**Rule:** New users start with `isDiscoverable = false`. Blanking username forces `isDiscoverable = false` and removes `username_lowercase`. Rules enforce format `[A-Za-z0-9_]`, length 3–20.

---

## 15. Red Dot Notification Persistence — Seen Items Storage

**Invariant:** Seen item/request/message IDs persist per user/device in SharedPreferences so red dots do not reappear after app restart or same-user logout/login.

**Rule:** (1) Load persisted IDs in `startObserving()`; (2) Save IDs after mark-seen actions; (3) `stopObserving()` clears only in-memory state and must not clear persisted seen IDs.

**Reset policy:** Clear persisted seen-state only in explicit reset flows, not routine logout.

**Benefits:** Consistent badge behavior, no stale notifications on restart, multi-account safe.

---

## 15.1 Settings Rules Type Safety — Notification Fields

**Invariant:** Notification preference and in-app badge fields in `users/{uid}/profile/settings` must be booleans when present.

**Rule:** Firestore rules enforce `bool` type checks for: `notifyNewMessages`, `notifyFriendRequests`, `notifyRequestAccepted`, `notifySharedInbox`, `inAppBadgeMessages`, `inAppBadgeFriendRequests`, `inAppBadgeSharedInbox`.

**Guard:** Backend Jest test `firestore-rules-settings.test.ts` ensures these rule guards remain in place.

---

## 16. Friend Request Rate Limiting — Persisted Hourly Window

**Invariant:** 10 sends per hour, persisted in SharedPreferences to survive app restarts.

**Rule:** (1) Prune expired timestamps before reads/writes; (2) Only record send after successful request; (3) Keep aligned with documented 10-per-hour rule.

**Guard:** `FriendRequestRateLimiterTest` verifies persistence and restart safety.

---

## 17. Translation Performance Fast Paths — Preserve Latency Guards

**Required guards:**
1. Reuse Firebase callable instances (don't recreate per request)
2. Keep retry logic enabled for detect-language calls
3. Preserve repository short-circuits: blank source returns immediately; same-language skips network
4. Keep `TranslationCache.getBatchCached()` memory-first lookup
5. Batch-cache writes update both persistent cache and in-memory LRU
6. Skip work in UI language selection when unchanged

**Rule:** Refactors must preserve or replace with equivalent behavior. Verify with `testDebugUnitTest` and `assembleDebug`.

---

## 18. UI Language Switching Continuity — Background Job Coordinator

**Invariant:** Long-running UI-language translation jobs must continue even when users navigate away.

**Rule:** (1) Run via shared `UiLanguageTranslationCoordinator` scope, not screen scope; (2) Surface explicit status (`in progress`, `completed`, `failed`) in dropdown; (3) Auto-dismiss status after delay; (4) Enforce guest translation limits before network call.

This prevents job cancellation on route changes and avoids stale completion banners.

---

## 19. Notification Toggle Consistency — Firestore to Local Cache Sync

**Invariant:** FCM dispatch uses local SharedPreferences, so those values must mirror server-backed settings continuously.

**Rule:** (1) Write changed toggle immediately; (2) Sync all push notification fields when `SharedSettingsDataSource.settings` emits; (3) Never assume local cache is valid on startup.

---

## 20. Friend Removal and Search Consistency Guards

**Rule:** (1) Optimistic remove must rollback if `RemoveFriendUseCase` fails; (2) `blockAndRemoveFriend()` must abort before blocking if remove fails; (3) Reject `userId == friendId`; (4) Rules allow counterpart delete for reciprocal unfriend batches; (5) Search excludes: self, existing friends, blocked users; (6) Unfriended users remain searchable unless blocked.

---

## 21. Friend Removal UI — Visual Delete Mode Feedback

**Invariant:** Selected friends in delete mode must have red border (3dp) for clear confirmation before dialog.

**Rule:** Red border appears only when BOTH `isSelected == true` AND `isDeleteMode == true`. Use `MaterialTheme.colorScheme.error` and match card corner radius.

---

## 22. Onboarding Screen — Version-Based Re-Show

**Invariant:** Show onboarding on: (1) first launch, (2) after app update (version differs from stored).

**Rule:** Store `"onboarding_complete"` (boolean) and `"onboarding_version"` (string). Both must match current `BuildConfig.VERSION_NAME`.

**Guard:** `OnboardingLogicTest` verifies all four cases.

---

## 23. sanitizeInput() — Encoding Order

**Invariant:** Ampersand (`&`) replacement MUST be first. If `<` → `&lt;` happens first, then `&` → `&amp;` double-encodes to `&amp;lt;`.

**Order:** (1) `&` → `&amp;`, (2) `<` → `&lt;`, (3) `>` → `&gt;`, (4) `"` → `&quot;`, (5) `'` → `&#x27;`, (6) `/` → `&#x2F;`.

**Guard:** `SanitizeInputExtendedTest` verifies no double-encoding.

---

## 24. Username Validation — Consistent Regex

**Rule:** Always use `validateUsername()` from `SecurityUtils.kt`. Canonical regex: `^[a-zA-Z0-9_-]+$`.

---

## 24.1 Shared Word Accept Flow — Receiver Primary Language Normalization

**Invariant:** Accepting a shared word keeps the original term language (`sourceLang`) but normalizes the displayed translation (`translatedWord`) to the receiver's primary language when sender/receiver primary languages differ.

**Rule:** In `FirestoreSharingRepository.acceptSharedItem()` for `SharedItemType.WORD`:
1. Read sender/receiver primary language from `users/{uid}/profile/settings.primaryLanguageCode`
2. If primary languages differ and shared `targetLang` is not already receiver primary, translate shared `targetText` into receiver primary before writing to `custom_words`
3. If translation fails, keep original shared translation as fallback (accept flow must remain non-fatal)

**Guard:** `SharingRepositoryLogicTest` includes regression tests for: cross-primary translation success path, same-primary no-translate path, and translation-failure fallback path.

---

## 24.2 Learning/WordBank/Quiz Primary Language Source Of Truth

**Invariant:** Learning sheets, generated word banks, and quizzes are keyed by account `primaryLanguageCode` only (from user settings), never by app UI language.

**Rule:**
1. `LearningViewModel` and `WordBankViewModel` must derive primary language from `SharedSettingsDataSource.settings.primaryLanguageCode`
2. `CustomWordsViewModel.translateCustomWord()` must also use settings primary language for source language
3. Navigation-passed values can be used as initial hints only; runtime state must follow settings stream

**Guard:** `WordBankViewModelTest` verifies settings primary changes update word-bank primary automatically, and `CustomWordsViewModelTest` verifies translation source language follows settings primary.

---

## 25. NetworkRetry — Standard Exponential Backoff

**Invariant:** Formula: `currentDelay = currentDelay * factor` (simple multiplicative), NOT `currentDelay * factor^attempt`.

**Default sequence (initial=500ms, factor=2.0):** 500ms → 1000ms → 2000ms → 4000ms → 5000ms (capped).

---

## 26. SpeechViewModel — Synchronized Pending Saves

**Invariant:** `pendingContinuousSaves` must be accessed under `synchronized(pendingLock)` because debounce coroutine and `onCleared()` can race.

**Rule:** In `onCleared()`, cancel debounce job FIRST, then flush. Never flush then cancel — debounce could fire between them.

---

## 27. Language Count Cache — Non-Negative Invariant

**Invariant:** `FieldValue.increment(-1)` can drive counts below zero if cache is stale.

**Rule:** After every decrement, call `clampNegativeCounts()` to set any negative values to 0.

---

## 28. CoinEligibility — Client Must Match Server

**Invariant:** Client-side `isEligibleForCoins()` must match server `awardQuizCoins` Cloud Function.

**Rule:** Both must: (1) Check score > 0; (2) Verify quiz version equals sheet's `historyCountAtGenerate`; (3) Require 10+ more records than last award; (4) Allow first quiz for language pair without threshold.

The third parameter is named `currentSheetHistoryCount` (sheet version), not `currentHistoryCount` (live count). This prevents false rejections when users translate between generation and completion.

---

## 29. Translation Language Code Validation — Cloud Function Guard

**Invariant:** Language codes in `translateText()`, `translateTexts()`, and `detectLanguage()` Cloud Functions must match supported language list.

**Supported codes:** `en-US`, `zh-HK`, `zh-TW`, `zh-CN`, `ja-JP`, `fr-FR`, `de-DE`, `ko-KR`, `es-ES`, `id-ID`, `vi-VN`, `th-TH`, `fil-PH`, `ms-MY`, `pt-BR`, `it-IT`, `ru-RU` (must match `app/src/main/assets/azure_languages.json`).

**Rule:** (1) Always call `validateLanguageCode(code, paramName)` before using language codes in API calls; (2) Validation list (SUPPORTED_LANGUAGES) must be kept in sync with Android asset file; (3) Azure API normalizes codes via `toTranslatorCode()` mapping (e.g., `zh-HK` → `yue`); (4) Update both helper.ts and android asset file in same commit when adding languages; (5) Keep legacy alias normalization (currently `en` → `en-US`) until clients that send short codes are fully retired.

**Guard:** Backend `translation.test.ts` validates error messages for invalid codes.

**Error Mapping:** Translation callable functions must map Azure HTTP `429` to Firebase `resource-exhausted` and map `5xx`/network failures to `unavailable` so clients can show appropriate retry messaging.

---

## 31. Username Change — 30-Day Cooldown

**Rule:** (1) `canChangeUsername()` checks elapsed time; (2) ViewModel fetches settings and checks cooldown; (3) Cooldown dialog on rejection; (4) Confirmation dialog warns of 30-day cooldown; (5) Record timestamp via set-merge. First-time changes are always allowed.

---

## 32. Camera OCR — Language Hint

**Invariant:** `ImageSourceDialog` shows language hint reminding users to set "From" language to match scanned text, because different script recognizers have different accuracy.

---

## 33. setVoiceForLanguage — Must Use set-merge

**Invariant:** Must use `set(..., SetOptions.merge())` instead of `.update()` because `.update()` fails with `NOT_FOUND` if settings doc doesn't exist yet.

---

## 34. updatePublicProfile — Must Propagate All Searchable Fields

**Invariant:** Must update both `profile/public` AND `user_search/{uid}`. Search update must include ALL fields: `username`, `avatarUrl`, `isDiscoverable`, `lastActiveAt`, `primaryLanguage`.

**Rule:** If adding new field to `user_search`, add to propagation block too.

---

## 33. Username Enforcement Gate — ViewModel Layer

**Invariant:** All friend mutations (send, accept, accept-all) must pass through `requireUsernameForFriendActions()` before domain use cases. Domain layer intentionally does NOT enforce.

**Rule:** Gate belongs in ViewModel for reusability. `rejectFriendRequest()` and `rejectAllRequests()` don't require username (rejecting always allowed).

**Guard:** `UsernameRequirementIntegrationTest` (11 tests) and `UsernameEnforcementIntegrationTest` (12 tests) verify this split.

---

## 35. Untestable Components — Android/Framework Dependencies

The following components cannot be unit tested due to Android framework dependencies. When modifying these, manual testing is required.

| Component | Reason | Verification |
|-----------|--------|--------------|
| `SecureStorage.kt` | Android Keystore + EncryptedSharedPreferences | Manual test on device |
| `AzureSpeechRepository.kt` | Azure Speech SDK hardware-dependendent | Integration test on device |
| `AzureSpeechProvider.kt` | Speech SDK native bindings | Integration test on device |
| `NetworkMonitor.kt` | ConnectivityManager system service | Verify offline banner shows |
| `UiLanguageStateController.kt` | `@Composable` + SharedPreferences | Visual test on device |
| `ConnectivityObserver.kt` | Android ConnectivityManager callbacks | Visual test on device |
| `LocalAppLanguage.kt` | Compose `CompositionLocal` | Visual test on device |
| `AudioRecorder.kt` | MediaRecorder hardware access | Manual recording test |
| `HapticFeedback.kt` | Vibrator system service | Manual haptic test |
| `FcmNotificationService.kt` dispatch | Firebase Messaging service | Push notification test |
| Navigation graphs (e.g., `FriendsChatGraph.kt`) | Compose Navigation DSL | UI navigation test |
| `FYPApplication.kt` / `MainActivity.kt` | Android lifecycle classes | App startup test |
| Permission handlers (`CameraPermissions.kt`) | Runtime permissions | Manual permission flow |
| DI modules | Hilt/Dagger wiring | Build verifies DI graph |

**Rule:** When changing these components:
1. Document expected behavior in code comments
2. Run `./gradlew assembleDebug` to verify compilation
3. Test affected flow on physical device or emulator
4. Update this table if new untestable components are added

---