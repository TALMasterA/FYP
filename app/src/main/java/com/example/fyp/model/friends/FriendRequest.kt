package com.example.fyp.model.friends

import com.google.firebase.Timestamp

/**
 * Represents a friend request with expiration support.
 *
 * FIX 1.3: Status is now updated to ACCEPTED/REJECTED instead of deleting,
 * preserving request history for analytics and audit trails.
 * FIX 2.7: expiresAt field added — requests expire after 30 days.
 * FIX 4.1: rejectionReason field added — optional reason when rejecting.
 */
data class FriendRequest(
    val requestId: String = "",
    val fromUserId: String = "",
    val fromUsername: String = "",
    val fromAvatarUrl: String = "",
    val toUserId: String = "",
    val toUsername: String = "",         // Recipient's username (cached at send time)
    val status: RequestStatus = RequestStatus.PENDING,
    val createdAt: Timestamp = Timestamp.now(),
    val updatedAt: Timestamp = Timestamp.now(),
    val note: String = "",               // Optional short note from sender
    val expiresAt: Timestamp? = null,    // FIX 2.7: Expiration timestamp (30 days from creation)
    val rejectionReason: String = ""     // FIX 4.1: Optional reason for rejection
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
