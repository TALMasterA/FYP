package com.example.fyp.model

import com.google.firebase.Timestamp
import org.junit.Test
import org.junit.Assert.*

/**
 * Unit tests for TranslationRecord data model.
 *
 * Tests business logic for:
 * - Session grouping (sessionId)
 * - Speaker/direction tracking for continuous conversation mode
 * - Sequence ordering within sessions
 * - Translation mode validation
 */
class TranslationRecordTest {

    // --- Basic Translation Record Tests ---

    @Test
    fun `translation record stores source and target text`() {
        val record = TranslationRecord(
            id = "rec1",
            userId = "user1",
            sourceText = "Hello",
            targetText = "Hola",
            sourceLang = "en",
            targetLang = "es"
        )

        assertEquals("Hello", record.sourceText)
        assertEquals("Hola", record.targetText)
        assertEquals("en", record.sourceLang)
        assertEquals("es", record.targetLang)
    }

    @Test
    fun `translation record has timestamp`() {
        val now = Timestamp.now()
        val record = TranslationRecord(
            id = "rec1",
            userId = "user1",
            sourceText = "Test",
            targetText = "Prueba",
            timestamp = now
        )

        assertEquals(now, record.timestamp)
        assertNotNull(record.timestamp)
    }

    // --- Session Grouping Tests ---

    @Test
    fun `multiple translations can share same session ID`() {
        val sessionId = "session_123"

        val record1 = TranslationRecord(
            id = "rec1",
            userId = "user1",
            sessionId = sessionId,
            sourceText = "First",
            targetText = "Primero"
        )

        val record2 = TranslationRecord(
            id = "rec2",
            userId = "user1",
            sessionId = sessionId,
            sourceText = "Second",
            targetText = "Segundo"
        )

        assertEquals(sessionId, record1.sessionId)
        assertEquals(sessionId, record2.sessionId)
        assertEquals(record1.sessionId, record2.sessionId)
    }

    @Test
    fun `different sessions have different session IDs`() {
        val record1 = TranslationRecord(
            id = "rec1",
            userId = "user1",
            sessionId = "session_1"
        )

        val record2 = TranslationRecord(
            id = "rec2",
            userId = "user1",
            sessionId = "session_2"
        )

        assertNotEquals(record1.sessionId, record2.sessionId)
    }

    // --- Speaker and Direction Tests (Continuous Mode) ---

    @Test
    fun `continuous mode record tracks speaker A`() {
        val record = TranslationRecord(
            id = "rec1",
            userId = "user1",
            mode = "continuous",
            speaker = "A",
            direction = "A_to_B"
        )

        assertEquals("A", record.speaker)
        assertEquals("A_to_B", record.direction)
    }

    @Test
    fun `continuous mode record tracks speaker B`() {
        val record = TranslationRecord(
            id = "rec1",
            userId = "user1",
            mode = "continuous",
            speaker = "B",
            direction = "B_to_A"
        )

        assertEquals("B", record.speaker)
        assertEquals("B_to_A", record.direction)
    }

    @Test
    fun `bidirectional conversation alternates speakers`() {
        val sessionId = "conv_123"

        val records = listOf(
            TranslationRecord(id = "1", sessionId = sessionId, speaker = "A", direction = "A_to_B", sequence = 1),
            TranslationRecord(id = "2", sessionId = sessionId, speaker = "B", direction = "B_to_A", sequence = 2),
            TranslationRecord(id = "3", sessionId = sessionId, speaker = "A", direction = "A_to_B", sequence = 3),
            TranslationRecord(id = "4", sessionId = sessionId, speaker = "B", direction = "B_to_A", sequence = 4)
        )

        // Verify session consistency
        assertTrue(records.all { it.sessionId == sessionId })

        // Verify speaker alternation
        assertEquals("A", records[0].speaker)
        assertEquals("B", records[1].speaker)
        assertEquals("A", records[2].speaker)
        assertEquals("B", records[3].speaker)
    }

    @Test
    fun `non-continuous mode can have null speaker and direction`() {
        val record = TranslationRecord(
            id = "rec1",
            userId = "user1",
            mode = "speech_to_text",
            speaker = null,
            direction = null
        )

        assertNull(record.speaker)
        assertNull(record.direction)
    }

    // --- Sequence Ordering Tests ---

    @Test
    fun `sequence tracks order within session`() {
        val sessionId = "session_1"

        val records = listOf(
            TranslationRecord(id = "1", sessionId = sessionId, sequence = 1L),
            TranslationRecord(id = "2", sessionId = sessionId, sequence = 2L),
            TranslationRecord(id = "3", sessionId = sessionId, sequence = 3L)
        )

        assertEquals(1L, records[0].sequence)
        assertEquals(2L, records[1].sequence)
        assertEquals(3L, records[2].sequence)
    }

