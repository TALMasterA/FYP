package com.example.fyp.core.security

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Tests for SecurityUtils validation functions, sanitization,
 * and the [RateLimiter] utility.
 */
class SecurityUtilsTest {

    // ── Email validation ───────────────────────────────────────────

    @Test
    fun `validateEmail - valid email returns Valid`() {
        val result = validateEmail("user@example.com")
        assertTrue(result is ValidationResult.Valid)
    }

    @Test
    fun `validateEmail - valid email with dots and plus returns Valid`() {
        val result = validateEmail("first.last+tag@example.co.uk")
        assertTrue(result is ValidationResult.Valid)
    }

    @Test
    fun `validateEmail - blank email returns Invalid`() {
        val result = validateEmail("")
        assertTrue(result is ValidationResult.Invalid)
        assertEquals("Email cannot be empty", (result as ValidationResult.Invalid).message)
    }

    @Test
    fun `validateEmail - missing at sign returns Invalid`() {
        val result = validateEmail("userexample.com")
        assertTrue(result is ValidationResult.Invalid)
    }

    @Test
    fun `validateEmail - missing domain returns Invalid`() {
        val result = validateEmail("user@")
        assertTrue(result is ValidationResult.Invalid)
    }

    // ── Password validation ────────────────────────────────────────

    @Test
    fun `validatePassword - strong password returns Valid`() {
        val result = validatePassword("Abc12345")
        assertTrue(result is ValidationResult.Valid)
    }

    @Test
    fun `validatePassword - too short returns Invalid`() {
        val result = validatePassword("Ab1")
        assertTrue(result is ValidationResult.Invalid)
        assertTrue((result as ValidationResult.Invalid).message.contains("at least"))
    }

    @Test
    fun `validatePassword - no uppercase returns Invalid`() {
        val result = validatePassword("abc12345")
        assertTrue(result is ValidationResult.Invalid)
    }

    @Test
    fun `validatePassword - no digit returns Invalid`() {
        val result = validatePassword("Abcdefgh")
        assertTrue(result is ValidationResult.Invalid)
    }

    @Test
    fun `validatePassword - custom min length enforced`() {
        val result = validatePassword("Ab123", minLength = 6)
        assertTrue(result is ValidationResult.Invalid)
    }

    // ── Username validation ────────────────────────────────────────

    @Test
    fun `validateUsername - valid username returns Valid`() {
        val result = validateUsername("user_name-123")
        assertTrue(result is ValidationResult.Valid)
    }

    @Test
    fun `validateUsername - too short returns Invalid`() {
        val result = validateUsername("ab")
        assertTrue(result is ValidationResult.Invalid)
    }

    @Test
    fun `validateUsername - too long returns Invalid`() {
        val result = validateUsername("a".repeat(31))
        assertTrue(result is ValidationResult.Invalid)
    }

    @Test
    fun `validateUsername - special characters returns Invalid`() {
        val result = validateUsername("user@name!")
        assertTrue(result is ValidationResult.Invalid)
    }

    // ── Text length validation ─────────────────────────────────────

    @Test
    fun `validateTextLength - within bounds returns Valid`() {
        val result = validateTextLength("hello", minLength = 1, maxLength = 10)
        assertTrue(result is ValidationResult.Valid)
    }

    @Test
    fun `validateTextLength - below min returns Invalid`() {
        val result = validateTextLength("", minLength = 1, fieldName = "Name")
        assertTrue(result is ValidationResult.Invalid)
        assertTrue((result as ValidationResult.Invalid).message.contains("Name"))
    }

    @Test
    fun `validateTextLength - above max returns Invalid`() {
        val result = validateTextLength("hello world", maxLength = 5, fieldName = "Title")
        assertTrue(result is ValidationResult.Invalid)
        assertTrue((result as ValidationResult.Invalid).message.contains("Title"))
    }

    // ── Safe string validation ─────────────────────────────────────

    @Test
    fun `validateSafeString - alphanumeric with dots returns Valid`() {
        val result = validateSafeString("file_name.txt")
        assertTrue(result is ValidationResult.Valid)
    }

    @Test
    fun `validateSafeString - string with spaces returns Invalid`() {
        val result = validateSafeString("not safe")
        assertTrue(result is ValidationResult.Invalid)
    }

    // ── SQL injection detection ────────────────────────────────────

