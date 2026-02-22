package com.example.fyp.model.friends

import org.junit.Test
import org.junit.Assert.*

/**
 * Unit tests for FriendRequest model and RequestStatus enum.
 */
class FriendRequestTest {

    @Test
    fun `default request has PENDING status`() {
        val request = FriendRequest()
        assertEquals(RequestStatus.PENDING, request.status)
    }

    @Test
    fun `default request has empty string fields`() {
        val request = FriendRequest()
        assertEquals("", request.requestId)
        assertEquals("", request.fromUserId)
        assertEquals("", request.fromUsername)
        assertEquals("", request.toUserId)
        assertEquals("", request.toUsername)
    }

    @Test
    fun `request stores all fields correctly`() {
        val request = FriendRequest(
            requestId = "req1",
            fromUserId = "user1",
            fromUsername = "sender",
            fromDisplayName = "Sender Name",
            fromAvatarUrl = "https://example.com/avatar1.jpg",
            toUserId = "user2",
            toUsername = "recipient",
            toDisplayName = "Recipient Name",
            status = RequestStatus.PENDING
        )
        assertEquals("req1", request.requestId)
        assertEquals("user1", request.fromUserId)
        assertEquals("sender", request.fromUsername)
        assertEquals("Sender Name", request.fromDisplayName)
        assertEquals("user2", request.toUserId)
        assertEquals("recipient", request.toUsername)
        assertEquals("Recipient Name", request.toDisplayName)
    }

    @Test
    fun `RequestStatus has all expected values`() {
        val values = RequestStatus.entries
        assertEquals(4, values.size)
        assertTrue(values.contains(RequestStatus.PENDING))
        assertTrue(values.contains(RequestStatus.ACCEPTED))
        assertTrue(values.contains(RequestStatus.REJECTED))
        assertTrue(values.contains(RequestStatus.CANCELLED))
    }

    @Test
    fun `request copy allows status update`() {
        val original = FriendRequest(requestId = "req1", status = RequestStatus.PENDING)
        val accepted = original.copy(status = RequestStatus.ACCEPTED)
        assertEquals(RequestStatus.ACCEPTED, accepted.status)
        assertEquals("req1", accepted.requestId)
    }

    @Test
    fun `RequestStatus name matches Firestore string`() {
        assertEquals("PENDING", RequestStatus.PENDING.name)
        assertEquals("ACCEPTED", RequestStatus.ACCEPTED.name)
        assertEquals("REJECTED", RequestStatus.REJECTED.name)
        assertEquals("CANCELLED", RequestStatus.CANCELLED.name)
    }
}
