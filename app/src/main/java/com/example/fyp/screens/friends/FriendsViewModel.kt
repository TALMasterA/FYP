package com.example.fyp.screens.friends

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fyp.data.friends.ChatRepository
import com.example.fyp.data.friends.SharedFriendsDataSource
import com.example.fyp.data.user.FirebaseAuthRepository
import com.example.fyp.domain.friends.*
import com.example.fyp.model.UserId
import com.example.fyp.model.friends.FriendRelation
import com.example.fyp.model.friends.FriendRequest
import com.example.fyp.model.friends.PublicUserProfile
import com.example.fyp.model.user.AuthState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * UI state for the Friends screen.
 */
data class FriendsUiState(
    val isLoading: Boolean = true,
    val friends: List<FriendRelation> = emptyList(),
    val incomingRequests: List<FriendRequest> = emptyList(),
    val outgoingRequests: List<FriendRequest> = emptyList(),
    val searchQuery: String = "",
    val searchResults: List<PublicUserProfile> = emptyList(),
    val isSearching: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null,
    val newRequestCount: Int = 0,   // For snackbar notification badge
    val unreadCountPerFriend: Map<String, Int> = emptyMap(), // friendId -> unread message count
    // Multi-select delete mode
    val isDeleteMode: Boolean = false,
    val selectedFriendIds: Set<String> = emptySet()
)

/**
 * OPTIMIZED: Friends list and incoming requests are read from [SharedFriendsDataSource]
 * (shared single-listener data source) instead of creating new Firestore listeners.
 * Outgoing requests still have their own listener (only needed on this screen).
 * Per-friend unread counts are observed via ChatRepository.observeChatMetadata().
 */
