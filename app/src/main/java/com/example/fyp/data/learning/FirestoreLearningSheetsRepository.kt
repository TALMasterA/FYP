package com.example.fyp.data.learning

import com.example.fyp.core.NetworkRetry
import com.example.fyp.domain.learning.LearningSheetsRepository
import com.example.fyp.domain.learning.SheetMetadata
import com.example.fyp.model.UserId
import com.example.fyp.model.LanguageCode
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.Source
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

data class LearningSheetDoc(
    val primaryLanguageCode: String = "",
    val targetLanguageCode: String = "",
    val content: String = "",
    val historyCountAtGenerate: Int = 0,
    val updatedAt: Timestamp? = null
)

data class QuizVersionDoc(
    val primaryLanguageCode: String = "",
    val targetLanguageCode: String = "",
    val historyCount: Int = 0,
    val sourceSheetId: String = "",
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

    private fun quizVersionRef(uid: String, primary: String, target: String) =
        db.collection("users")
            .document(uid)
            .collection("quiz_versions")
            .document(docId(primary, target))

    override suspend fun getSheet(uid: UserId, primary: LanguageCode, target: LanguageCode): LearningSheetDoc? {
        val p = norm(primary.value)
        val t = norm(target.value)
        val ref = docRef(uid.value, p, t)

        // Read sheet and server-owned version in parallel.
        // IMPORTANT: Use SERVER source for the sheet to ensure cross-device sync works correctly.
        // Cache-first approach can show stale "not found" on a new device before sync completes.
        val (sheetSnap, versionSnap) = coroutineScope {
            val sheetDeferred = async {
                try {
                    // Prefer server to ensure latest data is fetched (critical for cross-device sync).
                    // Fall back to default behavior only if server fetch fails (offline mode).
                    ref.get(Source.SERVER).await()
                } catch (_: Exception) {
                    // Offline or server error: fall back to cache-then-server default
                    ref.get().await()
                }
            }
            val versionDeferred = async {
                try {
                    quizVersionRef(uid.value, p, t).get(Source.SERVER).await()
                } catch (_: Exception) {
                    quizVersionRef(uid.value, p, t).get().await()
                }
            }
            Pair(sheetDeferred.await(), versionDeferred.await())
        }

        val sheet = if (sheetSnap.exists()) sheetSnap.toObject(LearningSheetDoc::class.java) else null
        if (sheet == null) return null

        val serverVersion = versionSnap.getLong("historyCount")?.toInt()
        return if (serverVersion != null && serverVersion > 0) {
            sheet.copy(historyCountAtGenerate = serverVersion)
        } else {
            sheet
        }
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
        val result = mutableMapOf<String, SheetMetadata>()

        val chunks = normalizedTargets.chunked(10)
        // Fetch all chunks in parallel to reduce latency
        // Use SERVER source to ensure cross-device sync works correctly
        val chunkResults = coroutineScope {
            chunks.map { chunk ->
                async {
                    val chunkDocIds = chunk.map { docId(p, it) }
                    val q = db.collection("users").document(uid.value).collection("learning_sheets")
                        .whereEqualTo("primaryLanguageCode", p)
                        .whereIn("targetLanguageCode", chunk)
                    val versionQuery = db.collection("users").document(uid.value).collection("quiz_versions")
                        .whereIn(FieldPath.documentId(), chunkDocIds)

                    // Prefer server source for cross-device sync; fall back to default on network error
                    val sheetSnap = try {
                        q.get(Source.SERVER).await()
                    } catch (_: Exception) {
                        q.get().await()
                    }
                    val versionSnap = try {
                        versionQuery.get(Source.SERVER).await()
                    } catch (_: Exception) {
                        versionQuery.get().await()
                    }
                    Pair(sheetSnap, versionSnap)
                }
            }.awaitAll()
        }

        for ((sheetSnaps, versionSnaps) in chunkResults) {
            val serverVersionByDocId = versionSnaps.documents.associate { doc ->
                doc.id to ((doc.getLong("historyCount") ?: 0L).toInt())
            }

            // Mark found ones
            sheetSnaps.documents.forEach { doc ->
                val data = doc.toObject(LearningSheetDoc::class.java)
                if (data != null) {
                    val currentDocId = doc.id
                    val resolvedCount = serverVersionByDocId[currentDocId]
                        ?.takeIf { it > 0 }
                        ?: data.historyCountAtGenerate
                    result[data.targetLanguageCode] = SheetMetadata(
                        exists = true,
                        historyCountAtGenerate = resolvedCount
                    )
                }
            }

            // If a server-owned version exists but sheet query missed due stale local state,
            // still expose metadata using the target parsed from document ID.
            serverVersionByDocId.forEach { (id, count) ->
                if (count <= 0) return@forEach
                val split = id.split("__", limit = 2)
                if (split.size == 2) {
                    val target = split[1]
                    if (!result.containsKey(target)) {
                        result[target] = SheetMetadata(exists = true, historyCountAtGenerate = count)
                    }
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
        NetworkRetry.withRetry(shouldRetry = NetworkRetry::isRetryableFirebaseException) {
            docRef(uid.value, p, t).set(data).await()
        }
    }
}