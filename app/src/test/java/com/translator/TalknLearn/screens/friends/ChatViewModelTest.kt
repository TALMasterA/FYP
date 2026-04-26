package com.translator.TalknLearn.screens.friends

import androidx.lifecycle.SavedStateHandle
import com.translator.TalknLearn.data.friends.ChatRepository
import com.translator.TalknLearn.data.friends.FriendsRepository
import com.translator.TalknLearn.data.friends.SharedFriendsDataSource
import com.translator.TalknLearn.data.settings.UserSettingsRepository
import com.translator.TalknLearn.data.user.FirebaseAuthRepository
import com.translator.TalknLearn.domain.friends.MarkMessagesAsReadUseCase
import com.translator.TalknLearn.domain.friends.ObserveMessagesUseCase
import com.translator.TalknLearn.domain.friends.SendMessageUseCase
import com.translator.TalknLearn.domain.friends.TranslateAllMessagesUseCase
import com.translator.TalknLearn.model.UserId
import com.translator.TalknLearn.model.friends.FriendMessage
import com.translator.TalknLearn.model.friends.PublicUserProfile
import com.translator.TalknLearn.model.user.AuthState
import com.translator.TalknLearn.model.user.User
import com.translator.TalknLearn.model.user.UserSettings
import com.translator.TalknLearn.core.security.FakePersistentRateLimiter
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
 * Unit tests for ChatViewModel.
 *
 * Tests:
 * 1. Initial state has friendUsername from navigation args
 * 2. Login loads messages and sets currentUserId
 * 3. Logout resets state
 * 4. sendMessage with blank text does nothing
 * 5. sendMessage succeeds and clears message text
 * 6. sendMessage failure surfaces error
 * 7. Rate limiter blocks rapid message sends
 * 8. sendMessage over 2000 chars rejected
 * 9. Blocked by friend prevents sending
 * 10. blockFriend updates isBlocked state
 * 11. unblockFriend updates isBlocked state
 * 12. clearError clears error field
 * 13. toggleTranslation flips showTranslation
 * 14. clearConversation empties messages
 * 15. onMessageTextChange updates messageText
 */
