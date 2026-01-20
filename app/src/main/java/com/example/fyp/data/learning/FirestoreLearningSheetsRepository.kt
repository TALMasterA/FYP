package com.example.fyp.data.learning

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

data class LearningSheetDoc(
    val primaryLanguageCode: String = "",
    val targetLanguageCode: String = "",
    val content: String = "",
    val historyCountAtGenerate: Int = 0,
    val updatedAt: Timestamp? = null
)

class FirestoreLearningSheetsRepository @Inject constructor(
    private val db: FirebaseFirestore
) {
    private fun docId(primary: String, target: String) = "${primary}__${target}"

    private fun docRef(uid: String, primary: String, target: String) =
        db.collection("users")
            .document(uid)
            .collection("learning_sheets")
            .document(docId(primary, target))

    suspend fun getSheet(uid: String, primary: String, target: String): LearningSheetDoc? {
        val snap = docRef(uid, primary, target).get().await()
        return if (snap.exists()) snap.toObject(LearningSheetDoc::class.java) else null
    }

    suspend fun upsertSheet(
        uid: String,
        primary: String,
        target: String,
        content: String,
        historyCountAtGenerate: Int
    ) {
        val data = mapOf(
            "primaryLanguageCode" to primary,
            "targetLanguageCode" to target,
            "content" to content,
            "historyCountAtGenerate" to historyCountAtGenerate,
            "updatedAt" to com.google.firebase.firestore.FieldValue.serverTimestamp()
        )
        docRef(uid, primary, target).set(data).await()
    }
}