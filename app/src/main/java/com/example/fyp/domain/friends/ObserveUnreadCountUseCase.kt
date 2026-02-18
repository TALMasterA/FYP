package com.example.fyp.domain.friends

import com.example.fyp.data.friends.ChatRepository
import com.example.fyp.model.UserId
import javax.inject.Inject

/**
 * Use case to get the total count of unread messages for a user.
 * Note: This is a suspend function, not observable. For real-time updates,
 * the caller should poll this periodically or trigger on chat updates.
 */
class ObserveUnreadCountUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {
    suspend operator fun invoke(userId: UserId): Int {
        return chatRepository.getTotalUnreadCount(userId)
    }
}
