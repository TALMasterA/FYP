package com.example.fyp.domain.validation

import org.junit.Test
import org.junit.Assert.*

/**
 * Unit tests for language code validation logic.
 *
 * Tests validation for:
 * - Language code format (ISO 639-1 and locale format)
 * - Supported language codes
 * - Language pair validity
 * - Language code normalization
 */
class LanguageValidationTest {

    // --- Supported Languages (from UiTextCore.kt) ---
    private val supportedLanguages = setOf(
        "en-US",  // English
        "zh-HK",  // Cantonese
        "ja-JP",  // Japanese
        "zh-CN",  // Mandarin
        "fr-FR",  // French
        "de-DE",  // German
        "ko-KR",  // Korean
        "es-ES",  // Spanish
        "id-ID",  // Indonesian
        "vi-VN",  // Vietnamese
        "th-TH",  // Thai
        "fil-PH", // Filipino
        "ms-MY",  // Malay
        "pt-BR",  // Portuguese
        "it-IT",  // Italian
        "ru-RU"   // Russian
    )

    // --- Language Code Format Tests ---

    @Test
    fun `valid language code follows locale format`() {
        val validCodes = listOf(
            "en-US", "es-ES", "fr-FR", "de-DE", "ja-JP",
            "zh-CN", "ko-KR", "it-IT", "pt-BR", "ru-RU"
        )

        validCodes.forEach { code ->
            assertTrue(isValidLanguageCodeFormat(code))
        }
    }

    @Test
    fun `language code must contain hyphen`() {
        val code = "en-US"
        assertTrue(code.contains("-"))
        assertEquals(2, code.split("-").size)
    }

    @Test
    fun `language code parts have correct length`() {
        val code = "en-US"
        val parts = code.split("-")

        assertTrue(parts[0].length in 2..3) // Language code: 2-3 chars
        assertTrue(parts[1].length == 2)     // Country code: 2 chars
    }

    @Test
    fun `invalid format codes are rejected`() {
        val invalidCodes = listOf(
            "en",      // Missing country code
            "EN-US",   // Should be lowercase language code
            "en_US",   // Wrong separator
            "english", // Full name not code
            "en-",     // Incomplete
            "-US",     // Incomplete
            "en-USA"   // Country code too long
        )

        invalidCodes.forEach { code ->
            assertFalse(isValidLanguageCodeFormat(code))
        }
    }

    // --- Supported Language Tests ---

    @Test
    fun `all standard supported languages are valid`() {
        supportedLanguages.forEach { lang ->
            assertTrue(isSupportedLanguage(lang))
        }
    }

    @Test
    fun `unsupported language codes return false`() {
        val unsupportedCodes = listOf(
            "ar-SA", // Arabic
            "he-IL", // Hebrew
            "hi-IN", // Hindi
            "pl-PL", // Polish
            "nl-NL"  // Dutch
        )

        unsupportedCodes.forEach { code ->
            assertFalse(isSupportedLanguage(code))
        }
    }

    @Test
    fun `supported languages count matches expected`() {
        assertEquals(16, supportedLanguages.size)
    }

    // --- Language Pair Validity Tests ---

    @Test
    fun `valid language pair has different source and target`() {
        val pair1 = LanguagePair("en-US", "es-ES")
        assertTrue(isValidLanguagePair(pair1))

        val pair2 = LanguagePair("ja-JP", "en-US")
        assertTrue(isValidLanguagePair(pair2))
    }

    @Test
    fun `invalid language pair has same source and target`() {
        val pair = LanguagePair("en-US", "en-US")
        assertFalse(isValidLanguagePair(pair))
    }

    @Test
    fun `language pair can be reversed`() {
        val pair1 = LanguagePair("en-US", "es-ES")
        val pair2 = LanguagePair("es-ES", "en-US")

        assertTrue(isValidLanguagePair(pair1))
        assertTrue(isValidLanguagePair(pair2))
        assertEquals(pair1.source, pair2.target)
        assertEquals(pair1.target, pair2.source)
    }

