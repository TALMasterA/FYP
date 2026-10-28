package com.translator.TalknLearn.domain.history

import com.translator.TalknLearn.data.history.FirestoreHistoryRepository
import com.translator.TalknLearn.model.SessionId
import com.translator.TalknLearn.model.UserId
import javax.inject.Inject

class DeleteSessionUseCase @Inject constructor(
    private val repo: FirestoreHistoryRepository
) {
    suspend operator fun invoke(userId: String, sessionId: String) {
        repo.deleteSession(UserId(userId), SessionId(sessionId))
    }
}