package com.example.fyp.appstate

import android.app.Application
import com.example.fyp.core.FcmNotificationService
import com.example.fyp.data.friends.ChatRepository
import com.example.fyp.data.friends.FriendsRepository
import com.example.fyp.data.wordbank.WordBankCacheDataStore
import com.example.fyp.data.friends.SharedFriendsDataSource
import com.example.fyp.data.history.SharedHistoryDataSource
import com.example.fyp.data.settings.SharedSettingsDataSource
import com.example.fyp.data.user.FirebaseAuthRepository
import com.example.fyp.domain.friends.EnsurePublicProfileExistsUseCase
import com.example.fyp.model.UserId
import com.example.fyp.model.user.AuthState
import com.example.fyp.model.user.User
import com.example.fyp.model.user.UserSettings
import com.example.fyp.model.friends.PublicUserProfile
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.yield
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.MockedStatic
import org.mockito.Mockito
import org.mockito.kotlin.*

/**
 * Unit tests for AppViewModel — the application-level ViewModel managing auth state,
 * notification badges, shared data source lifecycle, and user profile initialization.
 *
 * Tests:
 *  1. LoggedIn starts shared data sources (friends, settings, history)
 *  2. LoggedIn initializes user profile via EnsurePublicProfileExistsUseCase
 *  3. LoggedIn observes unread messages from ChatRepository
 *  4. LoggedIn fetches username from FriendsRepository
 *  5. LoggedOut stops all shared data sources
 *  6. LoggedOut cancels unread/username jobs and resets counts
 *  7. LoggedOut preserves seen state for previous user
 *  8. pendingFriendRequestCount gated by inAppBadgeFriendRequests setting
 *  9. hasUnseenSharedItems gated by inAppBadgeSharedInbox setting
 * 10. unseenSharedItemsCount gated by inAppBadgeSharedInbox setting
 * 11. hasUnreadMessages gated by inAppBadgeMessages setting
 * 12. Re-login with same user ID is idempotent (no duplicate init)
 * 13. Re-login with different user ID reinitializes
 * 14. Loading state is ignored (no side effects)
 */
