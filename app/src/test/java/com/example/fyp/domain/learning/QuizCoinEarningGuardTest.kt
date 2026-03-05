package com.example.fyp.domain.learning

import org.junit.Assert.*
import org.junit.Test

/**
 * Comprehensive tests for quiz coin earning and learning material generation guard (item 8).
 *
 * Verifies:
 * - Coin eligibility anti-cheat logic
 * - Word bank language count requirements
 * - Learning sheet generation thresholds
 * - Quiz regeneration rules
 * - Generation eligibility constants are correct
 */
class QuizCoinEarningGuardTest {

    // ── Constants Guard ────────────────────────────────────────────

    @Test
    fun `coin eligibility minimum increment is 10`() {
        assertEquals(10, CoinEligibility.MIN_INCREMENT_FOR_COINS)
    }

    @Test
    fun `coin eligibility minimum valid score is 1`() {
        assertEquals(1, CoinEligibility.MIN_VALID_SCORE)
    }

    @Test
    fun `word bank needs 20 records for regeneration`() {
        assertEquals(20, GenerationEligibility.MIN_RECORDS_FOR_WORD_BANK)
    }

    @Test
    fun `learning sheet needs 5 records for regeneration`() {
        assertEquals(5, GenerationEligibility.MIN_RECORDS_FOR_LEARNING_SHEET)
    }

    @Test
    fun `regeneration minimum is 20 records`() {
        assertEquals(20, GenerationEligibility.MIN_RECORDS_FOR_REGEN)
    }

    // ── Quiz Coin Earning - Happy Path ─────────────────────────────

    @Test
    fun `first quiz always earns coins`() {
        assertTrue(CoinEligibility.isEligibleForCoins(
            attemptScore = 5,
            generatedHistoryCount = 30,
            currentHistoryCount = 30,
            lastAwardedCount = null
        ))
    }

    @Test
    fun `quiz with 10+ new records earns coins`() {
        assertTrue(CoinEligibility.isEligibleForCoins(
            attemptScore = 8,
            generatedHistoryCount = 40,
            currentHistoryCount = 40,
            lastAwardedCount = 30
        ))
    }

    @Test
    fun `quiz with exactly 10 new records earns coins`() {
        assertTrue(CoinEligibility.isEligibleForCoins(
            attemptScore = 8,
            generatedHistoryCount = 40,
            currentHistoryCount = 40,
            lastAwardedCount = 30
        ))
    }

    // ── Quiz Coin Earning - Anti-Cheat ─────────────────────────────

    @Test
    fun `quiz with 9 new records does NOT earn coins`() {
        assertFalse(CoinEligibility.isEligibleForCoins(
            attemptScore = 10,
            generatedHistoryCount = 39,
            currentHistoryCount = 39,
            lastAwardedCount = 30
        ))
    }

    @Test
    fun `zero score does NOT earn coins`() {
        assertFalse(CoinEligibility.isEligibleForCoins(
            attemptScore = 0,
            generatedHistoryCount = 50,
            currentHistoryCount = 50,
            lastAwardedCount = null
        ))
    }

    @Test
    fun `mismatched history counts does NOT earn coins`() {
        assertFalse(CoinEligibility.isEligibleForCoins(
            attemptScore = 10,
            generatedHistoryCount = 50,
            currentHistoryCount = 55,
            lastAwardedCount = null
        ))
    }

    @Test
    fun `null current history does NOT earn coins`() {
        assertFalse(CoinEligibility.isEligibleForCoins(
            attemptScore = 10,
            generatedHistoryCount = 50,
            currentHistoryCount = null,
            lastAwardedCount = null
        ))
    }

    @Test
    fun `zero generated history does NOT earn coins`() {
        assertFalse(CoinEligibility.isEligibleForCoins(
            attemptScore = 10,
            generatedHistoryCount = 0,
            currentHistoryCount = 0,
            lastAwardedCount = null
        ))
    }

    // ── Word Bank Generation Guard ─────────────────────────────────

    @Test
    fun `word bank can regenerate with 20+ new records`() {
        assertTrue(GenerationEligibility.canRegenerateWordBank(70, 50))
    }

