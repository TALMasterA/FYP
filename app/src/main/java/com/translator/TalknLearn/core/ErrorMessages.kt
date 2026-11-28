package com.translator.TalknLearn.core

import com.translator.TalknLearn.model.ui.StatusText
import com.translator.TalknLearn.model.ui.UiTextKey

/**
 * FIX 3.6: Centralized error message mapper.
 *
 * Maps technical exceptions to user-friendly messages following a consistent
 * format: [What failed] + [Why] + [What to do].
 *
 * [statusFromException] returns a [StatusText] that the screen shows in the
 * user's UI language. (The older English-only `fromException` was removed in
 * Phase 1 part 5, once every caller used [StatusText].)
 *
 * Usage:
 * ```
 * _uiState.update { it.copy(error = ErrorMessages.statusFromException(e, UiTextKey.ErrGeneric)) }
 * ```
 */
object ErrorMessages {

    /**
     * Convert an exception into a localizable message. Unknown errors use
     * [fallback].
     */
    fun statusFromException(e: Throwable?, fallback: UiTextKey = UiTextKey.ErrGeneric): StatusText =
        e?.let(::classify) ?: StatusText(fallback)

    @Suppress("CyclomaticComplexMethod")
    private fun classify(e: Throwable): StatusText? {
        val message = e.message
        fun has(text: String) = message?.contains(text, ignoreCase = true) == true

        return when {
            // Network errors
            isNetworkError(e) -> StatusText(UiTextKey.ErrNoInternet)

            // Firebase auth errors
            has("PERMISSION_DENIED") -> StatusText(UiTextKey.ErrPermissionDenied)
            has("UNAUTHENTICATED") -> StatusText(UiTextKey.ErrSessionExpired)
            has("NOT_FOUND") -> StatusText(UiTextKey.ErrItemNotFound)

            // Security exceptions (from our own checks)
            e is SecurityException -> message?.let(StatusText::raw) ?: StatusText(UiTextKey.ErrAccessDenied)

            // Friend-specific errors
            has("Already friends") -> StatusText(UiTextKey.ErrAlreadyFriends)
            has("blocked") -> StatusText(UiTextKey.ErrUserMaybeBlocked)
            has("Request not found") -> StatusText(UiTextKey.ErrRequestGone)
            has("already been handled") -> StatusText(UiTextKey.ErrRequestHandled)
            has("Not authorized") -> StatusText(UiTextKey.ErrNotAuthorized)

            // Rate limiting
            has("RESOURCE_EXHAUSTED") || has("rate limit") -> StatusText(UiTextKey.ErrTooManyRequests)

            // Validation errors
            e is IllegalArgumentException -> message?.let(StatusText::raw) ?: StatusText(UiTextKey.ErrInvalidInput)
            e is IllegalStateException -> message?.let(StatusText::raw) ?: StatusText(UiTextKey.ErrCannotCompleteNow)

            // Size/quota errors
            has("DEADLINE_EXCEEDED") -> StatusText(UiTextKey.ErrTimedOut)

            // Default
            else -> null
        }
    }

    /**
     * Check if the error is a network-related error.
     */
    fun isNetworkError(e: Throwable): Boolean {
        val message = e.message?.lowercase() ?: ""
        return e is java.net.UnknownHostException ||
                e is java.net.SocketTimeoutException ||
                e is java.io.IOException && message.contains("network") ||
                e is java.net.ConnectException ||
                message.contains("unavailable") ||
                message.contains("network") ||
                message.contains("timeout") ||
                message.contains("UNAVAILABLE")
    }
}
