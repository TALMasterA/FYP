package com.example.fyp.screens.friends

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    val newRequestCount: Int = 0  // For notification badge
)

@HiltViewModel
class FriendsViewModel @Inject constructor(
    private val authRepo: FirebaseAuthRepository,
    private val observeFriendsUseCase: ObserveFriendsUseCase,
    private val observeIncomingRequestsUseCase: ObserveIncomingRequestsUseCase,
    private val observeOutgoingRequestsUseCase: ObserveOutgoingRequestsUseCase,
    private val searchUsersUseCase: SearchUsersUseCase,
    private val sendFriendRequestUseCase: SendFriendRequestUseCase,
    private val acceptFriendRequestUseCase: AcceptFriendRequestUseCase,
    private val rejectFriendRequestUseCase: RejectFriendRequestUseCase,
    private val removeFriendUseCase: RemoveFriendUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(FriendsUiState())
    val uiState: StateFlow<FriendsUiState> = _uiState.asStateFlow()

    private var friendsJob: Job? = null
    private var incomingRequestsJob: Job? = null
    private var outgoingRequestsJob: Job? = null
    private var currentUserId: UserId? = null
    private var previousIncomingCount = 0

    init {
        viewModelScope.launch {
            authRepo.currentUserState.collect { auth ->
                when (auth) {
                    is AuthState.LoggedIn -> {
                        currentUserId = UserId(auth.user.uid)
                        loadFriendsAndRequests(UserId(auth.user.uid))
                    }
                    AuthState.LoggedOut -> {
                        friendsJob?.cancel()
                        incomingRequestsJob?.cancel()
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

    private fun loadFriendsAndRequests(userId: UserId) {
        // Observe friends list
        friendsJob?.cancel()
        friendsJob = viewModelScope.launch {
            observeFriendsUseCase(userId).collect { friends ->
                _uiState.value = _uiState.value.copy(
                    friends = friends,
                    isLoading = false
                )
            }
        }

        // Observe incoming requests
        incomingRequestsJob?.cancel()
        incomingRequestsJob = viewModelScope.launch {
            observeIncomingRequestsUseCase(userId).collect { requests ->
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

        // Observe outgoing requests
        outgoingRequestsJob?.cancel()
        outgoingRequestsJob = viewModelScope.launch {
            observeOutgoingRequestsUseCase(userId).collect { requests ->
                _uiState.value = _uiState.value.copy(
                    outgoingRequests = requests
                )
            }
        }
    }

    private var searchJob: Job? = null

    fun onSearchQueryChange(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        
        // Cancel previous search
        searchJob?.cancel()
        
        if (query.length >= 2) {
            // Debounce: wait 300ms before searching
            searchJob = viewModelScope.launch {
                kotlinx.coroutines.delay(300)
                searchUsers(query)
            }
        } else {
            _uiState.value = _uiState.value.copy(
                searchResults = emptyList(),
                isSearching = false
            )
        }
    }

    private fun searchUsers(query: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSearching = true)
            try {
                val results = searchUsersUseCase(query)
                _uiState.value = _uiState.value.copy(
                    searchResults = results,
                    isSearching = false,
                    error = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSearching = false,
                    error = "Search failed: ${e.message}"
                )
            }
        }
    }

    fun sendFriendRequest(toUserId: String) {
        val fromUserId = currentUserId ?: return
        
        viewModelScope.launch {
            val result = sendFriendRequestUseCase(fromUserId, UserId(toUserId))
            result.fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(
                        successMessage = "Friend request sent!",
                        error = null
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        error = error.message ?: "Failed to send request"
                    )
                }
            )
        }
    }

    fun acceptFriendRequest(requestId: String) {
        val userId = currentUserId ?: return
        
        viewModelScope.launch {
            val result = acceptFriendRequestUseCase(requestId, userId)
            result.fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(
                        successMessage = "Friend request accepted!",
                        error = null
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        error = error.message ?: "Failed to accept request"
                    )
                }
            )
        }
    }

    fun rejectFriendRequest(requestId: String) {
        viewModelScope.launch {
            val result = rejectFriendRequestUseCase(requestId)
            result.fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(
                        successMessage = "Request rejected",
                        error = null
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        error = error.message ?: "Failed to reject request"
                    )
                }
            )
        }
    }

    fun removeFriend(friendId: String) {
        val userId = currentUserId ?: return
        
        viewModelScope.launch {
            val result = removeFriendUseCase(userId, UserId(friendId))
            result.fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(
                        successMessage = "Friend removed",
                        error = null
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        error = error.message ?: "Failed to remove friend"
                    )
                }
            )
        }
    }

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(
            error = null,
            successMessage = null
        )
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

        // Check if already friends
        if (state.friends.any { it.friendId == userId }) {
            return false
        }

        // Check if there's a pending outgoing request
        if (state.outgoingRequests.any { it.toUserId == userId }) {
            return false
        }

        // Check if there's a pending incoming request
        if (state.incomingRequests.any { it.fromUserId == userId }) {
            return false
        }

        return true
    }
}
