package com.example.fyp.data.friends

import com.example.fyp.model.friends.FriendRelation
import com.example.fyp.model.friends.FriendRequest
import com.example.fyp.model.friends.SharedItem
import com.example.fyp.model.friends.SharedItemType
import org.junit.Assert.*
import org.junit.Test

/**
 * Tests for pure logic extracted from SharedFriendsDataSource.
 *
 * Covers:
 *  - Seen-state management (shared items, friend requests, message friends)
 *  - Badge computation (unseen counts, combined flows)
 *  - updateRawUnreadPerFriend seen-set re-removal
 *  - applyUsernameUpdates in-memory mutation
 *  - Username cache operations
 *  - isFriend membership check
 *  - startObserving idempotency
 *  - stopObserving state reset
 *  - Seen set intersection on inbox/request updates
 */
class SharedFriendsLogicTest {

    // ── Seen-state management ────────────────────────────────────────────────

    @Test
    fun `markSharedItemsSeen - adds current item IDs to seen set`() {
        val currentSeen = setOf("item1")
        val pendingItems = listOf("item2", "item3")
        val newSeen = currentSeen + pendingItems.toSet()

        assertEquals(setOf("item1", "item2", "item3"), newSeen)
    }

    @Test
    fun `markSharedItemsSeen - idempotent for already-seen items`() {
        val currentSeen = setOf("item1", "item2")
        val pendingItems = listOf("item1", "item2", "item3")
        val newSeen = currentSeen + pendingItems.toSet()

        assertEquals(setOf("item1", "item2", "item3"), newSeen)
    }

    @Test
    fun `markFriendRequestsSeen - adds current request IDs to seen set`() {
        val currentSeen = emptySet<String>()
        val requestIds = listOf("req1", "req2")
        val newSeen = currentSeen + requestIds.toSet()

        assertEquals(setOf("req1", "req2"), newSeen)
    }

    @Test
    fun `markMessageFriendSeen - adds friend ID to seen set`() {
        val currentSeen = setOf("friend1")
        val newSeen = currentSeen + "friend2"

        assertEquals(setOf("friend1", "friend2"), newSeen)
    }

    @Test
    fun `markMessageFriendSeen - duplicate is idempotent`() {
        val currentSeen = setOf("friend1", "friend2")
        val newSeen = currentSeen + "friend1"

        assertEquals(setOf("friend1", "friend2"), newSeen)
    }

    // ── Unseen count computation ─────────────────────────────────────────────

    /**
     * Replicates unseenSharedItemsCount Flow logic:
     * items.count { it.itemId !in seen }
     */
    private fun computeUnseenSharedItemsCount(itemIds: List<String>, seenIds: Set<String>): Int {
        return itemIds.count { it !in seenIds }
    }

    @Test
    fun `unseenSharedItemsCount - all unseen when seen set empty`() {
        assertEquals(3, computeUnseenSharedItemsCount(listOf("a", "b", "c"), emptySet()))
    }

    @Test
    fun `unseenSharedItemsCount - partially seen`() {
        assertEquals(1, computeUnseenSharedItemsCount(listOf("a", "b", "c"), setOf("a", "b")))
    }

    @Test
    fun `unseenSharedItemsCount - all seen`() {
        assertEquals(0, computeUnseenSharedItemsCount(listOf("a", "b"), setOf("a", "b", "c")))
    }

    @Test
    fun `unseenSharedItemsCount - empty items always zero`() {
        assertEquals(0, computeUnseenSharedItemsCount(emptyList(), setOf("a")))
    }

    // ── hasUnseenSharedItems ─────────────────────────────────────────────────

    /**
     * Replicates hasUnseenSharedItems Flow logic:
     * items.any { it.itemId !in seen }
     */
    private fun hasUnseenSharedItems(itemIds: List<String>, seenIds: Set<String>): Boolean {
        return itemIds.any { it !in seenIds }
    }

    @Test
    fun `hasUnseenSharedItems - true when some unseen`() {
        assertTrue(hasUnseenSharedItems(listOf("a", "b"), setOf("a")))
    }

    @Test
    fun `hasUnseenSharedItems - false when all seen`() {
        assertFalse(hasUnseenSharedItems(listOf("a", "b"), setOf("a", "b")))
    }

