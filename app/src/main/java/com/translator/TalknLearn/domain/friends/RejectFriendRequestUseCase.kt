package com.translator.TalknLearn.domain.friends

import com.translator.TalknLearn.data.friends.FriendsRepository
import javax.inject.Inject

/**
 * Use case for rejecting a friend request.
 */
class RejectFriendRequestUseCase @Inject constructor(
    private val friendsRepository: FriendsRepository
) {
    suspend operator fun invoke(requestId: String): Result<Unit> {
        return friendsRepository.rejectFriendRequest(requestId)
    }
}
