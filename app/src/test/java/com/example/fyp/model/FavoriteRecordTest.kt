package com.example.fyp.model

import com.google.firebase.Timestamp
import org.junit.Test
import org.junit.Assert.*

/**
 * Unit tests for FavoriteRecord data model.
 *
 * Tests business logic for:
 * - Favorite creation from translation records
 * - Note management
 * - Timestamp tracking
 * - User ownership
 */
class FavoriteRecordTest {

    // --- Basic Favorite Record Tests ---

    @Test
    fun `favorite record stores source and target text`() {
        val favorite = FavoriteRecord(
            id = "fav1",
            userId = "user1",
            sourceText = "Hello",
            targetText = "Hola",
            sourceLang = "en-US",
            targetLang = "es-ES"
        )

        assertEquals("Hello", favorite.sourceText)
        assertEquals("Hola", favorite.targetText)
        assertEquals("en-US", favorite.sourceLang)
        assertEquals("es-ES", favorite.targetLang)
    }

    @Test
    fun `favorite record has creation timestamp`() {
        val now = Timestamp.now()
        val favorite = FavoriteRecord(
            id = "fav1",
            userId = "user1",
            sourceText = "Test",
            targetText = "Prueba",
            createdAt = now
        )

        assertEquals(now, favorite.createdAt)
        assertNotNull(favorite.createdAt)
    }

    @Test
    fun `favorite record belongs to specific user`() {
        val favorite = FavoriteRecord(
            id = "fav1",
            userId = "user123",
            sourceText = "Good morning",
            targetText = "Buenos días"
        )

        assertEquals("user123", favorite.userId)
        assertNotEquals("", favorite.userId)
    }

    // --- Note Management Tests ---

    @Test
    fun `favorite can have a note`() {
        val favorite = FavoriteRecord(
            id = "fav1",
            userId = "user1",
            sourceText = "Hello",
            targetText = "Bonjour",
            note = "Common greeting in French"
        )

        assertEquals("Common greeting in French", favorite.note)
    }

    @Test
    fun `favorite note can be empty`() {
        val favorite = FavoriteRecord(
            id = "fav1",
            userId = "user1",
            sourceText = "Hello",
            targetText = "Ciao",
            note = ""
        )

        assertEquals("", favorite.note)
    }

    @Test
    fun `favorite note can be multi-line`() {
        val note = """
            This is a common phrase.
            Used in formal settings.
            Remember the pronunciation.
        """.trimIndent()

        val favorite = FavoriteRecord(
            id = "fav1",
            userId = "user1",
            sourceText = "Thank you",
            targetText = "Gracias",
            note = note
        )

        assertTrue(favorite.note.contains("common phrase"))
        assertTrue(favorite.note.contains("formal settings"))
        assertTrue(favorite.note.contains("pronunciation"))
    }

    // --- Creation from TranslationRecord Tests ---

    @Test
    fun `favorite can be created from translation record`() {
        val translationRecord = TranslationRecord(
            id = "trans1",
            userId = "user1",
            sourceText = "Good night",
            targetText = "Buenas noches",
            sourceLang = "en-US",
            targetLang = "es-ES",
            timestamp = Timestamp.now()
        )

        val favorite = FavoriteRecord(
            id = "fav_${translationRecord.id}",
            userId = translationRecord.userId,
            sourceText = translationRecord.sourceText,
            targetText = translationRecord.targetText,
            sourceLang = translationRecord.sourceLang,
            targetLang = translationRecord.targetLang,
            createdAt = Timestamp.now(),
            note = ""
        )

        assertEquals(translationRecord.sourceText, favorite.sourceText)
        assertEquals(translationRecord.targetText, favorite.targetText)
        assertEquals(translationRecord.sourceLang, favorite.sourceLang)
        assertEquals(translationRecord.targetLang, favorite.targetLang)
        assertEquals(translationRecord.userId, favorite.userId)
    }

    // --- Language Pair Tests ---

    @Test
    fun `favorite supports multiple language pairs`() {
        val favorites = listOf(
            FavoriteRecord(id = "1", sourceLang = "en-US", targetLang = "es-ES"),
            FavoriteRecord(id = "2", sourceLang = "en-US", targetLang = "fr-FR"),
            FavoriteRecord(id = "3", sourceLang = "ja-JP", targetLang = "en-US"),
            FavoriteRecord(id = "4", sourceLang = "zh-CN", targetLang = "ja-JP")
        )

        assertEquals(4, favorites.size)
        assertEquals(4, favorites.map { "${it.sourceLang}-${it.targetLang}" }.distinct().size)
    }

