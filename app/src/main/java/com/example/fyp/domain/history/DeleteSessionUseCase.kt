package com.example.fyp.domain.history

import com.example.fyp.data.history.FirestoreHistoryRepository
import javax.inject.Inject

class DeleteSessionUseCase @Inject constructor(
    private val repo: FirestoreHistoryRepository
) {
    suspend operator fun invoke(userId: String, sessionId: String) {
        repo.deleteSession(userId, sessionId)
    }
}