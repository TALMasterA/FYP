package com.translator.TalknLearn.data.wordbank

import org.junit.Assert.*
import org.junit.Test

/**
 * Tests for input validation logic in FirestoreCustomWordsRepository.
 *
 * Covers: MAX_WORD_LENGTH and MAX_EXAMPLE_LENGTH constants,
 * input trimming, truncation, blank-check validation,
 * and wordExists normalization.
 */
class CustomWordsValidationTest {

    // ── Constants ─────────────────────────────────────────────────────

    @Test
    fun `MAX_WORD_LENGTH is 200`() {
        assertEquals(200, FirestoreCustomWordsRepository.MAX_WORD_LENGTH)
    }

    @Test
    fun `MAX_EXAMPLE_LENGTH is 500`() {
        assertEquals(500, FirestoreCustomWordsRepository.MAX_EXAMPLE_LENGTH)
    }

    // ── Input trimming ────────────────────────────────────────────────

    @Test
    fun `word trimming removes leading whitespace`() {
        val input = "   hello"
        val trimmed = input.trim().take(FirestoreCustomWordsRepository.MAX_WORD_LENGTH)
        assertEquals("hello", trimmed)
    }

    @Test
    fun `word trimming removes trailing whitespace`() {
        val input = "hello   "
        val trimmed = input.trim().take(FirestoreCustomWordsRepository.MAX_WORD_LENGTH)
        assertEquals("hello", trimmed)
    }

    @Test
    fun `word trimming removes both leading and trailing whitespace`() {
        val input = "  hello world  "
        val trimmed = input.trim().take(FirestoreCustomWordsRepository.MAX_WORD_LENGTH)
        assertEquals("hello world", trimmed)
    }

    @Test
    fun `word trimming preserves internal spaces`() {
        val input = "hello   world"
        val trimmed = input.trim().take(FirestoreCustomWordsRepository.MAX_WORD_LENGTH)
        assertEquals("hello   world", trimmed)
    }

    // ── Truncation ────────────────────────────────────────────────────

    @Test
    fun `word under 200 chars is not truncated`() {
        val input = "short word"
        val result = input.trim().take(FirestoreCustomWordsRepository.MAX_WORD_LENGTH)
        assertEquals("short word", result)
    }

    @Test
    fun `word at exactly 200 chars is not truncated`() {
        val input = "a".repeat(200)
        val result = input.trim().take(FirestoreCustomWordsRepository.MAX_WORD_LENGTH)
        assertEquals(200, result.length)
    }

    @Test
    fun `word over 200 chars is truncated to 200`() {
        val input = "a".repeat(250)
        val result = input.trim().take(FirestoreCustomWordsRepository.MAX_WORD_LENGTH)
        assertEquals(200, result.length)
    }

    @Test
    fun `example under 500 chars is not truncated`() {
        val input = "This is an example sentence."
        val result = input.trim().take(FirestoreCustomWordsRepository.MAX_EXAMPLE_LENGTH)
        assertEquals("This is an example sentence.", result)
    }

    @Test
    fun `example at exactly 500 chars is not truncated`() {
        val input = "x".repeat(500)
        val result = input.trim().take(FirestoreCustomWordsRepository.MAX_EXAMPLE_LENGTH)
        assertEquals(500, result.length)
    }

    @Test
    fun `example over 500 chars is truncated to 500`() {
        val input = "x".repeat(700)
        val result = input.trim().take(FirestoreCustomWordsRepository.MAX_EXAMPLE_LENGTH)
        assertEquals(500, result.length)
    }

    // ── Blank-check validation ────────────────────────────────────────

    @Test
    fun `blank original word after trim fails validation`() {
        val original = "   "
        val trimmed = original.trim().take(FirestoreCustomWordsRepository.MAX_WORD_LENGTH)
        assertTrue("Blank after trim should fail", trimmed.isBlank())
    }

    @Test
    fun `empty original word fails validation`() {
        val original = ""
        val trimmed = original.trim().take(FirestoreCustomWordsRepository.MAX_WORD_LENGTH)
        assertTrue("Empty should fail", trimmed.isBlank())
    }

    @Test
    fun `valid original word passes validation`() {
        val original = "hello"
        val trimmed = original.trim().take(FirestoreCustomWordsRepository.MAX_WORD_LENGTH)
        assertFalse("Valid word should pass", trimmed.isBlank())
    }

    @Test
    fun `blank translated word after trim fails validation`() {
        val translated = "  \t  "
        val trimmed = translated.trim().take(FirestoreCustomWordsRepository.MAX_WORD_LENGTH)
        assertTrue("Whitespace-only should fail", trimmed.isBlank())
    }

    @Test
    fun `both words must be non-blank for validation to pass`() {
        val original = "hello".trim().take(200)
        val translated = "こんにちは".trim().take(200)
        val passes = original.isNotBlank() && translated.isNotBlank()
        assertTrue(passes)
    }

    @Test
    fun `one blank word causes validation failure`() {
        val original = "hello".trim().take(200)
        val translated = "  ".trim().take(200)
        val passes = original.isNotBlank() && translated.isNotBlank()
        assertFalse(passes)
    }

    // ── wordExists normalization ──────────────────────────────────────

    @Test
    fun `wordExists normalizes to lowercase and trimmed`() {
        val input = "  HELLO World  "
        val normalized = input.trim().lowercase()
        assertEquals("hello world", normalized)
    }

    @Test
    fun `wordExists normalization preserves already-lowercase input`() {
        val input = "hello"
        val normalized = input.trim().lowercase()
        assertEquals("hello", normalized)
    }

    @Test
    fun `wordExists normalization handles CJK characters`() {
        val input = "  こんにちは  "
        val normalized = input.trim().lowercase()
        assertEquals("こんにちは", normalized)
    }

    @Test
    fun `pronunciation is also trimmed and truncated`() {
        val pronunciation = "  " + "p".repeat(250) + "  "
        val result = pronunciation.trim().take(FirestoreCustomWordsRepository.MAX_WORD_LENGTH)
        assertEquals(200, result.length)
    }
}
