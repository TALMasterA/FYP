@file:Suppress("unused")

package com.example.fyp.data.friends

import com.example.fyp.data.repositories.TranslationRepository
import com.example.fyp.model.SpeechResult
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
    private val db: FirebaseFirestore,
    private val translationRepository: TranslationRepository,
    private val friendsRepository: FriendsRepository
) : SharingRepository {

    internal data class SharedWordInsertPayload(
        val originalWord: String,
        val translatedWord: String,
        val sourceLang: String,
        val targetLang: String,
        val notes: String
    )

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
            if (!canShareToUser(fromUserId, toUserId)) {
                return Result.failure(SecurityException("Cannot share with this user"))
            }
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
            if (!canShareToUser(fromUserId, toUserId)) {
                return Result.failure(SecurityException("Cannot share with this user"))
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

            // Use a batch: write main item + full-content sub-doc atomically.
            // The sub-document is ALWAYS written so that fetchSharedItemFullContent
            // can distinguish "content stored (possibly empty)" from "doc missing (error)".
            val batch = db.batch()
            batch.set(itemRef, sharedItem)

            val contentRef = itemRef.collection("content").document("body")
            batch.set(contentRef, mapOf(
                "fullContent" to fullContent,
                "fromUserId" to fromUserId.value
            ))

            batch.commit().await()
            Result.success(sharedItem)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun canShareToUser(fromUserId: UserId, toUserId: UserId): Boolean {
        return try {
            friendsRepository.areFriends(fromUserId, toUserId) &&
                !friendsRepository.isBlocked(fromUserId, toUserId) &&
                !friendsRepository.isBlocked(toUserId, fromUserId)
        } catch (_: Exception) {
            false
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
                SharedItemType.WORD -> addWordToUserWordBank(
                    userId = userId,
                    senderUserId = item.fromUserId,
                    wordData = item.content
                )
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

    override suspend fun fetchSharedItemById(userId: UserId, itemId: String): SharedItem? = try {
        db.collection("users")
            .document(userId.value)
            .collection("shared_inbox")
            .document(itemId)
            .get()
            .await()
            .toObject(SharedItem::class.java)
    } catch (e: Exception) {
        android.util.Log.w("SharingRepo", "fetchSharedItemById failed for $itemId", e)
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
    private suspend fun addWordToUserWordBank(
        userId: UserId,
        senderUserId: String,
        wordData: Map<String, Any>
    ) {
        try {
            val senderPrimaryLanguage = fetchSenderPrimaryLanguageCode(senderUserId)
            val receiverPrimaryLanguage = fetchPrimaryLanguageCode(userId.value)
            val preparedWord = prepareSharedWordForRecipient(
                wordData = wordData,
                senderPrimaryLanguage = senderPrimaryLanguage,
                receiverPrimaryLanguage = receiverPrimaryLanguage,
                translateText = { text, fromLang, toLang ->
                    translationRepository.translate(
                        text = text,
                        fromLanguage = fromLang,
                        toLanguage = toLang
                    )
                }
            ) ?: return

            // Write to users/{userId}/custom_words/{docId} — same collection that
            // FirestoreCustomWordsRepository reads, so the word shows in Word Bank instantly.
            val wordRef = db.collection("users")
                .document(userId.value)
                .collection("custom_words")
                .document()

            val word = mapOf(
                "id"             to wordRef.id,
                "userId"         to userId.value,
                "originalWord"   to preparedWord.originalWord.trim().take(200),
                "translatedWord" to preparedWord.translatedWord.trim().take(200),
                "pronunciation"  to "",
                "example"        to preparedWord.notes.trim().take(500),
                "sourceLang"     to preparedWord.sourceLang,
                "targetLang"     to preparedWord.targetLang,
                "createdAt"      to Timestamp.now()
            )

            wordRef.set(word).await()
        } catch (e: Exception) {
            android.util.Log.w("SharingRepo", "addWordToUserWordBank failed", e)
            // Non-fatal: item is still marked ACCEPTED in the inbox
        }
    }

    private suspend fun fetchPrimaryLanguageCode(uid: String): String {
        if (uid.isBlank()) return "en-US"
        return try {
            db.collection("users")
                .document(uid)
                .collection("profile")
                .document("settings")
                .get()
                .await()
                .getString("primaryLanguageCode")
                .orEmpty()
                .ifBlank { "en-US" }
        } catch (e: Exception) {
            android.util.Log.w("SharingRepo", "fetchPrimaryLanguageCode failed for $uid", e)
            "en-US"
        }
    }

    /**
     * Sender primary language must be read from public profile.
     * Reading another user's private settings path is denied by Firestore rules.
     */
    private suspend fun fetchSenderPrimaryLanguageCode(uid: String): String {
        if (uid.isBlank()) return "en-US"
        return try {
            db.collection("users")
                .document(uid)
                .collection("profile")
                .document("public")
                .get()
                .await()
                .getString("primaryLanguage")
                .orEmpty()
                .ifBlank { "en-US" }
        } catch (e: Exception) {
            android.util.Log.w("SharingRepo", "fetchSenderPrimaryLanguageCode failed for $uid", e)
            "en-US"
        }
    }

    internal suspend fun prepareSharedWordForRecipient(
        wordData: Map<String, Any>,
        senderPrimaryLanguage: String,
        receiverPrimaryLanguage: String,
        translateText: suspend (text: String, fromLang: String, toLang: String) -> SpeechResult
    ): SharedWordInsertPayload? {
        val originalWord = wordData["sourceText"] as? String ?: return null
        val translatedWord = wordData["targetText"] as? String ?: return null
        val sourceLang = wordData["sourceLang"] as? String ?: return null
        val rawTargetLang = wordData["targetLang"] as? String ?: ""
        val notes = wordData["notes"] as? String ?: ""

        if (originalWord.isBlank() || translatedWord.isBlank() || sourceLang.isBlank()) return null

        var resolvedTranslatedWord = translatedWord
        var resolvedTargetLang = rawTargetLang.ifBlank { senderPrimaryLanguage.ifBlank { "en-US" } }

        val normalizedSenderPrimary = senderPrimaryLanguage.ifBlank { "en-US" }
        val normalizedReceiverPrimary = receiverPrimaryLanguage.ifBlank { "en-US" }
        val shouldTranslateForReceiver =
            !normalizedSenderPrimary.equals(normalizedReceiverPrimary, ignoreCase = true) &&
                !resolvedTargetLang.equals(normalizedReceiverPrimary, ignoreCase = true)

        if (shouldTranslateForReceiver) {
            val translationResult = try {
                translateText(resolvedTranslatedWord, resolvedTargetLang, normalizedReceiverPrimary)
            } catch (e: Exception) {
                android.util.Log.w("SharingRepo", "shared-word translation failed", e)
                null
            }

            if (translationResult is SpeechResult.Success && translationResult.text.isNotBlank()) {
                resolvedTranslatedWord = translationResult.text
                resolvedTargetLang = normalizedReceiverPrimary
            }
        }

        return SharedWordInsertPayload(
            originalWord = originalWord,
            translatedWord = resolvedTranslatedWord,
            sourceLang = sourceLang,
            targetLang = resolvedTargetLang,
            notes = notes
        )
    }
}
