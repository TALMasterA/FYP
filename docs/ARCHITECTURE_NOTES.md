# Architecture & Complex Logic Notes

> **Purpose:** Document complex, non-obvious business logic to prevent accidental regression.

---

## 1. UiText System — Enum / List Alignment

**Invariant:** `UiTextKey` enum ordinals must 1-to-1 map to `BaseUiTexts` list entries.

**Rule:** Add new `UiTextKey` entries and corresponding English + all 16 locale strings in the same commit. Never reorder existing entries.
**Guard:** `UiTextAlignmentTest` enforces count equality; `UiTextCompletenessTest` enforces non-blank coverage across every locale map.

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

## 1.3.1 Cloud Functions Dependency Locking

**Invariant:** `fyp-backend/functions/package-lock.json` must stay committed and in sync with `fyp-backend/functions/package.json` so Firebase deploy uses a deterministic dependency graph.

**Rule:** Update Firebase backend dependencies through an intentional `package.json` edit followed by a clean `npm install`. Do **not** use `npm audit fix --force` for this backend: npm currently suggests incompatible downgrade paths for `firebase-admin` / `firebase-functions`, which can break local manifest generation and cause Firebase CLI deploy-analysis failures.

---

## 1.4 UI Language Switching — Hardcoded Translations

**Invariant:** All 17 UI languages are fully hardcoded. Switching is instant with zero API calls.

**Rule:** The `hardcodedUiTexts` map (keyed by language code) in `CommonUi.kt` and `UiLanguageStateController.kt` is the single source of truth for UI strings. Adding a new UI language requires creating a new translations file and adding its entry to both maps. Content translation (speech/chat) still uses Azure API.

---

## 1.5 Learning Sheet Generated-Count Freshness

**Invariant:** Learning sheet detail should show the latest generated-count immediately after generation, without requiring page re-entry.

**Rule:** Prefer `LearningViewModel.sheetCountByLanguage[target]` as the displayed saved-count source, and only fall back to the sheet document value when in-memory metadata is unavailable.

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

## 2.3 Word Bank Direction Invariant (Generated + Custom + Shared)

**Invariant:** `WordBankItem.originalWord` is always the top/source-side text and `WordBankItem.translatedWord` is always the bottom/translated-side text, for both generated and custom entries.

**Rule:**
1. Non-custom sharing must send language codes aligned to the actual text fields (`originalWord` language, then `translatedWord` language).
2. Accepting shared words must persist the same direction into `custom_words`.
3. Changing custom target language must always retranslate from `originalWord` + `sourceLang` so source text remains immutable.

---

## 3. Username Propagation — Cache Consistency

**Invariant:** `FriendRelation.friendUsername` is cached and must stay in sync via `propagateUsernameChange()` (when user renames) and `syncFriendUsernames()` (on refresh).

**Rule:** Never update username without calling `propagateUsernameChange()`. The `setUsername()` method only updates the global registry, not friend caches.

---

## 4. Account Deletion — Complete Cleanup Required

**Invariant:** `deleteAccount()` must clean up ALL 18 subcollections.

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

**HistoryViewModel Reactive Tracking:** The `historyJob` must use `combine(historyRecords, isLoading, error)` to track all three SharedHistoryDataSource flows reactively. Never capture the limit in a closure — always read `_uiState.value.historyViewLimit` at collect time so limit expansions are reflected immediately.

---

## 7.0.1 Primary Language Change — WordBank Reset Contract

**Invariant:** When `primaryLanguageCode` changes, `WordBankViewModel.setPrimaryLanguageCode` must clear ALL language-specific state immediately (clusters, caches, selection) before triggering the async refresh — same as `LearningViewModel` does for sheet metadata.

**Rule:** `setPrimaryLanguageCode` must:
1. Clear `wordBankExistsCache` and `cachedCustomWordsCount`
2. Reset `languageClusters` to empty and `isLoading` to true in the UiState
3. Then launch `forceRefreshLanguageCounts` + `refreshClusters` asynchronously

**Immediate Propagation Rule:** `SettingsViewModel` (and `ShopViewModel` for history-limit changes) must call `sharedSettings.updateCache(updatedSettings)` after every successful write. This ensures all subscribing ViewModels (`WordBankViewModel`, `LearningViewModel`, `HistoryViewModel`) react immediately instead of waiting for the Firestore listener round-trip. `WordBankScreen` no longer accepts a `primaryLanguageCode` navigation parameter — it relies entirely on the ViewModel's settings observation.

