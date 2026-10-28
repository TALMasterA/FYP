package com.translator.TalknLearn.screens.friends

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.translator.TalknLearn.data.friends.FriendRequestRateLimiter
import com.translator.TalknLearn.data.friends.SharedFriendsDataSource
import com.translator.TalknLearn.data.settings.SharedSettingsDataSource
import com.translator.TalknLearn.data.user.FirebaseAuthRepository
import com.translator.TalknLearn.domain.friends.*
import com.translator.TalknLearn.model.UserId
import com.translator.TalknLearn.model.user.AuthState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/** UI state for the Friends screen. */
data class FriendsUiState(
    val isLoading: Boolean = true,
    val friends: List<com.translator.TalknLearn.model.friends.FriendRelation> = emptyList(),
    val incomingRequests: List<com.translator.TalknLearn.model.friends.FriendRequest> = emptyList(),
    val outgoingRequests: List<com.translator.TalknLearn.model.friends.FriendRequest> = emptyList(),
    val searchQuery: String = "",
    val searchResults: List<com.translator.TalknLearn.model.friends.PublicUserProfile> = emptyList(),
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
    val blockedUsers: List<com.translator.TalknLearn.data.friends.BlockedUser> = emptyList(),
    /** Batch operation progress: null when not in progress, otherwise "X/Y" format */
    val batchProgress: String? = null
)

/** Status of the local user's relationship with a search result. */
enum class RequestStatus {
    NONE,             // No connection — can send request
    ALREADY_FRIENDS,  // Already friends
    REQUEST_SENT,     // Current user sent a pending request
    REQUEST_RECEIVED  // The other user already sent a request to current user
}

/**
 * OPTIMIZED: Friends list and incoming requests are read from [SharedFriendsDataSource]
 * (shared single-listener data source) instead of creating new Firestore listeners.
 * Outgoing requests still have their own listener (only needed on this screen).
 * Per-friend unread counts are observed via [SharedFriendsDataSource.unseenUnreadPerFriend].
 *
 * Heavy logic is delegated to:
 * - [FriendSearchDelegate] — search / combined-search
 * - [FriendRequestDelegate] — send / accept / reject / cancel / batch ops
 * - [FriendListDelegate] — delete mode / multi-remove / refresh
 * - [BlockUserDelegate] — block / unblock
 */
