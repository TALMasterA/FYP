package com.translator.TalknLearn.utils

import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.firestore.FirebaseFirestoreException
import com.translator.TalknLearn.model.ui.BaseUiTexts
import com.translator.TalknLearn.model.ui.UiTextKey
import java.io.IOException
import java.net.SocketTimeoutException
import java.util.concurrent.CancellationException
import java.util.concurrent.TimeoutException

/**
 * Maps exceptions to user-friendly error messages.
 * Prevents exposing internal technical details to the end user.
 *
 * This utility sanitizes error messages to avoid information disclosure while
 * providing meaningful feedback to users.
 */
object ErrorMessageMapper {

    /**
     * Maps a throwable to a user-friendly error message in English.
     *
     * Screens should prefer [mapKey], which gives a key the screen shows in
     * the user's UI language. This English form is the same text.
     *
     * @param throwable The exception to map
     * @return A sanitized, user-friendly error message
     */
    fun map(throwable: Throwable?): String = BaseUiTexts[mapKey(throwable).ordinal]

    /**
     * Maps a throwable to the [UiTextKey] of a user-friendly error message.
     *
     * @param throwable The exception to map
     * @return The key of a sanitized, user-friendly error message
     */
    fun mapKey(throwable: Throwable?): UiTextKey {
        return when (throwable) {
            null -> UiTextKey.ErrUnknown

            // Network Errors
            is IOException, is SocketTimeoutException -> UiTextKey.ErrNetworkFailed

            is TimeoutException -> UiTextKey.ErrOperationTimedOut

            // Cancellation
            is CancellationException -> UiTextKey.ErrCancelled

            // Firebase Auth Errors
            is FirebaseAuthException -> mapAuthErrorKey(throwable)

            // Firebase Firestore Errors
            is FirebaseFirestoreException -> mapFirestoreErrorKey(throwable)

            // Generic
            else -> UiTextKey.ErrUnexpected
        }
    }

    /**
     * Sanitizes speech recognition error messages.
     *
     * @param errorDetails The raw error details from Azure Speech SDK
     * @return A user-friendly error message
     */
    fun mapSpeechRecognitionError(errorDetails: String): String {
        return when {
            errorDetails.contains("network", ignoreCase = true) ||
            errorDetails.contains("connection", ignoreCase = true) ->
                "Network connection failed. Please check your internet connection."

            errorDetails.contains("timeout", ignoreCase = true) ->
                "Recognition timed out. Please try again."

            errorDetails.contains("NoMatch", ignoreCase = true) ->
                "No speech detected. Please speak clearly and try again."

            errorDetails.contains("BadRequest", ignoreCase = true) ->
                "Invalid request. Please check your settings."

            errorDetails.contains("Forbidden", ignoreCase = true) ||
            errorDetails.contains("Unauthorized", ignoreCase = true) ->
                "Authentication failed. Please try again."

            else ->
                "Speech recognition failed. Please try again."
        }
    }

    /**
     * Sanitizes translation error messages.
     *
     * @param errorMessage The raw error message from translation service
     * @return A user-friendly error message
     */
    fun mapTranslationError(errorMessage: String): String {
        return when {
            errorMessage.contains("network", ignoreCase = true) ||
            errorMessage.contains("connection", ignoreCase = true) ->
                "Network connection failed. Please check your internet connection."

            errorMessage.contains("timeout", ignoreCase = true) ->
                "Translation timed out. Please try again."

            errorMessage.contains("rate limit", ignoreCase = true) ||
            errorMessage.contains("quota", ignoreCase = true) ||
            errorMessage.contains("resource-exhausted", ignoreCase = true) ||
            errorMessage.contains("429") ->
                "Service temporarily unavailable. Please try again later."

            errorMessage.contains("unauthenticated", ignoreCase = true) ||
            errorMessage.contains("unauthorized", ignoreCase = true) ||
            errorMessage.contains("permission", ignoreCase = true) ->
                "Translation service unavailable. Please restart the app and try again."

            errorMessage.contains("invalid", ignoreCase = true) ->
                "Invalid input. Please check your text and try again."

            else ->
                "Translation failed. Please try again."
        }
    }

    /**
     * Sanitizes OCR error messages.
     *
     * @param errorMessage The raw error message from OCR service
     * @return A user-friendly error message
     */
    fun mapOcrError(errorMessage: String): String {
        return when {
            errorMessage.contains("no text", ignoreCase = true) ->
                "No text detected in image. Please try a clearer image."

            errorMessage.contains("failed to load", ignoreCase = true) ->
                "Failed to load image. Please try again."

            else ->
                "Image recognition failed. Please try again."
        }
    }

    private fun mapAuthErrorKey(e: FirebaseAuthException): UiTextKey {
        return when (e.errorCode) {
            "ERROR_INVALID_EMAIL" -> UiTextKey.ErrAuthInvalidEmail
            "ERROR_WRONG_PASSWORD" -> UiTextKey.ErrAuthWrongPassword
            "ERROR_USER_NOT_FOUND" -> UiTextKey.ErrAuthUserNotFound
            "ERROR_USER_DISABLED" -> UiTextKey.ErrAuthUserDisabled
            "ERROR_TOO_MANY_REQUESTS" -> UiTextKey.ErrAuthTooManyRequests
            "ERROR_EMAIL_ALREADY_IN_USE" -> UiTextKey.ErrAuthEmailInUse
            else -> UiTextKey.ErrAuthFailed
        }
    }

    private fun mapFirestoreErrorKey(e: FirebaseFirestoreException): UiTextKey {
        return when (e.code) {
            FirebaseFirestoreException.Code.PERMISSION_DENIED -> UiTextKey.ErrDbPermission
            FirebaseFirestoreException.Code.UNAVAILABLE -> UiTextKey.ErrDbUnavailable
            else -> UiTextKey.ErrDbGeneric
        }
    }
}