    @Test
    fun `word bank cannot regenerate with 19 new records`() {
        assertFalse(GenerationEligibility.canRegenerateWordBank(69, 50))
    }

    @Test
    fun `word bank can regenerate with exactly 20 new records`() {
        assertTrue(GenerationEligibility.canRegenerateWordBank(70, 50))
    }

    @Test
    fun `word bank blocked when count decreased (anti-cheat)`() {
        assertFalse(GenerationEligibility.canRegenerateWordBank(40, 50))
    }

    @Test
    fun `word bank blocked when count same (no new records)`() {
        assertFalse(GenerationEligibility.canRegenerateWordBank(50, 50))
    }

    // ── Learning Sheet Generation Guard ────────────────────────────

    @Test
    fun `learning sheet can regenerate with 5+ new records`() {
        assertTrue(GenerationEligibility.canRegenerateLearningSheet(55, 50))
    }

    @Test
    fun `learning sheet cannot regenerate with 4 new records`() {
        assertFalse(GenerationEligibility.canRegenerateLearningSheet(54, 50))
    }

    @Test
    fun `learning sheet can regenerate with exactly 5 new records`() {
        assertTrue(GenerationEligibility.canRegenerateLearningSheet(55, 50))
    }

    @Test
    fun `learning sheet blocked when count decreased (anti-cheat)`() {
        assertFalse(GenerationEligibility.canRegenerateLearningSheet(45, 50))
    }

    @Test
    fun `learning sheet blocked when count same (no new records)`() {
        assertFalse(GenerationEligibility.canRegenerateLearningSheet(50, 50))
    }

    // ── Quiz Regeneration Guard ───────────────────────────────────

    @Test
    fun `first quiz generation is always allowed`() {
        assertTrue(GenerationEligibility.canRegenerateQuiz(50, null))
    }

    @Test
    fun `quiz regeneration allowed when sheet version changes`() {
        assertTrue(GenerationEligibility.canRegenerateQuiz(60, 50))
    }

    @Test
    fun `quiz regeneration blocked when sheet version unchanged`() {
        assertFalse(GenerationEligibility.canRegenerateQuiz(50, 50))
    }

    // ── Full Learning Flow Simulation ─────────────────────────────

    @Test
    fun `complete learning flow - generate sheet then quiz then earn coins`() {
        val initialHistoryCount = 50

        // Step 1: Generate learning sheet (needs 5+ new records since last gen)
        val sheetCanGenerate = GenerationEligibility.canRegenerateLearningSheet(55, initialHistoryCount)
        assertTrue("Sheet should be generable with 5 new records", sheetCanGenerate)

        // Step 2: Generate quiz (first quiz always allowed)
        val quizCanGenerate = GenerationEligibility.canRegenerateQuiz(55, null)
        assertTrue("First quiz should always be allowed", quizCanGenerate)

        // Step 3: Earn coins (first quiz always eligible)
        val isEligible = CoinEligibility.isEligibleForCoins(8, 55, 55, null)
        assertTrue("First quiz should earn coins", isEligible)
    }

    @Test
    fun `repeated quiz at same version does not earn coins`() {
        // First attempt earns coins
        val firstEligible = CoinEligibility.isEligibleForCoins(8, 50, 50, null)
        assertTrue(firstEligible)

        // Second attempt at same count does NOT earn (lastAwardedCount = 50)
        val secondEligible = CoinEligibility.isEligibleForCoins(10, 50, 50, 50)
        assertFalse("Same version should not earn coins again", secondEligible)
    }

    @Test
    fun `quiz regeneration requires new learning sheet first`() {
        // Quiz was generated at count 50, sheet also at count 50
        val quizCanRegen = GenerationEligibility.canRegenerateQuiz(50, 50)
        assertFalse("Quiz cannot regen when sheet version unchanged", quizCanRegen)

        // After regenerating sheet at count 55
        val quizCanRegenAfterSheet = GenerationEligibility.canRegenerateQuiz(55, 50)
        assertTrue("Quiz can regen after sheet version changes", quizCanRegenAfterSheet)
    }
}
