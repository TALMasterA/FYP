package com.example.fyp.model.ui

import com.example.fyp.model.user.UserSettings
import org.junit.Assert.*
import org.junit.Test

/**
 * Tests that hardcoded translations (zh-TW, Cantonese) are internally consistent
 * and that numeric values in UI text match actual app constants.
 *
 * These tests guard against:
 * - Mixed terminology within a single language (e.g., two different words for "coins")
 * - Hardcoded numbers that drift from the actual constants
 * - Missing template placeholders in hardcoded translations
 */
class TranslationConsistencyTest {

    // ── zh-TW Coin Terminology ──

    @Test
    fun `zh-TW uses consistent coin terminology - no mixed terms`() {
        val coinTerms = mutableMapOf<String, MutableList<UiTextKey>>()
        val knownCoinTerms = listOf("硬幣", "金幣")

        ZhTwUiTexts.forEach { (key, value) ->
            knownCoinTerms.forEach { term ->
                if (value.contains(term)) {
                    coinTerms.getOrPut(term) { mutableListOf() }.add(key)
                }
            }
        }

        // Should only use one term consistently
        val termsUsed = coinTerms.keys
        assertTrue(
            "zh-TW should use only ONE coin term but uses: $termsUsed. " +
                "硬幣 keys: ${coinTerms["硬幣"]?.map { it.name }}, " +
                "金幣 keys: ${coinTerms["金幣"]?.map { it.name }}",
            termsUsed.size <= 1
        )
    }

    @Test
    fun `Cantonese uses consistent coin terminology`() {
        val coinTerms = mutableMapOf<String, MutableList<UiTextKey>>()
        val knownCoinTerms = listOf("硬幣", "金幣")

        CantoneseUiTexts.forEach { (key, value) ->
            knownCoinTerms.forEach { term ->
                if (value.contains(term)) {
                    coinTerms.getOrPut(term) { mutableListOf() }.add(key)
                }
            }
        }

        val termsUsed = coinTerms.keys
        assertTrue(
            "Cantonese should use only ONE coin term but uses: $termsUsed",
            termsUsed.size <= 1
        )
    }

    // ── zh-TW Favorites Terminology ──

    @Test
    fun `zh-TW Favorites keys use consistent term`() {
        // All Favorites-related keys should use the same term
        val favoritesKeys = UiTextKey.entries.filter { it.name.startsWith("Favorites") }
        val terms = mutableSetOf<String>()

        favoritesKeys.forEach { key ->
            val value = ZhTwUiTexts[key] ?: return@forEach
            when {
                value.contains("最愛") -> terms.add("最愛")
                value.contains("收藏") -> terms.add("收藏")
            }
        }

        assertTrue(
            "zh-TW Favorites keys should use one consistent term but uses: $terms",
            terms.size <= 1
        )
    }

    // ── History Limit Numbers Match Constants ──

    @Test
    fun `HelpAppVersionNotes English text mentions correct history limit range`() {
        val text = BaseUiTexts.getOrNull(UiTextKey.HelpAppVersionNotes.ordinal).orEmpty()
        val base = UserSettings.BASE_HISTORY_LIMIT
        val max = UserSettings.MAX_HISTORY_LIMIT

        assertTrue(
            "English HelpAppVersionNotes should contain '$base-$max' but was: $text",
            text.contains("$base-$max")
        )
    }

    @Test
    fun `zh-TW HelpAppVersionNotes mentions correct history limit range`() {
        val text = ZhTwUiTexts[UiTextKey.HelpAppVersionNotes]!!
        val base = UserSettings.BASE_HISTORY_LIMIT
        val max = UserSettings.MAX_HISTORY_LIMIT

        assertTrue(
            "zh-TW HelpAppVersionNotes should contain '$base' and '$max' but was: $text",
            text.contains("$base") && text.contains("$max")
        )
    }

