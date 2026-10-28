package com.translator.TalknLearn.domain.learning

import org.junit.Assert.*
import org.junit.Test

/**
 * Unit tests for GenerationEligibility pure domain logic.
 *
 * Tests word bank, learning sheet, and quiz regeneration eligibility rules:
 * - Minimum record thresholds
 * - Anti-cheat: count decrease blocking
 * - Edge cases (equal counts, null quiz count)
 */
class GenerationEligibilityTest {

    // ── Constants ────────────────────────────────────────────────────

    @Test
    fun `MIN_RECORDS_FOR_WORD_BANK is 20`() {
        assertEquals(20, GenerationEligibility.MIN_RECORDS_FOR_WORD_BANK)
    }

    @Test
    fun `MIN_RECORDS_FOR_LEARNING_SHEET is 5`() {
        assertEquals(5, GenerationEligibility.MIN_RECORDS_FOR_LEARNING_SHEET)
    }

    @Test
    fun `MIN_RECORDS_FOR_REGEN is 20`() {
        assertEquals(20, GenerationEligibility.MIN_RECORDS_FOR_REGEN)
    }

    // ── canRegenerateWordBank ────────────────────────────────────────

    @Test
    fun `word bank - allows when exactly threshold records added`() {
        assertTrue(GenerationEligibility.canRegenerateWordBank(40, 20))
    }

    @Test
    fun `word bank - allows when more than threshold records added`() {
        assertTrue(GenerationEligibility.canRegenerateWordBank(50, 20))
    }

    @Test
    fun `word bank - blocks when fewer than threshold records added`() {
        assertFalse(GenerationEligibility.canRegenerateWordBank(30, 20))
    }

    @Test
    fun `word bank - blocks when count unchanged`() {
        assertFalse(GenerationEligibility.canRegenerateWordBank(20, 20))
    }

    @Test
    fun `word bank - blocks when count decreased (anti-cheat)`() {
        assertFalse(GenerationEligibility.canRegenerateWordBank(10, 20))
    }

    @Test
    fun `word bank - allows from zero saved count with threshold met`() {
        assertTrue(GenerationEligibility.canRegenerateWordBank(20, 0))
    }

    @Test
    fun `word bank - blocks from zero saved count below threshold`() {
        assertFalse(GenerationEligibility.canRegenerateWordBank(19, 0))
    }

    @Test
    fun `word bank - handles large counts correctly`() {
        assertTrue(GenerationEligibility.canRegenerateWordBank(1020, 1000))
    }

    // ── canRegenerateLearningSheet ───────────────────────────────────

    @Test
    fun `learning sheet - allows when exactly threshold records added`() {
        assertTrue(GenerationEligibility.canRegenerateLearningSheet(15, 10))
    }

    @Test
    fun `learning sheet - allows when more than threshold records added`() {
        assertTrue(GenerationEligibility.canRegenerateLearningSheet(20, 10))
    }

    @Test
    fun `learning sheet - blocks when fewer than threshold records added`() {
        assertFalse(GenerationEligibility.canRegenerateLearningSheet(14, 10))
    }

    @Test
    fun `learning sheet - blocks when count unchanged`() {
        assertFalse(GenerationEligibility.canRegenerateLearningSheet(10, 10))
    }

    @Test
    fun `learning sheet - blocks when count decreased (anti-cheat)`() {
        assertFalse(GenerationEligibility.canRegenerateLearningSheet(5, 10))
    }

    @Test
    fun `learning sheet - allows from zero saved count with threshold met`() {
        assertTrue(GenerationEligibility.canRegenerateLearningSheet(5, 0))
    }

    @Test
    fun `learning sheet - blocks from zero saved count below threshold`() {
        assertFalse(GenerationEligibility.canRegenerateLearningSheet(4, 0))
    }

    // ── canRegenerateQuiz ────────────────────────────────────────────

    @Test
    fun `quiz - allows first generation when quizHistoryCount is null`() {
        assertTrue(GenerationEligibility.canRegenerateQuiz(10, null))
    }

    @Test
    fun `quiz - allows when sheet version differs from quiz version`() {
        assertTrue(GenerationEligibility.canRegenerateQuiz(20, 10))
    }

    @Test
    fun `quiz - blocks when sheet version equals quiz version`() {
        assertFalse(GenerationEligibility.canRegenerateQuiz(10, 10))
    }

    @Test
    fun `quiz - allows when quiz count is higher than sheet count`() {
        // This is an unusual but valid state - still different versions
        assertTrue(GenerationEligibility.canRegenerateQuiz(10, 20))
    }

    @Test
    fun `quiz - allows with zero sheet count and null quiz count`() {
        assertTrue(GenerationEligibility.canRegenerateQuiz(0, null))
    }

    @Test
    fun `quiz - blocks with zero for both counts`() {
        assertFalse(GenerationEligibility.canRegenerateQuiz(0, 0))
    }
}
