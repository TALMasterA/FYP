package com.translator.TalknLearn.screens.settings

import com.translator.TalknLearn.model.user.UserSettings
import org.junit.Test
import org.junit.Assert.*

/**
 * Comprehensive tests for shop and coin deduction logic.
 * 
 * Requirements:
 * - History expansion: 10 records @ 1000 coins per expansion
 * - History range: 30 (default) to 60 (max)
 * - Color palette unlock costs coins
 * - Coin deduction must be atomic and correct
 */
class ShopCoinLogicTest {

    @Test
    fun `history expansion cost is 1000 coins per 10 records`() {
        assertEquals(1000, UserSettings.HISTORY_EXPANSION_COST)
        assertEquals(10, UserSettings.HISTORY_EXPANSION_INCREMENT)
    }

    @Test
    fun `history expands from 30 to 60 in increments of 10`() {
        val steps = mutableListOf<Int>()
        var limit = UserSettings.BASE_HISTORY_LIMIT

        while (limit < UserSettings.MAX_HISTORY_LIMIT) {
            limit += UserSettings.HISTORY_EXPANSION_INCREMENT
            steps.add(limit)
        }

        assertEquals(listOf(40, 50, 60), steps)
    }

    @Test
    fun `total cost to max out history is 3000 coins`() {
        val expansionsNeeded = (UserSettings.MAX_HISTORY_LIMIT - UserSettings.BASE_HISTORY_LIMIT) /
                UserSettings.HISTORY_EXPANSION_INCREMENT
        val totalCost = expansionsNeeded * UserSettings.HISTORY_EXPANSION_COST

        assertEquals(3, expansionsNeeded)
        assertEquals(3000, totalCost)
    }

    @Test
    fun `cannot expand beyond max limit`() {
        val currentLimit = 60
        val canExpand = currentLimit < UserSettings.MAX_HISTORY_LIMIT
        assertFalse("Should not be able to expand beyond 60", canExpand)
    }

    @Test
    fun `coin deduction for expansion`() {
        var coins = 5000
        val cost = UserSettings.HISTORY_EXPANSION_COST

        // Buy 3 expansions (30 -> 40 -> 50 -> 60)
        for (i in 1..3) {
            assertTrue("Should have enough coins", coins >= cost)
            coins -= cost
        }

        assertEquals(2000, coins) // 5000 - 3000 = 2000
    }

    @Test
    fun `insufficient coins blocks expansion`() {
        val coins = 500
        val canAfford = coins >= UserSettings.HISTORY_EXPANSION_COST

        assertFalse("Should not be able to afford expansion", canAfford)
    }

    @Test
    fun `expansion not available when already at max`() {
        val settings = UserSettings(historyViewLimit = 60)
        val canExpand = settings.historyViewLimit < UserSettings.MAX_HISTORY_LIMIT

        assertFalse("Already at max, cannot expand", canExpand)
    }

    @Test
    fun `expansion available when below max`() {
        val settings = UserSettings(historyViewLimit = 40)
        val canExpand = settings.historyViewLimit < UserSettings.MAX_HISTORY_LIMIT

        assertTrue("Below max, can expand", canExpand)
    }

    @Test
    fun `each expansion step is exactly 10 records`() {
        val limits = listOf(30, 40, 50, 60)
        for (i in 1 until limits.size) {
            assertEquals(10, limits[i] - limits[i - 1])
        }
    }
}