    @Test
    fun `favorite can be in reverse direction of original translation`() {
        // Original translation: English -> Spanish
        val originalFavorite = FavoriteRecord(
            id = "fav1",
            sourceText = "Hello",
            targetText = "Hola",
            sourceLang = "en-US",
            targetLang = "es-ES"
        )

        // Reverse favorite: Spanish -> English
        val reverseFavorite = FavoriteRecord(
            id = "fav2",
            sourceText = "Hola",
            targetText = "Hello",
            sourceLang = "es-ES",
            targetLang = "en-US"
        )

        assertEquals(originalFavorite.sourceLang, reverseFavorite.targetLang)
        assertEquals(originalFavorite.targetLang, reverseFavorite.sourceLang)
    }

    // --- Timestamp Ordering Tests ---

    @Test
    fun `favorites can be sorted by creation time`() {
        val time1 = Timestamp(1000, 0)
        val time2 = Timestamp(2000, 0)
        val time3 = Timestamp(3000, 0)

        val favorites = listOf(
            FavoriteRecord(id = "3", createdAt = time3),
            FavoriteRecord(id = "1", createdAt = time1),
            FavoriteRecord(id = "2", createdAt = time2)
        )

        val sorted = favorites.sortedBy { it.createdAt.seconds }

        assertEquals("1", sorted[0].id)
        assertEquals("2", sorted[1].id)
        assertEquals("3", sorted[2].id)
    }

    @Test
    fun `recent favorites come first when sorted descending`() {
        val time1 = Timestamp(1000, 0)
        val time2 = Timestamp(2000, 0)
        val time3 = Timestamp(3000, 0)

        val favorites = listOf(
            FavoriteRecord(id = "1", createdAt = time1),
            FavoriteRecord(id = "2", createdAt = time2),
            FavoriteRecord(id = "3", createdAt = time3)
        )

        val sorted = favorites.sortedByDescending { it.createdAt.seconds }

        assertEquals("3", sorted[0].id) // Most recent
        assertEquals("2", sorted[1].id)
        assertEquals("1", sorted[2].id) // Oldest
    }

    // --- Edge Cases ---

    @Test
    fun `favorite with empty text is allowed`() {
        val favorite = FavoriteRecord(
            id = "fav1",
            userId = "user1",
            sourceText = "",
            targetText = ""
        )

        assertEquals("", favorite.sourceText)
        assertEquals("", favorite.targetText)
    }

    @Test
    fun `favorite default values are empty strings`() {
        val favorite = FavoriteRecord()

        assertEquals("", favorite.id)
        assertEquals("", favorite.userId)
        assertEquals("", favorite.sourceText)
        assertEquals("", favorite.targetText)
        assertEquals("", favorite.sourceLang)
        assertEquals("", favorite.targetLang)
        assertEquals("", favorite.note)
    }

    @Test
    fun `favorite can have very long text`() {
        val longText = "a".repeat(500)
        val favorite = FavoriteRecord(
            id = "fav1",
            userId = "user1",
            sourceText = longText,
            targetText = longText
        )

        assertEquals(500, favorite.sourceText.length)
        assertEquals(500, favorite.targetText.length)
    }

    // --- Scenario Tests ---

    @Test
    fun `user builds vocabulary collection with favorites`() {
        val userId = "user1"
        val spanishVocab = listOf(
            FavoriteRecord(
                id = "1",
                userId = userId,
                sourceText = "Hello",
                targetText = "Hola",
                sourceLang = "en-US",
                targetLang = "es-ES",
                note = "Basic greeting"
            ),
            FavoriteRecord(
                id = "2",
                userId = userId,
                sourceText = "Goodbye",
                targetText = "Adiós",
                sourceLang = "en-US",
                targetLang = "es-ES",
                note = "Basic farewell"
            ),
            FavoriteRecord(
                id = "3",
                userId = userId,
                sourceText = "Thank you",
                targetText = "Gracias",
                sourceLang = "en-US",
                targetLang = "es-ES",
                note = "Expression of gratitude"
            )
        )

        // All favorites belong to same user
        assertTrue(spanishVocab.all { it.userId == userId })

        // All favorites are for same language pair
        assertTrue(spanishVocab.all { it.sourceLang == "en-US" && it.targetLang == "es-ES" })

        // All favorites have notes
        assertTrue(spanishVocab.all { it.note.isNotEmpty() })
    }

