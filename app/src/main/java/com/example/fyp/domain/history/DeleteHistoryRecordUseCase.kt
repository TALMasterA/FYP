package com.example.fyp.domain.history

import com.example.fyp.data.history.FirestoreHistoryRepository
import javax.inject.Inject

/**
 * Use case for deleting a translation record from user history.
 *
 * @param repo Repository for managing translation history
 */
class DeleteHistoryRecordUseCase @Inject constructor(
    private val repo: FirestoreHistoryRepository
) {
    /**
     * Deletes a specific translation record.
     *
     * @param userId The ID of the user who owns the record
     * @param recordId The ID of the record to delete
     */
    suspend operator fun invoke(userId: String, recordId: String) {
        repo.delete(userId, recordId)
    }
}