---

## 7.1 Chat Mark-Read Cost Guard

**Invariant:** `markAllMessagesAsRead()` must not read individual messages and should avoid pre-reading the user document.

**Rule:** Read `chats/{chatId}/metadata/info` once to get per-chat unread count, then batch:
1. Reset `unreadCount.{uid}` to `0`
2. Decrement `users/{uid}.totalUnreadMessages` with `FieldValue.increment(-chatUnread)`
3. Reset `users/{uid}.unreadPerFriend.{friendId}` to `0` when applicable

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

**Unread counter integrity invariant:** Owner writes to `users/{uid}` may update any profile/account fields, but if a write touches `totalUnreadMessages` or `unreadPerFriend`, rules must enforce: `totalUnreadMessages` is integer and `>= 0`, `unreadPerFriend` is a map, and map key count is capped (`<= 500`). Cross-user writes remain restricted to unread-counter-only updates.

**Rule:** Update rules after field renames. Run `firebase deploy --only firestore:rules` after changes.

---

## 11. Standard UI Components — Consistency Across Screens

**Rule:** Use shared UI helpers where they exist, such as `ConfirmationDialog`, and keep layout/styling aligned through `AppSpacing`, `AppCorners`, and `MaterialTheme.colorScheme`.

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

**Rule:** Validate all user input at entry points using `validateEmail()`, `validateUsername()`, `validateTextLength()`. Run write-path inputs through `sanitizeInput()` (strips ASCII control chars, collapses whitespace, caps at 5,000 chars). Run UGC through `escapeForDisplay()` only at the moment it is rendered as HTML/markup. Apply rate limiting (login: 5/min, friend requests: 5/hr, API: 100/min, password reset: 3/hr). Server-side enforcement: friend request notifications (5/hr, Firestore query + delete), chat notifications (10/min, `checkWriteRateLimit`), feedback submissions (3/hr, `checkWriteRateLimit` + delete), learning content generation (10/hr, `enforceRateLimit`).

**RateLimiter memory bound:** `RateLimiter` is bounded to 10,000 tracked keys. When the limit is exceeded, stale keys (those with no recent attempts within the window) are pruned automatically.

**Persistent rate limiter (§2.2):** Auth-path rate limits (login, password reset, feedback, chat send) use `PersistentRateLimiter`, a DataStore-backed counter that survives process death. The static in-memory `RateLimiter` remains for non-auth read-side throttles only.

**App Check enforcement (§2.3):** `FYPApplication.onCreate()` installs Firebase App Check before any Firestore/Functions client is built. Release builds use the Play Integrity provider; debug builds (`BuildConfig.DEBUG`) use the Debug provider. The debug secret is read from gitignored `local.properties` (`appCheckDebugToken`) or CI's `APP_CHECK_DEBUG_TOKEN`, emitted as `BuildConfig.APP_CHECK_DEBUG_TOKEN`, and supplied to Firebase through `DebugAppCheckSecretRegistrar` / `InternalDebugSecretProvider`. If no token is configured, the Firebase SDK falls back to a generated debug secret that must be registered in the Firebase console. All 7 v2 `onCall` Cloud Functions (`getSpeechToken`, `translateText`, `translateTexts`, `detectLanguage`, `generateLearningContent`, `awardQuizCoins`, `spendCoins`) declare `enforceAppCheck: true`.

**Audit logging PII:** `AuditLogger.logLoginFailed()` and `logPasswordResetRequested()` obfuscate email addresses before logging to Crashlytics (first 2 chars + domain only).

**CacheInterceptor safety:** `CacheInterceptor` skips caching for requests carrying an `Authorization` header to avoid persisting sensitive authenticated responses.

**Encoding order in `escapeForDisplay()`:** `&` → `&amp;` (FIRST), then `<`, `>`, `"`, `'`, `/`. Backward-compatible reader-side `decodeLegacyHtml()` reverses entities in the OPPOSITE order (`&amp;` LAST) to safely unwrap data that was double-encoded by the pre-§2.7 `sanitizeInput`. It is applied at `ChatViewModel.loadMessages` and `CustomWordsViewModel.loadCustomWords`.

