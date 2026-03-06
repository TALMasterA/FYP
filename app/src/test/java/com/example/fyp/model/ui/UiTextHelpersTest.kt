package com.example.fyp.model.ui

import org.junit.Assert.*
import org.junit.Test

/**
 * Unit tests for UiTextHelpers – buildUiTextMap, baseUiTextsHash, getUiText, replaceTemplateTokens.
 *
 * Tests:
 *  1. buildUiTextMap with matching part count returns all entries
 *  2. buildUiTextMap with fewer parts falls back to BaseUiTexts
 *  3. buildUiTextMap with empty string still produces full map
 *  4. ensureContains falls back when token is missing
 *  5. ensureContains keeps translation when token is present
 *  6. Language name override applied for non-English locale
 *  7. Language name override skipped for en-US
 *  8. Language name override skipped when locale not in LanguageNameTranslations
 *  9. baseUiTextsHash returns consistent value
 * 10. getUiText returns correct fallback
 * 11. replaceTemplateTokens replaces multiple tokens
 * 12. replaceTemplateTokens returns original when no matching tokens
 * 13. BaseUiTexts size matches UiTextKey entries count
 */
class UiTextHelpersTest {

    // ── buildUiTextMap ──

    @Test
    fun `buildUiTextMap with matching part count returns all translated entries`() {
        val keys = UiTextKey.entries
        val parts = keys.indices.map { "translated_$it" }
        val joined = parts.joinToString("\u0001")

        val map = buildUiTextMap(joined)

        assertEquals(keys.size, map.size)
        // First key should be translated (unless token fallback overrode it)
        assertEquals("translated_0", map[keys[0]])
    }

    @Test
    fun `buildUiTextMap with fewer parts falls back to BaseUiTexts for missing`() {
        // Only provide 3 parts — everything beyond index 2 should fallback
        val joined = "a\u0001b\u0001c"

        val map = buildUiTextMap(joined)

        assertEquals(UiTextKey.entries.size, map.size)
        assertEquals("a", map[UiTextKey.entries[0]])
        assertEquals("b", map[UiTextKey.entries[1]])
        assertEquals("c", map[UiTextKey.entries[2]])

        // A key beyond index 2 should use BaseUiTexts fallback
        val key3 = UiTextKey.entries[3]
        val expected = BaseUiTexts.getOrNull(3).orEmpty()
        assertEquals(expected, map[key3])
    }

    @Test
    fun `buildUiTextMap with empty string produces full map from fallbacks`() {
        val map = buildUiTextMap("")

        assertEquals(UiTextKey.entries.size, map.size)
        // Index 0 gets the empty-string part; the rest should be BaseUiTexts fallbacks
        val key1 = UiTextKey.entries[1]
        assertEquals(BaseUiTexts.getOrNull(1).orEmpty(), map[key1])
    }

    // ── ensureContains token-guarding ──

    @Test
    fun `ensureContains falls back when required token is missing from translation`() {
        // HistorySessionTitleTemplate requires {id}
        val key = UiTextKey.HistorySessionTitleTemplate
        val idx = key.ordinal

        // Build parts where the template key has no {id} token
        val parts = BaseUiTexts.toMutableList()
        parts[idx] = "Translated session title without the token"
        val joined = parts.joinToString("\u0001")

        val map = buildUiTextMap(joined)

        // Should fall back to BaseUiTexts value (which contains {id})
        val fallback = BaseUiTexts[idx]
        assertEquals(fallback, map[key])
        assertTrue(map[key]!!.contains("{id}"))
    }

    @Test
    fun `ensureContains keeps translation when token is present`() {
        val key = UiTextKey.HistorySessionTitleTemplate
        val idx = key.ordinal

        val parts = BaseUiTexts.toMutableList()
        parts[idx] = "Sitzung {id}"
        val joined = parts.joinToString("\u0001")

        val map = buildUiTextMap(joined)

        assertEquals("Sitzung {id}", map[key])
    }

    @Test
    fun `ensureContains falls back when one of multiple tokens is missing`() {
        // PaginationPageLabelTemplate requires both {page} and {total}
        val key = UiTextKey.PaginationPageLabelTemplate
        val idx = key.ordinal

        val parts = BaseUiTexts.toMutableList()
        parts[idx] = "Page {page} of X" // missing {total}
        val joined = parts.joinToString("\u0001")

        val map = buildUiTextMap(joined)

        val fallback = BaseUiTexts[idx]
        assertEquals(fallback, map[key])
        assertTrue(map[key]!!.contains("{page}") && map[key]!!.contains("{total}"))
    }

