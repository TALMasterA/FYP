package com.example.fyp.core.security

import org.junit.Assert.*
import org.junit.Test

/**
 * Extended tests for [sanitizeInput] covering the double-encoding bug fix
 * and encoding correctness for various special-character combinations.
 */
class SanitizeInputExtendedTest {

    // ── 1. No double-encoding of ampersands ────────────────────────

    @Test
    fun `sanitizeInput - does not double-encode ampersands introduced by encoding`() {
        // The old buggy implementation encoded '<' first, producing '&lt;',
        // and then encoded the '&' in '&lt;' again, yielding '&amp;lt;'.
        // After the fix, '&' is encoded FIRST so subsequent replacements
        // never touch the ampersands they themselves introduce.
        val result = sanitizeInput("<hello>")
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
    fun `sanitizeInput - angle brackets encode to correct HTML entities`() {
        val result = sanitizeInput("<hello>")
        assertEquals("&lt;hello&gt;", result)
    }

    // ── 3. Existing '&amp;' in input is single-encoded ────────────

    @Test
    fun `sanitizeInput - existing ampamp in input is single-encoded`() {
        // Input literally contains the five characters '&amp;'.
        // The '&' is encoded to '&amp;', and 'amp;' passes through,
        // so the result is '&amp;amp;'.
        val result = sanitizeInput("&amp;")
        assertEquals("&amp;amp;", result)
    }

    // ── 4. Existing '&lt;' in input is encoded correctly ──────────

    @Test
    fun `sanitizeInput - literal amplt in input encodes the ampersand only`() {
        // Input literally contains '&lt;'.
        // The '&' becomes '&amp;', 'lt;' is plain text, giving '&amp;lt;'.
        val result = sanitizeInput("&lt;")
        assertEquals("&amp;lt;", result)
    }

    // ── 5. Multiple special characters in one string ──────────────

    @Test
    fun `sanitizeInput - multiple special chars are all correctly encoded`() {
        // Input: <b>"Tom & Jerry"</b>
        // Expected encoding chain:
        //   & -> &amp;          =>  <b>"Tom &amp; Jerry"</b>
        //   < -> &lt;           =>  &lt;b>"Tom &amp; Jerry"&lt;/b>
        //   > -> &gt;           =>  &lt;b&gt;"Tom &amp; Jerry"&lt;/b&gt;
        //   " -> &quot;         =>  &lt;b&gt;&quot;Tom &amp; Jerry&quot;&lt;/b&gt;
        //   / -> &#x2F;         =>  &lt;b&gt;&quot;Tom &amp; Jerry&quot;&lt;&#x2F;b&gt;
        val result = sanitizeInput("<b>\"Tom & Jerry\"</b>")
        assertEquals(
            "&lt;b&gt;&quot;Tom &amp; Jerry&quot;&lt;&#x2F;b&gt;",
            result
        )
    }

    // ── 6. Lone ampersand ─────────────────────────────────────────

    @Test
    fun `sanitizeInput - lone ampersand encodes to ampamp`() {
        val result = sanitizeInput("&")
        assertEquals("&amp;", result)
    }

    // ── 7. HTML tag with attributes ───────────────────────────────

    @Test
    fun `sanitizeInput - HTML tag with attributes is fully encoded`() {
        // Input: <a href="x">
        // Encoding chain:
        //   & -> &amp;    (none)
        //   < -> &lt;     =>  &lt;a href="x">
        //   > -> &gt;     =>  &lt;a href="x"&gt;
        //   " -> &quot;   =>  &lt;a href=&quot;x&quot;&gt;
        //   / -> &#x2F;   (none)
        val result = sanitizeInput("<a href=\"x\">")
        assertEquals("&lt;a href=&quot;x&quot;&gt;", result)
    }

    // ── 8. Empty string ───────────────────────────────────────────

    @Test
    fun `sanitizeInput - empty string returns empty string`() {
        val result = sanitizeInput("")
        assertEquals("", result)
    }

    // ── 9. Idempotency – double sanitization re-encodes ───────────

    @Test
    fun `sanitizeInput - applying twice is NOT idempotent because ampersands are re-encoded`() {
        // First pass:  <hello>  ->  &lt;hello&gt;
        // Second pass: the '&' in '&lt;' and '&gt;' is re-encoded,
        //              producing &amp;lt;hello&amp;gt;
        // Therefore sanitize(sanitize(x)) != sanitize(x) for inputs
        // that contain characters requiring encoding.
        val once = sanitizeInput("<hello>")
        val twice = sanitizeInput(once)

        // Verify first pass is correct
        assertEquals("&lt;hello&gt;", once)

        // Verify second pass re-encodes the ampersands
        assertEquals("&amp;lt;hello&amp;gt;", twice)

        // The two results must differ
        assertNotEquals(
            "sanitizeInput must NOT be idempotent for inputs with special characters",
            once,
            twice
        )
    }
}
