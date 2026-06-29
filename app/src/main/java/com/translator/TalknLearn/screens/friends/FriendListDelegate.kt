package com.translator.TalknLearn.screens.friends

import com.translator.TalknLearn.core.ErrorMessages
import com.translator.TalknLearn.core.security.AuditLogger
import com.translator.TalknLearn.data.friends.FriendsRepository
import com.translator.TalknLearn.data.friends.SharedFriendsDataSource
import com.translator.TalknLearn.domain.friends.RemoveFriendUseCase
import com.translator.TalknLearn.model.UserId
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

/**
 * Manages delete-mode (multi-select friend removal), single removal, and friend-list refresh.
 * Extracted from [FriendsViewModel] for maintainability.
 */
internal class FriendListDelegate(
    private val uiState: MutableStateFlow<FriendsUiState>,
    private val scope: CoroutineScope,
    private val friendsRepository: FriendsRepository,
    private val sharedFriendsDataSource: SharedFriendsDataSource,
    private val removeFriendUseCase: RemoveFriendUseCase,
    private val getCurrentUserId: () -> UserId?,
    private val showSuccess: (String) -> Unit
) {
    fun toggleDeleteMode() {
        val state = uiState.value
        if (state.isDeleteMode) {
            if (state.selectedFriendIds.isEmpty()) {
                uiState.value = state.copy(isDeleteMode = false)
            }
            // If there are selections, the UI shows the confirm dialog — don't exit here
        } else {
            uiState.value = state.copy(isDeleteMode = true, selectedFriendIds = emptySet())
        }
    }

    fun toggleFriendSelection(friendId: String) {
        val selected = uiState.value.selectedFriendIds.toMutableSet()
        if (selected.contains(friendId)) selected.remove(friendId) else selected.add(friendId)
        uiState.value = uiState.value.copy(selectedFriendIds = selected)
    }

    fun exitDeleteMode() {
        uiState.value = uiState.value.copy(isDeleteMode = false, selectedFriendIds = emptySet())
    }

    fun removeSelectedFriends() {
        val userId = getCurrentUserId() ?: return
        val toDelete = uiState.value.selectedFriendIds.toList()
        if (toDelete.isEmpty()) return
        scope.launch {
            val previousFriends = uiState.value.friends
            // Optimistically update the in-memory friends list immediately
            val updatedFriends = uiState.value.friends.filter { it.friendId !in toDelete }
            uiState.value = uiState.value.copy(
                isDeleteMode = false,
                selectedFriendIds = emptySet(),
                friends = updatedFriends,
                searchQuery = "",
                searchResults = emptyList()
            )
            val failedRemovals = mutableSetOf<String>()
            toDelete.forEach { friendId ->
                removeFriendUseCase(userId, UserId(friendId)).onFailure {
                    failedRemovals += friendId
                }
            }

            if (failedRemovals.isNotEmpty()) {
                val restoredFriends = previousFriends.filter { it.friendId in failedRemovals }
                uiState.value = uiState.value.copy(
                    friends = uiState.value.friends + restoredFriends,
                    error = ErrorMessages.FRIEND_REMOVE_FAILED
                )
            } else {
                uiState.value = uiState.value.copy(error = null)
                showSuccess("Friend(s) removed.")
            }

            refreshFriendsList()
        }
    }

    fun removeFriend(friendId: String) {
        val userId = getCurrentUserId() ?: return
        scope.launch {
            val previousFriends = uiState.value.friends
            // Optimistically remove from in-memory list immediately
            val updatedFriends = previousFriends.filter { it.friendId != friendId }
            uiState.value = uiState.value.copy(
                friends = updatedFriends,
                searchQuery = "",
                searchResults = emptyList()
            )
            removeFriendUseCase(userId, UserId(friendId)).fold(
                onSuccess = {
                    AuditLogger.logFriendRemoved(userId.value, friendId)
                    uiState.value = uiState.value.copy(error = null)
                    showSuccess(ErrorMessages.FRIEND_REMOVED)
                    refreshFriendsList()
                },
                onFailure = { e ->
                    uiState.value = uiState.value.copy(
                        friends = previousFriends,
                        error = ErrorMessages.fromException(e, ErrorMessages.FRIEND_REMOVE_FAILED)
                    )
                }
            )
        }
    }

    fun refreshFriendsList() {
        val userId = getCurrentUserId()?.value ?: return
        scope.launch {
            sharedFriendsDataSource.stopObserving()
            sharedFriendsDataSource.startObserving(userId)
            val updates = friendsRepository.syncFriendUsernames(UserId(userId))
            if (updates.isNotEmpty()) {
                sharedFriendsDataSource.applyUsernameUpdates(updates)
            }
        }
    }
}
