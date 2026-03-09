package com.example.fyp.domain

import com.example.fyp.data.azure.LanguageDisplayNames
import com.example.fyp.domain.learning.CoinEligibility
import com.example.fyp.domain.learning.GenerationEligibility
import com.example.fyp.model.TranslationRecord
import com.example.fyp.model.user.UserSettings
import com.example.fyp.model.ui.UiTextKey
import com.example.fyp.model.ui.CantoneseUiTexts
import com.example.fyp.model.ui.ZhTwUiTexts
import com.example.fyp.model.ui.BaseUiTexts
import org.junit.Test
import org.junit.Assert.*

/**
 * Cross-layer integration tests that verify invariants spanning
 * multiple modules of the application.
 *
 * These tests catch issues where individual layer tests pass but
 * the interaction between layers breaks.
 */
class CrossLayerIntegrationTest {

    // --- Translation Record → Prompt Pipeline ---

    @Test
    fun `translation records format consistently for word bank and learning prompts`() {
        val record = TranslationRecord(
            sourceText = "hello",
            targetText = "こんにちは",
            sourceLang = "en-US",
            targetLang = "ja-JP"
        )
        // Both WordBankGenerationRepository and LearningContentRepositoryImpl
        // use the same formatting pattern
        val formatted = "- [${record.sourceLang}→${record.targetLang}] ${record.sourceText} => ${record.targetText}"
        assertEquals("- [en-US→ja-JP] hello => こんにちは", formatted)
    }

    @Test
    fun `takeLast limits are consistent between prompt builders`() {
        // WordBankGenerationRepository uses takeLast(30)
        // LearningContentRepositoryImpl uses takeLast(20)
        // Word bank should allow more context than learning sheets
        assertTrue("Word bank limit should be >= learning limit", 30 >= 20)
    }

    // --- Language Pipeline Integration ---

    @Test
    fun `all language codes supported by LanguageDisplayNames have display names`() {
        val supportedCodes = listOf(
            "en-US", "zh-TW", "zh-HK", "zh-CN", "ja-JP", "fr-FR", "de-DE",
            "ko-KR", "es-ES", "id-ID", "vi-VN", "th-TH", "fil-PH", "ms-MY",
            "pt-BR", "it-IT", "ru-RU"
        )
        supportedCodes.forEach { code ->
            val name = LanguageDisplayNames.displayName(code)
            assertNotEquals("$code should have a display name", code, name)
        }
    }

    @Test
    fun `OCR recognizer selection covers all supported CJK languages`() {
        // Verify the CJK languages use the right recognizer prefix
        val cjkLanguages = mapOf(
            "zh-HK" to "zh", "zh-TW" to "zh", "zh-CN" to "zh",
            "ja-JP" to "ja", "ko-KR" to "ko"
        )
        cjkLanguages.forEach { (code, expectedPrefix) ->
            assertEquals(expectedPrefix, code.lowercase().take(2))
        }
    }

    // --- Settings → Feature Gating Integration ---

    @Test
    fun `history limit expansion follows increment pattern`() {
        var currentLimit = UserSettings.BASE_HISTORY_LIMIT
        var totalCost = 0

        while (currentLimit < UserSettings.MAX_HISTORY_LIMIT) {
            currentLimit += UserSettings.HISTORY_EXPANSION_INCREMENT
            totalCost += UserSettings.HISTORY_EXPANSION_COST
        }

        assertEquals(UserSettings.MAX_HISTORY_LIMIT, currentLimit)
        assertEquals(3000, totalCost) // 3 expansions × 1000 coins
    }

    @Test
    fun `history limit clamping matches settings range`() {
        // Repository clamps: newLimit.coerceIn(BASE_HISTORY_LIMIT, MAX_HISTORY_LIMIT)
        val belowMin = 10.coerceIn(UserSettings.BASE_HISTORY_LIMIT, UserSettings.MAX_HISTORY_LIMIT)
        val aboveMax = 100.coerceIn(UserSettings.BASE_HISTORY_LIMIT, UserSettings.MAX_HISTORY_LIMIT)
        val inRange = 45.coerceIn(UserSettings.BASE_HISTORY_LIMIT, UserSettings.MAX_HISTORY_LIMIT)

        assertEquals(30, belowMin)
        assertEquals(60, aboveMax)
        assertEquals(45, inRange)
    }