---

## 14. Bottom Navigation Bar — System Bar Insets

**Invariant:** NavigationBar must include `windowInsets = WindowInsets.navigationBars` to avoid being drawn behind system navigation.

**Rule:** Never omit this parameter.

---

## 14.1 Friends Search Discoverability Guard

**Invariant:** Profiles with blank username must never be discoverable. `user_search/{uid}` must only contain `username_lowercase` when username is non-blank and valid.

**Rule:** New users start with `isDiscoverable = false`. Blanking username forces `isDiscoverable = false` and removes `username_lowercase`. Rules enforce format `[A-Za-z0-9_]`, length 3–20.

---

## 14.2 Profile Visibility Toggle — Public/Private Consistency

**Invariant:** 
1. New profiles MUST default to `isDiscoverable = false` (PRIVATE) for security
2. Toggling visibility MUST update both `users/{uid}/profile/public` AND `user_search/{uid}` atomically
3. Private profiles (isDiscoverable=false) MUST NOT appear in search results
4. Public profiles (isDiscoverable=true) MUST have a valid username (3-20 chars, [A-Za-z0-9_])
5. `EnsurePublicProfileExistsUseCase` MUST NOT include `isDiscoverable` or `username` in its merge write, so that existing values survive transient read failures on cold start

**Model Default:** `PublicUserProfile.isDiscoverable` defaults to `false` (not `true`)

**Code Locations:**
- Model default: `app/src/main/java/com/translator/TalknLearn/model/friends/PublicUserProfile.kt:21`
- Profile init (merge-safe): `app/src/main/java/com/translator/TalknLearn/domain/friends/EnsurePublicProfileExistsUseCase.kt:42-61`
- Profile update: `app/src/main/java/com/translator/TalknLearn/data/friends/FirestoreFriendsRepository.kt:99-147`
- Search filtering: `app/src/main/java/com/translator/TalknLearn/data/friends/FirestoreFriendsRepository.kt:214-230`
  - `.whereEqualTo("isDiscoverable", true)` ensures only public profiles indexed

**@PropertyName Annotation (Bug Fix — April 2026):**
- Kotlin `val isDiscoverable: Boolean` generates getter `isDiscoverable()` in bytecode.
- Firebase SDK JavaBean convention strips the `is` prefix → infers Firestore field name as
  `discoverable`, but `updatePublicProfile()` writes the field as `isDiscoverable` (Map key).
- `toObject(PublicUserProfile::class.java)` looked for `discoverable`, never found it, and
  silently returned the default `false` — making every read show the profile as private
  regardless of the actual Firestore value.
- **Fix:** `@field:PropertyName("isDiscoverable")` and `@get:PropertyName("isDiscoverable")`
  on `PublicUserProfile.isDiscoverable` force the Firebase SDK to use the correct field name
  for both serialization (`.set(object)`) and deserialization (`.toObject()`).
- **Rule:** Any future `is`-prefixed Boolean in a Firestore data class MUST carry
  `@field:PropertyName` + `@get:PropertyName` annotations.

**Merge-Safety (Bug Fix):**
- `EnsurePublicProfileExistsUseCase` now uses `updatePublicProfile` (merge) instead of
  `createOrUpdatePublicProfile` (full `.set()` overwrite) when `getPublicProfile` returns null.
- Because `getPublicProfile` returns null on BOTH "document doesn't exist" AND "read error",
  the old full-overwrite could reset `isDiscoverable` to false on a cold-start cache miss.
- The new merge write only includes `uid`, `primaryLanguage`, and `lastActiveAt` — never
  `isDiscoverable` or `username` — so existing server-side values are preserved.

**UI Behavior:**
- MyProfileScreen.kt displays two FilterChip buttons (Public / Private)
- Default state shows Private chip selected for new profiles
- MyProfileViewModel.updateVisibility() enforces username requirement before setting public

**Firestore Rules Guard:**
- `firestore.rules:247-268` validates `isDiscoverable` boolean and requires username for discovery
- Public profile with blank username is automatically forced to private by client-side guard

**Test Coverage:**
- `EnsurePublicProfileExistsUseCaseTest.kt` verifies:
  - Merge-based init omits isDiscoverable and username (7 tests)
  - Existing profile visibility is preserved on language update
