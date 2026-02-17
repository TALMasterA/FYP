package com.example.fyp.domain.friends

import com.example.fyp.data.friends.FriendsRepository
import com.example.fyp.model.UserId
import com.example.fyp.model.friends.FriendRequest
import javax.inject.Inject

/**
 * Use case for sending a friend request to another user.
 */
class SendFriendRequestUseCase @Inject constructor(
    private val friendsRepository: FriendsRepository
) {
    suspend operator fun invoke(fromUserId: UserId, toUserId: UserId): Result<FriendRequest> {
        return friendsRepository.sendFriendRequest(fromUserId, toUserId)
    }
}
