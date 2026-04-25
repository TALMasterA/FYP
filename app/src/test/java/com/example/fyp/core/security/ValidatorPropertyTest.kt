package com.example.fyp.core.security

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

/**
 * Lightweight property/fuzz tests for input validators (§3.8 of APP_SUGGESTIONS).
 *
 * Uses a deterministic seed so failures are reproducible. No new test
 * dependency is introduced; this is plain JUnit + kotlin.random.Random.
 */
class ValidatorPropertyTest {

    private val seed = 0xFEEDFACEL
    private val iterations = 200

    private fun rng() = Random(seed)

    @Test
    fun `sanitizeInput never leaves raw dangerous characters in output`() {
        val rng = rng()
        val raw = "<>\"'/&abcXYZ012 !?"
        repeat(iterations) {
            val len = rng.nextInt(0, 64)
            val sb = StringBuilder()
            repeat(len) { sb.append(raw[rng.nextInt(raw.length)]) }
            val out = sanitizeInput(sb.toString())
            // Output must never contain the raw dangerous characters that
            // sanitizeInput is responsible for encoding.
            assertFalse("contains '<' in: $out", out.contains('<'))
            assertFalse("contains '>' in: $out", out.contains('>'))
            assertFalse("contains '\"' in: $out", out.contains('"'))
            assertFalse("contains '\\'' in: $out", out.contains('\''))
            assertFalse("contains '/' in: $out", out.contains('/'))
        }
    }

    @Test
    fun `sanitizeInput preserves ampersand-first encoding for entity-like input`() {
        // Documented invariant: ampersands must be encoded first so existing
        // entity-looking text becomes &amp;lt; rather than being decoded.
        assertEquals("&lt;x&gt;", sanitizeInput("<x>"))
        assertEquals("&amp;lt;x&amp;gt;", sanitizeInput("&lt;x&gt;"))
    }

    @Test
    fun `validateUsername accepts random alphanumeric_underscore strings within bounds`() {
        val rng = rng()
        val alphabet = ("abcdefghijklmnopqrstuvwxyz" +
            "ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
            "0123456789_")
        repeat(iterations) {
            val len = rng.nextInt(3, 21)
            val name = buildString(len) {
                repeat(len) { append(alphabet[rng.nextInt(alphabet.length)]) }
            }
            val result = validateUsername(name)
            assertTrue("expected Valid for '$name' but got $result", result is ValidationResult.Valid)
        }
    }

    @Test
    fun `validateUsername rejects spaces, slashes, hyphens and emoji`() {
        // The current regex is [a-zA-Z0-9_]+; until the username rule is
        // unified per §2.1.1 these characters must continue to fail.
        val rejected = listOf("ab cd", "ab/cd", "ab-cd", "abc\uD83D\uDE00")
        rejected.forEach {
            assertTrue(
                "expected Invalid for '$it'",
                validateUsername(it) is ValidationResult.Invalid,
            )
        }
    }

    @Test
    fun `validateUsername rejects lengths outside bounds`() {
        val rng = rng()
        repeat(iterations) {
            val len = rng.nextInt(0, 3)
            val short = "a".repeat(len)
            assertTrue(
                "expected Invalid for too-short '$short'",
                validateUsername(short) is ValidationResult.Invalid,
            )
        }
        val tooLong = "a".repeat(21)
        assertTrue(
            "expected Invalid for too-long",
            validateUsername(tooLong) is ValidationResult.Invalid,
        )
    }

    @Test
    fun `validatePassword rejects short and whitespace-only inputs`() {
        val rng = rng()
        repeat(iterations) {
            // After the §2.1 sweep validatePassword's default minLength is 8.
            val len = rng.nextInt(0, 8)
            val pwd = " ".repeat(len) + "a".repeat(rng.nextInt(0, 8 - len))
            assertTrue(
                "expected Invalid for trimmed-length<8 '$pwd'",
                validatePassword(pwd) is ValidationResult.Invalid,
            )
        }
        assertTrue(validatePassword("        ") is ValidationResult.Invalid)
    }
}
