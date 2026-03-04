package com.example.fyp.screens.friends

import com.example.fyp.data.friends.ChatRepository
import com.example.fyp.data.friends.FriendsRepository
import com.example.fyp.data.friends.SharedFriendsDataSource
import com.example.fyp.data.user.FirebaseAuthRepository
import com.example.fyp.domain.friends.*
import com.example.fyp.model.UserId
import com.example.fyp.model.friends.FriendRelation
import com.example.fyp.model.friends.FriendRequest
import com.example.fyp.model.friends.PublicUserProfile
import com.example.fyp.model.user.AuthState
import com.example.fyp.model.user.User
import com.google.firebase.Timestamp
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
 * Unit tests for FriendsViewModel.
 *
 * Tests:
 * 1. Login subscribes to shared data and starts outgoing requests observer
 * 2. Logout resets state
 * 3. Search with <3 chars clears results
 * 4. sendFriendRequest without username shows error
 * 5. sendFriendRequest success clears search
 * 6. sendFriendRequest duplicate shows specific error
 * 7. acceptFriendRequest success shows success message
 * 8. rejectFriendRequest success shows success message
 * 9. cancelFriendRequest success shows success message
 * 10. removeFriend optimistically removes from list
 * 11. toggleDeleteMode enters/exits delete mode
 * 12. toggleFriendSelection adds/removes selection
 * 13. getRequestStatusFor returns correct status
 * 14. blockAndRemoveFriend updates blocked list
 * 15. unblockUser removes from blocked list
 * 16. Max pending requests limit enforced
 * 17. clearMessages clears error and success
 * 18. acceptAllRequests handles partial failures
 */
