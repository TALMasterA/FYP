package com.example.fyp.domain.learning

import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import org.junit.Test

/**
 * Comprehensive unit tests for generation eligibility logic using GenerationEligibility domain object.
 *
 * Tests the anti-cheat rules that prevent abuse of material generation:
 * 1. Word bank regeneration: need MIN_RECORDS_FOR_REGEN (20) more records
 * 2. Quiz regeneration: sheet version must differ from quiz version
 */
class GenerationEligibilityIntegrationTest {

    // --- Word Bank Regeneration Eligibility ---

    @Test
    fun `first word bank generation is always allowed`() {
        assertTrue(GenerationEligibility.canRegenerateWordBank(50, 0))
    }

    @Test
    fun `regeneration allowed with exactly minimum records`() {
        assertTrue(GenerationEligibility.canRegenerateWordBank(70, 50))
    }

    @Test
    fun `regeneration allowed with more than minimum records`() {
        assertTrue(GenerationEligibility.canRegenerateWordBank(100, 50))
    }

    @Test
    fun `regeneration not allowed with less than minimum records`() {
        assertFalse(GenerationEligibility.canRegenerateWordBank(69, 50))
    }

    @Test
    fun `regeneration not allowed with same record count`() {
        assertFalse(GenerationEligibility.canRegenerateWordBank(50, 50))
    }

    @Test
    fun `regeneration not allowed with fewer records`() {
        assertFalse(GenerationEligibility.canRegenerateWordBank(40, 50))
    }

    // --- Quiz Regeneration Eligibility ---

    @Test
    fun `quiz regeneration allowed when versions differ`() {
        assertTrue(GenerationEligibility.canRegenerateQuiz(100, 50))
    }

    @Test
    fun `quiz regeneration not allowed when versions match`() {
        assertFalse(GenerationEligibility.canRegenerateQuiz(100, 100))
    }

    @Test
    fun `quiz regeneration allowed when sheet version is higher`() {
        assertTrue(GenerationEligibility.canRegenerateQuiz(150, 100))
    }

    // --- Edge Cases ---

    @Test
    fun `exactly 1 less than minimum - not eligible`() {
        assertFalse(GenerationEligibility.canRegenerateWordBank(69, 50))
    }

    @Test
    fun `exactly minimum - eligible`() {
        assertTrue(GenerationEligibility.canRegenerateWordBank(70, 50))
    }

    @Test
    fun `zero records edge case`() {
        assertTrue(GenerationEligibility.canRegenerateWordBank(100, 0))
        assertFalse(GenerationEligibility.canRegenerateWordBank(10, 0))
    }

    @Test
    fun `very large history counts`() {
        assertTrue(GenerationEligibility.canRegenerateWordBank(10020, 10000))
        assertFalse(GenerationEligibility.canRegenerateWordBank(10019, 10000))
    }

    // --- Scenario Tests ---

    @Test
    fun `typical word bank workflow`() {
        // User starts with no word bank
        assertTrue(GenerationEligibility.canRegenerateWordBank(10, 0))

        // User adds some history
        assertFalse(GenerationEligibility.canRegenerateWordBank(25, 10))

        // User adds more history
        assertTrue(GenerationEligibility.canRegenerateWordBank(30, 10))

        // After regeneration at count 30
        assertFalse(GenerationEligibility.canRegenerateWordBank(49, 30))
        assertTrue(GenerationEligibility.canRegenerateWordBank(50, 30))
    }

    @Test
    fun `typical quiz workflow`() {
        val sheetAtGeneration = 50
        val quizVersion = 50

        // User hasn't added new records
        assertFalse(GenerationEligibility.canRegenerateQuiz(sheetAtGeneration, quizVersion))

        // User adds new records
        assertTrue(GenerationEligibility.canRegenerateQuiz(55, quizVersion))
        assertTrue(GenerationEligibility.canRegenerateQuiz(70, quizVersion))
    }

    @Test
    fun `preventing accidental regeneration spam`() {
        // User at 100 records with word bank saved at 80
        assertTrue(GenerationEligibility.canRegenerateWordBank(100, 80))

        // Immediately tries again
        assertFalse(GenerationEligibility.canRegenerateWordBank(100, 100))

        // Adds just 10 more records
        assertFalse(GenerationEligibility.canRegenerateWordBank(110, 100))

        // Adds another 10 records
        assertTrue(GenerationEligibility.canRegenerateWordBank(120, 100))
    }

    @Test
    fun `test range around minimum 20 record increment`() {
        val saved = 50

        assertFalse(GenerationEligibility.canRegenerateWordBank(68, saved))  // +18
        assertFalse(GenerationEligibility.canRegenerateWordBank(69, saved))  // +19
        assertTrue(GenerationEligibility.canRegenerateWordBank(70, saved))   // +20
        assertTrue(GenerationEligibility.canRegenerateWordBank(71, saved))   // +21
    }

    @Test
    fun `multiple language pairs regenerate independently`() {
        // Spanish word bank can regenerate
        assertTrue(GenerationEligibility.canRegenerateWordBank(70, 50))

        // French word bank cannot
        assertFalse(GenerationEligibility.canRegenerateWordBank(65, 50))

        // Regenerating Spanish doesn't affect French
        assertFalse(GenerationEligibility.canRegenerateWordBank(65, 50))
        assertTrue(GenerationEligibility.canRegenerateWordBank(70, 50))
    }

    @Test
    fun `learning sheet regeneration requires 5 more records`() {
        // Learning sheets have lower threshold (5) than word banks (20)
        assertTrue(GenerationEligibility.canRegenerateLearningSheet(55, 50))
        assertFalse(GenerationEligibility.canRegenerateLearningSheet(54, 50))

        // First generation always allowed
        assertTrue(GenerationEligibility.canRegenerateLearningSheet(10, 0))
    }
}

