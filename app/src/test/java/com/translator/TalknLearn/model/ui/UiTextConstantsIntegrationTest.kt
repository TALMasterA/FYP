package com.translator.TalknLearn.model.ui

import com.translator.TalknLearn.domain.learning.GenerationEligibility
import com.translator.TalknLearn.model.user.UserSettings
import org.junit.Assert.*
import org.junit.Test

/**
 * Integration test: verifies that numeric claims in UI text strings
 * match the actual constants defined in the codebase.
 *
 * This prevents UI text from drifting out of sync with business logic
 * when constants are updated but translations are not.
 */
class UiTextConstantsIntegrationTest {

    // ── History Limit Constants ──

    @Test
    fun `BASE_HISTORY_LIMIT constant is 30`() {
        assertEquals(30, UserSettings.BASE_HISTORY_LIMIT)
    }

    @Test
    fun `MAX_HISTORY_LIMIT constant is 60`() {
        assertEquals(60, UserSettings.MAX_HISTORY_LIMIT)
    }

    @Test
    fun `HISTORY_EXPANSION_COST constant is 1000`() {
        assertEquals(1000, UserSettings.HISTORY_EXPANSION_COST)
    }

    @Test
    fun `HISTORY_EXPANSION_INCREMENT constant is 10`() {
        assertEquals(10, UserSettings.HISTORY_EXPANSION_INCREMENT)
    }

    // ── Favorites Limit ──

    @Test
    fun `MAX_FAVORITE_RECORDS constant is 20`() {
        assertEquals(20, UserSettings.MAX_FAVORITE_RECORDS)
    }

    @Test
    fun `zh-TW favorites limit message mentions 20`() {
        val text = ZhTwUiTexts[UiTextKey.FavoritesLimitMessage]!!
        assertTrue("Favorites limit message should mention '20' but was: $text", text.contains("20"))
    }

    @Test
    fun `Cantonese favorites limit message mentions 20`() {
        val text = CantoneseUiTexts[UiTextKey.FavoritesLimitMessage]!!
        assertTrue("Favorites limit message should mention '20' but was: $text", text.contains("20"))
    }

    // ── Learning Sheet Regeneration Threshold ──

    @Test
    fun `MIN_RECORDS_FOR_LEARNING_SHEET constant is 5`() {
        assertEquals(5, GenerationEligibility.MIN_RECORDS_FOR_LEARNING_SHEET)
    }

    @Test
    fun `zh-TW learning regen text mentions 5`() {
        val text = ZhTwUiTexts[UiTextKey.LearningRegenInfoMessage]!!
        assertTrue("Learning regen message should mention '5' but was: $text", text.contains("5"))
    }

    // ── Word Bank Regeneration Threshold ──

    @Test
    fun `MIN_RECORDS_FOR_WORD_BANK constant is 20`() {
        assertEquals(20, GenerationEligibility.MIN_RECORDS_FOR_WORD_BANK)
    }

    @Test
    fun `zh-TW word bank regen text mentions 20`() {
        val text = ZhTwUiTexts[UiTextKey.WordBankRegenInfoMessage]!!
        assertTrue("Word bank regen message should mention '20' but was: $text", text.contains("20"))
    }

    // ── Cooldown Period ──

    @Test
    fun `PRIMARY_LANGUAGE_CHANGE_COOLDOWN is 30 days`() {
        val thirtyDaysMs = 30L * 24 * 60 * 60 * 1000
        assertEquals(thirtyDaysMs, UserSettings.PRIMARY_LANGUAGE_CHANGE_COOLDOWN_MS)
    }

    @Test
    fun `USERNAME_CHANGE_COOLDOWN is 30 days`() {
        val thirtyDaysMs = 30L * 24 * 60 * 60 * 1000
        assertEquals(thirtyDaysMs, UserSettings.USERNAME_CHANGE_COOLDOWN_MS)
    }

    @Test
    fun `zh-TW cooldown messages mention 30 days`() {
        val langText = ZhTwUiTexts[UiTextKey.SettingsPrimaryLanguageConfirmMessage]!!
        assertTrue("Primary language confirm should mention '30' but was: $langText", langText.contains("30"))

        val usernameText = ZhTwUiTexts[UiTextKey.SettingsUsernameConfirmMessage]!!
        assertTrue("Username confirm should mention '30' but was: $usernameText", usernameText.contains("30"))
    }

    // ── Shop Expansion Uses Dynamic Placeholders ──

    @Test
    fun `ShopBuyHistoryExpansion does not hardcode wrong numbers in any language`() {
        // English must use {increment} and {cost} placeholders
        val enText = BaseUiTexts.getOrNull(UiTextKey.ShopBuyHistoryExpansion.ordinal).orEmpty()
        assertTrue("English must use {increment}", enText.contains("{increment}"))
        assertTrue("English must use {cost}", enText.contains("{cost}"))

        // zh-TW must use {increment} and {cost} placeholders
        val zhTwText = ZhTwUiTexts[UiTextKey.ShopBuyHistoryExpansion]!!
        assertTrue("zh-TW must use {increment}", zhTwText.contains("{increment}"))
        assertTrue("zh-TW must use {cost}", zhTwText.contains("{cost}"))

        // Cantonese must use {increment} and {cost} placeholders
        val cantoneseText = CantoneseUiTexts[UiTextKey.ShopBuyHistoryExpansion]!!
        assertTrue("Cantonese must use {increment}", cantoneseText.contains("{increment}"))
        assertTrue("Cantonese must use {cost}", cantoneseText.contains("{cost}"))
    }

    // ── Coin Earning Rule ──

    @Test
    fun `quiz coin rules text mentions 10 records requirement`() {
        val zhTwText = ZhTwUiTexts[UiTextKey.QuizCoinRulePlus10]!!
        assertTrue("zh-TW coin rule should mention '10' but was: $zhTwText", zhTwText.contains("10"))

        val cantoneseText = CantoneseUiTexts[UiTextKey.QuizCoinRulePlus10]!!
        assertTrue("Cantonese coin rule should mention '10' but was: $cantoneseText", cantoneseText.contains("10"))
    }
}