- `ProfileVisibilityToggleIntegrationTest.kt` verifies:
  - Default profiles are private and not searchable
  - Toggling to public makes profile searchable (with valid username)
  - Toggling back to private removes profile from search
  - Multiple profiles show only public ones in search results

---

## 15. Red Dot Notification Persistence — Seen Items Storage

**Invariant:** Seen item/request/message IDs persist per user/device in EncryptedSharedPreferences (via `SecureStorage`) so red dots do not reappear after app restart or same-user logout/login.

**Rule:** (1) Load persisted IDs in `startObserving()`; (2) Save IDs after mark-seen actions; (3) `stopObserving()` clears only in-memory state and must not clear persisted seen IDs.

**Reset policy:** Clear persisted seen-state only in explicit reset flows, not routine logout.

**Benefits:** Consistent badge behavior, no stale notifications on restart, multi-account safe.

**View-only clear policy:**
- Chat red dot clears only after opening that friend chat screen.
- Shared inbox red dot clears only after opening the shared inbox screen.
- Friend-request red dot clears only after opening the Friends screen.
- Pull-to-refresh must not clear red dots.
- "Dismiss all" style shortcuts must not clear red dots.

---

## 15.1 Settings Rules Type Safety — Notification Fields

**Invariant:** Notification preference and in-app badge fields in `users/{uid}/profile/settings` must be booleans when present.

**Rule:** Firestore rules enforce `bool` type checks for: `notifyNewMessages`, `notifyFriendRequests`, `notifyRequestAccepted`, `notifySharedInbox`, `inAppBadgeMessages`, `inAppBadgeFriendRequests`, `inAppBadgeSharedInbox`.

**Guard:** Backend Jest test `firestore-rules-settings.test.ts` ensures these rule guards remain in place.

---

## 15.2 AppViewModel Unread Collector Lifecycle

**Invariant:** App-level unread badge observation must have exactly one active collector pair (raw unread feed + badge-derivation combine) per logged-in user session.

**Rule:** Track and cancel both unread jobs (`unreadJob` and badge combine job) on logout and before re-subscribing during user switches/re-login. Failing to cancel the badge collector can resurrect unread badges after logout or create duplicate collectors.

**Guard:** `AppViewModelTest` includes regressions for post-logout unseen-map emissions and user-switch collector duplication.

## 15.4 Unread Baseline Re-login Guard

**Invariant:** Per-friend unread reconciliation must run on every unread snapshot, including the first snapshot after login/start.

**Rule:** `SharedFriendsDataSource.updateRawUnreadPerFriend()` must not skip first-snapshot processing. If a friend currently has unread messages, that friend must be removed from the seen set so chat red dots can appear reliably.

---

## 15.5 Shared Inbox Refresh Guard

**Invariant:** Shared inbox pull-to-refresh must never clear unseen-item badges.

**Rule:** Only `markItemsAsSeen()` when the inbox screen is viewed; refresh actions must not call mark-seen APIs.

---

## 15.6 Shared Word Accept Simplicity

**Invariant:** Accepting a shared word writes exactly one record to `users/{uid}/custom_words` and does not perform receiver-primary retranslation.

**Rule:** Preserve source/target language and translated text from the shared payload, then insert directly into custom words with trimming/length guards.

---

## 15.7 Custom Word Target-Language Edit

**Invariant:** Editing a custom word's target language must recompute `translatedWord` for the new language before persistence.

**Rule:** Use translation use case with `(currentTranslatedWord, currentTargetLang, newTargetLang)` when the current target language is known, and persist both `translatedWord` and `targetLang` in the same update.

**Fallback:** For legacy/malformed category values where current target language cannot be parsed, fall back to `(originalWord, sourceLang, newTargetLang)`.

## 15.12 Unread Counter Schema Self-Heal

**Invariant:** Chat unread counters must remain functional even if `users/{uid}` unread fields are missing or malformed.

**Rule:** On message send, unread counter updates must recover from:
- missing user doc (`NOT_FOUND`) by creating baseline `totalUnreadMessages` + `unreadPerFriend`
- malformed unread schema (`INVALID_ARGUMENT` / `FAILED_PRECONDITION`) by transactional repair of both fields before applying increment semantics.

## 15.8 Chat Read-Marking Visibility Gate

