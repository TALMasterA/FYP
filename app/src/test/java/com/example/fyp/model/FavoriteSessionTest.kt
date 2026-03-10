package com.example.fyp.model

import org.junit.Assert.*
import org.junit.Test

/**
 * Unit tests for FavoriteSession and FavoriteSessionRecord data classes.
 *
 * Covers: default values, field storage, copy semantics, list handling.
 */
class FavoriteSessionTest {

    // ── FavoriteSession ────────────────────────────────────────────

    @Test
    fun `FavoriteSession has correct defaults`() {
        val session = FavoriteSession()
        assertEquals("", session.id)
        assertEquals("", session.userId)
        assertEquals("", session.sessionId)
        assertEquals("", session.sessionName)
        assertTrue(session.records.isEmpty())
        assertNotNull(session.createdAt)
    }

    @Test
    fun `FavoriteSession stores all fields`() {
        val records = listOf(
            FavoriteSessionRecord(
                sourceText = "Hello",
                targetText = "你好",
                sourceLang = "en-US",
                targetLang = "zh-TW",
                speaker = "A",
                direction = "A_to_B",
                sequence = 1
            ),
            FavoriteSessionRecord(
                sourceText = "你好",
                targetText = "Hello",
                sourceLang = "zh-TW",
                targetLang = "en-US",
                speaker = "B",
                direction = "B_to_A",
                sequence = 2
            )
        )
        val session = FavoriteSession(
            id = "fav-1",
            userId = "user123",
            sessionId = "sess-abc",
            sessionName = "Restaurant Chat",
            records = records
        )
        assertEquals("fav-1", session.id)
        assertEquals("user123", session.userId)
        assertEquals("sess-abc", session.sessionId)
        assertEquals("Restaurant Chat", session.sessionName)
        assertEquals(2, session.records.size)
    }

    @Test
    fun `FavoriteSession equality checks all fields`() {
        val records = listOf(FavoriteSessionRecord(sourceText = "Hi"))
        val a = FavoriteSession(id = "1", sessionName = "Test", records = records)
        val b = FavoriteSession(id = "1", sessionName = "Test", records = records)
        assertEquals(a, b)
    }

    @Test
    fun `FavoriteSession inequality when sessionName differs`() {
        val a = FavoriteSession(id = "1", sessionName = "Chat A")
        val b = FavoriteSession(id = "1", sessionName = "Chat B")
        assertNotEquals(a, b)
    }

    @Test
    fun `FavoriteSession copy adds records`() {
        val session = FavoriteSession(id = "1")
        val updated = session.copy(
            records = listOf(FavoriteSessionRecord(sourceText = "Hello"))
        )
        assertEquals(1, updated.records.size)
        assertEquals("1", updated.id)
    }

    // ── FavoriteSessionRecord ──────────────────────────────────────

    @Test
    fun `FavoriteSessionRecord has correct defaults`() {
        val rec = FavoriteSessionRecord()
        assertEquals("", rec.sourceText)
        assertEquals("", rec.targetText)
        assertEquals("", rec.sourceLang)
        assertEquals("", rec.targetLang)
        assertEquals("", rec.speaker)
        assertEquals("", rec.direction)
        assertEquals(0, rec.sequence)
    }

    @Test
    fun `FavoriteSessionRecord stores speaker and direction`() {
        val rec = FavoriteSessionRecord(
            sourceText = "Good morning",
            targetText = "おはようございます",
            sourceLang = "en-US",
            targetLang = "ja-JP",
            speaker = "A",
            direction = "A_to_B",
            sequence = 3
        )
        assertEquals("A", rec.speaker)
        assertEquals("A_to_B", rec.direction)
        assertEquals(3, rec.sequence)
    }

    @Test
    fun `FavoriteSessionRecord equality based on all fields`() {
        val a = FavoriteSessionRecord(sourceText = "Hi", sequence = 1)
        val b = FavoriteSessionRecord(sourceText = "Hi", sequence = 1)
        assertEquals(a, b)
    }

    @Test
    fun `FavoriteSessionRecord inequality when sequence differs`() {
        val a = FavoriteSessionRecord(sourceText = "Hi", sequence = 1)
        val b = FavoriteSessionRecord(sourceText = "Hi", sequence = 2)
        assertNotEquals(a, b)
    }

    @Test
    fun `FavoriteSessionRecords can be sorted by sequence`() {
        val records = listOf(
            FavoriteSessionRecord(sequence = 3, sourceText = "c"),
            FavoriteSessionRecord(sequence = 1, sourceText = "a"),
            FavoriteSessionRecord(sequence = 2, sourceText = "b")
        ).sortedBy { it.sequence }

        assertEquals("a", records[0].sourceText)
        assertEquals("b", records[1].sourceText)
        assertEquals("c", records[2].sourceText)
    }
}

