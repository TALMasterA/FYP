package com.example.fyp

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.fyp.data.friends.ChatRepository
import com.example.fyp.data.friends.SharedFriendsDataSource
import com.example.fyp.data.history.SharedHistoryDataSource
import com.example.fyp.data.settings.SharedSettingsDataSource
import com.example.fyp.data.user.FirebaseAuthRepository
import com.example.fyp.core.FcmNotificationService
import com.example.fyp.domain.friends.EnsurePublicProfileExistsUseCase
import com.example.fyp.model.UserId
import com.example.fyp.model.user.AuthState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
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
    application: Application,
    private val authRepository: FirebaseAuthRepository,
    private val ensurePublicProfileExistsUseCase: EnsurePublicProfileExistsUseCase,
    private val sharedFriendsDataSource: SharedFriendsDataSource,
    private val sharedSettingsDataSource: SharedSettingsDataSource,
    private val sharedHistoryDataSource: SharedHistoryDataSource,
    private val chatRepository: ChatRepository
) : AndroidViewModel(application) {

    private var lastInitializedUserId: String? = null

    // ── Notification state (read-only for UI) ──────────────────────────────

    /** Pending incoming friend requests — gated by inAppBadgeFriendRequests setting. */
    val pendingFriendRequestCount: StateFlow<Int> =
        combine(
            sharedFriendsDataSource.incomingRequests.map { it.size },
            sharedSettingsDataSource.settings.map { it.inAppBadgeFriendRequests }
        ) { count, enabled -> if (enabled) count else 0 }
            .stateIn(viewModelScope, SharingStarted.Eagerly, 0)

    /** Whether there are unseen shared inbox items — gated by inAppBadgeSharedInbox setting. */
    val hasUnseenSharedItems: StateFlow<Boolean> =
        combine(
            sharedFriendsDataSource.hasUnseenSharedItems,
            sharedSettingsDataSource.settings.map { it.inAppBadgeSharedInbox }
        ) { hasUnseen, enabled -> hasUnseen && enabled }
            .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    /** Accurate count of unseen shared inbox items — gated by inAppBadgeSharedInbox setting. */
    val unseenSharedItemsCount: StateFlow<Int> =
        combine(
            sharedFriendsDataSource.unseenSharedItemsCount,
            sharedSettingsDataSource.settings.map { it.inAppBadgeSharedInbox }
        ) { count, enabled -> if (enabled) count else 0 }
            .stateIn(viewModelScope, SharingStarted.Eagerly, 0)

    /** Whether there are any unread chat messages — gated by inAppBadgeMessages setting. */
    private val _hasUnreadMessages = MutableStateFlow(false)
    val hasUnreadMessages: StateFlow<Boolean> = _hasUnreadMessages.asStateFlow()

    /** Accurate count of unread chat messages — gated by inAppBadgeMessages setting. */
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
                            // Upload FCM token so backend can send push notifications
                            FcmNotificationService.uploadTokenIfLoggedIn(getApplication())
                        }
                    }
                    is AuthState.LoggedOut -> {
                        lastInitializedUserId = null
                        sharedFriendsDataSource.stopObserving()
                        sharedSettingsDataSource.stopObserving()
                        sharedHistoryDataSource.stopObserving()
                        unreadJob?.cancel()
                        _hasUnreadMessages.value = false
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
                combine(
                    chatRepository.observeTotalUnreadCount(UserId(userId)),
                    sharedSettingsDataSource.settings.map { it.inAppBadgeMessages }
                ) { count, enabled ->
                    Pair(enabled && count > 0, if (enabled) count else 0)
                }.collect { (showBadge, count) ->
                    _hasUnreadMessages.value = showBadge
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