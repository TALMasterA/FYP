package com.example.fyp.data.learning

import com.example.fyp.domain.learning.LearningSheetsRepository
import com.example.fyp.domain.learning.SheetMetadata
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
) : LearningSheetsRepository {
    private fun norm(code: String) = code.trim()
    private fun docId(primary: String, target: String) = "${norm(primary)}__${norm(target)}"

    private fun docRef(uid: String, primary: String, target: String) =
        db.collection("users")
            .document(uid)
            .collection("learning_sheets")
            .document(docId(primary, target))

    override suspend fun getSheet(uid: String, primary: String, target: String): LearningSheetDoc? {
        val snap = docRef(uid, norm(primary), norm(target)).get().await()
        return if (snap.exists()) snap.toObject(LearningSheetDoc::class.java) else null
    }

    /**
     * Batch retrieves metadata for multiple language pairs using a single collection query.
     * This significantly reduces Firestore reads compared to individual getSheet calls.
     */
    override suspend fun getBatchSheetMetadata(
        uid: String,
        primary: String,
        targets: List<String>
    ): Map<String, SheetMetadata> {
        if (targets.isEmpty()) return emptyMap()

        val p = norm(primary)
        val normalizedTargets = targets.map { norm(it) }
        
        // Build document IDs to query
        val docIds = normalizedTargets.map { docId(p, it) }

        // Batch get all documents in one query
        val collectionRef = db.collection("users")
            .document(uid)
            .collection("learning_sheets")

        // Use whereIn to get multiple documents (limited to 10 per query)
        val result = mutableMapOf<String, SheetMetadata>()
        
        // Process in chunks of 10 (Firestore whereIn limit)
        docIds.chunked(10).zip(normalizedTargets.chunked(10)).forEach { (chunkIds, chunkTargets) ->
            val snapshot = collectionRef
                .whereIn(com.google.firebase.firestore.FieldPath.documentId(), chunkIds)
                .get()
                .await()

            // Map found documents
            val foundDocs = snapshot.documents.associateBy { it.id }
            
            // Build result for this chunk
            chunkTargets.forEachIndexed { idx, target ->
                val docId = chunkIds[idx]
                val doc = foundDocs[docId]
                result[target] = if (doc != null && doc.exists()) {
                    SheetMetadata(
                        exists = true,
                        historyCountAtGenerate = doc.getLong("historyCountAtGenerate")?.toInt()
                    )
                } else {
                    SheetMetadata(exists = false, historyCountAtGenerate = null)
                }
            }
        }

        return result
    }

    override suspend fun upsertSheet(
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