package com.example.fyp.data.learning

import com.example.fyp.domain.learning.CoinEligibility
import org.junit.Test
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue

/**
 * Comprehensive unit tests for anti-cheat coin eligibility logic using CoinEligibility domain object.
 *
 * Tests the eligibility rules for awarding coins on quiz completion:
 * 1. Score must be > 0 (1 correct answer = 1 coin)
 * 2. Only first attempt per quiz version can earn coins
 * 3. Quiz version must EQUAL current learning sheet version (historyCountAtGenerate)
 * 4. Need 10+ more history records than previously awarded quiz
 * 5. First quiz for a language pair is always eligible
 */
class CoinEligibilityTest {

    // --- Basic Eligibility Checks (Rule 1) ---

    @Test
    fun `score 0 is not eligible`() {
        assertFalse(CoinEligibility.isEligibleForCoins(
            attemptScore = 0,
            generatedHistoryCount = 50,
            currentSheetHistoryCount = 50,
            lastAwardedCount = null
        ))
    }

    @Test
    fun `negative score is not eligible`() {
        assertFalse(CoinEligibility.isEligibleForCoins(
            attemptScore = -5,
            generatedHistoryCount = 50,
            currentSheetHistoryCount = 50,
            lastAwardedCount = null
        ))
    }

    @Test
    fun `generated history count 0 is not eligible`() {
        assertFalse(CoinEligibility.isEligibleForCoins(
            attemptScore = 10,
            generatedHistoryCount = 0,
            currentSheetHistoryCount = 0,
            lastAwardedCount = null
        ))
    }

    // --- Quiz Version vs Sheet Version Checks (Rule 3) ---

    @Test
    fun `quiz version matches sheet version - eligible`() {
        assertTrue(CoinEligibility.isEligibleForCoins(
            attemptScore = 10,
            generatedHistoryCount = 50,
            currentSheetHistoryCount = 50,
            lastAwardedCount = null
        ))
    }

    @Test
    fun `quiz version does not match sheet version - ineligible`() {
        // Sheet was regenerated (version changed from 50 to 60), old quiz no longer valid
        assertFalse(CoinEligibility.isEligibleForCoins(
            attemptScore = 10,
            generatedHistoryCount = 50,
            currentSheetHistoryCount = 60,
            lastAwardedCount = null
        ))
    }

    @Test
    fun `sheet version newer than quiz version - ineligible`() {
        // User regenerated sheet, old quiz version is stale
        assertFalse(CoinEligibility.isEligibleForCoins(
            attemptScore = 10,
            generatedHistoryCount = 50,
            currentSheetHistoryCount = 51,
            lastAwardedCount = null
        ))
    }

    @Test
    fun `null sheet version makes quiz ineligible`() {
        assertFalse(CoinEligibility.isEligibleForCoins(
            attemptScore = 10,
            generatedHistoryCount = 50,
            currentSheetHistoryCount = null,
            lastAwardedCount = null
        ))
    }

    // --- Anti-Cheat: Minimum Increment Rule (Rule 4) ---

    @Test
    fun `first quiz for language pair is always eligible`() {
        assertTrue(CoinEligibility.isEligibleForCoins(
            attemptScore = 10,
            generatedHistoryCount = 10,
            currentSheetHistoryCount = 10,
            lastAwardedCount = null
        ))
    }

    @Test
    fun `exactly 10 more records than last awarded is eligible`() {
        assertTrue(CoinEligibility.isEligibleForCoins(
            attemptScore = 10,
            generatedHistoryCount = 60,
            currentSheetHistoryCount = 60,
            lastAwardedCount = 50
        ))
    }

    @Test
    fun `more than 10 more records is eligible`() {
        assertTrue(CoinEligibility.isEligibleForCoins(
            attemptScore = 10,
            generatedHistoryCount = 65,
            currentSheetHistoryCount = 65,
            lastAwardedCount = 50
        ))
    }

    @Test
    fun `less than 10 more records is not eligible`() {
        assertFalse(CoinEligibility.isEligibleForCoins(
            attemptScore = 10,
            generatedHistoryCount = 59,
            currentSheetHistoryCount = 59,
            lastAwardedCount = 50
        ))
    }

    @Test
    fun `9 more records than last awarded is not eligible`() {
        assertFalse(CoinEligibility.isEligibleForCoins(
            attemptScore = 10,
            generatedHistoryCount = 59,
            currentSheetHistoryCount = 59,
            lastAwardedCount = 50
        ))
    }

    // --- Edge Cases and Boundary Conditions ---

    @Test
    fun `minimum eligible score is 1 - one correct answer one coin`() {
        assertTrue(CoinEligibility.isEligibleForCoins(
            attemptScore = 1,
            generatedHistoryCount = 50,
            currentSheetHistoryCount = 50,
            lastAwardedCount = null
        ))
    }

    @Test
    fun `very large history counts work correctly`() {
        assertTrue(CoinEligibility.isEligibleForCoins(
            attemptScore = 10,
            generatedHistoryCount = 10000,
            currentSheetHistoryCount = 10000,
            lastAwardedCount = 9990
        ))
    }

    // --- Scenario Tests ---

    @Test
    fun `typical happy path - first quiz`() {
        assertTrue(CoinEligibility.isEligibleForCoins(
            attemptScore = 8,
            generatedHistoryCount = 100,
            currentSheetHistoryCount = 100,
            lastAwardedCount = null
        ))
    }

    @Test
    fun `typical second quiz after good progress`() {
        assertTrue(CoinEligibility.isEligibleForCoins(
            attemptScore = 9,
            generatedHistoryCount = 115,
            currentSheetHistoryCount = 115,
            lastAwardedCount = 100
        ))
    }

    @Test
    fun `rapid history growth allows multiple quiz attempts`() {
        // First quiz at sheet version 50
        assertTrue(CoinEligibility.isEligibleForCoins(50, 50, 50, null))
        // Second quiz at sheet version 60 (+10 records)
        assertTrue(CoinEligibility.isEligibleForCoins(10, 60, 60, 50))
        // Third quiz at sheet version 70 (+10 more records)
        assertTrue(CoinEligibility.isEligibleForCoins(10, 70, 70, 60))
    }

    @Test
    fun `user adds history and retakes old quiz - sheet regenerated - ineligible`() {
        // Quiz generated at count 50, but sheet was regenerated at count 60
        assertFalse(CoinEligibility.isEligibleForCoins(
            attemptScore = 10,
            generatedHistoryCount = 50,
            currentSheetHistoryCount = 60,
            lastAwardedCount = null
        ))
    }

    @Test
    fun `user adds history but does not regenerate sheet - old quiz still valid`() {
        // Quiz generated at count 50, sheet is still at version 50 (not regenerated)
        assertTrue(CoinEligibility.isEligibleForCoins(
            attemptScore = 10,
            generatedHistoryCount = 50,
            currentSheetHistoryCount = 50,
            lastAwardedCount = null
        ))
    }
}
