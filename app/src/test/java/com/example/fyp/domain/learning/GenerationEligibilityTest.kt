package com.example.fyp.domain.learning

import org.junit.Assert.*
import org.junit.Test

/**
 * Comprehensive unit tests for [GenerationEligibility].
 *
 * Tests the eligibility rules that gate material regeneration:
 * 1. Word bank: currentCount >= 20 AND currentCount > lastGeneratedCount (or null)
 * 2. Learning sheet: currentCount >= 5 AND currentCount > lastGeneratedCount (or null)
 * 3. Quiz: quizHistoryCount is null OR sheetHistoryCount > quizHistoryCount
 */
class GenerationEligibilityTest {

    // ══════════════════════════════════════════
    //  canRegenerateWordBank – below minimum
    // ══════════════════════════════════════════

    @Test
    fun `word bank - below minimum with null lastGenerated returns false`() {
        assertFalse(GenerationEligibility.canRegenerateWordBank(0, null))
        assertFalse(GenerationEligibility.canRegenerateWordBank(19, null))
    }

    @Test
    fun `word bank - below minimum with non-null lastGenerated returns false`() {
        assertFalse(GenerationEligibility.canRegenerateWordBank(10, 5))
        assertFalse(GenerationEligibility.canRegenerateWordBank(19, 10))
    }

    // ══════════════════════════════════════════
    //  canRegenerateWordBank – at minimum
    // ══════════════════════════════════════════

    @Test
    fun `word bank - exactly at minimum with null lastGenerated returns true`() {
        assertTrue(GenerationEligibility.canRegenerateWordBank(20, null))
    }

    @Test
    fun `word bank - at minimum equal to lastGenerated returns false`() {
        assertFalse(GenerationEligibility.canRegenerateWordBank(20, 20))
    }

    @Test
    fun `word bank - at minimum greater than lastGenerated returns true`() {
        assertTrue(GenerationEligibility.canRegenerateWordBank(20, 10))
    }

    // ══════════════════════════════════════════
    //  canRegenerateWordBank – above minimum
    // ══════════════════════════════════════════

    @Test
    fun `word bank - above minimum with null lastGenerated returns true`() {
        assertTrue(GenerationEligibility.canRegenerateWordBank(50, null))
    }

    @Test
    fun `word bank - above minimum and greater than lastGenerated returns true`() {
        assertTrue(GenerationEligibility.canRegenerateWordBank(51, 50))
        assertTrue(GenerationEligibility.canRegenerateWordBank(100, 50))
    }

    @Test
    fun `word bank - above minimum but equal to lastGenerated returns false`() {
        assertFalse(GenerationEligibility.canRegenerateWordBank(50, 50))
    }

    @Test
    fun `word bank - above minimum but less than lastGenerated returns false`() {
        assertFalse(GenerationEligibility.canRegenerateWordBank(25, 50))
    }

    // ══════════════════════════════════════════
    //  canRegenerateLearningSheet – below minimum
    // ══════════════════════════════════════════

    @Test
    fun `learning sheet - below minimum with null lastGenerated returns false`() {
        assertFalse(GenerationEligibility.canRegenerateLearningSheet(0, null))
        assertFalse(GenerationEligibility.canRegenerateLearningSheet(4, null))
    }

    @Test
    fun `learning sheet - below minimum with non-null lastGenerated returns false`() {
        assertFalse(GenerationEligibility.canRegenerateLearningSheet(3, 2))
    }

    // ══════════════════════════════════════════
    //  canRegenerateLearningSheet – at minimum
    // ══════════════════════════════════════════

    @Test
    fun `learning sheet - exactly at minimum with null lastGenerated returns true`() {
        assertTrue(GenerationEligibility.canRegenerateLearningSheet(5, null))
    }

    @Test
    fun `learning sheet - at minimum equal to lastGenerated returns false`() {
        assertFalse(GenerationEligibility.canRegenerateLearningSheet(5, 5))
    }

    @Test
    fun `learning sheet - at minimum greater than lastGenerated returns true`() {
        assertTrue(GenerationEligibility.canRegenerateLearningSheet(5, 3))
    }

