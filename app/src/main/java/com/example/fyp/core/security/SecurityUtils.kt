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
 * Allows alphanumeric characters and underscores (no hyphens, spaces, or symbols).
 * The accepted character set is `^[a-zA-Z0-9_]+$`; this matches the
 * `firestore.rules` enforcement and the canonical regex documented in
 * `docs/ARCHITECTURE_NOTES.md` (§ Username — Format and Discoverability).
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
 * Enforces a trimmed-length minimum (default 8) so passwords made entirely of
 * whitespace are rejected. The default was raised from 6 to 8 as part of the
 * §2.1 sweep in `docs/APP_SUGGESTIONS.md`; complexity rules (mixed character
 * classes, common-password blocklist) are intentionally NOT enforced here
 * because they would require new user-facing error strings translated into
 * every supported UI language and a coordinated Firebase Auth password
 * policy update. Callers that need stricter validation should pass a higher
 * `minLength` or layer their own checks on top of this one.
 */
fun validatePassword(password: String, minLength: Int = 8): ValidationResult {
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
        val attempts: ArrayDeque<Long> = ArrayDeque()
    )

    /**
     * Access-order [LinkedHashMap] used as an LRU cache: every read or update via
     * [LinkedHashMap.get] / [LinkedHashMap.put] moves the entry to the tail, so the
     * iterator visits least-recently-used entries first. This gives deterministic
     * eviction order in [pruneStaleKeys] regardless of insertion history.
     */
    private val records = object : LinkedHashMap<String, AttemptRecord>(16, 0.75f, true) {
        override fun removeEldestEntry(eldest: MutableMap.MutableEntry<String, AttemptRecord>?): Boolean {
            // Hard cap: never let the map exceed maxKeys, even if pruneStaleKeys hasn't run yet.
            return size > maxKeys
        }
    }

    /**
     * Checks if an operation is allowed for the given key (e.g., user ID, IP).
     * Returns true if allowed, false if rate limit exceeded.
     */
    @Synchronized
    fun isAllowed(key: String): Boolean {
        val now = System.currentTimeMillis()
        pruneStaleKeys(now)
        val record = records.getOrPut(key) { AttemptRecord() }

        // Remove old attempts outside the window
        while (record.attempts.isNotEmpty() && now - record.attempts.first() > windowMillis) {
            record.attempts.removeFirst()
        }

        // Check if under limit
        return if (record.attempts.size < maxAttempts) {
            record.attempts.addLast(now)
            true
        } else {
            false
        }
    }

    /**
     * Evicts the least-recently-used keys whose entire attempt window has expired,
     * preventing unbounded memory growth from one-time callers. Iteration order
     * is LRU-first thanks to [records] being an access-order [LinkedHashMap].
     */
    private fun pruneStaleKeys(now: Long) {
        if (records.size <= maxKeys) return
        val iterator = records.entries.iterator()
        while (iterator.hasNext() && records.size > maxKeys) {
            val entry = iterator.next()
            val attempts = entry.value.attempts
            while (attempts.isNotEmpty() && now - attempts.first() > windowMillis) {
                attempts.removeFirst()
            }
            if (attempts.isEmpty()) iterator.remove()
        }
    }

    /** Removes all recorded attempts — used for test isolation via reflection. */
    @Synchronized
    fun clear() {
        records.clear()
    }
}
