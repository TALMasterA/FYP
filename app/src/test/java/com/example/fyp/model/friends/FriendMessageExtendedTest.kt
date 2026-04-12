package com.example.fyp.model.friends

import com.google.firebase.Timestamp
import org.junit.Assert.*
import org.junit.Test

/**
 * Unit tests for FriendMessage data class, MessageType enum, and ChatMetadata.
 */
class FriendMessageExtendedTest {

    // ── FriendMessage defaults ──────────────────────────────────────

    @Test
    fun `default FriendMessage has empty fields`() {
        val msg = FriendMessage()

        assertEquals("", msg.messageId)
        assertEquals("", msg.chatId)
        assertEquals("", msg.senderId)
        assertEquals("", msg.receiverId)
        assertEquals("", msg.content)
        assertEquals(MessageType.TEXT, msg.type)
        assertTrue(msg.metadata.isEmpty())
        assertFalse(msg.isRead)
    }

    @Test
    fun `FriendMessage with all fields set`() {
        val ts = Timestamp(1000, 0)
        val metadata = mapOf("key" to "value" as Any)
        val msg = FriendMessage(
            messageId = "m1",
            chatId = "chat1",
            senderId = "sender1",
            receiverId = "receiver1",
            content = "Hello!",
            type = MessageType.SHARED_WORD,
            metadata = metadata,
            isRead = true,
            createdAt = ts
        )

        assertEquals("m1", msg.messageId)
        assertEquals("chat1", msg.chatId)
        assertEquals("sender1", msg.senderId)
        assertEquals("receiver1", msg.receiverId)
        assertEquals("Hello!", msg.content)
        assertEquals(MessageType.SHARED_WORD, msg.type)
        assertEquals(metadata, msg.metadata)
        assertTrue(msg.isRead)
        assertEquals(ts, msg.createdAt)
    }

    @Test
    fun `FriendMessage copy works correctly`() {
        val original = FriendMessage(messageId = "m1", content = "Hi")
        val copy = original.copy(content = "Hello")

        assertEquals("m1", copy.messageId)
        assertEquals("Hello", copy.content)
    }

    // ── MessageType enum ────────────────────────────────────────────

    @Test
    fun `MessageType has three values`() {
        assertEquals(3, MessageType.entries.size)
    }

    @Test
    fun `MessageType TEXT exists`() {
        assertEquals(MessageType.TEXT, MessageType.valueOf("TEXT"))
    }

    @Test
    fun `MessageType SHARED_WORD exists`() {
        assertEquals(MessageType.SHARED_WORD, MessageType.valueOf("SHARED_WORD"))
    }

    @Test
    fun `MessageType SHARED_LEARNING_MATERIAL exists`() {
        assertEquals(MessageType.SHARED_LEARNING_MATERIAL, MessageType.valueOf("SHARED_LEARNING_MATERIAL"))
    }

    // ── ChatMetadata ────────────────────────────────────────────────

    @Test
    fun `ChatMetadata default values`() {
        val meta = ChatMetadata()

        assertEquals("", meta.chatId)
        assertTrue(meta.participants.isEmpty())
        assertEquals("", meta.lastMessageContent)
        assertTrue(meta.unreadCount.isEmpty())
    }

    @Test
    fun `ChatMetadata getUnreadFor returns correct count`() {
        val meta = ChatMetadata(
            unreadCount = mapOf("user1" to 5L, "user2" to 0L)
        )

        assertEquals(5, meta.getUnreadFor("user1"))
        assertEquals(0, meta.getUnreadFor("user2"))
    }

    @Test
    fun `ChatMetadata getUnreadFor returns 0 for unknown user`() {
        val meta = ChatMetadata(
            unreadCount = mapOf("user1" to 3L)
        )

        assertEquals(0, meta.getUnreadFor("unknown"))
    }

    @Test
    fun `ChatMetadata getUnreadFor handles Integer type`() {
        val meta = ChatMetadata(
            unreadCount = mapOf("user1" to 7 as Any)
        )

        assertEquals(7, meta.getUnreadFor("user1"))
    }

    @Test
    fun `ChatMetadata getUnreadFor handles Double type`() {
        val meta = ChatMetadata(
            unreadCount = mapOf("user1" to 3.0 as Any)
        )

        assertEquals(3, meta.getUnreadFor("user1"))
    }

    @Test
    fun `ChatMetadata getUnreadFor returns 0 for non-numeric`() {
        val meta = ChatMetadata(
            unreadCount = mapOf("user1" to "invalid" as Any)
        )

        assertEquals(0, meta.getUnreadFor("user1"))
    }

    @Test
    fun `ChatMetadata with participants`() {
        val meta = ChatMetadata(
            chatId = "chat1",
            participants = listOf("user1", "user2"),
            lastMessageContent = "Hello!"
        )

        assertEquals("chat1", meta.chatId)
        assertEquals(2, meta.participants.size)
        assertEquals("Hello!", meta.lastMessageContent)
    }
}
