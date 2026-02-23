# Friend System — Suggestions & Review

Ordered by priority (highest impact first).
**✅ Implemented** = already done in this PR or a previous session.
**⏳ Pending** = not yet implemented (requires more complex changes or architectural decisions).

---

## 1. Propagate username changes to friends' cached FriendRelation documents ✅ Implemented

**Problem:** `FriendRelation` stores a cached copy of the friend's username at the time the friendship was created. When a user renames themselves in Profile → Settings, the cached `friendUsername` in every friend's Friends list is never updated, so all friends still see the old name.

**Fix applied:** `FriendsRepository.propagateUsernameChange(userId, newUsername)` — after a successful username update, the app batch-updates `users/{friendId}/friends/{userId}.friendUsername` for every friend in the current user's friend list. Firestore rules already allow this via the `allow write: if request.auth.uid == friendId` rule.

**Files changed:** `FriendsRepository.kt` (interface), `FirestoreFriendsRepository.kt` (implementation), `ProfileViewModel.kt` (caller).

---

## 2. Require a username before sending friend requests ✅ Implemented

**Problem:** A user with no username could send a friend request; the recipient would see a blank sender name on the incoming request card.

**Fix applied:** `FriendsViewModel.requireUsernameForAddFriends()` gates the search dialog. `sendFriendRequest()` has a second guard. Both show a clear error message directing the user to set a username first.

---

## 3. Show specific relationship status in search results ✅ Implemented

**Problem:** The search dialog showed a disabled "Add" button with generic "Already connected or pending" text — users could not tell whether they had already sent a request, received one, or were already friends.

**Fix applied:** `RequestStatus` enum (`NONE` / `ALREADY_FRIENDS` / `REQUEST_SENT` / `REQUEST_RECEIVED`) with coloured labels in `SearchResultCard`. The Add button is hidden entirely when no connection is possible.

---

## 4. User-friendly error messages in friend actions ✅ Implemented

**Problem:** Error messages from Firestore (e.g., "Friend request already sent", "Not authorized") were shown verbatim — often confusing or unhelpful to end users.

**Fix applied:** `FriendsViewModel` now maps raw error strings to plain-English explanations with context:
- "You already have a pending request to this user. Please wait for their reply."
- "This request no longer exists — it may have been cancelled."
- "Could not find the user's profile. They may have deleted their account."
- etc.

---

## 5. Show friend's profile from the Chat screen ✅ Implemented

**Problem:** Users had no way to see information about the person they were chatting with from inside the chat screen.

**Fix applied:** `ChatViewModel` now loads the friend's public profile (`users/{friendId}/profile/public`) on login. An ⓘ icon in the chat top bar opens a dialog showing the friend's username, primary language, and learning languages.

---

## 6. Display username only (removed display-name redundancy) ✅ Implemented

**Problem:** `FriendCard` showed `friendDisplayName` as a subtitle even though it could be blank or duplicate the username, making some cards look broken.

**Fix applied:** Display name subtitle removed from `FriendCard`. Only `friendUsername` is shown everywhere in the friends UI.

---

## 7. Propagate username change to pending friend requests ✅ Implemented

**Problem:** `FriendRequest` stores `fromUsername` / `toUsername` at the time the request is created. If either party renames before the request is accepted/rejected, the request card still shows the old name.

**Fix applied:** `propagateUsernameChange` now also queries `friend_requests` where `fromUserId == userId` and `status == PENDING`, and updates `fromUsername` in a batch write. Firestore rule updated to allow the sender to update `fromUsername` on their own PENDING requests:
```
(resource.data.fromUserId == request.auth.uid &&
 resource.data.status == "PENDING" &&
 request.resource.data.diff(resource.data).affectedKeys().hasOnly(["fromUsername"]))
```

---

## 8. Real-time username refresh without app restart ✅ Implemented

**Problem:** `SharedFriendsDataSource` caches usernames in a `Map<String, String>`. If a friend renames after the cache is warmed, the in-memory cache becomes stale.

