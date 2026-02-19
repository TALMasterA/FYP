package com.example.fyp.model.friends

import com.example.fyp.model.UserId
import com.google.firebase.Timestamp

/**
 * Public user profile data that can be searched and shared with friends.
 * This is separate from the private UserProfile stored in users/{userId}/profile/settings.
 */
data class PublicUserProfile(
    val userId: UserId = UserId(""),
    val uid: String = userId.value,               // Firestore document ID (kept for compatibility)
    val username: String = "",                    // Unique, searchable username
    val displayName: String = "",                 // Optional display name
    val avatarUrl: String = "",                   // Profile picture URL (optional)
    val primaryLanguage: String = "",             // User's primary language code (e.g., "en-US")
    val learningLanguages: List<String> = emptyList(),  // Languages being learned
    val isDiscoverable: Boolean = true,           // Can be found via search
    val createdAt: Timestamp = Timestamp.now(),
    val lastActiveAt: Timestamp = Timestamp.now()
)
