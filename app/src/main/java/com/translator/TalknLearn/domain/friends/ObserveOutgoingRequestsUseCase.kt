package com.translator.TalknLearn.domain.friends

import com.translator.TalknLearn.data.friends.FriendsRepository
import com.translator.TalknLearn.model.UserId
import com.translator.TalknLearn.model.friends.FriendRequest
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for observing outgoing friend requests (requests sent by current user).
 */
class ObserveOutgoingRequestsUseCase @Inject constructor(
    private val friendsRepository: FriendsRepository
) {
    operator fun invoke(userId: UserId): Flow<List<FriendRequest>> {
        return friendsRepository.observeOutgoingRequests(userId)
    }
}