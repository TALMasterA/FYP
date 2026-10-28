package com.translator.TalknLearn.data.friends

import com.translator.TalknLearn.model.Username
import com.translator.TalknLearn.model.UserId
import com.translator.TalknLearn.model.friends.PublicUserProfile
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

/**
 * Handles profile and username management within Firestore.
 * Extracted from [FirestoreFriendsRepository] for maintainability.
 */
internal class FirestoreProfileHelper(private val db: FirebaseFirestore) {

    suspend fun createOrUpdatePublicProfile(
        userId: UserId,
        profile: PublicUserProfile
    ): Result<Unit> = try {
        val normalizedUsername = profile.username.trim()
        val canBeDiscoverable = normalizedUsername.isNotBlank() && profile.isDiscoverable

        db.collection("users")
            .document(userId.value)
            .collection("profile")
            .document("public")
            .set(
                profile.copy(
                    username = normalizedUsername,
                    isDiscoverable = canBeDiscoverable
                )
            )
            .await()

        val searchData = mutableMapOf<String, Any>(
            "username" to normalizedUsername,
            "isDiscoverable" to canBeDiscoverable,
            "primaryLanguage" to profile.primaryLanguage,
            "learningLanguages" to (profile.learningLanguages ?: emptyList<String>()),
            "lastActiveAt" to profile.lastActiveAt
        )
        if (normalizedUsername.isNotBlank()) {
            searchData["username_lowercase"] = normalizedUsername.lowercase()
        }
        db.collection("user_search")
            .document(userId.value)
            .set(searchData)
            .await()

        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun getPublicProfile(userId: UserId): PublicUserProfile? = try {
        db.collection("users")
            .document(userId.value)
            .collection("profile")
            .document("public")
            .get()
            .await()
            .toObject(PublicUserProfile::class.java)
    } catch (e: kotlinx.coroutines.CancellationException) {
        throw e
    } catch (e: Exception) {
        android.util.Log.e("FirestoreProfileHelper", "Failed to get public profile for ${userId.value}: ${e.message}")
        null
    }

    suspend fun updatePublicProfile(
        userId: UserId,
        updates: Map<String, Any>
    ): Result<Unit> = try {
        val profileUpdates = updates.toMutableMap()

        val requestedUsername = (updates["username"] as? String)?.trim()
        val requestedDiscoverable = updates["isDiscoverable"] as? Boolean

        if (requestedUsername != null) {
            profileUpdates["username"] = requestedUsername
            if (requestedUsername.isBlank()) {
                profileUpdates["isDiscoverable"] = false
            }
        }

        if (requestedDiscoverable == true && requestedUsername != null && requestedUsername.isBlank()) {
            profileUpdates["isDiscoverable"] = false
        }

        db.collection("users")
            .document(userId.value)
            .collection("profile")
            .document("public")
            .set(profileUpdates, com.google.firebase.firestore.SetOptions.merge())
            .await()

        val searchUpdates = mutableMapOf<String, Any>()
        requestedUsername?.let {
            searchUpdates["username"] = it
            if (it.isBlank()) {
                searchUpdates["isDiscoverable"] = false
                searchUpdates["username_lowercase"] = FieldValue.delete()
            } else {
                searchUpdates["username_lowercase"] = it.lowercase()
                requestedDiscoverable?.let { discoverable -> searchUpdates["isDiscoverable"] = discoverable }
            }
        }
        updates["displayName"]?.let { /* displayName field removed — username is the sole identity field */ }
        updates["isDiscoverable"]?.let {
            if (requestedUsername == null || requestedUsername.isNotBlank() || it == false) {
                searchUpdates["isDiscoverable"] = it
            }
        }
        updates["lastActiveAt"]?.let { searchUpdates["lastActiveAt"] = it }
        updates["primaryLanguage"]?.let { searchUpdates["primaryLanguage"] = it }

        if (searchUpdates.isNotEmpty()) {
            db.collection("user_search")
                .document(userId.value)
                .set(searchUpdates, com.google.firebase.firestore.SetOptions.merge())
                .await()
        }

        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun setUsername(userId: UserId, username: Username): Result<Unit> {
        return try {
            val usernameDoc = db.collection("usernames").document(username.value)

            val existing = usernameDoc.get().await()
            if (existing.exists() && existing.getString("userId") != userId.value) {
                return Result.failure(IllegalArgumentException("Username already taken"))
            }

            val currentProfile = getPublicProfile(userId)
            val oldUsername = currentProfile?.username?.takeIf { it.isNotBlank() && it != username.value }

            usernameDoc.set(mapOf(
                "userId" to userId.value,
                "createdAt" to Timestamp.now()
            )).await()

            if (oldUsername != null) {
                try {
                    db.collection("usernames").document(oldUsername).delete().await()
                } catch (e: Exception) {
                    android.util.Log.w("FirestoreProfileHelper", "Non-critical: old username '$oldUsername' cleanup failed", e)
                }
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun isUsernameAvailable(username: Username): Boolean = try {
        val doc = db.collection("usernames")
            .document(username.value)
            .get()
            .await()
        !doc.exists()
    } catch (e: Exception) {
        false
    }

    suspend fun ensureUserDocumentExists(userId: UserId) {
        try {
            db.collection("users").document(userId.value)
                .set(emptyMap<String, Any>(), com.google.firebase.firestore.SetOptions.merge())
                .await()
        } catch (e: Exception) {
            android.util.Log.e("FirestoreProfileHelper", "Failed to ensure user doc exists", e)
        }
    }
}
