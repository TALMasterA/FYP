package com.example.fyp.domain.learning

/**
 * Pure domain logic for quiz coin-earning eligibility checks.
 *
 * Used as a client-side pre-check before calling the awardQuizCoins Cloud Function.
 * The server performs the authoritative check; this avoids unnecessary network calls
 * when the client can already determine that a quiz attempt is ineligible.
 *
 * Rules:
 * 1. Score must be >= [MIN_VALID_SCORE] (at least 1 correct answer)
 * 2. Generated history count must be > 0
 * 3. Current sheet history count must match generated count (anti-cheat: prevents
 *    adding history after quiz generation to inflate eligibility)
 * 4. First attempt for a language pair is always eligible (lastAwardedCount == null)
 * 5. Subsequent attempts require [MIN_INCREMENT_FOR_COINS] (10) more records
 *    than the last awarded quiz
 */
object CoinEligibility {
    const val MIN_INCREMENT_FOR_COINS = 10
    const val MIN_VALID_SCORE = 1

    /**
     * Determine whether a quiz attempt is eligible for coin rewards.
     *
     * @param attemptScore Number of correct answers in the quiz attempt
     * @param generatedHistoryCount History count when the quiz was generated
     * @param currentSheetHistoryCount Current history count at submission time
     * @param lastAwardedCount History count of the last quiz that earned coins, or null if none
     * @return true if the attempt is eligible to earn coins
     */
    fun isEligibleForCoins(
        attemptScore: Int,
        generatedHistoryCount: Int,
        currentSheetHistoryCount: Int?,
        lastAwardedCount: Int?
    ): Boolean {
        // Must have at least 1 correct answer
        if (attemptScore < MIN_VALID_SCORE) return false

        // Must have generated from real history
        if (generatedHistoryCount <= 0) return false

        // History count must not have changed since generation (anti-cheat)
        if (currentSheetHistoryCount == null || currentSheetHistoryCount != generatedHistoryCount) return false

        // First quiz for this language pair is always eligible
        if (lastAwardedCount == null) return true

        // Need MIN_INCREMENT_FOR_COINS more records than last awarded quiz
        return generatedHistoryCount - lastAwardedCount >= MIN_INCREMENT_FOR_COINS
    }
}
