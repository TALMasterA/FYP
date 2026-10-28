package com.translator.TalknLearn.domain.friends

import com.translator.TalknLearn.data.friends.FriendsRepository
import com.translator.TalknLearn.model.UserId
import javax.inject.Inject

/**
 * Use case for sending a friend request to another user.
 */
class SendFriendRequestUseCase @Inject constructor(
    private val friendsRepository: FriendsRepository
) {
    suspend operator fun invoke(fromUserId: UserId, toUserId: UserId, note: String = ""): Result<Unit> {
        if (fromUserId == toUserId) {
            return Result.failure(IllegalArgumentException("Cannot send friend request to yourself"))
        }
        return friendsRepository.sendFriendRequest(fromUserId, toUserId, note).map { }
    }
}
