package com.example.fyp.domain.friends

import com.example.fyp.data.friends.ChatRepository
import com.example.fyp.model.UserId
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case to observe the count of unread messages for a user.
 */
class ObserveUnreadCountUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {
    operator fun invoke(userId: UserId): Flow<Int> {
        return chatRepository.observeUnreadCount(userId)
    }
}
