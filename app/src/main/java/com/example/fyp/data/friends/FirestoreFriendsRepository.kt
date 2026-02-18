@file:Suppress("unused")

package com.example.fyp.data.friends

import com.example.fyp.model.Username
import com.example.fyp.model.UserId
import com.example.fyp.model.friends.FriendRelation
import com.example.fyp.model.friends.FriendRequest
import com.example.fyp.model.friends.PublicUserProfile
import com.example.fyp.model.friends.RequestStatus
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirestoreFriendsRepository @Inject constructor(
    private val db: FirebaseFirestore
) : FriendsRepository {

    // ============================================
    // Profile Management
    // ============================================

    override suspend fun createOrUpdatePublicProfile(
        userId: UserId,
        profile: PublicUserProfile
    ): Result<Unit> = try {
        // Update public profile
        db.collection("users")
            .document(userId.value)
            .collection("profile")
            .document("public")
            .set(profile)
            .await()

        // Update search index
        val searchData = mapOf(
            "username_lowercase" to profile.username.lowercase(),
            "displayName" to profile.displayName,
            "isDiscoverable" to profile.isDiscoverable,
            "primaryLanguage" to profile.primaryLanguage,
            "learningLanguages" to profile.learningLanguages,
            "lastActiveAt" to profile.lastActiveAt
        )
        db.collection("user_search")
            .document(userId.value)
            .set(searchData)
            .await()

        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun getPublicProfile(userId: UserId): PublicUserProfile? = try {
        db.collection("users")
            .document(userId.value)
            .collection("profile")
            .document("public")
            .get()
            .await()
            .toObject(PublicUserProfile::class.java)
    } catch (e: Exception) {
        null
    }

    override suspend fun updatePublicProfile(
        userId: UserId,
        updates: Map<String, Any>
    ): Result<Unit> = try {
        // Update public profile
        db.collection("users")
            .document(userId.value)
            .collection("profile")
            .document("public")
            .update(updates)
            .await()

        // Update search index if username or discoverability changed
        val searchUpdates = mutableMapOf<String, Any>()
        updates["username"]?.let { searchUpdates["username_lowercase"] = (it as String).lowercase() }
        updates["displayName"]?.let { searchUpdates["displayName"] = it }
        updates["isDiscoverable"]?.let { searchUpdates["isDiscoverable"] = it }
        updates["lastActiveAt"]?.let { searchUpdates["lastActiveAt"] = it }

        if (searchUpdates.isNotEmpty()) {
            db.collection("user_search")
                .document(userId.value)
                .update(searchUpdates)
                .await()
        }

        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    // ============================================
    // Username Management
    // ============================================

    override suspend fun setUsername(userId: UserId, username: Username): Result<Unit> {
        return try {
            val usernameDoc = db.collection("usernames").document(username.value)
            
            // Check if username is taken by another user
            val existing = usernameDoc.get().await()
            if (existing.exists() && existing.getString("userId") != userId.value) {
                return Result.failure(IllegalArgumentException("Username already taken"))
            }

            // Set username
            usernameDoc.set(mapOf(
                "userId" to userId.value,
                "createdAt" to Timestamp.now()
            )).await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun isUsernameAvailable(username: Username): Boolean = try {
        val doc = db.collection("usernames")
            .document(username.value)
            .get()
            .await()
        !doc.exists()
    } catch (e: Exception) {
        false
    }

    // ============================================
    // Search
    // ============================================

    override suspend fun searchByUsername(query: String, limit: Long): List<PublicUserProfile> = try {
        val lowerQuery = query.lowercase()
        val endQuery = lowerQuery + '\uf8ff'

        val userIds = db.collection("user_search")
            .whereEqualTo("isDiscoverable", true)
            .whereGreaterThanOrEqualTo("username_lowercase", lowerQuery)
            .whereLessThanOrEqualTo("username_lowercase", endQuery)
            .limit(limit)
            .get()
            .await()
            .documents
            .map { it.id }

        // Fetch full profiles
        userIds.mapNotNull { userId ->
            getPublicProfile(UserId(userId))
        }
    } catch (e: Exception) {
        emptyList()
    }

    override suspend fun findByUserId(userId: UserId): PublicUserProfile? {
        val profile = getPublicProfile(userId)
        return if (profile?.isDiscoverable == true) profile else null
    }

    // ============================================
    // Friend Requests
    // ============================================

    override suspend fun sendFriendRequest(
        fromUserId: UserId,
        toUserId: UserId
    ): Result<FriendRequest> {
        return try {
            // Check if already friends
            if (areFriends(fromUserId, toUserId)) {
                return Result.failure(IllegalStateException("Already friends"))
            }

            // Check for existing pending request
            val existingRequest = db.collection("friend_requests")
                .whereEqualTo("fromUserId", fromUserId.value)
                .whereEqualTo("toUserId", toUserId.value)
                .whereEqualTo("status", RequestStatus.PENDING.name)
                .limit(1)
                .get()
                .await()

            if (!existingRequest.isEmpty) {
                return Result.failure(IllegalStateException("Friend request already sent"))
            }

            // Get sender profile
            val fromProfile = getPublicProfile(fromUserId)
                ?: return Result.failure(IllegalStateException("Sender profile not found"))

            // Create request
            val requestRef = db.collection("friend_requests").document()
            val request = FriendRequest(
                requestId = requestRef.id,
                fromUserId = fromUserId.value,
                fromUsername = fromProfile.username,
                fromDisplayName = fromProfile.displayName,
                fromAvatarUrl = fromProfile.avatarUrl,
                toUserId = toUserId.value,
                status = RequestStatus.PENDING,
                createdAt = Timestamp.now(),
                updatedAt = Timestamp.now()
            )

            requestRef.set(request).await()
            Result.success(request)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun acceptFriendRequest(
        requestId: String,
        currentUserId: UserId
    ): Result<Unit> {
        return try {
            val requestRef = db.collection("friend_requests").document(requestId)
            val requestDoc = requestRef.get().await()
            val request = requestDoc.toObject(FriendRequest::class.java)
                ?: return Result.failure(IllegalArgumentException("Request not found"))

            // Verify the current user is the recipient
            if (request.toUserId != currentUserId.value) {
                return Result.failure(IllegalArgumentException("Not authorized"))
            }

            // Get both profiles
            val fromProfile = getPublicProfile(UserId(request.fromUserId))
                ?: return Result.failure(IllegalStateException("Sender profile not found"))
            val toProfile = getPublicProfile(currentUserId)
                ?: return Result.failure(IllegalStateException("Recipient profile not found"))

            val now = Timestamp.now()

            // Use batch write for atomicity
            val batch = db.batch()

            // Update request status
            batch.update(
                requestRef,
                mapOf(
                    "status" to RequestStatus.ACCEPTED.name,
                    "updatedAt" to now
                )
            )

            // Add to fromUser's friends list
            val fromFriendRef = db.collection("users")
                .document(request.fromUserId)
                .collection("friends")
                .document(currentUserId.value)
            batch.set(
                fromFriendRef,
                FriendRelation(
                    friendId = currentUserId.value,
                    friendUsername = toProfile.username,
                    friendDisplayName = toProfile.displayName,
                    friendAvatarUrl = toProfile.avatarUrl,
                    addedAt = now
                )
            )

            // Add to toUser's friends list
            val toFriendRef = db.collection("users")
                .document(currentUserId.value)
                .collection("friends")
                .document(request.fromUserId)
            batch.set(
                toFriendRef,
                FriendRelation(
                    friendId = request.fromUserId,
                    friendUsername = fromProfile.username,
                    friendDisplayName = fromProfile.displayName,
                    friendAvatarUrl = fromProfile.avatarUrl,
                    addedAt = now
                )
            )

            batch.commit().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun rejectFriendRequest(requestId: String): Result<Unit> = try {
        db.collection("friend_requests")
            .document(requestId)
            .update(
                mapOf(
                    "status" to RequestStatus.REJECTED.name,
                    "updatedAt" to Timestamp.now()
                )
            )
            .await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun cancelFriendRequest(requestId: String): Result<Unit> = try {
        db.collection("friend_requests")
            .document(requestId)
            .update(
                mapOf(
                    "status" to RequestStatus.CANCELLED.name,
                    "updatedAt" to Timestamp.now()
                )
            )
            .await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override fun observeIncomingRequests(userId: UserId): Flow<List<FriendRequest>> = callbackFlow {
        val listener = db.collection("friend_requests")
            .whereEqualTo("toUserId", userId.value)
            .whereEqualTo("status", RequestStatus.PENDING.name)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .limit(100) // Limit to 100 incoming requests
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

    override fun observeOutgoingRequests(userId: UserId): Flow<List<FriendRequest>> = callbackFlow {
        val listener = db.collection("friend_requests")
            .whereEqualTo("fromUserId", userId.value)
            .whereEqualTo("status", RequestStatus.PENDING.name)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .limit(100) // Limit to 100 outgoing requests
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

    // ============================================
    // Friends List
    // ============================================

    override fun observeFriends(userId: UserId): Flow<List<FriendRelation>> = callbackFlow {
        val listener = db.collection("users")
            .document(userId.value)
            .collection("friends")
            .orderBy("addedAt", Query.Direction.DESCENDING)
            .limit(100) // Limit to 100 friends (can be expanded with pagination)
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

    override suspend fun removeFriend(userId: UserId, friendId: UserId): Result<Unit> = try {
        val batch = db.batch()

        // Remove from user's friends list
        val userFriendRef = db.collection("users")
            .document(userId.value)
            .collection("friends")
            .document(friendId.value)
        batch.delete(userFriendRef)

        // Remove from friend's friends list
        val friendUserRef = db.collection("users")
            .document(friendId.value)
            .collection("friends")
            .document(userId.value)
        batch.delete(friendUserRef)

        batch.commit().await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun getFriendCount(userId: UserId): Int = try {
        db.collection("users")
            .document(userId.value)
            .collection("friends")
            .get()
            .await()
            .size()
    } catch (e: Exception) {
        0
    }

    override suspend fun areFriends(userId: UserId, otherUserId: UserId): Boolean = try {
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
}
