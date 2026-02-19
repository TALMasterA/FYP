package com.example.fyp.screens.friends

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    val newRequestCount: Int = 0  // For snackbar notification badge
)

/**
 * OPTIMIZED: Friends list and incoming requests are read from [SharedFriendsDataSource]
 * (shared single-listener data source) instead of creating new Firestore listeners.
 * Outgoing requests still have their own listener (only needed on this screen).
 */
@HiltViewModel
class FriendsViewModel @Inject constructor(
    private val authRepo: FirebaseAuthRepository,
    private val sharedFriendsDataSource: SharedFriendsDataSource,
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
                    }
                    AuthState.LoggedOut -> {
                        outgoingRequestsJob?.cancel()
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
        if (query.length >= 2) {
            searchJob = viewModelScope.launch {
                kotlinx.coroutines.delay(300)
                searchUsers(query)
            }
        } else {
            _uiState.value = _uiState.value.copy(searchResults = emptyList(), isSearching = false)
        }
    }

    private fun searchUsers(query: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSearching = true)
            try {
                val results = searchUsersUseCase(query)
                _uiState.value = _uiState.value.copy(searchResults = results, isSearching = false, error = null)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isSearching = false, error = "Search failed: ${e.message}")
            }
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
        viewModelScope.launch {
            acceptFriendRequestUseCase(requestId, userId).fold(
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
            removeFriendUseCase(userId, UserId(friendId)).fold(
                onSuccess = { _uiState.value = _uiState.value.copy(successMessage = "Friend removed", error = null) },
                onFailure = { _uiState.value = _uiState.value.copy(error = it.message ?: "Failed to remove friend") }
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