**Invariant:** Chat unread counters and red-dot seen-state must only be cleared while the chat screen is actively visible (resumed).

**Rule:** `ChatViewModel` must gate `markMessagesAsRead()` behind explicit screen visibility callbacks; message listener updates while the destination is off-screen/back-stacked must not trigger mark-read writes.

**Rule:** `ChatScreen` must signal visibility via lifecycle (`ON_RESUME` => visible, `ON_PAUSE/ON_STOP` => hidden) so unread dots clear only when the user is actually in the chat screen.

## 15.9 Shared Inbox Re-entrant Action Guard

**Invariant:** Shared inbox item actions (accept/delete/dismiss) must execute one at a time to prevent duplicate backend mutations from rapid multi-taps.

**Rule:** `SharedInboxViewModel` must short-circuit action handlers when `isProcessing == true`.

## 15.10 Learning Sheet Friend Collector Lifecycle

**Invariant:** Learning sheet friend-list mirroring must keep only one active collector per session/user to avoid duplicate emissions and stale updates after auth transitions.

**Rule:** `LearningSheetViewModel.start()` cancels and replaces the friends collector job, and `stopJobs()` must cancel both history and friends jobs.

## 15.11 Word Share Payload Validation

**Invariant:** Sharing a word requires complete language metadata (`sourceLang`, `targetLang`) and non-blank word content.

**Rule:** `WordBankViewModel.shareWord()` validates parsed language pair and text payload before invoking `ShareWordUseCase`; malformed custom-word categories must fail fast with user-facing error.

## 15.3 Friend-Chat Red Dot Consistency (Seen-State Write Path)

**Invariant:** Marking a friend chat as seen must go through a single write path to avoid duplicate persistence races and inconsistent badge state.

**Rule:** `ChatViewModel.markMessagesAsRead()` must call only `SharedFriendsDataSource.markMessageFriendSeen(friendId)` and must not call `SeenItemsStorage` directly.

**Rule:** `SharedFriendsDataSource.startObserving()` should restore persisted seen sets on `Dispatchers.IO` before starting Firestore listeners to avoid startup badge flicker from transient empty seen-state without blocking the main thread.

**Rule:** Seen-state startup must be generation-scoped. `startObserving()` increments an observe generation token and `stopObserving()` cancels the pending startup job + invalidates the token. Startup completion must verify `(currentUserId, generation)` before attaching listeners.

**Rule:** Persistence operations in seen-state updates should be wrapped with error handling (`Log.e`) so storage failures are visible instead of silent.

---

## 16. Friend Request Rate Limiting — Persisted Hourly Window

**Invariant:** 5 sends per hour, persisted in EncryptedSharedPreferences (via `SecureStorage`) to survive app restarts.

**Rule:** (1) Prune expired timestamps before reads/writes; (2) Only record send after successful request; (3) Keep aligned with documented 5-per-hour rule.

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

## 18. UI Language Switching — Instant Hardcoded Lookup

**Invariant:** UI language switching is instant because all 17 translations are hardcoded at compile time.

**Rule:** No background jobs, coordinators, or network calls are needed for UI language switching. The `UiLanguageStateController` reads from the in-memory `hardcodedUiTexts` map and updates `appTexts` state immediately.

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

## 23. escapeForDisplay() and decodeLegacyHtml() — Encoding Order

**Invariant (escape):** Ampersand (`&`) replacement MUST be FIRST. If `<` → `&lt;` happens first, then `&` → `&amp;` double-encodes to `&amp;lt;`.

**Encode order:** (1) `&` → `&amp;`, (2) `<` → `&lt;`, (3) `>` → `&gt;`, (4) `"` → `&quot;`, (5) `'` → `&#x27;`, (6) `/` → `&#x2F;`.

**Invariant (decode):** `decodeLegacyHtml()` reverses entities in the OPPOSITE order — `&lt;`, `&gt;`, `&quot;`, `&#x27;`, `&#x2F;` first, then `&amp;` LAST. This unwraps data that was double-encoded by the pre-§2.7 `sanitizeInput` without re-introducing the original character early.

**Guards:** `EscapeForDisplayTest` (no double-encoding) and `SecurityUtilsTest` (decoder round-trip + leaves clean modern data unchanged).

---

## 24. Username Validation — Consistent Regex

