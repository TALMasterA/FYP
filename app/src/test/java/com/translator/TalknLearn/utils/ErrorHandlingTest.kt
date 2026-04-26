package com.translator.TalknLearn.utils

import org.junit.Assert.*
import org.junit.Test
import java.io.IOException
import java.net.SocketTimeoutException
import java.util.concurrent.CancellationException
import java.util.concurrent.TimeoutException

/**
 * Comprehensive error handling tests for the app (item 7).
 *
 * Verifies:
 * - All error types are mapped to user-friendly messages
 * - No internal technical details leak to users
 * - Network, timeout, auth, and generic errors all handled
 * - Speech recognition errors properly mapped
 * - Translation errors properly mapped
 * - OCR errors properly mapped
 */
class ErrorHandlingTest {

    // ── Network Error Mapping ──────────────────────────────────────

    @Test
    fun `IOException maps to network error message`() {
        val result = ErrorMessageMapper.map(IOException("Connection reset"))
        assertTrue(result.contains("Network"))
        assertFalse("Should not expose technical details", result.contains("Connection reset"))
    }

    @Test
    fun `SocketTimeoutException maps to network error`() {
        val result = ErrorMessageMapper.map(SocketTimeoutException("timed out"))
        assertTrue(result.contains("Network"))
    }

    @Test
    fun `TimeoutException maps to timeout message`() {
        val result = ErrorMessageMapper.map(TimeoutException("operation timeout"))
        assertTrue(result.contains("timed out"))
    }

    @Test
    fun `CancellationException maps to cancelled message`() {
        val result = ErrorMessageMapper.map(CancellationException("cancelled"))
        assertTrue(result.contains("cancelled"))
    }

    // ── Null and Generic Error ─────────────────────────────────────

    @Test
    fun `null throwable returns unknown error`() {
        val result = ErrorMessageMapper.map(null)
        assertEquals("Unknown error occurred.", result)
    }

    @Test
    fun `generic exception returns unexpected error`() {
        val result = ErrorMessageMapper.map(RuntimeException("internal error"))
        assertTrue(result.contains("unexpected"))
        assertFalse("Should not expose internal details", result.contains("internal error"))
    }

    @Test
    fun `IllegalArgumentException returns unexpected error`() {
        val result = ErrorMessageMapper.map(IllegalArgumentException("bad arg"))
        assertTrue(result.contains("unexpected"))
    }

    // ── Speech Recognition Error Mapping ──────────────────────────

    @Test
    fun `speech network error mapped correctly`() {
        val result = ErrorMessageMapper.mapSpeechRecognitionError("connection failed to server")
        assertTrue(result.contains("Network"))
    }

    @Test
    fun `speech timeout error mapped correctly`() {
        val result = ErrorMessageMapper.mapSpeechRecognitionError("timeout reached")
        assertTrue(result.contains("timed out"))
    }

    @Test
    fun `speech NoMatch error mapped correctly`() {
        val result = ErrorMessageMapper.mapSpeechRecognitionError("NoMatch detected")
        assertTrue(result.contains("No speech detected"))
    }

    @Test
    fun `speech BadRequest error mapped correctly`() {
        val result = ErrorMessageMapper.mapSpeechRecognitionError("BadRequest: invalid params")
        assertTrue(result.contains("Invalid request"))
    }

    @Test
    fun `speech Forbidden error mapped correctly`() {
        val result = ErrorMessageMapper.mapSpeechRecognitionError("Forbidden: invalid key")
        assertTrue(result.contains("Authentication"))
    }

    @Test
    fun `speech Unauthorized error mapped correctly`() {
        val result = ErrorMessageMapper.mapSpeechRecognitionError("Unauthorized access")
        assertTrue(result.contains("Authentication"))
    }

    @Test
    fun `speech unknown error returns generic message`() {
        val result = ErrorMessageMapper.mapSpeechRecognitionError("xyz unknown issue")
        assertEquals("Speech recognition failed. Please try again.", result)
    }

    // ── Translation Error Mapping ──────────────────────────────────

    @Test
    fun `translation network error mapped correctly`() {
        val result = ErrorMessageMapper.mapTranslationError("network connection lost")
        assertTrue(result.contains("Network"))
    }

    @Test
    fun `translation timeout error mapped correctly`() {
        val result = ErrorMessageMapper.mapTranslationError("request timeout exceeded")
        assertTrue(result.contains("timed out"))
    }

    @Test
    fun `translation rate limit error mapped correctly`() {
        val result = ErrorMessageMapper.mapTranslationError("rate limit exceeded")
        assertTrue(result.contains("temporarily unavailable"))
    }

    @Test
    fun `translation quota error mapped correctly`() {
        val result = ErrorMessageMapper.mapTranslationError("quota exhausted")
        assertTrue(result.contains("temporarily unavailable"))
    }

    @Test
    fun `translation resource-exhausted error mapped correctly`() {
        val result = ErrorMessageMapper.mapTranslationError("FirebaseFunctionsException: resource-exhausted")
        assertTrue(result.contains("temporarily unavailable"))
    }

    @Test
    fun `translation invalid input error mapped correctly`() {
        val result = ErrorMessageMapper.mapTranslationError("invalid text input")
        assertTrue(result.contains("Invalid input"))
    }

    @Test
    fun `translation unknown error returns generic message`() {
        val result = ErrorMessageMapper.mapTranslationError("xyz error")
        assertEquals("Translation failed. Please try again.", result)
    }

    // ── OCR Error Mapping ─────────────────────────────────────────

    @Test
    fun `OCR no text error mapped correctly`() {
        val result = ErrorMessageMapper.mapOcrError("no text found in image")
        assertTrue(result.contains("No text detected"))
    }

    @Test
    fun `OCR failed to load error mapped correctly`() {
        val result = ErrorMessageMapper.mapOcrError("failed to load bitmap")
        assertTrue(result.contains("Failed to load image"))
    }

    @Test
    fun `OCR unknown error returns generic message`() {
        val result = ErrorMessageMapper.mapOcrError("xyz error")
        assertEquals("Image recognition failed. Please try again.", result)
    }

    // ── Information Disclosure Prevention ──────────────────────────

    @Test
    fun `no stack traces leak to user`() {
        val errors = listOf(
            ErrorMessageMapper.map(IOException("Internal Server Error at 192.168.1.1:8080")),
            ErrorMessageMapper.map(RuntimeException("NullPointerException at com.example.Class.method")),
            ErrorMessageMapper.map(null)
        )

        errors.forEach { message ->
            assertFalse("Should not contain IP addresses", message.contains("192.168"))
            assertFalse("Should not contain package names", message.contains("com.example"))
            assertFalse("Should not contain port numbers", message.contains("8080"))
        }
    }

    @Test
    fun `error messages are user-friendly`() {
        val messages = listOf(
            ErrorMessageMapper.map(IOException()),
            ErrorMessageMapper.map(TimeoutException()),
            ErrorMessageMapper.map(CancellationException()),
            ErrorMessageMapper.map(RuntimeException()),
            ErrorMessageMapper.map(null)
        )

        messages.forEach { message ->
            assertTrue(
                "Error message should end with period: $message",
                message.endsWith(".")
            )
            assertTrue(
                "Error message should be reasonable length: $message",
                message.length in 10..200
            )
        }
    }
}
