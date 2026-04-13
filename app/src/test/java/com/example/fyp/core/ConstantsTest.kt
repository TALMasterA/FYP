package com.example.fyp.core

import org.junit.Assert.*
import org.junit.Test

/**
 * Unit tests for centralized constants.
 *
 * Ensures constant values are within expected ranges and
 * haven't been accidentally changed.
 */
class ConstantsTest {

    // ── UiConstants ─────────────────────────────────────────────────

    @Test
    fun `error auto dismiss is 3 seconds`() {
        assertEquals(3000L, UiConstants.ERROR_AUTO_DISMISS_MS)
    }

    @Test
    fun `success message duration is 5 seconds`() {
        assertEquals(5000L, UiConstants.SUCCESS_MESSAGE_DURATION_MS)
    }

    @Test
    fun `password reset success duration is 5 seconds`() {
        assertEquals(5000L, UiConstants.PASSWORD_RESET_SUCCESS_DURATION_MS)
    }

    @Test
    fun `coin unlock success duration is 3 seconds`() {
        assertEquals(3000L, UiConstants.COIN_UNLOCK_SUCCESS_DURATION_MS)
    }

    @Test
    fun `speech delays are positive`() {
        assertTrue(UiConstants.SPEECH_PREPARE_DELAY_MS > 0)
        assertTrue(UiConstants.SPEECH_LISTENING_DEBOUNCE_MS > 0)
        assertTrue(UiConstants.TTS_FINISH_DELAY_MS > 0)
        assertTrue(UiConstants.TTS_START_DELAY_MS > 0)
        assertTrue(UiConstants.TTS_ERROR_WAIT_MS > 0)
    }

    // ── AiConfig ────────────────────────────────────────────────────

    @Test
    fun `default deployment is not blank`() {
        assertTrue(AiConfig.DEFAULT_DEPLOYMENT.isNotBlank())
    }

    @Test
    fun `AI generation timeout is positive`() {
        assertTrue(AiConfig.AI_GENERATION_TIMEOUT_MINUTES > 0)
    }

    // ── DataConstants ───────────────────────────────────────────────

    @Test
    fun `default history limit is positive`() {
        assertTrue(DataConstants.DEFAULT_HISTORY_LIMIT > 0)
    }

    @Test
    fun `count refresh debounce is 5 seconds`() {
        assertEquals(5000L, DataConstants.COUNT_REFRESH_DEBOUNCE_MS)
    }

    // ── GenerationConstants ─────────────────────────────────────────

    @Test
    fun `min records for regen is 20`() {
        assertEquals(20, GenerationConstants.MIN_RECORDS_FOR_REGEN)
    }

    @Test
    fun `min records for regen is positive`() {
        assertTrue(GenerationConstants.MIN_RECORDS_FOR_REGEN > 0)
    }
}
