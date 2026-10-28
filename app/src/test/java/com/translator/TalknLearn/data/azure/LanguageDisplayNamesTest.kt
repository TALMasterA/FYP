package com.translator.TalknLearn.data.azure

import org.junit.Assert.*
import org.junit.Test

/**
 * Unit tests for LanguageDisplayNames utility.
 * Tests display name mapping, detected-to-supported code mapping, and support checks.
 */
class LanguageDisplayNamesTest {

    // ── displayName ──────────────────────────────────────────────────────────

    @Test
    fun `displayName returns English for en-US`() {
        assertEquals("English", LanguageDisplayNames.displayName("en-US"))
    }

    @Test
    fun `displayName returns Cantonese for zh-HK`() {
        assertEquals("Cantonese", LanguageDisplayNames.displayName("zh-HK"))
    }

    @Test
    fun `displayName returns Japanese for ja-JP`() {
        assertEquals("Japanese", LanguageDisplayNames.displayName("ja-JP"))
    }

    @Test
    fun `displayName returns code itself for unknown code`() {
        assertEquals("xx-YY", LanguageDisplayNames.displayName("xx-YY"))
    }

    // ── mapDetectedToSupportedCode ───────────────────────────────────────────

    @Test
    fun `mapDetectedToSupportedCode maps en to en-US`() {
        assertEquals("en-US", LanguageDisplayNames.mapDetectedToSupportedCode("en"))
    }

    @Test
    fun `mapDetectedToSupportedCode maps ja to ja-JP`() {
        assertEquals("ja-JP", LanguageDisplayNames.mapDetectedToSupportedCode("ja"))
    }

    @Test
    fun `mapDetectedToSupportedCode maps zh-Hans to zh-CN`() {
        assertEquals("zh-CN", LanguageDisplayNames.mapDetectedToSupportedCode("zh-Hans"))
    }

    @Test
    fun `mapDetectedToSupportedCode maps zh-Hant to zh-TW`() {
        assertEquals("zh-TW", LanguageDisplayNames.mapDetectedToSupportedCode("zh-Hant"))
    }

    @Test
    fun `mapDetectedToSupportedCode maps yue to zh-HK`() {
        assertEquals("zh-HK", LanguageDisplayNames.mapDetectedToSupportedCode("yue"))
    }

    @Test
    fun `mapDetectedToSupportedCode returns already-supported code unchanged`() {
        assertEquals("en-US", LanguageDisplayNames.mapDetectedToSupportedCode("en-US"))
        assertEquals("ja-JP", LanguageDisplayNames.mapDetectedToSupportedCode("ja-JP"))
    }

    // ── isSupportedLanguage ──────────────────────────────────────────────────

    @Test
    fun `isSupportedLanguage returns true for supported full codes`() {
        assertTrue(LanguageDisplayNames.isSupportedLanguage("en-US"))
        assertTrue(LanguageDisplayNames.isSupportedLanguage("zh-HK"))
    }

    @Test
    fun `isSupportedLanguage returns true for mapped short codes`() {
        assertTrue(LanguageDisplayNames.isSupportedLanguage("en"))
        assertTrue(LanguageDisplayNames.isSupportedLanguage("ja"))
    }

    @Test
    fun `isSupportedLanguage returns false for unsupported codes`() {
        assertFalse(LanguageDisplayNames.isSupportedLanguage("xx-YY"))
        assertFalse(LanguageDisplayNames.isSupportedLanguage("zz"))
    }

    // ── mapDetectedToSupportedCode edge cases ─────────────────────────────────

    @Test
    fun `mapDetectedToSupportedCode maps en-GB via base code fallback to en-US`() {
        assertEquals("en-US", LanguageDisplayNames.mapDetectedToSupportedCode("en-GB"))
    }

    @Test
    fun `mapDetectedToSupportedCode maps fr-CA via base code fallback to fr-FR`() {
        assertEquals("fr-FR", LanguageDisplayNames.mapDetectedToSupportedCode("fr-CA"))
    }

    @Test
    fun `mapDetectedToSupportedCode maps pt-PT via base code fallback to pt-BR`() {
        assertEquals("pt-BR", LanguageDisplayNames.mapDetectedToSupportedCode("pt-PT"))
    }

    @Test
    fun `mapDetectedToSupportedCode returns unknown code unchanged`() {
        assertEquals("abc-XY", LanguageDisplayNames.mapDetectedToSupportedCode("abc-XY"))
    }

    @Test
    fun `mapDetectedToSupportedCode maps zh to zh-CN`() {
        assertEquals("zh-CN", LanguageDisplayNames.mapDetectedToSupportedCode("zh"))
    }

    @Test
    fun `mapDetectedToSupportedCode maps all short codes correctly`() {
        val expectedMappings = mapOf(
            "en" to "en-US", "ja" to "ja-JP", "ko" to "ko-KR",
            "fr" to "fr-FR", "de" to "de-DE", "es" to "es-ES",
            "id" to "id-ID", "vi" to "vi-VN", "th" to "th-TH",
            "fil" to "fil-PH", "ms" to "ms-MY", "pt" to "pt-BR",
            "it" to "it-IT", "ru" to "ru-RU"
        )
        expectedMappings.forEach { (short, expected) ->
            assertEquals(
                "Mapping '$short' should produce '$expected'",
                expected,
                LanguageDisplayNames.mapDetectedToSupportedCode(short)
            )
        }
    }

    // ── displayName completeness ──────────────────────────────────────────────

    @Test
    fun `displayName returns non-code name for all 17 supported languages`() {
        val expectedCodes = listOf(
            "en-US", "zh-TW", "zh-HK", "zh-CN", "ja-JP", "fr-FR", "de-DE",
            "ko-KR", "es-ES", "id-ID", "vi-VN", "th-TH", "fil-PH", "ms-MY",
            "pt-BR", "it-IT", "ru-RU"
        )
        expectedCodes.forEach { code ->
            val name = LanguageDisplayNames.displayName(code)
            assertNotEquals(
                "displayName('$code') should return a human name, not the code itself",
                code, name
            )
        }
    }

    @Test
    fun `isSupportedLanguage returns true for all supported full codes`() {
        val allCodes = listOf(
            "en-US", "zh-TW", "zh-HK", "zh-CN", "ja-JP", "fr-FR", "de-DE",
            "ko-KR", "es-ES", "id-ID", "vi-VN", "th-TH", "fil-PH", "ms-MY",
            "pt-BR", "it-IT", "ru-RU"
        )
        allCodes.forEach { code ->
            assertTrue("'$code' should be supported", LanguageDisplayNames.isSupportedLanguage(code))
        }
    }
}
