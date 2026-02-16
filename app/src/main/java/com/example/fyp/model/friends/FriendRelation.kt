package com.example.fyp.model.friends

import com.google.firebase.Timestamp

/**
 * Represents a friend relationship between two users.
 * Each friendship is stored twice (once for each user) for efficient querying.
 */
data class FriendRelation(
    val friendId: String = "",                   // The friend's user ID
    val friendUsername: String = "",             // Cached for display
    val friendDisplayName: String = "",          // Cached for display
    val friendAvatarUrl: String = "",            // Cached for display (optional)
    val addedAt: Timestamp = Timestamp.now()
)