**Rule:** Always use `validateUsername()` from `SecurityUtils.kt`. Canonical regex: `^[a-zA-Z0-9_-]+$`.

---

## 24.1 Shared Word Accept Flow — Receiver Primary Language Normalization

**Invariant:** Accepting a shared word keeps the original term language (`sourceLang`) but normalizes the displayed translation (`translatedWord`) to the receiver's primary language when sender/receiver primary languages differ.

**Rule:** In `FirestoreSharingRepository.acceptSharedItem()` for `SharedItemType.WORD`:
1. Read receiver primary language from `users/{uid}/profile/settings.primaryLanguageCode`, and sender primary language from `users/{uid}/profile/public.primaryLanguage` (cross-user private settings reads are denied by rules)
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

---

## 24.3 Learning Interaction Readiness Guard

**Invariant:** Learning page interactions must remain locked until account-specific sheet metadata has finished loading.

**Rule:** `LearningViewModel` exposes `isSheetMetaLoading` and `generateFor()` must early-return while metadata is loading or missing for the target language. UI buttons (Generate/Open Sheet/Regenerate) must be disabled until metadata readiness is true for that language.

**Guard:** `WordBankViewModelTest` verifies settings primary changes update word-bank primary automatically, and `CustomWordsViewModelTest` verifies translation source language follows settings primary.

---

## 24.3 Learning Sheet Metadata Cache Must Be User-Scoped

**Invariant:** Learning screen sheet/quiz metadata cache cannot be reused across different authenticated users.

**Rule:** On auth user switch in `LearningViewModel.start(uid)`, clear `sheetMetaCache` and reset cache primary tracking before loading new user metadata.

**Why:** Reusing prior user cache can make existing sheets appear missing/stale until app restart and can incorrectly affect regeneration gating decisions.

**Guard:** `LearningViewModelTest` includes a user-switch regression ensuring Account B does not inherit Account A sheet metadata.

**Additional Guard:** Batch metadata fetch failures must not be cached as `exists=false` placeholders. Failed languages must remain uncached so subsequent refreshes can retry and recover automatically.

---

## 24.4 Share Inbox Write Gating — Friendship + Block Consistency

**Invariant:** A shared inbox item can be created only when sender and receiver are mutual friends and neither has blocked the other.

**Rule:**
1. App-side `FirestoreSharingRepository.shareWord/shareLearningMaterial` must pre-check `areFriends` and both block directions (`isBlocked` / `isBlockedBy` equivalent) before write.
2. Firestore rules for `users/{userId}/shared_inbox/{itemId}` and `.../content/{docId}` must enforce the same constraints via existence checks on both friend mirror docs and both blocked-user docs.

**Why:** Prevents spam/injection writes from authenticated non-friends, and keeps client behavior aligned with server authorization.

**Guard:** `firestore-rules-settings.test.ts` asserts presence of shared-inbox friendship/block guard function; `SharingRepositoryLogicTest` covers `canShareToUser` allow/deny logic.

---

## 24.5 Chat Write Gating — Friendship + Block Consistency

**Invariant:** Creating chat messages or updating chat metadata must require the same authorization as normal chat send: participants are mutual friends and neither side is blocked.

**Rule:** Firestore rules in `match /chats/{chatId}/messages/{messageId}` and `match /chats/{chatId}/metadata/info` must enforce:
1. `chatId` participant format is exactly two UIDs (`userIds.size() == 2`)
2. Mutual friendship mirror docs exist for both users
3. No block relation in either direction

**Why:** Client-side repository checks can be bypassed by direct SDK/API calls. Rules must fail-closed at write time.

**Guard:** `firestore-rules-settings.test.ts` includes regression checks for `canWriteChatContent()` and `canWriteChatMetadata()` rule guards.

---

## 25. NetworkRetry — Standard Exponential Backoff

**Invariant:** Formula: `currentDelay = currentDelay * factor` (simple multiplicative), NOT `currentDelay * factor^attempt`.

**Default sequence (initial=500ms, factor=2.0):** 500ms → 1000ms → 2000ms → 4000ms → 5000ms (capped).

**Rule:** Treat backend quota/rate-limit failures (`resource-exhausted`, "rate limit", "too many requests") as non-retryable for immediate client retries. Keep exponential retry for transient connectivity/server failures only.

