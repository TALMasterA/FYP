package com.translator.TalknLearn.domain.learning

import org.junit.Assert.*
import org.junit.Test

/**
 * Extended tests for GenerationEligibility and CoinEligibility interaction.
 *
 * Covers edge cases and boundary conditions not in the primary tests:
 *  - Zero count edge cases
 *  - Large count values
 *  - Negative delta protection
 *  - Quiz + sheet version alignment
 */
class EligibilityEdgeCasesTest {

    // ── GenerationEligibility boundary values ──────────────────────

    @Test
    fun `canRegenerateWordBank - exactly at threshold returns true`() {
        assertTrue(
            GenerationEligibility.canRegenerateWordBank(
                currentHistoryCount = 40,
                savedHistoryCount = 20
            )
        )
    }

    @Test
    fun `canRegenerateWordBank - one below threshold returns false`() {
        assertFalse(
            GenerationEligibility.canRegenerateWordBank(
                currentHistoryCount = 39,
                savedHistoryCount = 20
            )
        )
    }

    @Test
    fun `canRegenerateWordBank - zero saved, zero current returns false`() {
        assertFalse(
            GenerationEligibility.canRegenerateWordBank(
                currentHistoryCount = 0,
                savedHistoryCount = 0
            )
        )
    }

    @Test
    fun `canRegenerateWordBank - large values work correctly`() {
        assertTrue(
            GenerationEligibility.canRegenerateWordBank(
                currentHistoryCount = 1000,
                savedHistoryCount = 950
            )
        )
    }

    @Test
    fun `canRegenerateLearningSheet - exactly at threshold returns true`() {
        assertTrue(
            GenerationEligibility.canRegenerateLearningSheet(
                currentHistoryCount = 25,
                savedHistoryCount = 20
            )
        )
    }

    @Test
    fun `canRegenerateLearningSheet - one below threshold returns false`() {
        assertFalse(
            GenerationEligibility.canRegenerateLearningSheet(
                currentHistoryCount = 24,
                savedHistoryCount = 20
            )
        )
    }

    @Test
    fun `canRegenerateLearningSheet - anti-cheat blocks decreased count`() {
        assertFalse(
            GenerationEligibility.canRegenerateLearningSheet(
                currentHistoryCount = 10,
                savedHistoryCount = 20
            )
        )
    }

    @Test
    fun `canRegenerateQuiz - null quizHistoryCount always allows`() {
        assertTrue(
            GenerationEligibility.canRegenerateQuiz(
                sheetHistoryCount = 25,
                quizHistoryCount = null
            )
        )
    }

    @Test
    fun `canRegenerateQuiz - same version blocks regeneration`() {
        assertFalse(
            GenerationEligibility.canRegenerateQuiz(
                sheetHistoryCount = 25,
                quizHistoryCount = 25
            )
        )
    }

    @Test
    fun `canRegenerateQuiz - different version allows regeneration`() {
        assertTrue(
            GenerationEligibility.canRegenerateQuiz(
                sheetHistoryCount = 30,
                quizHistoryCount = 25
            )
        )
    }

    // ── CoinEligibility boundary values ────────────────────────────

    @Test
    fun `isEligibleForCoins - zero generatedHistoryCount is ineligible`() {
        assertFalse(
            CoinEligibility.isEligibleForCoins(
                attemptScore = 5,
                generatedHistoryCount = 0,
                currentSheetHistoryCount = 0,
                lastAwardedCount = null
            )
        )
    }

    @Test
    fun `isEligibleForCoins - zero score is ineligible`() {
        assertFalse(
            CoinEligibility.isEligibleForCoins(
                attemptScore = 0,
                generatedHistoryCount = 20,
                currentSheetHistoryCount = 20,
                lastAwardedCount = null
            )
        )
    }

    @Test
    fun `isEligibleForCoins - null sheetHistoryCount is ineligible`() {
        assertFalse(
            CoinEligibility.isEligibleForCoins(
                attemptScore = 5,
                generatedHistoryCount = 20,
                currentSheetHistoryCount = null,
                lastAwardedCount = null
            )
        )
    }

    @Test
    fun `isEligibleForCoins - version mismatch is ineligible`() {
        assertFalse(
            CoinEligibility.isEligibleForCoins(
                attemptScore = 5,
                generatedHistoryCount = 20,
                currentSheetHistoryCount = 25, // Sheet was regenerated
                lastAwardedCount = null
            )
        )
    }

    @Test
    fun `isEligibleForCoins - first attempt with matching version is eligible`() {
        assertTrue(
            CoinEligibility.isEligibleForCoins(
                attemptScore = 3,
                generatedHistoryCount = 20,
                currentSheetHistoryCount = 20,
                lastAwardedCount = null // First quiz ever
            )
        )
    }

    @Test
    fun `isEligibleForCoins - exactly at increment threshold is eligible`() {
        assertTrue(
            CoinEligibility.isEligibleForCoins(
                attemptScore = 3,
                generatedHistoryCount = 30,
                currentSheetHistoryCount = 30,
                lastAwardedCount = 20 // 30 - 20 = 10 >= MIN_INCREMENT_FOR_COINS
            )
        )
    }

    @Test
    fun `isEligibleForCoins - one below increment threshold is ineligible`() {
        assertFalse(
            CoinEligibility.isEligibleForCoins(
                attemptScore = 3,
                generatedHistoryCount = 29,
                currentSheetHistoryCount = 29,
                lastAwardedCount = 20 // 29 - 20 = 9 < MIN_INCREMENT_FOR_COINS
            )
        )
    }

    // ── Constants verification ─────────────────────────────────────

    @Test
    fun `GenerationEligibility MIN_RECORDS_FOR_WORD_BANK is 20`() {
        assertEquals(20, GenerationEligibility.MIN_RECORDS_FOR_WORD_BANK)
    }

    @Test
    fun `GenerationEligibility MIN_RECORDS_FOR_LEARNING_SHEET is 5`() {
        assertEquals(5, GenerationEligibility.MIN_RECORDS_FOR_LEARNING_SHEET)
    }

    @Test
    fun `CoinEligibility MIN_INCREMENT_FOR_COINS is 10`() {
        assertEquals(10, CoinEligibility.MIN_INCREMENT_FOR_COINS)
    }

    @Test
    fun `CoinEligibility MIN_VALID_SCORE is 1`() {
        assertEquals(1, CoinEligibility.MIN_VALID_SCORE)
    }
}

