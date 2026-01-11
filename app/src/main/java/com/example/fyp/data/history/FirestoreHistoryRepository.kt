package com.example.fyp.data.history

import com.example.fyp.model.TranslationRecord
import com.google.firebase.firestore.FirebaseFirestore
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

    fun getHistory(userId: String): Flow<List<TranslationRecord>> = callbackFlow {
        val listener = firestore.collection("users")
            .document(userId)
            .collection("history")
            .orderBy("timestamp")
            //.limit(50)
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

        // delete the session meta doc too
        firestore.collection("users")
            .document(userId)
            .collection("sessions")
            .document(sessionId)
            .delete()
            .await()
    }
}