    @Test
    fun `both languages in pair must be supported`() {
        val validPair = LanguagePair("en-US", "es-ES")
        assertTrue(
            isSupportedLanguage(validPair.source) &&
            isSupportedLanguage(validPair.target)
        )

        val invalidPair = LanguagePair("en-US", "ar-SA")
        assertFalse(
            isSupportedLanguage(invalidPair.source) &&
            isSupportedLanguage(invalidPair.target)
        )
    }

    // --- Language Code Normalization Tests ---

    @Test
    fun `normalize converts uppercase country code to proper case`() {
        assertEquals("en-US", normalizeLanguageCode("en-us"))
        assertEquals("es-ES", normalizeLanguageCode("es-es"))
        assertEquals("fr-FR", normalizeLanguageCode("fr-fr"))
    }

    @Test
    fun `normalize handles already correct codes`() {
        assertEquals("en-US", normalizeLanguageCode("en-US"))
        assertEquals("ja-JP", normalizeLanguageCode("ja-JP"))
    }

    @Test
    fun `normalize handles mixed case`() {
        assertEquals("en-US", normalizeLanguageCode("EN-US"))
        assertEquals("de-DE", normalizeLanguageCode("DE-de"))
    }

    @Test
    fun `normalize preserves Filipino special case`() {
        assertEquals("fil-PH", normalizeLanguageCode("fil-ph"))
        assertEquals("fil-PH", normalizeLanguageCode("FIL-PH"))
    }

    // --- Scenario Tests ---

    @Test
    fun `common translation pairs are all valid`() {
        val commonPairs = listOf(
            LanguagePair("en-US", "es-ES"), // English to Spanish
            LanguagePair("en-US", "fr-FR"), // English to French
            LanguagePair("en-US", "de-DE"), // English to German
            LanguagePair("en-US", "ja-JP"), // English to Japanese
            LanguagePair("en-US", "zh-CN"), // English to Mandarin
            LanguagePair("zh-CN", "zh-HK"), // Mandarin to Cantonese
            LanguagePair("ja-JP", "ko-KR"), // Japanese to Korean
        )

        commonPairs.forEach { pair ->
            assertTrue(isValidLanguagePair(pair))
            assertTrue(isSupportedLanguage(pair.source))
            assertTrue(isSupportedLanguage(pair.target))
        }
    }

    @Test
    fun `asian language group all supported`() {
        val asianLanguages = listOf(
            "ja-JP", "zh-CN", "zh-HK", "ko-KR",
            "th-TH", "vi-VN", "fil-PH", "ms-MY", "id-ID"
        )

        asianLanguages.forEach { lang ->
            assertTrue(isSupportedLanguage(lang))
        }
    }

    @Test
    fun `european language group all supported`() {
        val europeanLanguages = listOf(
            "en-US", "fr-FR", "de-DE", "es-ES",
            "it-IT", "pt-BR", "ru-RU"
        )

        europeanLanguages.forEach { lang ->
            assertTrue(isSupportedLanguage(lang))
        }
    }

    // --- Helper Functions (Would be in actual production code) ---

    private fun isValidLanguageCodeFormat(code: String): Boolean {
        if (!code.contains("-")) return false
        val parts = code.split("-")
        if (parts.size != 2) return false

        val langCode = parts[0]
        val countryCode = parts[1]

        return langCode.length in 2..3 &&
               langCode.all { it.isLowerCase() || it.isLetter() } &&
               countryCode.length == 2 &&
               countryCode.all { it.isUpperCase() || it.isLetter() }
    }

    private fun isSupportedLanguage(code: String): Boolean {
        return supportedLanguages.contains(code)
    }

    private fun isValidLanguagePair(pair: LanguagePair): Boolean {
        return pair.source != pair.target &&
               isSupportedLanguage(pair.source) &&
               isSupportedLanguage(pair.target)
    }

    private fun normalizeLanguageCode(code: String): String {
        val parts = code.split("-")
        if (parts.size != 2) return code

        val langCode = parts[0].lowercase()
        val countryCode = parts[1].uppercase()

        return "$langCode-$countryCode"
    }

    private data class LanguagePair(
        val source: String,
        val target: String
    )
}
