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
 * Validates password strength.
 * Requires minimum length and character variety.
 */
fun validatePassword(password: String, minLength: Int = 8): ValidationResult {
    if (password.length < minLength) {
        return ValidationResult.Invalid("Password must be at least $minLength characters")
    }

    val hasUpperCase = password.any { it.isUpperCase() }
    val hasLowerCase = password.any { it.isLowerCase() }
    val hasDigit = password.any { it.isDigit() }

    return if (hasUpperCase && hasLowerCase && hasDigit) {
        ValidationResult.Valid
    } else {
        ValidationResult.Invalid("Password must contain uppercase, lowercase, and numbers")
    }
}

/**
 * Validates username format.
 * Allows alphanumeric characters, underscores, and hyphens.
 */
fun validateUsername(username: String, minLength: Int = 3, maxLength: Int = 30): ValidationResult {
    if (username.length < minLength) {
        return ValidationResult.Invalid("Username must be at least $minLength characters")
    }

    if (username.length > maxLength) {
        return ValidationResult.Invalid("Username must not exceed $maxLength characters")
    }

    val usernameRegex = "^[a-zA-Z0-9_-]+$".toRegex()
    return if (usernameRegex.matches(username)) {
        ValidationResult.Valid
    } else {
        ValidationResult.Invalid("Username can only contain letters, numbers, underscores, and hyphens")
    }
}

/**
 * Sanitizes user input to prevent XSS and injection attacks.
 * Removes potentially dangerous characters and HTML tags.
 */
fun sanitizeInput(input: String): String {
    return input
        .replace("<", "&lt;")
        .replace(">", "&gt;")
        .replace("&", "&amp;")
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
 * Validates that a string contains only safe characters.
 * Useful for file names, IDs, and other system identifiers.
 */
fun validateSafeString(input: String): ValidationResult {
    val safeRegex = "^[a-zA-Z0-9_.-]+$".toRegex()
    return if (safeRegex.matches(input)) {
        ValidationResult.Valid
    } else {
        ValidationResult.Invalid("Input contains invalid characters")
    }
}

/**
 * Rate limiter to prevent abuse of sensitive operations.
 * Tracks operation attempts and enforces cooldown periods.
 */
class RateLimiter(
    private val maxAttempts: Int = 5,
    private val windowMillis: Long = 60 * 1000L // 1 minute
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
}

/**
 * Validates that a string doesn't contain SQL injection patterns.
 * Note: This is a basic check. Always use parameterized queries for database operations.
 */
fun validateNoSqlInjection(input: String): ValidationResult {
    val sqlKeywords = listOf("SELECT", "INSERT", "UPDATE", "DELETE", "DROP", "UNION", "EXEC", "--", ";")
    val upperInput = input.uppercase()

    for (keyword in sqlKeywords) {
        if (upperInput.contains(keyword)) {
            return ValidationResult.Invalid("Input contains potentially dangerous content")
        }
    }

    return ValidationResult.Valid
}

/**
 * Validates URL format and ensures it uses a safe protocol.
 */
fun validateUrl(url: String, allowedProtocols: List<String> = listOf("http", "https")): ValidationResult {
    if (url.isBlank()) {
        return ValidationResult.Invalid("URL cannot be empty")
    }

    try {
        val javaUrl = java.net.URL(url)
        return if (javaUrl.protocol in allowedProtocols) {
            ValidationResult.Valid
        } else {
            ValidationResult.Invalid("URL must use ${allowedProtocols.joinToString(" or ")} protocol")
        }
    } catch (e: Exception) {
        return ValidationResult.Invalid("Invalid URL format")
    }
}
