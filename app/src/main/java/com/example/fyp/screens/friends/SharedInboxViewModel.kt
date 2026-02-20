package com.example.fyp.screens.friends

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fyp.data.friends.SharedFriendsDataSource
import com.example.fyp.data.user.FirebaseAuthRepository
import com.example.fyp.domain.friends.AcceptSharedItemUseCase
import com.example.fyp.domain.friends.DismissSharedItemUseCase
import com.example.fyp.model.UserId
import com.example.fyp.model.friends.SharedItem
import com.example.fyp.model.user.AuthState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SharedInboxUiState(
    val isLoading: Boolean = true,
    val sharedItems: List<SharedItem> = emptyList(),
    val error: String? = null,
    val successMessage: String? = null,
    val isProcessing: Boolean = false,
    val newItemCount: Int = 0,          // items that arrived since screen was last opened
    val newItemIds: Set<String> = emptySet() // IDs of items that are new/unread (red dot)
)

/**
 * OPTIMIZED: Reads shared inbox from [SharedFriendsDataSource] (shared single-listener)
 * instead of creating its own Firestore listener, saving one read stream.
 */
@HiltViewModel
class SharedInboxViewModel @Inject constructor(
    private val authRepository: FirebaseAuthRepository,
    private val sharedFriendsDataSource: SharedFriendsDataSource,
    private val acceptSharedItemUseCase: AcceptSharedItemUseCase,
    private val dismissSharedItemUseCase: DismissSharedItemUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SharedInboxUiState())
    val uiState: StateFlow<SharedInboxUiState> = _uiState.asStateFlow()

    private var currentUserId: UserId? = null
    // Track item count when screen was last "seen" to compute new arrivals
    private var seenCount: Int = -1
    // Track IDs seen at last markItemsAsSeen() call
    private var seenItemIds: Set<String> = emptySet()

    init {
        viewModelScope.launch {
            authRepository.currentUserState.collect { auth ->
                when (auth) {
                    is AuthState.LoggedIn -> {
                        currentUserId = UserId(auth.user.uid)
                        // Ensure shared source is running (idempotent)
                        sharedFriendsDataSource.startObserving(auth.user.uid)
                        // Mirror shared state into local UI state
                        launch {
                            sharedFriendsDataSource.pendingSharedItems.collect { items ->
                                val currentIds = items.map { it.itemId }.toSet()
                                val newCount = when {
                                    seenCount < 0 -> 0          // first load — nothing "new" yet
                                    items.size > seenCount -> items.size - seenCount
                                    else -> _uiState.value.newItemCount
                                }
                                // Compute new item IDs: items not in the last seen set
                                val newIds = if (seenCount < 0) {
                                    emptySet() // first load — nothing "new" yet
                                } else {
                                    currentIds - seenItemIds
                                }
                                if (seenCount < 0) {
                                    seenCount = items.size
                                    seenItemIds = currentIds
                                }
                                _uiState.update {
                                    it.copy(
                                        isLoading = false,
                                        sharedItems = items,
                                        error = null,
                                        newItemCount = newCount,
                                        newItemIds = it.newItemIds + newIds
                                    )
                                }
                            }
                        }
                    }
                    is AuthState.LoggedOut -> {
                        currentUserId = null
                        seenCount = -1
                        seenItemIds = emptySet()
                        _uiState.update { SharedInboxUiState() }
                    }
                    is AuthState.Loading -> Unit
                }
            }
        }
    }

    /** Call when the user opens / views the inbox to reset the "new" badge. */
    fun markItemsAsSeen() {
        val current = _uiState.value.sharedItems
        seenCount = current.size
        seenItemIds = current.map { it.itemId }.toSet()
        _uiState.update { it.copy(newItemCount = 0, newItemIds = emptySet()) }
    }

    fun acceptItem(itemId: String) {
        val userId = currentUserId ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isProcessing = true, error = null) }
            acceptSharedItemUseCase(itemId, userId)
                .onSuccess {
                    _uiState.update { it.copy(isProcessing = false, successMessage = "Added to your collection") }
                    kotlinx.coroutines.delay(3000)
                    _uiState.update { it.copy(successMessage = null) }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isProcessing = false, error = error.message ?: "Failed to accept item") }
                }
        }
    }

    fun deleteItem(itemId: String) {
        val userId = currentUserId ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isProcessing = true, error = null) }
            dismissSharedItemUseCase(itemId, userId)
                .onSuccess {
                    _uiState.update { it.copy(isProcessing = false, successMessage = "Item deleted") }
                    kotlinx.coroutines.delay(3000)
                    _uiState.update { it.copy(successMessage = null) }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isProcessing = false, error = error.message ?: "Failed to delete item") }
                }
        }
    }

    fun dismissItem(itemId: String) {
        val userId = currentUserId ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isProcessing = true, error = null) }
            dismissSharedItemUseCase(itemId, userId)
                .onSuccess {
                    _uiState.update { it.copy(isProcessing = false, successMessage = "Item dismissed") }
                    kotlinx.coroutines.delay(3000)
                    _uiState.update { it.copy(successMessage = null) }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isProcessing = false, error = error.message ?: "Failed to dismiss item") }
                }
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(error = null, successMessage = null) }
    }
}

