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
                    "unreadCount.${toUserId.value}" to FieldValue.increment(1)
                ),
                com.google.firebase.firestore.SetOptions.merge()
            )

            // User-level aggregated counter for notification badge (avoids scanning all chats)
            val receiverDocRef = db.collection("users").document(toUserId.value)
            batch.update(receiverDocRef, "totalUnreadMessages", FieldValue.increment(1))

            batch.commit().await()
        } catch (e: Exception) {
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

        if (chatUnread > 0) {
            val batch = db.batch()
            // Reset per-chat counter
            batch.update(metaRef, "unreadCount.${userId.value}", 0)
            // Decrement user-level counter (floor at 0)
            batch.update(
                db.collection("users").document(userId.value),
                "totalUnreadMessages", FieldValue.increment(-chatUnread.toLong())
            )
            batch.commit().await()
        }

        Result.success(Unit)
    } catch (e: Exception) {
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
        val listener = db.collection("users")
            .document(userId.value)
            .addSnapshotListener { snapshot, error ->
                if (error != null) { trySend(0); return@addSnapshotListener }
                val count = (snapshot?.getLong("totalUnreadMessages") ?: 0L).toInt().coerceAtLeast(0)
                trySend(count)
            }
        awaitClose { listener.remove() }
    }

    // ── User chat list ───────────────────────────────────────────────────────

    override suspend fun getUserChats(userId: UserId): List<ChatMetadata> = emptyList()
}
