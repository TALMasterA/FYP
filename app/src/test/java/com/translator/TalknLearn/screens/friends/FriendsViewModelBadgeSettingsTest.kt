package com.translator.TalknLearn.screens.friends

import com.translator.TalknLearn.data.friends.ChatRepository
import com.translator.TalknLearn.data.friends.FriendRequestRateLimitStatus
import com.translator.TalknLearn.data.friends.FriendRequestRateLimiter
import com.translator.TalknLearn.data.friends.FriendsRepository
import com.translator.TalknLearn.data.friends.SharedFriendsDataSource
import com.translator.TalknLearn.data.settings.SharedSettingsDataSource
import com.translator.TalknLearn.data.user.FirebaseAuthRepository
import com.translator.TalknLearn.domain.friends.*
import com.translator.TalknLearn.model.UserId
import com.translator.TalknLearn.model.friends.FriendRelation
import com.translator.TalknLearn.model.friends.FriendRequest
import com.translator.TalknLearn.model.friends.PublicUserProfile
import com.translator.TalknLearn.model.user.AuthState
import com.translator.TalknLearn.model.user.User
import com.translator.TalknLearn.model.user.UserSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.*

/**
 * Unit tests verifying that badge flows in FriendsViewModel respect
 * inAppBadge* settings from SharedSettingsDataSource.
 *
 * Tests:
 * 1. hasUnseenSharedItems is false when setting disabled even with unseen items
 * 2. hasUnseenSharedItems is true when setting enabled and items exist
 * 3. unseenSharedItemsCount is 0 when setting disabled
 * 4. unseenSharedItemsCount shows count when setting enabled
 * 5. unseenFriendRequestCount is 0 when setting disabled
 * 6. unseenFriendRequestCount shows count when setting enabled
 * 7. toggling inAppBadgeSharedInbox dynamically updates badge
 * 8. toggling inAppBadgeMessages dynamically updates unread per friend
 */
