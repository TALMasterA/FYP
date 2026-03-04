package com.example.fyp.data.friends

import com.example.fyp.core.AppLogger
import com.example.fyp.model.friends.FriendRelation
import com.example.fyp.model.friends.FriendRequest
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

/**
 * FIX 6.1: In-memory cache for friend-related data.
 *
 * Reduces Firestore reads by caching friends list, blocked users, and
 * friendship status checks. Cache is invalidated when data changes
 * via real-time listeners.
 *
 * Expected impact:
 * - ~70% reduction in Firestore reads for repeated checks
 * - Faster screen loads (cached: <10ms vs Firestore: 300-500ms)
 * - Better offline experience during transient disconnects
 */
@Singleton
class FriendsCache @Inject constructor() {

    // ── Friends list cache ──────────────────────────────────────────────────

    @Volatile
    private var cachedFriends: List<FriendRelation>? = null

    @Volatile
    private var friendsCacheTime: Long = 0L

    /** Cache freshness: 5 minutes for friends list */
    private val friendsCacheTtlMs = 5 * 60 * 1000L

    fun getCachedFriends(): List<FriendRelation>? {
        val cached = cachedFriends ?: return null
        return if (System.currentTimeMillis() - friendsCacheTime < friendsCacheTtlMs) cached
        else null
    }

    fun updateFriendsCache(friends: List<FriendRelation>) {
        cachedFriends = friends
        friendsCacheTime = System.currentTimeMillis()
    }

    fun invalidateFriends() {
        cachedFriends = null
        friendsCacheTime = 0L
    }

    // ── Friendship status cache ─────────────────────────────────────────────

    private data class FriendshipKey(val userId1: String, val userId2: String) {
        companion object {
            fun of(a: String, b: String): FriendshipKey {
                val sorted = listOf(a, b).sorted()
                return FriendshipKey(sorted[0], sorted[1])
            }
        }
    }

    private val friendshipStatusCache = ConcurrentHashMap<FriendshipKey, Pair<Boolean, Long>>()
    private val friendshipCacheTtlMs = 2 * 60 * 1000L // 2 minutes

    fun getCachedFriendshipStatus(userId1: String, userId2: String): Boolean? {
        val key = FriendshipKey.of(userId1, userId2)
        val (status, time) = friendshipStatusCache[key] ?: return null
        return if (System.currentTimeMillis() - time < friendshipCacheTtlMs) status else null
    }

    fun updateFriendshipStatus(userId1: String, userId2: String, areFriends: Boolean) {
        val key = FriendshipKey.of(userId1, userId2)
        friendshipStatusCache[key] = areFriends to System.currentTimeMillis()
    }

    fun invalidateFriendshipStatus(userId1: String, userId2: String) {
        friendshipStatusCache.remove(FriendshipKey.of(userId1, userId2))
    }

    // ── Blocked users cache ─────────────────────────────────────────────────

    @Volatile
    private var cachedBlockedUserIds: Set<String>? = null

    @Volatile
    private var blockedCacheTime: Long = 0L

    private val blockedCacheTtlMs = 5 * 60 * 1000L

    fun getCachedBlockedUserIds(): Set<String>? {
        val cached = cachedBlockedUserIds ?: return null
        return if (System.currentTimeMillis() - blockedCacheTime < blockedCacheTtlMs) cached
        else null
    }

    fun updateBlockedCache(blockedIds: Set<String>) {
        cachedBlockedUserIds = blockedIds
        blockedCacheTime = System.currentTimeMillis()
    }

    fun invalidateBlocked() {
        cachedBlockedUserIds = null
        blockedCacheTime = 0L
    }

    // ── Global invalidation ─────────────────────────────────────────────────

    /** Invalidate all caches. Called on sign-out or major state change. */
    fun invalidateAll() {
        invalidateFriends()
        friendshipStatusCache.clear()
        invalidateBlocked()
        AppLogger.d("FriendsCache", "All caches invalidated")
    }
}
