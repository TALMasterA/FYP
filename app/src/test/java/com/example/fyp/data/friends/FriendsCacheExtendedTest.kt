package com.example.fyp.data.friends

import com.example.fyp.model.friends.FriendRelation
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Extended unit tests for FriendsCache.
 *
 * Tests:
 * 1. Friends list cache returns null when empty
 * 2. Friends list cache returns data when fresh
 * 3. Friendship status cache uses order-independent key
 * 4. Blocked users cache basic operations
 * 5. invalidateAll clears all caches
 * 6. Cache returns null after invalidation
 */
class FriendsCacheExtendedTest {

    private lateinit var cache: FriendsCache

    @Before
    fun setup() {
        cache = FriendsCache()
    }

    // ── Friends list cache ──────────────────────────────────────

    @Test
    fun `getCachedFriends returns null initially`() {
        assertNull(cache.getCachedFriends())
    }

    @Test
    fun `getCachedFriends returns data after update`() {
        val friends = listOf(
            FriendRelation(friendId = "friend1", friendUsername = "Alice"),
            FriendRelation(friendId = "friend2", friendUsername = "Bob")
        )
        cache.updateFriendsCache(friends)

        val cached = cache.getCachedFriends()
        assertNotNull(cached)
        assertEquals(2, cached!!.size)
        assertEquals("friend1", cached[0].friendId)
    }

    @Test
    fun `invalidateFriends clears friends list`() {
        cache.updateFriendsCache(listOf(FriendRelation(friendId = "f1")))
        cache.invalidateFriends()

        assertNull(cache.getCachedFriends())
    }

    // ── Friendship status cache ─────────────────────────────────

    @Test
    fun `getCachedFriendshipStatus returns null initially`() {
        assertNull(cache.getCachedFriendshipStatus("user1", "user2"))
    }

    @Test
    fun `friendship status is order-independent`() {
        cache.updateFriendshipStatus("user1", "user2", true)

        // Both orders should return the same cached value
        assertEquals(true, cache.getCachedFriendshipStatus("user1", "user2"))
        assertEquals(true, cache.getCachedFriendshipStatus("user2", "user1"))
    }

    @Test
    fun `invalidateFriendshipStatus clears specific pair`() {
        cache.updateFriendshipStatus("user1", "user2", true)
        cache.invalidateFriendshipStatus("user1", "user2")

        assertNull(cache.getCachedFriendshipStatus("user1", "user2"))
    }

    @Test
    fun `invalidateFriendshipStatus is order-independent`() {
        cache.updateFriendshipStatus("user1", "user2", true)
        cache.invalidateFriendshipStatus("user2", "user1")

        assertNull(cache.getCachedFriendshipStatus("user1", "user2"))
    }

    // ── Blocked users cache ─────────────────────────────────────

    @Test
    fun `getCachedBlockedUserIds returns null initially`() {
        assertNull(cache.getCachedBlockedUserIds())
    }

    @Test
    fun `blocked users cache returns data after update`() {
        val blocked = setOf("blocked1", "blocked2")
        cache.updateBlockedCache(blocked)

        val cached = cache.getCachedBlockedUserIds()
        assertNotNull(cached)
        assertEquals(2, cached!!.size)
        assertTrue(cached.contains("blocked1"))
    }

    @Test
    fun `invalidateBlocked clears blocked cache`() {
        cache.updateBlockedCache(setOf("b1"))
        cache.invalidateBlocked()

        assertNull(cache.getCachedBlockedUserIds())
    }

    // ── Global invalidation ─────────────────────────────────────

    @Test
    fun `invalidateAll clears all caches`() {
        cache.updateFriendsCache(listOf(FriendRelation(friendId = "f1")))
        cache.updateFriendshipStatus("user1", "user2", true)
        cache.updateBlockedCache(setOf("b1"))

        cache.invalidateAll()

        assertNull(cache.getCachedFriends())
        assertNull(cache.getCachedFriendshipStatus("user1", "user2"))
        assertNull(cache.getCachedBlockedUserIds())
    }

    @Test
    fun `updating friends cache with empty list returns empty list`() {
        cache.updateFriendsCache(emptyList())

        val cached = cache.getCachedFriends()
        assertNotNull(cached)
        assertEquals(0, cached!!.size)
    }

    @Test
    fun `updating blocked cache with empty set returns empty set`() {
        cache.updateBlockedCache(emptySet())

        val cached = cache.getCachedBlockedUserIds()
        assertNotNull(cached)
        assertTrue(cached!!.isEmpty())
    }
}