**Client Guard:** `CloudTranslatorClient` enforces a short local cooldown after a rate-limit response so repeated taps/segments do not immediately re-hit the callable and extend throttling windows.

**Continuous Mode Guard:** `ContinuousConversationController` must stop continuous translation when rate-limit signatures are detected in translation errors to avoid an endless fail loop for every recognized segment.

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

**Rule:** (1) Always call `validateLanguageCode(code, paramName)` before using language codes in API calls; (2) Validation list (SUPPORTED_LANGUAGES) must be kept in sync with Android asset file; (3) Azure API normalizes codes via `toTranslatorCode()` mapping (e.g., `zh-HK` → `yue`); (4) Update both helper.ts and android asset file in same commit when adding languages; (5) Keep legacy alias normalization (currently `en` → `en-US`) until clients that send short codes are fully retired; (6) Android `FirebaseTranslationRepository` must canonicalize source/target codes before cache lookup and callable invocations (short codes/locale variants map to supported full codes, invalid source falls back to auto-detect, invalid target must fail fast client-side); (7) Expected validation/auth rejections from translation callables should be logged as warnings, not Crashlytics non-fatal errors.

**Guard:** Backend `translation.test.ts` validates error messages for invalid codes.

**Error Mapping:** Translation callable functions must map Azure HTTP `429` to Firebase `resource-exhausted` and map `5xx`/network failures to `unavailable` so clients can show appropriate retry messaging.

---

## 31. Username Change — 30-Day Cooldown

**Rule:** (1) `canChangeUsername()` checks elapsed time; (2) ViewModel fetches settings and checks cooldown; (3) Cooldown dialog on rejection; (4) Confirmation dialog warns of 30-day cooldown; (5) Record timestamp via set-merge. First-time changes are always allowed.

---

## 32. Camera OCR — Language Hint

**Invariant:** `ImageSourceDialog` shows language hint reminding users to set "From" language to match scanned text, because different script recognizers have different accuracy.

**Lifecycle Guard:** `CameraCaptureScreen` must avoid blocking `ProcessCameraProvider.get()` during disposal. Acquire provider asynchronously, bind/unbind only local use-cases, and prefer `PreviewView.ImplementationMode.COMPATIBLE` on unstable devices to reduce HWUI/native surface crashes.

---

## 33. setVoiceForLanguage — Nested Field Update + NOT_FOUND Fallback

**Invariant:** `voiceSettings` is a nested map and must preserve existing per-language keys.

**Rule:** Use `update("voiceSettings.<languageCode>", voiceName)` for the common path so only the targeted language entry changes. If update fails with `NOT_FOUND` (settings doc missing), fallback to `set(..., SetOptions.merge())` to create the document.

---

## 34. updatePublicProfile — Must Propagate All Searchable Fields

**Invariant:** Must update both `profile/public` AND `user_search/{uid}`. Search update must include ALL fields: `username`, `isDiscoverable`, `lastActiveAt`, `primaryLanguage`.

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
| `UiLanguageStateController.kt` | `@Composable` + SharedPreferences | Visual test on device |
| `ConnectivityObserver.kt` | Android ConnectivityManager callbacks | Visual test on device |
| `LocalAppLanguage.kt` | Compose `CompositionLocal` | Visual test on device |
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

## 36. Background Generation — User Must Stay In App

**Invariant:** During AI content generation (learning sheets, word banks, quizzes), user must keep the app open but can navigate away from the triggering screen.

**Behavior:**
- **Learning Sheet generation** (`generateLearningContent`): 30-300 second timeout, uses Cloud Function
- **Word Bank generation** (`generateWordBank`): Similar long-running operation
- **Quiz generation** (`generateQuiz`): Similar long-running operation
- **UI Language translation** (`UiLanguageTranslationCoordinator`): Runs in shared scope, survives navigation

**Rule:**
1. User MUST keep the app in foreground during generation (Android process lifecycle)
2. User CAN navigate to other screens — generation continues via shared scope
3. Completion banners shown via `snackbarHostState` regardless of current screen
4. Generation progress visible in relevant dropdowns/buttons (e.g., "Generating..." status)

**User Attention Required:**
- If app is backgrounded during generation, job may be killed by OS
- On slow networks, timeout may occur before completion
- Cloud Function rate limits apply (10 calls/hour for `generateLearningContent`)

