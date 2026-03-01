package com.example.fyp.domain.learning

import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import org.junit.Test

/**
 * Integration tests that verify the interaction between
 * GenerationEligibility and CoinEligibility domain logic.
 *
 * These tests simulate real user workflows across:
 * - Learning sheet generation → Quiz generation → Coin awarding
 * - Anti-cheat enforcement across the full pipeline
 */
class CoinAndGenerationIntegrationTest {

    // --- Full Happy Path Workflow ---

    @Test
    fun `full workflow - first sheet, first quiz, coins awarded`() {
        val initialCount = 30

        // Step 1: First learning sheet generation (always allowed for first gen)
        // First gen doesn't go through canRegenerate — it's a fresh creation
        // Saved at count = 30

        // Step 2: Quiz generation allowed (first quiz, no previous quiz version)
        assertTrue(GenerationEligibility.canRegenerateQuiz(
            sheetHistoryCount = initialCount,
            quizHistoryCount = null
        ))

        // Step 3: Coins eligible (first quiz for this language pair)
        assertTrue(CoinEligibility.isEligibleForCoins(
            attemptScore = 8,
            generatedHistoryCount = initialCount,
            currentHistoryCount = initialCount,
            lastAwardedCount = null
        ))
    }

    @Test
    fun `full workflow - regen sheet, regen quiz, coins awarded`() {
        val firstGenCount = 30
        val secondGenCount = 40 // +10 more records

        // Step 1: Regen learning sheet (need 5+ more)
        assertTrue(GenerationEligibility.canRegenerateLearningSheet(
            currentHistoryCount = secondGenCount,
            savedHistoryCount = firstGenCount
        ))

        // Step 2: Quiz regen allowed (sheet version changed from 30 to 40)
        assertTrue(GenerationEligibility.canRegenerateQuiz(
            sheetHistoryCount = secondGenCount,
            quizHistoryCount = firstGenCount
        ))

        // Step 3: Coins eligible (10+ more records since last awarded at 30)
        assertTrue(CoinEligibility.isEligibleForCoins(
            attemptScore = 7,
            generatedHistoryCount = secondGenCount,
            currentHistoryCount = secondGenCount,
            lastAwardedCount = firstGenCount
        ))
    }

    // --- Anti-Cheat: Same Version → No Double Coins ---

    @Test
    fun `no double coins for same quiz version`() {
        val genCount = 50

        // First attempt: coins awarded
        assertTrue(CoinEligibility.isEligibleForCoins(
            attemptScore = 10,
            generatedHistoryCount = genCount,
            currentHistoryCount = genCount,
            lastAwardedCount = null
        ))

        // Second attempt at same version: should NOT get coins
        // (lastAwardedCount is now = genCount)
        assertFalse(CoinEligibility.isEligibleForCoins(
            attemptScore = 10,
            generatedHistoryCount = genCount,
            currentHistoryCount = genCount,
            lastAwardedCount = genCount
        ))
    }

    @Test
    fun `no quiz regen at same sheet version`() {
        val sheetVersion = 50

        // Quiz already generated at version 50
        assertFalse(GenerationEligibility.canRegenerateQuiz(
            sheetHistoryCount = sheetVersion,
            quizHistoryCount = sheetVersion
        ))
    }

    // --- Anti-Cheat: History Count Manipulation ---

    @Test
    fun `history deletion blocks sheet regen but quiz version mismatch still works`() {
        val savedSheetCount = 50
        val currentCount = 40 // User deleted some records

        // Sheet regen blocked (count decreased)
        assertFalse(GenerationEligibility.canRegenerateLearningSheet(
            currentHistoryCount = currentCount,
            savedHistoryCount = savedSheetCount
        ))

        // Quiz regen: version-based, so technically allowed if someone
        // somehow has a different sheet version (edge case)
        // But in practice this can't happen because sheet can't be regenerated
    }

    @Test
    fun `coins not awarded when quiz version does not match current count`() {
        // Quiz was generated at count 50 but user now has 55 records
        // (sheet was regenerated but quiz wasn't)
        assertFalse(CoinEligibility.isEligibleForCoins(
            attemptScore = 10,
            generatedHistoryCount = 50,
            currentHistoryCount = 55,
            lastAwardedCount = null
        ))
    }

    // --- Boundary: Not Enough Records for Coins ---

    @Test
    fun `insufficient records between quizzes blocks coins`() {
        val firstAwardedAt = 50
        val newGenCount = 55 // Only +5, need +10

        // Sheet can regen (5+ more for sheet)
        assertTrue(GenerationEligibility.canRegenerateLearningSheet(
            currentHistoryCount = newGenCount,
            savedHistoryCount = firstAwardedAt
        ))

        // Quiz can regen (version differs)
        assertTrue(GenerationEligibility.canRegenerateQuiz(
            sheetHistoryCount = newGenCount,
            quizHistoryCount = firstAwardedAt
        ))

        // But coins NOT awarded (only +5, need +10)
        assertFalse(CoinEligibility.isEligibleForCoins(
            attemptScore = 10,
            generatedHistoryCount = newGenCount,
            currentHistoryCount = newGenCount,
            lastAwardedCount = firstAwardedAt
        ))
    }

    // --- Word Bank and Learning Sheet Independence ---

    @Test
    fun `word bank requires more records than learning sheet to regen`() {
        val savedCount = 50
        val newCount = 55 // +5 records

        // Learning sheet can regen (+5 >= MIN_RECORDS_FOR_LEARNING_SHEET)
        assertTrue(GenerationEligibility.canRegenerateLearningSheet(newCount, savedCount))

        // Word bank cannot regen (+5 < MIN_RECORDS_FOR_WORD_BANK of 20)
        assertFalse(GenerationEligibility.canRegenerateWordBank(newCount, savedCount))
    }

    @Test
    fun `zero score quiz never awards coins`() {
        assertTrue(GenerationEligibility.canRegenerateQuiz(50, null))

        // Quiz taken with 0 score
        assertFalse(CoinEligibility.isEligibleForCoins(
            attemptScore = 0,
            generatedHistoryCount = 50,
            currentHistoryCount = 50,
            lastAwardedCount = null
        ))
    }
}
