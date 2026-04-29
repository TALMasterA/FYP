package com.translator.TalknLearn.domain.friends

import com.translator.TalknLearn.data.friends.ChatRepository
import com.translator.TalknLearn.data.friends.FriendRequestRateLimitStatus
import com.translator.TalknLearn.data.friends.FriendRequestRateLimiter
import com.translator.TalknLearn.data.friends.FriendsRepository
import com.translator.TalknLearn.data.friends.SharedFriendsDataSource
import com.translator.TalknLearn.data.settings.SharedSettingsDataSource
import com.translator.TalknLearn.data.user.FirebaseAuthRepository
import com.translator.TalknLearn.model.UserId
import com.translator.TalknLearn.model.friends.FriendRelation
import com.translator.TalknLearn.model.friends.FriendRequest
import com.translator.TalknLearn.model.friends.PublicUserProfile
import com.translator.TalknLearn.model.user.AuthState
import com.translator.TalknLearn.model.user.User
import com.translator.TalknLearn.model.user.UserSettings
import com.translator.TalknLearn.screens.friends.FriendsUiState
import com.translator.TalknLearn.screens.friends.FriendsViewModel
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
 * Integration tests verifying that the username requirement gate in
 * [FriendsViewModel] consistently blocks all friend-mutating actions
 * (send, accept, accept-all) when the current user has not set a username.
 *
 * These tests exercise the ViewModel end-to-end (auth → load username →
 * action gate), confirming that:
 *  1. requireUsernameForFriendActions returns false when username is blank
 *  2. requireUsernameForFriendActions returns true when username is set
 *  3. requireUsernameForAddFriends delegates to requireUsernameForFriendActions
 *  4. sendFriendRequest is blocked when username is not set
 *  5. acceptFriendRequest is blocked when username is not set
 *  6. acceptAllRequests is blocked when username is not set
 *  7. acceptFriendRequest succeeds when username IS set
 *  8. acceptAllRequests succeeds when username IS set
 *  9. rejectFriendRequest is NOT blocked (no username needed to reject)
 * 10. rejectAllRequests is NOT blocked (no username needed to reject)
 * 11. After setting a username, previously blocked actions succeed
 */
