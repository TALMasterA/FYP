package com.translator.TalknLearn.core.security

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
        val result = validatePassword("Abcdef1!")
        assertTrue(result is ValidationResult.Valid)
    }

    @Test
    fun `validatePassword - too short returns Invalid`() {
        val result = validatePassword("abc")
        assertTrue(result is ValidationResult.Invalid)
        assertTrue((result as ValidationResult.Invalid).message.contains("8"))
    }

    @Test
    fun `validatePassword - six char input now Invalid after minimum raised to 8`() {
        val result = validatePassword("secret")
        assertTrue(result is ValidationResult.Invalid)
        assertTrue((result as ValidationResult.Invalid).message.contains("8"))
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

    // ── Sanitization (§2.7 storage-only behaviour) ─────────────────

    @Test
    fun `sanitizeInput - preserves HTML angle brackets (no encoding)`() {
        // §2.7: sanitizeInput does NOT HTML-encode any more. Angle brackets
        // are kept verbatim because Compose Text renders them safely.
        val input = "<script>alert('xss')</script>"
        val sanitized = sanitizeInput(input)
        assertTrue(sanitized.contains("<"))
        assertTrue(sanitized.contains(">"))
        assertEquals("<script>alert('xss')</script>", sanitized)
    }

    @Test
    fun `sanitizeInput - preserves quotes (no encoding)`() {
        val sanitized = sanitizeInput("say \"hello\" and 'bye'")
        assertTrue(sanitized.contains("\""))
        assertTrue(sanitized.contains("'"))
    }

    @Test
    fun `sanitizeInput - trims and collapses whitespace`() {
        val sanitized = sanitizeInput("  hello   world  ")
        assertEquals("hello world", sanitized)
    }

    @Test
    fun `sanitizeInput - plain text unchanged except trim`() {
        val sanitized = sanitizeInput("Hello World 123")
        assertEquals("Hello World 123", sanitized)
    }

    @Test
    fun `sanitizeInput - strips ASCII control characters`() {
        val withControls = "hello\u0000\u0001\u0008world"
        val sanitized = sanitizeInput(withControls)
        assertEquals("helloworld", sanitized)
    }

    @Test
    fun `sanitizeInput - caps length at 5000 chars`() {
        val long = "a".repeat(6000)
        val sanitized = sanitizeInput(long)
        assertEquals(5000, sanitized.length)
    }

    // ── escapeForDisplay (§2.7 HTML/WebView path) ──────────────────

    @Test
    fun `escapeForDisplay - encodes angle brackets`() {
        val out = escapeForDisplay("<script>")
        assertEquals("&lt;script&gt;", out)
    }

    @Test
    fun `escapeForDisplay - encodes ampersand first to avoid double-encoding`() {
        val out = escapeForDisplay("a & b")
        assertEquals("a &amp; b", out)
    }

    @Test
    fun `escapeForDisplay - encodes quotes and slash`() {
        val out = escapeForDisplay("\"hello\"/'bye'")
        assertEquals("&quot;hello&quot;&#x2F;&#x27;bye&#x27;", out)
    }

    // ── decodeLegacyHtml (§2.7 backward-compat reader) ─────────────

    @Test
    fun `decodeLegacyHtml - reverses escapeForDisplay round trip`() {
        val original = "<a href=\"x\">it's & 'safe'/done</a>"
        val encoded = escapeForDisplay(original)
        val decoded = decodeLegacyHtml(encoded)
        assertEquals(original, decoded)
    }

    @Test
    fun `decodeLegacyHtml - decodes ampersand last so double-encoded data resolves correctly`() {
        // Double-encoded "<" is "&amp;lt;". Decoding entities first then & last
        // yields "&lt;" then "<" — verifying ampersand-last ordering.
        val doubleEncoded = "&amp;lt;"
        val decoded = decodeLegacyHtml(doubleEncoded)
        assertEquals("&lt;", decoded)
    }

    @Test
    fun `decodeLegacyHtml - leaves clean modern data unchanged`() {
        val modern = "Hello World 123 <not encoded>"
        assertEquals(modern, decodeLegacyHtml(modern))
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

}
