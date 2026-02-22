package com.example.fyp.model.friends

import com.google.firebase.Timestamp
import org.junit.Test
import org.junit.Assert.*

/**
 * Unit tests for ChatMetadata and FriendMessage models.
 */
class ChatMetadataTest {

    // --- ChatMetadata Tests ---

    @Test
    fun `getUnreadFor returns count for existing user`() {
        val meta = ChatMetadata(
            chatId = "chat1",
            unreadCount = mapOf("user1" to 5L, "user2" to 0L)
        )
        assertEquals(5, meta.getUnreadFor("user1"))
        assertEquals(0, meta.getUnreadFor("user2"))
    }

    @Test
    fun `getUnreadFor returns 0 for missing user`() {
        val meta = ChatMetadata(chatId = "chat1", unreadCount = emptyMap())
        assertEquals(0, meta.getUnreadFor("unknown_user"))
    }

    @Test
    fun `getUnreadFor handles numeric types from Firestore`() {
        val meta = ChatMetadata(
            chatId = "chat1",
            unreadCount = mapOf(
                "userLong" to 3L,
                "userInt" to 2,
                "userDouble" to 1.0
            )
        )
        assertEquals(3, meta.getUnreadFor("userLong"))
        assertEquals(2, meta.getUnreadFor("userInt"))
        assertEquals(1, meta.getUnreadFor("userDouble"))
    }

    @Test
    fun `ChatMetadata defaults are empty`() {
        val meta = ChatMetadata()
        assertEquals("", meta.chatId)
        assertTrue(meta.participants.isEmpty())
        assertEquals("", meta.lastMessageContent)
        assertTrue(meta.unreadCount.isEmpty())
    }

    @Test
    fun `ChatMetadata stores participants correctly`() {
        val meta = ChatMetadata(
            chatId = "user1_user2",
            participants = listOf("user1", "user2")
        )
        assertEquals(2, meta.participants.size)
        assertTrue(meta.participants.contains("user1"))
        assertTrue(meta.participants.contains("user2"))
    }

    // --- FriendMessage Tests ---

    @Test
    fun `FriendMessage default type is TEXT`() {
        val msg = FriendMessage()
        assertEquals(MessageType.TEXT, msg.type)
    }

    @Test
    fun `FriendMessage defaults isRead to false`() {
        val msg = FriendMessage(
            messageId = "msg1",
            senderId = "user1",
            receiverId = "user2",
            content = "Hello"
        )
        assertFalse(msg.isRead)
    }

    @Test
    fun `shared word message has correct type`() {
        val msg = FriendMessage(
            messageId = "msg1",
            type = MessageType.SHARED_WORD,
            metadata = mapOf("sourceText" to "hello", "targetText" to "hola")
        )
        assertEquals(MessageType.SHARED_WORD, msg.type)
        assertEquals("hello", msg.metadata["sourceText"])
    }

    @Test
    fun `shared learning material message has correct type`() {
        val msg = FriendMessage(
            messageId = "msg1",
            type = MessageType.SHARED_LEARNING_MATERIAL,
            metadata = mapOf("title" to "English → Spanish")
        )
        assertEquals(MessageType.SHARED_LEARNING_MATERIAL, msg.type)
    }

    @Test
    fun `FriendMessage content is limited to 2000 chars by convention`() {
        val longContent = "a".repeat(2000)
        val msg = FriendMessage(content = longContent)
        assertEquals(2000, msg.content.length)
    }

    @Test
    fun `chat ID is deterministic from sorted user IDs`() {
        val uid1 = "abc"
        val uid2 = "xyz"
        val chatId = listOf(uid1, uid2).sorted().joinToString("_")
        assertEquals("abc_xyz", chatId)

        val chatId2 = listOf(uid2, uid1).sorted().joinToString("_")
        assertEquals(chatId, chatId2)
    }
}