    @Test
    fun `Cantonese HelpAppVersionNotes mentions correct history limit range`() {
        val text = CantoneseUiTexts[UiTextKey.HelpAppVersionNotes]!!
        val base = UserSettings.BASE_HISTORY_LIMIT
        val max = UserSettings.MAX_HISTORY_LIMIT

        assertTrue(
            "Cantonese HelpAppVersionNotes should contain '$base' and '$max' but was: $text",
            text.contains("$base") && text.contains("$max")
        )
    }

    // ── Shop Buy Expansion Uses Placeholders ──

    @Test
    fun `English ShopBuyHistoryExpansion uses placeholder tokens`() {
        val text = BaseUiTexts.getOrNull(UiTextKey.ShopBuyHistoryExpansion.ordinal).orEmpty()
        assertTrue(
            "English ShopBuyHistoryExpansion should contain {increment}",
            text.contains("{increment}")
        )
        assertTrue(
            "English ShopBuyHistoryExpansion should contain {cost}",
            text.contains("{cost}")
        )
    }

    @Test
    fun `zh-TW ShopBuyHistoryExpansion uses placeholder tokens`() {
        val text = ZhTwUiTexts[UiTextKey.ShopBuyHistoryExpansion]!!
        assertTrue(
            "zh-TW ShopBuyHistoryExpansion should contain {increment} but was: $text",
            text.contains("{increment}")
        )
        assertTrue(
            "zh-TW ShopBuyHistoryExpansion should contain {cost} but was: $text",
            text.contains("{cost}")
        )
    }

    @Test
    fun `Cantonese ShopBuyHistoryExpansion uses placeholder tokens`() {
        val text = CantoneseUiTexts[UiTextKey.ShopBuyHistoryExpansion]!!
        assertTrue(
            "Cantonese ShopBuyHistoryExpansion should contain {increment} but was: $text",
            text.contains("{increment}")
        )
        assertTrue(
            "Cantonese ShopBuyHistoryExpansion should contain {cost} but was: $text",
            text.contains("{cost}")
        )
    }

    // ── zh-TW Learning Material Terminology ──

    @Test
    fun `zh-TW uses consistent learning material term`() {
        // Check for inconsistent 學習材料 vs 學習教材
        val keysWithMaterial = mutableListOf<UiTextKey>()
        val keysWithTeachingMaterial = mutableListOf<UiTextKey>()

        ZhTwUiTexts.forEach { (key, value) ->
            // Match 學習材料 but NOT 學習教材
            if (value.contains("學習材料") && !value.contains("學習教材")) {
                keysWithMaterial.add(key)
            }
            if (value.contains("學習教材")) {
                keysWithTeachingMaterial.add(key)
            }
        }

        assertTrue(
            "zh-TW should not mix 學習材料 and 學習教材. Keys with 學習材料: ${keysWithMaterial.map { it.name }}",
            keysWithMaterial.isEmpty() || keysWithTeachingMaterial.isEmpty()
        )
    }

    // ── Template tokens preserved in hardcoded translations ──

