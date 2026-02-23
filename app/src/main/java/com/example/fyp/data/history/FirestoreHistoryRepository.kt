package com.example.fyp.data.history

import com.example.fyp.core.NetworkRetry
import com.example.fyp.domain.history.HistoryRepository
import com.example.fyp.model.LanguageCode
import com.example.fyp.model.RecordId
import com.example.fyp.model.SessionId
import com.example.fyp.model.TranslationRecord
import com.example.fyp.model.UserId
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton
import com.example.fyp.model.HistorySession
import com.google.firebase.Timestamp
import android.util.Log

@Singleton
class FirestoreHistoryRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) : HistoryRepository {
    companion object {
        // Default limit for history queries to reduce read costs
        // Note: This is separate from UserSettings.historyViewLimit which controls UI display
        // This fetch limit should be >= the maximum possible UI display limit
        const val DEFAULT_HISTORY_LIMIT = 200L
    }

    // Track last fetched timestamp for incremental loading (Priority 3 #18)
    private val lastFetchTimestamp = mutableMapOf<String, Timestamp>()
    private val cachedRecords = mutableMapOf<String, List<TranslationRecord>>()

    // Background scope for non-blocking cache maintenance tasks
    private val backgroundScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override suspend fun save(record: TranslationRecord) {
        NetworkRetry.withRetry(shouldRetry = NetworkRetry::isRetryableFirebaseException) {
            firestore.collection("users")
                .document(record.userId)
                .collection("history")
                .document(record.id)
                .set(record)
                .await()
        }

        // Update language counts cache to keep it in sync
        updateLanguageCountsCache(
            UserId(record.userId),
            LanguageCode(record.sourceLang),
            LanguageCode(record.targetLang),
            increment = true
        )
    }

    /**
     * Batch-saves multiple [TranslationRecord]s using a single Firestore [WriteBatch].
     * One network round-trip instead of one per record — used by the continuous-mode
     * debounce flush to efficiently save a burst of segments together.
     *
     * After the batch write succeeds the per-language counts are updated incrementally.
     * Firestore WriteBatch supports up to 500 operations; records are split into chunks.
     */
    suspend fun saveBatch(records: List<TranslationRecord>) {
        if (records.isEmpty()) return

        // Firestore batch limit is 500 ops; split in chunks of 490 to leave headroom.
        records.chunked(490).forEach { chunk ->
            NetworkRetry.withRetry(shouldRetry = NetworkRetry::isRetryableFirebaseException) {
                val batch = firestore.batch()
                chunk.forEach { record ->
                    val ref = firestore.collection("users")
                        .document(record.userId)
                        .collection("history")
                        .document(record.id)
                    batch.set(ref, record)
                }
                batch.commit().await()
            }
            // Update per-language counts cache for each saved record
            chunk.forEach { record ->
                updateLanguageCountsCache(
                    UserId(record.userId),
                    LanguageCode(record.sourceLang),
                    LanguageCode(record.targetLang),
                    increment = true
                )
            }
        }
    }

    /**
     * Deletes a translation record.
     *
     * When [knownSourceLang] and [knownTargetLang] are supplied (e.g. from an
     * already-loaded [TranslationRecord]) the pre-delete document read is skipped,
     * saving 1 Firestore read per deletion.
     */
    override suspend fun delete(userId: UserId, recordId: RecordId) =
        delete(userId, recordId, null, null)

    suspend fun delete(
        userId: UserId,
        recordId: RecordId,
        knownSourceLang: LanguageCode?,
        knownTargetLang: LanguageCode?
    ) {
        // Only fetch the record if the caller didn't supply language codes.
        val (sourceLang, targetLang) = if (knownSourceLang != null && knownTargetLang != null) {
            knownSourceLang to knownTargetLang
        } else {
            try {
                val rec = firestore.collection("users")
                    .document(userId.value)
                    .collection("history")
                    .document(recordId.value)
                    .get()
                    .await()
                    .toObject(TranslationRecord::class.java)
                val s = rec?.sourceLang?.let { LanguageCode(it) }
                val t = rec?.targetLang?.let { LanguageCode(it) }
                s to t
            } catch (e: Exception) {
                null to null
            }
        }

        firestore.collection("users")
            .document(userId.value)
            .collection("history")
            .document(recordId.value)
            .delete()
            .await()

        if (sourceLang != null && targetLang != null) {
            updateLanguageCountsCache(userId, sourceLang, targetLang, increment = false)
        }
    }

