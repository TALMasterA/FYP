package com.example.fyp.domain.learning

import com.example.fyp.data.learning.LearningSheetDoc
import com.example.fyp.model.LanguageCode
import com.example.fyp.model.UserId

/**
 * Metadata for a learning sheet (without full content).
 */
data class SheetMetadata(
    val exists: Boolean,
    val historyCountAtGenerate: Int?
)

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
    suspend fun getSheet(uid: UserId, primary: LanguageCode, target: LanguageCode): LearningSheetDoc?

    /**
     * Batch retrieves metadata for multiple language pairs.
     * This is optimized to reduce the number of Firestore reads.
     * @param uid User ID
     * @param primary Primary language code
     * @param targets List of target language codes
     * @return Map of target language code to metadata
     */
    suspend fun getBatchSheetMetadata(
        uid: UserId,
        primary: LanguageCode,
        targets: List<String>
    ): Map<String, SheetMetadata>

    /**
     * Creates or updates a learning sheet.
     * @param uid User ID
     * @param primary Primary language code
     * @param target Target language code
     * @param content The learning content
     * @param historyCountAtGenerate Number of history records when generated
     */
    suspend fun upsertSheet(
        uid: UserId,
        primary: LanguageCode,
        target: LanguageCode,
        content: String,
        historyCountAtGenerate: Int
    )
}
