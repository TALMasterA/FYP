package com.example.fyp.domain.learning

import com.example.fyp.data.learning.LearningSheetDoc

/**
 * Repository interface for managing learning sheets.
 * Abstracts the data source implementation (e.g., Firestore) from the domain layer.
 */
interface LearningSheetsRepository {
    /**
     * Retrieves a learning sheet for the given language pair.
     * @param uid User ID
     * @param primary Primary language code
     * @param target Target language code
     * @return LearningSheetDoc if found, null otherwise
     */
    suspend fun getSheet(uid: String, primary: String, target: String): LearningSheetDoc?

    /**
     * Creates or updates a learning sheet.
     * @param uid User ID
     * @param primary Primary language code
     * @param target Target language code
     * @param content The learning content
     * @param historyCountAtGenerate Number of history records when generated
     */
    suspend fun upsertSheet(
        uid: String,
        primary: String,
        target: String,
        content: String,
        historyCountAtGenerate: Int
    )
}
