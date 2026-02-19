package com.example.fyp.domain.friends

import com.example.fyp.data.friends.FriendsRepository
import com.example.fyp.model.UserId
import com.example.fyp.model.friends.FriendRequest
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