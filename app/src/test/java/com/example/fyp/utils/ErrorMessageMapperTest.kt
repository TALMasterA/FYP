package com.example.fyp.utils

import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.firestore.FirebaseFirestoreException
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import java.io.IOException
import java.net.SocketTimeoutException
import java.util.concurrent.CancellationException
import java.util.concurrent.TimeoutException

/**
 * Unit tests for ErrorMessageMapper.
 *
 * Tests all map() branches and specialized error mappers.
 */
class ErrorMessageMapperTest {

    // ══════════════════════════════════════════════════════════════════
    // map(Throwable?)
    // ══════════════════════════════════════════════════════════════════

    @Test
    fun `map null returns unknown error`() {
        val result = ErrorMessageMapper.map(null)
        assertEquals("Unknown error occurred.", result)
    }

    @Test
    fun `map IOException returns network message`() {
        val result = ErrorMessageMapper.map(IOException("test"))
        assertTrue(result.contains("Network connection failed"))
    }

    @Test
    fun `map SocketTimeoutException returns network message`() {
        val result = ErrorMessageMapper.map(SocketTimeoutException("test"))
        assertTrue(result.contains("Network connection failed"))
    }

    @Test
    fun `map TimeoutException returns timeout message`() {
        val result = ErrorMessageMapper.map(TimeoutException("test"))
        assertTrue(result.contains("timed out"))
    }

    @Test
    fun `map CancellationException returns cancelled message`() {
        val result = ErrorMessageMapper.map(CancellationException("test"))
        assertTrue(result.contains("cancelled"))
    }

    @Test
    fun `map FirebaseAuthException invalid email returns formatted message`() {
        val exception = mock<FirebaseAuthException> {
            on { errorCode } doReturn "ERROR_INVALID_EMAIL"
        }

        val result = ErrorMessageMapper.map(exception)
        assertTrue(result.contains("email"))
        assertTrue(result.contains("badly formatted"))
    }

    @Test
    fun `map FirebaseAuthException wrong password returns safe message`() {
        val exception = mock<FirebaseAuthException> {
            on { errorCode } doReturn "ERROR_WRONG_PASSWORD"
        }

        val result = ErrorMessageMapper.map(exception)
        assertTrue(result.contains("Invalid password"))
    }

    @Test
    fun `map FirebaseAuthException user not found returns message`() {
        val exception = mock<FirebaseAuthException> {
            on { errorCode } doReturn "ERROR_USER_NOT_FOUND"
        }

        val result = ErrorMessageMapper.map(exception)
        assertTrue(result.contains("No account found"))
    }

    @Test
    fun `map FirebaseAuthException user disabled returns message`() {
        val exception = mock<FirebaseAuthException> {
            on { errorCode } doReturn "ERROR_USER_DISABLED"
        }

        val result = ErrorMessageMapper.map(exception)
        assertTrue(result.contains("disabled"))
    }

    @Test
    fun `map FirebaseAuthException too many requests returns message`() {
        val exception = mock<FirebaseAuthException> {
            on { errorCode } doReturn "ERROR_TOO_MANY_REQUESTS"
        }

        val result = ErrorMessageMapper.map(exception)
        assertTrue(result.contains("Too many requests"))
    }

    @Test
    fun `map FirebaseAuthException email already in use returns message`() {
        val exception = mock<FirebaseAuthException> {
            on { errorCode } doReturn "ERROR_EMAIL_ALREADY_IN_USE"
        }

        val result = ErrorMessageMapper.map(exception)
        assertTrue(result.contains("already in use"))
    }

    @Test
    fun `map FirebaseAuthException unknown code returns generic auth error`() {
        val exception = mock<FirebaseAuthException> {
            on { errorCode } doReturn "ERROR_SOME_UNKNOWN_CODE"
        }

        val result = ErrorMessageMapper.map(exception)
        assertEquals("Authentication failed. Please try again.", result)
    }

    @Test
    fun `map FirebaseFirestoreException permission denied`() {
        val exception = FirebaseFirestoreException(
            "Permission denied",
            FirebaseFirestoreException.Code.PERMISSION_DENIED
        )

        val result = ErrorMessageMapper.map(exception)
        assertTrue(result.contains("permission"))
    }

    @Test
    fun `map FirebaseFirestoreException unavailable`() {
        val exception = FirebaseFirestoreException(
            "Service unavailable",
            FirebaseFirestoreException.Code.UNAVAILABLE
        )

        val result = ErrorMessageMapper.map(exception)
        assertTrue(result.contains("unavailable"))
    }

    @Test
    fun `map FirebaseFirestoreException other code returns generic database error`() {
        val exception = FirebaseFirestoreException(
            "Internal error",
            FirebaseFirestoreException.Code.INTERNAL
        )

        val result = ErrorMessageMapper.map(exception)
        assertEquals("Database error. Please try again.", result)
    }

    @Test
    fun `map unknown exception returns generic error`() {
        val result = ErrorMessageMapper.map(IllegalArgumentException("test"))
        assertEquals("An unexpected error occurred. Please try again.", result)
    }

    // ══════════════════════════════════════════════════════════════════
    // mapSpeechRecognitionError
    // ══════════════════════════════════════════════════════════════════

