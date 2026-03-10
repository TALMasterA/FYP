package com.example.fyp.domain.history

import com.example.fyp.model.LanguageCode
import org.junit.Assert.*
import org.junit.Test

/**
 * Tests for pure logic extracted from DeleteHistoryRecordUseCase.
 *
 * Covers:
 *  - Language code null/blank handling (takeIf + LanguageCode conversion)
 *  - Valid language codes are preserved
 *  - Blank/whitespace language codes become null
 */
class DeleteHistoryRecordLogicTest {

    /**
     * Replicates the language code conversion from DeleteHistoryRecordUseCase (line 33-34):
     * sourceLang?.takeIf { it.isNotBlank() }?.let { LanguageCode(it) }
     */
    private fun toLanguageCodeOrNull(lang: String?): LanguageCode? {
        return lang?.takeIf { it.isNotBlank() }?.let { LanguageCode(it) }
    }

    // ── Valid language codes ─────────────────────────────────────────────────

    @Test
    fun `valid language code is preserved`() {
        val result = toLanguageCodeOrNull("en-US")
        assertNotNull(result)
        assertEquals("en-US", result!!.value)
    }

    @Test
    fun `two-letter language code is preserved`() {
        val result = toLanguageCodeOrNull("ja-JP")
        assertNotNull(result)
        assertEquals("ja-JP", result!!.value)
    }

    // ── Null and blank handling ──────────────────────────────────────────────

    @Test
    fun `null language code returns null`() {
        val result = toLanguageCodeOrNull(null)
        assertNull(result)
    }

    @Test
    fun `empty string returns null`() {
        val result = toLanguageCodeOrNull("")
        assertNull(result)
    }

    @Test
    fun `blank string returns null`() {
        val result = toLanguageCodeOrNull("   ")
        assertNull(result)
    }

    @Test
    fun `tab and whitespace returns null`() {
        val result = toLanguageCodeOrNull("\t \n")
        assertNull(result)
    }

    // ── Both source and target handling ──────────────────────────────────────

    @Test
    fun `both langs provided are preserved`() {
        val source = toLanguageCodeOrNull("en-US")
        val target = toLanguageCodeOrNull("zh-TW")
        assertNotNull(source)
        assertNotNull(target)
        assertEquals("en-US", source!!.value)
        assertEquals("zh-TW", target!!.value)
    }

    @Test
    fun `one null one valid is handled correctly`() {
        val source = toLanguageCodeOrNull(null)
        val target = toLanguageCodeOrNull("ja-JP")
        assertNull(source)
        assertNotNull(target)
    }

    @Test
    fun `both null is handled correctly`() {
        val source = toLanguageCodeOrNull(null)
        val target = toLanguageCodeOrNull(null)
        assertNull(source)
        assertNull(target)
    }

    @Test
    fun `one blank one valid is handled correctly`() {
        val source = toLanguageCodeOrNull("")
        val target = toLanguageCodeOrNull("ko-KR")
        assertNull(source)
        assertNotNull(target)
    }
}
