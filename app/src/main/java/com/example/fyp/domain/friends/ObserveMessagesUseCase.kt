package com.example.fyp.domain.friends

import com.example.fyp.data.friends.ChatRepository
import com.example.fyp.model.UserId
import com.example.fyp.model.friends.FriendMessage
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case to observe messages in a chat in real-time.
 */
class ObserveMessagesUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {
    operator fun invoke(userId: UserId, friendId: UserId): Flow<List<FriendMessage>> {
        val chatId = chatRepository.generateChatId(userId, friendId)
        return chatRepository.observeMessages(chatId)
    }
}