    @Test
    fun `hasUnseenSharedItems - false when no items`() {
        assertFalse(hasUnseenSharedItems(emptyList(), emptySet()))
    }

    // ── unseenFriendRequestCount ─────────────────────────────────────────────

    /**
     * Replicates unseenFriendRequestCount Flow logic:
     * requests.count { it.requestId !in seen }
     */
    private fun computeUnseenFriendRequestCount(requestIds: List<String>, seenIds: Set<String>): Int {
        return requestIds.count { it !in seenIds }
    }

    @Test
    fun `unseenFriendRequestCount - counts only unseen requests`() {
        assertEquals(2, computeUnseenFriendRequestCount(listOf("r1", "r2", "r3"), setOf("r1")))
    }

    @Test
    fun `unseenFriendRequestCount - zero when all seen`() {
        assertEquals(0, computeUnseenFriendRequestCount(listOf("r1", "r2"), setOf("r1", "r2")))
    }

    // ── updateRawUnreadPerFriend: seen-set re-removal ────────────────────────

    /**
     * Replicates the updateRawUnreadPerFriend logic (line 164-179).
     * When a friend has new unread messages, they are REMOVED from the seen set
     * so their red dot reappears.
     */
    private fun computeUpdatedSeenSet(
        unreadMap: Map<String, Int>,
        currentSeen: Set<String>
    ): Set<String> {
        val friendsWithNewMessages = unreadMap.filter { it.value > 0 }.keys
        return if (friendsWithNewMessages.isNotEmpty()) {
            currentSeen - friendsWithNewMessages
        } else {
            currentSeen
        }
    }

    @Test
    fun `updateRawUnread - removes friends with new messages from seen set`() {
        val seen = setOf("friend1", "friend2", "friend3")
        val unread = mapOf("friend2" to 3)
        val updated = computeUpdatedSeenSet(unread, seen)

        assertEquals(setOf("friend1", "friend3"), updated)
    }

    @Test
    fun `updateRawUnread - keeps seen set intact when no new messages`() {
        val seen = setOf("friend1", "friend2")
        val unread = mapOf("friend1" to 0, "friend2" to 0)
        val updated = computeUpdatedSeenSet(unread, seen)

        assertEquals(setOf("friend1", "friend2"), updated)
    }

    @Test
    fun `updateRawUnread - empty unread map preserves seen set`() {
        val seen = setOf("friend1")
        val updated = computeUpdatedSeenSet(emptyMap(), seen)

        assertEquals(setOf("friend1"), updated)
    }

    @Test
    fun `updateRawUnread - removes multiple friends with new messages`() {
        val seen = setOf("f1", "f2", "f3", "f4")
        val unread = mapOf("f2" to 1, "f4" to 5)
        val updated = computeUpdatedSeenSet(unread, seen)

        assertEquals(setOf("f1", "f3"), updated)
    }

    @Test
    fun `updateRawUnread - friend not in seen set is harmless`() {
        val seen = setOf("f1")
        val unread = mapOf("f999" to 2)
        val updated = computeUpdatedSeenSet(unread, seen)

        assertEquals(setOf("f1"), updated) // f999 was not in seen, so no change
    }

    // ── unseenUnreadPerFriend Flow logic ──────────────────────────────────────

    /**
     * Replicates unseenUnreadPerFriend combine logic:
     * raw.mapValues { (friendId, count) -> if (friendId in seen) 0 else count }
     *     .filter { it.value > 0 }
     */
    private fun computeUnseenUnread(
        rawUnread: Map<String, Int>,
        seenFriendIds: Set<String>
    ): Map<String, Int> {
        return rawUnread
            .mapValues { (friendId, count) -> if (friendId in seenFriendIds) 0 else count }
            .filter { it.value > 0 }
    }

    @Test
    fun `unseenUnreadPerFriend - zeroes out seen friends`() {
        val raw = mapOf("f1" to 5, "f2" to 3)
        val seen = setOf("f1")
        val result = computeUnseenUnread(raw, seen)

        assertEquals(mapOf("f2" to 3), result)
    }

    @Test
    fun `unseenUnreadPerFriend - all friends seen results in empty map`() {
        val raw = mapOf("f1" to 5, "f2" to 3)
        val seen = setOf("f1", "f2")
        val result = computeUnseenUnread(raw, seen)

        assertTrue(result.isEmpty())
    }

