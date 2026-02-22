package com.example.fyp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fyp.data.friends.ChatRepository
import com.example.fyp.data.friends.SharedFriendsDataSource
import com.example.fyp.data.history.SharedHistoryDataSource
import com.example.fyp.data.settings.SharedSettingsDataSource
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
 *
 * OPTIMIZED: [SharedSettingsDataSource] is started here so the settings listener is running
 * before any screen-level ViewModel needs it, and we can pass the cached primary language
 * to [EnsurePublicProfileExistsUseCase] to avoid an extra fetchUserSettings read.
 */
@HiltViewModel
class AppViewModel @Inject constructor(
    private val authRepository: FirebaseAuthRepository,
    private val ensurePublicProfileExistsUseCase: EnsurePublicProfileExistsUseCase,
    private val sharedFriendsDataSource: SharedFriendsDataSource,
    private val sharedSettingsDataSource: SharedSettingsDataSource,
    private val sharedHistoryDataSource: SharedHistoryDataSource,
    private val chatRepository: ChatRepository
) : ViewModel() {

    private var lastInitializedUserId: String? = null

    // ── Notification state (read-only for UI) ──────────────────────────────

    /** Pending incoming friend requests — derived from shared in-memory state. */
    val pendingFriendRequestCount: StateFlow<Int> =
        sharedFriendsDataSource.incomingRequests
            .map { it.size }
            .stateIn(viewModelScope, SharingStarted.Eagerly, 0)

    /** Whether there are unseen shared inbox items (red dot, no count). */
    val hasUnseenSharedItems: StateFlow<Boolean> =
        sharedFriendsDataSource.hasUnseenSharedItems
            .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    /** Whether there are any unread chat messages (red dot, no count). */
    private val _hasUnreadMessages = MutableStateFlow(false)
    val hasUnreadMessages: StateFlow<Boolean> = _hasUnreadMessages.asStateFlow()

    private var unreadJob: Job? = null

    // ── Init ─────────────────────────────────────────────────────────────────

    init {
        viewModelScope.launch {
            authRepository.currentUserState.collect { authState ->
                when (authState) {
                    is AuthState.LoggedIn -> {
                        val userId = authState.user.uid
                        // Start shared listeners (all idempotent — no-op if already running)
                        sharedFriendsDataSource.startObserving(userId)
                        // Start settings listener early so subsequent ViewModels find it warm
                        sharedSettingsDataSource.startObserving(userId)
                        // Start history listener early so Learning and WordBank screens load immediately
                        sharedHistoryDataSource.startObserving(userId)
                        if (lastInitializedUserId != userId) {
                            lastInitializedUserId = userId
                            initializeUserProfile(userId)
                            startObservingUnread(userId)
                        }
                    }
                    is AuthState.LoggedOut -> {
                        lastInitializedUserId = null
                        sharedFriendsDataSource.stopObserving()
                        sharedSettingsDataSource.stopObserving()
                        sharedHistoryDataSource.stopObserving()
                        unreadJob?.cancel()
                        _hasUnreadMessages.value = false
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
                    android.util.Log.d("AppViewModel", "Unread count updated: $count")
                    _hasUnreadMessages.value = count > 0
                }
            } catch (e: Exception) {
                android.util.Log.e("AppViewModel", "Error observing unread messages", e)
            }
        }
    }

    private fun initializeUserProfile(userId: String) {
        viewModelScope.launch {
            try {
                // Pass the already-cached primary language so EnsurePublicProfileExistsUseCase
                // can skip its own fetchUserSettings Firestore read when the settings are warm.
                val cachedPrimaryLang = sharedSettingsDataSource.settings.value
                    .primaryLanguageCode.takeIf { it.isNotBlank() }
                ensurePublicProfileExistsUseCase(userId, cachedPrimaryLang)
            } catch (e: Exception) {
                android.util.Log.e("AppViewModel", "Failed to initialize profile for user $userId", e)
            }
        }
    }
}