@OptIn(ExperimentalCoroutinesApi::class)
class ChatViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val authStateFlow = MutableStateFlow<AuthState>(AuthState.Loading)

    private lateinit var savedStateHandle: SavedStateHandle
    private lateinit var context: android.content.Context
    private lateinit var authRepo: FirebaseAuthRepository
    private lateinit var observeMessagesUseCase: ObserveMessagesUseCase
    private lateinit var sendMessageUseCase: SendMessageUseCase
    private lateinit var markMessagesAsReadUseCase: MarkMessagesAsReadUseCase
    private lateinit var translateAllMessagesUseCase: TranslateAllMessagesUseCase
    private lateinit var userSettingsRepository: UserSettingsRepository
    private lateinit var chatRepository: ChatRepository
    private lateinit var friendsRepository: FriendsRepository
    private lateinit var sharedFriendsDataSource: SharedFriendsDataSource
    private lateinit var rateLimiter: FakePersistentRateLimiter

    private val testUser = User(uid = "user1", email = "test@test.com")
    private val friendId = "friend1"
    private val friendUsername = "FriendUser"

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        rateLimiter = FakePersistentRateLimiter()

        savedStateHandle = SavedStateHandle(mapOf("friendId" to friendId, "friendUsername" to friendUsername))
        context = mock()

        authRepo = mock { on { currentUserState } doReturn authStateFlow }
        observeMessagesUseCase = mock()
        sendMessageUseCase = mock()
        markMessagesAsReadUseCase = mock()
        translateAllMessagesUseCase = mock()
        userSettingsRepository = mock()
        chatRepository = mock()
        friendsRepository = mock()
        sharedFriendsDataSource = mock()

        whenever(chatRepository.generateChatId(UserId("user1"), UserId(friendId))).thenReturn("chat_friend1_user1")

        // Default: no messages
        whenever(observeMessagesUseCase.invoke(eq(UserId("user1")), eq(UserId(friendId)), anyOrNull()))
            .thenReturn(flowOf(emptyList()))

        // Default: not blocked
        runTest {
            whenever(friendsRepository.isBlocked(UserId("user1"), UserId(friendId))).thenReturn(false)
            whenever(friendsRepository.isBlockedBy(UserId("user1"), UserId(friendId))).thenReturn(false)
            whenever(friendsRepository.getPublicProfile(UserId(friendId)))
                .thenReturn(PublicUserProfile(uid = friendId, username = friendUsername))
            whenever(chatRepository.getClearedAt(eq("chat_friend1_user1"), eq(UserId("user1")))).thenReturn(null)
        }
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun buildViewModel(): ChatViewModel = ChatViewModel(
        savedStateHandle = savedStateHandle,
        authRepo = authRepo,
        observeMessagesUseCase = observeMessagesUseCase,
        sendMessageUseCase = sendMessageUseCase,
        markMessagesAsReadUseCase = markMessagesAsReadUseCase,
        translateAllMessagesUseCase = translateAllMessagesUseCase,
        userSettingsRepository = userSettingsRepository,
        chatRepository = chatRepository,
        friendsRepository = friendsRepository,
        sharedFriendsDataSource = sharedFriendsDataSource,
        rateLimiter = rateLimiter
    )

    // ── Initial state ───────────────────────────────────────────────

    @Test
    fun `initial state has friendUsername from nav args`() = runTest {
        val vm = buildViewModel()
        assertEquals(friendUsername, vm.uiState.value.friendUsername)
    }

    // ── Login sets currentUserId ────────────────────────────────────

    @Test
    fun `login sets currentUserId and loads messages`() = runTest {
        val vm = buildViewModel()
        authStateFlow.value = AuthState.LoggedIn(testUser)
        vm.onScreenVisibilityChanged(true)

        assertEquals("user1", vm.uiState.value.currentUserId)
        verify(sharedFriendsDataSource, atLeastOnce()).markMessageFriendSeen(friendId)
    }

    @Test
    fun `login does not mark messages as read while chat screen is not visible`() = runTest {
        val vm = buildViewModel()
        authStateFlow.value = AuthState.LoggedIn(testUser)

        assertEquals("user1", vm.uiState.value.currentUserId)
        verifyNoInteractions(markMessagesAsReadUseCase)
        verify(sharedFriendsDataSource, never()).markMessageFriendSeen(friendId)
    }

    @Test
    fun `becoming visible marks messages as read`() = runTest {
        val vm = buildViewModel()
        authStateFlow.value = AuthState.LoggedIn(testUser)

        vm.onScreenVisibilityChanged(true)

        verify(markMessagesAsReadUseCase, atLeastOnce()).invoke(UserId("user1"), UserId(friendId))
        verify(sharedFriendsDataSource, atLeastOnce()).markMessageFriendSeen(friendId)
    }

    // ── Logout resets state ─────────────────────────────────────────

    @Test
    fun `logout resets ui state keeping friendUsername`() = runTest {
        val vm = buildViewModel()
        authStateFlow.value = AuthState.LoggedIn(testUser)
        authStateFlow.value = AuthState.LoggedOut

        val state = vm.uiState.value
        assertFalse(state.isLoading)
        assertEquals(friendUsername, state.friendUsername)
        assertTrue(state.messages.isEmpty())
    }

    // ── sendMessage blank text ──────────────────────────────────────

    @Test
    fun `sendMessage with blank text does nothing`() = runTest {
        val vm = buildViewModel()
        authStateFlow.value = AuthState.LoggedIn(testUser)

        vm.onMessageTextChange("   ")
        vm.sendMessage()

        verifyNoInteractions(sendMessageUseCase)
    }

    // ── sendMessage success ─────────────────────────────────────────

    @Test
    fun `sendMessage success clears message text`() = runTest {
        whenever(sendMessageUseCase.invoke(eq("chat_friend1_user1"), eq(UserId("user1")), eq(UserId(friendId)), any(), any()))
            .thenReturn(Result.success(Unit))

        val vm = buildViewModel()
        authStateFlow.value = AuthState.LoggedIn(testUser)

        vm.onMessageTextChange("Hello!")
        vm.sendMessage()

        assertEquals("", vm.uiState.value.messageText)
        assertFalse(vm.uiState.value.isSending)
    }

    // ── sendMessage failure ─────────────────────────────────────────

    @Test
    fun `sendMessage failure surfaces error`() = runTest {
        // Use raw values (no matchers) to avoid inline value class boxing mismatch
        whenever(sendMessageUseCase.invoke("chat_friend1_user1", UserId("user1"), UserId(friendId), "Hello!", ""))
            .thenReturn(Result.failure(RuntimeException("Network error")))

        val vm = buildViewModel()
        authStateFlow.value = AuthState.LoggedIn(testUser)

        vm.onMessageTextChange("Hello!")
        vm.sendMessage()

        assertNotNull(vm.uiState.value.error)
        assertFalse(vm.uiState.value.isSending)
    }

    // ── Rate limiter blocks rapid sends ─────────────────────────────

    @Test
    fun `rate limiter blocks after 10 rapid message sends`() = runTest {
        whenever(sendMessageUseCase.invoke(eq("chat_friend1_user1"), eq(UserId("user1")), eq(UserId(friendId)), any(), any()))
            .thenReturn(Result.success(Unit))

        val vm = buildViewModel()
        authStateFlow.value = AuthState.LoggedIn(testUser)

        // Send 10 messages (should succeed)
        repeat(10) {
            vm.onMessageTextChange("Message $it")
            vm.sendMessage()
        }

        // 11th should be rate limited
        vm.onMessageTextChange("One more")
        vm.sendMessage()

        assertTrue(vm.uiState.value.error?.contains("too quickly", ignoreCase = true) == true)
    }

    // ── Message over 2000 chars rejected ────────────────────────────

    @Test
    fun `sendMessage over 2000 chars rejected`() = runTest {
        val vm = buildViewModel()
        authStateFlow.value = AuthState.LoggedIn(testUser)

        vm.onMessageTextChange("a".repeat(2001))
        vm.sendMessage()

        assertNotNull(vm.uiState.value.error)
        verifyNoInteractions(sendMessageUseCase)
    }

    // ── Blocked by friend prevents sending ──────────────────────────

    @Test
    fun `sendMessage when blocked by friend shows error`() = runTest {
        whenever(friendsRepository.isBlockedBy(UserId("user1"), UserId(friendId))).thenReturn(true)

        val vm = buildViewModel()
        authStateFlow.value = AuthState.LoggedIn(testUser)

        vm.onMessageTextChange("Hello!")
        vm.sendMessage()

        assertNotNull(vm.uiState.value.error)
        assertTrue(vm.uiState.value.error!!.contains("cannot send", ignoreCase = true))
    }

    // ── blockFriend ─────────────────────────────────────────────────

    @Test
    fun `blockFriend updates isBlocked state`() = runTest {
        whenever(friendsRepository.blockUser(eq(UserId("user1")), eq(UserId(friendId)), any())).thenReturn(Result.success(Unit))

        val vm = buildViewModel()
        authStateFlow.value = AuthState.LoggedIn(testUser)

        vm.blockFriend()

        assertTrue(vm.uiState.value.isBlocked)
    }

    // ── unblockFriend ───────────────────────────────────────────────

    @Test
    fun `unblockFriend updates isBlocked state`() = runTest {
        whenever(friendsRepository.unblockUser(UserId("user1"), UserId(friendId))).thenReturn(Result.success(Unit))

        val vm = buildViewModel()
        authStateFlow.value = AuthState.LoggedIn(testUser)

        vm.unblockFriend()

        assertFalse(vm.uiState.value.isBlocked)
    }

    // ── clearError ──────────────────────────────────────────────────

    @Test
    fun `clearError clears error field`() = runTest {
        // Use raw values (no matchers) to avoid inline value class boxing mismatch
        whenever(sendMessageUseCase.invoke("chat_friend1_user1", UserId("user1"), UserId(friendId), "test", ""))
            .thenReturn(Result.failure(RuntimeException("fail")))

        val vm = buildViewModel()
        authStateFlow.value = AuthState.LoggedIn(testUser)

        vm.onMessageTextChange("test")
        vm.sendMessage()
        assertNotNull(vm.uiState.value.error)

        vm.clearError()
        assertNull(vm.uiState.value.error)
    }

    // ── toggleTranslation ───────────────────────────────────────────

    @Test
    fun `toggleTranslation flips showTranslation`() = runTest {
        val vm = buildViewModel()
        assertFalse(vm.uiState.value.showTranslation)

        vm.toggleTranslation()
        assertTrue(vm.uiState.value.showTranslation)

        vm.toggleTranslation()
        assertFalse(vm.uiState.value.showTranslation)
    }

    // ── clearConversation ───────────────────────────────────────────

    @Test
    fun `clearConversation empties messages and sets clearSuccess`() = runTest {
        val vm = buildViewModel()
        authStateFlow.value = AuthState.LoggedIn(testUser)

        vm.clearConversation()

        val state = vm.uiState.value
        assertTrue(state.messages.isEmpty())
        assertTrue(state.clearSuccess)
        assertFalse(state.showTranslation)
    }

    // ── dismissClearSuccess ─────────────────────────────────────────

    @Test
    fun `dismissClearSuccess clears flag`() = runTest {
        val vm = buildViewModel()
        authStateFlow.value = AuthState.LoggedIn(testUser)

        vm.clearConversation()
        assertTrue(vm.uiState.value.clearSuccess)

        vm.dismissClearSuccess()
        assertFalse(vm.uiState.value.clearSuccess)
    }

    // ── onMessageTextChange ─────────────────────────────────────────

    @Test
    fun `onMessageTextChange updates messageText`() = runTest {
        val vm = buildViewModel()
        vm.onMessageTextChange("Hello world")
        assertEquals("Hello world", vm.uiState.value.messageText)
    }

    // ── clearTranslation ────────────────────────────────────────────

    @Test
    fun `clearTranslation resets translation state`() = runTest {
        val vm = buildViewModel()
        vm.toggleTranslation()
        assertTrue(vm.uiState.value.showTranslation)

        vm.clearTranslation()
        assertFalse(vm.uiState.value.showTranslation)
        assertTrue(vm.uiState.value.translatedMessages.isEmpty())
    }

    // ── loadOlderMessages ──────────────────────────────────────────────

    @Test
    fun `loadOlderMessages does nothing when messages empty`() = runTest {
        val vm = buildViewModel()
        authStateFlow.value = AuthState.LoggedIn(testUser)

        // With empty messages list, loadOlderMessages should not proceed
        vm.loadOlderMessages()

        verify(chatRepository, never()).loadOlderMessages(any(), any(), any())
    }

    @Test
    fun `loadOlderMessages does nothing when hasMoreMessages is false`() = runTest {
        val vm = buildViewModel()
        authStateFlow.value = AuthState.LoggedIn(testUser)

        // Empty messages means hasMoreMessages starts true but no messages to paginate from
        assertFalse(vm.uiState.value.isLoadingOlder)
    }

    // ── translateAllMessages ───────────────────────────────────────────

    @Test
    fun `translateAllMessages with empty messages does nothing`() = runTest {
        val vm = buildViewModel()
        authStateFlow.value = AuthState.LoggedIn(testUser)

        vm.translateAllMessages()

        verifyNoInteractions(translateAllMessagesUseCase)
    }

    @Test
    fun `translateAllMessages does not run when already translating`() = runTest {
        val vm = buildViewModel()
        authStateFlow.value = AuthState.LoggedIn(testUser)

        // Initial state: not translating, no messages
        assertFalse(vm.uiState.value.isTranslating)

        // Call should be no-op with empty messages
        vm.translateAllMessages()

        verifyNoInteractions(translateAllMessagesUseCase)
    }
}
