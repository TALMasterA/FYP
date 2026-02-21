# App Improvement Suggestions

> Ordered by recommendation priority — impact vs. effort ratio.

---

## 🔴 High Priority — ✅ IMPLEMENTED

### 1. ✅ Firestore Offline Persistence Caching Strategy

**Status:** Implemented  
**Changes:**
- Set Firestore persistent cache to 50 MB (was unlimited) in `DaggerModule.kt`
- Added `Source.CACHE`-first reads in `FirestoreLearningSheetsRepository.getSheet()` and `FirestoreUserSettingsRepository.fetchUserSettings()` — cached data appears instantly, server sync follows via real-time listener.

---

### 2. ✅ Chat Message Pagination

**Status:** Implemented  
**Changes:**
- Added `isLoadingOlder` and `hasMoreMessages` to `ChatUiState`
- Added `loadOlderMessages()` to `ChatViewModel` — fetches 25 messages before the oldest loaded message
- Added infinite-scroll trigger in `ChatScreen` — auto-loads when user scrolls within 2 items of the top
- Added loading spinner at top of message list while fetching older messages

---

### 3. ✅ Error Recovery and Retry Logic

**Status:** Implemented  
**Changes:**
- Wrapped `sendTextMessage` in `FirestoreChatRepository` with `NetworkRetry.withRetry()` (3 attempts, exponential backoff)
- Wrapped `rejectFriendRequest` in `FirestoreFriendsRepository` with retry
- Added auto-restart for failed Firestore listeners in `SharedFriendsDataSource` — all 3 listeners (friends, requests, inbox) now automatically restart after 5 seconds on error

---

### 4. ✅ Navigation State Restoration

**Status:** Implemented  
**Changes:**
- Created dedicated `SharedMaterialDetailViewModel` that independently fetches item + full content from Firestore via `SavedStateHandle`, eliminating ViewModel sharing issues on process death
- `SharedMaterialDetailScreen` now works correctly even when navigated to directly or after process death
---

## 🟠 Medium Priority

### 5. Reduce Firestore Listener Count for Per-Friend Unread Badges

**Impact:** Performance, Cost  
**Effort:** Medium

`FriendsViewModel.updateUnreadObservers()` creates one real-time listener per friend to track unread counts (via `observeChatMetadata`). With 20 friends, that's 20 concurrent Firestore listeners.

**Suggestions:**
- Aggregate unread counts into a single user-level document field (e.g., `unreadPerFriend: { friendId1: 3, friendId2: 1 }`) updated atomically in `updateChatMetadata`.
- Observe this single document instead of N metadata documents.
- This reduces Firestore connections from N to 1 and cuts bandwidth significantly.

---

### 6. Add Accessibility Improvements

**Impact:** UX, Compliance  
**Effort:** Medium

Several composables use icons without meaningful `contentDescription`, and touch targets may be too small for accessibility.

**Suggestions:**
- Audit all `Icon` and `IconButton` composables for proper `contentDescription` (some already have "Dismiss" hardcoded in English — these should use the `t()` translator).
- Ensure minimum touch target size of 48dp per Material Design guidelines.
- Add `semantics` blocks for screen readers on complex cards (FriendCard, SharedItemCard).
- Support dynamic text sizes — the app already has font scaling via `fontSizeScale` which is great.

---

### 7. Add Unit Tests for Friend System Use Cases

**Impact:** Reliability, Maintainability  
**Effort:** Medium

The `test/` directory exists and has test infrastructure (`mockito-kotlin`, `coroutines-test`), but the friend system domain layer (21 use cases) appears to lack test coverage.

**Suggestions:**
- Write unit tests for critical use cases: `SendFriendRequestUseCase`, `AcceptFriendRequestUseCase`, `ShareWordUseCase`, `ShareLearningMaterialUseCase`.
- Mock `FriendsRepository`, `SharingRepository`, `ChatRepository` and test edge cases (sending request to self, duplicate request, sharing to non-friend).
- Add ViewModel tests for `FriendsViewModel` and `ChatViewModel` state transitions.

---

### 8. Implement Friend Display Name Support

**Impact:** UX  
**Effort:** Low

`FriendRelation` has both `friendUsername` and `friendDisplayName`, and `ChatScreen` receives both. However, `FriendCard` only shows `friendUsername`, and many places don't use `friendDisplayName`.

**Suggestions:**
- Show `friendDisplayName` as the primary label in `FriendCard` with `@friendUsername` as secondary text.
- Apply the same pattern in `SearchResultCard` and `FriendRequestCard`.
- This matches social media conventions and provides better UX when users have display names.

---

### 9. Add Pull-to-Refresh on Friends and Shared Inbox Screens

**Impact:** UX  
**Effort:** Low

`LearningScreen` already uses `PullToRefreshBox`, but `FriendsScreen` and `SharedInboxScreen` rely solely on real-time listeners. If a listener stalls, users have no manual refresh option.

