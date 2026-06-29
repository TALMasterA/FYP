package com.translator.TalknLearn.data.friends

import com.translator.TalknLearn.core.NetworkRetry
import com.translator.TalknLearn.model.UserId
import com.translator.TalknLearn.model.friends.FriendRequest
import com.translator.TalknLearn.model.friends.RequestStatus
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

/**
 * Handles friend-request CRUD and real-time observation.
 * Extracted from [FirestoreFriendsRepository] for maintainability.
 */
internal class FirestoreFriendRequestHelper(private val db: FirebaseFirestore) {

    suspend fun rejectFriendRequest(requestId: String): Result<Unit> = try {
        NetworkRetry.withRetry(
            maxAttempts = 3,
            shouldRetry = NetworkRetry::isRetryableFirebaseException
        ) {
            db.collection("friend_requests")
                .document(requestId)
                .update(mapOf(
                    "status" to RequestStatus.REJECTED.name,
                    "updatedAt" to Timestamp.now()
                ))
                .await()
        }
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun cancelFriendRequest(requestId: String): Result<Unit> = try {
        db.collection("friend_requests")
            .document(requestId)
            .delete()
            .await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    fun observeIncomingRequests(userId: UserId): Flow<List<FriendRequest>> = callbackFlow {
        val listener = db.collection("friend_requests")
            .whereEqualTo("toUserId", userId.value)
            .whereEqualTo("status", RequestStatus.PENDING.name)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .limit(100)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val now = Timestamp.now()
                val requests = (snapshot?.toObjects(FriendRequest::class.java) ?: emptyList())
                    .filter { request ->
                        val expiresAt = request.expiresAt
                        expiresAt == null || expiresAt.seconds > now.seconds
                    }
                trySend(requests)
            }
        awaitClose { listener.remove() }
    }

    fun observeOutgoingRequests(userId: UserId): Flow<List<FriendRequest>> = callbackFlow {
        val listener = db.collection("friend_requests")
            .whereEqualTo("fromUserId", userId.value)
            .whereEqualTo("status", RequestStatus.PENDING.name)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .limit(100)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val requests = snapshot?.toObjects(FriendRequest::class.java) ?: emptyList()
                trySend(requests)
            }
        awaitClose { listener.remove() }
    }
}
