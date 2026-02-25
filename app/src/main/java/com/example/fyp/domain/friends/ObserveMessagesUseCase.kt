package com.example.fyp.domain.friends

import com.example.fyp.data.friends.ChatRepository
import com.example.fyp.model.UserId
import com.example.fyp.model.friends.FriendMessage
import com.google.firebase.Timestamp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Use case to observe messages in a chat in real-time.
 * Optionally filters out messages older than the given clearedAt timestamp.
 */
class ObserveMessagesUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {
    operator fun invoke(
        userId: UserId,
        friendId: UserId,
        clearedAt: Timestamp? = null
    ): Flow<List<FriendMessage>> {
        val chatId = chatRepository.generateChatId(userId, friendId)
        val flow = chatRepository.observeMessages(chatId)
        return if (clearedAt != null) {
            flow.map { messages -> messages.filter { it.createdAt > clearedAt } }
        } else {
            flow
        }
    }
}
