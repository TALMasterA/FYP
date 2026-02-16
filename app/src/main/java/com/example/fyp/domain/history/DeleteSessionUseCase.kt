package com.example.fyp.domain.history

import com.example.fyp.data.history.FirestoreHistoryRepository
import com.example.fyp.model.SessionId
import com.example.fyp.model.UserId
import javax.inject.Inject

class DeleteSessionUseCase @Inject constructor(
    private val repo: FirestoreHistoryRepository
) {
    suspend operator fun invoke(userId: String, sessionId: String) {
        repo.deleteSession(UserId(userId), SessionId(sessionId))
    }
}