    /**
     * Observe history with a limit to reduce Firestore reads.
     * Returns most recent records first (descending by timestamp).
     *
     * OPTIMIZATION (Priority 3 #18): Implements incremental loading.
     * After first load, only fetches records newer than last fetch timestamp,
     * reducing bandwidth and improving performance for large histories.
     */
    override fun getHistory(userId: UserId, limit: Long): Flow<List<TranslationRecord>> = callbackFlow {
        val uid = userId.value
        val listener = firestore.collection("users")
            .document(uid)
            .collection("history")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(limit)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val newRecords = snapshot?.toObjects(TranslationRecord::class.java) ?: emptyList()

                // Track the latest timestamp for incremental loading
                if (newRecords.isNotEmpty()) {
                    lastFetchTimestamp[uid] = newRecords.first().timestamp
                }

                // Cache records for merge with incremental updates
                cachedRecords[uid] = newRecords

                trySend(newRecords)
            }
        awaitClose {
            listener.remove()
            // Clean up cache when listener is removed
            cachedRecords.remove(uid)
            lastFetchTimestamp.remove(uid)
        }
    }

    /**
     * Load more history records for pagination (cursor-based).
     * This allows users to load older records beyond the initial limit.
     */
    override suspend fun loadMoreHistory(
        userId: UserId,
        limit: Long,
        lastTimestamp: Timestamp
    ): List<TranslationRecord> {
        return try {
            val snapshot = firestore.collection("users")
                .document(userId.value)
                .collection("history")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .startAfter(lastTimestamp)
                .limit(limit)
                .get()
                .await()

            snapshot.toObjects(TranslationRecord::class.java)
        } catch (e: Exception) {
            Log.w("FirestoreHistoryRepository", "Failed to load more history", e)
            emptyList()
        }
    }

    /**
     * Fetch only new records since last update (incremental loading).
     * This is called internally when we want to update history without re-fetching everything.
     */
    suspend fun getIncrementalHistory(userId: String): List<TranslationRecord> {
        val lastTimestamp = lastFetchTimestamp[userId] ?: return emptyList()

        return try {
            val snapshot = firestore.collection("users")
                .document(userId)
                .collection("history")
                .whereGreaterThan("timestamp", lastTimestamp)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .await()

            val newRecords = snapshot.toObjects(TranslationRecord::class.java)

            // Update timestamp if we got new records
            if (newRecords.isNotEmpty()) {
                lastFetchTimestamp[userId] = newRecords.first().timestamp

                // Merge with cached records
                val cached = cachedRecords[userId] ?: emptyList()
                val merged = (newRecords + cached).distinctBy { it.id }
                    .sortedByDescending { it.timestamp.seconds }
                cachedRecords[userId] = merged
            }

            newRecords
        } catch (e: Exception) {
            Log.w("FirestoreHistoryRepository", "Failed to get incremental history", e)
            emptyList()
        }
    }

    /**
     * Get history count without fetching all documents (uses aggregation).
     * This is much cheaper than fetching all documents just to count them.
     */
    override suspend fun getHistoryCount(userId: UserId): Int {
        return try {
            val snapshot = firestore.collection("users")
                .document(userId.value)
                .collection("history")
                .count()
                .get(com.google.firebase.firestore.AggregateSource.SERVER)
                .await()
            snapshot.count.toInt()
        } catch (e: Exception) {
            // Fallback: if count aggregation fails, return -1 to indicate unknown
            Log.w("FirestoreHistoryRepository", "Failed to get history count for user ${userId.value}", e)
            -1
        }
    }

    /**
     * Get counts of records per language (for learning/quiz/wordbank generation).
     * Returns map of languageCode -> count where the language appears in sourceLang or targetLang.
     *
     * OPTIMIZATION: Reads from a cached stats document instead of fetching all history records.
     * The stats document is maintained by updateLanguageCountsCache() which should be called
     * after each history save/delete operation. This reduces reads from N (all records) to 1.
     *
     * NOTE: Does NOT filter by primary language - caller should filter as needed.
     */
    override suspend fun getLanguageCounts(userId: UserId, primaryLanguageCode: LanguageCode): Map<String, Int> {
        return try {
            // First try to read from the cached stats document (1 read instead of N reads)
            val statsDoc = firestore.collection("users")
                .document(userId.value)
                .collection("user_stats")
                .document("language_counts")
                .get()
                .await()

            if (statsDoc.exists()) {
                val counts = mutableMapOf<String, Int>()
                @Suppress("UNCHECKED_CAST")
                val data = statsDoc.data as? Map<String, Any?> ?: emptyMap()

                data.forEach { (lang, count) ->
                    if (lang.isNotEmpty() && count is Number) {
                        counts[lang] = count.toInt()
                    }
                }

                if (counts.isNotEmpty()) {
                    return counts
                }
            }

            // Fallback: if cache doesn't exist, compute from history records and update cache
            Log.w("FirestoreHistoryRepository", "Language counts cache missing for user ${userId.value}, rebuilding...")
            rebuildLanguageCountsCache(userId.value)
        } catch (e: Exception) {
            Log.w("FirestoreHistoryRepository", "Failed to get language counts for user ${userId.value}", e)
            emptyMap()
        }
    }

    /**
     * Update the language counts cache after saving a new history record.
     * This increments the counts for both source and target languages.
     * Call this after save() to keep the cache in sync.
     */
    override suspend fun updateLanguageCountsCache(userId: UserId, sourceLang: LanguageCode, targetLang: LanguageCode, increment: Boolean) {
        try {
            val statsRef = firestore.collection("users")
                .document(userId.value)
                .collection("user_stats")
                .document("language_counts")

            val updates = mutableMapOf<String, Any>()
            val delta = if (increment) 1 else -1

            if (sourceLang.value.isNotBlank()) {
                updates[sourceLang.value] = com.google.firebase.firestore.FieldValue.increment(delta.toLong())
            }
            if (targetLang.value.isNotBlank() && targetLang.value != sourceLang.value) {
                updates[targetLang.value] = com.google.firebase.firestore.FieldValue.increment(delta.toLong())
            }

            if (updates.isNotEmpty()) {
                statsRef.set(updates, com.google.firebase.firestore.SetOptions.merge()).await()
            }
        } catch (e: Exception) {
            Log.w("FirestoreHistoryRepository", "Failed to update language counts cache for user ${userId.value}", e)
        }
    }

    /**
     * Rebuild the language counts cache from all history records.
     * This is called when the cache is missing or needs to be recalculated.
     * Should only be needed once per user (on first use) or after data corruption.
     */
    private suspend fun rebuildLanguageCountsCache(userId: String): Map<String, Int> {
        return try {
            val snapshot = firestore.collection("users")
                .document(userId)
                .collection("history")
                .limit(10_000)
                .get()
                .await()

            val counts = mutableMapOf<String, Int>()

            snapshot.documents.forEach { doc ->
                val sourceLang = doc.getString("sourceLang")?.trim() ?: ""
                val targetLang = doc.getString("targetLang")?.trim() ?: ""

                listOf(sourceLang, targetLang).forEach { lang ->
                    if (lang.isNotEmpty()) {
                        counts[lang] = (counts[lang] ?: 0) + 1
                    }
                }
            }

            // Save the rebuilt cache
            if (counts.isNotEmpty()) {
                firestore.collection("users")
                    .document(userId)
                    .collection("user_stats")
                    .document("language_counts")
                    .set(counts)
                    .await()
            }

            counts
        } catch (e: Exception) {
            Log.w("FirestoreHistoryRepository", "Failed to rebuild language counts cache for user $userId", e)
            emptyMap()
        }
    }

    override fun listenSessions(userId: UserId): Flow<List<HistorySession>> = callbackFlow {
        val listener = firestore.collection("users")
            .document(userId.value)
            .collection("sessions")
            .limit(1_000)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val list = snapshot?.toObjects(HistorySession::class.java) ?: emptyList()
                trySend(list)
            }
        awaitClose { listener.remove() }
    }

    override suspend fun setSessionName(userId: UserId, sessionId: SessionId, name: String) {
        firestore.collection("users")
            .document(userId.value)
            .collection("sessions")
            .document(sessionId.value)
            .set(
                HistorySession(
                    sessionId = sessionId.value,
                    name = name,
                    updatedAt = Timestamp.now()
                )
            )
            .await()
    }

    override suspend fun deleteSession(userId: UserId, sessionId: SessionId) {
        val col = firestore.collection("users").document(userId.value).collection("history")

        while (true) {
            val snapshot = col.whereEqualTo("sessionId", sessionId.value).limit(400).get().await()
            if (snapshot.isEmpty) break

            val batch = firestore.batch()
            snapshot.documents.forEach { batch.delete(it.reference) }
            batch.commit().await()
        }

        firestore.collection("users")
            .document(userId.value)
            .collection("sessions")
            .document(sessionId.value)
            .delete()
            .await()

        // Rebuild language counts cache after bulk deletion.
        // Launched on background scope so the delete operation returns immediately —
        // the UI is not blocked while the full history is re-read.
        backgroundScope.launch {
            try {
                rebuildLanguageCountsCache(userId.value)
            } catch (e: Exception) {
                Log.w("FirestoreHistoryRepository", "Background cache rebuild failed after deleteSession", e)
            }
        }
    }
}