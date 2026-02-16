package com.example.fyp.domain.history

import com.example.fyp.data.history.FirestoreHistoryRepository
import com.example.fyp.model.TranslationRecord
import com.example.fyp.model.UserId
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveUserHistoryUseCase @Inject constructor(
    private val repo: FirestoreHistoryRepository
) {
    operator fun invoke(userId: UserId): Flow<List<TranslationRecord>> = repo.getHistory(userId)
}