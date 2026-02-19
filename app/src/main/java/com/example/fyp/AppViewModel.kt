package com.example.fyp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fyp.data.friends.ChatRepository
import com.example.fyp.data.friends.SharedFriendsDataSource
import com.example.fyp.data.user.FirebaseAuthRepository
import com.example.fyp.domain.friends.EnsurePublicProfileExistsUseCase
import com.example.fyp.model.UserId
import com.example.fyp.model.user.AuthState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Application-level ViewModel.
 *
 * OPTIMIZED: Notification counts are derived from [SharedFriendsDataSource] (shared listeners
 * already used by FriendsViewModel / SharedInboxViewModel) and a single-document listener on
 * users/{userId}.totalUnreadMessages — no extra Firestore connections are created here.
 */
@HiltViewModel
class AppViewModel @Inject constructor(
    private val authRepository: FirebaseAuthRepository,
    private val ensurePublicProfileExistsUseCase: EnsurePublicProfileExistsUseCase,
    private val sharedFriendsDataSource: SharedFriendsDataSource,
    private val chatRepository: ChatRepository
) : ViewModel() {

    private var lastInitializedUserId: String? = null

    // ── Notification counts (read-only for UI) ───────────────────────────────

    /** Pending incoming friend requests — derived from shared in-memory state. */
    val pendingFriendRequestCount: StateFlow<Int> =
        sharedFriendsDataSource.incomingRequests
            .map { it.size }
            .stateIn(viewModelScope, SharingStarted.Eagerly, 0)

    /** Pending shared inbox items — derived from shared in-memory state. */
    val pendingSharedItemCount: StateFlow<Int> =
        sharedFriendsDataSource.pendingSharedItems
            .map { it.size }
            .stateIn(viewModelScope, SharingStarted.Eagerly, 0)

    private val _unreadMessageCount = MutableStateFlow(0)
    val unreadMessageCount: StateFlow<Int> = _unreadMessageCount.asStateFlow()

    private var unreadJob: Job? = null

    // ── Init ─────────────────────────────────────────────────────────────────

    init {
        viewModelScope.launch {
            authRepository.currentUserState.collect { authState ->
                when (authState) {
                    is AuthState.LoggedIn -> {
                        val userId = authState.user.uid
                        // Start shared friends listeners (idempotent — no-op if already running)
                        sharedFriendsDataSource.startObserving(userId)
                        if (lastInitializedUserId != userId) {
                            lastInitializedUserId = userId
                            initializeUserProfile(userId)
                            startObservingUnread(userId)
                        }
                    }
                    is AuthState.LoggedOut -> {
                        lastInitializedUserId = null
                        sharedFriendsDataSource.stopObserving()
                        unreadJob?.cancel()
                        _unreadMessageCount.value = 0
                    }
                    is AuthState.Loading -> Unit
                }
            }
        }
    }

    private fun startObservingUnread(userId: String) {
        unreadJob?.cancel()
        unreadJob = viewModelScope.launch {
            try {
                chatRepository.observeTotalUnreadCount(UserId(userId)).collect { count ->
                    _unreadMessageCount.value = count
                }
            } catch (e: Exception) {
                android.util.Log.e("AppViewModel", "Error observing unread messages", e)
            }
        }
    }

    private fun initializeUserProfile(userId: String) {
        viewModelScope.launch {
            try {
                ensurePublicProfileExistsUseCase(userId)
            } catch (e: Exception) {
                android.util.Log.e("AppViewModel", "Failed to initialize profile for user $userId", e)
            }
        }
    }
}

