package com.example.fyp.domain.friends

import com.example.fyp.data.friends.FriendsRepository
import com.example.fyp.model.UserId
import com.example.fyp.model.friends.FriendRequest
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for observing incoming friend requests in real-time.
 */
class ObserveIncomingRequestsUseCase @Inject constructor(
    private val friendsRepository: FriendsRepository
) {
    operator fun invoke(userId: UserId): Flow<List<FriendRequest>> {
        return friendsRepository.observeIncomingRequests(userId)
    }
}
