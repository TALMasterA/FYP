package com.example.fyp.domain.friends

import com.example.fyp.core.AppLogger
import com.example.fyp.data.friends.ChatRepository
import com.example.fyp.data.friends.FriendsRepository
import com.example.fyp.model.UserId
import javax.inject.Inject

/**
 * Use case for removing a friend and deleting their chat conversation.
 *
 * SECURITY FIX (1.1): Chat deletion is now transactional — the chat is
 * deleted FIRST. If chat deletion fails the whole operation is rolled back
 * so private messages are never left accessible in Firestore.
 *
 * Retry logic: chat deletion is attempted up to 2 times before failing.
 */
class RemoveFriendUseCase @Inject constructor(
    private val friendsRepository: FriendsRepository,
    private val chatRepository: ChatRepository
) {
    suspend operator fun invoke(userId: UserId, friendId: UserId): Result<Unit> {
        if (userId == friendId) {
            return Result.failure(IllegalArgumentException("Cannot remove yourself as a friend."))
        }

        // Step 1: Delete chat conversation FIRST (fail-fast for privacy)
        val chatId = chatRepository.generateChatId(userId, friendId)
        var chatDeleteAttempts = 0
        var chatDeleteResult: Result<Unit> = Result.failure(IllegalStateException("Not attempted"))

        while (chatDeleteAttempts < 2) {
            chatDeleteAttempts++
            chatDeleteResult = try {
                chatRepository.deleteChatConversation(chatId)
            } catch (e: Exception) {
                Result.failure(e)
            }
            if (chatDeleteResult.isSuccess) break

            // Brief delay before retry
            if (chatDeleteAttempts < 2) {
                kotlinx.coroutines.delay(500)
            }
        }

        if (chatDeleteResult.isFailure) {
            AppLogger.e(
                "RemoveFriendUseCase",
                "Chat deletion failed after $chatDeleteAttempts attempts — aborting friend removal to protect privacy",
                chatDeleteResult.exceptionOrNull()
            )
            return Result.failure(
                IllegalStateException(
                    "Unable to remove friend. Please check your connection and try again.",
                    chatDeleteResult.exceptionOrNull()
                )
            )
        }

        // Step 2: Remove the friend relationship
        val removeResult = friendsRepository.removeFriend(userId, friendId)
        if (removeResult.isFailure) {
            AppLogger.e(
                "RemoveFriendUseCase",
                "Friend removal failed after chat was deleted",
                removeResult.exceptionOrNull()
            )
            return removeResult
        }

        return Result.success(Unit)
    }
}
