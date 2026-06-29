package com.translator.TalknLearn.data.friends

import com.translator.TalknLearn.core.AppLogger
import com.translator.TalknLearn.model.UserId
import com.translator.TalknLearn.model.friends.FriendRelation
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

/**
 * Handles friend-list queries, removal, and username synchronisation.
 * Extracted from [FirestoreFriendsRepository] for maintainability.
 */
internal class FirestoreFriendListHelper(private val db: FirebaseFirestore) {

    companion object {
        private const val MAX_FRIENDS_PER_SYNC = 100L
        private const val USERNAME_SYNC_FRESHNESS_MS = 3_600_000L // 1 hour
    }

    @Volatile private var lastUsernameSyncTime = 0L

    fun observeFriends(userId: UserId): Flow<List<FriendRelation>> = callbackFlow {
        val listener = db.collection("users")
            .document(userId.value)
            .collection("friends")
            .orderBy("addedAt", Query.Direction.DESCENDING)
            .limit(500)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val friends = snapshot?.toObjects(FriendRelation::class.java) ?: emptyList()
                trySend(friends)
            }
        awaitClose { listener.remove() }
    }

    suspend fun removeFriend(userId: UserId, friendId: UserId): Result<Unit> = try {
        val batch = db.batch()

        val userFriendRef = db.collection("users")
            .document(userId.value)
            .collection("friends")
            .document(friendId.value)
        batch.delete(userFriendRef)

        val friendUserRef = db.collection("users")
            .document(friendId.value)
            .collection("friends")
            .document(userId.value)
        batch.delete(friendUserRef)

        batch.commit().await()

        // Clean up ALL friend_request documents in both directions (any status).
        try {
            val (snap1, snap2) = coroutineScope {
                val d1 = async {
                    db.collection("friend_requests")
                        .whereEqualTo("fromUserId", userId.value)
                        .whereEqualTo("toUserId", friendId.value)
                        .get().await()
                }
                val d2 = async {
                    db.collection("friend_requests")
                        .whereEqualTo("fromUserId", friendId.value)
                        .whereEqualTo("toUserId", userId.value)
                        .get().await()
                }
                Pair(d1.await(), d2.await())
            }
            val allDocs = snap1.documents + snap2.documents
            if (allDocs.isNotEmpty()) {
                coroutineScope {
                    allDocs.chunked(500).map { chunk ->
                        async {
                            val cleanupBatch = db.batch()
                            chunk.forEach { doc -> cleanupBatch.delete(doc.reference) }
                            cleanupBatch.commit().await()
                        }
                    }.forEach { it.await() }
                }
            }
        } catch (e: Exception) {
            AppLogger.w("FriendListHelper", "friend_request cleanup failed (non-fatal): ${e.message}")
        }

        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun getFriendCount(userId: UserId): Int = try {
        db.collection("users")
            .document(userId.value)
            .collection("friends")
            .get()
            .await()
            .size()
    } catch (e: Exception) {
        0
    }

    suspend fun areFriends(userId: UserId, otherUserId: UserId): Boolean = try {
        val doc = db.collection("users")
            .document(userId.value)
            .collection("friends")
            .document(otherUserId.value)
            .get()
            .await()
        doc.exists()
    } catch (e: Exception) {
        false
    }

    suspend fun getFriendIds(userId: UserId): List<String> = try {
        db.collection("users")
            .document(userId.value)
            .collection("friends")
            .limit(500)
            .get()
            .await()
            .documents
            .map { it.id }
    } catch (e: Exception) {
        emptyList()
    }

    suspend fun propagateUsernameChange(
        userId: UserId,
        newUsername: String
    ): Result<Unit> {
        return try {
            val friendDocs = db.collection("users")
                .document(userId.value)
                .collection("friends")
                .limit(500)
                .get()
                .await()
                .documents

            if (friendDocs.isNotEmpty()) {
                friendDocs.chunked(500).forEach { chunk ->
                    val batch = db.batch()
                    chunk.forEach { friendDoc ->
                        val friendId = friendDoc.getString("friendId") ?: friendDoc.id
                        val ref = db.collection("users")
                            .document(friendId)
                            .collection("friends")
                            .document(userId.value)
                        batch.set(ref, mapOf("friendUsername" to newUsername),
                            com.google.firebase.firestore.SetOptions.merge())
                    }
                    batch.commit().await()
                }
            }

            val pendingOutgoing = db.collection("friend_requests")
                .whereEqualTo("fromUserId", userId.value)
                .whereEqualTo("status", "PENDING")
                .limit(100)
                .get()
                .await()
                .documents

            if (pendingOutgoing.isNotEmpty()) {
                pendingOutgoing.chunked(500).forEach { chunk ->
                    val batch = db.batch()
                    chunk.forEach { doc ->
                        batch.update(doc.reference, "fromUsername", newUsername)
                    }
                    batch.commit().await()
                }
            }

            Result.success(Unit)
        } catch (e: Exception) {
            AppLogger.e("FriendListHelper", "propagateUsernameChange failed", e)
            Result.failure(e)
        }
    }

    suspend fun syncFriendUsernames(userId: UserId): Map<String, String> {
        val now = System.currentTimeMillis()
        if (now - lastUsernameSyncTime < USERNAME_SYNC_FRESHNESS_MS) return emptyMap()
        lastUsernameSyncTime = now

        return try {
            val friendDocs = db.collection("users")
                .document(userId.value)
                .collection("friends")
                .limit(MAX_FRIENDS_PER_SYNC)
                .get()
                .await()
                .documents

            if (friendDocs.isEmpty()) return emptyMap()

            val cachedByFriendId: Map<String, String> = friendDocs.associate { doc ->
                val fId = doc.getString("friendId") ?: doc.id
                fId to (doc.getString("friendUsername") ?: "")
            }

            val freshUsernames: Map<String, String> = coroutineScope {
                cachedByFriendId.keys
                    .map { fId ->
                        fId to async<String> {
                            try {
                                db.collection("users").document(fId)
                                    .collection("profile").document("public")
                                    .get().await()
                                    .getString("username").orEmpty()
                            } catch (_: Exception) { "" }
                        }
                    }
                    .associate { (fId, deferred) -> fId to deferred.await() }
                    .filterValues { it.isNotBlank() }
            }

            val latestUsernames = mutableMapOf<String, String>()
            val batch = db.batch()
            var hasStaleDocs = false

            for ((friendId, currentUsername) in freshUsernames) {
                latestUsernames[friendId] = currentUsername
                if (currentUsername != (cachedByFriendId[friendId] ?: "")) {
                    batch.set(
                        db.collection("users").document(userId.value)
                            .collection("friends").document(friendId),
                        mapOf("friendUsername" to currentUsername),
                        com.google.firebase.firestore.SetOptions.merge()
                    )
                    hasStaleDocs = true
                }
            }

            if (hasStaleDocs) batch.commit().await()
            latestUsernames
        } catch (e: Exception) {
            AppLogger.e("FriendListHelper", "syncFriendUsernames failed", e)
            emptyMap()
        }
    }
}