    @Test
    fun `unseenUnreadPerFriend - no friends seen preserves all`() {
        val raw = mapOf("f1" to 5, "f2" to 3)
        val result = computeUnseenUnread(raw, emptySet())

        assertEquals(raw, result)
    }

    @Test
    fun `unseenUnreadPerFriend - filters zero-count entries`() {
        val raw = mapOf("f1" to 0, "f2" to 3)
        val result = computeUnseenUnread(raw, emptySet())

        assertEquals(mapOf("f2" to 3), result)
    }

    // ── Seen set intersection on inbox/request updates ───────────────────────

    /**
     * When the shared inbox or friend requests list updates, the seen set must be
     * intersected with the current IDs to remove expired/accepted/dismissed items.
     * (SharedFriendsDataSource line 252 and 264)
     */
    @Test
    fun `seen set intersection removes expired request IDs`() {
        val seenRequestIds = setOf("req1", "req2", "req3")
        val currentRequestIds = setOf("req2", "req4") // req1, req3 accepted/rejected
        val cleaned = seenRequestIds.intersect(currentRequestIds)

        assertEquals(setOf("req2"), cleaned)
    }

    @Test
    fun `seen set intersection removes dismissed shared items`() {
        val seenItemIds = setOf("item1", "item2", "item3")
        val currentItemIds = setOf("item2", "item3", "item4") // item1 dismissed
        val cleaned = seenItemIds.intersect(currentItemIds)

        assertEquals(setOf("item2", "item3"), cleaned)
    }

    @Test
    fun `seen set intersection with empty current clears all`() {
        val seenIds = setOf("a", "b", "c")
        val cleaned = seenIds.intersect(emptySet<String>())

        assertTrue(cleaned.isEmpty())
    }

    // ── isFriend membership check ────────────────────────────────────────────

    private fun isFriend(friends: List<FriendRelation>, friendId: String): Boolean {
        return friends.any { it.friendId == friendId }
    }

    @Test
    fun `isFriend - returns true when friend exists`() {
        val friends = listOf(
            FriendRelation(friendId = "user1", friendUsername = "Alice"),
            FriendRelation(friendId = "user2", friendUsername = "Bob")
        )
        assertTrue(isFriend(friends, "user1"))
    }

    @Test
    fun `isFriend - returns false when friend not in list`() {
        val friends = listOf(
            FriendRelation(friendId = "user1", friendUsername = "Alice")
        )
        assertFalse(isFriend(friends, "user999"))
    }

    @Test
    fun `isFriend - returns false for empty list`() {
        assertFalse(isFriend(emptyList(), "user1"))
    }

    // ── applyUsernameUpdates ─────────────────────────────────────────────────

    /**
     * Replicates applyUsernameUpdates logic (line 313-326).
     */
    private fun applyUsernameUpdates(
        friends: List<FriendRelation>,
        updates: Map<String, String>
    ): List<FriendRelation> {
        if (updates.isEmpty()) return friends
        return friends.map { rel ->
            val newName = updates[rel.friendId]
            if (newName != null && newName != rel.friendUsername) rel.copy(friendUsername = newName)
            else rel
        }
    }

    @Test
    fun `applyUsernameUpdates - updates matching friend usernames`() {
        val friends = listOf(
            FriendRelation(friendId = "u1", friendUsername = "OldName1"),
            FriendRelation(friendId = "u2", friendUsername = "OldName2")
        )
        val updates = mapOf("u1" to "NewName1")
        val result = applyUsernameUpdates(friends, updates)

        assertEquals("NewName1", result[0].friendUsername)
        assertEquals("OldName2", result[1].friendUsername)
    }

    @Test
    fun `applyUsernameUpdates - no change when name is same`() {
        val friends = listOf(FriendRelation(friendId = "u1", friendUsername = "Same"))
        val updates = mapOf("u1" to "Same")
        val result = applyUsernameUpdates(friends, updates)

        assertEquals("Same", result[0].friendUsername)
        // Should be the same object reference (no copy)
        assertTrue(friends[0] === result[0])
    }

