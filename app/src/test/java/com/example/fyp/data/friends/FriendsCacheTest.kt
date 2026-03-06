package com.example.fyp.data.friends

import com.example.fyp.model.friends.FriendRelation
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class FriendsCacheTest {

    private lateinit var cache: FriendsCache

    @Before
    fun setUp() {
        cache = FriendsCache()
    }

    // ── Friends list cache ──────────────────────────────────────────

    @Test
    fun `getCachedFriends - returns null when empty`() {
        assertNull(cache.getCachedFriends())
    }

    @Test
    fun `getCachedFriends - returns cached list after update`() {
        val friends = listOf(
            FriendRelation(friendId = "user1", friendUsername = "Alice"),
            FriendRelation(friendId = "user2", friendUsername = "Bob")
        )
        cache.updateFriendsCache(friends)

        val result = cache.getCachedFriends()
        assertNotNull(result)
        assertEquals(2, result!!.size)
        assertEquals("user1", result[0].friendId)
    }

    @Test
    fun `invalidateFriends - clears friends cache`() {
        cache.updateFriendsCache(listOf(FriendRelation(friendId = "user1")))
        assertNotNull(cache.getCachedFriends())

        cache.invalidateFriends()
        assertNull(cache.getCachedFriends())
    }

    @Test
    fun `updateFriendsCache - overwrites previous cache`() {
        cache.updateFriendsCache(listOf(FriendRelation(friendId = "old")))
        cache.updateFriendsCache(listOf(FriendRelation(friendId = "new")))

        val result = cache.getCachedFriends()
        assertNotNull(result)
        assertEquals(1, result!!.size)
        assertEquals("new", result[0].friendId)
    }

    // ── Friendship status cache ─────────────────────────────────────

    @Test
    fun `getCachedFriendshipStatus - returns null when not cached`() {
        assertNull(cache.getCachedFriendshipStatus("user1", "user2"))
    }

    @Test
    fun `getCachedFriendshipStatus - returns cached status`() {
        cache.updateFriendshipStatus("user1", "user2", true)
        assertEquals(true, cache.getCachedFriendshipStatus("user1", "user2"))
    }

    @Test
    fun `getCachedFriendshipStatus - symmetric key means order does not matter`() {
        cache.updateFriendshipStatus("user1", "user2", true)
        // Querying in reverse order should return the same result
        assertEquals(true, cache.getCachedFriendshipStatus("user2", "user1"))
    }

    @Test
    fun `getCachedFriendshipStatus - can store false status`() {
        cache.updateFriendshipStatus("user1", "user2", false)
        assertEquals(false, cache.getCachedFriendshipStatus("user1", "user2"))
    }

    @Test
    fun `updateFriendshipStatus - overwrites previous status`() {
        cache.updateFriendshipStatus("user1", "user2", true)
        cache.updateFriendshipStatus("user1", "user2", false)
        assertEquals(false, cache.getCachedFriendshipStatus("user1", "user2"))
    }

    @Test
    fun `invalidateFriendshipStatus - removes specific pair`() {
        cache.updateFriendshipStatus("user1", "user2", true)
        cache.updateFriendshipStatus("user3", "user4", true)

        cache.invalidateFriendshipStatus("user1", "user2")

        assertNull(cache.getCachedFriendshipStatus("user1", "user2"))
        // Other pair should still be cached
        assertEquals(true, cache.getCachedFriendshipStatus("user3", "user4"))
    }

    @Test
    fun `invalidateFriendshipStatus - reverse order also invalidates`() {
        cache.updateFriendshipStatus("user1", "user2", true)
        cache.invalidateFriendshipStatus("user2", "user1")
        assertNull(cache.getCachedFriendshipStatus("user1", "user2"))
    }

    // ── Blocked users cache ─────────────────────────────────────────

    @Test
    fun `getCachedBlockedUserIds - returns null when empty`() {
        assertNull(cache.getCachedBlockedUserIds())
    }

    @Test
    fun `getCachedBlockedUserIds - returns cached set after update`() {
        cache.updateBlockedCache(setOf("blocked1", "blocked2"))
        val result = cache.getCachedBlockedUserIds()
        assertNotNull(result)
        assertEquals(2, result!!.size)
        assertTrue(result.contains("blocked1"))
        assertTrue(result.contains("blocked2"))
    }

    @Test
    fun `invalidateBlocked - clears blocked cache`() {
        cache.updateBlockedCache(setOf("blocked1"))
        assertNotNull(cache.getCachedBlockedUserIds())

        cache.invalidateBlocked()
        assertNull(cache.getCachedBlockedUserIds())
    }

    @Test
    fun `updateBlockedCache - overwrites previous cache`() {
        cache.updateBlockedCache(setOf("old"))
        cache.updateBlockedCache(setOf("new1", "new2"))

        val result = cache.getCachedBlockedUserIds()
        assertNotNull(result)
        assertEquals(2, result!!.size)
        assertTrue(result.contains("new1"))
        assertFalse(result.contains("old"))
    }

    // ── Global invalidation ─────────────────────────────────────────

    @Test
    fun `invalidateAll - clears all caches`() {
        cache.updateFriendsCache(listOf(FriendRelation(friendId = "friend1")))
        cache.updateFriendshipStatus("user1", "user2", true)
        cache.updateBlockedCache(setOf("blocked1"))

        cache.invalidateAll()

        assertNull(cache.getCachedFriends())
        assertNull(cache.getCachedFriendshipStatus("user1", "user2"))
        assertNull(cache.getCachedBlockedUserIds())
    }

    // ── Multiple friendship pairs ───────────────────────────────────

    @Test
    fun `multiple friendship pairs are independent`() {
        cache.updateFriendshipStatus("a", "b", true)
        cache.updateFriendshipStatus("c", "d", false)
        cache.updateFriendshipStatus("a", "c", true)

        assertEquals(true, cache.getCachedFriendshipStatus("a", "b"))
        assertEquals(false, cache.getCachedFriendshipStatus("c", "d"))
        assertEquals(true, cache.getCachedFriendshipStatus("a", "c"))
    }

    // ── Edge cases ──────────────────────────────────────────────────

    @Test
    fun `empty friends list can be cached`() {
        cache.updateFriendsCache(emptyList())
        val result = cache.getCachedFriends()
        assertNotNull(result)
        assertTrue(result!!.isEmpty())
    }

    @Test
    fun `empty blocked set can be cached`() {
        cache.updateBlockedCache(emptySet())
        val result = cache.getCachedBlockedUserIds()
        assertNotNull(result)
        assertTrue(result!!.isEmpty())
    }
}