    @Test
    fun `validateNoSqlInjection - clean input returns Valid`() {
        val result = validateNoSqlInjection("Hello world 123")
        assertTrue(result is ValidationResult.Valid)
    }

    @Test
    fun `validateNoSqlInjection - SELECT keyword returns Invalid`() {
        val result = validateNoSqlInjection("'; SELECT * FROM users --")
        assertTrue(result is ValidationResult.Invalid)
    }

    @Test
    fun `validateNoSqlInjection - DROP keyword returns Invalid`() {
        val result = validateNoSqlInjection("DROP TABLE students")
        assertTrue(result is ValidationResult.Invalid)
    }

    @Test
    fun `validateNoSqlInjection - comment syntax returns Invalid`() {
        val result = validateNoSqlInjection("admin'--")
        assertTrue(result is ValidationResult.Invalid)
    }

    // ── URL validation ─────────────────────────────────────────────

    @Test
    fun `validateUrl - valid HTTPS returns Valid`() {
        val result = validateUrl("https://example.com/path")
        assertTrue(result is ValidationResult.Valid)
    }

    @Test
    fun `validateUrl - valid HTTP returns Valid`() {
        val result = validateUrl("http://example.com")
        assertTrue(result is ValidationResult.Valid)
    }

    @Test
    fun `validateUrl - blank URL returns Invalid`() {
        val result = validateUrl("")
        assertTrue(result is ValidationResult.Invalid)
    }

    @Test
    fun `validateUrl - FTP protocol rejected by default`() {
        val result = validateUrl("ftp://files.example.com")
        assertTrue(result is ValidationResult.Invalid)
    }

    @Test
    fun `validateUrl - malformed URL returns Invalid`() {
        val result = validateUrl("not a url at all")
        assertTrue(result is ValidationResult.Invalid)
    }

    // ── Sanitization ───────────────────────────────────────────────

    @Test
    fun `sanitizeInput - escapes HTML angle brackets`() {
        val input = "<script>alert('xss')</script>"
        val sanitized = sanitizeInput(input)
        assertFalse(sanitized.contains("<"))
        assertFalse(sanitized.contains(">"))
    }

    @Test
    fun `sanitizeInput - escapes quotes`() {
        val sanitized = sanitizeInput("say \"hello\" and 'bye'")
        assertFalse(sanitized.contains("\""))
        assertFalse(sanitized.contains("'"))
    }

    @Test
    fun `sanitizeInput - trims whitespace`() {
        val sanitized = sanitizeInput("  hello  ")
        assertEquals("hello", sanitized)
    }

    @Test
    fun `sanitizeInput - plain text unchanged except trim`() {
        val sanitized = sanitizeInput("Hello World 123")
        assertEquals("Hello World 123", sanitized)
    }

    // ── Rate limiter ───────────────────────────────────────────────

    private lateinit var limiter: RateLimiter

    @Before
    fun setup() {
        limiter = RateLimiter(maxAttempts = 3, windowMillis = 60_000L)
    }

    @Test
    fun `rateLimiter - allows under limit`() {
        assertTrue(limiter.isAllowed("user1"))
        assertTrue(limiter.isAllowed("user1"))
        assertTrue(limiter.isAllowed("user1"))
    }

    @Test
    fun `rateLimiter - blocks when limit exceeded`() {
        repeat(3) { limiter.isAllowed("user1") }
        assertFalse(limiter.isAllowed("user1"))
    }

    @Test
    fun `rateLimiter - different keys are independent`() {
        repeat(3) { limiter.isAllowed("user1") }
        assertTrue(limiter.isAllowed("user2"))
    }

    @Test
    fun `rateLimiter - remaining attempts tracks correctly`() {
        assertEquals(3, limiter.getRemainingAttempts("user1"))
        limiter.isAllowed("user1")
        assertEquals(2, limiter.getRemainingAttempts("user1"))
    }

    @Test
    fun `rateLimiter - reset clears attempts`() {
        repeat(3) { limiter.isAllowed("user1") }
        assertFalse(limiter.isAllowed("user1"))
        limiter.reset("user1")
        assertTrue(limiter.isAllowed("user1"))
    }

    @Test
    fun `rateLimiter - clear removes all records`() {
        repeat(3) { limiter.isAllowed("user1") }
        repeat(3) { limiter.isAllowed("user2") }
        limiter.clear()
        assertTrue(limiter.isAllowed("user1"))
        assertTrue(limiter.isAllowed("user2"))
    }
}
