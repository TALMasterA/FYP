package com.example.fyp.core.security

/**
 * Security validation utilities for input sanitization and validation.
 * Helps prevent injection attacks and ensures data integrity.
 */

/**
 * Input validation result.
 */
sealed class ValidationResult {
    data object Valid : ValidationResult()
    data class Invalid(val message: String) : ValidationResult()
}

/**
 * Validates email format.
 */
fun validateEmail(email: String): ValidationResult {
    if (email.isBlank()) {
        return ValidationResult.Invalid("Email cannot be empty")
    }

    val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()
    return if (emailRegex.matches(email)) {
        ValidationResult.Valid
    } else {
        ValidationResult.Invalid("Invalid email format")
    }
}

/**
 * Validates username format.
 * Allows alphanumeric characters, underscores, and hyphens.
 */
fun validateUsername(username: String, minLength: Int = 3, maxLength: Int = 20): ValidationResult {
    if (username.length < minLength) {
        return ValidationResult.Invalid("Username must be at least $minLength characters")
    }

    if (username.length > maxLength) {
        return ValidationResult.Invalid("Username must not exceed $maxLength characters")
    }

    val usernameRegex = "^[a-zA-Z0-9_]+$".toRegex()
    return if (usernameRegex.matches(username)) {
        ValidationResult.Valid
    } else {
        ValidationResult.Invalid("Username can only contain letters, numbers, and underscores")
    }
}

/**
 * Validates password minimum-strength requirements.
 *
 * Currently enforces minimum length (matching Firebase Auth's own minimum).
 * The trimmed password is checked to prevent passwords that are only whitespace.
 */
fun validatePassword(password: String, minLength: Int = 6): ValidationResult {
    val trimmed = password.trim()
    if (trimmed.length < minLength) {
        return ValidationResult.Invalid("Password must be at least $minLength characters")
    }
    return ValidationResult.Valid
}

/**
 * Sanitizes user input to prevent XSS and injection attacks.
 * Removes potentially dangerous characters and HTML tags.
 */
fun sanitizeInput(input: String): String {
    return input
        .replace("&", "&amp;")   // Ampersand MUST be first to avoid double-encoding
        .replace("<", "&lt;")
        .replace(">", "&gt;")
        .replace("\"", "&quot;")
        .replace("'", "&#x27;")
        .replace("/", "&#x2F;")
        .trim()
}

/**
 * Validates text length within bounds.
 */
fun validateTextLength(
    text: String,
    minLength: Int = 0,
    maxLength: Int = Int.MAX_VALUE,
    fieldName: String = "Text"
): ValidationResult {
    if (text.length < minLength) {
        return ValidationResult.Invalid("$fieldName must be at least $minLength characters")
    }

    if (text.length > maxLength) {
        return ValidationResult.Invalid("$fieldName must not exceed $maxLength characters")
    }

    return ValidationResult.Valid
}

/**
 * Rate limiter to prevent abuse of sensitive operations.
 * Tracks operation attempts and enforces cooldown periods.
 * Bounded to [maxKeys] tracked keys to prevent unbounded memory growth.
 */
class RateLimiter(
    private val maxAttempts: Int = 5,
    private val windowMillis: Long = 60 * 1000L, // 1 minute
    private val maxKeys: Int = 10_000
) {
    private data class AttemptRecord(
        val attempts: MutableList<Long> = mutableListOf()
    )

    private val records = mutableMapOf<String, AttemptRecord>()

    /**
     * Checks if an operation is allowed for the given key (e.g., user ID, IP).
     * Returns true if allowed, false if rate limit exceeded.
     */
    fun isAllowed(key: String): Boolean {
        val now = System.currentTimeMillis()
        pruneStaleKeys(now)
        val record = records.getOrPut(key) { AttemptRecord() }

        // Remove old attempts outside the window
        record.attempts.removeAll { now - it > windowMillis }

        // Check if under limit
        return if (record.attempts.size < maxAttempts) {
            record.attempts.add(now)
            true
        } else {
            false
        }
    }

    /**
     * Gets remaining attempts for the key.
     */
    fun getRemainingAttempts(key: String): Int {
        val now = System.currentTimeMillis()
        val record = records[key] ?: return maxAttempts

        // Remove old attempts
        record.attempts.removeAll { now - it > windowMillis }

        return maxAttempts - record.attempts.size
    }

    /**
     * Resets the rate limit for a key.
     */
    fun reset(key: String) {
        records.remove(key)
    }

    /**
     * Clears all records.
     */
    fun clear() {
        records.clear()
    }

    /**
     * Removes keys whose attempts are all outside the current window,
     * preventing unbounded memory growth from one-time callers.
     */
    private fun pruneStaleKeys(now: Long) {
        if (records.size <= maxKeys) return
        val iterator = records.iterator()
        while (iterator.hasNext()) {
            val entry = iterator.next()
            entry.value.attempts.removeAll { now - it > windowMillis }
            if (entry.value.attempts.isEmpty()) iterator.remove()
        }
    }
}
