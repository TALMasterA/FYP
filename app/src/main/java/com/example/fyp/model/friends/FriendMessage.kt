package com.example.fyp.model.friends

import com.google.firebase.Timestamp

/**
 * Represents a chat message between two friends.
 */
data class FriendMessage(
    val messageId: String = "",
    val chatId: String = "",                     // Composite ID: smaller_uid + "_" + larger_uid
    val senderId: String = "",
    val receiverId: String = "",
    val content: String = "",
    val type: MessageType = MessageType.TEXT,
    val metadata: Map<String, Any> = emptyMap(), // For shared items metadata
    val isRead: Boolean = false,
    val createdAt: Timestamp = Timestamp.now()
)

/**
 * Type of message in chat.
 */
enum class MessageType {
    TEXT,                    // Regular text message
    SHARED_WORD,             // Shared word bank word
    SHARED_LEARNING_MATERIAL // Shared learning sheet/quiz
}

/**
 * Chat metadata stored in chats/{chatId}/metadata/info
 */
data class ChatMetadata(
    val chatId: String = "",
    val participants: List<String> = emptyList(),  // List of user IDs
    val participantNames: Map<String, String> = emptyMap(), // userId -> username
    val lastMessageContent: String = "",
    val lastMessageAt: Timestamp = Timestamp.now(),
    val unreadCount: Map<String, Any> = emptyMap()  // userId -> unread count (any Number)
) {
    fun getUnreadFor(userId: String): Int {
        return (unreadCount[userId] as? Number)?.toInt() ?: 0
    }
}
