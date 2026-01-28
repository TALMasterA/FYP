package com.example.fyp.data.history

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

@Singleton
class FirestoreHistoryRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    companion object {
        // Default limit for history queries to reduce read costs
        const val DEFAULT_HISTORY_LIMIT = 200L
    }

    suspend fun save(record: TranslationRecord) {
        firestore.collection("users")
            .document(record.userId)
            .collection("history")
            .document(record.id)
            .set(record)
            .await()
    }

    suspend fun delete(userId: String, recordId: String) {
        firestore.collection("users")
            .document(userId)
            .collection("history")
            .document(recordId)
            .delete()
            .await()
    }

    /**
     * Observe history with a limit to reduce Firestore reads.
     * Returns most recent records first (descending by timestamp).
     */
    fun getHistory(userId: String, limit: Long = DEFAULT_HISTORY_LIMIT): Flow<List<TranslationRecord>> = callbackFlow {
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
    suspend fun getHistoryCount(userId: String): Int {
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
            -1
        }
    }

    fun listenSessions(userId: String): Flow<List<HistorySession>> = callbackFlow {
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

    suspend fun setSessionName(userId: String, sessionId: String, name: String) {
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

    suspend fun deleteSession(userId: String, sessionId: String) {
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
    }
}