    @Test
    fun `zh-TW template strings contain all required placeholders`() {
        val templateChecks = mapOf(
            UiTextKey.ShopCurrentLimit to listOf("{limit}"),
            UiTextKey.ShopBuyHistoryExpansion to listOf("{increment}", "{cost}"),
            UiTextKey.ShopHistoryExpandedMessage to listOf("{limit}"),
            UiTextKey.SettingsColorCostTemplate to listOf("{cost}"),
            UiTextKey.HistoryInfoLimitMessage to listOf("{limit}"),
            UiTextKey.HistoryInfoFilterMessage to listOf("{limit}"),
            UiTextKey.QuizTitleTemplate to listOf("{language}"),
            UiTextKey.QuizQuestionTemplate to listOf("{current}", "{total}"),
            UiTextKey.QuizCoinsEarnedMessageTemplate to listOf("{Coins}"),
            UiTextKey.FriendsRequestsSection to listOf("{count}"),
            UiTextKey.FriendsSectionTitle to listOf("{count}"),
            UiTextKey.ChatTitle to listOf("{username}"),
            UiTextKey.ChatUnreadBadge to listOf("{count}"),
            UiTextKey.HomeWelcomeBack to listOf("{name}"),
            UiTextKey.ShopUnlockConfirmTitle to listOf("{name}"),
            UiTextKey.ShopUnlockCost to listOf("{cost}"),
            UiTextKey.ShopYourCoins to listOf("{coins}"),
            UiTextKey.PaginationPageLabelTemplate to listOf("{page}", "{total}"),
        )

        templateChecks.forEach { (key, tokens) ->
            val value = ZhTwUiTexts[key]
            assertNotNull("zh-TW missing key: ${key.name}", value)
            tokens.forEach { token ->
                assertTrue(
                    "zh-TW[${key.name}] must contain '$token' but was: $value",
                    value!!.contains(token)
                )
            }
        }
    }

    @Test
    fun `Cantonese template strings contain all required placeholders`() {
        val templateChecks = mapOf(
            UiTextKey.ShopCurrentLimit to listOf("{limit}"),
            UiTextKey.ShopBuyHistoryExpansion to listOf("{increment}", "{cost}"),
            UiTextKey.ShopHistoryExpandedMessage to listOf("{limit}"),
            UiTextKey.SettingsColorCostTemplate to listOf("{cost}"),
            UiTextKey.HistoryInfoLimitMessage to listOf("{limit}"),
            UiTextKey.HistoryInfoFilterMessage to listOf("{limit}"),
            UiTextKey.QuizTitleTemplate to listOf("{language}"),
            UiTextKey.QuizQuestionTemplate to listOf("{current}", "{total}"),
            UiTextKey.QuizCoinsEarnedMessageTemplate to listOf("{Coins}"),
            UiTextKey.FriendsRequestsSection to listOf("{count}"),
            UiTextKey.FriendsSectionTitle to listOf("{count}"),
            UiTextKey.ChatTitle to listOf("{username}"),
            UiTextKey.ChatUnreadBadge to listOf("{count}"),
            UiTextKey.HomeWelcomeBack to listOf("{name}"),
            UiTextKey.ShopUnlockConfirmTitle to listOf("{name}"),
            UiTextKey.ShopUnlockCost to listOf("{cost}"),
            UiTextKey.ShopYourCoins to listOf("{coins}"),
            UiTextKey.PaginationPageLabelTemplate to listOf("{page}", "{total}"),
        )

        templateChecks.forEach { (key, tokens) ->
            val value = CantoneseUiTexts[key]
            assertNotNull("Cantonese missing key: ${key.name}", value)
            tokens.forEach { token ->
                assertTrue(
                    "Cantonese[${key.name}] must contain '$token' but was: $value",
                    value!!.contains(token)
                )
            }
        }
    }

    // ── SecurityUtils defaults match UI text claims ──

    @Test
    fun `password minimum length in auth text matches 6`() {
        val zhTwText = ZhTwUiTexts[UiTextKey.AuthErrorPasswordTooShort]!!
        assertTrue(
            "zh-TW password error should mention '6' but was: $zhTwText",
            zhTwText.contains("6")
        )

        val enText = BaseUiTexts.getOrNull(UiTextKey.AuthRegisterRules.ordinal).orEmpty()
        assertTrue(
            "English register rules should mention '6' for password length",
            enText.contains("6")
        )
    }

    @Test
    fun `username length in profile hint matches 3-20`() {
        val zhTwText = ZhTwUiTexts[UiTextKey.ProfileUsernameHintFull]!!
        assertTrue(
            "zh-TW profile username hint should mention '3-20' but was: $zhTwText",
            zhTwText.contains("3-20") || (zhTwText.contains("3") && zhTwText.contains("20"))
        )
    }
}
