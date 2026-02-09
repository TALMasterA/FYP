package com.example.fyp.domain.history

import com.example.fyp.model.HistorySession
import com.example.fyp.model.TranslationRecord
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for managing translation history.
 * Abstracts the data source implementation (e.g., Firestore) from the domain layer.
 */
interface HistoryRepository {
    /**
     * Saves a translation record.
     */
    suspend fun save(record: TranslationRecord)

    /**
     * Deletes a translation record.
     */
    suspend fun delete(userId: String, recordId: String)

    /**
     * Observes translation history with a limit.
     */
    fun getHistory(userId: String, limit: Long): Flow<List<TranslationRecord>>

    /**
     * Gets the total count of translation records.
     */
    suspend fun getHistoryCount(userId: String): Int

    /**
     * Gets language pair counts for the user's primary language.
     */
    suspend fun getLanguageCounts(userId: String, primaryLanguageCode: String): Map<String, Int>

    /**
     * Updates the cached language counts.
     */
    suspend fun updateLanguageCountsCache(
        userId: String,
        sourceLang: String,
        targetLang: String,
        increment: Boolean = true
    )

    /**
     * Observes conversation sessions.
     */
    fun listenSessions(userId: String): Flow<List<HistorySession>>

    /**
     * Sets a custom name for a session.
     */
    suspend fun setSessionName(userId: String, sessionId: String, name: String)

    /**
     * Deletes a session and all its records.
     */
    suspend fun deleteSession(userId: String, sessionId: String)
}
