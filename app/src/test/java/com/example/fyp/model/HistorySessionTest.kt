package com.example.fyp.model

import com.google.firebase.Timestamp
import org.junit.Test
import org.junit.Assert.*

/**
 * Unit tests for HistorySession data model.
 *
 * Tests business logic for:
 * - Session identification and naming
 * - Session timestamp tracking
 * - Session uniqueness
 * - Session organization
 */
class HistorySessionTest {

    // --- Basic Session Tests ---

    @Test
    fun `session has unique identifier`() {
        val session = HistorySession(
            sessionId = "session_123",
            name = "Morning Conversation",
            updatedAt = Timestamp.now()
        )

        assertEquals("session_123", session.sessionId)
        assertNotEquals("", session.sessionId)
    }

    @Test
    fun `session has descriptive name`() {
        val session = HistorySession(
            sessionId = "session_1",
            name = "Business Meeting",
            updatedAt = Timestamp.now()
        )

        assertEquals("Business Meeting", session.name)
    }

    @Test
    fun `session has update timestamp`() {
        val now = Timestamp.now()
        val session = HistorySession(
            sessionId = "session_1",
            name = "Test Session",
            updatedAt = now
        )

        assertEquals(now, session.updatedAt)
        assertNotNull(session.updatedAt)
    }

    // --- Session Naming Tests ---

    @Test
    fun `session can be renamed`() {
        val originalSession = HistorySession(
            sessionId = "session_1",
            name = "Untitled Session"
        )

        val renamedSession = originalSession.copy(
            name = "French Lesson 1"
        )

        assertEquals("Untitled Session", originalSession.name)
        assertEquals("French Lesson 1", renamedSession.name)
        assertEquals(originalSession.sessionId, renamedSession.sessionId)
    }

    @Test
    fun `session name can be descriptive`() {
        val sessions = listOf(
            HistorySession(sessionId = "1", name = "Restaurant Conversation"),
            HistorySession(sessionId = "2", name = "Hotel Check-in"),
            HistorySession(sessionId = "3", name = "Shopping at Market"),
            HistorySession(sessionId = "4", name = "Meeting with Client")
        )

        assertTrue(sessions.all { it.name.isNotEmpty() })
        assertTrue(sessions.all { it.name.length > 5 })
    }

    @Test
    fun `session name can contain special characters`() {
        val session = HistorySession(
            sessionId = "session_1",
            name = "Meeting @ Café - 2024/01/15"
        )

        assertTrue(session.name.contains("@"))
        assertTrue(session.name.contains("-"))
        assertTrue(session.name.contains("/"))
    }

    @Test
    fun `session name can be empty for default sessions`() {
        val session = HistorySession(
            sessionId = "session_1",
            name = ""
        )

        assertEquals("", session.name)
    }

    // --- Session Timestamp Tests ---

    @Test
    fun `session timestamp updates when modified`() {
        val time1 = Timestamp(1000, 0)
        val time2 = Timestamp(2000, 0)

        val session = HistorySession(
            sessionId = "session_1",
            name = "Test",
            updatedAt = time1
        )

        val updatedSession = session.copy(updatedAt = time2)

        assertTrue(updatedSession.updatedAt.seconds > session.updatedAt.seconds)
    }

    @Test
    fun `sessions can be sorted by update time`() {
        val time1 = Timestamp(1000, 0)
        val time2 = Timestamp(2000, 0)
        val time3 = Timestamp(3000, 0)

        val sessions = listOf(
            HistorySession(sessionId = "2", name = "Middle", updatedAt = time2),
            HistorySession(sessionId = "3", name = "Newest", updatedAt = time3),
            HistorySession(sessionId = "1", name = "Oldest", updatedAt = time1)
        )

        val sorted = sessions.sortedByDescending { it.updatedAt.seconds }

        assertEquals("3", sorted[0].sessionId) // Most recent
        assertEquals("2", sorted[1].sessionId)
        assertEquals("1", sorted[2].sessionId) // Oldest
    }

    // --- Session Uniqueness Tests ---

    @Test
    fun `different sessions have different IDs`() {
        val session1 = HistorySession(sessionId = "session_1", name = "First")
        val session2 = HistorySession(sessionId = "session_2", name = "Second")

        assertNotEquals(session1.sessionId, session2.sessionId)
    }

    @Test
    fun `sessions with same ID but different names are considered equal by ID`() {
        val session1 = HistorySession(sessionId = "session_1", name = "Name 1")
        val session2 = HistorySession(sessionId = "session_1", name = "Name 2")

        assertEquals(session1.sessionId, session2.sessionId)
        assertNotEquals(session1.name, session2.name)
    }

    @Test
    fun `session ID can be used as unique key`() {
        val sessions = listOf(
            HistorySession(sessionId = "session_1", name = "A"),
            HistorySession(sessionId = "session_2", name = "B"),
            HistorySession(sessionId = "session_3", name = "C")
        )

        val uniqueIds = sessions.map { it.sessionId }.distinct()
        assertEquals(sessions.size, uniqueIds.size)
    }

    // --- Session Organization Tests ---

