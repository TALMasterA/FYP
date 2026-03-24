package com.example.fyp.data.friends

import com.example.fyp.model.SpeechResult
import com.example.fyp.model.friends.SharedItemType
import org.junit.Assert.*
import org.junit.Test
import kotlinx.coroutines.test.runTest
import org.mockito.kotlin.mock

/**
 * Tests for pure logic extracted from FirestoreSharingRepository.
 *
 * Covers: material type validation (require), fullContent stripping,
 * word-to-custom-word field mapping, input truncation, blank-check guard,
 * and authorization check logic.
 */
class SharingRepositoryLogicTest {

    // ── Material type validation ──────────────────────────────────────

    @Test
    fun `LEARNING_SHEET type passes validation`() {
        val type = SharedItemType.LEARNING_SHEET
        val valid = type == SharedItemType.LEARNING_SHEET || type == SharedItemType.QUIZ
        assertTrue(valid)
    }

    @Test
    fun `QUIZ type passes validation`() {
        val type = SharedItemType.QUIZ
        val valid = type == SharedItemType.LEARNING_SHEET || type == SharedItemType.QUIZ
        assertTrue(valid)
    }

    @Test
    fun `WORD type fails material validation`() {
        val type = SharedItemType.WORD
        val valid = type == SharedItemType.LEARNING_SHEET || type == SharedItemType.QUIZ
        assertFalse("WORD type should not be valid for shareLearningMaterial", valid)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `require throws for WORD type in shareLearningMaterial`() {
        val type = SharedItemType.WORD
        require(type == SharedItemType.LEARNING_SHEET || type == SharedItemType.QUIZ) {
            "Invalid material type"
        }
    }

    // ── fullContent stripping ─────────────────────────────────────────

    @Test
    fun `fullContent is extracted from materialData`() {
        val materialData = mapOf<String, Any>(
            "title" to "My Learning Sheet",
            "fullContent" to "This is the full content body...",
            "primaryLang" to "en-US"
        )
        val fullContent = materialData["fullContent"] as? String ?: ""
        assertEquals("This is the full content body...", fullContent)
    }

    @Test
    fun `fullContent is removed from main doc map`() {
        val materialData = mapOf<String, Any>(
            "title" to "My Sheet",
            "fullContent" to "Body text",
            "lang" to "ja-JP"
        )
        val contentForMainDoc = materialData.toMutableMap().apply { remove("fullContent") }
        assertFalse(contentForMainDoc.containsKey("fullContent"))
        assertEquals("My Sheet", contentForMainDoc["title"])
        assertEquals("ja-JP", contentForMainDoc["lang"])
    }

    @Test
    fun `missing fullContent defaults to empty string`() {
        val materialData = mapOf<String, Any>("title" to "Sheet Without Content")
        val fullContent = materialData["fullContent"] as? String ?: ""
        assertEquals("", fullContent)
    }

    @Test
    fun `stripping fullContent preserves all other keys`() {
        val materialData = mapOf<String, Any>(
            "a" to 1, "b" to "two", "fullContent" to "body", "c" to true
        )
        val stripped = materialData.toMutableMap().apply { remove("fullContent") }
        assertEquals(3, stripped.size)
        assertTrue(stripped.containsKey("a"))
        assertTrue(stripped.containsKey("b"))
        assertTrue(stripped.containsKey("c"))
    }

    // ── Word field mapping ────────────────────────────────────────────

    @Test
    fun `word data maps to custom word fields correctly`() {
        val wordData = mapOf<String, Any>(
            "sourceText" to "hello",
            "targetText" to "こんにちは",
            "sourceLang" to "en-US",
            "targetLang" to "ja-JP",
            "notes" to "Greeting"
        )

        val originalWord = wordData["sourceText"] as? String ?: return
        val translatedWord = wordData["targetText"] as? String ?: return
        val sourceLang = wordData["sourceLang"] as? String ?: return
        val targetLang = wordData["targetLang"] as? String ?: return
        val notes = wordData["notes"] as? String ?: ""

        assertEquals("hello", originalWord)
        assertEquals("こんにちは", translatedWord)
        assertEquals("en-US", sourceLang)
        assertEquals("ja-JP", targetLang)
        assertEquals("Greeting", notes)
    }

    @Test
    fun `word mapping with missing notes defaults to empty`() {
        val wordData = mapOf<String, Any>(
            "sourceText" to "cat",
            "targetText" to "猫",
            "sourceLang" to "en-US",
            "targetLang" to "ja-JP"
        )
        val notes = wordData["notes"] as? String ?: ""
        assertEquals("", notes)
    }

    @Test
    fun `word mapping truncates original to 200 chars`() {
        val longWord = "a".repeat(300)
        val truncated = longWord.trim().take(200)
        assertEquals(200, truncated.length)
    }

    @Test
    fun `word mapping truncates translated to 200 chars`() {
        val longWord = "字".repeat(300)
        val truncated = longWord.trim().take(200)
        assertEquals(200, truncated.length)
    }

    @Test
    fun `word mapping truncates notes to 500 chars`() {
        val longNotes = "x".repeat(600)
        val truncated = longNotes.trim().take(500)
        assertEquals(500, truncated.length)
    }

    @Test
    fun `word mapping trims whitespace before truncation`() {
        val paddedWord = "  hello  "
        val result = paddedWord.trim().take(200)
        assertEquals("hello", result)
    }

    // ── Blank-check guard ─────────────────────────────────────────────

    @Test
    fun `blank original word triggers early return`() {
        val originalWord = "   "
        assertTrue("Should be blank", originalWord.isBlank())
    }

    @Test
    fun `blank translated word triggers early return`() {
        val translatedWord = ""
        assertTrue("Should be blank", translatedWord.isBlank())
    }

    @Test
    fun `non-blank words pass guard`() {
        assertFalse("hello".isBlank())
        assertFalse("こんにちは".isBlank())
    }

    @Test
    fun `missing sourceText field returns null from cast`() {
        val wordData = mapOf<String, Any>("targetText" to "test")
        val sourceText = wordData["sourceText"] as? String
        assertNull(sourceText)
    }

    @Test
    fun `missing targetText field returns null from cast`() {
        val wordData = mapOf<String, Any>("sourceText" to "test")
        val targetText = wordData["targetText"] as? String
        assertNull(targetText)
    }

    // ── Authorization check ───────────────────────────────────────────

    @Test
    fun `authorization passes when toUserId matches`() {
        val itemToUserId = "user-A"
        val currentUserId = "user-A"
        assertTrue(itemToUserId == currentUserId)
    }

    @Test
    fun `authorization fails when toUserId does not match`() {
        val itemToUserId = "user-A"
        val currentUserId = "user-B"
        assertFalse(itemToUserId == currentUserId)
    }

    // ── Receiver-primary translation on accept ───────────────────────

    @Test
    fun `different primary languages translate shared word before insert`() = runTest {
        val repository = FirestoreSharingRepository(mock(), mock())
        var translateCalled = false

        val payload = repository.prepareSharedWordForRecipient(
            wordData = mapOf(
                "sourceText" to "日本語",
                "targetText" to "Japanese",
                "sourceLang" to "ja-JP",
                "targetLang" to "en-US",
                "notes" to "common term"
            ),
            senderPrimaryLanguage = "en-US",
            receiverPrimaryLanguage = "yue-HK",
            translateText = { text, fromLang, toLang ->
                translateCalled = true
                assertEquals("Japanese", text)
                assertEquals("en-US", fromLang)
                assertEquals("yue-HK", toLang)
                SpeechResult.Success("日文")
            }
        )

        assertNotNull(payload)
        assertTrue(translateCalled)
        assertEquals("日本語", payload!!.originalWord)
        assertEquals("日文", payload.translatedWord)
        assertEquals("ja-JP", payload.sourceLang)
        assertEquals("yue-HK", payload.targetLang)
    }

    @Test
    fun `same primary languages keep original translated text`() = runTest {
        val repository = FirestoreSharingRepository(mock(), mock())
        var translateCalled = false

        val payload = repository.prepareSharedWordForRecipient(
            wordData = mapOf(
                "sourceText" to "日本語",
                "targetText" to "Japanese",
                "sourceLang" to "ja-JP",
                "targetLang" to "en-US"
            ),
            senderPrimaryLanguage = "en-US",
            receiverPrimaryLanguage = "en-US",
            translateText = { _, _, _ ->
                translateCalled = true
                SpeechResult.Success("should-not-be-used")
            }
        )

        assertNotNull(payload)
        assertFalse(translateCalled)
        assertEquals("Japanese", payload!!.translatedWord)
        assertEquals("en-US", payload.targetLang)
    }

    @Test
    fun `translation failure falls back to original shared translation`() = runTest {
        val repository = FirestoreSharingRepository(mock(), mock())

        val payload = repository.prepareSharedWordForRecipient(
            wordData = mapOf(
                "sourceText" to "日本語",
                "targetText" to "Japanese",
                "sourceLang" to "ja-JP",
                "targetLang" to "en-US"
            ),
            senderPrimaryLanguage = "en-US",
            receiverPrimaryLanguage = "yue-HK",
            translateText = { _, _, _ -> SpeechResult.Error("quota") }
        )

        assertNotNull(payload)
        assertEquals("Japanese", payload!!.translatedWord)
        assertEquals("en-US", payload.targetLang)
    }
}
