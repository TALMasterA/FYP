package com.example.fyp.appstate

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.fyp.data.friends.ChatRepository
import com.example.fyp.data.friends.FriendsRepository
import com.example.fyp.data.friends.SharedFriendsDataSource
import com.example.fyp.data.history.SharedHistoryDataSource
import com.example.fyp.data.settings.SharedSettingsDataSource
import com.example.fyp.data.user.FirebaseAuthRepository
import com.example.fyp.core.FcmNotificationService
import com.example.fyp.domain.friends.EnsurePublicProfileExistsUseCase
import com.example.fyp.model.UserId
import com.example.fyp.model.user.AuthState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
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
    private val chatRepository: ChatRepository,
    private val friendsRepository: FriendsRepository,
) : AndroidViewModel(application) {

    private var lastInitializedUserId: String? = null

    // ── Notification state (read-only for UI) ──────────────────────────────

    /** Unseen incoming friend requests (persisted across app restarts) — gated by inAppBadgeFriendRequests setting. */
    val pendingFriendRequestCount: StateFlow<Int> =
        combine(
            sharedFriendsDataSource.unseenFriendRequestCount,
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

    /** Current user's username from PublicUserProfile (null if not set or not loaded yet). */
    private val _currentUsername = MutableStateFlow<String?>(null)
    val currentUsername: StateFlow<String?> = _currentUsername.asStateFlow()

    private var unreadJob: Job? = null
    private var usernameJob: Job? = null

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
                            startObservingUsername(userId)
                            // Upload FCM token so backend can send push notifications
                            FcmNotificationService.uploadTokenIfLoggedIn(getApplication())
                            // Sync FCM notification prefs: on first login the SharedPreferences cache
                            // defaults to true (fail-open) but the Firestore model defaults to false.
                            // Wait for the first settings emission and write Firestore values to cache.
                            syncFcmPrefsFromFirestore()
                        }
                    }
                    is AuthState.LoggedOut -> {
                        lastInitializedUserId?.let { uid ->
                            // On explicit logout, clear persisted notification-seen state so the
                            // next user (or same user after re-login) starts with a clean slate.
                            sharedFriendsDataSource.clearAllSeenStateForUser(uid)
                        }
                        lastInitializedUserId = null
                        sharedFriendsDataSource.stopObserving()
                        sharedSettingsDataSource.stopObserving()
                        sharedHistoryDataSource.stopObserving()
                        unreadJob?.cancel()
                        usernameJob?.cancel()
                        _hasUnreadMessages.value = false
                        _unreadMessageCount.value = 0
                        _currentUsername.value = null
                    }
                    is AuthState.Loading -> Unit
                }
            }
        }
    }

    private fun startObservingUnread(userId: String) {
        unreadJob?.cancel()
        // Job 1: Feed raw per-friend unread counts into SharedFriendsDataSource for seen-filtering
        unreadJob = viewModelScope.launch {
            try {
                chatRepository.observeUnreadPerFriend(UserId(userId)).collect { rawMap ->
                    sharedFriendsDataSource.updateRawUnreadPerFriend(rawMap)
                }
            } catch (e: CancellationException) {
                // Expected when user switches account/logs out or ViewModel is cleared.
                throw e
            } catch (e: Exception) {
                android.util.Log.e("AppViewModel", "Error observing unreadPerFriend", e)
            }
        }
        // Job 2: Derive badge signals from the seen-filtered flow
        viewModelScope.launch {
            try {
                combine(
                    sharedFriendsDataSource.unseenUnreadPerFriend,
                    sharedSettingsDataSource.settings.map { it.inAppBadgeMessages }
                ) { unseenMap, enabled ->
                    val total = if (enabled) unseenMap.values.sum() else 0
                    Pair(enabled && total > 0, total)
                }.collect { (showBadge, count) ->
                    _hasUnreadMessages.value = showBadge
                    _unreadMessageCount.value = count
                }
            } catch (e: CancellationException) {
                // Expected when user switches account/logs out or ViewModel is cleared.
                throw e
            } catch (e: Exception) {
                android.util.Log.e("AppViewModel", "Error observing unseen unread messages", e)
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

    private fun startObservingUsername(userId: String) {
        usernameJob?.cancel()
        usernameJob = viewModelScope.launch {
            try {
                // Fetch the user's public profile to get their username
                val profile = friendsRepository.getPublicProfile(UserId(userId))
                _currentUsername.value = profile?.username?.takeIf { it.isNotBlank() }
            } catch (e: Exception) {
                android.util.Log.e("AppViewModel", "Error fetching username", e)
                _currentUsername.value = null
            }
        }
    }

    /**
     * One-time sync of FCM push notification preferences from Firestore to SharedPreferences.
     * The FCM service reads from SharedPreferences (defaulting to true if absent), but the
     * Firestore model defaults push toggles to false. Without this sync, first-time users
     * would receive push notifications even though their Firestore settings say OFF.
     */
    private fun syncFcmPrefsFromFirestore() {
        viewModelScope.launch {
            try {
                val settings = sharedSettingsDataSource.settings.first { !sharedSettingsDataSource.isLoading.value }
                val ctx = getApplication<Application>()
                FcmNotificationService.saveNotifPrefToCache(ctx, "notifyNewMessages", settings.notifyNewMessages)
                FcmNotificationService.saveNotifPrefToCache(ctx, "notifyFriendRequests", settings.notifyFriendRequests)
                FcmNotificationService.saveNotifPrefToCache(ctx, "notifyRequestAccepted", settings.notifyRequestAccepted)
                FcmNotificationService.saveNotifPrefToCache(ctx, "notifySharedInbox", settings.notifySharedInbox)
            } catch (e: Exception) {
                android.util.Log.e("AppViewModel", "Failed to sync FCM prefs from Firestore", e)
            }
        }
    }
}