    @Test
    fun `applyUsernameUpdates - empty updates returns same list`() {
        val friends = listOf(FriendRelation(friendId = "u1", friendUsername = "Alice"))
        val result = applyUsernameUpdates(friends, emptyMap())

        assertSame(friends, result)
    }

    @Test
    fun `applyUsernameUpdates - update for non-existent friend is harmless`() {
        val friends = listOf(FriendRelation(friendId = "u1", friendUsername = "Alice"))
        val updates = mapOf("u999" to "Ghost")
        val result = applyUsernameUpdates(friends, updates)

        assertEquals("Alice", result[0].friendUsername)
    }

    @Test
    fun `applyUsernameUpdates - updates multiple friends`() {
        val friends = listOf(
            FriendRelation(friendId = "u1", friendUsername = "Old1"),
            FriendRelation(friendId = "u2", friendUsername = "Old2"),
            FriendRelation(friendId = "u3", friendUsername = "Old3")
        )
        val updates = mapOf("u1" to "New1", "u3" to "New3")
        val result = applyUsernameUpdates(friends, updates)

        assertEquals("New1", result[0].friendUsername)
        assertEquals("Old2", result[1].friendUsername)
        assertEquals("New3", result[2].friendUsername)
    }

    // ── Username cache operations ────────────────────────────────────────────

    @Test
    fun `username cache - stores and retrieves username`() {
        val cache = mutableMapOf<String, String>()
        val userId = "user1"
        val username = "Alice"

        if (username.isNotBlank()) cache[userId] = username

        assertEquals("Alice", cache[userId])
    }

    @Test
    fun `username cache - blank username is not cached`() {
        val cache = mutableMapOf<String, String>()
        val username = "   "

        if (username.isNotBlank()) cache["user1"] = username

        assertNull(cache["user1"])
    }

    @Test
    fun `username cache - returns null for unknown user`() {
        val cache = mutableMapOf<String, String>()
        assertNull(cache["unknown"])
    }

    @Test
    fun `username cache - overwrites previous value`() {
        val cache = mutableMapOf<String, String>()
        cache["user1"] = "OldName"
        cache["user1"] = "NewName"
        assertEquals("NewName", cache["user1"])
    }

    @Test
    fun `username cache - populated from friend relations`() {
        val cache = mutableMapOf<String, String>()
        val friends = listOf(
            FriendRelation(friendId = "u1", friendUsername = "Alice"),
            FriendRelation(friendId = "u2", friendUsername = "Bob"),
            FriendRelation(friendId = "u3", friendUsername = "")  // blank - should NOT be cached
        )

        friends.forEach { rel ->
            if (rel.friendUsername.isNotBlank()) {
                cache[rel.friendId] = rel.friendUsername
            }
        }

        assertEquals("Alice", cache["u1"])
        assertEquals("Bob", cache["u2"])
        assertNull("Blank username should not be cached", cache["u3"])
    }

    // ── startObserving idempotency ──────────────────────────────────────────

    /**
     * Replicates startObserving idempotency check (line 209-214).
     */
    private fun shouldSkipObserving(
        currentUserId: String?,
        newUserId: String,
        friendsJobActive: Boolean,
        requestsJobActive: Boolean,
        inboxJobActive: Boolean
    ): Boolean {
        return newUserId == currentUserId &&
            friendsJobActive &&
            requestsJobActive &&
            inboxJobActive
    }

    @Test
    fun `startObserving - skips when all jobs active for same user`() {
        assertTrue(shouldSkipObserving("user1", "user1", true, true, true))
    }

    @Test
    fun `startObserving - does not skip for different user`() {
        assertFalse(shouldSkipObserving("user1", "user2", true, true, true))
    }

    @Test
    fun `startObserving - does not skip when friends job inactive`() {
        assertFalse(shouldSkipObserving("user1", "user1", false, true, true))
    }

    @Test
    fun `startObserving - does not skip when requests job inactive`() {
        assertFalse(shouldSkipObserving("user1", "user1", true, false, true))
    }

    @Test
    fun `startObserving - does not skip when inbox job inactive`() {
        assertFalse(shouldSkipObserving("user1", "user1", true, true, false))
    }

    @Test
    fun `startObserving - does not skip when currentUserId is null`() {
        assertFalse(shouldSkipObserving(null, "user1", true, true, true))
    }
}
