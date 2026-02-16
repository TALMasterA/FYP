package com.example.fyp.domain.history

import com.example.fyp.model.HistorySession
import com.example.fyp.model.TranslationRecord
import com.google.firebase.Timestamp
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
    fun getHistory(userId: String, limit: Long = 200): Flow<List<TranslationRecord>>

    /**
     * Load more history records for pagination (cursor-based).
     * @param userId User ID
     * @param limit Number of records to load
     * @param lastTimestamp Timestamp of the last record from previous page (cursor)
     * @return List of older records after the cursor
     */
    suspend fun loadMoreHistory(
        userId: String,
        limit: Long,
        lastTimestamp: Timestamp
    ): List<TranslationRecord>

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
