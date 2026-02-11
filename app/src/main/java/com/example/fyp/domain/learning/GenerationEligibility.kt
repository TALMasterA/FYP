package com.example.fyp.domain.learning

/**
 * Pure domain logic for determining material generation and regeneration eligibility.
 *
 * Rules:
 * 1. Word bank regeneration requires MIN_RECORDS_FOR_WORD_BANK (20) more records since last generation
 * 2. Learning sheet regeneration requires MIN_RECORDS_FOR_LEARNING_SHEET (5) more records since last generation
 * 3. Quiz regeneration allowed only when learning sheet version changes (sheet version != quiz version)
 * 4. All regeneration is blocked when current count is lower than saved count (anti-cheat)
 */
object GenerationEligibility {
    const val MIN_RECORDS_FOR_REGEN = 20
    const val MIN_RECORDS_FOR_WORD_BANK = 20
    const val MIN_RECORDS_FOR_LEARNING_SHEET = 5

    /**
     * Determine if a word bank can be regenerated based on history growth.
     * Blocked if current count is lower than saved count (anti-cheat).
     *
     * @param currentHistoryCount Current count of translation records for the target language
     * @param savedHistoryCount The history count when the word bank was last generated
     * @return true if word bank can be regenerated, false otherwise
     */
    fun canRegenerateWordBank(
        currentHistoryCount: Int,
        savedHistoryCount: Int
    ): Boolean {
        // Block if count decreased (anti-cheat)
        if (currentHistoryCount < savedHistoryCount) return false
        val recordsAdded = currentHistoryCount - savedHistoryCount
        return recordsAdded >= MIN_RECORDS_FOR_WORD_BANK
    }

    /**
     * Determine if a learning quiz can be regenerated based on learning sheet version.
     *
     * Quiz can ONLY be regenerated when the learning sheet version changes.
     * One sheet version = one quiz. User must regenerate learning materials first.
     *
     * @param sheetHistoryCount The history count when the learning sheet was generated
     * @param quizHistoryCount The history count when the quiz was generated (null if no quiz exists)
     * @return true if quiz can be regenerated (sheet version differs from quiz version), false otherwise
     */
    fun canRegenerateQuiz(
        sheetHistoryCount: Int,
        quizHistoryCount: Int?
    ): Boolean {
        // First quiz generation is always allowed
        if (quizHistoryCount == null) return true
        // Quiz can only be regenerated when sheet version changes
        return sheetHistoryCount != quizHistoryCount
    }

    /**
     * Determine if a learning sheet can be regenerated based on history growth.
     * Blocked if current count is lower than saved count (anti-cheat).
     *
     * @param currentHistoryCount Current count of translation records for the language pair
     * @param savedHistoryCount The history count when the learning material was last generated
     * @return true if learning sheet can be regenerated, false otherwise
     */
    fun canRegenerateLearningSheet(
        currentHistoryCount: Int,
        savedHistoryCount: Int
    ): Boolean {
        // Block if count decreased (anti-cheat)
        if (currentHistoryCount < savedHistoryCount) return false
        val recordsAdded = currentHistoryCount - savedHistoryCount
        return recordsAdded >= MIN_RECORDS_FOR_LEARNING_SHEET
    }
}

