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
        // Best-effort: We don't fail the whole operation if chat deletion fails,
        // since the friend relationship is already removed
        try {
            val chatId = chatRepository.generateChatId(userId, friendId)
            val deleteResult = chatRepository.deleteChatConversation(chatId)
            if (deleteResult.isFailure) {
                // Log but don't fail - friend removal succeeded
                android.util.Log.w("RemoveFriendUseCase", "Chat deletion failed but friend was removed", deleteResult.exceptionOrNull())
            }
        } catch (e: Exception) {
            // Catch any exception from chat deletion - don't let it fail friend removal
            android.util.Log.w("RemoveFriendUseCase", "Exception during chat deletion", e)
        }

        return Result.success(Unit)
    }
}
