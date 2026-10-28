package com.translator.TalknLearn.model.friends

import com.google.firebase.Timestamp
import org.junit.Test
import org.junit.Assert.*

/**
 * Unit tests for FriendRequest and FriendRelation models.
 */
class FriendRelationTest {

    // --- FriendRequest Tests ---

    @Test
    fun `friend request default status is PENDING`() {
        val request = FriendRequest(
            requestId = "req1",
            fromUserId = "user1",
            toUserId = "user2"
        )
        assertEquals(RequestStatus.PENDING, request.status)
    }

    @Test
    fun `friend request stores both user IDs`() {
        val request = FriendRequest(
            requestId = "req1",
            fromUserId = "sender",
            toUserId = "receiver",
            fromUsername = "sender_name",
            toUsername = "receiver_name"
        )
        assertEquals("sender", request.fromUserId)
        assertEquals("receiver", request.toUserId)
        assertEquals("sender_name", request.fromUsername)
        assertEquals("receiver_name", request.toUsername)
    }

    @Test
    fun `all request statuses are valid`() {
        val statuses = RequestStatus.entries
        assertEquals(4, statuses.size)
        assertTrue(statuses.contains(RequestStatus.PENDING))
        assertTrue(statuses.contains(RequestStatus.ACCEPTED))
        assertTrue(statuses.contains(RequestStatus.REJECTED))
        assertTrue(statuses.contains(RequestStatus.CANCELLED))
    }

    @Test
    fun `friend request defaults are empty strings`() {
        val request = FriendRequest()
        assertEquals("", request.requestId)
        assertEquals("", request.fromUserId)
        assertEquals("", request.toUserId)
        assertEquals("", request.fromUsername)
        assertEquals("", request.toUsername)
        assertEquals("", request.fromUsername)
    }

    // --- FriendRelation Tests ---

    @Test
    fun `friend relation stores friend info`() {
        val friend = FriendRelation(
            friendId = "user2",
            friendUsername = "john_doe"
        )
        assertEquals("user2", friend.friendId)
        assertEquals("john_doe", friend.friendUsername)
    }

    @Test
    fun `friend relation defaults are empty strings`() {
        val friend = FriendRelation()
        assertEquals("", friend.friendId)
        assertEquals("", friend.friendUsername)
    }

    @Test
    fun `friend relation addedAt is set`() {
        val friend = FriendRelation(friendId = "user1")
        assertNotNull(friend.addedAt)
    }

    @Test
    fun `friends can be sorted by addedAt`() {
        val t1 = Timestamp(1000, 0)
        val t2 = Timestamp(2000, 0)
        val t3 = Timestamp(3000, 0)

        val friends = listOf(
            FriendRelation(friendId = "user3", addedAt = t3),
            FriendRelation(friendId = "user1", addedAt = t1),
            FriendRelation(friendId = "user2", addedAt = t2)
        )

        val sorted = friends.sortedBy { it.addedAt.seconds }
        assertEquals("user1", sorted[0].friendId)
        assertEquals("user2", sorted[1].friendId)
        assertEquals("user3", sorted[2].friendId)
    }

    @Test
    fun `friends list can be filtered by username`() {
        val friends = listOf(
            FriendRelation(friendId = "1", friendUsername = "alice"),
            FriendRelation(friendId = "2", friendUsername = "bob"),
            FriendRelation(friendId = "3", friendUsername = "alice2")
        )

        val alices = friends.filter { it.friendUsername.startsWith("alice") }
        assertEquals(2, alices.size)
    }
}
