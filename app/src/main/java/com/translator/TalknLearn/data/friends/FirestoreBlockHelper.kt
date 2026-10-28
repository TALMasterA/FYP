package com.translator.TalknLearn.data.friends

import com.translator.TalknLearn.model.UserId
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

/**
 * Handles user blocking/unblocking within Firestore.
 * Extracted from [FirestoreFriendsRepository] for maintainability.
 */
internal class FirestoreBlockHelper(private val db: FirebaseFirestore) {

    suspend fun unblockUser(userId: UserId, blockedUserId: UserId): Result<Unit> = try {
        db.collection("users")
            .document(userId.value)
            .collection("blocked_users")
            .document(blockedUserId.value)
            .delete()
            .await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun isBlocked(userId: UserId, otherUserId: UserId): Boolean = try {
        db.collection("users")
            .document(userId.value)
            .collection("blocked_users")
            .document(otherUserId.value)
            .get()
            .await()
            .exists()
    } catch (e: Exception) {
        false
    }

    suspend fun isBlockedBy(userId: UserId, otherUserId: UserId): Boolean = try {
        db.collection("users")
            .document(otherUserId.value)
            .collection("blocked_users")
            .document(userId.value)
            .get()
            .await()
            .exists()
    } catch (e: Exception) {
        false
    }

    suspend fun getBlockedUserIds(userId: UserId): List<String> = try {
        db.collection("users")
            .document(userId.value)
            .collection("blocked_users")
            .get()
            .await()
            .documents
            .map { it.id }
    } catch (e: Exception) {
        emptyList()
    }

    suspend fun getBlockedUsers(userId: UserId): List<BlockedUser> = try {
        db.collection("users")
            .document(userId.value)
            .collection("blocked_users")
            .orderBy("blockedAt", Query.Direction.DESCENDING)
            .limit(200)
            .get()
            .await()
            .documents
            .map { doc ->
                BlockedUser(
                    userId = doc.id,
                    username = doc.getString("blockedUsername") ?: doc.id,
                    blockedAt = doc.getTimestamp("blockedAt")?.seconds ?: 0L
                )
            }
    } catch (e: Exception) {
        emptyList()
    }
}
