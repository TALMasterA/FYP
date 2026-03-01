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
import com.example.fyp.core.security.ValidationResult
import com.example.fyp.core.security.sanitizeInput
import com.example.fyp.core.security.validateTextLength
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
    val newRequestCount: Int = 0,
    val unreadCountPerFriend: Map<String, Int> = emptyMap(),
    val isDeleteMode: Boolean = false,
    val selectedFriendIds: Set<String> = emptySet(),
    val currentUserHasUsername: Boolean = false,
    /** IDs the current user has blocked. */
    val blockedUserIds: Set<String> = emptySet(),
    /** Full blocked-user records (with usernames) for the Blocked Users screen. */
    val blockedUsers: List<com.example.fyp.data.friends.BlockedUser> = emptyList()
)

/** Status of the local user's relationship with a search result. */
enum class RequestStatus {
    NONE,             // No connection — can send request
    ALREADY_FRIENDS,  // Already friends
    REQUEST_SENT,     // Current user sent a pending request
    REQUEST_RECEIVED  // The other user already sent a request to current user
}

/** Maximum pending outgoing friend requests a user can have at any one time. */
private const val MAX_PENDING_REQUESTS = 20

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

    // Single document unread observer
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
                        // Load own profile to determine if username is set
                        loadOwnUsername(auth.user.uid)
                        // Load blocked users list
                        loadBlockedUsers(UserId(auth.user.uid))
                    }
                    AuthState.LoggedOut -> {
                        outgoingRequestsJob?.cancel()
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

    /**
     * Check if the current user has a username set.
     * First checks the in-memory cache; falls back to a Firestore read if not cached.
     */
    private fun loadOwnUsername(userId: String) {
        // Check in-memory cache first (populated when MyProfileScreen was opened)
        val cached = sharedFriendsDataSource.getCachedUsername(userId)
        if (!cached.isNullOrBlank()) {
            _uiState.value = _uiState.value.copy(currentUserHasUsername = true)
            return
        }
        viewModelScope.launch {
            try {
                val profile = friendsRepository.getPublicProfile(UserId(userId))
                val username = profile?.username.orEmpty()
                val hasUsername = username.isNotBlank()
                if (hasUsername) {
                    sharedFriendsDataSource.cacheOwnUsername(userId, username)
                }
                _uiState.value = _uiState.value.copy(currentUserHasUsername = hasUsername)
            } catch (_: Exception) {
                // Non-fatal; user will see an error when they try to send a request
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
                android.util.Log.d("FriendsViewModel", "Unread per friend updated: $unreadMap")
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

        val caller = currentUserId

        // Parallel execution: Search by Username AND by UserID (if applicable)
        val (usernameResults, idResult) = coroutineScope {
            val usernameSearchDeferred = async {
                searchUsersUseCase(query, callerUserId = caller).getOrElse { emptyList() }
            }

            val idSearchDeferred = if (!query.contains(' ')) {
                async {
                    try {
                        friendsRepository.findByUserId(UserId(query), callerUserId = caller)
                    } catch (_: Exception) {
                        null
                    }
                }
            } else null

            Pair(usernameSearchDeferred.await(), idSearchDeferred?.await())
        }

        // Combine results, prioritizing ID match at the top if found
        // Also filter out the current user from search results
        val currentUid = currentUserId?.value
        val distinctResults = (listOfNotNull(idResult) + usernameResults)
            .distinctBy { it.uid }
            .filter { it.uid != currentUid }

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
                successMessage = "Friend(s) removed.",
                error = null
            )
            // Refresh to sync Firestore state so re-search shows correct status
            refreshFriendsList()
        }
    }

    // ── Friend request actions ────────────────────────────────────────────────

    /**
     * Surfaces a clear error when the user tries to open the Add Friends dialog
     * without having set a username yet.
     */
    fun requireUsernameForAddFriends(): Boolean {
        return if (!_uiState.value.currentUserHasUsername) {
            _uiState.value = _uiState.value.copy(
                error = "Please set a username in your profile before sending friend requests."
            )
            false
        } else {
            true
        }
    }

    fun sendFriendRequest(toUserId: String, note: String = "") {
        val fromUserId = currentUserId ?: return
        // Require a username before sending any request so the recipient sees a real name
        if (!_uiState.value.currentUserHasUsername) {
            _uiState.value = _uiState.value.copy(
                error = "Please set a username in your profile before sending friend requests."
            )
            return
        }
        // Client-side rate limit: max MAX_PENDING_REQUESTS pending outgoing requests
        if (_uiState.value.outgoingRequests.size >= MAX_PENDING_REQUESTS) {
            _uiState.value = _uiState.value.copy(
                error = "You have reached the maximum of 20 pending friend requests. " +
                        "Please wait for some to be accepted or cancel them before sending more."
            )
            return
        }

        // Validate note length (max 200 chars)
        val noteValidation = validateTextLength(note, minLength = 0, maxLength = 200, fieldName = "Note")
        if (noteValidation is ValidationResult.Invalid) {
            _uiState.value = _uiState.value.copy(error = noteValidation.message)
            return
        }

        // Sanitize the note
        val sanitizedNote = sanitizeInput(note)

        viewModelScope.launch {
            sendFriendRequestUseCase(fromUserId, UserId(toUserId), sanitizedNote).fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(
                        successMessage = "Friend request sent! They will be notified.",
                        error = null,
                        // Clear search so the outgoing-requests listener re-evaluates status
                        searchResults = emptyList(),
                        searchQuery = ""
                    )
                },
                onFailure = { e ->
                    val message = when {
                        e.message?.contains("Already friends", ignoreCase = true) == true ->
                            "You are already friends with this user."
                        e.message?.contains("already sent", ignoreCase = true) == true ->
                            "You already have a pending request to this user. Please wait for their reply."
                        e.message?.contains("profile not found", ignoreCase = true) == true ->
                            "Could not find the user's profile. They may have deleted their account."
                        e.message?.contains("blocked this user", ignoreCase = true) == true ->
                            "You have blocked this user. Unblock them first."
                        e.message?.contains("Unable to send", ignoreCase = true) == true ->
                            "Unable to send friend request."
                        else -> "Failed to send friend request. Please try again."
                    }
                    _uiState.value = _uiState.value.copy(error = message)
                }
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
                onSuccess = {
                    _uiState.value = _uiState.value.copy(
                        successMessage = "Friend request accepted! You are now friends.",
                        error = null
                    )
                },
                onFailure = { e ->
                    val message = when {
                        e.message?.contains("not found", ignoreCase = true) == true ->
                            "This request no longer exists — it may have been cancelled."
                        e.message?.contains("not authorized", ignoreCase = true) == true ->
                            "You are not authorized to accept this request."
                        else -> "Failed to accept request. Please try again."
                    }
                    _uiState.value = _uiState.value.copy(error = message)
                }
            )
        }
    }

    fun rejectFriendRequest(requestId: String) {
        viewModelScope.launch {
            rejectFriendRequestUseCase(requestId).fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(
                        successMessage = "Request declined.",
                        error = null
                    )
                },
                onFailure = { e ->
                    val message = when {
                        e.message?.contains("not found", ignoreCase = true) == true ->
                            "This request no longer exists."
                        else -> "Failed to decline request. Please try again."
                    }
                    _uiState.value = _uiState.value.copy(error = message)
                }
            )
        }
    }

    fun cancelFriendRequest(requestId: String) {
        viewModelScope.launch {
            cancelFriendRequestUseCase(requestId).fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(
                        successMessage = "Friend request cancelled.",
                        error = null
                    )
                },
                onFailure = { e ->
                    val message = when {
                        e.message?.contains("not found", ignoreCase = true) == true ->
                            "This request no longer exists — it may have already been accepted or declined."
                        else -> "Failed to cancel request. Please try again."
                    }
                    _uiState.value = _uiState.value.copy(error = message)
                }
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
                        successMessage = "Friend removed.",
                        error = null
                    )
                    // Refresh to sync Firestore state so re-search shows correct status
                    refreshFriendsList()
                },
                onFailure = {
                    _uiState.value = _uiState.value.copy(
                        error = "Failed to remove friend. Please try again."
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
     * Dismiss all in-app notification dots:
     * - Marks all unread chat messages as read for each friend.
     * - Clears the unread badge count locally.
     */
    fun dismissAllUnreadDots() {
        val userId = currentUserId ?: return
        val friends = _uiState.value.friends
        viewModelScope.launch {
            friends.forEach { friend ->
                try {
                    val chatId = chatRepository.generateChatId(userId, UserId(friend.friendId))
                    chatRepository.markAllMessagesAsRead(chatId, userId)
                } catch (_: Exception) { /* best-effort */ }
            }
            _uiState.value = _uiState.value.copy(unreadCountPerFriend = emptyMap())
        }
    }

    /**
     * Dismiss the shared inbox notification dot by marking all items as seen.
     */
    fun dismissSharedInboxDot() {
        sharedFriendsDataSource.markSharedItemsSeen()
    }

    // ── Block / Unblock ───────────────────────────────────────────────────────

    private fun loadBlockedUsers(userId: UserId) {
        viewModelScope.launch {
            try {
                val blockedList = friendsRepository.getBlockedUsers(userId)
                _uiState.value = _uiState.value.copy(
                    blockedUsers = blockedList,
                    blockedUserIds = blockedList.map { it.userId }.toSet()
                )
            } catch (_: Exception) { /* non-fatal */ }
        }
    }

    /**
     * Block a friend and remove the friendship atomically.
     * Removes the friendship first, then adds to the block list.
     * Also deletes the chat conversation so messages don't persist.
     */
    fun blockAndRemoveFriend(targetUserId: String, targetUsername: String) {
        val userId = currentUserId ?: return
        viewModelScope.launch {
            // Step 1: remove friend relationship AND delete chat (mirrors RemoveFriendUseCase)
            removeFriendUseCase(userId, UserId(targetUserId))

            // Step 2: block the user
            friendsRepository.blockUser(userId, UserId(targetUserId), targetUsername).fold(
                onSuccess = {
                    val newBlockedUser = com.example.fyp.data.friends.BlockedUser(
                        userId = targetUserId,
                        username = targetUsername
                    )
                    val updatedIds = _uiState.value.blockedUserIds + targetUserId
                    val updatedList = _uiState.value.blockedUsers + newBlockedUser
                    // Also remove from friends list in UI immediately
                    val updatedFriends = _uiState.value.friends.filter { it.friendId != targetUserId }
                    _uiState.value = _uiState.value.copy(
                        blockedUserIds = updatedIds,
                        blockedUsers = updatedList,
                        friends = updatedFriends,
                        successMessage = "User blocked and removed from friends.",
                        error = null
                    )
                },
                onFailure = {
                    _uiState.value = _uiState.value.copy(error = "Failed to block user. Please try again.")
                }
            )
        }
    }

    fun blockUser(targetUserId: String) {
        val userId = currentUserId ?: return
        viewModelScope.launch {
            friendsRepository.blockUser(userId, UserId(targetUserId)).fold(
                onSuccess = {
                    val updated = _uiState.value.blockedUserIds + targetUserId
                    _uiState.value = _uiState.value.copy(
                        blockedUserIds = updated,
                        successMessage = "User blocked.",
                        error = null
                    )
                },
                onFailure = {
                    _uiState.value = _uiState.value.copy(error = "Failed to block user. Please try again.")
                }
            )
        }
    }

    fun unblockUser(targetUserId: String) {
        val userId = currentUserId ?: return
        viewModelScope.launch {
            friendsRepository.unblockUser(userId, UserId(targetUserId)).fold(
                onSuccess = {
                    val updatedIds = _uiState.value.blockedUserIds - targetUserId
                    val updatedList = _uiState.value.blockedUsers.filter { it.userId != targetUserId }
                    _uiState.value = _uiState.value.copy(
                        blockedUserIds = updatedIds,
                        blockedUsers = updatedList,
                        successMessage = "User unblocked.",
                        error = null
                    )
                },
                onFailure = {
                    _uiState.value = _uiState.value.copy(error = "Failed to unblock user. Please try again.")
                }
            )
        }
    }

    fun isUserBlocked(targetUserId: String): Boolean =
        _uiState.value.blockedUserIds.contains(targetUserId)

    /**
     * Manually refresh friends list by restarting the shared data source.
     * This helps ensure the latest state after operations like removing friends.
     */
    fun refreshFriendsList() {
        val userId = currentUserId?.value ?: return
        viewModelScope.launch {
            sharedFriendsDataSource.stopObserving()
            sharedFriendsDataSource.startObserving(userId)
            // Sync latest usernames in background (best-effort)
            val updates = friendsRepository.syncFriendUsernames(UserId(userId))
            if (updates.isNotEmpty()) {
                sharedFriendsDataSource.applyUsernameUpdates(updates)
            }
        }
    }

    /**
     * Returns the current user's relationship status with [userId] for display in search results.
     */
    fun getRequestStatusFor(userId: String): RequestStatus {
        val state = _uiState.value
        if (state.friends.any { it.friendId == userId }) return RequestStatus.ALREADY_FRIENDS
        if (state.outgoingRequests.any { it.toUserId == userId }) return RequestStatus.REQUEST_SENT
        if (state.incomingRequests.any { it.fromUserId == userId }) return RequestStatus.REQUEST_RECEIVED
        return RequestStatus.NONE
    }

    /**
     * Check if a user can receive a friend request from current user.
     * Returns true if they are NOT friends and NO pending request exists.
     */
    fun canSendRequestTo(userId: String): Boolean =
        getRequestStatusFor(userId) == RequestStatus.NONE
}