    @Test
    fun `multilingual user has favorites in multiple languages`() {
        val userId = "polyglot_user"
        val favorites = listOf(
            FavoriteRecord(id = "1", userId = userId, sourceLang = "en-US", targetLang = "es-ES"),
            FavoriteRecord(id = "2", userId = userId, sourceLang = "en-US", targetLang = "fr-FR"),
            FavoriteRecord(id = "3", userId = userId, sourceLang = "en-US", targetLang = "de-DE"),
            FavoriteRecord(id = "4", userId = userId, sourceLang = "en-US", targetLang = "ja-JP")
        )

        // All belong to same user
        assertTrue(favorites.all { it.userId == userId })

        // Different target languages
        val targetLanguages = favorites.map { it.targetLang }.distinct()
        assertEquals(4, targetLanguages.size)
    }

    @Test
    fun `favorite phrasebook organized by topic with notes`() {
        val userId = "user1"
        val travelPhrases = listOf(
            FavoriteRecord(
                id = "1",
                userId = userId,
                sourceText = "Where is the bathroom?",
                targetText = "¿Dónde está el baño?",
                note = "Travel - Essential"
            ),
            FavoriteRecord(
                id = "2",
                userId = userId,
                sourceText = "How much does this cost?",
                targetText = "¿Cuánto cuesta esto?",
                note = "Travel - Shopping"
            ),
            FavoriteRecord(
                id = "3",
                userId = userId,
                sourceText = "I need help",
                targetText = "Necesito ayuda",
                note = "Travel - Emergency"
            )
        )

        // All notes start with "Travel"
        assertTrue(travelPhrases.all { it.note.startsWith("Travel") })

        // Different categories
        val categories = travelPhrases.map { it.note.split(" - ").last() }
        assertEquals(listOf("Essential", "Shopping", "Emergency"), categories)
    }

    // --- FavoriteSession Tests ---

    @Test
    fun `favorite session stores session metadata`() {
        val session = FavoriteSession(
            id = "fs1",
            userId = "user1",
            sessionId = "sess1",
            sessionName = "Airport Check-in"
        )

        assertEquals("fs1", session.id)
        assertEquals("user1", session.userId)
        assertEquals("sess1", session.sessionId)
        assertEquals("Airport Check-in", session.sessionName)
    }

    @Test
    fun `favorite session default values are empty`() {
        val session = FavoriteSession()
        assertEquals("", session.id)
        assertEquals("", session.userId)
        assertEquals("", session.sessionId)
        assertEquals("", session.sessionName)
        assertEquals(emptyList<FavoriteSessionRecord>(), session.records)
    }

    @Test
    fun `favorite session contains embedded records`() {
        val records = listOf(
            FavoriteSessionRecord(sourceText = "Hello", targetText = "Hola", speaker = "A", sequence = 1),
            FavoriteSessionRecord(sourceText = "How are you?", targetText = "¿Cómo estás?", speaker = "B", sequence = 2),
            FavoriteSessionRecord(sourceText = "I am fine", targetText = "Estoy bien", speaker = "A", sequence = 3)
        )
        val session = FavoriteSession(id = "fs1", sessionId = "s1", records = records)

        assertEquals(3, session.records.size)
        assertEquals("A", session.records[0].speaker)
        assertEquals("B", session.records[1].speaker)
        assertEquals(3, session.records[2].sequence)
    }

    @Test
    fun `favorite session records maintain conversation order`() {
        val records = listOf(
            FavoriteSessionRecord(sourceText = "Hi", sequence = 1),
            FavoriteSessionRecord(sourceText = "Good morning", sequence = 2),
            FavoriteSessionRecord(sourceText = "Nice weather", sequence = 3)
        )
        val session = FavoriteSession(id = "fs1", records = records)

        val sorted = session.records.sortedBy { it.sequence }
        assertEquals("Hi", sorted[0].sourceText)
        assertEquals("Good morning", sorted[1].sourceText)
        assertEquals("Nice weather", sorted[2].sourceText)
    }

    @Test
    fun `favorite session record stores language info`() {
        val record = FavoriteSessionRecord(
            sourceText = "Thank you",
            targetText = "Gracias",
            sourceLang = "en-US",
            targetLang = "es-ES",
            speaker = "A",
            direction = "A_to_B",
            sequence = 1
        )

        assertEquals("en-US", record.sourceLang)
        assertEquals("es-ES", record.targetLang)
        assertEquals("A_to_B", record.direction)
    }

    @Test
    fun `favorite session record default values`() {
        val record = FavoriteSessionRecord()
        assertEquals("", record.sourceText)
        assertEquals("", record.targetText)
        assertEquals("", record.sourceLang)
        assertEquals("", record.targetLang)
        assertEquals("", record.speaker)
        assertEquals("", record.direction)
        assertEquals(0, record.sequence)
    }

    @Test
    fun `favorite session with empty records list`() {
        val session = FavoriteSession(id = "fs1", sessionId = "s1", records = emptyList())
        assertTrue(session.records.isEmpty())
    }
}
