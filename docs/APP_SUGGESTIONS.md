# App Improvement Suggestions

Ordered by estimated user impact and implementation effort ratio (highest value first).

---

## 1. Friend Presence / Online Status  ⭐ High impact

**What**: Show a green dot or "X minutes ago" label on `FriendCard` and in the chat header.  
**Why**: Users have no way to tell when a friend is available. This is the most fundamental social feature missing.  
**How**: Write `lastSeen: Timestamp` to the user document every 60 s while the app is foregrounded (extend `AppViewModel`'s keep-alive coroutine). Read it in `FriendsViewModel` when loading friends.  
**Files**: `AppViewModel`, `FirestoreFriendsRepository`, `FriendCard`, `ChatScreen`.

---

## 2. Read Receipts in Chat (delivered / read ticks)  ⭐ High impact

**What**: Show ✓ (sent) and ✓✓ (read) tick icons on the sender's chat bubble.  
**Why**: Without them, users repeatedly re-send messages assuming they were lost.  
**How**: Add `readAt: Timestamp?` to `FriendMessage`. Batch-update it when the recipient opens the chat (extend `markAllMessagesAsRead`). Render a tick icon in `MessageBubble`.  
**Files**: `FriendMessage`, `FirestoreChatRepository`, `ChatScreen`.

---

## 3. Richer Error Recovery — Retry Button on Network Errors

**What**: When a network operation fails (load history, send message, load word bank), show a **Retry** button instead of only a dismiss-able message.  
**Why**: Currently errors auto-dismiss. If the user is offline and comes back online, they must re-navigate to retry.  
**How**: Keep an `isError: Boolean` + `retryAction: (() -> Unit)?` in each UiState. Render a `Button("Retry")` when set.  
**Files**: `HistoryViewModel`, `ChatViewModel`, `LearningViewModel`, their respective screens.

---

## 4. Offline Mode Indicator

**What**: Show a subtle banner ("Offline — using cached data") when Firestore cache is the data source.  
**Why**: Firestore offline persistence is now enabled; users will see stale data without knowing why it isn't updating.  
**How**: Collect `FirebaseFirestore.getInstance().snapshotListenerSuppressor` or monitor `ConnectivityManager` and expose a `isOffline: StateFlow<Boolean>` in `AppViewModel`.  
**Files**: `AppViewModel`, `AppNavigation` (banner slot), `StandardScreenScaffold`.

---

## 5. Friend Request Note / Context Message

**What**: Allow the sender to attach a short note (≤80 chars) when sending a friend request.  
**Why**: Without context, users reject requests from people they don't recognise.  
**How**: Add an optional `TextField` in `SearchUsersDialog`. Store `note: String` in the `friend_request` Firestore document. Show it in `FriendRequestCard`.  
**Files**: `SearchUsersDialog`, `FriendRequest` model, `FriendRequestCard`, `FirestoreFriendsRepository`.

---

## 6. Translation History Filters & Search

**What**: Add a search bar and language-pair filter to the History screen.  
**Why**: History grows quickly; finding a specific entry currently requires scrolling through all 50–100 records.  
**How**: Add a `searchQuery: String` and `languageFilter: String?` to `HistoryUiState`. Filter the in-memory list client-side (free, no extra reads).  
**Files**: `HistoryDiscreteTab`, `HistoryViewModel`.

---

## 7. Word Bank / Learning Sheet Sharing with Non-Friends

**What**: Generate a shareable link (deep-link) to a word bank or learning sheet, accessible without being friends.  
**Why**: The current share-to-friend system is limited to existing friends only.  
**How**: Add `isPublic: Boolean` and `shareToken: String` to the word bank document. Store a `/public_shares/{token}` document pointing to the content. Handle deep-links in `AppNavigation`.  
**Files**: `WordBankRepository`, `AppNavigation`, new `PublicShareScreen`.

---

## 8. Quiz Progress Persistence Across Sessions

**What**: Save quiz answers mid-attempt so users can resume after closing the app.  
**Why**: Long quiz sessions (20+ questions) are lost if the app is killed.  
**How**: Periodically write the in-progress attempt to `quiz_attempts/{attemptId}` with `status: "in_progress"`. On `QuizTakingViewModel` init, check for an unfinished attempt and offer to resume.  
**Files**: `QuizTakingViewModel`, `FirestoreLearningRepository`, `QuizTakingScreen`.

---

## 9. Push Notification Deep-Links

**What**: Tapping an FCM notification for a new chat message should open the specific `ChatScreen`; tapping a friend-request notification should open the Friends screen.  
**Why**: Currently notifications are delivered but tapping them only opens the app to the last screen, not the relevant screen.  
**How**: Add a `data` payload to each Cloud Function notification with `screen` and `args`. Handle it in `FYPApplication`'s `onMessageReceived` by publishing an `IntentData` to a `Channel` that `AppNavigation` consumes on launch.  
**Files**: `FcmNotificationService`, `fyp-backend/functions/src/index.ts`, `AppNavigation`.

---

## 10. Empty State Illustrations

**What**: Replace the plain text empty states (friends list, history, word bank, inbox) with small illustrations or icons.  
**Why**: Empty states with illustrations feel polished and guide users toward the next action.  
**How**: Use the existing `EmptyStateView` composable (already used in some screens). Standardise it across all empty-list locations.  
**Files**: `FriendsScreen`, `HistoryDiscreteTab`, `WordBankScreen`, `SharedInboxScreen`, `BlockedUsersScreen`.

---

## 11. Settings — Notification Fine-Grained Controls for In-App Badges

**What**: The notification settings dialog (Friends screen) currently has FCM toggle and in-app badge toggles. Consider merging them into a dedicated **Settings > Notifications** sub-screen for discoverability.  
**Why**: Users can't easily find notification preferences; they're hidden inside the Friends screen top-bar.  
**How**: Add `AppScreen.NotificationSettings` route. Move the dialog content into a full screen. Link from Settings quick-links card.  
**Files**: `SettingsScreen`, new `NotificationSettingsScreen`, `AppNavigation`.

---

## 12. Coin Anti-Cheat — Server-Side Validation

**What**: Move coin-awarding logic from the client (`CoinAwardUseCase`) to a Firestore Cloud Function with server-side time-gap validation.  
**Why**: A client-side coin award can be replayed by re-running the network call. The Firestore rules do not currently validate coin amounts.  
**How**: Create a `awardCoins` Cloud Function that reads `last_awarded_quiz/{quizId}` server-side and only awards if the cooldown has passed. Remove client-side `CoinAwardUseCase`.  
**Files**: `fyp-backend/functions/src/index.ts`, `CoinAwardUseCase`, `FirestoreLearningRepository`.

---

## 13. Localisation — Chinese (Traditional/Simplified) UI

**What**: Add zh-TW and zh-CN as selectable **app UI** languages (not just translation target languages).  
**Why**: The app's primary audience speaks Cantonese / Mandarin; having the UI in English creates friction.  
**How**: Add `zh-TW` and `zh-CN` entries to `UiLanguages`. Provide Chinese `BaseUiTexts` lists in `UiTextScreens.kt`. The existing `AppLanguageState` system handles the rest.  
**Files**: `UiTextScreens.kt`, `AppLanguageState`, `AppNavigation` (language list).

---

## 14. Accessibility — Minimum Touch Target & Contrast

**What**: Several icon buttons (`Block`, small mic stop button) are `size(20.dp)` — below the Android minimum of 48dp touch target.  
**Why**: Users with motor impairments or large fingers will struggle to tap them accurately.  
**How**: Wrap icon content in `Modifier.minimumInteractiveComponentSize()` (Material3 guarantee) or pad to 48dp. Verify all text contrast ratios with the accessibility checker.  
**Files**: `FriendCard` (block icon), `ContinuousConversationScreen` (mic button), `WordBankDetailView` (action icons).
