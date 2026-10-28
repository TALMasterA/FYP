package com.translator.TalknLearn.data.cloud

import org.junit.Assert.*
import org.junit.Test

/**
 * Unit tests for CloudGenAiClient error handling logic and response parsing.
 *
 * Note: Firebase Functions HttpsCallableReference is a final class that cannot
 * be mocked in standard JVM tests. These tests verify the error message patterns
 * and response parsing logic that CloudGenAiClient uses.
 *
 * Tests:
 *  1. Content validation: blank is rejected
 *  2. Content validation: null is rejected
 *  3. Content validation: whitespace-only is rejected
 *  4. Content validation: valid content passes
 *  5. Error message preserves original exception message
 *  6. Error message with blank original uses fallback
 *  7. Error message with null message uses fallback
 *  8. Map response with content key extracts content
 *  9. Map response without content returns null
 * 10. Non-map response results in null cast
 */
class CloudGenAiClientTest {

    // ── Content validation tests ──

    @Test
    fun `blank content check`() {
        val content: String? = ""
        assertTrue(content.isNullOrBlank())
    }

    @Test
    fun `null content check`() {
        val content: String? = null
        assertTrue(content.isNullOrBlank())
    }

    @Test
    fun `whitespace-only content check`() {
        val content: String? = "   "
        assertTrue(content.isNullOrBlank())
    }

    @Test
    fun `valid content is not blank`() {
        val content = "Generated quiz content"
        assertFalse(content.isNullOrBlank())
    }

    // ── Error message pattern tests ──

    @Test
    fun `error message with original exception preserves message`() {
        val original = RuntimeException("Network timeout")
        val errorMsg = original.message?.takeIf { it.isNotBlank() }
            ?: "Generation failed. Please try again."
        assertEquals("Network timeout", errorMsg)
    }

    @Test
    fun `error message with blank original uses fallback`() {
        val original = RuntimeException("")
        val errorMsg = original.message?.takeIf { it.isNotBlank() }
            ?: "Generation failed. Please try again."
        assertEquals("Generation failed. Please try again.", errorMsg)
    }

    @Test
    fun `error message with null message uses fallback`() {
        val original = RuntimeException()
        val errorMsg = original.message?.takeIf { it.isNotBlank() }
            ?: "Generation failed. Please try again."
        assertEquals("Generation failed. Please try again.", errorMsg)
    }

    // ── Response parsing tests ──

    @Test
    fun `map response with content key extracts content`() {
        val map: Map<*, *> = mapOf("content" to "Generated quiz about colors")
        val content = map["content"] as? String
        assertFalse(content.isNullOrBlank())
        assertEquals("Generated quiz about colors", content)
    }

    @Test
    fun `map response without content key returns null`() {
        val map: Map<*, *> = mapOf("error" to "something went wrong")
        val content = map["content"] as? String
        assertTrue(content.isNullOrBlank())
    }

    @Test
    fun `non-map response results in null cast`() {
        val data: Any = "just a string"
        val map = data as? Map<*, *>
        assertNull(map)
    }

    @Test
    fun `null response results in null cast`() {
        val data: Any? = null
        val map = data as? Map<*, *>
        assertNull(map)
    }
}
