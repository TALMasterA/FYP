package com.example.fyp.model.user

import com.google.firebase.Timestamp

/**
 * Extended user profile data stored in Firestore.
 * This is separate from FirebaseAuth User data.
 */
data class UserProfile(
    val createdAt: Timestamp? = null,
    val updatedAt: Timestamp? = null
)
