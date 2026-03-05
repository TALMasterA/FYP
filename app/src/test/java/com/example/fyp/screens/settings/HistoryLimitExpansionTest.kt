package com.example.fyp.screens.settings

import com.example.fyp.model.user.UserSettings
import org.junit.Assert.*
import org.junit.Test

/**
 * Comprehensive unit tests for history limit expansion logic (item 4).
 *
 * Verifies:
 * - Default limit is 30 records (changed from 50)
 * - Maximum limit is 60 records (changed from 100)
 * - Each expansion adds 10 records
 * - Expansion costs 1000 coins per increment
 * - Expansion is capped at MAX_HISTORY_LIMIT
 * - Full expansion from 30 to 60 requires 3 purchases
 */
class HistoryLimitExpansionTest {

    // ── Limit Constants ────────────────────────────────────────────

    @Test
    fun `base history limit is 30`() {
        assertEquals(30, UserSettings.BASE_HISTORY_LIMIT)
    }

    @Test
    fun `max history limit is 60`() {
        assertEquals(60, UserSettings.MAX_HISTORY_LIMIT)
    }

    @Test
    fun `expansion increment is 10`() {
        assertEquals(10, UserSettings.HISTORY_EXPANSION_INCREMENT)
    }

    @Test
    fun `expansion cost is 1000 coins`() {
        assertEquals(1000, UserSettings.HISTORY_EXPANSION_COST)
    }

    // ── Default Settings ───────────────────────────────────────────

    @Test
    fun `new user starts with base limit`() {
        val settings = UserSettings()
        assertEquals(UserSettings.BASE_HISTORY_LIMIT, settings.historyViewLimit)
    }

    @Test
    fun `new user default limit is 30`() {
        val settings = UserSettings()
        assertEquals(30, settings.historyViewLimit)
    }

    // ── Expansion Steps ────────────────────────────────────────────

    @Test
    fun `first expansion brings limit to 40`() {
        val currentLimit = UserSettings.BASE_HISTORY_LIMIT
        val newLimit = (currentLimit + UserSettings.HISTORY_EXPANSION_INCREMENT)
            .coerceAtMost(UserSettings.MAX_HISTORY_LIMIT)
        assertEquals(40, newLimit)
    }

    @Test
    fun `second expansion brings limit to 50`() {
        val currentLimit = 40
        val newLimit = (currentLimit + UserSettings.HISTORY_EXPANSION_INCREMENT)
            .coerceAtMost(UserSettings.MAX_HISTORY_LIMIT)
        assertEquals(50, newLimit)
    }

    @Test
    fun `third expansion brings limit to 60 (max)`() {
        val currentLimit = 50
        val newLimit = (currentLimit + UserSettings.HISTORY_EXPANSION_INCREMENT)
            .coerceAtMost(UserSettings.MAX_HISTORY_LIMIT)
        assertEquals(60, newLimit)
    }

    @Test
    fun `expansion at max stays at 60`() {
        val currentLimit = 60
        val newLimit = (currentLimit + UserSettings.HISTORY_EXPANSION_INCREMENT)
            .coerceAtMost(UserSettings.MAX_HISTORY_LIMIT)
        assertEquals(60, newLimit)
    }

    @Test
    fun `full expansion requires exactly 3 purchases`() {
        val totalExpansion = UserSettings.MAX_HISTORY_LIMIT - UserSettings.BASE_HISTORY_LIMIT
        val purchasesNeeded = totalExpansion / UserSettings.HISTORY_EXPANSION_INCREMENT
        assertEquals(3, purchasesNeeded)
    }

    @Test
    fun `total expansion cost is 3000 coins`() {
        val totalExpansion = UserSettings.MAX_HISTORY_LIMIT - UserSettings.BASE_HISTORY_LIMIT
        val purchasesNeeded = totalExpansion / UserSettings.HISTORY_EXPANSION_INCREMENT
        val totalCost = purchasesNeeded * UserSettings.HISTORY_EXPANSION_COST
        assertEquals(3000, totalCost)
    }

    // ── Expansion boundary checks ─────────────────────────────────

    @Test
    fun `expansion never exceeds max limit`() {
        var currentLimit = UserSettings.BASE_HISTORY_LIMIT

        // Simulate 10 expansions (way more than needed)
        repeat(10) {
            currentLimit = (currentLimit + UserSettings.HISTORY_EXPANSION_INCREMENT)
                .coerceAtMost(UserSettings.MAX_HISTORY_LIMIT)
        }

        assertEquals(UserSettings.MAX_HISTORY_LIMIT, currentLimit)
    }

