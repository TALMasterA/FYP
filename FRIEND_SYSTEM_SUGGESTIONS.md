# Friend System — Suggestions & Review

Ordered by priority (highest impact first).
**✅ Implemented** = already done in this PR or a previous session.
**⏳ Pending** = not yet implemented (requires more complex changes or architectural decisions).

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