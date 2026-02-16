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
    private val db: FirebaseFirestore,
    private val friendsRepository: FriendsRepository
) : SharingRepository {

    override suspend fun shareWord(
        fromUserId: UserId,
        toUserId: UserId,
        wordData: Map<String, Any>
    ): Result<SharedItem> {
        return try {
            // Verify friendship
            if (!friendsRepository.areFriends(fromUserId, toUserId)) {
                return Result.failure(IllegalStateException("Users are not friends"))
            }

            // Get sender profile
            val fromProfile = friendsRepository.getPublicProfile(fromUserId)
                ?: return Result.failure(IllegalStateException("Sender profile not found"))

            val itemRef = db.collection("users")
                .document(toUserId.value)
                .collection("shared_inbox")
                .document()

            val sharedItem = SharedItem(
                itemId = itemRef.id,
                fromUserId = fromUserId.value,
                fromUsername = fromProfile.username,
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

    override suspend fun shareLearningMaterial(
        fromUserId: UserId,
        toUserId: UserId,
        type: SharedItemType,
        materialData: Map<String, Any>
    ): Result<SharedItem> {
        return try {
            // Verify friendship
            if (!friendsRepository.areFriends(fromUserId, toUserId)) {
                return Result.failure(IllegalStateException("Users are not friends"))
            }

            // Validate type
            require(type == SharedItemType.LEARNING_SHEET || type == SharedItemType.QUIZ) {
                "Invalid material type"
            }

            // Get sender profile
            val fromProfile = friendsRepository.getPublicProfile(fromUserId)
                ?: return Result.failure(IllegalStateException("Sender profile not found"))

            val itemRef = db.collection("users")
                .document(toUserId.value)
                .collection("shared_inbox")
                .document()

            val sharedItem = SharedItem(
                itemId = itemRef.id,
                fromUserId = fromUserId.value,
                fromUsername = fromProfile.username,
                toUserId = toUserId.value,
                type = type,
                content = materialData,
                status = SharedItemStatus.PENDING,
                createdAt = Timestamp.now()
            )

            itemRef.set(sharedItem).await()
            Result.success(sharedItem)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

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

            // Verify ownership
            if (item.toUserId != userId.value) {
                return Result.failure(IllegalArgumentException("Not authorized"))
            }

            // Update status
            itemRef.update("status", SharedItemStatus.ACCEPTED.name).await()

            // Handle different item types
            when (item.type) {
                SharedItemType.WORD -> addWordToUserWordBank(userId, item.content)
                SharedItemType.LEARNING_SHEET -> {
                    // In a real implementation, add to user's learning materials
                }
                SharedItemType.QUIZ -> {
                    // In a real implementation, add to user's quiz collection
                }
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

    override fun observeSharedInbox(userId: UserId): Flow<List<SharedItem>> = callbackFlow {
        val listener = db.collection("users")
            .document(userId.value)
            .collection("shared_inbox")
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

    private suspend fun addWordToUserWordBank(userId: UserId, wordData: Map<String, Any>) {
        try {
            // Extract word details
            val sourceText = wordData["sourceText"] as? String ?: return
            val targetText = wordData["targetText"] as? String ?: return
            val sourceLang = wordData["sourceLang"] as? String ?: return
            val targetLang = wordData["targetLang"] as? String ?: return

            // Add to user's custom words collection
            val wordRef = db.collection("users")
                .document(userId.value)
                .collection("wordbank")
                .document(sourceLang)
                .collection("words")
                .document()

            val word = mapOf(
                "id" to wordRef.id,
                "sourceText" to sourceText,
                "targetText" to targetText,
                "sourceLang" to sourceLang,
                "targetLang" to targetLang,
                "isCustom" to true,
                "sharedByFriend" to true,
                "addedAt" to Timestamp.now()
            )

            wordRef.set(word).await()
        } catch (e: Exception) {
            // Log but don't fail the accept operation
        }
    }
}