    @Test
    fun `sessions can be grouped by purpose`() {
        val sessions = listOf(
            HistorySession(sessionId = "1", name = "Work: Client Meeting"),
            HistorySession(sessionId = "2", name = "Work: Team Standup"),
            HistorySession(sessionId = "3", name = "Personal: Shopping"),
            HistorySession(sessionId = "4", name = "Personal: Restaurant")
        )

        val workSessions = sessions.filter { it.name.startsWith("Work:") }
        val personalSessions = sessions.filter { it.name.startsWith("Personal:") }

        assertEquals(2, workSessions.size)
        assertEquals(2, personalSessions.size)
    }

    @Test
    fun `sessions can be filtered by name pattern`() {
        val sessions = listOf(
            HistorySession(sessionId = "1", name = "French Lesson 1"),
            HistorySession(sessionId = "2", name = "French Lesson 2"),
            HistorySession(sessionId = "3", name = "Spanish Practice"),
            HistorySession(sessionId = "4", name = "German Conversation")
        )

        val frenchSessions = sessions.filter { it.name.contains("French") }

        assertEquals(2, frenchSessions.size)
        assertTrue(frenchSessions.all { it.name.contains("French") })
    }

    // --- Edge Cases ---

    @Test
    fun `session default values are empty strings`() {
        val session = HistorySession()

        assertEquals("", session.sessionId)
        assertEquals("", session.name)
    }

    @Test
    fun `session name can be very long`() {
        val longName = "A".repeat(200)
        val session = HistorySession(
            sessionId = "session_1",
            name = longName
        )

        assertEquals(200, session.name.length)
    }

    @Test
    fun `session name can contain unicode characters`() {
        val sessions = listOf(
            HistorySession(sessionId = "1", name = "日本語の練習"),     // Japanese
            HistorySession(sessionId = "2", name = "Français练习"),    // Mixed
            HistorySession(sessionId = "3", name = "Deutsche Übung"), // German with umlaut
            HistorySession(sessionId = "4", name = "Español 🇪🇸")      // With emoji
        )

        assertTrue(sessions.all { it.name.isNotEmpty() })
    }

    // --- Scenario Tests ---

    @Test
    fun `typical user session workflow`() {
        // User creates new session
        val newSession = HistorySession(
            sessionId = "session_${System.currentTimeMillis()}",
            name = "Untitled Session",
            updatedAt = Timestamp.now()
        )

        assertEquals("Untitled Session", newSession.name)

        // User renames session
        val renamedSession = newSession.copy(
            name = "Restaurant Conversation - 2024/01/15",
            updatedAt = Timestamp(newSession.updatedAt.seconds + 60, 0)
        )

        assertEquals("Restaurant Conversation - 2024/01/15", renamedSession.name)
        assertEquals(newSession.sessionId, renamedSession.sessionId)
        assertTrue(renamedSession.updatedAt.seconds > newSession.updatedAt.seconds)
    }

    @Test
    fun `user organizes sessions by date and topic`() {
        val sessions = listOf(
            HistorySession(sessionId = "1", name = "2024-01-15: French Restaurant"),
            HistorySession(sessionId = "2", name = "2024-01-16: Business Meeting"),
            HistorySession(sessionId = "3", name = "2024-01-17: French Market"),
            HistorySession(sessionId = "4", name = "2024-01-18: German Conversation")
        )

        // All sessions have date prefixes
        assertTrue(sessions.all { it.name.matches(Regex("\\d{4}-\\d{2}-\\d{2}:.*")) })

        // Can filter by date
        val jan15Sessions = sessions.filter { it.name.startsWith("2024-01-15") }
        assertEquals(1, jan15Sessions.size)

        // Can filter by topic
        val frenchSessions = sessions.filter { it.name.contains("French") }
        assertEquals(2, frenchSessions.size)
    }

    @Test
    fun `continuous conversation mode creates session automatically`() {
        val timestamp = Timestamp.now()
        val autoSessionId = "conv_${timestamp.seconds}"

        val session = HistorySession(
            sessionId = autoSessionId,
            name = "Conversation ${timestamp.toDate()}",
            updatedAt = timestamp
        )

        assertTrue(session.sessionId.startsWith("conv_"))
        assertTrue(session.name.startsWith("Conversation"))
    }

    @Test
    fun `session list shows most recent first`() {
        val now = Timestamp.now()
        val sessions = listOf(
            HistorySession(
                sessionId = "1",
                name = "Yesterday",
                updatedAt = Timestamp(now.seconds - 86400, 0)
            ),
            HistorySession(
                sessionId = "2",
                name = "Today",
                updatedAt = now
            ),
            HistorySession(
                sessionId = "3",
                name = "Last Week",
                updatedAt = Timestamp(now.seconds - 604800, 0)
            )
        )

        val sorted = sessions.sortedByDescending { it.updatedAt.seconds }

        assertEquals("Today", sorted[0].name)      // Most recent
        assertEquals("Yesterday", sorted[1].name)
        assertEquals("Last Week", sorted[2].name)  // Oldest
    }
}
