package com.example.fyp.domain.learning

/**
 * Pure domain logic for determining quiz coin eligibility.
 *
 * Extracted from [com.example.fyp.data.learning.FirestoreQuizRepository.awardCoinsIfEligible]
 * to enable comprehensive unit testing and separation of business logic from database operations.
 *
 * Rules (must match server-side awardQuizCoins Cloud Function):
 * 1. Score must be > 0 (1 correct answer = 1 coin)
 * 2. Only first attempt per quiz version can earn coins (managed by caller via versionKey)
 * 3. Quiz version must EQUAL current learning sheet version (historyCountAtGenerate)
 * 4. Must have 10+ more records than previously awarded quiz (prevents farming)
 * 5. First quiz for a language pair is always eligible (no minimum threshold)
 */
object CoinEligibility {
    const val MIN_INCREMENT_FOR_COINS = 10
    const val MIN_VALID_SCORE = 1

    /**
     * Determine if a completed quiz is eligible for coin awards.
     *
     * @param attemptScore The score from the completed quiz attempt (1 coin per correct answer)
     * @param generatedHistoryCount The history count when the quiz was generated
     * @param currentSheetHistoryCount The current learning sheet's historyCountAtGenerate
     *        (read from Firestore, NOT the user's live history count).
     *        This ensures the quiz matches the current sheet version.
     * @param lastAwardedCount The history count at which coins were last awarded for this language pair (null if first)
     * @return true if the attempt is eligible for coins, false otherwise
     */
    fun isEligibleForCoins(
        attemptScore: Int,
        generatedHistoryCount: Int,
        currentSheetHistoryCount: Int?,
        lastAwardedCount: Int?
    ): Boolean {
        // Check 1: Generated history count must be positive and score must be > 0
        if (generatedHistoryCount <= 0) return false
        if (attemptScore < MIN_VALID_SCORE) return false

        // Check 2: Quiz version must EQUAL current learning sheet version.
        // This prevents: user regenerates the sheet (new version), then tries to submit
        // a quiz from the old version to earn coins.
        // Server does: generatedCount !== currentSheetVersion => version_mismatch
        val sheetVersion = currentSheetHistoryCount ?: return false
        if (generatedHistoryCount != sheetVersion) return false

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

