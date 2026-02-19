package com.example.fyp.domain.friends

import com.example.fyp.data.friends.ChatRepository
import com.example.fyp.model.UserId
import javax.inject.Inject

/**
 * Use case to send a text message to a friend.
 */
class SendMessageUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {
    suspend operator fun invoke(
        chatId: String,
        fromUserId: UserId,
        toUserId: UserId,
        content: String
    ): Result<Unit> {
        if (content.isBlank()) {
            return Result.failure(IllegalArgumentException("Message text cannot be empty"))
        }
        return chatRepository.sendMessage(chatId, fromUserId, toUserId, content)
    }
}
