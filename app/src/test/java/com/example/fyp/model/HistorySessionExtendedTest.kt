package com.example.fyp.model

import org.junit.Assert.*
import org.junit.Test

/**
 * Tests for HistorySession data class and session management logic.
 *
 * Covers: defaults, field storage, sorting, equality, and session naming.
 */
class HistorySessionExtendedTest {

    @Test
    fun `HistorySession has correct defaults`() {
        val session = HistorySession()
        assertEquals("", session.sessionId)
        assertEquals("", session.name)
        assertNotNull(session.updatedAt)
    }

    @Test
    fun `HistorySession stores all fields`() {
        val session = HistorySession(
            sessionId = "sess-abc-12345678",
            name = "Restaurant Conversation"
        )
        assertEquals("sess-abc-12345678", session.sessionId)
        assertEquals("Restaurant Conversation", session.name)
    }

    @Test
    fun `HistorySession equality based on all fields`() {
        val ts = com.google.firebase.Timestamp(1000, 0)
        val a = HistorySession("s1", "Chat", ts)
        val b = HistorySession("s1", "Chat", ts)
        assertEquals(a, b)
    }

    @Test
    fun `HistorySession inequality when name differs`() {
        val ts = com.google.firebase.Timestamp(1000, 0)
        val a = HistorySession("s1", "Chat A", ts)
        val b = HistorySession("s1", "Chat B", ts)
        assertNotEquals(a, b)
    }

    @Test
    fun `HistorySession inequality when sessionId differs`() {
        val ts = com.google.firebase.Timestamp(1000, 0)
        val a = HistorySession("s1", "Chat", ts)
        val b = HistorySession("s2", "Chat", ts)
        assertNotEquals(a, b)
    }

    @Test
    fun `HistorySession copy updates name only`() {
        val ts = com.google.firebase.Timestamp(1000, 0)
        val original = HistorySession("s1", "Old Name", ts)
        val renamed = original.copy(name = "New Name")
        assertEquals("s1", renamed.sessionId)
        assertEquals("New Name", renamed.name)
        assertEquals(ts, renamed.updatedAt)
    }

    @Test
    fun `HistorySession sessions can be sorted by updatedAt descending`() {
        val sessions = listOf(
            HistorySession("s1", "Oldest", com.google.firebase.Timestamp(100, 0)),
            HistorySession("s3", "Newest", com.google.firebase.Timestamp(300, 0)),
            HistorySession("s2", "Middle", com.google.firebase.Timestamp(200, 0))
        ).sortedByDescending { it.updatedAt.seconds }

        assertEquals("s3", sessions[0].sessionId)
        assertEquals("s2", sessions[1].sessionId)
        assertEquals("s1", sessions[2].sessionId)
    }

    @Test
    fun `HistorySession with empty name represents unnamed session`() {
        val session = HistorySession(sessionId = "s1", name = "")
        assertEquals("", session.name)
        assertTrue(session.name.isEmpty())
    }

    @Test
    fun `HistorySession sessionId can be used for display truncation`() {
        val session = HistorySession(sessionId = "abcdefgh12345678")
        // First 8 chars used for display (as in formatSessionTitle)
        assertEquals("abcdefgh", session.sessionId.take(8))
    }
}