@HiltViewModel
class FriendsViewModel @Inject constructor(
    private val authRepo: FirebaseAuthRepository,
    private val sharedFriendsDataSource: SharedFriendsDataSource,
    private val chatRepository: ChatRepository,
    private val friendsRepository: com.example.fyp.data.friends.FriendsRepository,
    private val observeOutgoingRequestsUseCase: ObserveOutgoingRequestsUseCase,
    private val searchUsersUseCase: SearchUsersUseCase,
    private val sendFriendRequestUseCase: SendFriendRequestUseCase,
    private val acceptFriendRequestUseCase: AcceptFriendRequestUseCase,
    private val rejectFriendRequestUseCase: RejectFriendRequestUseCase,
    private val cancelFriendRequestUseCase: CancelFriendRequestUseCase,
    private val removeFriendUseCase: RemoveFriendUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(FriendsUiState())
    val uiState: StateFlow<FriendsUiState> = _uiState.asStateFlow()

    private var outgoingRequestsJob: Job? = null
    private var currentUserId: UserId? = null
    private var previousIncomingCount = 0

    // Per-friend chat unread observers: friendId -> Job
    private val unreadJobs = mutableMapOf<String, Job>()
    // Single document unread observer (replaces N per-chat observers)
    private var unreadPerFriendJob: Job? = null

    init {
        viewModelScope.launch {
            authRepo.currentUserState.collect { auth ->
                when (auth) {
                    is AuthState.LoggedIn -> {
                        currentUserId = UserId(auth.user.uid)
                        // Ensure shared data source is running (idempotent)
                        sharedFriendsDataSource.startObserving(auth.user.uid)
                        subscribeToSharedData()
                        startOutgoingRequestsObserver(UserId(auth.user.uid))
                        startUnreadPerFriendObserver(UserId(auth.user.uid))
                    }
                    AuthState.LoggedOut -> {
                        outgoingRequestsJob?.cancel()
                        unreadJobs.values.forEach { it.cancel() }
                        unreadJobs.clear()
                        unreadPerFriendJob?.cancel()
                        currentUserId = null
                        previousIncomingCount = 0
                        _uiState.value = FriendsUiState(isLoading = false)
                    }
                    AuthState.Loading -> {
                        _uiState.value = _uiState.value.copy(isLoading = true)
                    }
                }
            }
        }
    }

    /** Mirror shared-data-source flows into UI state. */
    private fun subscribeToSharedData() {
        // Friends list
        viewModelScope.launch {
            sharedFriendsDataSource.friends.collect { friends ->
                _uiState.value = _uiState.value.copy(friends = friends, isLoading = false)
            }
        }
        // Incoming requests — also tracks new-request notification
        viewModelScope.launch {
            sharedFriendsDataSource.incomingRequests.collect { requests ->
                val newCount = if (previousIncomingCount > 0 && requests.size > previousIncomingCount) {
                    requests.size - previousIncomingCount
                } else {
                    0
                }
                previousIncomingCount = requests.size
                _uiState.value = _uiState.value.copy(
                    incomingRequests = requests,
                    newRequestCount = newCount
                )
            }
        }
    }

    /**
     * OPTIMIZED (Medium #5): Single real-time listener on user document
     * instead of one listener per friend chat metadata document.
     * Observes users/{userId}.unreadPerFriend map for O(1) connections.
     */
    private fun startUnreadPerFriendObserver(userId: UserId) {
        unreadPerFriendJob?.cancel()
        unreadPerFriendJob = viewModelScope.launch {
            chatRepository.observeUnreadPerFriend(userId).collect { unreadMap ->
                _uiState.value = _uiState.value.copy(unreadCountPerFriend = unreadMap)
            }
        }
    }

    private fun startOutgoingRequestsObserver(userId: UserId) {
        outgoingRequestsJob?.cancel()
        outgoingRequestsJob = viewModelScope.launch {
            observeOutgoingRequestsUseCase(userId).collect { requests ->
                _uiState.value = _uiState.value.copy(outgoingRequests = requests)
            }
        }
    }

    // ── Search ───────────────────────────────────────────────────────────────

    private var searchJob: Job? = null

    fun onSearchQueryChange(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        searchJob?.cancel()
        val trimmed = query.trim()

        if (trimmed.length >= 3) {
            searchJob = viewModelScope.launch {
                delay(500) // OPTIMIZATION: Increased from 300ms to reduce Firestore queries
                performCombinedSearch(trimmed)
            }
        } else {
            _uiState.value = _uiState.value.copy(searchResults = emptyList(), isSearching = false)
        }
    }

    private suspend fun performCombinedSearch(query: String) {
        _uiState.value = _uiState.value.copy(isSearching = true)

        // Parallel execution: Search by Username AND by UserID (if applicable)
        val (usernameResults, idResult) = coroutineScope {
            val usernameSearchDeferred = async {
                searchUsersUseCase(query).getOrElse { emptyList() }
            }

            val idSearchDeferred = if (!query.contains(' ')) {
                async {
                    try {
                        friendsRepository.findByUserId(UserId(query))
                    } catch (_: Exception) {
                        null
                    }
                }
            } else null

            Pair(usernameSearchDeferred.await(), idSearchDeferred?.await())
        }

        // Combine results, prioritizing ID match at the top if found
        val distinctResults = (listOfNotNull(idResult) + usernameResults)
            .distinctBy { it.uid }

        _uiState.value = _uiState.value.copy(
            searchResults = distinctResults,
            isSearching = false,
            error = null
        )
    }

    // ── Delete mode ──────────────────────────────────────────────────────────

    fun toggleDeleteMode() {
        val state = _uiState.value
        if (state.isDeleteMode) {
            if (state.selectedFriendIds.isEmpty()) {
                // Exit delete mode without doing anything
                _uiState.value = state.copy(isDeleteMode = false)
            }
            // If there are selections, the UI shows the confirm dialog — don't exit here
        } else {
            _uiState.value = state.copy(isDeleteMode = true, selectedFriendIds = emptySet())
        }
    }

    fun toggleFriendSelection(friendId: String) {
        val selected = _uiState.value.selectedFriendIds.toMutableSet()
        if (selected.contains(friendId)) selected.remove(friendId) else selected.add(friendId)
        _uiState.value = _uiState.value.copy(selectedFriendIds = selected)
    }

    fun exitDeleteMode() {
        _uiState.value = _uiState.value.copy(isDeleteMode = false, selectedFriendIds = emptySet())
    }

    fun removeSelectedFriends() {
        val userId = currentUserId ?: return
        val toDelete = _uiState.value.selectedFriendIds.toList()
        if (toDelete.isEmpty()) return
        viewModelScope.launch {
            // Optimistically update the in-memory friends list immediately so
            // canSendRequestTo() reflects the removal before the Firestore listener fires.
            val updatedFriends = _uiState.value.friends.filter { it.friendId !in toDelete }
            _uiState.value = _uiState.value.copy(
                isDeleteMode = false,
                selectedFriendIds = emptySet(),
                friends = updatedFriends,
                searchQuery = "",
                searchResults = emptyList()
            )
            toDelete.forEach { friendId ->
                removeFriendUseCase(userId, UserId(friendId))
            }
            _uiState.value = _uiState.value.copy(
                successMessage = "Friend(s) removed. Pull down to refresh before adding them again.",
                error = null
            )
        }
    }

    // ── Friend request actions ────────────────────────────────────────────────

    fun sendFriendRequest(toUserId: String) {
        val fromUserId = currentUserId ?: return
        viewModelScope.launch {
            sendFriendRequestUseCase(fromUserId, UserId(toUserId)).fold(
                onSuccess = { _uiState.value = _uiState.value.copy(successMessage = "Friend request sent!", error = null) },
                onFailure = { _uiState.value = _uiState.value.copy(error = it.message ?: "Failed to send request") }
            )
        }
    }

    fun acceptFriendRequest(requestId: String) {
        val userId = currentUserId ?: return
        val friendUserId = _uiState.value.incomingRequests
            .firstOrNull { it.requestId == requestId }
            ?.fromUserId
            ?.let { UserId(it) } ?: return
        viewModelScope.launch {
            acceptFriendRequestUseCase(requestId, userId, friendUserId).fold(
                onSuccess = { _uiState.value = _uiState.value.copy(successMessage = "Friend request accepted!", error = null) },
                onFailure = { _uiState.value = _uiState.value.copy(error = it.message ?: "Failed to accept request") }
            )
        }
    }

    fun rejectFriendRequest(requestId: String) {
        viewModelScope.launch {
            rejectFriendRequestUseCase(requestId).fold(
                onSuccess = { _uiState.value = _uiState.value.copy(successMessage = "Request rejected", error = null) },
                onFailure = { _uiState.value = _uiState.value.copy(error = it.message ?: "Failed to reject request") }
            )
        }
    }

    fun cancelFriendRequest(requestId: String) {
        viewModelScope.launch {
            cancelFriendRequestUseCase(requestId).fold(
                onSuccess = { _uiState.value = _uiState.value.copy(successMessage = "Friend request cancelled", error = null) },
                onFailure = { _uiState.value = _uiState.value.copy(error = it.message ?: "Failed to cancel request") }
            )
        }
    }

    fun removeFriend(friendId: String) {
        val userId = currentUserId ?: return
        viewModelScope.launch {
            // Optimistically remove from in-memory list immediately
            val updatedFriends = _uiState.value.friends.filter { it.friendId != friendId }
            _uiState.value = _uiState.value.copy(
                friends = updatedFriends,
                searchQuery = "",
                searchResults = emptyList()
            )
            removeFriendUseCase(userId, UserId(friendId)).fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(
                        successMessage = "Friend removed. Pull down to refresh before adding them again.",
                        error = null
                    )
                },
                onFailure = {
                    // On failure, we should reload from the data source to restore correct state
                    _uiState.value = _uiState.value.copy(
                        error = it.message ?: "Failed to remove friend"
                    )
                }
            )
        }
    }

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(error = null, successMessage = null)
    }

    fun clearNewRequestCount() {
        _uiState.value = _uiState.value.copy(newRequestCount = 0)
    }

    /**
     * Manually refresh friends list by restarting the shared data source.
     * This helps ensure the latest state after operations like removing friends.
     */
    fun refreshFriendsList() {
        val userId = currentUserId?.value ?: return
        viewModelScope.launch {
            sharedFriendsDataSource.stopObserving()
            sharedFriendsDataSource.startObserving(userId)
        }
    }

    /**
     * Check if a user can receive a friend request from current user.
     * Returns true if they are NOT friends and NO pending request exists.
     */
    fun canSendRequestTo(userId: String): Boolean {
        val state = _uiState.value
        if (state.friends.any { it.friendId == userId }) return false
        if (state.outgoingRequests.any { it.toUserId == userId }) return false
        if (state.incomingRequests.any { it.fromUserId == userId }) return false
        return true
    }
}
