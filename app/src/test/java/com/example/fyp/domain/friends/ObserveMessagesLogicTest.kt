package com.example.fyp.domain.friends

import com.example.fyp.model.friends.FriendMessage
import com.example.fyp.model.friends.MessageType
import com.google.firebase.Timestamp
import org.junit.Assert.*
import org.junit.Test

/**
 * Tests for pure logic extracted from ObserveMessagesUseCase.
 *
 * Covers:
 *  - clearedAt timestamp filtering
 *  - No clearedAt returns all messages
 *  - Edge cases: exact boundary, empty list, all filtered out
 */
class ObserveMessagesLogicTest {

    /**
     * Replicates the clearedAt filtering from ObserveMessagesUseCase (line 27-28):
     * messages.filter { it.createdAt > clearedAt }
     */
    private fun filterMessagesByClearedAt(
        messages: List<FriendMessage>,
        clearedAt: Timestamp?
    ): List<FriendMessage> {
        return if (clearedAt != null) {
            messages.filter { it.createdAt > clearedAt }
        } else {
            messages
        }
    }

    // Helper to create a Timestamp from seconds
    private fun ts(seconds: Long) = Timestamp(seconds, 0)

    private val sampleMessages = listOf(
        FriendMessage(messageId = "m1", content = "Hello", createdAt = ts(100)),
        FriendMessage(messageId = "m2", content = "Hi", createdAt = ts(200)),
        FriendMessage(messageId = "m3", content = "How are you?", createdAt = ts(300)),
        FriendMessage(messageId = "m4", content = "Fine", createdAt = ts(400)),
        FriendMessage(messageId = "m5", content = "Bye", createdAt = ts(500))
    )

    // ── No clearedAt ─────────────────────────────────────────────────────────

    @Test
    fun `no clearedAt returns all messages`() {
        val result = filterMessagesByClearedAt(sampleMessages, null)
        assertEquals(5, result.size)
    }

    // ── With clearedAt ───────────────────────────────────────────────────────

    @Test
    fun `clearedAt filters messages older than or equal to timestamp`() {
        val result = filterMessagesByClearedAt(sampleMessages, ts(200))
        assertEquals(3, result.size) // m3(300), m4(400), m5(500)
        assertEquals("m3", result[0].messageId)
        assertEquals("m4", result[1].messageId)
        assertEquals("m5", result[2].messageId)
    }

    @Test
    fun `clearedAt at exact message timestamp excludes that message`() {
        val result = filterMessagesByClearedAt(sampleMessages, ts(300))
        assertEquals(2, result.size) // m4(400), m5(500)
        assertEquals("m4", result[0].messageId)
    }

    @Test
    fun `clearedAt before all messages returns all`() {
        val result = filterMessagesByClearedAt(sampleMessages, ts(50))
        assertEquals(5, result.size)
    }

    @Test
    fun `clearedAt after all messages returns empty`() {
        val result = filterMessagesByClearedAt(sampleMessages, ts(600))
        assertTrue(result.isEmpty())
    }

    @Test
    fun `clearedAt at last message timestamp returns empty`() {
        val result = filterMessagesByClearedAt(sampleMessages, ts(500))
        assertTrue(result.isEmpty())
    }

    @Test
    fun `clearedAt with empty message list returns empty`() {
        val result = filterMessagesByClearedAt(emptyList(), ts(100))
        assertTrue(result.isEmpty())
    }

    @Test
    fun `clearedAt filters correctly with single message after threshold`() {
        val result = filterMessagesByClearedAt(sampleMessages, ts(450))
        assertEquals(1, result.size)
        assertEquals("m5", result[0].messageId)
    }

    // ── Edge: nanosecond precision ───────────────────────────────────────────

    @Test
    fun `clearedAt with nanosecond precision`() {
        val messages = listOf(
            FriendMessage(messageId = "m1", createdAt = Timestamp(100, 500_000_000)),
            FriendMessage(messageId = "m2", createdAt = Timestamp(100, 500_000_001))
        )
        val clearedAt = Timestamp(100, 500_000_000) // exactly m1's timestamp

        val result = filterMessagesByClearedAt(messages, clearedAt)
        assertEquals(1, result.size)
        assertEquals("m2", result[0].messageId)
    }
}