@OptIn(ExperimentalCoroutinesApi::class)
class AppViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val authStateFlow = MutableStateFlow<AuthState>(AuthState.Loading)
    private val settingsFlow = MutableStateFlow(UserSettings())
    private val unseenFriendRequestCountFlow = MutableStateFlow(0)
    private val hasUnseenSharedItemsFlow = MutableStateFlow(false)
    private val unseenSharedItemsCountFlow = MutableStateFlow(0)
    private val unseenUnreadPerFriendFlow = MutableStateFlow<Map<String, Int>>(emptyMap())
    private val unreadPerFriendFlow = MutableStateFlow<Map<String, Int>>(emptyMap())

    private val testUserId = "user123"
    private val testUser = User(uid = testUserId, email = "test@test.com")

    private lateinit var app: Application
    private lateinit var authRepo: FirebaseAuthRepository
    private lateinit var ensureProfile: EnsurePublicProfileExistsUseCase
    private lateinit var sharedFriends: SharedFriendsDataSource
    private lateinit var sharedSettings: SharedSettingsDataSource
    private lateinit var sharedHistory: SharedHistoryDataSource
    private lateinit var chatRepo: ChatRepository
    private lateinit var friendsRepo: FriendsRepository
    private lateinit var wordBankCacheDataStore: WordBankCacheDataStore
    private lateinit var fcmMock: MockedStatic<FcmNotificationService>
    private lateinit var firebaseAuthMock: MockedStatic<FirebaseAuth>

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        // Mock static Firebase methods to prevent initialization errors.
        // FcmNotificationService.uploadTokenIfLoggedIn calls FirebaseAuth.getInstance().
        val mockAuth: FirebaseAuth = mock()
        whenever(mockAuth.currentUser).thenReturn(null) // early-return path
        firebaseAuthMock = Mockito.mockStatic(FirebaseAuth::class.java)
        firebaseAuthMock.`when`<FirebaseAuth> { FirebaseAuth.getInstance() }.thenReturn(mockAuth)

        fcmMock = Mockito.mockStatic(FcmNotificationService::class.java)

        app = mock()

        authRepo = mock { on { currentUserState } doReturn authStateFlow }

        ensureProfile = mock()

        sharedFriends = mock {
            on { unseenFriendRequestCount } doReturn unseenFriendRequestCountFlow
            on { hasUnseenSharedItems } doReturn hasUnseenSharedItemsFlow
            on { unseenSharedItemsCount } doReturn unseenSharedItemsCountFlow
            on { unseenUnreadPerFriend } doReturn unseenUnreadPerFriendFlow
        }

        sharedSettings = mock {
            on { settings } doReturn settingsFlow
        }

        sharedHistory = mock()

        chatRepo = mock {
            on { observeUnreadPerFriend(UserId(testUserId)) } doReturn unreadPerFriendFlow
        }

        friendsRepo = mock()
        wordBankCacheDataStore = mock()
    }

    @After
    fun tearDown() {
        fcmMock.close()
        firebaseAuthMock.close()
        Dispatchers.resetMain()
    }

    private fun buildViewModel() = AppViewModel(
        application = app,
        authRepository = authRepo,
        ensurePublicProfileExistsUseCase = ensureProfile,
        sharedFriendsDataSource = sharedFriends,
        sharedSettingsDataSource = sharedSettings,
        sharedHistoryDataSource = sharedHistory,
        chatRepository = chatRepo,
        friendsRepository = friendsRepo,
        wordBankCacheDataStore = wordBankCacheDataStore
    )

    // ── Test 1: LoggedIn starts shared data sources ──

    @Test
    fun `login starts all shared data sources`() = runTest {
        val vm = buildViewModel()
        authStateFlow.value = AuthState.LoggedIn(testUser)

        verify(sharedFriends).startObserving(testUserId)
        verify(sharedSettings).startObserving(testUserId)
        verify(sharedHistory).startObserving(testUserId)
    }

    // ── Test 2: LoggedIn initializes user profile ──

    @Test
    fun `login initializes user profile`() = runTest {
        val vm = buildViewModel()
        authStateFlow.value = AuthState.LoggedIn(testUser)

        verify(ensureProfile).invoke(eq(testUserId), any())
    }

    // ── Test 3: LoggedIn observes unread messages ──

    @Test
    fun `login observes unread messages`() = runTest {
        val vm = buildViewModel()
        authStateFlow.value = AuthState.LoggedIn(testUser)

        verify(chatRepo).observeUnreadPerFriend(UserId(testUserId))
    }

    // ── Test 4: LoggedIn fetches username ──

    @Test
    fun `login fetches username from public profile`() = runTest {
        whenever(friendsRepo.getPublicProfile(UserId(testUserId)))
            .thenReturn(PublicUserProfile(uid = testUserId, username = "testuser"))

        val vm = buildViewModel()
        authStateFlow.value = AuthState.LoggedIn(testUser)

        assertEquals("testuser", vm.currentUsername.value)
    }

    @Test
    fun `login with blank username sets null`() = runTest {
        whenever(friendsRepo.getPublicProfile(UserId(testUserId)))
            .thenReturn(PublicUserProfile(uid = testUserId, username = ""))

        val vm = buildViewModel()
        authStateFlow.value = AuthState.LoggedIn(testUser)

        assertNull(vm.currentUsername.value)
    }

    // ── Test 5: LoggedOut stops shared data sources ──

    @Test
    fun `logout stops all shared data sources`() = runTest {
        val vm = buildViewModel()
        authStateFlow.value = AuthState.LoggedIn(testUser)
        authStateFlow.value = AuthState.LoggedOut

        verify(sharedFriends).stopObserving()
        verify(sharedSettings).stopObserving()
        verify(sharedHistory).stopObserving()
    }

    // ── Test 6: LoggedOut resets unread counts ──

    @Test
    fun `logout resets unread and username state`() = runTest {
        whenever(friendsRepo.getPublicProfile(UserId(testUserId)))
            .thenReturn(PublicUserProfile(uid = testUserId, username = "testuser"))

        val vm = buildViewModel()
        authStateFlow.value = AuthState.LoggedIn(testUser)
        assertEquals("testuser", vm.currentUsername.value)

        authStateFlow.value = AuthState.LoggedOut

        assertFalse(vm.hasUnreadMessages.value)
        assertEquals(0, vm.unreadMessageCount.value)
        assertNull(vm.currentUsername.value)
    }

    // ── Test 8: pendingFriendRequestCount gated by setting ──

    @Test
    fun `pendingFriendRequestCount returns 0 when badge disabled`() = runTest {
        val vm = buildViewModel()
        authStateFlow.value = AuthState.LoggedIn(testUser)

        unseenFriendRequestCountFlow.value = 3
        settingsFlow.value = UserSettings(inAppBadgeFriendRequests = false)

        assertEquals(0, vm.pendingFriendRequestCount.value)
    }

    @Test
    fun `pendingFriendRequestCount returns count when badge enabled`() = runTest {
        val vm = buildViewModel()
        authStateFlow.value = AuthState.LoggedIn(testUser)

        unseenFriendRequestCountFlow.value = 3
        settingsFlow.value = UserSettings(inAppBadgeFriendRequests = true)

        assertEquals(3, vm.pendingFriendRequestCount.value)
    }

    // ── Test 9: hasUnseenSharedItems gated by setting ──

    @Test
    fun `hasUnseenSharedItems false when badge disabled`() = runTest {
        val vm = buildViewModel()
        authStateFlow.value = AuthState.LoggedIn(testUser)

        hasUnseenSharedItemsFlow.value = true
        settingsFlow.value = UserSettings(inAppBadgeSharedInbox = false)

        assertFalse(vm.hasUnseenSharedItems.value)
    }

    @Test
    fun `hasUnseenSharedItems true when badge enabled and items exist`() = runTest {
        val vm = buildViewModel()
        authStateFlow.value = AuthState.LoggedIn(testUser)

        hasUnseenSharedItemsFlow.value = true
        settingsFlow.value = UserSettings(inAppBadgeSharedInbox = true)

        assertTrue(vm.hasUnseenSharedItems.value)
    }

    // ── Test 10: unseenSharedItemsCount gated by setting ──

    @Test
    fun `unseenSharedItemsCount returns 0 when badge disabled`() = runTest {
        val vm = buildViewModel()
        authStateFlow.value = AuthState.LoggedIn(testUser)

        unseenSharedItemsCountFlow.value = 5
        settingsFlow.value = UserSettings(inAppBadgeSharedInbox = false)

        assertEquals(0, vm.unseenSharedItemsCount.value)
    }

    // ── Test 12: Re-login with same user is idempotent ──

    @Test
    fun `re-login with same user does not reinitialize`() = runTest {
        val vm = buildViewModel()
        authStateFlow.value = AuthState.LoggedIn(testUser)
        authStateFlow.value = AuthState.LoggedIn(testUser) // same user

        // ensureProfile should only be called once
        verify(ensureProfile, times(1)).invoke(eq(testUserId), any())
    }

    // ── Test 13: Re-login with different user reinitializes ──

    @Test
    fun `login with different user reinitializes`() = runTest {
        val otherUser = User(uid = "other456", email = "other@test.com")

        whenever(chatRepo.observeUnreadPerFriend(UserId("other456")))
            .thenReturn(flowOf(emptyMap()))

        val vm = buildViewModel()
        authStateFlow.value = AuthState.LoggedIn(testUser)
        authStateFlow.value = AuthState.LoggedIn(otherUser)

        verify(ensureProfile).invoke(eq(testUserId), any())
        verify(ensureProfile).invoke(eq("other456"), any())
    }

    // ── Test 14: Loading state has no side effects ──

    @Test
    fun `loading state does not trigger any operations`() = runTest {
        val vm = buildViewModel()
        // authStateFlow starts as Loading

        verify(sharedFriends, never()).startObserving(testUserId)
        verify(sharedSettings, never()).startObserving(testUserId)
        verify(sharedHistory, never()).startObserving(testUserId)
        verifyNoInteractions(ensureProfile)
    }

    @Test
    fun `logout keeps unread badges reset when unseen map emits afterward`() = runTest {
        val vm = buildViewModel()
        authStateFlow.value = AuthState.LoggedIn(testUser)

        unseenUnreadPerFriendFlow.value = mapOf("friendA" to 2)
        settingsFlow.value = UserSettings(inAppBadgeMessages = true)
        yield()
        assertEquals(2, vm.unreadMessageCount.value)
        assertTrue(vm.hasUnreadMessages.value)

        authStateFlow.value = AuthState.LoggedOut
        assertEquals(0, vm.unreadMessageCount.value)
        assertFalse(vm.hasUnreadMessages.value)

        // Regression guard: old behavior could revive badges after logout because
        // the second unread collector wasn't tracked/cancelled.
        unseenUnreadPerFriendFlow.value = mapOf("friendA" to 5)
        yield()
        assertEquals(0, vm.unreadMessageCount.value)
        assertFalse(vm.hasUnreadMessages.value)
    }

    @Test
    fun `switching user does not duplicate unread badge collectors`() = runTest {
        val vm = buildViewModel()
        authStateFlow.value = AuthState.LoggedIn(testUser)
        unseenUnreadPerFriendFlow.value = mapOf("friendA" to 3)
        settingsFlow.value = UserSettings(inAppBadgeMessages = true)
        yield()
        assertEquals(3, vm.unreadMessageCount.value)

        val otherUser = User(uid = "other456", email = "other@test.com")
        whenever(chatRepo.observeUnreadPerFriend(UserId("other456")))
            .thenReturn(flowOf(emptyMap()))

        authStateFlow.value = AuthState.LoggedIn(otherUser)
        yield()
        assertEquals(3, vm.unreadMessageCount.value)

        // With duplicated collectors, disabling badge could race/flip unexpectedly.
        settingsFlow.value = UserSettings(inAppBadgeMessages = false)
        yield()
        assertEquals(0, vm.unreadMessageCount.value)
        assertFalse(vm.hasUnreadMessages.value)
    }
}