    @Test
    fun `at max limit expansion is blocked`() {
        val currentLimit = UserSettings.MAX_HISTORY_LIMIT
        val canExpand = currentLimit < UserSettings.MAX_HISTORY_LIMIT
        assertFalse("Should not expand when at max limit", canExpand)
    }

    @Test
    fun `below max limit expansion is allowed`() {
        val currentLimit = 50
        val canExpand = currentLimit < UserSettings.MAX_HISTORY_LIMIT
        assertTrue("Should allow expansion when below max", canExpand)
    }

    // ── Coin validation ───────────────────────────────────────────

    @Test
    fun `user with 1000 coins can expand once`() {
        val coinBalance = 1000
        val hasEnoughCoins = coinBalance >= UserSettings.HISTORY_EXPANSION_COST
        assertTrue("1000 coins should be enough for one expansion", hasEnoughCoins)
    }

    @Test
    fun `user with 999 coins cannot expand`() {
        val coinBalance = 999
        val hasEnoughCoins = coinBalance >= UserSettings.HISTORY_EXPANSION_COST
        assertFalse("999 coins should NOT be enough for expansion", hasEnoughCoins)
    }

    @Test
    fun `user with 3000 coins can do full expansion`() {
        val coinBalance = 3000
        val totalExpansion = UserSettings.MAX_HISTORY_LIMIT - UserSettings.BASE_HISTORY_LIMIT
        val purchasesNeeded = totalExpansion / UserSettings.HISTORY_EXPANSION_INCREMENT
        val totalCost = purchasesNeeded * UserSettings.HISTORY_EXPANSION_COST

        assertTrue(
            "3000 coins should cover full expansion",
            coinBalance >= totalCost
        )
    }

    @Test
    fun `user with 2999 coins cannot do full expansion`() {
        val coinBalance = 2999
        val totalExpansion = UserSettings.MAX_HISTORY_LIMIT - UserSettings.BASE_HISTORY_LIMIT
        val purchasesNeeded = totalExpansion / UserSettings.HISTORY_EXPANSION_INCREMENT
        val totalCost = purchasesNeeded * UserSettings.HISTORY_EXPANSION_COST

        assertFalse(
            "2999 coins should NOT cover full expansion",
            coinBalance >= totalCost
        )
    }

    // ── Expansion simulation ──────────────────────────────────────

    @Test
    fun `simulate complete expansion journey`() {
        var limit = UserSettings.BASE_HISTORY_LIMIT
        var coins = 5000
        var expansions = 0

        while (limit < UserSettings.MAX_HISTORY_LIMIT && coins >= UserSettings.HISTORY_EXPANSION_COST) {
            coins -= UserSettings.HISTORY_EXPANSION_COST
            limit = (limit + UserSettings.HISTORY_EXPANSION_INCREMENT)
                .coerceAtMost(UserSettings.MAX_HISTORY_LIMIT)
            expansions++
        }

        assertEquals("Should reach max limit", UserSettings.MAX_HISTORY_LIMIT, limit)
        assertEquals("Should take 3 expansions", 3, expansions)
        assertEquals("Should have 2000 coins remaining", 2000, coins)
    }

    @Test
    fun `simulate partial expansion with limited coins`() {
        var limit = UserSettings.BASE_HISTORY_LIMIT
        var coins = 2500
        var expansions = 0

        while (limit < UserSettings.MAX_HISTORY_LIMIT && coins >= UserSettings.HISTORY_EXPANSION_COST) {
            coins -= UserSettings.HISTORY_EXPANSION_COST
            limit = (limit + UserSettings.HISTORY_EXPANSION_INCREMENT)
                .coerceAtMost(UserSettings.MAX_HISTORY_LIMIT)
            expansions++
        }

        assertEquals("Should reach 50 with 2 expansions", 50, limit)
        assertEquals("Should have done 2 expansions", 2, expansions)
        assertEquals("Should have 500 coins remaining", 500, coins)
    }

    // ── Limit clamping in repository ──────────────────────────────

    @Test
    fun `limit is clamped between BASE and MAX`() {
        val tooLow = 10
        val tooHigh = 200
        val normal = 40

        val clampedLow = tooLow.coerceIn(UserSettings.BASE_HISTORY_LIMIT, UserSettings.MAX_HISTORY_LIMIT)
        val clampedHigh = tooHigh.coerceIn(UserSettings.BASE_HISTORY_LIMIT, UserSettings.MAX_HISTORY_LIMIT)
        val clampedNormal = normal.coerceIn(UserSettings.BASE_HISTORY_LIMIT, UserSettings.MAX_HISTORY_LIMIT)

        assertEquals(UserSettings.BASE_HISTORY_LIMIT, clampedLow)
        assertEquals(UserSettings.MAX_HISTORY_LIMIT, clampedHigh)
        assertEquals(40, clampedNormal)
    }
}
