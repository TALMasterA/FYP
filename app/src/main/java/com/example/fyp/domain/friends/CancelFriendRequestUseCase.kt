package com.example.fyp.domain.friends

import com.example.fyp.data.friends.FriendsRepository
import javax.inject.Inject

/**
 * Use case for cancelling an outgoing (sent) friend request.
 */
class CancelFriendRequestUseCase @Inject constructor(
    private val friendsRepository: FriendsRepository
) {
    suspend operator fun invoke(requestId: String): Result<Unit> {
        return friendsRepository.cancelFriendRequest(requestId)
    }
}

