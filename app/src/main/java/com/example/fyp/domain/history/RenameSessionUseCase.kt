package com.example.fyp.domain.history

import com.example.fyp.data.history.FirestoreHistoryRepository
import com.example.fyp.model.SessionId
import com.example.fyp.model.UserId
import javax.inject.Inject

class RenameSessionUseCase @Inject constructor(
    private val repo: FirestoreHistoryRepository
) {
    suspend operator fun invoke(userId: UserId, sessionId: SessionId, name: String) {
        repo.setSessionName(userId, sessionId, name)
    }
}