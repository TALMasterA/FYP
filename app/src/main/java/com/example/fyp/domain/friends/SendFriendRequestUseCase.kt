package com.example.fyp.domain.friends

import com.example.fyp.data.friends.FriendsRepository
import com.example.fyp.model.UserId
import javax.inject.Inject

/**
 * Use case for sending a friend request to another user.
 */
class SendFriendRequestUseCase @Inject constructor(
    private val friendsRepository: FriendsRepository
) {
    suspend operator fun invoke(fromUserId: UserId, toUserId: UserId): Result<Unit> {
        if (fromUserId == toUserId) {
            return Result.failure(IllegalArgumentException("Cannot send friend request to yourself"))
        }
        return friendsRepository.sendFriendRequest(fromUserId, toUserId).map { }
    }
}
