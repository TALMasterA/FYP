package com.example.fyp.core.security

import org.junit.Assert.*
import org.junit.Test

/**
 * Tests for [escapeForDisplay] covering the double-encoding bug fix and
 * encoding correctness for various special-character combinations.
 *
 * §2.7 history note: these tests were originally written against the
 * pre-§2.7 `sanitizeInput` which performed HTML encoding. After the §2.7
 * split, the HTML-encoding responsibility moved to [escapeForDisplay] and
 * `sanitizeInput` only strips controls / collapses whitespace / caps length.
 * The encoding semantics asserted below are unchanged.
 */
class EscapeForDisplayTest {

    // ── 1. No double-encoding of ampersands ────────────────────────

    @Test
    fun `escapeForDisplay - does not double-encode ampersands introduced by encoding`() {
        val result = escapeForDisplay("<hello>")
        assertFalse(
            "Output must not contain double-encoded '&amp;lt;'",
            result.contains("&amp;lt;")
        )
        assertFalse(
            "Output must not contain double-encoded '&amp;gt;'",
            result.contains("&amp;gt;")
        )
    }

    // ── 2. Angle-bracket encoding produces correct entities ────────

    @Test
    fun `escapeForDisplay - angle brackets encode to correct HTML entities`() {
        val result = escapeForDisplay("<hello>")
        assertEquals("&lt;hello&gt;", result)
    }

    // ── 3. Existing '&amp;' in input is single-encoded ────────────

    @Test
    fun `escapeForDisplay - existing ampamp in input is single-encoded`() {
        val result = escapeForDisplay("&amp;")
        assertEquals("&amp;amp;", result)
    }

    // ── 4. Existing '&lt;' in input is encoded correctly ──────────

    @Test
    fun `escapeForDisplay - literal amplt in input encodes the ampersand only`() {
        val result = escapeForDisplay("&lt;")
        assertEquals("&amp;lt;", result)
    }

    // ── 5. Multiple special characters in one string ──────────────

    @Test
    fun `escapeForDisplay - multiple special chars are all correctly encoded`() {
        val result = escapeForDisplay("<b>\"Tom & Jerry\"</b>")
        assertEquals(
            "&lt;b&gt;&quot;Tom &amp; Jerry&quot;&lt;&#x2F;b&gt;",
            result
        )
    }

    // ── 6. Lone ampersand ─────────────────────────────────────────

    @Test
    fun `escapeForDisplay - lone ampersand encodes to ampamp`() {
        val result = escapeForDisplay("&")
        assertEquals("&amp;", result)
    }

    // ── 7. HTML tag with attributes ───────────────────────────────

    @Test
    fun `escapeForDisplay - HTML tag with attributes is fully encoded`() {
        val result = escapeForDisplay("<a href=\"x\">")
        assertEquals("&lt;a href=&quot;x&quot;&gt;", result)
    }

    // ── 8. Empty string ───────────────────────────────────────────

    @Test
    fun `escapeForDisplay - empty string returns empty string`() {
        val result = escapeForDisplay("")
        assertEquals("", result)
    }

    // ── 9. Idempotency – double escaping re-encodes ───────────────

    @Test
    fun `escapeForDisplay - applying twice is NOT idempotent because ampersands are re-encoded`() {
        val once = escapeForDisplay("<hello>")
        val twice = escapeForDisplay(once)

        assertEquals("&lt;hello&gt;", once)
        assertEquals("&amp;lt;hello&amp;gt;", twice)

        assertNotEquals(
            "escapeForDisplay must NOT be idempotent for inputs with special characters",
            once,
            twice
        )
    }
}
