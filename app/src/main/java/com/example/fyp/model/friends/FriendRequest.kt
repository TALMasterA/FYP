package com.example.fyp.model.friends

import com.google.firebase.Timestamp

/**
 * Represents a pending friend request.
 */
data class FriendRequest(
    val requestId: String = "",
    val fromUserId: String = "",
    val fromUsername: String = "",
    val fromDisplayName: String = "",
    val fromAvatarUrl: String = "",
    val toUserId: String = "",
    val toUsername: String = "",         // Recipient's username (cached at send time)
    val toDisplayName: String = "",      // Recipient's display name (cached at send time)
    val status: RequestStatus = RequestStatus.PENDING,
    val createdAt: Timestamp = Timestamp.now(),
    val updatedAt: Timestamp = Timestamp.now()
)

/**
 * Status of a friend request.
 */
enum class RequestStatus {
    PENDING,     // Request sent, awaiting response
    ACCEPTED,    // Request accepted, friendship created
    REJECTED,    // Request rejected
    CANCELLED    // Request cancelled by sender
}
