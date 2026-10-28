package com.translator.TalknLearn.domain.friends

import com.translator.TalknLearn.data.friends.FriendsRepository
import com.translator.TalknLearn.model.UserId
import com.translator.TalknLearn.model.friends.PublicUserProfile
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
