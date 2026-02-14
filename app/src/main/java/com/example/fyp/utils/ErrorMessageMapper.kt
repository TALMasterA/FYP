package com.example.fyp.utils

import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.firestore.FirebaseFirestoreException
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
     * Maps a throwable to a user-friendly error message.
     *
     * @param throwable The exception to map
     * @return A sanitized, user-friendly error message
     */
    fun map(throwable: Throwable?): String {
        return when (throwable) {
            null -> "Unknown error occurred."

            // Network Errors
            is IOException, is SocketTimeoutException ->
                "Network connection failed. Please check your internet connection."

            is TimeoutException ->
                "The operation timed out. Please try again."

            // Cancellation
            is CancellationException ->
                "Operation was cancelled."

            // Firebase Auth Errors
            is FirebaseAuthException -> mapAuthError(throwable)

            // Firebase Firestore Errors
            is FirebaseFirestoreException -> mapFirestoreError(throwable)

            // Generic
            else -> "An unexpected error occurred. Please try again."
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
            errorMessage.contains("quota", ignoreCase = true) ->
                "Service temporarily unavailable. Please try again later."

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

    private fun mapAuthError(e: FirebaseAuthException): String {
        return when (e.errorCode) {
            "ERROR_INVALID_EMAIL" -> "The email address is badly formatted."
            "ERROR_WRONG_PASSWORD" -> "Invalid password."
            "ERROR_USER_NOT_FOUND" -> "No account found with this email."
            "ERROR_USER_DISABLED" -> "This account has been disabled."
            "ERROR_TOO_MANY_REQUESTS" -> "Too many requests. Please try again later."
            "ERROR_EMAIL_ALREADY_IN_USE" -> "The email address is already in use by another account."
            else -> "Authentication failed. Please try again."
        }
    }

    private fun mapFirestoreError(e: FirebaseFirestoreException): String {
        return when (e.code) {
            FirebaseFirestoreException.Code.PERMISSION_DENIED -> "You do not have permission to access this data."
            FirebaseFirestoreException.Code.UNAVAILABLE -> "Service currently unavailable. Please try again later."
            else -> "Database error. Please try again."
        }
    }
}

