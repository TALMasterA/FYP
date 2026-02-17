package com.example.fyp.domain.friends

import com.example.fyp.data.friends.FriendsRepository
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