    // ── Language name overrides ──

    @Test
    fun `language name override applied for known non-English locale`() {
        val parts = BaseUiTexts.toMutableList()
        val joined = parts.joinToString("\u0001")

        val map = buildUiTextMap(joined, "ja-JP")

        // Japanese translations should override language name keys
        assertEquals("\u82F1\u8A9E", map[UiTextKey.LangEnUs]) // 英語
        assertEquals("\u65E5\u672C\u8A9E", map[UiTextKey.LangJaJp]) // 日本語
    }

    @Test
    fun `language name override skipped for en-US`() {
        val parts = BaseUiTexts.toMutableList()
        val joined = parts.joinToString("\u0001")

        val mapEn = buildUiTextMap(joined, "en-US")
        val mapNoLang = buildUiTextMap(joined)

        // en-US should not apply overrides — both should be identical
        assertEquals(mapNoLang[UiTextKey.LangEnUs], mapEn[UiTextKey.LangEnUs])
    }

    @Test
    fun `language name override skipped for unknown locale`() {
        val parts = BaseUiTexts.toMutableList()
        val joined = parts.joinToString("\u0001")

        val map = buildUiTextMap(joined, "xx-XX")

        // Unknown locale — no override applied, should keep BaseUiTexts value
        val expected = BaseUiTexts[UiTextKey.LangEnUs.ordinal]
        assertEquals(expected, map[UiTextKey.LangEnUs])
    }

    // ── baseUiTextsHash ──

    @Test
    fun `baseUiTextsHash returns consistent value`() {
        val hash1 = baseUiTextsHash()
        val hash2 = baseUiTextsHash()
        assertEquals(hash1, hash2)
    }

    // ── getUiText ──

    @Test
    fun `getUiText returns correct BaseUiTexts value`() {
        val key = UiTextKey.AzureRecognizeButton
        val expected = BaseUiTexts.getOrNull(key.ordinal).orEmpty()
        assertEquals(expected, getUiText(key))
    }

    // ── replaceTemplateTokens ──

    @Test
    fun `replaceTemplateTokens replaces multiple tokens`() {
        val result = "Page {page} of {total}".replaceTemplateTokens(
            "page" to "3",
            "total" to "10"
        )
        assertEquals("Page 3 of 10", result)
    }

    @Test
    fun `replaceTemplateTokens returns original when no matching tokens`() {
        val text = "No tokens here"
        val result = text.replaceTemplateTokens("page" to "1")
        assertEquals("No tokens here", result)
    }

    // ── BaseUiTexts consistency ──

    @Test
    fun `BaseUiTexts size matches UiTextKey entries count`() {
        assertEquals(
            "BaseUiTexts size must match UiTextKey.entries.size",
            UiTextKey.entries.size,
            BaseUiTexts.size
        )
    }

    // ── LanguageNameKeys coverage ──

    @Test
    fun `every LanguageNameTranslations locale has entries for all LanguageNameKeys it covers`() {
        for ((locale, translations) in LanguageNameTranslations) {
            for (key in translations.keys) {
                assertTrue(
                    "Locale $locale has key $key that is not in LanguageNameKeys",
                    key in LanguageNameKeys
                )
            }
        }
    }

    // ── Quiz template token guarding ──

    @Test
    fun `quiz template tokens are guarded by ensureContains`() {
        val key = UiTextKey.QuizQuestionTemplate
        val idx = key.ordinal

        val parts = BaseUiTexts.toMutableList()
        parts[idx] = "Question without tokens"
        val joined = parts.joinToString("\u0001")

        val map = buildUiTextMap(joined)

        // Should fall back because {current} and {total} are missing
        val fallback = BaseUiTexts[idx]
        assertEquals(fallback, map[key])
    }

    // ── Friends template token guarding ──

    @Test
    fun `friends remove dialog template is guarded`() {
        val key = UiTextKey.FriendsRemoveDialogMessage
        val idx = key.ordinal

        val parts = BaseUiTexts.toMutableList()
        parts[idx] = "Remove friend?"
        val joined = parts.joinToString("\u0001")

        val map = buildUiTextMap(joined)

        val fallback = BaseUiTexts[idx]
        assertEquals(fallback, map[key])
        assertTrue(map[key]!!.contains("{username}"))
    }
}