**Fix applied:** This now works end-to-end automatically. When a friend renames:
1. `propagateUsernameChange` writes the new `friendUsername` to `users/{myId}/friends/{friendId}`.
2. `SharedFriendsDataSource.observeFriends` real-time listener fires (Firestore snapshot listener).
3. The `_friends.value` is updated with the new `FriendRelation` — the Friends screen re-renders immediately.
4. The `usernameCache` map is also updated on every emission: `list.forEach { rel -> usernameCache[rel.friendId] = rel.friendUsername }`.

No app restart needed.

---

## 9. Notification when a friend request is accepted ⏳ Pending

**Problem:** The sender of a friend request gets no immediate in-app notification when their request is accepted. They only discover it when they open the Friends screen.

**Suggested fix:** Use Firebase Cloud Messaging (FCM) to push a notification to the sender's device when their request is accepted. This requires a Cloud Functions backend trigger on the `friend_requests` document `status` field changing to `ACCEPTED`.

---

## 10. Block / report user ⏳ Pending

**Problem:** There is no way to block a user who is sending unwanted friend requests or messages.

**Suggested fix:** Add a `blocked_users` subcollection under each user's document. When user A blocks user B: (1) B is removed from A's friends list if present, (2) B is added to `users/{A}/blocked_users/{B}`, (3) Firestore rules on `friend_requests` and `chats` should deny creation if the recipient has blocked the sender. This requires rule changes and a new use case.

---

## 11. Friend request expiry / TTL ⏳ Pending

**Problem:** Pending `friend_requests` documents accumulate indefinitely. A user who sent a request and was never answered will have their request document sitting in Firestore forever, consuming storage and appearing in all future queries.

**Suggested fix:** Add a `expiresAt` timestamp field to `FriendRequest` (e.g., 30 days from creation). A Cloud Functions scheduled job (or the next time the sender opens the app) should auto-cancel expired requests. Alternatively, display an "expires in N days" label in `OutgoingRequestCard`.

---

## 12. Mutual friend count / discovery ⏳ Pending

**Problem:** When viewing a search result, users have no social signal to help them decide whether to add a stranger.

**Suggested fix:** Add a "N mutual friends" hint to `SearchResultCard`. Requires a Cloud Function or client-side intersection of the two users' friends lists — expensive without denormalization. Consider storing a `friendIds` array on each profile document (max 500 entries per Firestore array) to enable client-side intersection via `array-contains-any`.

---

## 13. Chat message delivery / read receipts ⏳ Pending

**Problem:** The sender has no way to know if their message was read — only `isRead` is tracked but not surfaced in the chat bubble.

**Suggested fix:** Show a checkmark indicator (single = delivered, double = read) on each `MessageBubble` based on `isRead`. The `isRead` field is already written by `markMessagesAsReadUseCase` when the recipient opens the chat.

---

## 14. Last-message preview in FriendCard ⏳ Pending

**Problem:** The Friends list shows all friends uniformly with no indication of recent activity, making it hard to prioritise conversations.

**Suggested fix:** Store `lastMessage` and `lastMessageAt` in `chats/{chatId}/metadata/info`. Surface `lastMessage` text (truncated to ~60 chars) and relative time in `FriendCard` below the username. Sort friends by `lastMessageAt` descending so active conversations bubble up.

---

## 15. Rate-limit friend request sending ✅ Implemented

**Problem:** A user can send friend requests to many users in a short time, potentially spam-adding strangers.

**Fix applied:** Client-side rate limit: `FriendsViewModel.sendFriendRequest()` checks `outgoingRequests.size >= MAX_PENDING_REQUESTS` (20) before sending. If the limit is reached, a clear error message is shown. The search dialog already hides the Add button for existing connections, preventing duplicate requests. The constant `MAX_PENDING_REQUESTS = 20` is defined at file level for easy adjustment.

> **Note:** A malicious user could bypass client-side checks by using the Firestore API directly. For full backend enforcement, a Cloud Functions callable that validates request counts server-side would be needed (still pending).
