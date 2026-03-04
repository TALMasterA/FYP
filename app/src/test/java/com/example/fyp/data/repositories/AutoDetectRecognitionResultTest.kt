package com.example.fyp.data.repositories

import org.junit.Assert.*
import org.junit.Test

/**
 * Unit tests for AutoDetectRecognitionResult data class.
 */
class AutoDetectRecognitionResultTest {

    @Test
    fun `constructor sets text and detectedLanguage`() {
        val result = AutoDetectRecognitionResult(text = "Hello", detectedLanguage = "en-US")

        assertEquals("Hello", result.text)
        assertEquals("en-US", result.detectedLanguage)
    }

    @Test
    fun `handles empty text`() {
        val result = AutoDetectRecognitionResult(text = "", detectedLanguage = "en-US")

        assertEquals("", result.text)
    }

    @Test
    fun `handles empty language`() {
        val result = AutoDetectRecognitionResult(text = "Test", detectedLanguage = "")

        assertEquals("", result.detectedLanguage)
    }

    @Test
    fun `equality works by value`() {
        val r1 = AutoDetectRecognitionResult("Hello", "en-US")
        val r2 = AutoDetectRecognitionResult("Hello", "en-US")

        assertEquals(r1, r2)
    }

    @Test
    fun `inequality on different text`() {
        val r1 = AutoDetectRecognitionResult("Hello", "en-US")
        val r2 = AutoDetectRecognitionResult("Hola", "en-US")

        assertNotEquals(r1, r2)
    }

    @Test
    fun `inequality on different language`() {
        val r1 = AutoDetectRecognitionResult("Hello", "en-US")
        val r2 = AutoDetectRecognitionResult("Hello", "ja")

        assertNotEquals(r1, r2)
    }

    @Test
    fun `copy works correctly`() {
        val original = AutoDetectRecognitionResult("Hello", "en-US")
        val copy = original.copy(detectedLanguage = "ja")

        assertEquals("Hello", copy.text)
        assertEquals("ja", copy.detectedLanguage)
    }

    @Test
    fun `handles unicode text`() {
        val result = AutoDetectRecognitionResult(text = "こんにちは世界", detectedLanguage = "ja")

        assertEquals("こんにちは世界", result.text)
    }
}
