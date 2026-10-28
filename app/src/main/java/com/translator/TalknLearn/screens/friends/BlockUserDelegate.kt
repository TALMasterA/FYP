package com.translator.TalknLearn.screens.friends

import com.translator.TalknLearn.core.ErrorMessages
import com.translator.TalknLearn.core.security.AuditLogger
import com.translator.TalknLearn.data.friends.BlockedUser
import com.translator.TalknLearn.data.friends.FriendsRepository
import com.translator.TalknLearn.domain.friends.RemoveFriendUseCase
import com.translator.TalknLearn.model.UserId
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

/**
 * Handles block / unblock user logic for the Friends screen.
 * Extracted from [FriendsViewModel] for maintainability.
 */
internal class BlockUserDelegate(
    private val uiState: MutableStateFlow<FriendsUiState>,
    private val scope: CoroutineScope,
    private val friendsRepository: FriendsRepository,
    private val removeFriendUseCase: RemoveFriendUseCase,
    private val getCurrentUserId: () -> UserId?,
    private val showSuccess: (String) -> Unit
) {
    fun loadBlockedUsers(userId: UserId) {
        scope.launch {
            try {
                val blockedList = friendsRepository.getBlockedUsers(userId)
                uiState.value = uiState.value.copy(
                    blockedUsers = blockedList,
                    blockedUserIds = blockedList.map { it.userId }.toSet()
                )
            } catch (e: Exception) {
                android.util.Log.w("FriendsViewModel", "Failed to load blocked users", e)
            }
        }
    }

    fun blockAndRemoveFriend(targetUserId: String, targetUsername: String) {
        val userId = getCurrentUserId() ?: return
        scope.launch {
            // Step 1: remove friend relationship AND delete chat
            val removeResult = removeFriendUseCase(userId, UserId(targetUserId))
            if (removeResult.isFailure) {
                uiState.value = uiState.value.copy(error = ErrorMessages.FRIEND_REMOVE_FAILED)
                return@launch
            }

            // Step 2: block the user
            friendsRepository.blockUser(userId, UserId(targetUserId), targetUsername).fold(
                onSuccess = {
                    val newBlockedUser = BlockedUser(
                        userId = targetUserId,
                        username = targetUsername
                    )
                    val updatedIds = uiState.value.blockedUserIds + targetUserId
                    val updatedList = uiState.value.blockedUsers + newBlockedUser
                    val updatedFriends = uiState.value.friends.filter { it.friendId != targetUserId }
                    uiState.value = uiState.value.copy(
                        blockedUserIds = updatedIds,
                        blockedUsers = updatedList,
                        friends = updatedFriends,
                        searchQuery = "",
                        searchResults = emptyList(),
                        error = null
                    )
                    showSuccess("User blocked and removed from friends.")
                },
                onFailure = {
                    uiState.value = uiState.value.copy(error = "Failed to block user. Please try again.")
                }
            )
        }
    }

    fun blockUser(targetUserId: String) {
        val userId = getCurrentUserId() ?: return
        scope.launch {
            friendsRepository.blockUser(userId, UserId(targetUserId)).fold(
                onSuccess = {
                    AuditLogger.logUserBlocked(userId.value, targetUserId)
                    val updated = uiState.value.blockedUserIds + targetUserId
                    uiState.value = uiState.value.copy(
                        blockedUserIds = updated,
                        error = null
                    )
                    showSuccess("User blocked.")
                },
                onFailure = {
                    uiState.value = uiState.value.copy(error = "Failed to block user. Please try again.")
                }
            )
        }
    }

    fun unblockUser(targetUserId: String) {
        val userId = getCurrentUserId() ?: return
        scope.launch {
            friendsRepository.unblockUser(userId, UserId(targetUserId)).fold(
                onSuccess = {
                    AuditLogger.logUserUnblocked(userId.value, targetUserId)
                    val updatedIds = uiState.value.blockedUserIds - targetUserId
                    val updatedList = uiState.value.blockedUsers.filter { it.userId != targetUserId }
                    uiState.value = uiState.value.copy(
                        blockedUserIds = updatedIds,
                        blockedUsers = updatedList,
                        error = null
                    )
                    showSuccess("User unblocked.")
                },
                onFailure = {
                    uiState.value = uiState.value.copy(error = "Failed to unblock user. Please try again.")
                }
            )
        }
    }
}
