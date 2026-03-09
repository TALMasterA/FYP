package com.example.fyp.model.ui

import org.junit.Test
import org.junit.Assert.*

/**
 * Ensures that every translation map (Cantonese, Traditional Chinese)
 * has a translation for ALL UiTextKey entries.
 *
 * Missing keys cause the app to fall back to the English default,
 * which breaks the UX for non-English users.
 */
class TranslationCompletenessTest {

    @Test
    fun `Cantonese translation map contains all UiTextKey entries`() {
        val allKeys = UiTextKey.entries.toSet()
        val cantoneseKeys = CantoneseUiTexts.keys

        val missing = allKeys - cantoneseKeys
        assertTrue(
            "CantoneseUiTexts is missing ${missing.size} key(s): ${missing.joinToString { it.name }}",
            missing.isEmpty()
        )
    }

    @Test
    fun `Traditional Chinese translation map contains all UiTextKey entries`() {
        val allKeys = UiTextKey.entries.toSet()
        val zhTwKeys = ZhTwUiTexts.keys

        val missing = allKeys - zhTwKeys
        assertTrue(
            "ZhTwUiTexts is missing ${missing.size} key(s): ${missing.joinToString { it.name }}",
            missing.isEmpty()
        )
    }

    @Test
    fun `all Cantonese translations are non-blank`() {
        CantoneseUiTexts.forEach { (key, value) ->
            assertTrue(
                "CantoneseUiTexts[${key.name}] should not be blank",
                value.isNotBlank()
            )
        }
    }

    @Test
    fun `all Traditional Chinese translations are non-blank`() {
        ZhTwUiTexts.forEach { (key, value) ->
            assertTrue(
                "ZhTwUiTexts[${key.name}] should not be blank",
                value.isNotBlank()
            )
        }
    }

    @Test
    fun `Cantonese and Traditional Chinese have same number of entries`() {
        assertEquals(
            "CantoneseUiTexts and ZhTwUiTexts should have the same number of entries",
            CantoneseUiTexts.size,
            ZhTwUiTexts.size
        )
    }

    @Test
    fun `translation maps have no extra keys beyond UiTextKey entries`() {
        val allKeys = UiTextKey.entries.toSet()

        val extraCantonese = CantoneseUiTexts.keys - allKeys
        assertTrue(
            "CantoneseUiTexts has ${extraCantonese.size} extra key(s) not in UiTextKey",
            extraCantonese.isEmpty()
        )

        val extraZhTw = ZhTwUiTexts.keys - allKeys
        assertTrue(
            "ZhTwUiTexts has ${extraZhTw.size} extra key(s) not in UiTextKey",
            extraZhTw.isEmpty()
        )
    }
}
