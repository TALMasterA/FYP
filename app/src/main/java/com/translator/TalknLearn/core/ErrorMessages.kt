package com.translator.TalknLearn.core

/**
 * FIX 3.6: Centralized error message mapper.
 *
 * Maps technical exceptions to user-friendly messages following a consistent
 * format: [What failed] + [Why] + [What to do].
 *
 * Usage:
 * ```
 * val message = ErrorMessages.fromException(e)
 * _errorState.value = message
 * ```
 */
object ErrorMessages {

    /**
     * Convert an exception into a user-friendly error message (English only).
     */
    fun fromException(e: Throwable?, fallback: String = "Something went wrong. Please try again."): String {
        if (e == null) return fallback

        return when {
            // Network errors
            isNetworkError(e) ->
                "No internet connection. Please check your network and try again."

            // Firebase auth errors
            e.message?.contains("PERMISSION_DENIED", ignoreCase = true) == true ->
                "You don't have permission to perform this action."
            e.message?.contains("UNAUTHENTICATED", ignoreCase = true) == true ->
                "Your session has expired. Please sign in again."
            e.message?.contains("NOT_FOUND", ignoreCase = true) == true ->
                "The requested item was not found. It may have been deleted."

            // Security exceptions (from our own checks)
            e is SecurityException ->
                e.message ?: "Access denied."

            // Friend-specific errors
            e.message?.contains("Already friends", ignoreCase = true) == true ->
                "You're already friends with this user."
            e.message?.contains("blocked", ignoreCase = true) == true ->
                "Unable to complete this action. The user may be blocked."
            e.message?.contains("Request not found", ignoreCase = true) == true ->
                "This friend request no longer exists."
            e.message?.contains("already been handled", ignoreCase = true) == true ->
                "This request has already been handled by someone else."
            e.message?.contains("Not authorized", ignoreCase = true) == true ->
                "You're not authorized to perform this action."

            // Rate limiting
            e.message?.contains("RESOURCE_EXHAUSTED", ignoreCase = true) == true ||
            e.message?.contains("rate limit", ignoreCase = true) == true ->
                "Too many requests. Please wait a moment and try again."

            // Validation errors
            e is IllegalArgumentException ->
                e.message ?: "Invalid input. Please check and try again."
            e is IllegalStateException ->
                e.message ?: "Operation cannot be completed right now."

            // Size/quota errors
            e.message?.contains("DEADLINE_EXCEEDED", ignoreCase = true) == true ->
                "The operation timed out. Please try again."

            // Default
            else -> fallback
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

    // ── Pre-built messages for common scenarios ─────────────────────────────

    const val FRIEND_REMOVED = "Friend removed successfully."
    const val FRIEND_REMOVE_FAILED = "Unable to remove friend. Please check your connection and try again."
    const val GENERIC_RETRY = "Something went wrong. Please try again."
}
