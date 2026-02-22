@file:Suppress("unused")

package com.example.fyp.data.friends

import com.example.fyp.core.NetworkRetry
import com.example.fyp.model.UserId
import com.example.fyp.model.friends.ChatMetadata
import com.example.fyp.model.friends.FriendMessage
import com.example.fyp.model.friends.MessageType
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirestoreChatRepository @Inject constructor(
    private val db: FirebaseFirestore
) : ChatRepository {

    override fun generateChatId(userId1: UserId, userId2: UserId): String {
        val ids = listOf(userId1.value, userId2.value).sorted()
        return "${ids[0]}_${ids[1]}"
    }

    // ── Send messages ────────────────────────────────────────────────────────

    override suspend fun sendMessage(
        chatId: String,
        fromUserId: UserId,
        toUserId: UserId,
        content: String
    ): Result<Unit> = sendTextMessage(fromUserId, toUserId, content).map { }

    /**
     * OPTIMIZED: Removed areFriends() Firestore read — the UI only presents
     * the send button for confirmed friends, so the check is redundant.
     * Net saving: 1 read per message sent.
     */
    override suspend fun sendTextMessage(
        fromUserId: UserId,
        toUserId: UserId,
        content: String
    ): Result<FriendMessage> {
        return try {
            require(content.isNotBlank()) { "Message content cannot be blank" }
            require(content.length <= 2000) { "Message content too long" }

            val chatId = generateChatId(fromUserId, toUserId)
            val messageRef = db.collection("chats")
                .document(chatId)
                .collection("messages")
                .document()

            val message = FriendMessage(
                messageId = messageRef.id,
                chatId = chatId,
                senderId = fromUserId.value,
                receiverId = toUserId.value,
                content = content,
                type = MessageType.TEXT,
                isRead = false,
                createdAt = Timestamp.now()
            )

            // Save message with retry for transient network failures
            NetworkRetry.withRetry(
                maxAttempts = 3,
                shouldRetry = NetworkRetry::isRetryableFirebaseException
            ) {
                messageRef.set(message).await()
            }

            // Update chat metadata
            updateChatMetadata(chatId, fromUserId, toUserId, content)

            Result.success(message)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * OPTIMIZED: Same — areFriends() check removed.
     */
    override suspend fun sendSharedItemMessage(
        fromUserId: UserId,
        toUserId: UserId,
        type: MessageType,
        metadata: Map<String, Any>
    ): Result<FriendMessage> {
        return try {
            val chatId = generateChatId(fromUserId, toUserId)
            val messageRef = db.collection("chats")
                .document(chatId)
                .collection("messages")
                .document()

            val content = when (type) {
                MessageType.SHARED_WORD -> "Shared a word"
                MessageType.SHARED_LEARNING_MATERIAL -> "Shared learning material"
                else -> "Shared an item"
            }

            val message = FriendMessage(
                messageId = messageRef.id,
                chatId = chatId,
                senderId = fromUserId.value,
                receiverId = toUserId.value,
                content = content,
                type = type,
                metadata = metadata,
                isRead = false,
                createdAt = Timestamp.now()
            )

            // Save message
            messageRef.set(message).await()

            // Update chat metadata
            updateChatMetadata(chatId, fromUserId, toUserId, content)

            Result.success(message)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ── Chat metadata ────────────────────────────────────────────────────────

    /**
     * OPTIMIZED: Single batch write that updates BOTH the per-chat metadata AND
     * the user-level totalUnreadMessages counter atomically, replacing two
     * separate writes from the previous implementation.
     */
    private suspend fun updateChatMetadata(
        chatId: String,
        fromUserId: UserId,
        toUserId: UserId,
        lastMessageContent: String
    ) {
        try {
            val now = Timestamp.now()
            val batch = db.batch()

            // Per-chat metadata (used by ChatScreen)
            // Use proper nested structure instead of dot notation for better security rules compatibility
            val metaRef = db.collection("chats")
                .document(chatId)
                .collection("metadata")
                .document("info")
            batch.set(
                metaRef,
                mapOf(
                    "chatId" to chatId,
                    "participants" to listOf(fromUserId.value, toUserId.value),
                    "lastMessageContent" to lastMessageContent,
                    "lastMessageAt" to now,
                    "unreadCount" to mapOf(
                        toUserId.value to FieldValue.increment(1)
                    )
                ),
                com.google.firebase.firestore.SetOptions.merge()
            )

            // User-level aggregated counter for notification badge (avoids scanning all chats)
            // Use proper nested structure instead of dot notation
            val receiverDocRef = db.collection("users").document(toUserId.value)
            batch.set(
                receiverDocRef,
                mapOf(
                    "totalUnreadMessages" to FieldValue.increment(1),
                    "unreadPerFriend" to mapOf(
                        fromUserId.value to FieldValue.increment(1)
                    )
                ),
                com.google.firebase.firestore.SetOptions.merge()
            )

            batch.commit().await()
            android.util.Log.d("ChatRepository", "Updated chat metadata: chatId=$chatId, receiver=${toUserId.value}, sender=${fromUserId.value}")
        } catch (e: Exception) {
            android.util.Log.e("ChatRepository", "Failed to update chat metadata", e)
            // Non-fatal: message is still saved; counter will re-sync on next markRead
        }
    }

    // ── Read status ──────────────────────────────────────────────────────────

    override suspend fun markMessageAsRead(chatId: String, messageId: String): Result<Unit> = try {
        db.collection("chats")
            .document(chatId)
            .collection("messages")
            .document(messageId)
            .update("isRead", true)
            .await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    /**
     * OPTIMIZED: No longer reads individual messages to mark them read.
     * Instead, reads the per-chat unread counter (1 read), resets it to 0 (1 write),
     * and decrements the user-level total counter by the same amount (1 write).
     * Previous cost: N+2 operations. New cost: 3 operations.
     */
    override suspend fun markAllMessagesAsRead(chatId: String, userId: UserId): Result<Unit> = try {
        val metaRef = db.collection("chats")
            .document(chatId)
            .collection("metadata")
            .document("info")

        // 1 read: get current per-chat unread count
        val metaDoc = metaRef.get().await()
        @Suppress("UNCHECKED_CAST")
        val unreadMap = metaDoc.get("unreadCount") as? Map<String, Any>
        val chatUnread = (unreadMap?.get(userId.value) as? Number)?.toInt() ?: 0

        android.util.Log.d("ChatRepository", "Marking messages as read: chatId=$chatId, userId=${userId.value}, unreadCount=$chatUnread")

        if (chatUnread > 0) {
            val batch = db.batch()
            // Reset per-chat counter using nested structure instead of dot notation
            batch.set(
                metaRef,
                mapOf("unreadCount" to mapOf(userId.value to 0)),
                com.google.firebase.firestore.SetOptions.merge()
            )
            // Decrement user-level counter (floor at 0) using set-merge for safety
            val userDocRef = db.collection("users").document(userId.value)
            batch.set(
                userDocRef,
                mapOf("totalUnreadMessages" to FieldValue.increment(-chatUnread.toLong())),
                com.google.firebase.firestore.SetOptions.merge()
            )
            // Determine the friend's ID from participants
            val participants = metaDoc.get("participants") as? List<*>
            val friendId = participants?.firstOrNull { it != userId.value }?.toString()
            if (friendId != null) {
                // Clear per-friend unread entry using nested structure instead of dot notation
                batch.set(
                    userDocRef,
                    mapOf("unreadPerFriend" to mapOf(friendId to 0)),
                    com.google.firebase.firestore.SetOptions.merge()
                )
                android.util.Log.d("ChatRepository", "Clearing unread for friend: $friendId")
            }
            batch.commit().await()
            android.util.Log.d("ChatRepository", "Messages marked as read successfully")
        }

        Result.success(Unit)
    } catch (e: Exception) {
        android.util.Log.e("ChatRepository", "Failed to mark messages as read", e)
        Result.failure(e)
    }

    // ── Observe messages ─────────────────────────────────────────────────────

    override fun observeMessages(chatId: String, limit: Long): Flow<List<FriendMessage>> = callbackFlow {
        val listener = db.collection("chats")
            .document(chatId)
            .collection("messages")
            .orderBy("createdAt", Query.Direction.ASCENDING)
            .limitToLast(limit)
            .addSnapshotListener { snapshot, error ->
                if (error != null) { close(error); return@addSnapshotListener }
                trySend(snapshot?.toObjects(FriendMessage::class.java) ?: emptyList())
            }
        awaitClose { listener.remove() }
    }

    override suspend fun loadOlderMessages(
        chatId: String,
        beforeTimestamp: Timestamp,
        limit: Long
    ): List<FriendMessage> = try {
        db.collection("chats")
            .document(chatId)
            .collection("messages")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .whereLessThan("createdAt", beforeTimestamp)
            .limit(limit)
            .get()
            .await()
            .toObjects(FriendMessage::class.java)
            .reversed()
    } catch (e: Exception) {
        emptyList()
    }

    // ── Chat metadata queries ────────────────────────────────────────────────

    override suspend fun getChatMetadata(chatId: String): ChatMetadata? = try {
        db.collection("chats")
            .document(chatId)
            .collection("metadata")
            .document("info")
            .get()
            .await()
            .toObject(ChatMetadata::class.java)
    } catch (e: Exception) {
        null
    }

    override fun observeChatMetadata(chatId: String): Flow<ChatMetadata?> = callbackFlow {
        val listener = db.collection("chats")
            .document(chatId)
            .collection("metadata")
            .document("info")
            .addSnapshotListener { snapshot, error ->
                if (error != null) { close(error); return@addSnapshotListener }
                trySend(snapshot?.toObject(ChatMetadata::class.java))
            }
        awaitClose { listener.remove() }
    }

    override suspend fun getUnreadCount(chatId: String, userId: UserId): Int = try {
        getChatMetadata(chatId)?.getUnreadFor(userId.value) ?: 0
    } catch (e: Exception) {
        0
    }

    // ── Global unread count ──────────────────────────────────────────────────

    /**
     * OPTIMIZED: Reads a single field on the user document instead of
     * scanning all chat metadata subcollections.
     */
    override suspend fun getTotalUnreadCount(userId: UserId): Int = try {
        val doc = db.collection("users").document(userId.value).get().await()
        (doc.getLong("totalUnreadMessages") ?: 0L).toInt().coerceAtLeast(0)
    } catch (e: Exception) {
        0
    }

    /**
     * Real-time listener on the single user document field.
     * Fires only when totalUnreadMessages changes — minimal bandwidth.
     */
    override fun observeTotalUnreadCount(userId: UserId): Flow<Int> = callbackFlow {
        android.util.Log.d("ChatRepository", "Setting up totalUnreadCount listener for user: ${userId.value}")
        val listener = db.collection("users")
            .document(userId.value)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    android.util.Log.e("ChatRepository", "Error in totalUnreadCount listener", error)
                    trySend(0)
                    return@addSnapshotListener
                }
                val exists = snapshot?.exists() ?: false
                val count = (snapshot?.getLong("totalUnreadMessages") ?: 0L).toInt().coerceAtLeast(0)
                android.util.Log.d("ChatRepository", "totalUnreadCount listener fired: exists=$exists, count=$count, userId=${userId.value}")
                trySend(count)
            }
        awaitClose {
            android.util.Log.d("ChatRepository", "Closing totalUnreadCount listener for user: ${userId.value}")
            listener.remove()
        }
    }

    /**
     * Observe per-friend unread counts from the single user document.
     * Returns map of friendId -> unread count. Much more efficient than
     * one listener per chat (medium priority #5).
     */
    @Suppress("UNCHECKED_CAST")
    override fun observeUnreadPerFriend(userId: UserId): Flow<Map<String, Int>> = callbackFlow {
        android.util.Log.d("ChatRepository", "Setting up unreadPerFriend listener for user: ${userId.value}")
        val listener = db.collection("users")
            .document(userId.value)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    android.util.Log.e("ChatRepository", "Error in unreadPerFriend listener", error)
                    trySend(emptyMap())
                    return@addSnapshotListener
                }
                val exists = snapshot?.exists() ?: false
                val raw = snapshot?.get("unreadPerFriend") as? Map<String, Any> ?: emptyMap()
                val mapped = raw.mapValues { (_, v) -> (v as? Number)?.toInt()?.coerceAtLeast(0) ?: 0 }
                    .filter { it.value > 0 }
                android.util.Log.d("ChatRepository", "unreadPerFriend listener fired: exists=$exists, mapped=$mapped, userId=${userId.value}")
                trySend(mapped)
            }
        awaitClose {
            android.util.Log.d("ChatRepository", "Closing unreadPerFriend listener for user: ${userId.value}")
            listener.remove()
        }
    }

    // ── User chat list ───────────────────────────────────────────────────────

    override suspend fun getUserChats(userId: UserId): List<ChatMetadata> = emptyList()

    // ── Delete chat conversation ─────────────────────────────────────────────

    /**
     * Delete entire chat conversation including all messages and metadata.
     * Used when unfriending. Uses batched deletes for efficiency.
     *
     * NOTE: Firestore batch writes have a limit of 500 operations per batch.
     * For large conversations, this may need to be called multiple times.
     */
    override suspend fun deleteChatConversation(chatId: String): Result<Unit> = try {
        // Delete all messages in batches (max 500 per batch)
        var deletedCount: Int
        do {
            deletedCount = 0
            val batch = db.batch()

            val messagesSnapshot = db.collection("chats")
                .document(chatId)
                .collection("messages")
                .limit(500)
                .get()
                .await()

            messagesSnapshot.documents.forEach { doc ->
                batch.delete(doc.reference)
                deletedCount++
            }

            if (deletedCount > 0) {
                batch.commit().await()
            }
        } while (deletedCount >= 500)

        // Delete chat metadata (stored at chats/{chatId}/metadata/info)
        try {
            db.collection("chats")
                .document(chatId)
                .collection("metadata")
                .document("info")
                .delete()
                .await()
        } catch (_: Exception) { /* best-effort */ }

        // Clean up per-friend unread counters on both user documents
        try {
            // chatId is "smallerUid_largerUid"
            val parts = chatId.split("_", limit = 2)
            if (parts.size == 2) {
                val uid1 = parts[0]
                val uid2 = parts[1]
                val batch = db.batch()
                val doc1 = db.collection("users").document(uid1)
                val doc2 = db.collection("users").document(uid2)
                batch.update(doc1, "unreadPerFriend.$uid2", FieldValue.delete())
                batch.update(doc2, "unreadPerFriend.$uid1", FieldValue.delete())
                batch.commit().await()
            }
        } catch (_: Exception) { /* best-effort */ }

        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }
}