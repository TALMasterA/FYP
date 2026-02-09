package com.example.fyp.data.history

import com.example.fyp.domain.history.HistoryRepository
import com.example.fyp.model.TranslationRecord
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
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

    override suspend fun save(record: TranslationRecord) {
        firestore.collection("users")
            .document(record.userId)
            .collection("history")
            .document(record.id)
            .set(record)
            .await()

        // Update language counts cache to keep it in sync
        updateLanguageCountsCache(record.userId, record.sourceLang, record.targetLang, increment = true)
    }

    override suspend fun delete(userId: String, recordId: String) {
        // Fetch the record first to get language info for cache update
        val record = try {
            firestore.collection("users")
                .document(userId)
                .collection("history")
                .document(recordId)
                .get()
                .await()
                .toObject(TranslationRecord::class.java)
        } catch (e: Exception) {
            null
        }

        firestore.collection("users")
            .document(userId)
            .collection("history")
            .document(recordId)
            .delete()
            .await()

        // Update language counts cache if we got the record info
        record?.let {
            updateLanguageCountsCache(userId, it.sourceLang, it.targetLang, increment = false)
        }
    }

    /**
     * Observe history with a limit to reduce Firestore reads.
     * Returns most recent records first (descending by timestamp).
     */
    override fun getHistory(userId: String, limit: Long): Flow<List<TranslationRecord>> = callbackFlow {
        val listener = firestore.collection("users")
            .document(userId)
            .collection("history")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(limit)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val records = snapshot?.toObjects(TranslationRecord::class.java) ?: emptyList()
                trySend(records)
            }
        awaitClose { listener.remove() }
    }

    /**
     * Get history count without fetching all documents (uses aggregation).
     * This is much cheaper than fetching all documents just to count them.
     */
    override suspend fun getHistoryCount(userId: String): Int {
        return try {
            val snapshot = firestore.collection("users")
                .document(userId)
                .collection("history")
                .count()
                .get(com.google.firebase.firestore.AggregateSource.SERVER)
                .await()
            snapshot.count.toInt()
        } catch (e: Exception) {
            // Fallback: if count aggregation fails, return -1 to indicate unknown
            Log.w("FirestoreHistoryRepository", "Failed to get history count for user $userId", e)
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
    override suspend fun getLanguageCounts(userId: String, primaryLanguageCode: String): Map<String, Int> {
        return try {
            // First try to read from the cached stats document (1 read instead of N reads)
            val statsDoc = firestore.collection("users")
                .document(userId)
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
            Log.w("FirestoreHistoryRepository", "Language counts cache missing for user $userId, rebuilding...")
            rebuildLanguageCountsCache(userId)
        } catch (e: Exception) {
            Log.w("FirestoreHistoryRepository", "Failed to get language counts for user $userId", e)
            emptyMap()
        }
    }

    /**
     * Update the language counts cache after saving a new history record.
     * This increments the counts for both source and target languages.
     * Call this after save() to keep the cache in sync.
     */
    override suspend fun updateLanguageCountsCache(userId: String, sourceLang: String, targetLang: String, increment: Boolean) {
        try {
            val statsRef = firestore.collection("users")
                .document(userId)
                .collection("user_stats")
                .document("language_counts")

            val updates = mutableMapOf<String, Any>()
            val delta = if (increment) 1 else -1

            if (sourceLang.isNotBlank()) {
                updates[sourceLang] = com.google.firebase.firestore.FieldValue.increment(delta.toLong())
            }
            if (targetLang.isNotBlank() && targetLang != sourceLang) {
                updates[targetLang] = com.google.firebase.firestore.FieldValue.increment(delta.toLong())
            }

            if (updates.isNotEmpty()) {
                statsRef.set(updates, com.google.firebase.firestore.SetOptions.merge()).await()
            }
        } catch (e: Exception) {
            Log.w("FirestoreHistoryRepository", "Failed to update language counts cache for user $userId", e)
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

    override fun listenSessions(userId: String): Flow<List<HistorySession>> = callbackFlow {
        val listener = firestore.collection("users")
            .document(userId)
            .collection("sessions")
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

    override suspend fun setSessionName(userId: String, sessionId: String, name: String) {
        firestore.collection("users")
            .document(userId)
            .collection("sessions")
            .document(sessionId)
            .set(
                HistorySession(
                    sessionId = sessionId,
                    name = name,
                    updatedAt = Timestamp.now()
                )
            )
            .await()
    }

    override suspend fun deleteSession(userId: String, sessionId: String) {
        val col = firestore.collection("users").document(userId).collection("history")

        while (true) {
            val snapshot = col.whereEqualTo("sessionId", sessionId).limit(400).get().await()
            if (snapshot.isEmpty) break

            val batch = firestore.batch()
            snapshot.documents.forEach { batch.delete(it.reference) }
            batch.commit().await()
        }

        firestore.collection("users")
            .document(userId)
            .collection("sessions")
            .document(sessionId)
            .delete()
            .await()

        // Rebuild language counts cache after bulk deletion
        // This is more efficient than tracking each deletion individually
        rebuildLanguageCountsCache(userId)
    }
}