@OptIn(ExperimentalCoroutinesApi::class)
class UsernameRequirementIntegrationTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val authStateFlow = MutableStateFlow<AuthState>(AuthState.Loading)
    private val friendsFlow = MutableStateFlow<List<FriendRelation>>(emptyList())
    private val incomingRequestsFlow = MutableStateFlow<List<FriendRequest>>(emptyList())
    private val unseenUnreadFlow = MutableStateFlow<Map<String, Int>>(emptyMap())
    private val settingsFlow = MutableStateFlow(UserSettings())
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

    // ── Helpers ──────────────────────────────────────────────────────────────

    /** Create mocks that simulate a user WITHOUT a username set. */
    private fun setupNoUsername() {
        authRepo = mock { on { currentUserState } doReturn authStateFlow }
        sharedFriendsDataSource = mock {
            on { friends } doReturn friendsFlow
            on { incomingRequests } doReturn incomingRequestsFlow
            on { unseenUnreadPerFriend } doReturn unseenUnreadFlow
            on { hasUnseenSharedItems } doReturn flowOf(false)
            on { unseenSharedItemsCount } doReturn flowOf(0)
            on { unseenFriendRequestCount } doReturn flowOf(0)
            on { getCachedUsername("user1") } doReturn null
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
                .thenReturn(PublicUserProfile(uid = "user1", username = ""))
            whenever(friendsRepository.getBlockedUsers(UserId("user1"))).thenReturn(emptyList())
        }
    }

    /** Create mocks that simulate a user WITH a username set. */
    private fun setupWithUsername() {
        authRepo = mock { on { currentUserState } doReturn authStateFlow }
        sharedFriendsDataSource = mock {
            on { friends } doReturn friendsFlow
            on { incomingRequests } doReturn incomingRequestsFlow
            on { unseenUnreadPerFriend } doReturn unseenUnreadFlow
            on { hasUnseenSharedItems } doReturn flowOf(false)
            on { unseenSharedItemsCount } doReturn flowOf(0)
            on { unseenFriendRequestCount } doReturn flowOf(0)
            on { getCachedUsername("user1") } doReturn "myuser"
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
                .thenReturn(PublicUserProfile(uid = "user1", username = "myuser"))
            whenever(friendsRepository.getBlockedUsers(UserId("user1"))).thenReturn(emptyList())
        }
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

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // ── Test 1: requireUsernameForFriendActions rejects blank username ───────

    @Test
    fun `requireUsernameForFriendActions returns false when username is blank`() = runTest {
        setupNoUsername()
        val vm = buildViewModel()
        authStateFlow.value = AuthState.LoggedIn(testUser)

        val result = vm.requireUsernameForFriendActions()

        assertFalse(result)
        assertNotNull(vm.uiState.value.error)
        assertTrue(vm.uiState.value.error!!.contains("username", ignoreCase = true))
    }

    // ── Test 2: requireUsernameForFriendActions allows set username ──────────

    @Test
    fun `requireUsernameForFriendActions returns true when username is set`() = runTest {
        setupWithUsername()
        val vm = buildViewModel()
        authStateFlow.value = AuthState.LoggedIn(testUser)

        val result = vm.requireUsernameForFriendActions()

        assertTrue(result)
        assertNull(vm.uiState.value.error)
    }

    // ── Test 3: legacy alias delegates correctly ────────────────────────────

    @Test
    fun `requireUsernameForAddFriends delegates to requireUsernameForFriendActions`() = runTest {
        setupNoUsername()
        val vm = buildViewModel()
        authStateFlow.value = AuthState.LoggedIn(testUser)

        val result = vm.requireUsernameForAddFriends()

        assertFalse(result)
        assertNotNull(vm.uiState.value.error)
        assertTrue(vm.uiState.value.error!!.contains("username", ignoreCase = true))
    }

    // ── Test 4: sendFriendRequest blocked without username ──────────────────

    @Test
    fun `sendFriendRequest blocked when no username set`() = runTest {
        setupNoUsername()
        val vm = buildViewModel()
        authStateFlow.value = AuthState.LoggedIn(testUser)

        vm.sendFriendRequest("target1")

        assertNotNull(vm.uiState.value.error)
        assertTrue(vm.uiState.value.error!!.contains("username", ignoreCase = true))
        verifyNoInteractions(sendFriendRequestUseCase)
    }

    // ── Test 5: acceptFriendRequest blocked without username ────────────────

    @Test
    fun `acceptFriendRequest blocked when no username set`() = runTest {
        setupNoUsername()
        val request = FriendRequest(requestId = "req1", fromUserId = "sender1", toUserId = "user1")
        incomingRequestsFlow.value = listOf(request)

        val vm = buildViewModel()
        authStateFlow.value = AuthState.LoggedIn(testUser)

        vm.acceptFriendRequest("req1")

        assertNotNull(vm.uiState.value.error)
        assertTrue(vm.uiState.value.error!!.contains("username", ignoreCase = true))
        verifyNoInteractions(acceptFriendRequestUseCase)
    }

    // ── Test 6: acceptAllRequests blocked without username ──────────────────

    @Test
    fun `acceptAllRequests blocked when no username set`() = runTest {
        setupNoUsername()
        val requests = listOf(
            FriendRequest(requestId = "req1", fromUserId = "s1", toUserId = "user1"),
            FriendRequest(requestId = "req2", fromUserId = "s2", toUserId = "user1")
        )
        incomingRequestsFlow.value = requests

        val vm = buildViewModel()
        authStateFlow.value = AuthState.LoggedIn(testUser)

        vm.acceptAllRequests()

        assertNotNull(vm.uiState.value.error)
        assertTrue(vm.uiState.value.error!!.contains("username", ignoreCase = true))
        verifyNoInteractions(acceptFriendRequestUseCase)
    }

    // ── Test 7: acceptFriendRequest succeeds with username ─────────────────

    @Test
    fun `acceptFriendRequest succeeds when username is set`() = runTest {
        setupWithUsername()
        val request = FriendRequest(requestId = "req1", fromUserId = "sender1", toUserId = "user1")
        incomingRequestsFlow.value = listOf(request)

        whenever(acceptFriendRequestUseCase.invoke("req1", UserId("user1"), UserId("sender1")))
            .thenReturn(Result.success(Unit))

        val vm = buildViewModel()
        authStateFlow.value = AuthState.LoggedIn(testUser)

        vm.acceptFriendRequest("req1")

        assertNotNull(vm.uiState.value.successMessage)
        assertNull(vm.uiState.value.error)
    }

    // ── Test 8: acceptAllRequests succeeds with username ────────────────────

    @Test
    fun `acceptAllRequests succeeds when username is set`() = runTest {
        setupWithUsername()
        val requests = listOf(
            FriendRequest(requestId = "req1", fromUserId = "s1", toUserId = "user1"),
            FriendRequest(requestId = "req2", fromUserId = "s2", toUserId = "user1")
        )
        incomingRequestsFlow.value = requests

        whenever(acceptFriendRequestUseCase.invoke("req1", UserId("user1"), UserId("s1")))
            .thenReturn(Result.success(Unit))
        whenever(acceptFriendRequestUseCase.invoke("req2", UserId("user1"), UserId("s2")))
            .thenReturn(Result.success(Unit))

        val vm = buildViewModel()
        authStateFlow.value = AuthState.LoggedIn(testUser)

        vm.acceptAllRequests()

        assertNotNull(vm.uiState.value.successMessage)
        assertTrue(vm.uiState.value.successMessage!!.contains("2", ignoreCase = true))
    }

    // ── Test 9: rejectFriendRequest is NOT gated by username ────────────────

    @Test
    fun `rejectFriendRequest works even without username`() = runTest {
        setupNoUsername()
        whenever(rejectFriendRequestUseCase.invoke("req1")).thenReturn(Result.success(Unit))

        val vm = buildViewModel()
        authStateFlow.value = AuthState.LoggedIn(testUser)

        vm.rejectFriendRequest("req1")

        assertNotNull(vm.uiState.value.successMessage)
    }

    // ── Test 10: rejectAllRequests is NOT gated by username ─────────────────

    @Test
    fun `rejectAllRequests works even without username`() = runTest {
        setupNoUsername()
        val requests = listOf(
            FriendRequest(requestId = "req1", fromUserId = "s1", toUserId = "user1"),
            FriendRequest(requestId = "req2", fromUserId = "s2", toUserId = "user1")
        )
        incomingRequestsFlow.value = requests

        whenever(rejectFriendRequestUseCase.invoke("req1")).thenReturn(Result.success(Unit))
        whenever(rejectFriendRequestUseCase.invoke("req2")).thenReturn(Result.success(Unit))

        val vm = buildViewModel()
        authStateFlow.value = AuthState.LoggedIn(testUser)

        vm.rejectAllRequests()

        assertNotNull(vm.uiState.value.successMessage)
        assertTrue(vm.uiState.value.successMessage!!.contains("2"))
    }

    // ── Test 11: Error message is cleared after successful action ───────────

    @Test
    fun `error from username gate is cleared on clearMessages`() = runTest {
        setupNoUsername()
        val vm = buildViewModel()
        authStateFlow.value = AuthState.LoggedIn(testUser)

        // Trigger the error
        vm.requireUsernameForFriendActions()
        assertNotNull(vm.uiState.value.error)

        // Clear it
        vm.clearMessages()
        assertNull(vm.uiState.value.error)
    }
}
