@file:Suppress("unused")

package com.translator.TalknLearn.data.friends

import com.translator.TalknLearn.core.AppLogger
import com.translator.TalknLearn.model.Username
import com.translator.TalknLearn.model.UserId
import com.translator.TalknLearn.model.friends.FriendRelation
import com.translator.TalknLearn.model.friends.FriendRequest
import com.translator.TalknLearn.model.friends.PublicUserProfile
import com.translator.TalknLearn.model.friends.RequestStatus
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Facade that delegates to domain-specific helpers for maintainability.
 *
 * Helpers:
 *  - [FirestoreProfileHelper] — profile & username management
 *  - [FirestoreFriendRequestHelper] — request CRUD & observation
 *  - [FirestoreFriendListHelper] — friend-list queries & sync
 *  - [FirestoreBlockHelper] — block / unblock operations
 *
 * Cross-cutting methods (search, sendRequest, acceptRequest, blockUser)
 * remain here because they span multiple helpers.
 */
@Singleton
class FirestoreFriendsRepository @Inject constructor(
    private val db: FirebaseFirestore
) : FriendsRepository {

    private val profileHelper = FirestoreProfileHelper(db)
    private val requestHelper = FirestoreFriendRequestHelper(db)
    private val friendListHelper = FirestoreFriendListHelper(db)
    private val blockHelper = FirestoreBlockHelper(db)

    // ============================================
    // Profile Management  →  FirestoreProfileHelper
    // ============================================

    override suspend fun createOrUpdatePublicProfile(userId: UserId, profile: PublicUserProfile): Result<Unit> =
        profileHelper.createOrUpdatePublicProfile(userId, profile)

    override suspend fun getPublicProfile(userId: UserId): PublicUserProfile? =
        profileHelper.getPublicProfile(userId)

    override suspend fun updatePublicProfile(userId: UserId, updates: Map<String, Any>): Result<Unit> =
        profileHelper.updatePublicProfile(userId, updates)

    override suspend fun setUsername(userId: UserId, username: Username): Result<Unit> =
        profileHelper.setUsername(userId, username)

    override suspend fun isUsernameAvailable(username: Username): Boolean =
        profileHelper.isUsernameAvailable(username)

    override suspend fun ensureUserDocumentExists(userId: UserId) =
        profileHelper.ensureUserDocumentExists(userId)

    // ============================================
    // Search  (cross-cutting: profile + block + friendList)
    // ============================================

    override suspend fun searchByUsername(query: String, limit: Long, callerUserId: UserId?): List<PublicUserProfile> {
        return try {
            val lowerQuery = query.lowercase()
            val endQuery = lowerQuery + '\uf8ff'

            val rawResults = db.collection("user_search")
                .whereEqualTo("isDiscoverable", true)
                .whereGreaterThanOrEqualTo("username_lowercase", lowerQuery)
                .whereLessThanOrEqualTo("username_lowercase", endQuery)
                .limit(limit)
                .get()
                .await()
                .documents
                .mapNotNull { doc ->
                    val username = doc.getString("username") ?: return@mapNotNull null
                    @Suppress("UNCHECKED_CAST")
                    val learningLangs = (doc.get("learningLanguages") as? List<String>) ?: emptyList()
                    PublicUserProfile(
                        uid = doc.id,
                        username = username,
                        isDiscoverable = doc.getBoolean("isDiscoverable") ?: true,
                        primaryLanguage = doc.getString("primaryLanguage") ?: "",
                        learningLanguages = learningLangs,
                        lastActiveAt = doc.getTimestamp("lastActiveAt") ?: Timestamp.now()
                    )
                }

            if (callerUserId == null) {
                rawResults
            } else {
                val blockedByCallerIds = blockHelper.getBlockedUserIds(callerUserId).toSet()
                val currentFriendIds = friendListHelper.getFriendIds(callerUserId).toSet()
                rawResults.filter { profile ->
                    profile.uid !in blockedByCallerIds &&
                        profile.uid !in currentFriendIds &&
                        !blockHelper.isBlockedBy(callerUserId, UserId(profile.uid))
                }
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun searchUsersByUsername(query: String, limit: Long, callerUserId: UserId?): Result<List<PublicUserProfile>> = try {
        Result.success(searchByUsername(query, limit, callerUserId))
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun findByUserId(userId: UserId, callerUserId: UserId?): PublicUserProfile? {
        val profile = profileHelper.getPublicProfile(userId)
        if (profile?.isDiscoverable != true) return null
        if (callerUserId == null) return profile
        if (blockHelper.isBlocked(callerUserId, userId) || blockHelper.isBlockedBy(callerUserId, userId)) return null
        return profile
    }

    // ============================================
    // Friend Requests  →  FirestoreFriendRequestHelper
    // (sendFriendRequest & acceptFriendRequest stay here — cross-cutting)
    // ============================================

    override suspend fun sendFriendRequest(
        fromUserId: UserId,
        toUserId: UserId,
        note: String
    ): Result<FriendRequest> {
        return try {
            if (friendListHelper.areFriends(fromUserId, toUserId)) {
                return Result.failure(IllegalStateException("Already friends"))
            }
            if (blockHelper.isBlocked(fromUserId, toUserId)) {
                return Result.failure(IllegalStateException("You have blocked this user"))
            }
            if (blockHelper.isBlockedBy(fromUserId, toUserId)) {
                return Result.failure(IllegalStateException("Unable to send friend request"))
            }

            // Clean up stale requests in both directions
            try {
                val stale1 = db.collection("friend_requests")
                    .whereEqualTo("fromUserId", fromUserId.value)
                    .whereEqualTo("toUserId", toUserId.value)
                    .get().await()
                val stale2 = db.collection("friend_requests")
                    .whereEqualTo("fromUserId", toUserId.value)
                    .whereEqualTo("toUserId", fromUserId.value)
                    .get().await()
                val staleDocs = stale1.documents + stale2.documents
                if (staleDocs.isNotEmpty()) {
                    staleDocs.chunked(500).forEach { chunk ->
                        val cleanBatch = db.batch()
                        chunk.forEach { doc -> cleanBatch.delete(doc.reference) }
                        cleanBatch.commit().await()
                    }
                }
            } catch (e: Exception) {
                AppLogger.w("FriendsRepository", "Pre-send request cleanup failed (non-fatal): ${e.message}")
            }

            val fromProfile = profileHelper.getPublicProfile(fromUserId)
                ?: return Result.failure(IllegalStateException("Sender profile not found"))
            val toProfile = profileHelper.getPublicProfile(toUserId)

            val requestRef = db.collection("friend_requests").document()
            val expiresAt = Timestamp(Timestamp.now().seconds + 30 * 24 * 3600, 0)
            val request = FriendRequest(
                requestId = requestRef.id,
                fromUserId = fromUserId.value,
                fromUsername = fromProfile.username,
                toUserId = toUserId.value,
                toUsername = toProfile?.username ?: "",
                status = RequestStatus.PENDING,
                createdAt = Timestamp.now(),
                updatedAt = Timestamp.now(),
                note = note.take(80).trim()
                    .replace("&", "&amp;")
                    .replace("<", "&lt;")
                    .replace(">", "&gt;"),
                expiresAt = expiresAt
            )

            requestRef.set(request).await()
            Result.success(request)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun acceptFriendRequest(
        requestId: String,
        currentUserId: UserId,
        friendUserId: UserId
    ): Result<Unit> {
        return try {
            val requestRef = db.collection("friend_requests").document(requestId)

            db.runTransaction { transaction ->
                val requestDoc = transaction.get(requestRef)
                val request = requestDoc.toObject(FriendRequest::class.java)
                    ?: throw IllegalArgumentException("Request not found")

                if (request.toUserId != currentUserId.value) {
                    throw IllegalArgumentException("Not authorized")
                }
                if (request.status != RequestStatus.PENDING) {
                    throw IllegalStateException("This request has already been handled")
                }

                val now = Timestamp.now()

                transaction.update(requestRef, mapOf(
                    "status" to RequestStatus.ACCEPTED.name,
                    "updatedAt" to now
                ))

                val fromFriendRef = db.collection("users")
                    .document(request.fromUserId)
                    .collection("friends")
                    .document(currentUserId.value)
                transaction.set(
                    fromFriendRef,
                    mapOf(
                        "friendId" to currentUserId.value,
                        "friendUsername" to (request.toUsername.ifBlank { currentUserId.value }),
                        "addedAt" to now
                    )
                )

                val toFriendRef = db.collection("users")
                    .document(currentUserId.value)
                    .collection("friends")
                    .document(request.fromUserId)
                transaction.set(
                    toFriendRef,
                    mapOf(
                        "friendId" to request.fromUserId,
                        "friendUsername" to request.fromUsername,
                        "addedAt" to now
                    )
                )
            }.await()

            // Enrich profiles outside transaction (best-effort)
            try {
                val fromProfile = profileHelper.getPublicProfile(friendUserId)
                val toProfile = profileHelper.getPublicProfile(currentUserId)
                if (fromProfile != null) {
                    db.collection("users").document(currentUserId.value)
                        .collection("friends").document(friendUserId.value)
                        .update(mapOf("friendUsername" to fromProfile.username)).await()
                }
                if (toProfile != null) {
                    db.collection("users").document(friendUserId.value)
                        .collection("friends").document(currentUserId.value)
                        .update(mapOf("friendUsername" to toProfile.username)).await()
                }
            } catch (e: Exception) {
                AppLogger.w("FriendsRepository", "Profile enrichment after accept failed (non-fatal): ${e.message}")
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun rejectFriendRequest(requestId: String): Result<Unit> =
        requestHelper.rejectFriendRequest(requestId)

    override suspend fun cancelFriendRequest(requestId: String): Result<Unit> =
        requestHelper.cancelFriendRequest(requestId)

    override fun observeIncomingRequests(userId: UserId): Flow<List<FriendRequest>> =
        requestHelper.observeIncomingRequests(userId)

    override fun observeOutgoingRequests(userId: UserId): Flow<List<FriendRequest>> =
        requestHelper.observeOutgoingRequests(userId)

    // ============================================
    // Friends List  →  FirestoreFriendListHelper
    // ============================================

    override fun observeFriends(userId: UserId): Flow<List<FriendRelation>> =
        friendListHelper.observeFriends(userId)

    override suspend fun removeFriend(userId: UserId, friendId: UserId): Result<Unit> =
        friendListHelper.removeFriend(userId, friendId)

    override suspend fun getFriendCount(userId: UserId): Int =
        friendListHelper.getFriendCount(userId)

    override suspend fun areFriends(userId: UserId, otherUserId: UserId): Boolean =
        friendListHelper.areFriends(userId, otherUserId)

    override suspend fun propagateUsernameChange(userId: UserId, newUsername: String): Result<Unit> =
        friendListHelper.propagateUsernameChange(userId, newUsername)

    override suspend fun syncFriendUsernames(userId: UserId): Map<String, String> =
        friendListHelper.syncFriendUsernames(userId)

    // ============================================
    // Block / Unblock  →  FirestoreBlockHelper
    // (blockUser stays here — calls removeFriend)
    // ============================================

    override suspend fun blockUser(userId: UserId, blockedUserId: UserId, blockedUsername: String): Result<Unit> = try {
        // Auto-remove friendship when blocking
        val removeResult = friendListHelper.removeFriend(userId, blockedUserId)
        if (removeResult.isFailure) {
            AppLogger.w(
                "FriendsRepository",
                "blockUser: removeFriend failed before block write: ${removeResult.exceptionOrNull()?.message}"
            )
        }

        db.collection("users")
            .document(userId.value)
            .collection("blocked_users")
            .document(blockedUserId.value)
            .set(mapOf(
                "blockedUserId" to blockedUserId.value,
                "blockedUsername" to blockedUsername,
                "blockedAt" to Timestamp.now()
            ))
            .await()

        // Delete pending friend requests between the two users
        try {
            val snap1 = db.collection("friend_requests")
                .whereEqualTo("fromUserId", userId.value)
                .whereEqualTo("toUserId", blockedUserId.value)
                .get().await()
            val snap2 = db.collection("friend_requests")
                .whereEqualTo("fromUserId", blockedUserId.value)
                .whereEqualTo("toUserId", userId.value)
                .get().await()
            val allDocs = snap1.documents + snap2.documents
            if (allDocs.isNotEmpty()) {
                allDocs.chunked(500).forEach { chunk ->
                    val cleanupBatch = db.batch()
                    chunk.forEach { doc -> cleanupBatch.delete(doc.reference) }
                    cleanupBatch.commit().await()
                }
            }
        } catch (e: Exception) {
            AppLogger.w("FriendsRepository", "blockUser: friend_request cleanup failed (non-fatal): ${e.message}")
        }

        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun unblockUser(userId: UserId, blockedUserId: UserId): Result<Unit> =
        blockHelper.unblockUser(userId, blockedUserId)

    override suspend fun isBlocked(userId: UserId, otherUserId: UserId): Boolean =
        blockHelper.isBlocked(userId, otherUserId)

    override suspend fun isBlockedBy(userId: UserId, otherUserId: UserId): Boolean =
        blockHelper.isBlockedBy(userId, otherUserId)

    override suspend fun getBlockedUserIds(userId: UserId): List<String> =
        blockHelper.getBlockedUserIds(userId)

    override suspend fun getBlockedUsers(userId: UserId): List<BlockedUser> =
        blockHelper.getBlockedUsers(userId)
}
