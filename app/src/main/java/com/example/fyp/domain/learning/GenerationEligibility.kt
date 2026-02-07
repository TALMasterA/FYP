package com.example.fyp.domain.learning

/**
 * Pure domain logic for determining material generation and regeneration eligibility.
 *
 * Extracted from [com.example.fyp.screens.wordbank.WordBankViewModel] and related screens
 * to enable comprehensive unit testing and separation of business logic from database operations.
 *
 * Rules:
 * 1. Word bank regeneration requires MIN_RECORDS_FOR_WORD_BANK (20) more records since last generation
 * 2. Learning sheet regeneration requires MIN_RECORDS_FOR_LEARNING_SHEET (5) more records since last generation
 * 3. Quiz regeneration allowed when the generated quiz version (history count) differs from current version
 */
object GenerationEligibility {
    const val MIN_RECORDS_FOR_REGEN = 20
    const val MIN_RECORDS_FOR_WORD_BANK = 20
    const val MIN_RECORDS_FOR_LEARNING_SHEET = 5

    /**
     * Determine if a word bank can be regenerated based on history growth.
     *
     * @param currentHistoryCount Current count of translation records for the target language
     * @param savedHistoryCount The history count when the word bank was last generated
     * @return true if word bank can be regenerated, false otherwise
     */
    fun canRegenerateWordBank(
        currentHistoryCount: Int,
        savedHistoryCount: Int
    ): Boolean {
        val recordsAdded = currentHistoryCount - savedHistoryCount
        return recordsAdded >= MIN_RECORDS_FOR_WORD_BANK
    }

    /**
     * Determine if a learning quiz can be regenerated based on generation version mismatch.
     *
     * This prevents using a stale quiz when the underlying learning material (based on
     * history count) has changed significantly.
     *
     * @param currentHistoryCount The current user history count (determines current generation version)
     * @param generatedHistoryCount The history count when the quiz was generated
     * @return true if quiz should be regenerated (versions don't match), false if current quiz is up-to-date
     */
    fun canRegenerateQuiz(
        currentHistoryCount: Int,
        generatedHistoryCount: Int
    ): Boolean {
        return currentHistoryCount != generatedHistoryCount
    }

    /**
     * Determine if a learning sheet can be regenerated based on history growth.
     *
     * Learning sheets require MIN_RECORDS_FOR_LEARNING_SHEET more records for regeneration.
     *
     * @param currentHistoryCount Current count of translation records for the language pair
     * @param savedHistoryCount The history count when the learning material was last generated
     * @return true if learning sheet can be regenerated, false otherwise
     */
    fun canRegenerateLearningSheet(
        currentHistoryCount: Int,
        savedHistoryCount: Int
    ): Boolean {
        val recordsAdded = currentHistoryCount - savedHistoryCount
        return recordsAdded >= MIN_RECORDS_FOR_LEARNING_SHEET
    }
}

