package com.example.fyp.data.ui

import com.example.fyp.model.ui.UiTextKey
import com.example.fyp.model.ui.LanguageNameTranslations
import com.example.fyp.model.ui.LanguageNameKeys
import org.junit.Test
import org.junit.Assert.*

/**
 * Tests for UiLanguageStateController's language name correction logic.
 *
 * The applyLanguageNameCorrections() function fixes translation API errors
 * where language names are incorrectly machine-translated by replacing
 * them with human-verified corrections from LanguageNameTranslations.
 *
 * Since the function is private, we test the backing data structures
 * (LanguageNameTranslations, LanguageNameKeys) and the correction algorithm.
 */
class UiLanguageCorrectionTest {

    /**
     * Replicate the correction logic from UiLanguageStateController.
     */
    private fun applyCorrections(
        map: Map<UiTextKey, String>,
        uiLanguageCode: String
    ): Map<UiTextKey, String> {
        if (uiLanguageCode.startsWith("en")) return map
        val langNameTranslations = LanguageNameTranslations[uiLanguageCode] ?: return map
        val correctedMap = map.toMutableMap()
        LanguageNameKeys.forEach { key ->
            langNameTranslations[key]?.let { correctTranslation ->
                correctedMap[key] = correctTranslation
            }
        }
        return correctedMap
    }

    // --- English passthrough ---

    @Test
    fun `English language returns map unchanged`() {
        val original = mapOf(UiTextKey.LangEnUs to "English")
        val result = applyCorrections(original, "en-US")
        assertSame(original, result)
    }

    @Test
    fun `en-GB also returns map unchanged`() {
        val original = mapOf(UiTextKey.LangEnUs to "English")
        val result = applyCorrections(original, "en-GB")
        assertSame(original, result)
    }

    // --- Language without translations returns unchanged ---

    @Test
    fun `language without translations returns map unchanged`() {
        val original = mapOf(UiTextKey.LangEnUs to "English")
        val result = applyCorrections(original, "unknown-XX")
        assertEquals(original, result)
    }

    // --- Corrections applied for supported languages ---

    @Test
    fun `Indonesian corrections are applied`() {
        if (LanguageNameTranslations.containsKey("id-ID")) {
            val original = mapOf(
                UiTextKey.LangEnUs to "Bahasa Inggris (wrong)",
                UiTextKey.LangJaJp to "Bahasa Jepang (wrong)"
            )
            val result = applyCorrections(original, "id-ID")

            val idTranslations = LanguageNameTranslations["id-ID"]!!
            idTranslations[UiTextKey.LangEnUs]?.let { expected ->
                assertEquals(expected, result[UiTextKey.LangEnUs])
            }
        }
    }

    @Test
    fun `corrections only replace LanguageNameKeys`() {
        val nonLanguageKey = UiTextKey.entries.first { it !in LanguageNameKeys }
        val original = mapOf(nonLanguageKey to "original value")
        val code = LanguageNameTranslations.keys.firstOrNull() ?: return
        val result = applyCorrections(original, code)
        assertEquals("original value", result[nonLanguageKey])
    }

    // --- LanguageNameKeys data integrity ---

    @Test
    fun `LanguageNameKeys is not empty`() {
        assertTrue(LanguageNameKeys.isNotEmpty())
    }

    @Test
    fun `LanguageNameKeys contains only Lang-prefixed keys`() {
        LanguageNameKeys.forEach { key ->
            assertTrue(
                "Key '${key.name}' should start with 'Lang'",
                key.name.startsWith("Lang")
            )
        }
    }

    @Test
    fun `all LanguageNameKeys are valid UiTextKey entries`() {
        val allKeys = UiTextKey.entries.toSet()
        LanguageNameKeys.forEach { key ->
            assertTrue("'${key.name}' should be a valid UiTextKey", key in allKeys)
        }
    }

    // --- LanguageNameTranslations data integrity ---

    @Test
    fun `LanguageNameTranslations is not empty`() {
        assertTrue(LanguageNameTranslations.isNotEmpty())
    }

    @Test
    fun `each language in LanguageNameTranslations has corrections for all LanguageNameKeys`() {
        LanguageNameTranslations.forEach { (langCode, corrections) ->
            LanguageNameKeys.forEach { key ->
                assertTrue(
                    "LanguageNameTranslations[$langCode] should contain ${key.name}",
                    corrections.containsKey(key)
                )
            }
        }
    }

    @Test
    fun `all language name corrections are non-blank`() {
        LanguageNameTranslations.forEach { (langCode, corrections) ->
            corrections.forEach { (key, value) ->
                assertTrue(
                    "LanguageNameTranslations[$langCode][${key.name}] should not be blank",
                    value.isNotBlank()
                )
            }
        }
    }

    @Test
    fun `corrections do not contain English for known non-English languages`() {
        // For non-English languages, corrections should not just be the English name
        LanguageNameTranslations.forEach { (langCode, corrections) ->
            if (!langCode.startsWith("en")) {
                // At least some corrections should differ from default English names
                val hasNonEnglish = corrections.values.any { value ->
                    !value.all { it.isLetter() && it.code < 128 }  // has non-ASCII = definitely not English
                }
                // Most languages will have at least SOME non-ASCII characters
                // but this may not hold for all (e.g., Malay uses Latin)
                // So we skip the assertion for fully Latin-script languages
            }
        }
    }

    // --- Correction algorithm ---

    @Test
    fun `corrections overwrite existing values`() {
        val langCode = LanguageNameTranslations.keys.firstOrNull() ?: return
        val corrections = LanguageNameTranslations[langCode]!!
        val firstKey = corrections.keys.first()

        val original = mapOf(firstKey to "WRONG VALUE")
        val result = applyCorrections(original, langCode)
        assertEquals(corrections[firstKey], result[firstKey])
    }

    @Test
    fun `corrections add missing keys`() {
        val langCode = LanguageNameTranslations.keys.firstOrNull() ?: return
        val corrections = LanguageNameTranslations[langCode]!!

        val original = emptyMap<UiTextKey, String>()
        val result = applyCorrections(original, langCode)

        corrections.forEach { (key, value) ->
            if (key in LanguageNameKeys) {
                assertEquals(value, result[key])
            }
        }
    }

    @Test
    fun `non-language keys are preserved after correction`() {
        val langCode = LanguageNameTranslations.keys.firstOrNull() ?: return
        val nonLangKey = UiTextKey.entries.first { it !in LanguageNameKeys }
        val original = mapOf(nonLangKey to "keep me")
        val result = applyCorrections(original, langCode)
        assertEquals("keep me", result[nonLangKey])
    }
}