**Suggestions:**
- Wrap the `LazyColumn` in `FriendsScreen` and `SharedInboxScreen` with `PullToRefreshBox`.
- On refresh, restart the shared data source listeners via `sharedFriendsDataSource.stopObserving()` then `startObserving()`.

---

### 10. Implement Message Read Receipts UI

**Impact:** UX  
**Effort:** Medium

Messages are tracked as read/unread in Firestore (`isRead` field on `FriendMessage`), and `markAllMessagesAsRead` works. However, the UI doesn't show read receipts to the sender.

**Suggestions:**
- Add a small "✓✓" (double check) or "Read" indicator on sent messages when `isRead = true`.
- This requires observing the read status for sent messages — currently the listener already returns all messages including read status.

---

### 11. Add Animated Transitions Between Screens

**Impact:** UX Polish  
**Effort:** Low–Medium

Navigation between screens uses the default Compose Navigation transitions (crossfade). Custom animations would improve perceived quality.

**Suggestions:**
- Add `enterTransition`/`exitTransition` to `composable()` calls in `AppNavigation.kt`.
- Use `slideInHorizontally`/`slideOutHorizontally` for forward/back navigation.
- Use `fadeIn`/`fadeOut` for modal-like screens (Settings, Profile).

---

## 🟢 Lower Priority (Nice to Have)

### 12. Implement Chat Message Search

**Impact:** Feature  
**Effort:** Medium–High

As chat history grows, users may want to search for specific messages.

**Suggestions:**
- Add a search bar in `ChatScreen` that filters messages locally by content.
- For server-side search, consider using Firestore full-text search via Algolia or Typesense integration.

---

### 13. Add Image/Media Sharing in Chat

**Impact:** Feature  
**Effort:** High

The chat system currently supports text only. `MessageType` enum has `SHARED_WORD` and `SHARED_LEARNING_MATERIAL` but no `IMAGE` type.

**Suggestions:**
- Add Firebase Storage integration for image uploads.
- Add `MessageType.IMAGE` and render images inline in `MessageBubble`.
- Use `coil-compose` (already in dependencies) for async image loading.

---

### 14. Add Friend Request Notifications via FCM

**Impact:** Engagement  
**Effort:** Medium

Currently, notification badges only work while the app is open. Users won't know about new friend requests or messages until they open the app.

**Suggestions:**
- Use Firebase Cloud Messaging (FCM) + Cloud Functions (already set up in `fyp-backend/functions/`) to send push notifications on new friend requests, messages, and shared items.
- The backend infrastructure (`firebase.json`, `functions/`) is already in place.

---

### 15. Implement Chat Typing Indicators

**Impact:** UX Polish  
**Effort:** Medium

Show when a friend is typing a message.

**Suggestions:**
- Add a `typing` field to chat metadata: `typingUsers: { userId: timestamp }`.
- Update on `onMessageTextChange` with debounce (500ms).
- Show a "typing…" bubble in `ChatScreen` when the friend's typing timestamp is recent (< 3 seconds).
- Clean up stale typing indicators on the server side via TTL or Cloud Functions.

---

### 16. Add Dark/Light Mode Preview for Color Palettes

**Impact:** UX Polish  
**Effort:** Low

`ColorPaletteSelector` already shows palette options, but users can't preview what their app will look like before selecting.

**Suggestions:**
- Add a small preview card showing sample UI elements (text, buttons, cards) in the palette's colors.
- Show both light and dark variants in the preview.

---

### 17. Implement Batch Operations for Word Bank

**Impact:** UX  
**Effort:** Medium

Users can only share or delete words one at a time. For users with many words, this is tedious.

**Suggestions:**
- Add multi-select mode to the word bank list.
- Support batch delete, batch share, and batch export.
- Use Firestore batch writes (max 500 per batch) for efficiency.

---

### 18. Add App Onboarding / Tutorial Flow

**Impact:** UX, Retention  
**Effort:** Medium

New users land on the Home screen with no guidance about features like continuous conversation, learning sheets, or the friend system.

**Suggestions:**
- Add a first-launch onboarding flow with 3–4 screens explaining key features.
- Use `DataStore` to track whether onboarding has been completed.
- Add contextual tooltips on first visit to each major feature screen.

---

### 19. Implement Conversation Export

**Impact:** Feature  
**Effort:** Low–Medium

Users may want to export chat conversations or speech recognition history for study.

**Suggestions:**
- Add an "Export" button that generates a text/PDF file of the conversation.
- Use Android's `ShareSheet` (already used in `MyProfileScreen`) to share the exported file.
- Include timestamps, translations, and original text in the export.

---

### 20. Add Crash-Free Session Monitoring Dashboard

**Impact:** Reliability  
**Effort:** Low

Firebase Crashlytics is already integrated. Enhance monitoring:

**Suggestions:**
- Add custom Crashlytics keys for screen name, user action, and feature usage to make crash reports more actionable.
- Log non-fatal exceptions for network failures, Firestore errors, and translation API failures.
- Set up Crashlytics alerts for crash-free rate drops below 99%.

---

*Last updated: Feb 2026*

