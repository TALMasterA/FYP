package com.example.fyp.domain.friends

import com.example.fyp.data.friends.ChatRepository
import com.example.fyp.model.UserId
import com.example.fyp.model.friends.FriendMessage
import javax.inject.Inject

/**
 * Use case to send a text message to a friend.
 */
class SendMessageUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {
    suspend operator fun invoke(
        fromUserId: UserId,
        toUserId: UserId,
        content: String
    ): Result<FriendMessage> {
        return chatRepository.sendTextMessage(fromUserId, toUserId, content)
    }
}
