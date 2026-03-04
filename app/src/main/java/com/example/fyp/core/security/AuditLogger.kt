package com.example.fyp.core.security

import com.example.fyp.core.AppLogger
import com.google.firebase.crashlytics.FirebaseCrashlytics

/**
 * Audit logger for security-relevant events.
 * Records authentication attempts, rate-limit violations,
 * and other events that may indicate misuse or attacks.
 *
 * Events are logged locally via [AppLogger] and reported
 * to Firebase Crashlytics as custom keys for monitoring.
 */
object AuditLogger {

    private const val TAG = "Audit"

    /** Categories of auditable security events. */
    enum class EventType {
        LOGIN_FAILED,
        LOGIN_SUCCESS,
        RATE_LIMIT_EXCEEDED,
        INVALID_INPUT,
        ACCOUNT_DELETED,
        PASSWORD_RESET_REQUESTED,
        FRIEND_REQUEST_BLOCKED,
        SUSPICIOUS_ACTIVITY,
        SEND_FRIEND_REQUEST,
        ACCEPT_FRIEND_REQUEST,
        REJECT_FRIEND_REQUEST,
        CANCEL_FRIEND_REQUEST,
        REMOVE_FRIEND,
        BLOCK_USER,
        UNBLOCK_USER,
        SEND_MESSAGE,
        DELETE_CHAT,
        PERMISSION_DENIED
    }

    /**
     * Logs a security-relevant event.
     *
     * @param eventType The category of the event
     * @param userId Optional user identifier (null for anonymous events)
     * @param details Human-readable description of what happened
     */
    fun log(eventType: EventType, userId: String? = null, details: String = "") {
        val message = buildString {
            append("[${eventType.name}]")
            if (userId != null) append(" user=$userId")
            if (details.isNotEmpty()) append(" $details")
        }

        AppLogger.i(TAG, message)

        try {
            val crashlytics = FirebaseCrashlytics.getInstance()
            crashlytics.log("AUDIT: $message")
            crashlytics.setCustomKey("last_audit_event", eventType.name)
            if (userId != null) {
                crashlytics.setCustomKey("last_audit_user", userId)
            }
        } catch (_: Exception) {
            // Crashlytics not yet initialized
        }
    }

    /**
     * Logs a failed login attempt.
     *
     * @param email The email used in the attempt
     * @param reason The failure reason
     */
    fun logLoginFailed(email: String, reason: String) {
        log(EventType.LOGIN_FAILED, details = "email=$email reason=$reason")
    }

    /**
     * Logs a successful login.
     *
     * @param userId The authenticated user's ID
     */
    fun logLoginSuccess(userId: String) {
        log(EventType.LOGIN_SUCCESS, userId = userId)
    }

    /**
     * Logs a rate-limit violation.
     *
     * @param userId The user who exceeded the limit
     * @param operation The operation that was rate-limited
     */
    fun logRateLimitExceeded(userId: String, operation: String) {
        log(EventType.RATE_LIMIT_EXCEEDED, userId = userId, details = "operation=$operation")
    }

    /**
     * Logs an invalid input attempt (potential injection or abuse).
     *
     * @param userId The user who sent the input
     * @param field The field name that failed validation
     * @param reason The validation failure reason
     */
    fun logInvalidInput(userId: String? = null, field: String, reason: String) {
        log(EventType.INVALID_INPUT, userId = userId, details = "field=$field reason=$reason")
    }

    /**
     * Logs an account deletion event.
     *
     * @param userId The user whose account was deleted
     */
    fun logAccountDeleted(userId: String) {
        log(EventType.ACCOUNT_DELETED, userId = userId)
    }

    /**
     * Logs a password reset request.
     *
     * @param email The email for which reset was requested
     */
    fun logPasswordResetRequested(email: String) {
        log(EventType.PASSWORD_RESET_REQUESTED, details = "email=$email")
    }

    // FIX 5.4: Friend-system audit helpers

    /** Logs a friend request being sent. */
    fun logFriendRequestSent(userId: String, targetUserId: String) {
        log(EventType.SEND_FRIEND_REQUEST, userId = userId, details = "target=$targetUserId")
    }

    /** Logs a friend request acceptance. */
    fun logFriendRequestAccepted(userId: String, fromUserId: String) {
        log(EventType.ACCEPT_FRIEND_REQUEST, userId = userId, details = "from=$fromUserId")
    }

    /** Logs a friend request rejection. */
    fun logFriendRequestRejected(userId: String, fromUserId: String) {
        log(EventType.REJECT_FRIEND_REQUEST, userId = userId, details = "from=$fromUserId")
    }

    /** Logs a friend removal. */
    fun logFriendRemoved(userId: String, friendId: String) {
        log(EventType.REMOVE_FRIEND, userId = userId, details = "friend=$friendId")
    }

    /** Logs a user being blocked. */
    fun logUserBlocked(userId: String, blockedId: String) {
        log(EventType.BLOCK_USER, userId = userId, details = "blocked=$blockedId")
    }

    /** Logs a user being unblocked. */
    fun logUserUnblocked(userId: String, unblockedId: String) {
        log(EventType.UNBLOCK_USER, userId = userId, details = "unblocked=$unblockedId")
    }

    /** Logs a permission denied event. */
    fun logPermissionDenied(userId: String? = null, action: String) {
        log(EventType.PERMISSION_DENIED, userId = userId, details = "action=$action")
    }
}
