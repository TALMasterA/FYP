package com.example.fyp.domain.friends

import com.example.fyp.data.friends.ChatRepository
import com.example.fyp.model.UserId
import javax.inject.Inject

/**
 * Use case to mark all messages in a chat as read.
 */
class MarkMessagesAsReadUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {
    suspend operator fun invoke(userId: UserId, friendId: UserId): Result<Unit> {
        val chatId = chatRepository.generateChatId(userId, friendId)
        return chatRepository.markAllMessagesAsRead(chatId, userId)
    }
}
