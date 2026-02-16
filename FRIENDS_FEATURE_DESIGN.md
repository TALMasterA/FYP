# Friends Feature Design Document

## Overview

This document outlines the design and implementation plan for the Friends feature in the FYP Translation & Learning App. The feature allows users to connect with other learners, enabling social learning experiences.

## Table of Contents

1. [Feature Requirements](#feature-requirements)
2. [Data Models](#data-models)
3. [Firestore Schema](#firestore-schema)
4. [Search Implementation](#search-implementation)
5. [Friend Request Flow](#friend-request-flow)
6. [API Design](#api-design)
7. [UI/UX Design](#uiux-design)
8. [Security Rules](#security-rules)
9. [Implementation Phases](#implementation-phases)
10. [Future Enhancements](#future-enhancements)

---

## Feature Requirements

### Core Features

1. **Search for Friends**
   - Search by username (case-insensitive partial match)
   - Search by unique user ID (exact match)
   - Display search results with user profiles

2. **Friend Requests**
   - Send friend request to another user
   - Accept/reject incoming friend requests
   - Cancel outgoing friend requests

3. **Friend List**
   - View list of all friends
   - Remove friend from list
   - View friend's public profile

4. **Privacy Controls**
   - Option to make profile discoverable/hidden
   - Option to accept friend requests from anyone/nobody

---

## Data Models

### User Profile Extension

```kotlin
/**
 * Public user profile data that can be searched and shared with friends.
 */
data class UserProfile(
    val uid: String = "",
    val username: String = "",           // Unique, searchable username
    val displayName: String = "",        // Optional display name
    val avatarUrl: String = "",          // Profile picture URL
    val primaryLanguage: String = "",    // User's primary language code
    val learningLanguages: List<String> = emptyList(),  // Languages being learned
    val isDiscoverable: Boolean = true,  // Can be found via search
    val createdAt: Timestamp = Timestamp.now(),
    val lastActiveAt: Timestamp = Timestamp.now()
)

// Value class for type safety
@JvmInline
@Serializable
value class Username(val value: String) {
    init {
        require(value.length in 3..20) { "Username must be 3-20 characters" }
        require(value.matches(Regex("^[a-zA-Z0-9_]+$"))) {
            "Username can only contain letters, numbers, and underscores"
        }
    }
}
```

### Friend Relationship

```kotlin
/**
 * Represents a friend relationship between two users.
 * Each friendship is stored twice (once for each user) for efficient querying.
 */
data class FriendRelation(
    val friendId: String = "",           // The friend's user ID
    val friendUsername: String = "",     // Cached for display
    val friendDisplayName: String = "",  // Cached for display
    val friendAvatarUrl: String = "",    // Cached for display
    val addedAt: Timestamp = Timestamp.now()
)
```

### Friend Request

```kotlin
/**
 * Represents a pending friend request.
 */
data class FriendRequest(
    val requestId: String = "",
    val fromUserId: String = "",
    val fromUsername: String = "",
    val fromDisplayName: String = "",
    val fromAvatarUrl: String = "",
    val toUserId: String = "",
    val status: RequestStatus = RequestStatus.PENDING,
    val createdAt: Timestamp = Timestamp.now(),
    val updatedAt: Timestamp = Timestamp.now()
)

enum class RequestStatus {
    PENDING,
    ACCEPTED,
    REJECTED,
    CANCELLED
}
```

---

## Firestore Schema

### Collection Structure

```
/users/{userId}/
    profile/settings          # Existing user settings
    profile/public            # NEW: Public profile data
    friends/{friendId}        # NEW: Friend list
    history/{recordId}        # Existing history

/friend_requests/{requestId}  # NEW: Global friend requests collection
    - fromUserId
    - toUserId
    - status
    - timestamps...

/usernames/{username}         # NEW: Username registry (for uniqueness)
    - userId
    - createdAt

/user_search/{userId}         # NEW: Search index (for username search)
    - username_lowercase      # For case-insensitive search
    - displayName
    - isDiscoverable
```

### Indexes Required

```javascript
// firestore.indexes.json additions
{
  "indexes": [
    {
      "collectionGroup": "user_search",
      "queryScope": "COLLECTION",
      "fields": [
        { "fieldPath": "isDiscoverable", "order": "ASCENDING" },
        { "fieldPath": "username_lowercase", "order": "ASCENDING" }
      ]
    },
    {
      "collectionGroup": "friend_requests",
      "queryScope": "COLLECTION",
      "fields": [
        { "fieldPath": "toUserId", "order": "ASCENDING" },
        { "fieldPath": "status", "order": "ASCENDING" },
        { "fieldPath": "createdAt", "order": "DESCENDING" }
      ]
    }
  ]
}
```

---

## Search Implementation

### Search by Username

Username search uses a prefix-based approach for real-time search:

```kotlin
/**
 * Search users by username prefix (case-insensitive).
 * Returns up to [limit] matching users who are discoverable.
 */
suspend fun searchByUsername(query: String, limit: Long = 20): List<UserProfile> {
    val lowerQuery = query.lowercase()
    val endQuery = lowerQuery + '\uf8ff' // Unicode high character for range query
    
    return firestore.collection("user_search")
        .whereEqualTo("isDiscoverable", true)
        .whereGreaterThanOrEqualTo("username_lowercase", lowerQuery)
        .whereLessThanOrEqualTo("username_lowercase", endQuery)
        .limit(limit)
        .get()
        .await()
        .toObjects(UserProfile::class.java)
}
```

### Search by User ID

Exact match search for sharing user IDs:

```kotlin
/**
 * Find user by exact user ID.
 * Returns null if user not found or not discoverable.
 */
suspend fun findByUserId(userId: String): UserProfile? {
    val doc = firestore.collection("users")
        .document(userId)
        .collection("profile")
        .document("public")
        .get()
        .await()
    
    val profile = doc.toObject(UserProfile::class.java)
    return if (profile?.isDiscoverable == true) profile else null
}
```

### Search Optimization

1. **Debounce Input**: Wait 300ms after user stops typing
2. **Minimum Query Length**: Require at least 2 characters
3. **Cache Results**: Cache recent searches for 5 minutes
4. **Rate Limiting**: Max 10 searches per minute per user

---

## Friend Request Flow

### Sending a Request

```
User A                          Firestore                         User B
   |                               |                                 |
   |-- Send Friend Request ------->|                                 |
   |                               |-- Create /friend_requests/xxx   |
   |                               |                                 |
   |                               |-- Cloud Function Trigger ------>|
   |                               |   (optional: push notification) |
   |                               |                                 |
   |<-- Request Sent Confirmation--|                                 |
```

### Accepting a Request

```
User B                          Firestore                         User A
   |                               |                                 |
   |-- Accept Request ------------>|                                 |
   |                               |-- Update request.status=ACCEPTED|
   |                               |                                 |
   |                               |-- Add A to B's friends list     |
   |                               |-- Add B to A's friends list     |
   |                               |                                 |
   |<-- Success --------------------|                                 |
   |                               |                                 |
   |                               |-- Cloud Function --------------->|
   |                               |   (notify A: request accepted)  |
```

### Request States

```
PENDING ──┬── ACCEPTED (creates friendship)
          ├── REJECTED (deletes request)
          └── CANCELLED (by sender)
```

---

## API Design

### Repository Interface

```kotlin
interface FriendsRepository {
    // Profile Management
    suspend fun createPublicProfile(userId: UserId, profile: UserProfile)
    suspend fun updatePublicProfile(userId: UserId, updates: Map<String, Any>)
    suspend fun getPublicProfile(userId: UserId): UserProfile?
    
    // Username Management
    suspend fun setUsername(userId: UserId, username: Username): Result<Unit>
    suspend fun isUsernameAvailable(username: Username): Boolean
    
    // Search
    suspend fun searchByUsername(query: String, limit: Long = 20): List<UserProfile>
    suspend fun findByUserId(userId: UserId): UserProfile?
    
    // Friend Requests
    suspend fun sendFriendRequest(fromUserId: UserId, toUserId: UserId): Result<FriendRequest>
    suspend fun acceptFriendRequest(requestId: String): Result<Unit>
    suspend fun rejectFriendRequest(requestId: String): Result<Unit>
    suspend fun cancelFriendRequest(requestId: String): Result<Unit>
    fun observeIncomingRequests(userId: UserId): Flow<List<FriendRequest>>
    fun observeOutgoingRequests(userId: UserId): Flow<List<FriendRequest>>
    
    // Friends List
    fun observeFriends(userId: UserId): Flow<List<FriendRelation>>
    suspend fun removeFriend(userId: UserId, friendId: UserId): Result<Unit>
    suspend fun getFriendCount(userId: UserId): Int
}
```

### Use Cases

```kotlin
// Search
class SearchUsersUseCase @Inject constructor(private val repo: FriendsRepository)
class FindUserByIdUseCase @Inject constructor(private val repo: FriendsRepository)

// Friend Requests
class SendFriendRequestUseCase @Inject constructor(private val repo: FriendsRepository)
class AcceptFriendRequestUseCase @Inject constructor(private val repo: FriendsRepository)
class RejectFriendRequestUseCase @Inject constructor(private val repo: FriendsRepository)
class ObserveIncomingRequestsUseCase @Inject constructor(private val repo: FriendsRepository)

// Friends Management
class ObserveFriendsUseCase @Inject constructor(private val repo: FriendsRepository)
class RemoveFriendUseCase @Inject constructor(private val repo: FriendsRepository)
```

---

## UI/UX Design

### Navigation Structure

```
Settings Tab
    └── Friends
        ├── Friends List (default view)
        │   ├── List of friends with avatars
        │   ├── Click to view profile
        │   └── Swipe/long-press to remove
        │
        ├── Add Friends (FAB or top bar button)
        │   ├── Search bar (username or ID)
        │   ├── Search results list
        │   ├── QR Code scanner (optional)
        │   └── Share my ID button
        │
        └── Friend Requests (badge on icon)
            ├── Incoming requests tab
            │   ├── Accept/Reject buttons
            │   └── View profile
            └── Outgoing requests tab
                └── Cancel button
```

### Screen Wireframes

#### Friends List Screen
```
┌────────────────────────────────┐
│  Friends                    🔍 │
├────────────────────────────────┤
│                                │
│  ┌──────────────────────────┐ │
│  │ 🧑 John Doe              │ │
│  │    @johndoe • en-US      │ │
│  └──────────────────────────┘ │
│                                │
│  ┌──────────────────────────┐ │
│  │ 👩 Jane Smith            │ │
│  │    @janesmith • ja-JP    │ │
│  └──────────────────────────┘ │
│                                │
│              ...               │
│                                │
├────────────────────────────────┤
│              ➕                │  ← FAB to add friends
└────────────────────────────────┘
```

#### Add Friends Screen
```
┌────────────────────────────────┐
│ ← Add Friends                  │
├────────────────────────────────┤
│ ┌────────────────────────────┐│
│ │ 🔍 Search username or ID   ││
│ └────────────────────────────┘│
│                                │
│ Search Results                 │
│ ┌──────────────────────────┐  │
│ │ 🧑 Alex Brown     [+ Add] │  │
│ │    @alexb • zh-CN         │  │
│ └──────────────────────────┘  │
│                                │
│ ┌────────────────────────────┐│
│ │ 📤 Share My ID: abc123... ││
│ └────────────────────────────┘│
│                                │
│ ┌────────────────────────────┐│
│ │ 📱 Scan QR Code            ││
│ └────────────────────────────┘│
└────────────────────────────────┘
```

---

## Security Rules

### Firestore Rules

```javascript
// firestore.rules additions

rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    
    // Username registry - read for availability check, write only for owner
    match /usernames/{username} {
      allow read: if request.auth != null;
      allow create: if request.auth != null 
        && request.resource.data.userId == request.auth.uid;
      allow delete: if request.auth != null 
        && resource.data.userId == request.auth.uid;
    }
    
    // User search index
    match /user_search/{userId} {
      allow read: if request.auth != null;
      allow write: if request.auth != null && request.auth.uid == userId;
    }
    
    // Public profiles
    match /users/{userId}/profile/public {
      allow read: if request.auth != null;
      allow write: if request.auth != null && request.auth.uid == userId;
    }
    
    // Friends list
    match /users/{userId}/friends/{friendId} {
      allow read: if request.auth != null && request.auth.uid == userId;
      allow write: if request.auth != null && request.auth.uid == userId;
    }
    
    // Friend requests
    match /friend_requests/{requestId} {
      // Read if sender or receiver
      allow read: if request.auth != null && (
        resource.data.fromUserId == request.auth.uid ||
        resource.data.toUserId == request.auth.uid
      );
      
      // Create if authenticated and sender
      allow create: if request.auth != null 
        && request.resource.data.fromUserId == request.auth.uid
        && request.resource.data.status == "PENDING";
      
      // Update if receiver (accept/reject) or sender (cancel)
      allow update: if request.auth != null && (
        (resource.data.toUserId == request.auth.uid && 
         request.resource.data.status in ["ACCEPTED", "REJECTED"]) ||
        (resource.data.fromUserId == request.auth.uid && 
         request.resource.data.status == "CANCELLED")
      );
      
      // Delete not allowed - use status updates
      allow delete: if false;
    }
    
    // Chat messages
    match /chats/{chatId}/messages/{messageId} {
      // Helper function to check if user is participant
      function isParticipant() {
        let participants = get(/databases/$(database)/documents/chats/$(chatId)/metadata/info).data.participants;
        return request.auth.uid in participants;
      }
      
      // Read if participant in chat
      allow read: if request.auth != null && isParticipant();
      
      // Create if participant and sender matches auth.uid
      allow create: if request.auth != null 
        && isParticipant()
        && request.resource.data.senderId == request.auth.uid
        && request.resource.data.content.size() > 0
        && request.resource.data.content.size() <= 2000;
      
      // Update only allowed for marking as read
      allow update: if request.auth != null 
        && isParticipant()
        && request.resource.data.diff(resource.data).affectedKeys().hasOnly(['isRead']);
      
      allow delete: if false;
    }
    
    // Chat metadata
    match /chats/{chatId}/metadata/info {
      // Read if participant
      function isParticipant() {
        return request.auth.uid in resource.data.participants;
      }
      
      allow read: if request.auth != null && isParticipant();
      allow write: if request.auth != null && request.auth.uid in request.resource.data.participants;
    }
    
    // Shared items inbox
    match /users/{userId}/shared_inbox/{itemId} {
      // Read own inbox
      allow read: if request.auth != null && request.auth.uid == userId;
      
      // Create if sending to another user
      allow create: if request.auth != null 
        && request.resource.data.fromUserId == request.auth.uid
        && request.resource.data.toUserId == userId;
      
      // Update own inbox items (accept/dismiss)
      allow update: if request.auth != null 
        && request.auth.uid == userId
        && request.resource.data.status in ["ACCEPTED", "DISMISSED"];
      
      // Delete own inbox items
      allow delete: if request.auth != null && request.auth.uid == userId;
    }
  }
}
```

### Rate Limiting (Cloud Functions)

```typescript
// Cloud Function to rate limit friend requests
export const onFriendRequestCreate = functions.firestore
  .document('friend_requests/{requestId}')
  .onCreate(async (snap, context) => {
    const request = snap.data() as FriendRequest;
    
    // Check rate limit (max 20 requests per hour)
    const recentRequests = await admin.firestore()
      .collection('friend_requests')
      .where('fromUserId', '==', request.fromUserId)
      .where('createdAt', '>', Timestamp.fromDate(new Date(Date.now() - 3600000)))
      .count()
      .get();
    
    if (recentRequests.data().count > 20) {
      await snap.ref.delete();
      throw new functions.https.HttpsError('resource-exhausted', 
        'Too many friend requests. Please try again later.');
    }
    
    // Send notification to receiver (optional)
    // await sendPushNotification(request.toUserId, ...);
  });
```

---

## Implementation Phases

### Phase 1: Foundation (Week 1)
- [ ] Add `Username` value class to `ValueTypes.kt`
- [ ] Create `UserProfile` extension and `FriendRelation` data models
- [ ] Create `FriendRequest` data model
- [ ] Create `FriendMessage` and `SharedItem` data models
- [ ] Implement username registry and validation
- [ ] Update Firestore security rules

### Phase 2: Repository & Use Cases - Friends (Week 2)
- [ ] Create `FriendsRepository` interface
- [ ] Implement `FirestoreFriendsRepository`
- [ ] Create use cases for search, requests, and friend management
- [ ] Add Cloud Functions for request handling (optional)

### Phase 3: Repository & Use Cases - Chat (Week 3)
- [ ] Create `ChatRepository` interface
- [ ] Implement `FirestoreChatRepository`
- [ ] Create use cases for messaging
- [ ] Add real-time message listeners

### Phase 4: Repository & Use Cases - Sharing (Week 3)
- [ ] Create `SharingRepository` interface
- [ ] Implement `FirestoreSharingRepository`
- [ ] Create use cases for sharing and receiving items
- [ ] Integrate with word bank for adding shared words

### Phase 5: UI - Friends List & Management (Week 4)
- [ ] Create `FriendsViewModel`
- [ ] Create `FriendsScreen` composable
- [ ] Create `AddFriendsScreen` composable
- [ ] Create `FriendRequestsScreen` composable
- [ ] Add navigation from Settings
- [ ] Add "Share my ID" functionality

### Phase 6: UI - Chat Feature (Week 5)
- [ ] Create `ChatViewModel`
- [ ] Create `ChatScreen` composable
- [ ] Create message list UI
- [ ] Create message input UI
- [ ] Implement real-time message updates
- [ ] Add unread message badge

### Phase 7: UI - Sharing Feature (Week 6)
- [ ] Add share buttons to word bank screen
- [ ] Add share buttons to learning materials screen
- [ ] Create `SharedInboxScreen` composable
- [ ] Implement add-to-wordbank functionality
- [ ] Add notification badge for shared items

### Phase 8: UI Text & Polish (Week 7)
- [ ] Add all UI text keys to `UiTextScreens.kt`
- [ ] Add loading states and error handling
- [ ] Add empty states for all lists
- [ ] Implement offline support
- [ ] Performance optimization

### Phase 9: Testing & Security (Week 8)
- [ ] Write unit tests for repositories
- [ ] Write unit tests for use cases
- [ ] Write tests for ViewModels
- [ ] Security review with codeql_checker
- [ ] Integration testing

---

## Chat Feature (Phase 7)

### Chat Data Model

```kotlin
/**
 * Represents a chat message between two friends.
 */
data class FriendMessage(
    val messageId: String = "",
    val chatId: String = "",              // Composite ID: smaller_uid + "_" + larger_uid
    val senderId: String = "",
    val receiverId: String = "",
    val content: String = "",
    val type: MessageType = MessageType.TEXT,
    val metadata: Map<String, Any> = emptyMap(),  // For shared items
    val isRead: Boolean = false,
    val createdAt: Timestamp = Timestamp.now()
)

enum class MessageType {
    TEXT,                    // Regular text message
    SHARED_WORD,             // Shared word bank word
    SHARED_LEARNING_MATERIAL // Shared learning sheet/quiz
}
```

### Chat Collection Structure

```
/chats/{chatId}/
    metadata/info           # Chat metadata (participants, lastMessage, etc.)
    messages/{messageId}    # Individual messages
```

### Chat Features

1. **Real-time Messaging**
   - Send and receive text messages
   - Real-time updates using Firestore listeners
   - Message read status

2. **Unread Message Badge**
   - Display unread message count on chat icon
   - Clear badge when opening chat

3. **Message History**
   - Load last 50 messages initially
   - Pagination for older messages
   - Timestamps with date separators

---

## Sharing Feature (Phase 8)

### Shared Item Data Model

```kotlin
/**
 * Represents an item shared between friends.
 */
data class SharedItem(
    val itemId: String = "",
    val fromUserId: String = "",
    val fromUsername: String = "",
    val toUserId: String = "",
    val type: SharedItemType = SharedItemType.WORD,
    val content: Map<String, Any> = emptyMap(),
    val status: SharedItemStatus = SharedItemStatus.PENDING,
    val createdAt: Timestamp = Timestamp.now()
)

enum class SharedItemType {
    WORD,                    // Word bank word
    LEARNING_SHEET,          // Learning sheet
    QUIZ                     // Quiz
}

enum class SharedItemStatus {
    PENDING,                 // Not yet acted upon
    ACCEPTED,                // Added to user's collection
    DISMISSED                // Dismissed/ignored
}
```

### Sharing Collection Structure

```
/users/{userId}/
    shared_inbox/{itemId}   # Received shared items
```

### Sharing Features

1. **Share Word Bank Words**
   - Select words from word bank to share
   - Send to specific friend via message
   - Friend can add to their word bank with one click

2. **Share Learning Materials**
   - Share learning sheets with friends
   - Share quiz references
   - Friend can view and add to their learning collection

3. **Received Items Inbox**
   - View all shared items received
   - Accept (add to own collection) or dismiss
   - Notification badge for new shared items

4. **Word Bank Integration**
   - When accepting a shared word, add to user's word bank under same language
   - Maintain original translation and context
   - Mark as "received from friend" in metadata

---

## Future Enhancements

### Phase 2 Features (Post-MVP)

1. **QR Code Sharing**
   - Generate QR code with user ID
   - Scan QR code to add friend

2. **Friend Activity Feed**
   - See when friends complete quizzes
   - Celebrate friend achievements

3. **Study Together**
   - Challenge friends to quizzes
   - Compare learning streaks

4. **Friend Recommendations**
   - Suggest friends learning same languages
   - "People you may know" based on mutual friends

5. **Privacy Enhancements**
   - Block users
   - Report inappropriate profiles
   - Granular visibility settings

6. **Group Features**
   - Group chats (3+ friends)
   - Group study sessions
   - Collaborative word banks

---

## Technical Considerations

### Performance

1. **Denormalization**: Store friend username/avatar in both directions to avoid joins
2. **Caching**: Cache friend list locally for offline access
3. **Pagination**: Limit friends list to 50 per page for large friend lists
4. **Debounced Search**: Wait 300ms after typing before querying

### Data Consistency

1. **Transactions**: Use Firestore transactions when accepting friend requests to ensure both friend lists are updated atomically
2. **Cloud Functions**: Consider using Cloud Functions for complex operations like cleaning up rejected requests

### Scalability

1. **Sharding**: If user search becomes slow, consider sharding by first letter
2. **Composite Indexes**: Create appropriate indexes for common query patterns
3. **Read Optimization**: Use `friend_count` field instead of counting documents

---

**Document Version**: 2.0  
**Created**: 2026-02-16  
**Updated**: 2026-02-16  
**Status**: Design Phase - Enhanced with Chat & Sharing Features  
**Estimated Total Effort**: 8 weeks

