package com.example.fyp.data.history

import com.example.fyp.model.TranslationRecord
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

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

    fun getHistory(userId: String): Flow<List<TranslationRecord>> = callbackFlow {
        val listener = firestore.collection("users")
            .document(userId)
            .collection("history")
            .orderBy("timestamp")
            .limit(50)
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
}