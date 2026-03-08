package com.example.fyp.domain.learning

import org.junit.Test
import org.junit.Assert.*

/**
 * Comprehensive tests for learning sheet and quiz generation eligibility.
 * 
 * Requirements:
 * - Learning sheet first gen: no restriction
 * - Learning sheet regen: need at least 5 more records
 * - Quiz first gen: no restriction (one sheet version = one quiz version)
 * - Quiz regen: only when learning sheet version changes
 * - Word bank first gen: no restriction
 * - Word bank regen: need +20 records, word bank is refreshed (add up), not overwrite
 * - All generation count must increase from previous generation
 */
class LearningGenerationRulesTest {

    // ── Learning Sheet Rules ──

    @Test
    fun `learning sheet first generation has no restriction`() {
        // First gen: savedHistoryCount = 0 (no previous generation)
        // With enough records (>= MIN_RECORDS_FOR_LEARNING_SHEET), first gen is allowed
        val canGenerateWithRecords = GenerationEligibility.canRegenerateLearningSheet(
            currentHistoryCount = 5,
            savedHistoryCount = 0
        )
        assertTrue("First gen with 5 records should be allowed", canGenerateWithRecords)
    }

    @Test
    fun `learning sheet regen needs at least 5 more records`() {
        // Exactly 5 more records
        assertTrue(
            GenerationEligibility.canRegenerateLearningSheet(
                currentHistoryCount = 55,
                savedHistoryCount = 50
            )
        )
    }

    @Test
    fun `learning sheet regen with only 4 more records is blocked`() {
        assertFalse(
            GenerationEligibility.canRegenerateLearningSheet(
                currentHistoryCount = 54,
                savedHistoryCount = 50
            )
        )
    }

    @Test
    fun `learning sheet regen with same count is blocked`() {
        assertFalse(
            GenerationEligibility.canRegenerateLearningSheet(
                currentHistoryCount = 50,
                savedHistoryCount = 50
            )
        )
    }

    @Test
    fun `learning sheet regen blocked if count decreased (anti-cheat)`() {
        assertFalse(
            GenerationEligibility.canRegenerateLearningSheet(
                currentHistoryCount = 45,
                savedHistoryCount = 50
            )
        )
    }

    @Test
    fun `MIN_RECORDS_FOR_LEARNING_SHEET is 5`() {
        assertEquals(5, GenerationEligibility.MIN_RECORDS_FOR_LEARNING_SHEET)
    }

    // ── Quiz Generation Rules ──

    @Test
    fun `quiz first generation is always allowed`() {
        assertTrue(
            GenerationEligibility.canRegenerateQuiz(
                sheetHistoryCount = 50,
                quizHistoryCount = null
            )
        )
    }

    @Test
    fun `quiz regen allowed when sheet version changes`() {
        assertTrue(
            GenerationEligibility.canRegenerateQuiz(
                sheetHistoryCount = 60,
                quizHistoryCount = 50
            )
        )
    }

    @Test
    fun `quiz regen blocked when sheet version is same`() {
        // One sheet version = one quiz version
        assertFalse(
            GenerationEligibility.canRegenerateQuiz(
                sheetHistoryCount = 50,
                quizHistoryCount = 50
            )
        )
    }

    // ── Word Bank Rules ──

    @Test
    fun `word bank first generation with enough records`() {
        assertTrue(
            GenerationEligibility.canRegenerateWordBank(
                currentHistoryCount = 20,
                savedHistoryCount = 0
            )
        )
    }

    @Test
    fun `word bank regen needs at least 20 more records`() {
        assertTrue(
            GenerationEligibility.canRegenerateWordBank(
                currentHistoryCount = 70,
                savedHistoryCount = 50
            )
        )
    }

    @Test
    fun `word bank regen with only 19 more records is blocked`() {
        assertFalse(
            GenerationEligibility.canRegenerateWordBank(
                currentHistoryCount = 69,
                savedHistoryCount = 50
            )
        )
    }

    @Test
    fun `word bank regen blocked if count decreased (anti-cheat)`() {
        assertFalse(
            GenerationEligibility.canRegenerateWordBank(
                currentHistoryCount = 30,
                savedHistoryCount = 50
            )
        )
    }

    @Test
    fun `MIN_RECORDS_FOR_WORD_BANK is 20`() {
        assertEquals(20, GenerationEligibility.MIN_RECORDS_FOR_WORD_BANK)
    }

    // ── Count Must Increase Rule ──

    @Test
    fun `generation count must increase - learning sheet`() {
        // Previous gen at count 50, need at least 55 to regen
        assertFalse(
            GenerationEligibility.canRegenerateLearningSheet(
                currentHistoryCount = 50,
                savedHistoryCount = 50
            )
        )
        assertTrue(
            GenerationEligibility.canRegenerateLearningSheet(
                currentHistoryCount = 55,
                savedHistoryCount = 50
            )
        )
    }

    @Test
    fun `generation count must increase - word bank`() {
        // Previous gen at count 50, need at least 70 to regen
        assertFalse(
            GenerationEligibility.canRegenerateWordBank(
                currentHistoryCount = 50,
                savedHistoryCount = 50
            )
        )
        assertTrue(
            GenerationEligibility.canRegenerateWordBank(
                currentHistoryCount = 70,
                savedHistoryCount = 50
            )
        )
    }

    // ── Full Learning Flow Test ──

    @Test
    fun `complete learning flow - sheet then quiz then coins`() {
        // 1. First learning sheet generation (count=50)
        val canGenerateSheet = GenerationEligibility.canRegenerateLearningSheet(
            currentHistoryCount = 50,
            savedHistoryCount = 0
        )
        assertTrue("First sheet gen should be allowed", canGenerateSheet)

        // 2. First quiz generation for this sheet version
        val canGenerateQuiz = GenerationEligibility.canRegenerateQuiz(
            sheetHistoryCount = 50,
            quizHistoryCount = null
        )
        assertTrue("First quiz gen should be allowed", canGenerateQuiz)

        // 3. Quiz coins eligibility (first attempt)
        val coinEligible = CoinEligibility.isEligibleForCoins(
            attemptScore = 5,
            generatedHistoryCount = 50,
            currentSheetHistoryCount = 50,
            lastAwardedCount = null
        )
        assertTrue("First quiz attempt should earn coins", coinEligible)

        // 4. Cannot regen quiz without new sheet version
        val canRegenQuiz = GenerationEligibility.canRegenerateQuiz(
            sheetHistoryCount = 50,
            quizHistoryCount = 50
        )
        assertFalse("Cannot regen quiz without new sheet", canRegenQuiz)

        // 5. After 5 more records, can regen sheet
        val canRegenSheet = GenerationEligibility.canRegenerateLearningSheet(
            currentHistoryCount = 55,
            savedHistoryCount = 50
        )
        assertTrue("Can regen sheet after 5 more records", canRegenSheet)

        // 6. Now can generate new quiz
        val canGenNewQuiz = GenerationEligibility.canRegenerateQuiz(
            sheetHistoryCount = 55,
            quizHistoryCount = 50
        )
        assertTrue("Can gen new quiz after sheet regen", canGenNewQuiz)
    }
}
