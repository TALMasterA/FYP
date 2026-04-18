package com.example.fyp.model.friends

import androidx.compose.runtime.Immutable
import com.google.firebase.Timestamp

/**
 * Represents a friend relationship between two users.
 * Each friendship is stored twice (once for each user) for efficient querying.
 *
 * FIX 6.5: Marked @Immutable so Compose can skip recomposition when the
 * reference hasn't changed, reducing unnecessary UI updates in LazyColumn.
 */
@Immutable
data class FriendRelation(
    val friendId: String = "",                   // The friend's user ID
    val friendUsername: String = "",             // Cached for display
    val addedAt: Timestamp = Timestamp.now()
)
