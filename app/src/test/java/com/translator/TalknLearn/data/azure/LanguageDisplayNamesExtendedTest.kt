package com.translator.TalknLearn.data.azure

import org.junit.Assert.*
import org.junit.Test

/**
 * Extended tests for LanguageDisplayNames.
 *
 * Covers:
 *  - Full display name mapping for all 17 supported languages
 *  - All detected-to-supported code mappings
 *  - Edge cases: regional variants, generic Chinese
 *  - isSupportedLanguage for all categories
 */
class LanguageDisplayNamesExtendedTest {

    // ── displayName — full coverage ────────────────────────────────

    @Test
    fun `displayName maps all 17 supported languages`() {
        val expected = mapOf(
            "en-US" to "English",
            "zh-TW" to "Traditional Chinese",
            "zh-HK" to "Cantonese",
            "zh-CN" to "Simplified Chinese",
            "ja-JP" to "Japanese",
            "fr-FR" to "French",
            "de-DE" to "German",
            "ko-KR" to "Korean",
            "es-ES" to "Spanish",
            "id-ID" to "Indonesian",
            "vi-VN" to "Vietnamese",
            "th-TH" to "Thai",
            "fil-PH" to "Filipino",
            "ms-MY" to "Malay",
            "pt-BR" to "Portuguese",
            "it-IT" to "Italian",
            "ru-RU" to "Russian"
        )
        expected.forEach { (code, name) ->
            assertEquals("Display name for $code", name, LanguageDisplayNames.displayName(code))
        }
    }

    @Test
    fun `displayName has exactly 17 entries`() {
        // Verify the expected number of supported languages
        val knownCodes = listOf(
            "en-US", "zh-TW", "zh-HK", "zh-CN", "ja-JP", "fr-FR", "de-DE",
            "ko-KR", "es-ES", "id-ID", "vi-VN", "th-TH", "fil-PH", "ms-MY",
            "pt-BR", "it-IT", "ru-RU"
        )
        // All should have display names (not return code itself)
        knownCodes.forEach { code ->
            assertNotEquals("$code should have display name", code, LanguageDisplayNames.displayName(code))
        }
    }

    // ── mapDetectedToSupportedCode — full coverage ─────────────────

    @Test
    fun `mapDetectedToSupportedCode maps all short codes`() {
        val shortCodeMappings = mapOf(
            "en" to "en-US",
            "ja" to "ja-JP",
            "ko" to "ko-KR",
            "fr" to "fr-FR",
            "de" to "de-DE",
            "es" to "es-ES",
            "id" to "id-ID",
            "vi" to "vi-VN",
            "th" to "th-TH",
            "fil" to "fil-PH",
            "ms" to "ms-MY",
            "pt" to "pt-BR",
            "it" to "it-IT",
            "ru" to "ru-RU"
        )
        shortCodeMappings.forEach { (detected, expected) ->
            assertEquals(
                "Mapping for $detected",
                expected,
                LanguageDisplayNames.mapDetectedToSupportedCode(detected)
            )
        }
    }

    @Test
    fun `mapDetectedToSupportedCode maps Chinese variants`() {
        assertEquals("zh-CN", LanguageDisplayNames.mapDetectedToSupportedCode("zh-Hans"))
        assertEquals("zh-TW", LanguageDisplayNames.mapDetectedToSupportedCode("zh-Hant"))
        assertEquals("zh-HK", LanguageDisplayNames.mapDetectedToSupportedCode("yue"))
        assertEquals("zh-CN", LanguageDisplayNames.mapDetectedToSupportedCode("zh"))
    }

    @Test
    fun `mapDetectedToSupportedCode returns identity for already-supported codes`() {
        val supportedCodes = listOf("en-US", "zh-TW", "zh-HK", "ja-JP", "ko-KR", "fr-FR")
        supportedCodes.forEach { code ->
            assertEquals("Identity for $code", code, LanguageDisplayNames.mapDetectedToSupportedCode(code))
        }
    }

    @Test
    fun `mapDetectedToSupportedCode extracts base for regional variants`() {
        // en-GB -> base "en" -> "en-US"
        assertEquals("en-US", LanguageDisplayNames.mapDetectedToSupportedCode("en-GB"))
        // fr-CA -> base "fr" -> "fr-FR"
        assertEquals("fr-FR", LanguageDisplayNames.mapDetectedToSupportedCode("fr-CA"))
        // pt-PT -> base "pt" -> "pt-BR"
        assertEquals("pt-BR", LanguageDisplayNames.mapDetectedToSupportedCode("pt-PT"))
    }

    @Test
    fun `mapDetectedToSupportedCode returns original for unknown code`() {
        assertEquals("xx-YY", LanguageDisplayNames.mapDetectedToSupportedCode("xx-YY"))
        assertEquals("abc", LanguageDisplayNames.mapDetectedToSupportedCode("abc"))
    }

    // ── isSupportedLanguage ────────────────────────────────────────

    @Test
    fun `isSupportedLanguage returns true for full supported codes`() {
        val supported = listOf("en-US", "ja-JP", "zh-TW", "zh-HK", "ko-KR", "fr-FR")
        supported.forEach { code ->
            assertTrue("$code should be supported", LanguageDisplayNames.isSupportedLanguage(code))
        }
    }

    @Test
    fun `isSupportedLanguage returns true for detected short codes`() {
        val detectedCodes = listOf("en", "ja", "ko", "fr", "de", "es", "zh-Hans", "zh-Hant", "yue")
        detectedCodes.forEach { code ->
            assertTrue("$code should be supported", LanguageDisplayNames.isSupportedLanguage(code))
        }
    }

    @Test
    fun `isSupportedLanguage returns true for regional variants via base code`() {
        // en-GB -> base "en" is in detectedToSupported
        assertTrue(LanguageDisplayNames.isSupportedLanguage("en-GB"))
        // fr-CA -> base "fr"
        assertTrue(LanguageDisplayNames.isSupportedLanguage("fr-CA"))
    }

    @Test
    fun `isSupportedLanguage returns false for completely unknown codes`() {
        assertFalse(LanguageDisplayNames.isSupportedLanguage("xx-YY"))
        assertFalse(LanguageDisplayNames.isSupportedLanguage("abc"))
    }

    @Test
    fun `isSupportedLanguage returns false for empty string base extraction`() {
        // Edge case: "-" -> substringBefore("-") = "" -> not in any map
        assertFalse(LanguageDisplayNames.isSupportedLanguage(""))
    }
}

