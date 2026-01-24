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
    private fun norm(code: String) = code.trim()
    private fun docId(primary: String, target: String) = "${norm(primary)}__${norm(target)}"

    private fun docRef(uid: String, primary: String, target: String) =
        db.collection("users")
            .document(uid)
            .collection("learning_sheets")
            .document(docId(primary, target))

    suspend fun getSheet(uid: String, primary: String, target: String): LearningSheetDoc? {
        val snap = docRef(uid, norm(primary), norm(target)).get().await()
        return if (snap.exists()) snap.toObject(LearningSheetDoc::class.java) else null
    }

    suspend fun upsertSheet(
        uid: String,
        primary: String,
        target: String,
        content: String,
        historyCountAtGenerate: Int
    ) {
        val p = norm(primary)
        val t = norm(target)
        val data = mapOf(
            "primaryLanguageCode" to p,
            "targetLanguageCode" to t,
            "content" to content,
            "historyCountAtGenerate" to historyCountAtGenerate,
            "updatedAt" to com.google.firebase.firestore.FieldValue.serverTimestamp()
        )
        docRef(uid, p, t).set(data).await()
    }
}