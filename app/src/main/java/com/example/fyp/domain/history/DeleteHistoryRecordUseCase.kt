package com.example.fyp.domain.history

import com.example.fyp.data.history.FirestoreHistoryRepository
import javax.inject.Inject

class DeleteHistoryRecordUseCase @Inject constructor(
    private val repo: FirestoreHistoryRepository
) {
    suspend operator fun invoke(userId: String, recordId: String) {
        repo.delete(userId, recordId)
    }
}