**Note:** This constraint exists because long-running Cloud Functions rely on the client maintaining an active HTTP connection. Future improvement could use FCM push notifications to deliver results even when app is backgrounded.

---

## 37. Favorites Limit — Server-Side Count Enforcement

**Invariant:** `UserSettings.MAX_FAVORITE_RECORDS` (20) must be enforced via Firestore count, not in-memory state. Each individual record counts as 1; each favourite session counts as N where N is the number of records it contains.

**Bug (fixed 2026-04-11):** The original `HistoryViewModel.toggleFavorite()` and `favouriteSession()` checked `_uiState.value.favoritedTexts.size` — an in-memory `Set` populated asynchronously by `loadFavoritedTexts()`. If the set hadn't loaded yet (race condition on first tap), the check always passed and unlimited favorites could be added.

**Fix (2026-04-11):** Both methods now call `favoritesRepo.getFavoriteCount(uid)` inside the existing Mutex-protected coroutine.

**Bug (fixed 2026-04-13):** `getFavoriteCount()` only counted documents in the `favorites` collection (individual records) and ignored records embedded inside `favorite_sessions` documents. Users could bypass the 20-record limit by favouriting sessions.

**Fix (2026-04-13):** Added `getTotalFavoriteRecordCount()` to `FirestoreFavoritesRepository` which sums individual records + session-embedded records. Both `toggleFavorite()` and `favouriteSession()` now use this method. The old `getFavoriteCount()` is retained for individual-count queries.

---

## 38. AuditLogger — Friend System Audit Trail

**Invariant:** Security-relevant friend-system actions must be audit-logged via `AuditLogger` for Crashlytics observability.

**Wired (2026-04-11):** `FriendsViewModel` now calls `AuditLogger` in the success path of 6 operations: `sendFriendRequest`, `acceptFriendRequest`, `rejectFriendRequest`, `removeFriend`, `blockUser`, `unblockUser`.

**Rule:** When adding new friend-system actions, include an `AuditLogger` call in the success callback. Log calls are fire-and-forget and must not block the UI or fail the action.

---

## 39. Error Handling — Dual Visibility Invariant

**Invariant:** Every catch block in ViewModel/repository code must produce BOTH a log entry AND a user-facing message (or be explicitly documented as non-fatal with log-only).

**Rules:**
1. **User-facing errors:** Use `ErrorMessages.fromException(e, fallback)` to map exceptions to consistent, user-friendly messages. Never expose raw `e.message` to the UI.
2. **Logging:** Use `Log.e(TAG, ...)` for errors affecting user flow, `Log.w(TAG, ...)` for non-fatal/best-effort failures, `Log.i(TAG, ...)` for expected fallbacks (e.g., network-to-cache).
3. **Fail-closed security:** When a security-relevant check (cooldown, permissions) cannot verify state due to an exception, block the action rather than proceeding. Example: `ProfileViewModel` cooldown check fails closed when `fetchUserSettings` throws.
4. **No empty catch blocks:** Every `catch` must at minimum log the exception. Silent swallowing is prohibited.

**Guard:** `ProfileViewModelTest.updateUsername fails closed when settings fetch throws` verifies the cooldown fail-closed behavior.

---

## 40. Translation Record — Same-Language Guard

**Invariant:** A translation record must never be saved with `sourceLang == targetLang`. Such records carry no translatable content and create misleading language pairs on the learning screen.

**Rules:**
1. **SpeechViewModel.translate():** After resolving the actual source language (via auto-detect or manual selection), skip `saveHistory()` when `actualFromLanguage == toLanguage`.
2. **ContinuousConversationController:** Same guard before calling `saveHistory` in the `onFinal` callback.
3. **FirestoreHistoryRepository.save() / saveBatch():** Defensive repository-level guard that logs a warning and silently drops records where `sourceLang == targetLang`.
4. **rebuildLanguageCountsCache:** Uses `setOf(sourceLang, targetLang)` (not `listOf`) to prevent double-counting if a same-language record exists in Firestore from before this guard.

**Guard:** `SpeechViewModelTest.translate with same from and to language does not save history`, `SpeechViewModelTest.translate auto-detect resolving to target language does not save history`, `FirestoreHistoryRepositoryLogicTest.rebuild counts does not double-count when sourceLang equals targetLang`.