    // ══════════════════════════════════════════
    //  canRegenerateLearningSheet – above minimum
    // ══════════════════════════════════════════

    @Test
    fun `learning sheet - above minimum with null lastGenerated returns true`() {
        assertTrue(GenerationEligibility.canRegenerateLearningSheet(30, null))
    }

    @Test
    fun `learning sheet - above minimum and greater than lastGenerated returns true`() {
        assertTrue(GenerationEligibility.canRegenerateLearningSheet(11, 10))
        assertTrue(GenerationEligibility.canRegenerateLearningSheet(50, 10))
    }

    @Test
    fun `learning sheet - above minimum but equal to lastGenerated returns false`() {
        assertFalse(GenerationEligibility.canRegenerateLearningSheet(30, 30))
    }

    @Test
    fun `learning sheet - above minimum but less than lastGenerated returns false`() {
        assertFalse(GenerationEligibility.canRegenerateLearningSheet(20, 30))
    }

    // ══════════════════════════════════════════
    //  canRegenerateQuiz
    // ══════════════════════════════════════════

    @Test
    fun `quiz - null quizHistoryCount always returns true`() {
        assertTrue(GenerationEligibility.canRegenerateQuiz(0, null))
        assertTrue(GenerationEligibility.canRegenerateQuiz(50, null))
    }

    @Test
    fun `quiz - sheetHistoryCount greater than quizHistoryCount returns true`() {
        assertTrue(GenerationEligibility.canRegenerateQuiz(51, 50))
        assertTrue(GenerationEligibility.canRegenerateQuiz(100, 1))
    }

    @Test
    fun `quiz - sheetHistoryCount equal to quizHistoryCount returns false`() {
        assertFalse(GenerationEligibility.canRegenerateQuiz(50, 50))
        assertFalse(GenerationEligibility.canRegenerateQuiz(0, 0))
    }

    @Test
    fun `quiz - sheetHistoryCount less than quizHistoryCount returns false`() {
        assertFalse(GenerationEligibility.canRegenerateQuiz(49, 50))
        assertFalse(GenerationEligibility.canRegenerateQuiz(0, 10))
    }

    // ══════════════════════════════════════════
    //  Constants
    // ══════════════════════════════════════════

    @Test
    fun `constants have expected values`() {
        assertEquals(20, GenerationEligibility.MIN_RECORDS_FOR_WORD_BANK)
        assertEquals(5, GenerationEligibility.MIN_RECORDS_FOR_LEARNING_SHEET)
    }

    // ══════════════════════════════════════════
    //  Scenario tests
    // ══════════════════════════════════════════

    @Test
    fun `word bank workflow - first generation then incremental`() {
        // Not enough records yet
        assertFalse(GenerationEligibility.canRegenerateWordBank(10, null))

        // Reaches minimum → first generation allowed
        assertTrue(GenerationEligibility.canRegenerateWordBank(20, null))

        // After generating at 20, same count blocked
        assertFalse(GenerationEligibility.canRegenerateWordBank(20, 20))

        // One more record → allowed
        assertTrue(GenerationEligibility.canRegenerateWordBank(21, 20))

        // Count decreased (e.g. records deleted) → blocked
        assertFalse(GenerationEligibility.canRegenerateWordBank(15, 21))
    }

    @Test
    fun `quiz workflow - depends only on sheet vs quiz history`() {
        // First quiz always allowed
        assertTrue(GenerationEligibility.canRegenerateQuiz(5, null))

        // After generating quiz at sheetCount 5, same count blocked
        assertFalse(GenerationEligibility.canRegenerateQuiz(5, 5))

        // New sheet generated → quiz allowed again
        assertTrue(GenerationEligibility.canRegenerateQuiz(6, 5))
    }

    @Test
    fun `large counts work correctly`() {
        assertTrue(GenerationEligibility.canRegenerateWordBank(10001, 10000))
        assertFalse(GenerationEligibility.canRegenerateWordBank(10000, 10000))
        assertTrue(GenerationEligibility.canRegenerateLearningSheet(10001, 10000))
        assertFalse(GenerationEligibility.canRegenerateLearningSheet(10000, 10000))
    }
}

