package com.example.fyp.domain.friends

import com.example.fyp.data.friends.FriendsRepository
import com.example.fyp.model.UserId
import com.example.fyp.model.friends.PublicUserProfile
import javax.inject.Inject

/**
 * Use case to get the current user's public profile.
 * This is used to display the user's own profile information.
 */
class GetCurrentUserProfileUseCase @Inject constructor(
    private val friendsRepository: FriendsRepository
) {
    suspend operator fun invoke(userId: UserId): PublicUserProfile? {
        return friendsRepository.getPublicProfile(userId)
    }
}
