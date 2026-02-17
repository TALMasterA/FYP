package com.example.fyp.domain.friends

import com.example.fyp.data.friends.FriendsRepository
import com.example.fyp.model.UserId
import com.example.fyp.model.friends.FriendRelation
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for observing the user's friends list in real-time.
 */
class ObserveFriendsUseCase @Inject constructor(
    private val friendsRepository: FriendsRepository
) {
    operator fun invoke(userId: UserId): Flow<List<FriendRelation>> {
        return friendsRepository.observeFriends(userId)
    }
}
