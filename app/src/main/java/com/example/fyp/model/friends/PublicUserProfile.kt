package com.example.fyp.model.friends

import com.google.firebase.Timestamp

/**
 * Public user profile data that can be searched and shared with friends.
 * This is separate from the private UserProfile stored in users/{userId}/profile/settings.
 *
 * NOTE: Firestore's toObject() may return null for collection fields that are missing from
 * a document. Use orEmpty() when accessing learningLanguages to handle this safely.
 *
 * NOTE: uid defaults to empty string here for safe default-construction
 * (e.g., in ProfileUiState). Always set it explicitly when creating real profiles.
 */
data class PublicUserProfile(
    val uid: String = "",                         // Firestore document ID
    val username: String = "",                    // Unique, searchable username
    val displayName: String = "",                 // Optional display name
    val avatarUrl: String = "",                   // Profile picture URL (optional)
    val primaryLanguage: String = "",             // User's primary language code (e.g., "en-US")
    val learningLanguages: List<String>? = emptyList(),  // Languages being learned (nullable for Firestore compat)
    val isDiscoverable: Boolean = true,           // Can be found via search
    val createdAt: Timestamp = Timestamp.now(),
    val lastActiveAt: Timestamp = Timestamp.now()
)
