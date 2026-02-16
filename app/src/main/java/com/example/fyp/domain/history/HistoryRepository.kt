package com.example.fyp.domain.history

import com.example.fyp.model.HistorySession
import com.example.fyp.model.LanguageCode
import com.example.fyp.model.RecordId
import com.example.fyp.model.SessionId
import com.example.fyp.model.TranslationRecord
import com.example.fyp.model.UserId
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
    suspend fun delete(userId: UserId, recordId: RecordId)

    /**
     * Observes translation history with a limit.
     */
    fun getHistory(userId: UserId, limit: Long = 200): Flow<List<TranslationRecord>>

    /**
     * Load more history records for pagination (cursor-based).
     * @param userId User ID
     * @param limit Number of records to load
     * @param lastTimestamp Timestamp of the last record from previous page (cursor)
     * @return List of older records after the cursor
     */
    suspend fun loadMoreHistory(
        userId: UserId,
        limit: Long,
        lastTimestamp: Timestamp
    ): List<TranslationRecord>

    /**
     * Gets the total count of translation records.
     */
    suspend fun getHistoryCount(userId: UserId): Int

    /**
     * Gets language pair counts for the user's primary language.
     */
    suspend fun getLanguageCounts(userId: UserId, primaryLanguageCode: LanguageCode): Map<String, Int>

    /**
     * Updates the cached language counts.
     */
    suspend fun updateLanguageCountsCache(
        userId: UserId,
        sourceLang: LanguageCode,
        targetLang: LanguageCode,
        increment: Boolean = true
    )

    /**
     * Observes conversation sessions.
     */
    fun listenSessions(userId: UserId): Flow<List<HistorySession>>

    /**
     * Sets a custom name for a session.
     */
    suspend fun setSessionName(userId: UserId, sessionId: SessionId, name: String)

    /**
     * Deletes a session and all its records.
     */
    suspend fun deleteSession(userId: UserId, sessionId: SessionId)
}
