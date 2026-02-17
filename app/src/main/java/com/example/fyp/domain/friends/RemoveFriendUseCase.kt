package com.example.fyp.domain.friends

import com.example.fyp.data.friends.FriendsRepository
import com.example.fyp.model.UserId
import javax.inject.Inject

/**
 * Use case for removing a friend.
 */
class RemoveFriendUseCase @Inject constructor(
    private val friendsRepository: FriendsRepository
) {
    suspend operator fun invoke(userId: UserId, friendId: UserId): Result<Unit> {
        return friendsRepository.removeFriend(userId, friendId)
    }
}
