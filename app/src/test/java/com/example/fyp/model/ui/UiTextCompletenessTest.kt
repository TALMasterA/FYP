package com.example.fyp.model.ui

import org.junit.Assert.*
import org.junit.Test

/**
 * Unit tests for UiText translation system completeness and consistency.
 *
 * Verifies that:
 *  1. Every UiTextKey has a non-empty English default in BaseUiTexts
 *  2. BaseUiTexts size matches UiTextKey entries count
 *  3. All LanguageNameKeys are valid UiTextKey entries
 *  4. Every locale in LanguageNameTranslations has entries for all LanguageNameKeys
 *  5. No LanguageNameTranslations locale has empty translation values
 *  6. buildUiTextMap produces a map with all UiTextKey entries
 *  7. Template tokens are preserved after buildUiTextMap
 *  8. New template keys have their tokens guarded
 */
class UiTextCompletenessTest {

    // ── 1. English defaults ──

    @Test
    fun `every UiTextKey has a non-empty English default in BaseUiTexts`() {
        for (key in UiTextKey.entries) {
            val value = BaseUiTexts[key.ordinal]
            assertFalse(
                "BaseUiTexts[${key.name}] (ordinal ${key.ordinal}) must not be blank",
                value.isBlank()
            )
        }
    }

    // ── 2. Size alignment ──

    @Test
    fun `BaseUiTexts size matches UiTextKey entries count`() {
        assertEquals(
            "BaseUiTexts size must equal UiTextKey.entries.size",
            UiTextKey.entries.size,
            BaseUiTexts.size
        )
    }

    // ── 3. LanguageNameKeys validity ──

    @Test
    fun `all LanguageNameKeys are valid UiTextKey entries`() {
        val allKeys = UiTextKey.entries.toSet()
        for (langKey in LanguageNameKeys) {
            assertTrue(
                "LanguageNameKeys contains $langKey which is not in UiTextKey.entries",
                langKey in allKeys
            )
        }
    }

    // ── 4. LanguageNameTranslations coverage ──

    @Test
    fun `every locale in LanguageNameTranslations has entry for all LanguageNameKeys`() {
        for ((locale, translations) in LanguageNameTranslations) {
            for (expectedKey in LanguageNameKeys) {
                assertTrue(
                    "Locale '$locale' is missing translation for key ${expectedKey.name}",
                    translations.containsKey(expectedKey)
                )
            }
        }
    }

    // ── 5. No empty translation values ──

    @Test
    fun `no LanguageNameTranslations locale has empty translation values`() {
        for ((locale, translations) in LanguageNameTranslations) {
            for ((key, value) in translations) {
                assertFalse(
                    "Locale '$locale' has blank value for key ${key.name}",
                    value.isBlank()
                )
            }
        }
    }

    // ── 6. buildUiTextMap produces full map ──

    @Test
    fun `buildUiTextMap produces map with all UiTextKey entries`() {
        val joined = BaseUiTexts.joinToString("\u0001")
        val map = buildUiTextMap(joined)

        assertEquals(
            "buildUiTextMap should produce an entry for every UiTextKey",
            UiTextKey.entries.size,
            map.size
        )
    }

    // ── 7. Template tokens preserved ──

    @Test
    fun `template tokens are preserved after buildUiTextMap`() {
        val joined = BaseUiTexts.joinToString("\u0001")
        val map = buildUiTextMap(joined)

        assertTrue(
            "HistorySessionTitleTemplate must contain {id}",
            map[UiTextKey.HistorySessionTitleTemplate]!!.contains("{id}")
        )
        assertTrue(
            "HistoryItemsCountTemplate must contain {count}",
            map[UiTextKey.HistoryItemsCountTemplate]!!.contains("{count}")
        )
        assertTrue(
            "PaginationPageLabelTemplate must contain {page}",
            map[UiTextKey.PaginationPageLabelTemplate]!!.contains("{page}")
        )
        assertTrue(
            "PaginationPageLabelTemplate must contain {total}",
            map[UiTextKey.PaginationPageLabelTemplate]!!.contains("{total}")
        )
        assertTrue(
            "QuizQuestionTemplate must contain {current}",
            map[UiTextKey.QuizQuestionTemplate]!!.contains("{current}")
        )
        assertTrue(
            "QuizQuestionTemplate must contain {total}",
            map[UiTextKey.QuizQuestionTemplate]!!.contains("{total}")
        )
        assertTrue(
            "FriendsRemoveDialogMessage must contain {username}",
            map[UiTextKey.FriendsRemoveDialogMessage]!!.contains("{username}")
        )
        assertTrue(
            "SettingsScaleTemplate must contain {pct}",
            map[UiTextKey.SettingsScaleTemplate]!!.contains("{pct}")
        )
    }

    // ── 8. New template keys have tokens guarded ──

    @Test
    fun `new template keys have tokens guarded`() {
        val joined = BaseUiTexts.joinToString("\u0001")
        val map = buildUiTextMap(joined)

        assertTrue(
            "FavoritesDeleteConfirm must contain {count}",
            map[UiTextKey.FavoritesDeleteConfirm]!!.contains("{count}")
        )
        assertTrue(
            "WordBankDeleteConfirm must contain {word}",
            map[UiTextKey.WordBankDeleteConfirm]!!.contains("{word}")
        )
        assertTrue(
            "ShopUnlockConfirmTitle must contain {name}",
            map[UiTextKey.ShopUnlockConfirmTitle]!!.contains("{name}")
        )
        assertTrue(
            "ShopUnlockCost must contain {cost}",
            map[UiTextKey.ShopUnlockCost]!!.contains("{cost}")
        )
        assertTrue(
            "ShopYourCoins must contain {coins}",
            map[UiTextKey.ShopYourCoins]!!.contains("{coins}")
        )
        assertTrue(
            "SettingsPrimaryLanguageCooldownMessageHours must contain {hours}",
            map[UiTextKey.SettingsPrimaryLanguageCooldownMessageHours]!!.contains("{hours}")
        )
    }
}