    @Test
    fun `mapSpeechRecognitionError network keyword`() {
        val result = ErrorMessageMapper.mapSpeechRecognitionError("network timeout occurred")
        assertTrue(result.contains("Network connection failed"))
    }

    @Test
    fun `mapSpeechRecognitionError connection keyword`() {
        val result = ErrorMessageMapper.mapSpeechRecognitionError("Connection refused")
        assertTrue(result.contains("Network connection failed"))
    }

    @Test
    fun `mapSpeechRecognitionError timeout keyword`() {
        val result = ErrorMessageMapper.mapSpeechRecognitionError("Operation timeout")
        assertTrue(result.contains("timed out"))
    }

    @Test
    fun `mapSpeechRecognitionError NoMatch keyword`() {
        val result = ErrorMessageMapper.mapSpeechRecognitionError("Speech result: NoMatch")
        assertTrue(result.contains("No speech detected"))
    }

    @Test
    fun `mapSpeechRecognitionError BadRequest keyword`() {
        val result = ErrorMessageMapper.mapSpeechRecognitionError("BadRequest received")
        assertTrue(result.contains("Invalid request"))
    }

    @Test
    fun `mapSpeechRecognitionError Forbidden keyword`() {
        val result = ErrorMessageMapper.mapSpeechRecognitionError("Forbidden access")
        assertTrue(result.contains("Authentication failed"))
    }

    @Test
    fun `mapSpeechRecognitionError Unauthorized keyword`() {
        val result = ErrorMessageMapper.mapSpeechRecognitionError("Unauthorized request")
        assertTrue(result.contains("Authentication failed"))
    }

    @Test
    fun `mapSpeechRecognitionError unknown error`() {
        val result = ErrorMessageMapper.mapSpeechRecognitionError("Something went wrong")
        assertEquals("Speech recognition failed. Please try again.", result)
    }

    // ══════════════════════════════════════════════════════════════════
    // mapTranslationError
    // ══════════════════════════════════════════════════════════════════

    @Test
    fun `mapTranslationError network keyword`() {
        val result = ErrorMessageMapper.mapTranslationError("Network unreachable")
        assertTrue(result.contains("Network connection failed"))
    }

    @Test
    fun `mapTranslationError timeout keyword`() {
        val result = ErrorMessageMapper.mapTranslationError("Request timeout")
        assertTrue(result.contains("timed out"))
    }

    @Test
    fun `mapTranslationError rate limit keyword`() {
        val result = ErrorMessageMapper.mapTranslationError("Rate limit exceeded")
        assertTrue(result.contains("temporarily unavailable"))
    }

    @Test
    fun `mapTranslationError quota keyword`() {
        val result = ErrorMessageMapper.mapTranslationError("Quota exceeded")
        assertTrue(result.contains("temporarily unavailable"))
    }

    @Test
    fun `mapTranslationError invalid keyword`() {
        val result = ErrorMessageMapper.mapTranslationError("Invalid language pair")
        assertTrue(result.contains("Invalid input"))
    }

    @Test
    fun `mapTranslationError unknown error`() {
        val result = ErrorMessageMapper.mapTranslationError("Something else")
        assertEquals("Translation failed. Please try again.", result)
    }

    // ══════════════════════════════════════════════════════════════════
    // mapOcrError
    // ══════════════════════════════════════════════════════════════════

    @Test
    fun `mapOcrError no text keyword`() {
        val result = ErrorMessageMapper.mapOcrError("No text found in image")
        assertTrue(result.contains("No text detected"))
    }

    @Test
    fun `mapOcrError failed to load keyword`() {
        val result = ErrorMessageMapper.mapOcrError("Failed to load image from URI")
        assertTrue(result.contains("Failed to load image"))
    }

    @Test
    fun `mapOcrError unknown error`() {
        val result = ErrorMessageMapper.mapOcrError("Unexpected OCR issue")
        assertEquals("Image recognition failed. Please try again.", result)
    }

    // ══════════════════════════════════════════════════════════════════
    // Edge cases
    // ══════════════════════════════════════════════════════════════════

    @Test
    fun `map IOException subclass (UnknownHostException) returns network message`() {
        val result = ErrorMessageMapper.map(java.net.UnknownHostException("example.com"))
        assertTrue(result.contains("Network connection failed"))
    }

    @Test
    fun `map nested cause is NOT inspected - wrapping IOException in IllegalStateException`() {
        val wrapped = IllegalStateException("outer", IOException("inner"))
        val result = ErrorMessageMapper.map(wrapped)
        // Should return generic error because mapper checks throwable type, not cause
        assertEquals("An unexpected error occurred. Please try again.", result)
    }

    @Test
    fun `mapTranslationError connection keyword returns network message`() {
        val result = ErrorMessageMapper.mapTranslationError("Connection refused by server")
        assertTrue(result.contains("Network connection failed"))
    }

    @Test
    fun `mapSpeechRecognitionError combined keywords uses first match (network before timeout)`() {
        val result = ErrorMessageMapper.mapSpeechRecognitionError("network timeout error")
        // "network" matches first in the when clause
        assertTrue(result.contains("Network connection failed"))
    }

    @Test
    fun `map RuntimeException returns generic error`() {
        val result = ErrorMessageMapper.map(RuntimeException("crash"))
        assertEquals("An unexpected error occurred. Please try again.", result)
    }
}
