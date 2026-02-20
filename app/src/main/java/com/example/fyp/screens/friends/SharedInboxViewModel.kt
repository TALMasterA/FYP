package com.example.fyp.screens.friends

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fyp.data.friends.SharedFriendsDataSource
import com.example.fyp.data.friends.SharingRepository
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
    val newItemIds: Set<String> = emptySet(), // IDs of items that are new/unread (red dot)
    val fullContentMap: Map<String, String> = emptyMap() // itemId -> full content text
)

/**
 * OPTIMIZED: Reads shared inbox from [SharedFriendsDataSource] (shared single-listener)
 * instead of creating its own Firestore listener, saving one read stream.
 */
@HiltViewModel
class SharedInboxViewModel @Inject constructor(
    private val authRepository: FirebaseAuthRepository,
    private val sharedFriendsDataSource: SharedFriendsDataSource,
    private val sharingRepository: SharingRepository,
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
                                val isFirstLoad = seenCount < 0
                                // On first load, ALL pending items are "new/unread" since the
                                // user hasn't opened the inbox yet in this session.
                                val newIds = if (isFirstLoad) {
                                    currentIds // all items unseen on first load
                                } else {
                                    currentIds - seenItemIds // only items not seen before
                                }
                                val newCount = newIds.size
                                if (isFirstLoad) {
                                    // Don't set seenItemIds here — wait for markItemsAsSeen()
                                    seenCount = 0 // mark as "past first load" but nothing seen yet
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
        val currentIds = current.map { it.itemId }.toSet()
        seenCount = current.size
        seenItemIds = currentIds
        _uiState.update { it.copy(newItemCount = 0, newItemIds = emptySet()) }
        // Also clear the notification badge that lives in SharedFriendsDataSource
        sharedFriendsDataSource.markSharedItemsSeen()
    }

    /** Call when the user opens a specific shared item detail to clear its red dot. */
    fun markItemSeen(itemId: String) {
        _uiState.update { it.copy(newItemIds = it.newItemIds - itemId) }
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

    /**
     * Fetch the full text content of a shared learning material from the
     * sub-document (stored separately to keep the main doc small).
     * Results are cached in uiState.fullContentMap so subsequent navigations are instant.
     * Stores an empty string if the sub-document doesn't exist so the loading
     * spinner stops and the UI falls back to the description preview.
     */
    fun loadFullContent(itemId: String) {
        val userId = currentUserId ?: return
        // Already cached — no need to re-fetch
        if (_uiState.value.fullContentMap.containsKey(itemId)) return
        viewModelScope.launch {
            val content = sharingRepository.fetchSharedItemFullContent(userId, itemId)
            // Store result (even if null/empty) so we don't show infinite spinner.
            // Falls back to description preview in SharedMaterialDetailScreen.
            _uiState.update {
                it.copy(fullContentMap = it.fullContentMap + (itemId to (content ?: "")))
            }
        }
    }
}

