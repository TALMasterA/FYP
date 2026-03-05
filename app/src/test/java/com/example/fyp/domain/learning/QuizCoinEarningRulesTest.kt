package com.example.fyp.domain.learning

import org.junit.Test
import org.junit.Assert.*

/**
 * Comprehensive tests for quiz coin earning logic.
 * 
 * Requirements:
 * - Only first attempt for each quiz version earns coins
 * - 1 coin per correct answer
 * - Need 10+ more records than previously awarded quiz
 * - Score must be > 0
 * - Generated history count must equal current history count (anti-cheat)
 */
class QuizCoinEarningRulesTest {

    @Test
    fun `MIN_INCREMENT_FOR_COINS is 10`() {
        assertEquals(10, CoinEligibility.MIN_INCREMENT_FOR_COINS)
    }

    @Test
    fun `MIN_VALID_SCORE is 1`() {
        assertEquals(1, CoinEligibility.MIN_VALID_SCORE)
    }

    // ── First Attempt Rules ──

    @Test
    fun `first attempt for language pair is always eligible`() {
        assertTrue(
            CoinEligibility.isEligibleForCoins(
                attemptScore = 5,
                generatedHistoryCount = 50,
                currentHistoryCount = 50,
                lastAwardedCount = null
            )
        )
    }

    @Test
    fun `first attempt with score 1 is eligible`() {
        assertTrue(
            CoinEligibility.isEligibleForCoins(
                attemptScore = 1,
                generatedHistoryCount = 50,
                currentHistoryCount = 50,
                lastAwardedCount = null
            )
        )
    }

    // ── Score Validation ──

    @Test
    fun `score of 0 is not eligible`() {
        assertFalse(
            CoinEligibility.isEligibleForCoins(
                attemptScore = 0,
                generatedHistoryCount = 50,
                currentHistoryCount = 50,
                lastAwardedCount = null
            )
        )
    }

    @Test
    fun `negative score is not eligible`() {
        assertFalse(
            CoinEligibility.isEligibleForCoins(
                attemptScore = -1,
                generatedHistoryCount = 50,
                currentHistoryCount = 50,
                lastAwardedCount = null
            )
        )
    }

    // ── History Count Anti-Cheat ──

    @Test
    fun `mismatched history counts are not eligible`() {
        assertFalse(
            CoinEligibility.isEligibleForCoins(
                attemptScore = 5,
                generatedHistoryCount = 50,
                currentHistoryCount = 55, // Different from generated
                lastAwardedCount = null
            )
        )
    }

    @Test
    fun `null current history count is not eligible`() {
        assertFalse(
            CoinEligibility.isEligibleForCoins(
                attemptScore = 5,
                generatedHistoryCount = 50,
                currentHistoryCount = null,
                lastAwardedCount = null
            )
        )
    }

    @Test
    fun `zero generated history count is not eligible`() {
        assertFalse(
            CoinEligibility.isEligibleForCoins(
                attemptScore = 5,
                generatedHistoryCount = 0,
                currentHistoryCount = 0,
                lastAwardedCount = null
            )
        )
    }

    // ── 10+ Records Increment Rule ──

    @Test
    fun `need 10 plus more records than last awarded`() {
        // Last awarded at count 50, now at 60 (exactly 10 more)
        assertTrue(
            CoinEligibility.isEligibleForCoins(
                attemptScore = 5,
                generatedHistoryCount = 60,
                currentHistoryCount = 60,
                lastAwardedCount = 50
            )
        )
    }

    @Test
    fun `9 more records than last awarded is not enough`() {
        assertFalse(
            CoinEligibility.isEligibleForCoins(
                attemptScore = 5,
                generatedHistoryCount = 59,
                currentHistoryCount = 59,
                lastAwardedCount = 50
            )
        )
    }

    @Test
    fun `same count as last awarded is not eligible`() {
        assertFalse(
            CoinEligibility.isEligibleForCoins(
                attemptScore = 5,
                generatedHistoryCount = 50,
                currentHistoryCount = 50,
                lastAwardedCount = 50
            )
        )
    }

    @Test
    fun `20 more records than last awarded is eligible`() {
        assertTrue(
            CoinEligibility.isEligibleForCoins(
                attemptScore = 5,
                generatedHistoryCount = 70,
                currentHistoryCount = 70,
                lastAwardedCount = 50
            )
        )
    }

    // ── Progression Test ──

    @Test
    fun `coin earning progression over multiple quizzes`() {
        // First quiz at count 50 - eligible (no previous award)
        assertTrue(
            CoinEligibility.isEligibleForCoins(
                attemptScore = 8,
                generatedHistoryCount = 50,
                currentHistoryCount = 50,
                lastAwardedCount = null
            )
        )

        // Second quiz at count 60 - eligible (10 more than last award at 50)
        assertTrue(
            CoinEligibility.isEligibleForCoins(
                attemptScore = 7,
                generatedHistoryCount = 60,
                currentHistoryCount = 60,
                lastAwardedCount = 50
            )
        )

        // Third quiz at count 65 - NOT eligible (only 5 more than last award at 60)
        assertFalse(
            CoinEligibility.isEligibleForCoins(
                attemptScore = 9,
                generatedHistoryCount = 65,
                currentHistoryCount = 65,
                lastAwardedCount = 60
            )
        )

        // Fourth quiz at count 70 - eligible (10 more than last award at 60)
        assertTrue(
            CoinEligibility.isEligibleForCoins(
                attemptScore = 6,
                generatedHistoryCount = 70,
                currentHistoryCount = 70,
                lastAwardedCount = 60
            )
        )
    }

    // ── Anti-Cheat: Retake Same Quiz ──

    @Test
    fun `retaking same quiz version after adding history is blocked`() {
        // Quiz generated at count 50, user adds history to 55, tries to claim coins
        assertFalse(
            CoinEligibility.isEligibleForCoins(
                attemptScore = 10,
                generatedHistoryCount = 50,
                currentHistoryCount = 55, // Count changed since quiz was generated
                lastAwardedCount = null
            )
        )
    }
}
