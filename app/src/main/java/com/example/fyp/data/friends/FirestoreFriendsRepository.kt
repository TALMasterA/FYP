@file:Suppress("unused")

package com.example.fyp.data.friends

import com.example.fyp.core.AppLogger
import com.example.fyp.core.NetworkRetry
import com.example.fyp.model.Username
import com.example.fyp.model.UserId
import com.example.fyp.model.friends.FriendRelation
import com.example.fyp.model.friends.FriendRequest
import com.example.fyp.model.friends.PublicUserProfile
import com.example.fyp.model.friends.RequestStatus
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.coroutineScope
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

        // Update search index — store full profile fields to avoid N reads in searchByUsername
        val searchData = mapOf(
            "username_lowercase" to profile.username.lowercase(),
            "username" to profile.username,
            "displayName" to profile.displayName,
            "avatarUrl" to profile.avatarUrl,
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
    } catch (e: kotlinx.coroutines.CancellationException) {
        // Re-throw cancellation to properly propagate coroutine cancellation
        throw e
    } catch (e: Exception) {
        // Catch other exceptions (network, Firestore errors, etc.) and return null
        android.util.Log.e("FirestoreFriendsRepository", "Failed to get public profile for ${userId.value}: ${e.message}")
        null
    }

    override suspend fun updatePublicProfile(
        userId: UserId,
        updates: Map<String, Any>
    ): Result<Unit> = try {
        // Use set with merge so it works even if the document doesn't exist yet
        db.collection("users")
            .document(userId.value)
            .collection("profile")
            .document("public")
            .set(updates, com.google.firebase.firestore.SetOptions.merge())
            .await()

        // Update search index if username or discoverability changed
        val searchUpdates = mutableMapOf<String, Any>()
        updates["username"]?.let {
            searchUpdates["username_lowercase"] = (it as String).lowercase()
            searchUpdates["username"] = it
        }
        updates["displayName"]?.let { searchUpdates["displayName"] = it }
        updates["avatarUrl"]?.let { searchUpdates["avatarUrl"] = it }
        updates["isDiscoverable"]?.let { searchUpdates["isDiscoverable"] = it }
        updates["lastActiveAt"]?.let { searchUpdates["lastActiveAt"] = it }

        if (searchUpdates.isNotEmpty()) {
            db.collection("user_search")
                .document(userId.value)
                .set(searchUpdates, com.google.firebase.firestore.SetOptions.merge())
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

            // Look up the user's current username from their profile to release it
            val currentProfile = getPublicProfile(userId)
            val oldUsername = currentProfile?.username?.takeIf { it.isNotBlank() && it != username.value }

            // Set new username
            usernameDoc.set(mapOf(
                "userId" to userId.value,
                "createdAt" to Timestamp.now()
            )).await()

            // Release old username so others can use it
            if (oldUsername != null) {
                try {
                    db.collection("usernames").document(oldUsername).delete().await()
                } catch (_: Exception) {
                    // Non-critical: old name cleanup failed, but new name is set
                }
            }

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

    /**
     * OPTIMIZED: Profile fields are now stored in user_search documents (see
     * createOrUpdatePublicProfile), so we build PublicUserProfile directly from
     * the search query results — no additional per-user reads needed.
     * Previous cost: 1 query + N reads. New cost: 1 query.
     */
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
                        displayName = doc.getString("displayName") ?: "",
                        avatarUrl = doc.getString("avatarUrl") ?: "",
                        isDiscoverable = doc.getBoolean("isDiscoverable") ?: true,
                        primaryLanguage = doc.getString("primaryLanguage") ?: "",
                        learningLanguages = learningLangs,
                        lastActiveAt = doc.getTimestamp("lastActiveAt") ?: Timestamp.now()
                    )
                }

            if (callerUserId == null) {
                rawResults
            } else {
                // Filter out users who have blocked the caller OR who the caller has blocked
                val blockedByCallerIds = getBlockedUserIds(callerUserId).toSet()
                rawResults.filter { profile ->
                    profile.uid !in blockedByCallerIds &&
                        !isBlockedBy(callerUserId, UserId(profile.uid))
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
        val profile = getPublicProfile(userId)
        if (profile?.isDiscoverable != true) return null
        if (callerUserId == null) return profile
        // Hide from search if either side has blocked the other
        if (isBlocked(callerUserId, userId) || isBlockedBy(callerUserId, userId)) return null
        return profile
    }

    // ============================================
    // Friend Requests
    // ============================================

    override suspend fun sendFriendRequest(
        fromUserId: UserId,
        toUserId: UserId,
        note: String
    ): Result<FriendRequest> {
        return try {
            // Check if already friends
            if (areFriends(fromUserId, toUserId)) {
                return Result.failure(IllegalStateException("Already friends"))
            }

            // Check block status in both directions
            if (isBlocked(fromUserId, toUserId)) {
                return Result.failure(IllegalStateException("You have blocked this user"))
            }
            if (isBlockedBy(fromUserId, toUserId)) {
                return Result.failure(IllegalStateException("Unable to send friend request"))
            }

            // Clean up any stale request documents in both directions before proceeding.
            // This ensures users can re-add each other after unfriending, even if the
            // removeFriend cleanup was partial (best-effort) or failed silently.
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

            // Get sender profile
            val fromProfile = getPublicProfile(fromUserId)
                ?: return Result.failure(IllegalStateException("Sender profile not found"))

            // Get recipient profile for caching username (best-effort)
            val toProfile = getPublicProfile(toUserId)

            // Create request
            val requestRef = db.collection("friend_requests").document()
            val request = FriendRequest(
                requestId = requestRef.id,
                fromUserId = fromUserId.value,
                fromUsername = fromProfile.username,
                fromDisplayName = fromProfile.displayName,
                fromAvatarUrl = fromProfile.avatarUrl,
                toUserId = toUserId.value,
                toUsername = toProfile?.username ?: "",
                toDisplayName = toProfile?.displayName ?: "",
                status = RequestStatus.PENDING,
                createdAt = Timestamp.now(),
                updatedAt = Timestamp.now(),
                note = note.take(80).trim()
                    .replace("&", "&amp;")
                    .replace("<", "&lt;")
                    .replace(">", "&gt;")
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

            // Use batch write for atomicity:
            // DELETE the request doc (so re-adding after unfriend is always clean)
            // and add both users to each other's friends list.
            val batch = db.batch()

            // Delete the request document instead of updating to ACCEPTED.
            // This ensures there is never a stale ACCEPTED doc that complicates re-adding.
            batch.delete(requestRef)

            // Add to fromUser's friends list
            val fromFriendRef = db.collection("users")
                .document(request.fromUserId)
                .collection("friends")
                .document(currentUserId.value)
            batch.set(
                fromFriendRef,
                mapOf(
                    "friendId" to currentUserId.value,
                    "friendUsername" to toProfile.username,
                    "friendDisplayName" to toProfile.displayName,
                    "friendAvatarUrl" to toProfile.avatarUrl,
                    "addedAt" to now
                )
            )

            // Add to toUser's friends list
            val toFriendRef = db.collection("users")
                .document(currentUserId.value)
                .collection("friends")
                .document(request.fromUserId)
            batch.set(
                toFriendRef,
                mapOf(
                    "friendId" to request.fromUserId,
                    "friendUsername" to fromProfile.username,
                    "friendDisplayName" to fromProfile.displayName,
                    "friendAvatarUrl" to fromProfile.avatarUrl,
                    "addedAt" to now
                )
            )

            batch.commit().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun rejectFriendRequest(requestId: String): Result<Unit> = try {
        // Delete the request doc instead of updating to REJECTED.
        // This keeps the collection clean and allows the sender to re-send a request later.
        NetworkRetry.withRetry(
            maxAttempts = 3,
            shouldRetry = NetworkRetry::isRetryableFirebaseException
        ) {
            db.collection("friend_requests")
                .document(requestId)
                .delete()
                .await()
        }
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun cancelFriendRequest(requestId: String): Result<Unit> = try {
        // Delete instead of updating to CANCELLED for a clean collection state.
        db.collection("friend_requests")
            .document(requestId)
            .delete()
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

        // Clean up ALL friend_request documents in both directions (any status).
        // This ensures users can re-add each other after unfriending.
        // Failure is non-fatal since the friendship is already removed.
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
            // Non-fatal: cleanup best-effort.
            AppLogger.w("FriendsRepository", "friend_request cleanup failed (non-fatal): ${e.message}")
        }

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

    /**
     * Propagate a username change to:
     * 1. All friends' cached FriendRelation documents (friendUsername field).
     * 2. Any pending outgoing friend_requests (fromUsername field), so the
     *    recipient always sees the current name on the request card.
     *
     * Firestore rules updated to allow the sender to update `fromUsername`
     * on their own PENDING requests.
     */
    override suspend fun propagateUsernameChange(
        userId: UserId,
        newUsername: String
    ): Result<Unit> {
        return try {
            // ── 1. Update friendUsername in every friend's friend-list doc ──
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
                        // set-merge: safe even if the friend removed the user concurrently
                        batch.set(ref, mapOf("friendUsername" to newUsername),
                            com.google.firebase.firestore.SetOptions.merge())
                    }
                    batch.commit().await()
                }
            }

            // ── 2. Update fromUsername in pending outgoing friend requests ──
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
            AppLogger.e("FriendsRepository", "propagateUsernameChange failed", e)
            Result.failure(e)
        }
    }

    override suspend fun syncFriendUsernames(userId: UserId): Map<String, String> {
        // Skip if recently synced (freshness check to reduce reads)
        val now = System.currentTimeMillis()
        if (now - lastUsernameSyncTime < USERNAME_SYNC_FRESHNESS_MS) return emptyMap()
        lastUsernameSyncTime = now

        return try {
            // Read own friends list
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

            // Fetch all friends' public profiles in parallel (1 read per friend, all concurrent)
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
            AppLogger.e("FriendsRepository", "syncFriendUsernames failed", e)
            emptyMap()
        }
    }

    /**
     * Ensure the main user document (/users/{userId}) exists so that
     * subsequent update() calls from chat operations don't fail with NOT_FOUND.
     *
     * Uses set-merge with an empty map: creates the document if it doesn't
     * exist, but does NOT overwrite any existing fields (including unread
     * counters that may already have data).
     */
    override suspend fun ensureUserDocumentExists(userId: UserId) {
        try {
            db.collection("users").document(userId.value)
                .set(emptyMap<String, Any>(), com.google.firebase.firestore.SetOptions.merge())
                .await()
        } catch (e: Exception) {
            android.util.Log.e("FriendsRepository", "Failed to ensure user doc exists", e)
        }
    }

    // ============================================
    // Block / Unblock
    // ============================================

    override suspend fun blockUser(userId: UserId, blockedUserId: UserId, blockedUsername: String): Result<Unit> = try {
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

        // Delete all pending friend requests between the two users in both directions.
        // This ensures the blocked user does not see a stale red-dot / pending request
        // card on their Friends screen after being blocked.
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
            // Non-fatal: friend-request cleanup is best-effort.
            AppLogger.w("FriendsRepository", "blockUser: friend_request cleanup failed (non-fatal): ${e.message}")
        }

        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun unblockUser(userId: UserId, blockedUserId: UserId): Result<Unit> = try {
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

    override suspend fun isBlocked(userId: UserId, otherUserId: UserId): Boolean = try {
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

    override suspend fun isBlockedBy(userId: UserId, otherUserId: UserId): Boolean = try {
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

    override suspend fun getBlockedUserIds(userId: UserId): List<String> = try {
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

    override suspend fun getBlockedUsers(userId: UserId): List<BlockedUser> = try {
        db.collection("users")
            .document(userId.value)
            .collection("blocked_users")
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

    companion object {
        /** Maximum number of friends to sync usernames for in a single batch. */
        private const val MAX_FRIENDS_PER_SYNC = 100L
        /** Only sync usernames if last sync was more than 1 hour ago. */
        private const val USERNAME_SYNC_FRESHNESS_MS = 3_600_000L // 1 hour
    }

    @Volatile private var lastUsernameSyncTime = 0L
}
