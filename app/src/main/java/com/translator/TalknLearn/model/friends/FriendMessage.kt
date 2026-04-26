package com.translator.TalknLearn.model.friends

import androidx.compose.runtime.Immutable
import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName

/**
 * Represents a chat message between two friends.
 *
 * FIX 6.5: Marked @Immutable so Compose can skip recomposition when the
 * reference hasn't changed, reducing unnecessary UI updates in chat LazyColumn.
 */
@Immutable
data class FriendMessage(
    val messageId: String = "",
    val chatId: String = "",                     // Composite ID: smaller_uid + "_" + larger_uid
    val senderId: String = "",
    val senderUsername: String = "",
    val receiverId: String = "",
    val content: String = "",
    val type: MessageType = MessageType.TEXT,
    /**
     * Legacy field retained for backward-compatibility with existing
     * Firestore chat documents. Not read or written by current code.
     */
    val metadata: Map<String, Any> = emptyMap(),
    @field:PropertyName("isRead")
    @get:PropertyName("isRead")
    val isRead: Boolean = false,
    val createdAt: Timestamp = Timestamp.now()
)

/**
 * Type of message in chat.
 *
 * Only TEXT is produced by current call sites. The enum is retained
 * so the stored ``type`` field and backward compatibility with existing
 * Firestore documents are preserved.
 */
enum class MessageType {
    TEXT
}

/**
 * Chat metadata stored in chats/{chatId}/metadata/info
 */
data class ChatMetadata(
    val chatId: String = "",
    val participants: List<String> = emptyList(),  // List of user IDs
    val lastMessageContent: String = "",
    val lastMessageAt: Timestamp = Timestamp.now(),
    val unreadCount: Map<String, Any> = emptyMap()  // userId -> unread count (any Number)
) {
    fun getUnreadFor(userId: String): Int {
        return (unreadCount[userId] as? Number)?.toInt() ?: 0
    }
}