@OptIn(ExperimentalCoroutinesApi::class)
class FriendsViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val authStateFlow = MutableStateFlow<AuthState>(AuthState.Loading)
    private val friendsFlow = MutableStateFlow<List<FriendRelation>>(emptyList())
    private val incomingRequestsFlow = MutableStateFlow<List<FriendRequest>>(emptyList())
    private val unseenUnreadFlow = MutableStateFlow<Map<String, Int>>(emptyMap())

    private lateinit var authRepo: FirebaseAuthRepository
    private lateinit var sharedFriendsDataSource: SharedFriendsDataSource
    private lateinit var chatRepository: ChatRepository
    private lateinit var friendsRepository: FriendsRepository
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
            on { unseenUnreadPerFriend } doReturn unseenUnreadFlow
            on { hasUnseenSharedItems } doReturn flowOf(false)
            on { unseenSharedItemsCount } doReturn flowOf(0)
            on { unseenFriendRequestCount } doReturn flowOf(0)
            on { getCachedUsername("user1") } doReturn "myuser"
        }
        chatRepository = mock()
        friendsRepository = mock()
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

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun buildViewModel() = FriendsViewModel(
        authRepo = authRepo,
        sharedFriendsDataSource = sharedFriendsDataSource,
        chatRepository = chatRepository,
        friendsRepository = friendsRepository,
        observeOutgoingRequestsUseCase = observeOutgoingRequestsUseCase,
        searchUsersUseCase = searchUsersUseCase,
        sendFriendRequestUseCase = sendFriendRequestUseCase,
        acceptFriendRequestUseCase = acceptFriendRequestUseCase,
        rejectFriendRequestUseCase = rejectFriendRequestUseCase,
        cancelFriendRequestUseCase = cancelFriendRequestUseCase,
        removeFriendUseCase = removeFriendUseCase
    )

    // ── Login subscribes to shared data ─────────────────────────────

    @Test
    fun `login starts observing and loading stops`() = runTest {
        val vm = buildViewModel()
        friendsFlow.value = listOf(
            FriendRelation(friendId = "f1", friendUsername = "Friend1")
        )
        authStateFlow.value = AuthState.LoggedIn(testUser)

        val state = vm.uiState.value
        assertFalse(state.isLoading)
        assertEquals(1, state.friends.size)
    }

    // ── Logout resets state ─────────────────────────────────────────

    @Test
    fun `logout resets ui state`() = runTest {
        val vm = buildViewModel()
        authStateFlow.value = AuthState.LoggedIn(testUser)
        authStateFlow.value = AuthState.LoggedOut

        val state = vm.uiState.value
        assertFalse(state.isLoading)
        assertTrue(state.friends.isEmpty())
    }

    // ── Search with <3 chars clears results ─────────────────────────

    @Test
    fun `search query under 3 chars clears results`() = runTest {
        val vm = buildViewModel()
        authStateFlow.value = AuthState.LoggedIn(testUser)

        vm.onSearchQueryChange("ab")

        assertTrue(vm.uiState.value.searchResults.isEmpty())
        assertFalse(vm.uiState.value.isSearching)
    }

    // ── sendFriendRequest without username ───────────────────────────

    @Test
    fun `sendFriendRequest without username shows error`() = runTest {
        whenever(sharedFriendsDataSource.getCachedUsername("user1")).thenReturn(null)
        runTest {
            whenever(friendsRepository.getPublicProfile(UserId("user1")))
                .thenReturn(PublicUserProfile(uid = "user1", username = ""))
        }

        val vm = buildViewModel()
        authStateFlow.value = AuthState.LoggedIn(testUser)

        vm.sendFriendRequest("target1")

        assertNotNull(vm.uiState.value.error)
        assertTrue(vm.uiState.value.error!!.contains("username", ignoreCase = true))
    }

    // ── sendFriendRequest success ───────────────────────────────────

    @Test
    fun `sendFriendRequest success shows success message`() = runTest {
        whenever(sendFriendRequestUseCase.invoke(eq(UserId("user1")), eq(UserId("target1")), any()))
            .thenReturn(Result.success(Unit))

        val vm = buildViewModel()
        authStateFlow.value = AuthState.LoggedIn(testUser)

        vm.sendFriendRequest("target1")

        assertNotNull(vm.uiState.value.successMessage)
        assertNull(vm.uiState.value.error)
    }

    // ── sendFriendRequest duplicate ─────────────────────────────────

    @Test
    fun `sendFriendRequest duplicate shows specific error`() = runTest {
        whenever(sendFriendRequestUseCase.invoke(eq(UserId("user1")), eq(UserId("target1")), any()))
            .thenReturn(Result.failure(RuntimeException("already sent")))

        val vm = buildViewModel()
        authStateFlow.value = AuthState.LoggedIn(testUser)

        vm.sendFriendRequest("target1")

        assertNotNull(vm.uiState.value.error)
        assertTrue(vm.uiState.value.error!!.contains("pending", ignoreCase = true))
    }

    // ── acceptFriendRequest success ─────────────────────────────────

    @Test
    fun `acceptFriendRequest success shows success message`() = runTest {
        val request = FriendRequest(requestId = "req1", fromUserId = "sender1", toUserId = "user1")
        incomingRequestsFlow.value = listOf(request)

        whenever(acceptFriendRequestUseCase.invoke(eq("req1"), eq(UserId("user1")), eq(UserId("sender1"))))
            .thenReturn(Result.success(Unit))

        val vm = buildViewModel()
        authStateFlow.value = AuthState.LoggedIn(testUser)

        vm.acceptFriendRequest("req1")

        assertNotNull(vm.uiState.value.successMessage)
        assertNull(vm.uiState.value.error)
    }

    // ── rejectFriendRequest success ─────────────────────────────────

    @Test
    fun `rejectFriendRequest success shows success message`() = runTest {
        whenever(rejectFriendRequestUseCase.invoke(any()))
            .thenReturn(Result.success(Unit))

        val vm = buildViewModel()
        authStateFlow.value = AuthState.LoggedIn(testUser)

        vm.rejectFriendRequest("req1")

        assertNotNull(vm.uiState.value.successMessage)
    }

    // ── cancelFriendRequest success ─────────────────────────────────

    @Test
    fun `cancelFriendRequest success shows success message`() = runTest {
        whenever(cancelFriendRequestUseCase.invoke(any()))
            .thenReturn(Result.success(Unit))

        val vm = buildViewModel()
        authStateFlow.value = AuthState.LoggedIn(testUser)

        vm.cancelFriendRequest("req1")

        assertNotNull(vm.uiState.value.successMessage)
    }

    // ── removeFriend optimistically removes from list ────────────────

    @Test
    fun `removeFriend optimistically removes from friends list`() = runTest {
        friendsFlow.value = listOf(
            FriendRelation(friendId = "f1", friendUsername = "Friend1"),
            FriendRelation(friendId = "f2", friendUsername = "Friend2")
        )
        whenever(removeFriendUseCase.invoke(UserId("user1"), UserId("f1")))
            .thenReturn(Result.success(Unit))
        whenever(friendsRepository.syncFriendUsernames(UserId("user1"))).thenReturn(emptyMap())

        val vm = buildViewModel()
        authStateFlow.value = AuthState.LoggedIn(testUser)

        vm.removeFriend("f1")

        // f1 should be removed optimistically
        val friends = vm.uiState.value.friends
        assertTrue(friends.none { it.friendId == "f1" })
    }

    // ── toggleDeleteMode ────────────────────────────────────────────

    @Test
    fun `toggleDeleteMode enters and exits delete mode`() = runTest {
        val vm = buildViewModel()
        authStateFlow.value = AuthState.LoggedIn(testUser)

        assertFalse(vm.uiState.value.isDeleteMode)

        vm.toggleDeleteMode()
        assertTrue(vm.uiState.value.isDeleteMode)

        // With no selections, toggleDeleteMode exits
        vm.toggleDeleteMode()
        assertFalse(vm.uiState.value.isDeleteMode)
    }

    // ── toggleFriendSelection ───────────────────────────────────────

    @Test
    fun `toggleFriendSelection adds and removes selection`() = runTest {
        val vm = buildViewModel()
        authStateFlow.value = AuthState.LoggedIn(testUser)

        vm.toggleDeleteMode()

        vm.toggleFriendSelection("f1")
        assertTrue(vm.uiState.value.selectedFriendIds.contains("f1"))

        vm.toggleFriendSelection("f1")
        assertFalse(vm.uiState.value.selectedFriendIds.contains("f1"))
    }

    // ── getRequestStatusFor ─────────────────────────────────────────

    @Test
    fun `getRequestStatusFor returns ALREADY_FRIENDS for existing friend`() = runTest {
        friendsFlow.value = listOf(FriendRelation(friendId = "f1"))

        val vm = buildViewModel()
        authStateFlow.value = AuthState.LoggedIn(testUser)

        assertEquals(
            com.example.fyp.screens.friends.RequestStatus.ALREADY_FRIENDS,
            vm.getRequestStatusFor("f1")
        )
    }

    @Test
    fun `getRequestStatusFor returns NONE for unknown user`() = runTest {
        val vm = buildViewModel()
        authStateFlow.value = AuthState.LoggedIn(testUser)

        assertEquals(
            com.example.fyp.screens.friends.RequestStatus.NONE,
            vm.getRequestStatusFor("unknown")
        )
    }

    // ── blockAndRemoveFriend ────────────────────────────────────────

    @Test
    fun `blockAndRemoveFriend updates blocked list and removes friend`() = runTest {
        friendsFlow.value = listOf(FriendRelation(friendId = "f1", friendUsername = "Friend1"))
        whenever(removeFriendUseCase.invoke(UserId("user1"), UserId("f1"))).thenReturn(Result.success(Unit))
        whenever(friendsRepository.blockUser(eq(UserId("user1")), eq(UserId("f1")), eq("Friend1"))).thenReturn(Result.success(Unit))

        val vm = buildViewModel()
        authStateFlow.value = AuthState.LoggedIn(testUser)

        vm.blockAndRemoveFriend("f1", "Friend1")

        assertTrue(vm.uiState.value.blockedUserIds.contains("f1"))
        assertTrue(vm.uiState.value.friends.none { it.friendId == "f1" })
    }

    // ── unblockUser ─────────────────────────────────────────────────

    @Test
    fun `unblockUser removes from blocked list`() = runTest {
        whenever(friendsRepository.unblockUser(UserId("user1"), UserId("f1"))).thenReturn(Result.success(Unit))

        val vm = buildViewModel()
        authStateFlow.value = AuthState.LoggedIn(testUser)

        // First block
        whenever(friendsRepository.blockUser(eq(UserId("user1")), eq(UserId("f1")), any())).thenReturn(Result.success(Unit))
        vm.blockUser("f1")
        assertTrue(vm.isUserBlocked("f1"))

        // Then unblock
        vm.unblockUser("f1")
        assertFalse(vm.isUserBlocked("f1"))
    }

    // ── Max pending requests limit ──────────────────────────────────

    @Test
    fun `sendFriendRequest blocked when max pending reached`() = runTest {
        // Set up 20 pending requests via the outgoing requests flow
        val requests = (1..20).map {
            FriendRequest(requestId = "req$it", fromUserId = "user1", toUserId = "target$it")
        }
        whenever(observeOutgoingRequestsUseCase.invoke(UserId("user1")))
            .thenReturn(flowOf(requests))

        val vm = buildViewModel()
        authStateFlow.value = AuthState.LoggedIn(testUser)

        vm.sendFriendRequest("newTarget")

        assertNotNull(vm.uiState.value.error)
        assertTrue(vm.uiState.value.error!!.contains("maximum", ignoreCase = true))
        verifyNoInteractions(sendFriendRequestUseCase)
    }

    // ── clearMessages ───────────────────────────────────────────────

    @Test
    fun `clearMessages clears error and success`() = runTest {
        whenever(sendFriendRequestUseCase.invoke(eq(UserId("user1")), eq(UserId("target1")), any()))
            .thenReturn(Result.success(Unit))

        val vm = buildViewModel()
        authStateFlow.value = AuthState.LoggedIn(testUser)

        vm.sendFriendRequest("target1")
        assertNotNull(vm.uiState.value.successMessage)

        vm.clearMessages()
        assertNull(vm.uiState.value.error)
        assertNull(vm.uiState.value.successMessage)
    }

    // ── acceptAllRequests handles partial failures ───────────────────

    @Test
    fun `acceptAllRequests reports partial failures`() = runTest {
        val requests = listOf(
            FriendRequest(requestId = "req1", fromUserId = "s1", toUserId = "user1"),
            FriendRequest(requestId = "req2", fromUserId = "s2", toUserId = "user1")
        )
        incomingRequestsFlow.value = requests

        whenever(acceptFriendRequestUseCase.invoke(eq("req1"), eq(UserId("user1")), eq(UserId("s1"))))
            .thenReturn(Result.success(Unit))
        whenever(acceptFriendRequestUseCase.invoke(eq("req2"), eq(UserId("user1")), eq(UserId("s2"))))
            .thenReturn(Result.failure(RuntimeException("fail")))

        val vm = buildViewModel()
        authStateFlow.value = AuthState.LoggedIn(testUser)

        vm.acceptAllRequests()

        // Should report partial success
        val state = vm.uiState.value
        assertNotNull(state.successMessage)
        assertTrue(state.successMessage!!.contains("1 accepted", ignoreCase = true))
    }

    // ── requireUsernameForAddFriends ────────────────────────────────

    @Test
    fun `requireUsernameForAddFriends returns false and sets error when no username`() = runTest {
        whenever(sharedFriendsDataSource.getCachedUsername("user1")).thenReturn(null)
        runTest {
            whenever(friendsRepository.getPublicProfile(UserId("user1")))
                .thenReturn(PublicUserProfile(uid = "user1", username = ""))
        }

        val vm = buildViewModel()
        authStateFlow.value = AuthState.LoggedIn(testUser)

        val result = vm.requireUsernameForAddFriends()

        assertFalse(result)
        assertNotNull(vm.uiState.value.error)
    }

    // ── exitDeleteMode ──────────────────────────────────────────────

    @Test
    fun `exitDeleteMode clears selections and mode`() = runTest {
        val vm = buildViewModel()
        authStateFlow.value = AuthState.LoggedIn(testUser)

        vm.toggleDeleteMode()
        vm.toggleFriendSelection("f1")

        vm.exitDeleteMode()

        assertFalse(vm.uiState.value.isDeleteMode)
        assertTrue(vm.uiState.value.selectedFriendIds.isEmpty())
    }
}
