package com.example.fyp.data.learning

import com.example.fyp.domain.learning.LearningSheetsRepository
import com.example.fyp.domain.learning.SheetMetadata
import com.example.fyp.model.UserId
import com.example.fyp.model.LanguageCode
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
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

    override suspend fun getSheet(uid: UserId, primary: LanguageCode, target: LanguageCode): LearningSheetDoc? {
        val ref = docRef(uid.value, norm(primary.value), norm(target.value))
        // Try cache first for instant display, fall back to server
        val snap = try {
            val cached = ref.get(Source.CACHE).await()
            if (cached.exists()) cached else ref.get(Source.SERVER).await()
        } catch (_: Exception) {
            ref.get().await()
        }
        return if (snap.exists()) snap.toObject(LearningSheetDoc::class.java) else null
    }

    /**
     * Batch retrieves metadata for multiple language pairs using a single collection query.
     * This significantly reduces Firestore reads compared to individual getSheet calls.
     */
    override suspend fun getBatchSheetMetadata(
        uid: UserId,
        primary: LanguageCode,
        targets: List<String>
    ): Map<String, SheetMetadata> {
        if (targets.isEmpty()) return emptyMap()

        val p = norm(primary.value)
        val normalizedTargets = targets.map { norm(it) }
        
        // Build document IDs to query
        val docIds = normalizedTargets.map { docId(p, it) }

        // Firestore 'in' query is limited to 10 items (or 30 in some cases), but getting by ID is faster via getAll
        // However, we can't use getAll on a collection reference easily.
        // Instead, we'll use a collection group query or just multiple gets if list is small.
        // Efficient approach: fetch all docs in the collection that match the primary language?
        // No, that might be too broad.

        // Actually, we can just use `getAll` by constructing DocumentReferences
        val refs = docIds.map {
            db.collection("users").document(uid.value).collection("learning_sheets").document(it)
        }

        val snapshots = db.runBatch { batch ->
            // getAll is not available in batch in client SDK in the same way, but we can use Tasks.whenAll
        }
        // Wait, the Android SDK `getAll` is on `FirebaseFirestore` to fetch multiple docs.
        // It's `db.getAll(*refs.toTypedArray())`? No, that's server SDK.

        // For client SDK, we can just run parallel gets.
        // Or query where 'primaryLanguageCode' == p AND 'targetLanguageCode' IN targets
        // Creating a compound query. Limtit 10 for IN.

        val result = mutableMapOf<String, SheetMetadata>()
        
        // Let's use the query approach if possible, or parallel gets.
        // Given complexity, let's just do parallel gets for now, or use the existing logic if it was working.
        // The previous code seemed to be incomplete implemented or I missed it.
        // Let's implement it using 'in' query (chunks of 10)

        val chunks = normalizedTargets.chunked(10)
        for (chunk in chunks) {
            val q = db.collection("users").document(uid.value).collection("learning_sheets")
                .whereEqualTo("primaryLanguageCode", p)
                .whereIn("targetLanguageCode", chunk)

            val snaps = q.get().await()

            // Mark found ones
            snaps.documents.forEach { doc ->
                val data = doc.toObject(LearningSheetDoc::class.java)
                if (data != null) {
                    result[data.targetLanguageCode] = SheetMetadata(
                        exists = true,
                        historyCountAtGenerate = data.historyCountAtGenerate
                    )
                }
            }
        }

        // Fill missing as non-existent
        normalizedTargets.forEach { t ->
            if (!result.containsKey(t)) {
                result[t] = SheetMetadata(exists = false, historyCountAtGenerate = null)
            }
        }

        return result
    }

    override suspend fun upsertSheet(
        uid: UserId,
        primary: LanguageCode,
        target: LanguageCode,
        content: String,
        historyCountAtGenerate: Int
    ) {
        val p = norm(primary.value)
        val t = norm(target.value)
        val data = mapOf(
            "primaryLanguageCode" to p,
            "targetLanguageCode" to t,
            "content" to content,
            "historyCountAtGenerate" to historyCountAtGenerate,
            "updatedAt" to com.google.firebase.firestore.FieldValue.serverTimestamp()
        )
        docRef(uid.value, p, t).set(data).await()
    }
}