@OptIn(ExperimentalCoroutinesApi::class)
class FriendsViewModelBadgeSettingsTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    // Controllable flows for settings and badge data
    private val authStateFlow = MutableStateFlow<AuthState>(AuthState.Loading)
    private val friendsFlow = MutableStateFlow<List<FriendRelation>>(emptyList())
    private val incomingRequestsFlow = MutableStateFlow<List<FriendRequest>>(emptyList())
    private val settingsFlow = MutableStateFlow(UserSettings())
    private val hasUnseenSharedItemsFlow = MutableStateFlow(false)
    private val unseenSharedItemsCountFlow = MutableStateFlow(0)
    private val unseenFriendRequestCountFlow = MutableStateFlow(0)
    private val unseenUnreadPerFriendFlow = MutableStateFlow<Map<String, Int>>(emptyMap())

    private val isLoadingFlow = MutableStateFlow(false)

    private lateinit var authRepo: FirebaseAuthRepository
    private lateinit var sharedFriendsDataSource: SharedFriendsDataSource
    private lateinit var sharedSettingsDataSource: SharedSettingsDataSource
    private lateinit var chatRepository: ChatRepository
    private lateinit var friendsRepository: FriendsRepository
    private lateinit var friendRequestRateLimiter: FriendRequestRateLimiter
    private lateinit var observeOutgoingRequestsUseCase: ObserveOutgoingRequestsUseCase
    private lateinit var searchUsersUseCase: SearchUsersUseCase
    private lateinit var sendFriendRequestUseCase: SendFriendRequestUseCase
    private lateinit var acceptFriendRequestUseCase: AcceptFriendRequestUseCase
    private lateinit var rejectFriendRequestUseCase: RejectFriendRequestUseCase
    private lateinit var cancelFriendRequestUseCase: CancelFriendRequestUseCase
    private lateinit var removeFriendUseCase: RemoveFriendUseCase

    private val testUser = User(uid = "user1", email = "test@test.com")

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        authRepo = mock { on { currentUserState } doReturn authStateFlow }
        sharedFriendsDataSource = mock {
            on { friends } doReturn friendsFlow
            on { incomingRequests } doReturn incomingRequestsFlow
            on { hasUnseenSharedItems } doReturn hasUnseenSharedItemsFlow
            on { unseenSharedItemsCount } doReturn unseenSharedItemsCountFlow
            on { unseenFriendRequestCount } doReturn unseenFriendRequestCountFlow
            on { unseenUnreadPerFriend } doReturn unseenUnreadPerFriendFlow
            on { getCachedUsername("user1") } doReturn "testuser"
        }
        sharedSettingsDataSource = mock {
            on { settings } doReturn settingsFlow
            on { isLoading } doReturn isLoadingFlow
        }
        chatRepository = mock()
        friendsRepository = mock()
        friendRequestRateLimiter = mock {
            on { canSend(eq("user1"), any()) } doReturn FriendRequestRateLimitStatus(allowed = true)
        }
        observeOutgoingRequestsUseCase = mock()
        searchUsersUseCase = mock()
        sendFriendRequestUseCase = mock()
        acceptFriendRequestUseCase = mock()
        rejectFriendRequestUseCase = mock()
        cancelFriendRequestUseCase = mock()
        removeFriendUseCase = mock()

        whenever(observeOutgoingRequestsUseCase.invoke(UserId("user1")))
            .thenReturn(flowOf(emptyList()))

        runTest {
            whenever(friendsRepository.getPublicProfile(UserId("user1")))
                .thenReturn(PublicUserProfile(uid = "user1", username = "testuser"))
            whenever(friendsRepository.getBlockedUsers(UserId("user1"))).thenReturn(emptyList())
        }
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun buildViewModel() = FriendsViewModel(
        authRepo = authRepo,
        sharedFriendsDataSource = sharedFriendsDataSource,
        sharedSettingsDataSource = sharedSettingsDataSource,
        chatRepository = chatRepository,
        friendsRepository = friendsRepository,
        friendRequestRateLimiter = friendRequestRateLimiter,
        observeOutgoingRequestsUseCase = observeOutgoingRequestsUseCase,
        searchUsersUseCase = searchUsersUseCase,
        sendFriendRequestUseCase = sendFriendRequestUseCase,
        acceptFriendRequestUseCase = acceptFriendRequestUseCase,
        rejectFriendRequestUseCase = rejectFriendRequestUseCase,
        cancelFriendRequestUseCase = cancelFriendRequestUseCase,
        removeFriendUseCase = removeFriendUseCase,
        funnelTracker = mock()
    )

    // ── hasUnseenSharedItems gated by inAppBadgeSharedInbox ─────────────────

    @Test
    fun `hasUnseenSharedItems is false when setting disabled even with unseen items`() = runTest {
        settingsFlow.value = UserSettings(inAppBadgeSharedInbox = false)
        hasUnseenSharedItemsFlow.value = true

        val vm = buildViewModel()

        assertFalse(vm.hasUnseenSharedItems.value)
    }

    @Test
    fun `hasUnseenSharedItems is true when setting enabled and items exist`() = runTest {
        settingsFlow.value = UserSettings(inAppBadgeSharedInbox = true)
        hasUnseenSharedItemsFlow.value = true

        val vm = buildViewModel()

        assertTrue(vm.hasUnseenSharedItems.value)
    }

    // ── unseenSharedItemsCount gated by inAppBadgeSharedInbox ───────────────

    @Test
    fun `unseenSharedItemsCount is 0 when setting disabled`() = runTest {
        settingsFlow.value = UserSettings(inAppBadgeSharedInbox = false)
        unseenSharedItemsCountFlow.value = 5

        val vm = buildViewModel()

        assertEquals(0, vm.unseenSharedItemsCount.value)
    }

    @Test
    fun `unseenSharedItemsCount shows count when setting enabled`() = runTest {
        settingsFlow.value = UserSettings(inAppBadgeSharedInbox = true)
        unseenSharedItemsCountFlow.value = 5

        val vm = buildViewModel()

        assertEquals(5, vm.unseenSharedItemsCount.value)
    }

    // ── unseenFriendRequestCount gated by inAppBadgeFriendRequests ──────────

    @Test
    fun `unseenFriendRequestCount is 0 when setting disabled`() = runTest {
        settingsFlow.value = UserSettings(inAppBadgeFriendRequests = false)
        unseenFriendRequestCountFlow.value = 3

        val vm = buildViewModel()

        assertEquals(0, vm.unseenFriendRequestCount.value)
    }

    @Test
    fun `unseenFriendRequestCount shows count when setting enabled`() = runTest {
        settingsFlow.value = UserSettings(inAppBadgeFriendRequests = true)
        unseenFriendRequestCountFlow.value = 3

        val vm = buildViewModel()

        assertEquals(3, vm.unseenFriendRequestCount.value)
    }

    // ── Dynamic toggle ──────────────────────────────────────────────────────

    @Test
    fun `toggling inAppBadgeSharedInbox dynamically updates badge`() = runTest {
        settingsFlow.value = UserSettings(inAppBadgeSharedInbox = true)
        unseenSharedItemsCountFlow.value = 4
        hasUnseenSharedItemsFlow.value = true

        val vm = buildViewModel()

        // Initially enabled — badge and count should reflect data
        assertTrue(vm.hasUnseenSharedItems.value)
        assertEquals(4, vm.unseenSharedItemsCount.value)

        // Disable the setting
        settingsFlow.value = settingsFlow.value.copy(inAppBadgeSharedInbox = false)

        assertFalse(vm.hasUnseenSharedItems.value)
        assertEquals(0, vm.unseenSharedItemsCount.value)

        // Re-enable the setting
        settingsFlow.value = settingsFlow.value.copy(inAppBadgeSharedInbox = true)

        assertTrue(vm.hasUnseenSharedItems.value)
        assertEquals(4, vm.unseenSharedItemsCount.value)
    }

    @Test
    fun `toggling inAppBadgeMessages dynamically updates unread per friend`() = runTest {
        settingsFlow.value = UserSettings(inAppBadgeMessages = true)
        unseenUnreadPerFriendFlow.value = mapOf("friend1" to 3, "friend2" to 1)

        val vm = buildViewModel()
        // Log in to trigger startUnreadPerFriendObserver
        authStateFlow.value = AuthState.LoggedIn(testUser)

        // Enabled — unread counts should appear in UI state
        assertEquals(mapOf("friend1" to 3, "friend2" to 1), vm.uiState.value.unreadCountPerFriend)

        // Disable the setting
        settingsFlow.value = settingsFlow.value.copy(inAppBadgeMessages = false)

        assertEquals(emptyMap<String, Int>(), vm.uiState.value.unreadCountPerFriend)

        // Re-enable the setting
        settingsFlow.value = settingsFlow.value.copy(inAppBadgeMessages = true)

        assertEquals(mapOf("friend1" to 3, "friend2" to 1), vm.uiState.value.unreadCountPerFriend)
    }

    @Test
    fun `badge-gated counts remain disabled across logout and relogin`() = runTest {
        settingsFlow.value = UserSettings(
            inAppBadgeSharedInbox = false,
            inAppBadgeFriendRequests = false,
            inAppBadgeMessages = false
        )
        hasUnseenSharedItemsFlow.value = true
        unseenSharedItemsCountFlow.value = 2
        unseenFriendRequestCountFlow.value = 3
        unseenUnreadPerFriendFlow.value = mapOf("friend1" to 4)

        val vm = buildViewModel()
        authStateFlow.value = AuthState.LoggedIn(testUser)

        assertFalse(vm.hasUnseenSharedItems.value)
        assertEquals(0, vm.unseenSharedItemsCount.value)
        assertEquals(0, vm.unseenFriendRequestCount.value)
        assertEquals(emptyMap<String, Int>(), vm.uiState.value.unreadCountPerFriend)

        authStateFlow.value = AuthState.LoggedOut
        authStateFlow.value = AuthState.LoggedIn(testUser)

        assertFalse(vm.hasUnseenSharedItems.value)
        assertEquals(0, vm.unseenSharedItemsCount.value)
        assertEquals(0, vm.unseenFriendRequestCount.value)
        assertEquals(emptyMap<String, Int>(), vm.uiState.value.unreadCountPerFriend)
    }
}
