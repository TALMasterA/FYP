@file:Suppress("unused")

package com.example.fyp.data.friends

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
    private val db: FirebaseFirestore,
    private val friendsRepository: FriendsRepository
) : ChatRepository {

    override fun generateChatId(userId1: UserId, userId2: UserId): String {
        val ids = listOf(userId1.value, userId2.value).sorted()
        return "${ids[0]}_${ids[1]}"
    }

    override suspend fun sendTextMessage(
        fromUserId: UserId,
        toUserId: UserId,
        content: String
    ): Result<FriendMessage> {
        return try {
            // Validate content
            require(content.isNotBlank()) { "Message content cannot be blank" }
            require(content.length <= 2000) { "Message content too long" }

            // Verify friendship
            if (!friendsRepository.areFriends(fromUserId, toUserId)) {
                return Result.failure(IllegalStateException("Users are not friends"))
            }

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

            // Save message
            messageRef.set(message).await()

            // Update chat metadata
            updateChatMetadata(chatId, fromUserId, toUserId, content)

            Result.success(message)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun sendSharedItemMessage(
        fromUserId: UserId,
        toUserId: UserId,
        type: MessageType,
        metadata: Map<String, Any>
    ): Result<FriendMessage> {
        return try {
            // Verify friendship
            if (!friendsRepository.areFriends(fromUserId, toUserId)) {
                return Result.failure(IllegalStateException("Users are not friends"))
            }

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

    private suspend fun updateChatMetadata(
        chatId: String,
        fromUserId: UserId,
        toUserId: UserId,
        lastMessageContent: String
    ) {
        try {
            val metadataRef = db.collection("chats")
                .document(chatId)
                .collection("metadata")
                .document("info")

            val now = Timestamp.now()
            val updates = mapOf(
                "chatId" to chatId,
                "participants" to listOf(fromUserId.value, toUserId.value),
                "lastMessageContent" to lastMessageContent,
                "lastMessageAt" to now,
                "unreadCount.${toUserId.value}" to FieldValue.increment(1)
            )

            metadataRef.set(updates, com.google.firebase.firestore.SetOptions.merge()).await()
        } catch (e: Exception) {
            // Log but don't fail the message send
        }
    }

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

    override suspend fun markAllMessagesAsRead(chatId: String, userId: UserId): Result<Unit> = try {
        // Get unread messages
        val unreadMessages = db.collection("chats")
            .document(chatId)
            .collection("messages")
            .whereEqualTo("receiverId", userId.value)
            .whereEqualTo("isRead", false)
            .get()
            .await()

        // Batch update
        val batch = db.batch()
        unreadMessages.documents.forEach { doc ->
            batch.update(doc.reference, "isRead", true)
        }
        batch.commit().await()

        // Reset unread count in metadata
        db.collection("chats")
            .document(chatId)
            .collection("metadata")
            .document("info")
            .update("unreadCount.${userId.value}", 0)
            .await()

        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override fun observeMessages(chatId: String, limit: Long): Flow<List<FriendMessage>> = callbackFlow {
        val listener = db.collection("chats")
            .document(chatId)
            .collection("messages")
            .orderBy("createdAt", Query.Direction.ASCENDING)
            .limitToLast(limit)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val messages = snapshot?.toObjects(FriendMessage::class.java) ?: emptyList()
                trySend(messages)
            }
        awaitClose { listener.remove() }
    }

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
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val metadata = snapshot?.toObject(ChatMetadata::class.java)
                trySend(metadata)
            }
        awaitClose { listener.remove() }
    }

    override suspend fun getUnreadCount(chatId: String, userId: UserId): Int = try {
        val metadata = getChatMetadata(chatId)
        metadata?.unreadCount?.get(userId.value) ?: 0
    } catch (e: Exception) {
        0
    }

    override suspend fun getTotalUnreadCount(userId: UserId): Int {
        // TODO: Implement proper unread count tracking
        // This requires maintaining a user-level aggregated unread count
        // For now, return 0. Future implementation should:
        // 1. Add a user-level document tracking total unread messages
        // 2. Update this count when messages are sent/read
        // 3. Use Cloud Functions to maintain consistency
        return 0
    }

    override suspend fun getUserChats(userId: UserId): List<ChatMetadata> {
        // TODO: Implement user chats list
        // This requires maintaining a user-level chats collection
        // For now, return empty list. Future implementation should:
        // 1. Create /users/{userId}/chats collection
        // 2. Add chat references when first message is sent
        // 3. Sort by lastMessageAt
        return emptyList()
    }
}
