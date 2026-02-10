package com.example.fyp.data.learning

import com.example.fyp.domain.learning.CoinEligibility
import org.junit.Test
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue

/**
 * Comprehensive unit tests for anti-cheat coin eligibility logic using CoinEligibility domain object.
 *
 * Tests the eligibility rules for awarding coins on quiz completion:
 * 1. Score must be > 0
 * 2. Generated history count must > 0 and match current count
 * 3. Quiz version can only be awarded once
 * 4. Need 10+ more history records than previously awarded quiz
 * 5. First quiz for a language pair is always eligible
 */
class CoinEligibilityTest {

    // --- Basic Eligibility Checks (Rule 1 & 2) ---

    @Test
    fun `score 0 is not eligible`() {
        assertFalse(CoinEligibility.isEligibleForCoins(
            attemptScore = 0,
            generatedHistoryCount = 50,
            currentHistoryCount = 50,
            lastAwardedCount = null
        ))
    }

    @Test
    fun `negative score is not eligible`() {
        assertFalse(CoinEligibility.isEligibleForCoins(
            attemptScore = -5,
            generatedHistoryCount = 50,
            currentHistoryCount = 50,
            lastAwardedCount = null
        ))
    }

    @Test
    fun `generated history count 0 is not eligible`() {
        assertFalse(CoinEligibility.isEligibleForCoins(
            attemptScore = 10,
            generatedHistoryCount = 0,
            currentHistoryCount = 0,
            lastAwardedCount = null
        ))
    }

    // --- Version/History Count Consistency Checks (Rule 2) ---

    @Test
    fun `quiz count must equal current history count`() {
        // Matching count - eligible
        assertTrue(CoinEligibility.isEligibleForCoins(
            attemptScore = 10,
            generatedHistoryCount = 50,
            currentHistoryCount = 50,
            lastAwardedCount = null
        ))

        // Non-matching count - ineligible
        assertFalse(CoinEligibility.isEligibleForCoins(
            attemptScore = 10,
            generatedHistoryCount = 50,
            currentHistoryCount = 51,
            lastAwardedCount = null
        ))
    }

    @Test
    fun `null current history count makes quiz ineligible`() {
        assertFalse(CoinEligibility.isEligibleForCoins(
            attemptScore = 10,
            generatedHistoryCount = 50,
            currentHistoryCount = null,
            lastAwardedCount = null
        ))
    }

    // --- Anti-Cheat: Minimum Increment Rule (Rule 4) ---

    @Test
    fun `first quiz for language pair is always eligible`() {
        assertTrue(CoinEligibility.isEligibleForCoins(
            attemptScore = 10,
            generatedHistoryCount = 10,
            currentHistoryCount = 10,
            lastAwardedCount = null
        ))
    }

    @Test
    fun `exactly 10 more records than last awarded is eligible`() {
        assertTrue(CoinEligibility.isEligibleForCoins(
            attemptScore = 10,
            generatedHistoryCount = 60,
            currentHistoryCount = 60,
            lastAwardedCount = 50
        ))
    }

    @Test
    fun `more than 10 more records is eligible`() {
        assertTrue(CoinEligibility.isEligibleForCoins(
            attemptScore = 10,
            generatedHistoryCount = 65,
            currentHistoryCount = 65,
            lastAwardedCount = 50
        ))
    }

    @Test
    fun `less than 10 more records is not eligible`() {
        assertFalse(CoinEligibility.isEligibleForCoins(
            attemptScore = 10,
            generatedHistoryCount = 59,
            currentHistoryCount = 59,
            lastAwardedCount = 50
        ))
    }

    @Test
    fun `9 more records than last awarded is not eligible`() {
        assertFalse(CoinEligibility.isEligibleForCoins(
            attemptScore = 10,
            generatedHistoryCount = 59,
            currentHistoryCount = 59,
            lastAwardedCount = 50
        ))
    }

    // --- Edge Cases and Boundary Conditions ---

    @Test
    fun `minimum eligible score is 1`() {
        assertTrue(CoinEligibility.isEligibleForCoins(
            attemptScore = 1,
            generatedHistoryCount = 50,
            currentHistoryCount = 50,
            lastAwardedCount = null
        ))
    }

    @Test
    fun `very large history counts work correctly`() {
        assertTrue(CoinEligibility.isEligibleForCoins(
            attemptScore = 10,
            generatedHistoryCount = 10000,
            currentHistoryCount = 10000,
            lastAwardedCount = 9990
        ))
    }

    // --- Scenario Tests ---

    @Test
    fun `typical happy path - first quiz`() {
        assertTrue(CoinEligibility.isEligibleForCoins(
            attemptScore = 8,
            generatedHistoryCount = 100,
            currentHistoryCount = 100,
            lastAwardedCount = null
        ))
    }

    @Test
    fun `typical second quiz after good progress`() {
        assertTrue(CoinEligibility.isEligibleForCoins(
            attemptScore = 9,
            generatedHistoryCount = 115,
            currentHistoryCount = 115,
            lastAwardedCount = 100
        ))
    }

    @Test
    fun `rapid history growth allows multiple quiz attempts`() {
        // First quiz
        assertTrue(CoinEligibility.isEligibleForCoins(50, 50, 50, null))
        // Second quiz, +10 records
        assertTrue(CoinEligibility.isEligibleForCoins(10, 60, 60, 50))
        // Third quiz, +10 more records
        assertTrue(CoinEligibility.isEligibleForCoins(10, 70, 70, 60))
    }
}

