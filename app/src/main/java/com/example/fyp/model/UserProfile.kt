package com.example.fyp.model

import com.google.firebase.Timestamp

/**
 * Extended user profile data stored in Firestore.
 * This is separate from FirebaseAuth User data.
 */
data class UserProfile(
    val displayName: String = "",
    val photoUrl: String? = null,
    val createdAt: Timestamp? = null,
    val updatedAt: Timestamp? = null
)