    @Test
    fun `sequence can be used to sort session records`() {
        val sessionId = "session_1"

        val records = listOf(
            TranslationRecord(id = "3", sessionId = sessionId, sequence = 3L),
            TranslationRecord(id = "1", sessionId = sessionId, sequence = 1L),
            TranslationRecord(id = "2", sessionId = sessionId, sequence = 2L)
        )

        val sorted = records.sortedBy { it.sequence }

        assertEquals("1", sorted[0].id)
        assertEquals("2", sorted[1].id)
        assertEquals("3", sorted[2].id)
    }

    @Test
    fun `sequence can be null for simple translations`() {
        val record = TranslationRecord(
            id = "rec1",
            userId = "user1",
            sequence = null
        )

        assertNull(record.sequence)
    }

    // --- Mode Validation Tests ---

    @Test
    fun `translation record supports speech mode`() {
        val record = TranslationRecord(
            id = "rec1",
            userId = "user1",
            mode = "speech_to_text"
        )

        assertEquals("speech_to_text", record.mode)
    }

    @Test
    fun `translation record supports continuous mode`() {
        val record = TranslationRecord(
            id = "rec1",
            userId = "user1",
            mode = "continuous"
        )

        assertEquals("continuous", record.mode)
    }

    @Test
    fun `translation record can have empty mode`() {
        val record = TranslationRecord(
            id = "rec1",
            userId = "user1",
            mode = ""
        )

        assertEquals("", record.mode)
    }

    // --- Language Pair Tests ---

    @Test
    fun `translation supports bidirectional language pairs`() {
        val recordAtoB = TranslationRecord(
            id = "rec1",
            sourceLang = "en",
            targetLang = "es"
        )

        val recordBtoA = TranslationRecord(
            id = "rec2",
            sourceLang = "es",
            targetLang = "en"
        )

        // Languages are swapped but it's the same pair
        assertEquals(recordAtoB.sourceLang, recordBtoA.targetLang)
        assertEquals(recordAtoB.targetLang, recordBtoA.sourceLang)
    }

    // --- Edge Cases ---

    @Test
    fun `translation record with empty text is allowed`() {
        val record = TranslationRecord(
            id = "rec1",
            userId = "user1",
            sourceText = "",
            targetText = ""
        )

        assertEquals("", record.sourceText)
        assertEquals("", record.targetText)
    }

    @Test
    fun `translation record default values are empty strings`() {
        val record = TranslationRecord()

        assertEquals("", record.id)
        assertEquals("", record.userId)
        assertEquals("", record.sourceText)
        assertEquals("", record.targetText)
        assertEquals("", record.sourceLang)
        assertEquals("", record.targetLang)
        assertEquals("", record.mode)
        assertEquals("", record.sessionId)
    }

    // --- Scenario Tests ---

    @Test
    fun `continuous conversation session maintains context`() {
        val sessionId = "conv_dinner_2024"
        val userId = "user1"

        val conversation = listOf(
            TranslationRecord(
                id = "1",
                userId = userId,
                sessionId = sessionId,
                sourceText = "What would you like to eat?",
                targetText = "¿Qué te gustaría comer?",
                sourceLang = "en",
                targetLang = "es",
                speaker = "A",
                direction = "A_to_B",
                sequence = 1L
            ),
            TranslationRecord(
                id = "2",
                userId = userId,
                sessionId = sessionId,
                sourceText = "Me gustaría pizza",
                targetText = "I would like pizza",
                sourceLang = "es",
                targetLang = "en",
                speaker = "B",
                direction = "B_to_A",
                sequence = 2L
            ),
            TranslationRecord(
                id = "3",
                userId = userId,
                sessionId = sessionId,
                sourceText = "Good choice!",
                targetText = "¡Buena elección!",
                sourceLang = "en",
                targetLang = "es",
                speaker = "A",
                direction = "A_to_B",
                sequence = 3L
            )
        )

        // All records share same session
        assertTrue(conversation.all { it.sessionId == sessionId })

        // All records belong to same user
        assertTrue(conversation.all { it.userId == userId })

        // Sequence is ordered
        assertEquals(listOf(1L, 2L, 3L), conversation.map { it.sequence })

        // Speakers alternate
        assertEquals(listOf("A", "B", "A"), conversation.map { it.speaker })
    }
}
