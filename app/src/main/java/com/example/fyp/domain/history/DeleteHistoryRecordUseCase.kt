package com.example.fyp.domain.history

import com.example.fyp.data.history.FirestoreHistoryRepository
import com.example.fyp.model.LanguageCode
import com.example.fyp.model.RecordId
import com.example.fyp.model.UserId
import javax.inject.Inject

/**
 * Use case for deleting a translation record from user history.
 *
 * Accepts optional [sourceLang] / [targetLang] so callers that already have the
 * TranslationRecord in memory can supply the language codes, letting the repository
 * skip its pre-delete Firestore read (saves 1 read per delete).
 */
class DeleteHistoryRecordUseCase @Inject constructor(
    private val repo: FirestoreHistoryRepository
) {
    /**
     * Deletes a specific translation record.
     *
     * @param userId The ID of the user who owns the record
     * @param recordId The ID of the record to delete
     * @param sourceLang The source language code (optional)
     * @param targetLang The target language code (optional)
     */
    suspend operator fun invoke(
        userId: String,
        recordId: String,
        sourceLang: String? = null,
        targetLang: String? = null
    ) {
        // Call the 4-arg overload directly on the concrete class; it skips the pre-read
        // when language codes are known.
        repo.delete(
            userId = UserId(userId),
            recordId = RecordId(recordId),
            knownSourceLang = sourceLang?.takeIf { it.isNotBlank() }?.let { LanguageCode(it) },
            knownTargetLang = targetLang?.takeIf { it.isNotBlank() }?.let { LanguageCode(it) }
        )
    }
}