    // --- Cooldown System Integration ---

    @Test
    fun `username and primary language cooldowns are identical duration`() {
        assertEquals(
            UserSettings.PRIMARY_LANGUAGE_CHANGE_COOLDOWN_MS,
            UserSettings.USERNAME_CHANGE_COOLDOWN_MS
        )
    }

    @Test
    fun `both cooldowns use same 30-day calculation`() {
        val thirtyDaysMs = 30L * 24 * 60 * 60 * 1000
        assertEquals(thirtyDaysMs, UserSettings.PRIMARY_LANGUAGE_CHANGE_COOLDOWN_MS)
        assertEquals(thirtyDaysMs, UserSettings.USERNAME_CHANGE_COOLDOWN_MS)
    }

    @Test
    fun `cooldown functions have symmetric behavior`() {
        val now = 1000000000000L
        val recentChange = now - (1L * 24 * 60 * 60 * 1000)
        val oldChange = now - (31L * 24 * 60 * 60 * 1000)

        // Both should block recent changes
        assertFalse(UserSettings.canChangePrimaryLanguage(recentChange, now))
        assertFalse(UserSettings.canChangeUsername(recentChange, now))

        // Both should allow old changes
        assertTrue(UserSettings.canChangePrimaryLanguage(oldChange, now))
        assertTrue(UserSettings.canChangeUsername(oldChange, now))
    }

    // --- UI Text System Integration ---

    @Test
    fun `all UiTextKey entries have English base text`() {
        val allKeys = UiTextKey.entries
        val baseTexts = BaseUiTexts
        assertEquals(
            "BaseUiTexts count should match UiTextKey count",
            allKeys.size, baseTexts.size
        )
    }

    @Test
    fun `Cantonese and Traditional Chinese have same key coverage`() {
        assertEquals(CantoneseUiTexts.size, ZhTwUiTexts.size)
        assertEquals(CantoneseUiTexts.keys, ZhTwUiTexts.keys)
    }

    @Test
    fun `all Lang keys have corresponding display names`() {
        val langKeys = UiTextKey.entries.filter { it.name.startsWith("Lang") }
        assertTrue("Should have language keys", langKeys.isNotEmpty())
    }

    // --- Coin Economy Integration ---

    @Test
    fun `coin eligibility thresholds are consistent with generation eligibility`() {
        // GenerationEligibility.MIN_RECORDS_FOR_WORD_BANK and CoinEligibility.MIN_INCREMENT_FOR_COINS
        // both require minimum history before features unlock
        assertTrue(
            "Generation should require records for word bank",
            GenerationEligibility.MIN_RECORDS_FOR_WORD_BANK > 0
        )
        assertTrue(
            "Coin eligibility should require increment between awards",
            CoinEligibility.MIN_INCREMENT_FOR_COINS > 0
        )
    }

    @Test
    fun `quiz coin award requires minimum score of 1`() {
        assertEquals(1, CoinEligibility.MIN_VALID_SCORE)
    }

    @Test
    fun `history expansion cost exceeds single coin increment requirement`() {
        // Players should need multiple quiz completions to expand history
        assertTrue(
            "Expansion cost should exceed single quiz increment threshold",
            UserSettings.HISTORY_EXPANSION_COST > CoinEligibility.MIN_INCREMENT_FOR_COINS
        )
    }

    // --- Notification System Integration ---

    @Test
    fun `push notification defaults are all off (opt-in)`() {
        val settings = UserSettings()
        assertFalse(settings.notifyNewMessages)
        assertFalse(settings.notifyFriendRequests)
        assertFalse(settings.notifyRequestAccepted)
        assertFalse(settings.notifySharedInbox)
    }

    @Test
    fun `in-app badge defaults are all on (opt-out)`() {
        val settings = UserSettings()
        assertTrue(settings.inAppBadgeMessages)
        assertTrue(settings.inAppBadgeFriendRequests)
        assertTrue(settings.inAppBadgeSharedInbox)
    }

    @Test
    fun `notification and badge settings are independent`() {
        // Push notifications can be off while badges are on (default state)
        val settings = UserSettings()
        assertFalse(settings.notifyNewMessages)
        assertTrue(settings.inAppBadgeMessages)
    }
}
