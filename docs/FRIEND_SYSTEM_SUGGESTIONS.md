# Friend System Improvement Suggestions

Ordered by estimated user impact (highest impact first).

---

## 1. Real-time Friend Presence / Online Status

**What**: Show a green dot on friend cards when a friend is currently active in the app.  
**Why**: This is the most-requested social feature; it tells users when to start a conversation.  
**How**: Add a `lastSeen: Timestamp` field to the user document, updated every 60 seconds while the app is in the foreground (`AppViewModel` already has a keep-alive pattern). Display "Online" / "X min ago" below the username in `FriendCard`.  
**Files**: `FriendsRepository`, `FirestoreFriendsRepository`, `FriendCard`, `AppViewModel`, Firestore rules.

---

## 2. Read Receipts in Chat (Delivered / Read ticks)

**What**: Show ✓ (sent) and ✓✓ (read) indicators on chat bubbles, similar to WhatsApp.  
**Why**: Reduces anxiety about whether messages were received; improves chat UX significantly.  
**How**: `ChatMessage` already has a `timestamp`; add `readAt: Timestamp?`. Update it when the recipient opens the chat. Display a tick icon on the sender's bubble.  
**Files**: `ChatMessage` model, `FirestoreChatRepository`, `ChatScreen`.

---

## 3. Friend Request Preview — Include a Short Note

**What**: Allow the sender to add a short note (≤80 chars) when sending a friend request.  
**Why**: "Who are you?" — without context, many users reject requests from strangers.  
**How**: Add a `note: String` field to `FriendRequest`. Show an optional `TextField` in `SearchUsersDialog` after the user taps "Add". Display the note in `FriendRequestCard`.  
**Files**: `FriendRequest` model, `SearchUsersDialog`, `FriendRequestCard`, `FirestoreFriendsRepository`.

---

## 4. Mute / Silence a Friend's Notifications

**What**: Let users mute FCM push notifications from a specific friend without unfriending or blocking.  
**Why**: Some friends are chatty; users want to control noise without losing the relationship.  
**How**: Store `mutedFriendIds: Set<String>` in `UserSettings`. In `sendChatNotification` Cloud Function, check the muted list before sending.  
**Files**: `UserSettings`, `FirestoreUserSettingsRepository`, `FriendCard` (mute icon), `fyp-backend/functions/src/index.ts`.

---

## 5. Friend Profile View Screen

**What**: Tapping a friend's username shows a mini-profile: display name, avatar, mutual friends count, shared translations.  
**Why**: Users have no way to learn anything about a friend other than their username.  
**How**: Navigate from `FriendCard` to a new `FriendProfileScreen`. Load `PublicUserProfile` via `getPublicProfile()`.  
**Files**: New `FriendProfileScreen.kt`, `AppNavigation`, `FriendsScreen`.

---

## 6. Shared Translation Quick-Share from Speech Screen

**What**: After a translation in Quick Translate, show a "Share with Friend" button that prefills a chat message.  
**Why**: The friend system and translation feature are currently disconnected; linking them adds clear value.  
**How**: Add an `onShareToFriend(text)` callback from `SpeechRecognitionScreen`. Open a friend picker dialog; on selection, call `chatRepository.sendMessage`.  
**Files**: `SpeechRecognitionScreen`, `SpeechViewModel`, new `FriendPickerDialog` composable.

---

## 7. Block from Search Results + Incoming Requests

**What**: Allow blocking directly from incoming `FriendRequestCard` and from `SearchResultCard`.  
**Why**: Currently users can only block existing friends. Blocking from an unwanted request is a common need.  
**How**: Add an overflow menu (⋮) or a `Block & Reject` button to `FriendRequestCard`. Similarly add to `SearchResultCard`.  
**Files**: `FriendsScreen.kt` (`FriendRequestCard`, `SearchResultCard`), `FriendsViewModel`.

---

## 8. Friend Count Badge on Profile

**What**: Show the total number of friends on `MyProfileScreen` and on the public profile.  
**Why**: Social proof; mirrors most social apps and encourages users to add more friends.  
**How**: `getFriendCount()` already exists in `FriendsRepository`. Display as a chip on `MyProfileScreen`.  
**Files**: `MyProfileScreen`, `MyProfileViewModel`.

---

## 9. Paginate Friends List for Large Contacts

**What**: Replace the single `observeFriends` query with a paginated `LazyColumn` load (20 at a time).  
**Why**: Users with 100+ friends will face slow initial load and high Firestore bandwidth.  
**How**: Use Firestore `limit()` + `startAfter()` cursor queries. Show a "Load more" button or infinite scroll trigger.  
**Files**: `FirestoreFriendsRepository`, `SharedFriendsDataSource`, `FriendsScreen`.

---

## 10. Export / Backup Chat History

**What**: Allow users to export a conversation as a `.txt` or `.pdf` file.  
**Why**: Users studying languages want to keep a record of translated chats for review.  
**How**: Add an "Export" item to the chat overflow menu. Iterate `observeMessages` and write to a file using `FileOutputStream`. Share via Android `ShareCompat`.  
**Files**: `ChatScreen`, `ChatViewModel`.

---

## 11. Search Within Chat

**What**: A search icon in `ChatScreen` that filters messages by keyword, highlighted in yellow.  
**Why**: Long chats are hard to navigate; keyword search is a baseline feature in modern messengers.  
**How**: Filter the in-memory messages list in `ChatViewModel` by a search query. Highlight matching substrings in the bubble `Text`.  
**Files**: `ChatScreen`, `ChatViewModel`.

---

## 12. Firestore Security: Server-Side Block Enforcement

**What**: Add Firestore security rules that reject `friend_requests` where the recipient has blocked the sender.  
**Why**: Currently blocking is enforced client-side only; a malicious client could bypass it and send requests.  
**How**: In `firestore.rules`, add a `get()` call checking `users/$(toUserId)/blocked_users/$(request.auth.uid)` before allowing `friend_requests` writes.  
**Files**: `fyp-backend/firestore.rules`.
