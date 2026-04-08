package com.example.fyp.model.friends

import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName

/**
 * Public user profile data that can be searched and shared with friends.
 * This is separate from the private UserProfile stored in users/{userId}/profile/settings.
 *
 * NOTE: Firestore's toObject() may return null for collection fields that are missing from
 * a document. Use orEmpty() when accessing learningLanguages to handle this safely.
 *
 * NOTE: uid defaults to empty string here for safe default-construction
 * (e.g., in ProfileUiState). Always set it explicitly when creating real profiles.
 *
 * IMPORTANT: The @PropertyName("isDiscoverable") annotation is required because Kotlin's
 * JavaBean convention for `is`-prefixed Boolean properties causes the Firebase SDK to
 * infer the Firestore field name as "discoverable" (stripping the "is" prefix from the
 * getter `isDiscoverable()`). Without the annotation, writes via Map<String,Any> store
 * the field as "isDiscoverable", but toObject() looks for "discoverable" — causing the
 * value to silently default to false on every read.
 */
data class PublicUserProfile(
    val uid: String = "",                         // Firestore document ID
    val username: String = "",                    // Unique, searchable username
    val avatarUrl: String = "",                   // Profile picture URL (optional)
    val primaryLanguage: String = "",             // User's primary language code (e.g., "en-US")
    val learningLanguages: List<String>? = emptyList(),  // Languages being learned (nullable for Firestore compat)
    @field:PropertyName("isDiscoverable")
    @get:PropertyName("isDiscoverable")
    val isDiscoverable: Boolean = false,          // Can be found via search (defaults to PRIVATE for security)
    val createdAt: Timestamp = Timestamp.now(),
    val lastActiveAt: Timestamp = Timestamp.now()
)
