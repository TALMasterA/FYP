package com.translator.TalknLearn.domain.history

import com.translator.TalknLearn.data.history.FirestoreHistoryRepository
import com.translator.TalknLearn.model.SessionId
import com.translator.TalknLearn.model.UserId
import javax.inject.Inject

class RenameSessionUseCase @Inject constructor(
    private val repo: FirestoreHistoryRepository
) {
    suspend operator fun invoke(userId: UserId, sessionId: SessionId, name: String) {
        repo.setSessionName(userId, sessionId, name)
    }
}