package com.translator.TalknLearn.domain.friends

import com.translator.TalknLearn.data.friends.FriendsRepository
import com.translator.TalknLearn.model.UserId
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
