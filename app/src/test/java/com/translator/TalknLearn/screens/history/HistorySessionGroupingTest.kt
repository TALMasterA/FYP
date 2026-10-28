package com.translator.TalknLearn.screens.history

import com.translator.TalknLearn.model.TranslationRecord
import com.google.firebase.Timestamp
import org.junit.Assert.*
import org.junit.Test

class HistorySessionGroupingTest {

    // Helper to create a TranslationRecord with specific fields
    private fun record(
        mode: String = "continuous",
        sessionId: String = "session-abc-12345678",
        timestampSeconds: Long = 1000L
    ) = TranslationRecord(
        id = "rec-${timestampSeconds}",
        mode = mode,
        sessionId = sessionId,
        timestamp = Timestamp(timestampSeconds, 0)
    )

    // ── groupContinuousSessions ─────────────────────────────────────

    @Test
    fun `groupContinuousSessions - empty list returns empty`() {
        val result = groupContinuousSessions(emptyList())
        assertTrue(result.isEmpty())
    }

    @Test
    fun `groupContinuousSessions - filters out non-continuous records`() {
        val records = listOf(
            record(mode = "single", sessionId = "s1"),
            record(mode = "ocr", sessionId = "s2"),
            record(mode = "quick", sessionId = "s3")
        )
        val result = groupContinuousSessions(records)
        assertTrue(result.isEmpty())
    }

    @Test
    fun `groupContinuousSessions - filters out blank sessionIds`() {
        val records = listOf(
            record(sessionId = ""),
            record(sessionId = "  ") // not blank technically but not empty either – but let's test empty
        )
        // Only empty sessionId is filtered by isNotBlank
        val result = groupContinuousSessions(records)
        // "  " is not blank of String content, but isNotBlank returns false for whitespace-only
        // Actually "  ".isNotBlank() == false in Kotlin, so both are filtered
        assertTrue(result.isEmpty())
    }

    @Test
    fun `groupContinuousSessions - groups records by sessionId`() {
        val records = listOf(
            record(sessionId = "s1", timestampSeconds = 100),
            record(sessionId = "s1", timestampSeconds = 200),
            record(sessionId = "s2", timestampSeconds = 300)
        )
        val result = groupContinuousSessions(records)

        assertEquals(2, result.size)
        val s1 = result.find { it.sessionId == "s1" }
        val s2 = result.find { it.sessionId == "s2" }
        assertNotNull(s1)
        assertNotNull(s2)
        assertEquals(2, s1!!.records.size)
        assertEquals(1, s2!!.records.size)
    }

    @Test
    fun `groupContinuousSessions - sorted by latest timestamp descending`() {
        val records = listOf(
            record(sessionId = "old-session", timestampSeconds = 100),
            record(sessionId = "new-session", timestampSeconds = 500),
            record(sessionId = "mid-session", timestampSeconds = 300)
        )
        val result = groupContinuousSessions(records)

        assertEquals(3, result.size)
        assertEquals("new-session", result[0].sessionId)
        assertEquals("mid-session", result[1].sessionId)
        assertEquals("old-session", result[2].sessionId)
    }

    @Test
    fun `groupContinuousSessions - mixed mode records only includes continuous`() {
        val records = listOf(
            record(mode = "continuous", sessionId = "s1", timestampSeconds = 100),
            record(mode = "single", sessionId = "s1", timestampSeconds = 200),
            record(mode = "continuous", sessionId = "s1", timestampSeconds = 300)
        )
        val result = groupContinuousSessions(records)
        assertEquals(1, result.size)
        assertEquals(2, result[0].records.size) // only 2 continuous records
    }

    // ── formatSessionTitle ──────────────────────────────────────────

    @Test
    fun `formatSessionTitle - template with id placeholder`() {
        val result = formatSessionTitle("Session {id}", "abcdef1234567890")
        assertEquals("Session abcdef12", result)
    }

    @Test
    fun `formatSessionTitle - template with percent-s placeholder`() {
        val result = formatSessionTitle("Session %s", "abcdef1234567890")
        assertEquals("Session abcdef12", result)
    }

    @Test
    fun `formatSessionTitle - sessionId shorter than 8 chars`() {
        val result = formatSessionTitle("Session {id}", "abc")
        assertEquals("Session abc", result)
    }

    @Test
    fun `formatSessionTitle - sessionId exactly 8 chars`() {
        val result = formatSessionTitle("Session {id}", "12345678")
        assertEquals("Session 12345678", result)
    }

    @Test
    fun `formatSessionTitle - malformed format string falls back gracefully`() {
        // A broken format string should not crash
        val result = formatSessionTitle("Session %d %d", "abc123")
        // safeFormat catches the exception and returns the template
        assertNotNull(result)
    }

    // ── formatItemsCount ────────────────────────────────────────────

    @Test
    fun `formatItemsCount - template with count placeholder`() {
        val result = formatItemsCount("{count} items", 5)
        assertEquals("5 items", result)
    }

    @Test
    fun `formatItemsCount - template with percent-d placeholder`() {
        val result = formatItemsCount("%d items", 5)
        assertEquals("5 items", result)
    }

    @Test
    fun `formatItemsCount - count of zero`() {
        val result = formatItemsCount("{count} items", 0)
        assertEquals("0 items", result)
    }

    @Test
    fun `formatItemsCount - large count`() {
        val result = formatItemsCount("{count} items", 9999)
        assertEquals("9999 items", result)
    }

    @Test
    fun `formatItemsCount - malformed format string falls back gracefully`() {
        val result = formatItemsCount("items %s %s", 5)
        assertNotNull(result)
    }
}