@HiltViewModel
class FriendsViewModel @Inject constructor(
    private val authRepo: FirebaseAuthRepository,
    private val sharedFriendsDataSource: SharedFriendsDataSource,
    private val sharedSettingsDataSource: SharedSettingsDataSource,
    private val friendsRepository: com.translator.TalknLearn.data.friends.FriendsRepository,
    private val friendRequestRateLimiter: FriendRequestRateLimiter,
    private val observeOutgoingRequestsUseCase: ObserveOutgoingRequestsUseCase,
    private val searchUsersUseCase: SearchUsersUseCase,
    private val sendFriendRequestUseCase: SendFriendRequestUseCase,
    private val acceptFriendRequestUseCase: AcceptFriendRequestUseCase,
    private val rejectFriendRequestUseCase: RejectFriendRequestUseCase,
    private val cancelFriendRequestUseCase: CancelFriendRequestUseCase,
    private val removeFriendUseCase: RemoveFriendUseCase,
    private val funnelTracker: com.translator.TalknLearn.observability.FunnelAnalyticsTracker,
) : ViewModel() {

    private val _uiState = MutableStateFlow(FriendsUiState())
    val uiState: StateFlow<FriendsUiState> = _uiState.asStateFlow()

    // ── Reactive notification state ──────────────────────────────────────────

    val hasUnseenSharedItems: StateFlow<Boolean> =
        combine(
            sharedFriendsDataSource.hasUnseenSharedItems,
            sharedSettingsDataSource.settings.map { it.inAppBadgeSharedInbox }
        ) { hasUnseen, enabled -> hasUnseen && enabled }
            .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    val unseenSharedItemsCount: StateFlow<Int> =
        combine(
            sharedFriendsDataSource.unseenSharedItemsCount,
            sharedSettingsDataSource.settings.map { it.inAppBadgeSharedInbox }
        ) { count, enabled -> if (enabled) count else 0 }
            .stateIn(viewModelScope, SharingStarted.Eagerly, 0)

    val unseenFriendRequestCount: StateFlow<Int> =
        combine(
            sharedFriendsDataSource.unseenFriendRequestCount,
            sharedSettingsDataSource.settings.map { it.inAppBadgeFriendRequests }
        ) { count, enabled -> if (enabled) count else 0 }
            .stateIn(viewModelScope, SharingStarted.Eagerly, 0)

    // ── Internal state ───────────────────────────────────────────────────────

    private var outgoingRequestsJob: Job? = null
    private var currentUserId: UserId? = null
    private var previousIncomingCount = 0
    private var unreadPerFriendJob: Job? = null

    // ── Delegates ────────────────────────────────────────────────────────────

    private val searchDelegate = FriendSearchDelegate(
        uiState = _uiState,
        scope = viewModelScope,
        searchUsersUseCase = searchUsersUseCase,
        friendsRepository = friendsRepository,
        getCurrentUserId = { currentUserId }
    )

    private val requestDelegate = FriendRequestDelegate(
        uiState = _uiState,
        scope = viewModelScope,
        friendRequestRateLimiter = friendRequestRateLimiter,
        sendFriendRequestUseCase = sendFriendRequestUseCase,
        acceptFriendRequestUseCase = acceptFriendRequestUseCase,
        rejectFriendRequestUseCase = rejectFriendRequestUseCase,
        cancelFriendRequestUseCase = cancelFriendRequestUseCase,
        funnelTracker = funnelTracker,
        getCurrentUserId = { currentUserId },
        requireUsername = ::requireUsernameForFriendActions,
        showSuccess = ::showSuccessMessage
    )

    private val listDelegate = FriendListDelegate(
        uiState = _uiState,
        scope = viewModelScope,
        friendsRepository = friendsRepository,
        sharedFriendsDataSource = sharedFriendsDataSource,
        removeFriendUseCase = removeFriendUseCase,
        getCurrentUserId = { currentUserId },
        showSuccess = ::showSuccessMessage
    )

    private val blockDelegate = BlockUserDelegate(
        uiState = _uiState,
        scope = viewModelScope,
        friendsRepository = friendsRepository,
        removeFriendUseCase = removeFriendUseCase,
        getCurrentUserId = { currentUserId },
        showSuccess = ::showSuccessMessage
    )

    // ── Init ─────────────────────────────────────────────────────────────────

    init {
        viewModelScope.launch {
            authRepo.currentUserState.collect { auth ->
                when (auth) {
                    is AuthState.LoggedIn -> {
                        currentUserId = UserId(auth.user.uid)
                        sharedFriendsDataSource.startObserving(auth.user.uid)
                        subscribeToSharedData()
                        startOutgoingRequestsObserver(UserId(auth.user.uid))
                        startUnreadPerFriendObserver()
                        loadOwnUsername(auth.user.uid)
                        blockDelegate.loadBlockedUsers(UserId(auth.user.uid))
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

    // ── Private helpers ──────────────────────────────────────────────────────

    private fun loadOwnUsername(userId: String) {
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
            } catch (_: Exception) { }
        }
    }

    private fun subscribeToSharedData() {
        viewModelScope.launch {
            sharedFriendsDataSource.friends.collect { friends ->
                _uiState.value = _uiState.value.copy(friends = friends, isLoading = false)
            }
        }
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

    private fun startUnreadPerFriendObserver() {
        unreadPerFriendJob?.cancel()
        unreadPerFriendJob = viewModelScope.launch {
            combine(
                sharedFriendsDataSource.unseenUnreadPerFriend,
                sharedSettingsDataSource.settings.map { it.inAppBadgeMessages }
            ) { unseenMap, enabled ->
                if (enabled) unseenMap else emptyMap()
            }.collect { gatedMap ->
                _uiState.value = _uiState.value.copy(unreadCountPerFriend = gatedMap)
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

    private fun showSuccessMessage(message: String) {
        _uiState.value = _uiState.value.copy(successMessage = message)
        viewModelScope.launch {
            delay(3000)
            _uiState.value = _uiState.value.copy(successMessage = null)
        }
    }

    // ── Public API (delegates) ───────────────────────────────────────────────

    // Search
    fun onSearchQueryChange(query: String) = searchDelegate.onSearchQueryChange(query)

    // Username gate
    fun requireUsernameForFriendActions(): Boolean {
        return if (!_uiState.value.currentUserHasUsername) {
            _uiState.value = _uiState.value.copy(
                error = "Please set a username in your profile before managing friend requests."
            )
            false
        } else {
            true
        }
    }
    fun requireUsernameForAddFriends(): Boolean = requireUsernameForFriendActions()

    // Friend requests
    fun sendFriendRequest(toUserId: String, note: String = "") =
        requestDelegate.sendFriendRequest(toUserId, note)
    fun acceptFriendRequest(requestId: String) = requestDelegate.acceptFriendRequest(requestId)
    fun rejectFriendRequest(requestId: String) = requestDelegate.rejectFriendRequest(requestId)
    fun cancelFriendRequest(requestId: String) = requestDelegate.cancelFriendRequest(requestId)
    fun acceptAllRequests() = requestDelegate.acceptAllRequests()
    fun rejectAllRequests() = requestDelegate.rejectAllRequests()
    fun cancelBatchOperation() = requestDelegate.cancelBatchOperation()

    // Delete mode / friend list
    fun toggleDeleteMode() = listDelegate.toggleDeleteMode()
    fun toggleFriendSelection(friendId: String) = listDelegate.toggleFriendSelection(friendId)
    fun exitDeleteMode() = listDelegate.exitDeleteMode()
    fun removeSelectedFriends() = listDelegate.removeSelectedFriends()
    fun removeFriend(friendId: String) = listDelegate.removeFriend(friendId)
    fun refreshFriendsList() = listDelegate.refreshFriendsList()

    // Block / unblock
    fun blockAndRemoveFriend(targetUserId: String, targetUsername: String) =
        blockDelegate.blockAndRemoveFriend(targetUserId, targetUsername)
    fun blockUser(targetUserId: String) = blockDelegate.blockUser(targetUserId)
    fun unblockUser(targetUserId: String) = blockDelegate.unblockUser(targetUserId)

    // Misc
    fun clearMessages() {
        _uiState.value = _uiState.value.copy(error = null, successMessage = null)
    }

    fun clearNewRequestCount() {
        _uiState.value = _uiState.value.copy(newRequestCount = 0)
    }

    fun markFriendRequestsSeen() {
        sharedFriendsDataSource.markFriendRequestsSeen()
    }

    fun getRequestStatusFor(userId: String): RequestStatus {
        val state = _uiState.value
        if (state.friends.any { it.friendId == userId }) return RequestStatus.ALREADY_FRIENDS
        if (state.outgoingRequests.any { it.toUserId == userId }) return RequestStatus.REQUEST_SENT
        if (state.incomingRequests.any { it.fromUserId == userId }) return RequestStatus.REQUEST_RECEIVED
        return RequestStatus.NONE
    }

    fun canSendRequestTo(userId: String): Boolean =
        getRequestStatusFor(userId) == RequestStatus.NONE

    override fun onCleared() {
        super.onCleared()
        outgoingRequestsJob?.cancel()
        unreadPerFriendJob?.cancel()
        requestDelegate.cancelJobs()
        searchDelegate.cancelJob()
    }
}
