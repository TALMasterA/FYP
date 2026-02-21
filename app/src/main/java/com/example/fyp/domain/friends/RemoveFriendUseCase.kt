package com.example.fyp.domain.friends

import com.example.fyp.data.friends.ChatRepository
import com.example.fyp.data.friends.FriendsRepository
import com.example.fyp.model.UserId
import javax.inject.Inject

/**
 * Use case for removing a friend and deleting their chat conversation.
 */
class RemoveFriendUseCase @Inject constructor(
    private val friendsRepository: FriendsRepository,
    private val chatRepository: ChatRepository
) {
    suspend operator fun invoke(userId: UserId, friendId: UserId): Result<Unit> {
        // First remove the friend relationship
        val removeResult = friendsRepository.removeFriend(userId, friendId)
        if (removeResult.isFailure) {
            return removeResult
        }

        // Then delete the chat conversation (messages + metadata)
        val chatId = chatRepository.generateChatId(userId, friendId)
        chatRepository.deleteChatConversation(chatId)
        // Note: We don't fail the whole operation if chat deletion fails,
        // since the friend relationship is already removed

        return Result.success(Unit)
    }
}
