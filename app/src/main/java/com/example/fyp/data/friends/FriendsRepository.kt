package com.example.fyp.data.friends

import com.example.fyp.model.Username
import com.example.fyp.model.UserId
import com.example.fyp.model.friends.FriendRelation
import com.example.fyp.model.friends.FriendRequest
import com.example.fyp.model.friends.PublicUserProfile
import kotlinx.coroutines.flow.Flow

/**
 * Repository for managing friends, friend requests, and user profiles.
 */
interface FriendsRepository {
    
    // ============================================
    // Profile Management
    // ============================================
    
    /**
     * Create or update the public profile for a user.
     */
    suspend fun createOrUpdatePublicProfile(userId: UserId, profile: PublicUserProfile): Result<Unit>
    
    /**
     * Get a user's public profile.
     */
    suspend fun getPublicProfile(userId: UserId): PublicUserProfile?
    
    /**
     * Update specific fields in the public profile.
     */
    suspend fun updatePublicProfile(userId: UserId, updates: Map<String, Any>): Result<Unit>
    
    // ============================================
    // Username Management
    // ============================================
    
    /**
     * Set or update a username. Ensures uniqueness.
     * @return Result.success if username is set successfully
     * @return Result.failure if username is already taken
     */
    suspend fun setUsername(userId: UserId, username: Username): Result<Unit>
    
    /**
     * Check if a username is available.
     */
    suspend fun isUsernameAvailable(username: Username): Boolean
    
    // ============================================
    // Search
    // ============================================
    
    /**
     * Search users by username prefix (case-insensitive).
     * Returns up to [limit] matching users who are discoverable.
     */
    suspend fun searchByUsername(query: String, limit: Long = 20): List<PublicUserProfile>

    /**
     * Search users by username, returning a Result wrapper.
     */
    suspend fun searchUsersByUsername(query: String, limit: Long = 20): Result<List<PublicUserProfile>>

    /**
     * Find user by exact user ID.
     * Returns null if user not found or not discoverable.
     */
    suspend fun findByUserId(userId: UserId): PublicUserProfile?
    
    // ============================================
    // Friend Requests
    // ============================================
    
    /**
     * Send a friend request to another user.
     */
    suspend fun sendFriendRequest(fromUserId: UserId, toUserId: UserId): Result<FriendRequest>
    
    /**
     * Accept a friend request.
     * Creates friendship for both users.
     */
    suspend fun acceptFriendRequest(requestId: String, currentUserId: UserId, friendUserId: UserId): Result<Unit>

    /**
     * Reject a friend request.
     */
    suspend fun rejectFriendRequest(requestId: String): Result<Unit>
    
    /**
     * Cancel an outgoing friend request.
     */
    suspend fun cancelFriendRequest(requestId: String): Result<Unit>
    
    /**
     * Observe incoming friend requests in real-time.
     */
    fun observeIncomingRequests(userId: UserId): Flow<List<FriendRequest>>
    
    /**
     * Observe outgoing friend requests in real-time.
     */
    fun observeOutgoingRequests(userId: UserId): Flow<List<FriendRequest>>
    
    // ============================================
    // Friends List
    // ============================================
    
    /**
     * Observe friends list in real-time.
     */
    fun observeFriends(userId: UserId): Flow<List<FriendRelation>>
    
    /**
     * Remove a friend from the friends list.
     * Removes friendship for both users.
     */
    suspend fun removeFriend(userId: UserId, friendId: UserId): Result<Unit>
    
    /**
     * Get friend count for a user.
     */
    suspend fun getFriendCount(userId: UserId): Int
    
    /**
     * Check if two users are friends.
     */
    suspend fun areFriends(userId: UserId, otherUserId: UserId): Boolean

    /**
     * Ensure the main user document (/users/{userId}) exists with default
     * unread counter fields so that update() calls from chat operations
     * don't fail with NOT_FOUND errors.
     */
    suspend fun ensureUserDocumentExists(userId: UserId)
}
