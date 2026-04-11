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

    // ── Username validation ────────────────────────────────────────

    @Test
    fun `validateUsername - valid username returns Valid`() {
        val result = validateUsername("user_name123")
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

    @Test
    fun `validateUsername - hyphens returns Invalid`() {
        val result = validateUsername("user-name")
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

    // ── Password validation ────────────────────────────────────────

    @Test
    fun `validatePassword - valid password returns Valid`() {
        val result = validatePassword("secret")
        assertTrue(result is ValidationResult.Valid)
    }

    @Test
    fun `validatePassword - too short returns Invalid`() {
        val result = validatePassword("abc")
        assertTrue(result is ValidationResult.Invalid)
        assertTrue((result as ValidationResult.Invalid).message.contains("6"))
    }

    @Test
    fun `validatePassword - blank returns Invalid`() {
        val result = validatePassword("   ")
        assertTrue(result is ValidationResult.Invalid)
    }

    @Test
    fun `validatePassword - whitespace trimmed before check`() {
        val result = validatePassword("  ab  ")
        assertTrue(result is ValidationResult.Invalid)
    }

    @Test
    fun `validatePassword - custom minLength respected`() {
        val result = validatePassword("abcdefgh", minLength = 10)
        assertTrue(result is ValidationResult.Invalid)
        assertTrue((result as ValidationResult.Invalid).message.contains("10"))
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

    @Test
    fun `rateLimiter - window expiry resets attempts`() {
        // Use a very short window so the test completes quickly
        val shortWindowLimiter = RateLimiter(maxAttempts = 2, windowMillis = 50L)

        // Exhaust attempts
        assertTrue(shortWindowLimiter.isAllowed("user1"))
        assertTrue(shortWindowLimiter.isAllowed("user1"))
        assertFalse(shortWindowLimiter.isAllowed("user1"))

        // Wait for window to expire
        Thread.sleep(100)

        // Should now be allowed again
        assertTrue(shortWindowLimiter.isAllowed("user1"))
    }

    @Test
    fun `rateLimiter - getRemainingAttempts reflects window expiry`() {
        val shortWindowLimiter = RateLimiter(maxAttempts = 2, windowMillis = 50L)

        shortWindowLimiter.isAllowed("user1")
        assertEquals(1, shortWindowLimiter.getRemainingAttempts("user1"))

        Thread.sleep(100)

        // After window, all attempts should be available again
        assertEquals(2, shortWindowLimiter.getRemainingAttempts("user1"))
    }
}
