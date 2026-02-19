package com.example.fyp.data.friends

import com.example.fyp.model.UserId
import com.example.fyp.model.friends.ChatMetadata
import com.example.fyp.model.friends.FriendMessage
import com.example.fyp.model.friends.MessageType
import kotlinx.coroutines.flow.Flow

/**
 * Repository for managing chat messages between friends.
 */
interface ChatRepository {
    
    /**
     * Generate a chat ID from two user IDs.
     * Chat ID is deterministic: smaller_uid + "_" + larger_uid
     */
    fun generateChatId(userId1: UserId, userId2: UserId): String
    
    /**
     * Send a text message to a friend.
     */
    suspend fun sendTextMessage(
        fromUserId: UserId,
        toUserId: UserId,
        content: String
    ): Result<FriendMessage>

    /**
     * Send a text message using an explicit chatId.
     */
    suspend fun sendMessage(
        chatId: String,
        fromUserId: UserId,
        toUserId: UserId,
        content: String
    ): Result<Unit>

    /**
     * Send a shared item message (word or learning material).
     */
    suspend fun sendSharedItemMessage(
        fromUserId: UserId,
        toUserId: UserId,
        type: MessageType,
        metadata: Map<String, Any>
    ): Result<FriendMessage>
    
    /**
     * Mark a message as read.
     */
    suspend fun markMessageAsRead(chatId: String, messageId: String): Result<Unit>
    
    /**
     * Mark all messages in a chat as read for the current user.
     */
    suspend fun markAllMessagesAsRead(chatId: String, userId: UserId): Result<Unit>
    
    /**
     * Observe messages in a chat in real-time.
     * Returns messages ordered by creation time (newest last).
     * @param limit Maximum number of messages to load
     */
    fun observeMessages(chatId: String, limit: Long = 50): Flow<List<FriendMessage>>
    
    /**
     * Load older messages for pagination.
     * Returns messages older than the provided timestamp.
     * @param chatId The chat ID
     * @param beforeTimestamp Load messages created before this timestamp
     * @param limit Maximum number of messages to load
     */
    suspend fun loadOlderMessages(
        chatId: String,
        beforeTimestamp: com.google.firebase.Timestamp,
        limit: Long = 50
    ): List<FriendMessage>
    
    /**
     * Get chat metadata.
     */
    suspend fun getChatMetadata(chatId: String): ChatMetadata?
    
    /**
     * Observe chat metadata in real-time.
     */
    fun observeChatMetadata(chatId: String): Flow<ChatMetadata?>
    
    /**
     * Get unread message count for a user in a specific chat.
     */
    suspend fun getUnreadCount(chatId: String, userId: UserId): Int
    
    /**
     * Get total unread message count across all chats for a user.
     */
    suspend fun getTotalUnreadCount(userId: UserId): Int

    /**
     * Observe total unread message count across all chats for a user in real-time.
     * Emits a new value whenever messages are sent or read.
     */
    fun observeTotalUnreadCount(userId: UserId): Flow<Int>

    /**
     * Get list of chats for a user (sorted by last message time).
     */
    suspend fun getUserChats(userId: UserId): List<ChatMetadata>
}
