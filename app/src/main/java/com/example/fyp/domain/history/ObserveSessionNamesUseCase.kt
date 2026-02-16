package com.example.fyp.domain.history

import com.example.fyp.data.history.FirestoreHistoryRepository
import com.example.fyp.model.UserId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ObserveSessionNamesUseCase @Inject constructor(
    private val repo: FirestoreHistoryRepository
) {
    operator fun invoke(userId: String): Flow<Map<String, String>> =
        repo.listenSessions(UserId(userId)).map { list -> list.associate { it.sessionId to it.name } }
}