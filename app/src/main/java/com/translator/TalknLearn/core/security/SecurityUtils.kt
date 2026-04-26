package com.translator.TalknLearn.core.security

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
 * Maximum allowed length for any user-provided free-text field after
 * [sanitizeInput] cleanup. Acts as a hard cap that prevents pathological
 * inputs from inflating Firestore documents or downstream payloads.
 */
const val SANITIZE_INPUT_MAX_LENGTH: Int = 5000

/**
 * Sanitizes user-provided free text for STORAGE.
 *
 * This implementation explicitly DOES NOT HTML-encode the input. The earlier
 * (pre-§2.7) implementation HTML-escaped `&<>"'/` before persisting to
 * Firestore, which corrupted user content because the app renders all chat,
 * feedback, and word-bank fields with Jetpack Compose `Text` — Compose does
 * not auto-decode HTML entities, so users saw literal `&lt;` / `&amp;`
 * sequences inside their own messages.
 *
 * After §2.7 the responsibilities are split:
 *  - [sanitizeInput] — cleans control characters / collapses whitespace /
 *    enforces length cap. Use this everywhere user input is persisted.
 *  - [escapeForDisplay] — HTML-encodes the cleaned text. Use only when the
 *    output is rendered in an HTML / WebView context.
 *  - [decodeLegacyHtml] — reverses the legacy HTML encoding so existing
 *    Firestore documents that were written under the old [sanitizeInput]
 *    still display correctly.
 *
 * Cleanup steps performed here:
 *  1. Strip ASCII control characters (`U+0000`–`U+001F` excluding `\t`, `\n`,
 *     `\r`) — these have no legitimate use in chat / feedback / word-bank
 *     fields and can corrupt Firestore log output.
 *  2. Collapse runs of whitespace (including newlines) into a single space.
 *  3. Trim leading / trailing whitespace.
 *  4. Cap the total length at [SANITIZE_INPUT_MAX_LENGTH].
 */
fun sanitizeInput(input: String): String {
    val stripped = input.replace(Regex("[\\x00-\\x08\\x0B\\x0C\\x0E-\\x1F]"), "")
    val collapsed = stripped.replace(Regex("\\s+"), " ").trim()
    return if (collapsed.length > SANITIZE_INPUT_MAX_LENGTH) {
        collapsed.substring(0, SANITIZE_INPUT_MAX_LENGTH)
    } else {
        collapsed
    }
}

/**
 * Escapes user content for safe rendering inside an HTML / WebView context.
 *
 * This is the same encoding the pre-§2.7 [sanitizeInput] performed
 * (ampersand-first to avoid double encoding). Use this ONLY at presentation
 * sites that actually render HTML; the rest of the app uses Compose `Text`
 * and should use the raw [sanitizeInput] output instead.
 */
fun escapeForDisplay(input: String): String {
    return input
        .replace("&", "&amp;")   // Ampersand MUST be first to avoid double-encoding
        .replace("<", "&lt;")
        .replace(">", "&gt;")
        .replace("\"", "&quot;")
        .replace("'", "&#x27;")
        .replace("/", "&#x2F;")
}

/**
 * Reverses the legacy HTML encoding that pre-§2.7 [sanitizeInput] applied
 * before persisting user content to Firestore. Newer documents written by
 * the post-§2.7 sanitizer pass through unchanged because they contain no
 * entities. Use at READ sites that surface free-text user content (chat
 * messages, feedback, custom words, friend notes) so legacy data renders
 * correctly in Compose `Text`.
 *
 * The order of replacements is important: the named-character entities
 * are decoded BEFORE `&amp;` so that `&amp;lt;` (a double-encoded `<`)
 * decodes to `&lt;` and then to `<` — never to `<` directly via a
 * single pass that would leave `&amp;` orphaned.
 */
fun decodeLegacyHtml(input: String): String {
    return input
        .replace("&lt;", "<")
        .replace("&gt;", ">")
        .replace("&quot;", "\"")
        .replace("&#x27;", "'")
        .replace("&#x2F;", "/")
        .replace("&amp;", "&")   // Ampersand MUST be last for symmetry with escapeForDisplay
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
