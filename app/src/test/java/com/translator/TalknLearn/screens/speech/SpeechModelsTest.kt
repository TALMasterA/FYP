package com.translator.TalknLearn.screens.speech

import org.junit.Assert.*
import org.junit.Test

/**
 * Unit tests for SpeechScreenState, RecognizePhase, and ChatMessage data classes.
 *
 * Tests default values, copy semantics, enum coverage, and ChatMessage properties.
 */
class SpeechModelsTest {

    // ── SpeechScreenState defaults ─────────────────────────────────

    @Test
    fun `SpeechScreenState has correct defaults`() {
        val state = SpeechScreenState()
        assertEquals("", state.recognizedText)
        assertEquals("", state.translatedText)
        assertEquals("", state.ttsStatus)
        assertEquals("", state.statusMessage)
        assertFalse(state.isTtsRunning)
        assertEquals(RecognizePhase.Idle, state.recognizePhase)
    }

    @Test
    fun `SpeechScreenState copy updates specified fields only`() {
        val original = SpeechScreenState()
        val updated = original.copy(
            recognizedText = "Hello",
            recognizePhase = RecognizePhase.Listening
        )
        assertEquals("Hello", updated.recognizedText)
        assertEquals(RecognizePhase.Listening, updated.recognizePhase)
        // Unchanged fields
        assertEquals("", updated.translatedText)
        assertFalse(updated.isTtsRunning)
    }

    @Test
    fun `SpeechScreenState equality based on all fields`() {
        val a = SpeechScreenState(recognizedText = "Hi", isTtsRunning = true)
        val b = SpeechScreenState(recognizedText = "Hi", isTtsRunning = true)
        assertEquals(a, b)
    }

    @Test
    fun `SpeechScreenState inequality when field differs`() {
        val a = SpeechScreenState(recognizedText = "Hi")
        val b = SpeechScreenState(recognizedText = "Bye")
        assertNotEquals(a, b)
    }

    // ── RecognizePhase ─────────────────────────────────────────────

    @Test
    fun `RecognizePhase has exactly three values`() {
        val values = RecognizePhase.entries
        assertEquals(3, values.size)
    }

    @Test
    fun `RecognizePhase values are Idle, Preparing, Listening`() {
        val names = RecognizePhase.entries.map { it.name }
        assertTrue(names.contains("Idle"))
        assertTrue(names.contains("Preparing"))
        assertTrue(names.contains("Listening"))
    }

    @Test
    fun `RecognizePhase ordinals are sequential`() {
        assertEquals(0, RecognizePhase.Idle.ordinal)
        assertEquals(1, RecognizePhase.Preparing.ordinal)
        assertEquals(2, RecognizePhase.Listening.ordinal)
    }

    // ── ChatMessage ────────────────────────────────────────────────

    @Test
    fun `ChatMessage stores all fields`() {
        val msg = ChatMessage(
            id = 1L,
            text = "Hello",
            lang = "en-US",
            isFromPersonA = true,
            isTranslation = false
        )
        assertEquals(1L, msg.id)
        assertEquals("Hello", msg.text)
        assertEquals("en-US", msg.lang)
        assertTrue(msg.isFromPersonA)
        assertFalse(msg.isTranslation)
    }

    @Test
    fun `ChatMessage equality based on all fields`() {
        val a = ChatMessage(1L, "Hi", "en-US", true, false)
        val b = ChatMessage(1L, "Hi", "en-US", true, false)
        assertEquals(a, b)
    }

    @Test
    fun `ChatMessage inequality when id differs`() {
        val a = ChatMessage(1L, "Hi", "en-US", true, false)
        val b = ChatMessage(2L, "Hi", "en-US", true, false)
        assertNotEquals(a, b)
    }

    @Test
    fun `ChatMessage inequality when isFromPersonA differs`() {
        val a = ChatMessage(1L, "Hi", "en-US", true, false)
        val b = ChatMessage(1L, "Hi", "en-US", false, false)
        assertNotEquals(a, b)
    }

    @Test
    fun `ChatMessage for translation has isTranslation true`() {
        val msg = ChatMessage(2L, "Hola", "es-ES", false, true)
        assertTrue(msg.isTranslation)
    }

    @Test
    fun `ChatMessage copy preserves other fields`() {
        val original = ChatMessage(1L, "Hi", "en-US", true, false)
        val translated = original.copy(text = "Hola", lang = "es-ES", isTranslation = true)
        assertEquals(1L, translated.id)
        assertTrue(translated.isFromPersonA) // Preserved
        assertEquals("Hola", translated.text)
        assertTrue(translated.isTranslation)
    }
}

