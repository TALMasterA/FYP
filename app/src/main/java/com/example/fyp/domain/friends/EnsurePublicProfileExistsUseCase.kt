package com.example.fyp.domain.friends

import com.example.fyp.data.friends.FriendsRepository
import com.example.fyp.data.settings.UserSettingsRepository
import com.example.fyp.model.UserId
import com.google.firebase.Timestamp
import javax.inject.Inject

/**
 * Ensures that a public profile exists for a user on login/registration.
 *
 * Optimizations vs. original:
 * - Accepts an optional [knownPrimaryLanguage] so callers that already have the
 *   settings in-memory (e.g. AppViewModel via SharedSettingsDataSource) can skip
 *   the extra [UserSettingsRepository.fetchUserSettings] read entirely.
 * - Only writes `lastActiveAt` when the profile exists AND the primary language
 *   has actually changed, eliminating a pointless write on every login.
 *
 * Safety: Uses a merge-based write (updatePublicProfile) instead of a full-overwrite
 * (createOrUpdatePublicProfile). This ensures that if `getPublicProfile` returns null
 * due to a transient read error (not because the profile truly doesn't exist), the
 * existing `isDiscoverable` and `username` fields are **not** reset to defaults.
 *
 * Cost (profile already exists, same primary language): 2 reads, 0 writes.
 * Cost (profile already exists, language changed):       2 reads, 1 write.
 * Cost (profile does not exist):                         2 reads, 1 write.
 * Previous cost (always):                                2 reads, 1 write.
 */
class EnsurePublicProfileExistsUseCase @Inject constructor(
    private val friendsRepository: FriendsRepository,
    private val settingsRepository: UserSettingsRepository
) {
    suspend operator fun invoke(userId: String, knownPrimaryLanguage: String? = null) {
        val uid = UserId(userId)

        // Use caller-supplied language when available to avoid a Firestore read.
        val currentPrimaryLanguage = knownPrimaryLanguage?.takeIf { it.isNotBlank() }
            ?: settingsRepository.fetchUserSettings(uid).primaryLanguageCode.ifBlank { "en-US" }

        val existingProfile = friendsRepository.getPublicProfile(uid)

        if (existingProfile == null) {
            // Profile was not found. This could mean:
            // (a) Profile truly doesn't exist (new user), OR
            // (b) Read failed (network error / Firestore cache miss on cold start).
            //
            // Use a merge-based write that initialises only non-identity fields.
            // Crucially, we do NOT include 'isDiscoverable' or 'username' so that:
            //   • For case (a): the new document has no isDiscoverable field; Kotlin
            //     default (false = private) applies on read — correct for new users.
            //   • For case (b): the existing isDiscoverable and username values on the
            //     server are preserved, preventing an accidental visibility reset.
            val defaultFields = mapOf<String, Any>(
                "uid" to userId,
                "primaryLanguage" to currentPrimaryLanguage,
                "lastActiveAt" to Timestamp.now()
            )
            friendsRepository.updatePublicProfile(uid, defaultFields)
            // Ensure the main user document exists with default unread fields
            // so chat update() calls don't fail with NOT_FOUND for new users.
            friendsRepository.ensureUserDocumentExists(uid)
        } else {
            // Only write if the primary language actually changed – avoids a write on
            // every login when the user hasn't switched languages.
            if (existingProfile.primaryLanguage != currentPrimaryLanguage) {
                friendsRepository.updatePublicProfile(
                    uid,
                    mapOf(
                        "lastActiveAt" to Timestamp.now(),
                        "primaryLanguage" to currentPrimaryLanguage
                    )
                )
            }
            // If nothing changed we skip the write entirely.
        }
    }
}

