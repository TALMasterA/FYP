package com.example.fyp.domain.learning

/**
 * Pure domain logic for determining quiz coin eligibility.
 *
 * Extracted from [com.example.fyp.data.learning.FirestoreQuizRepository.awardCoinsIfEligible]
 * to enable comprehensive unit testing and separation of business logic from database operations.
 *
 * Rules:
 * 1. Score must be > 0
 * 2. Generated history count must be > 0 and match current history count
 * 3. Each quiz version can only be awarded once (managed by caller via versionKey)
 * 4. Must have 10+ more records than previously awarded quiz (prevents farming)
 * 5. First quiz for a language pair is always eligible (no minimum threshold)
 */
object CoinEligibility {
    const val MIN_INCREMENT_FOR_COINS = 10
    const val MIN_VALID_SCORE = 1

    /**
     * Determine if a completed quiz is eligible for coin awards.
     *
     * @param attemptScore The score from the completed quiz attempt
     * @param generatedHistoryCount The history count when the quiz was generated
     * @param currentHistoryCount The current user history count at award time
     * @param lastAwardedCount The history count at which coins were last awarded for this language pair (null if first)
     * @return true if the attempt is eligible for coins, false otherwise
     */
    fun isEligibleForCoins(
        attemptScore: Int,
        generatedHistoryCount: Int,
        currentHistoryCount: Int?,
        lastAwardedCount: Int?
    ): Boolean {
        // Check 1: Score must be > 0
        if (generatedHistoryCount <= 0) return false
        if (attemptScore < MIN_VALID_SCORE) return false

        // Check 2: Quiz count must EQUAL current history count
        // This prevents: user takes quiz at count=50, adds history to count=60, retakes same quiz to earn coins
        val current = currentHistoryCount ?: return false
        if (current != generatedHistoryCount) return false

        // Check 3: Anti-cheat - need 10+ more records than last awarded quiz count
        // (First quiz for a language pair is always eligible)
        if (lastAwardedCount != null) {
            val minRequired = lastAwardedCount + MIN_INCREMENT_FOR_COINS
            if (generatedHistoryCount < minRequired) {
                // User needs at least MIN_INCREMENT_FOR_COINS more records than previous awarded quiz
                return false
            }
        }

        return true
    }
}

