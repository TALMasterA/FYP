package com.example.fyp.domain.friends

import com.example.fyp.data.friends.FriendsRepository
import com.example.fyp.model.UserId
import javax.inject.Inject

/**
 * Use case for accepting a friend request.
 */
class AcceptFriendRequestUseCase @Inject constructor(
    private val friendsRepository: FriendsRepository
) {
    suspend operator fun invoke(requestId: String, currentUserId: UserId, friendUserId: UserId): Result<Unit> {
        return friendsRepository.acceptFriendRequest(requestId, currentUserId, friendUserId)
    }
}
