@file:Suppress("unused")

package com.example.fyp.data.friends

import com.example.fyp.model.UserId
import com.example.fyp.model.friends.SharedItem
import com.example.fyp.model.friends.SharedItemStatus
import com.example.fyp.model.friends.SharedItemType
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirestoreSharingRepository @Inject constructor(
    private val db: FirebaseFirestore
) : SharingRepository {

    // ── Share Word ───────────────────────────────────────────────────────────

    /**
     * OPTIMIZED: Removed areFriends() Firestore read (UI enforces friends-only) and
     * getPublicProfile() read (caller now passes fromUsername from in-memory profile).
     * Net saving: 2 Firestore reads per share operation.
     */
    override suspend fun shareWord(
        fromUserId: UserId,
        fromUsername: String,
        toUserId: UserId,
        wordData: Map<String, Any>
    ): Result<SharedItem> {
        return try {
            val itemRef = db.collection("users")
                .document(toUserId.value)
                .collection("shared_inbox")
                .document()

            val sharedItem = SharedItem(
                itemId = itemRef.id,
                fromUserId = fromUserId.value,
                fromUsername = fromUsername,
                toUserId = toUserId.value,
                type = SharedItemType.WORD,
                content = wordData,
                status = SharedItemStatus.PENDING,
                createdAt = Timestamp.now()
            )

            itemRef.set(sharedItem).await()
            Result.success(sharedItem)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ── Share Learning Material ──────────────────────────────────────────────

    /**
     * Stores the shared item in the inbox AND writes the full content to a
     * separate sub-document (shared_inbox/{itemId}/content/body) so that
     * the main SharedItem document stays small (well within Firestore 1 MB),
     * while the full learning-sheet text is fetched on demand by the
     * SharedMaterialDetailScreen.
     */
    override suspend fun shareLearningMaterial(
        fromUserId: UserId,
        fromUsername: String,
        toUserId: UserId,
        type: SharedItemType,
        materialData: Map<String, Any>
    ): Result<SharedItem> {
        return try {
            require(type == SharedItemType.LEARNING_SHEET || type == SharedItemType.QUIZ) {
                "Invalid material type"
            }

            val itemRef = db.collection("users")
                .document(toUserId.value)
                .collection("shared_inbox")
                .document()

            // Strip fullContent out of the main doc to keep it small
            val fullContent = materialData["fullContent"] as? String ?: ""
            val contentForMainDoc = materialData.toMutableMap().apply { remove("fullContent") }

            val sharedItem = SharedItem(
                itemId = itemRef.id,
                fromUserId = fromUserId.value,
                fromUsername = fromUsername,
                toUserId = toUserId.value,
                type = type,
                content = contentForMainDoc,
                status = SharedItemStatus.PENDING,
                createdAt = Timestamp.now()
            )

            // Use a batch: write main item + full-content sub-doc atomically
            val batch = db.batch()
            batch.set(itemRef, sharedItem)

            if (fullContent.isNotBlank()) {
                val contentRef = itemRef.collection("content").document("body")
                batch.set(contentRef, mapOf("fullContent" to fullContent))
            }

            batch.commit().await()
            Result.success(sharedItem)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ── Accept / Dismiss ─────────────────────────────────────────────────────

    override suspend fun acceptSharedItem(
        itemId: String,
        userId: UserId
    ): Result<Unit> {
        return try {
            val itemRef = db.collection("users")
                .document(userId.value)
                .collection("shared_inbox")
                .document(itemId)

            val item = itemRef.get().await().toObject(SharedItem::class.java)
                ?: return Result.failure(IllegalArgumentException("Item not found"))

            if (item.toUserId != userId.value) {
                return Result.failure(IllegalArgumentException("Not authorized"))
            }

            itemRef.update("status", SharedItemStatus.ACCEPTED.name).await()

            when (item.type) {
                SharedItemType.WORD -> addWordToUserWordBank(userId, item.content)
                SharedItemType.LEARNING_SHEET -> { /* future: add to learning materials */ }
                SharedItemType.QUIZ -> { /* future: add to quiz collection */ }
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun dismissSharedItem(
        itemId: String,
        userId: UserId
    ): Result<Unit> = try {
        db.collection("users")
            .document(userId.value)
            .collection("shared_inbox")
            .document(itemId)
            .update("status", SharedItemStatus.DISMISSED.name)
            .await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    // ── Observe ──────────────────────────────────────────────────────────────

    override fun observeSharedInbox(userId: UserId): Flow<List<SharedItem>> = callbackFlow {
        val listener = db.collection("users")
            .document(userId.value)
            .collection("shared_inbox")
            .whereEqualTo("status", SharedItemStatus.PENDING.name)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val items = snapshot?.toObjects(SharedItem::class.java) ?: emptyList()
                trySend(items)
            }
        awaitClose { listener.remove() }
    }

    override suspend fun getPendingItemsCount(userId: UserId): Int = try {
        db.collection("users")
            .document(userId.value)
            .collection("shared_inbox")
            .whereEqualTo("status", SharedItemStatus.PENDING.name)
            .get()
            .await()
            .size()
    } catch (e: Exception) {
        0
    }

    override suspend fun fetchSharedItemFullContent(userId: UserId, itemId: String): String? = try {
        val doc = db.collection("users")
            .document(userId.value)
            .collection("shared_inbox")
            .document(itemId)
            .collection("content")
            .document("body")
            .get()
            .await()
        doc.getString("fullContent")
    } catch (e: Exception) {
        android.util.Log.w("SharingRepo", "fetchSharedItemFullContent failed for $itemId", e)
        null
    }

    // ── Private helpers ──────────────────────────────────────────────────────

    /**
     * Add a shared word to the recipient's custom_words collection.
     * Uses the same path/schema as FirestoreCustomWordsRepository so the word
     * appears immediately in the Word Bank screen without any migration.
     *
     * Shared word data keys (from ShareWordUseCase):
     *   "sourceText"  → CustomWord.originalWord
     *   "targetText"  → CustomWord.translatedWord
     *   "sourceLang"  → CustomWord.sourceLang
     *   "targetLang"  → CustomWord.targetLang
     *   "notes"       → CustomWord.example
     */
    private suspend fun addWordToUserWordBank(userId: UserId, wordData: Map<String, Any>) {
        try {
            val originalWord = wordData["sourceText"] as? String ?: return
            val translatedWord = wordData["targetText"] as? String ?: return
            val sourceLang = wordData["sourceLang"] as? String ?: return
            val targetLang = wordData["targetLang"] as? String ?: return
            val notes = wordData["notes"] as? String ?: ""

            if (originalWord.isBlank() || translatedWord.isBlank()) return

            // Write to users/{userId}/custom_words/{docId} — same collection that
            // FirestoreCustomWordsRepository reads, so the word shows in Word Bank instantly.
            val wordRef = db.collection("users")
                .document(userId.value)
                .collection("custom_words")
                .document()

            val word = mapOf(
                "id"             to wordRef.id,
                "userId"         to userId.value,
                "originalWord"   to originalWord.trim().take(200),
                "translatedWord" to translatedWord.trim().take(200),
                "pronunciation"  to "",
                "example"        to notes.trim().take(500),
                "sourceLang"     to sourceLang,
                "targetLang"     to targetLang,
                "createdAt"      to Timestamp.now()
            )

            wordRef.set(word).await()
        } catch (e: Exception) {
            android.util.Log.w("SharingRepo", "addWordToUserWordBank failed", e)
            // Non-fatal: item is still marked ACCEPTED in the inbox
        }
    }
}
