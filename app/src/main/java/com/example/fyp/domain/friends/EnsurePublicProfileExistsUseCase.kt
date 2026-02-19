package com.example.fyp.domain.friends

import com.example.fyp.data.friends.FriendsRepository
import com.example.fyp.data.settings.UserSettingsRepository
import com.example.fyp.model.UserId
import com.example.fyp.model.friends.PublicUserProfile
import com.google.firebase.Timestamp
import javax.inject.Inject

/**
 * Use case to ensure that a public profile exists for a user.
 * This should be called when a user logs in or registers.
 *
 * If the profile doesn't exist, it creates one with default values.
 * If it exists, it updates the lastActiveAt timestamp.
 */
class EnsurePublicProfileExistsUseCase @Inject constructor(
    private val friendsRepository: FriendsRepository,
    private val settingsRepository: UserSettingsRepository
) {
    suspend operator fun invoke(userId: String) {
        val uid = UserId(userId)

        // Get user settings to get current primary language
        val settings = settingsRepository.fetchUserSettings(uid)
        val currentPrimaryLanguage = settings.primaryLanguageCode.ifBlank { "en-US" }

        // Check if profile already exists
        val existingProfile = friendsRepository.getPublicProfile(uid)

        if (existingProfile == null) {
            // Create a new public profile with default values
            val newProfile = PublicUserProfile(
                uid = userId,
                username = "",  // Will be empty until user sets it
                displayName = "",  // Will be empty until user sets it
                avatarUrl = "",
                primaryLanguage = currentPrimaryLanguage,
                learningLanguages = emptyList(),
                isDiscoverable = true,
                createdAt = Timestamp.now(),
                lastActiveAt = Timestamp.now()
            )

            friendsRepository.createOrUpdatePublicProfile(uid, newProfile)
        } else {
            // Update lastActiveAt timestamp AND primary language (in case it changed)
            friendsRepository.updatePublicProfile(
                uid,
                mapOf(
                    "lastActiveAt" to Timestamp.now(),
                    "primaryLanguage" to currentPrimaryLanguage
                )
            )
        }
    }
}

