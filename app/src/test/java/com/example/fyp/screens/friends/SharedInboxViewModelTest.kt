package com.example.fyp.screens.friends

import com.example.fyp.data.friends.SharedFriendsDataSource
import com.example.fyp.data.friends.SharingRepository
import com.example.fyp.data.user.FirebaseAuthRepository
import com.example.fyp.domain.friends.AcceptSharedItemUseCase
import com.example.fyp.domain.friends.DismissSharedItemUseCase
import com.example.fyp.model.UserId
import com.example.fyp.model.friends.SharedItem
import com.example.fyp.model.friends.SharedItemStatus
import com.example.fyp.model.friends.SharedItemType
import com.example.fyp.model.user.AuthState
import com.example.fyp.model.user.User
import com.google.firebase.Timestamp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.*

@OptIn(ExperimentalCoroutinesApi::class)
class SharedInboxViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val testScheduler = testDispatcher.scheduler

    private lateinit var authRepository: FirebaseAuthRepository
    private lateinit var sharedFriendsDataSource: SharedFriendsDataSource
    private lateinit var acceptSharedItemUseCase: AcceptSharedItemUseCase
    private lateinit var dismissSharedItemUseCase: DismissSharedItemUseCase
    private lateinit var sharingRepository: SharingRepository

    private val authStateFlow = MutableStateFlow<AuthState>(AuthState.Loading)
    private val pendingItemsFlow = MutableStateFlow<List<SharedItem>>(emptyList())

    private val loggedInState = AuthState.LoggedIn(User(uid = "user1"))
    private val testUserId = UserId("user1")

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        authRepository = mock()
        whenever(authRepository.currentUserState).thenReturn(authStateFlow)
        whenever(authRepository.currentUser).thenReturn(null)

        sharedFriendsDataSource = mock()
        whenever(sharedFriendsDataSource.pendingSharedItems).thenReturn(pendingItemsFlow)

        acceptSharedItemUseCase = mock()
        dismissSharedItemUseCase = mock()
        sharingRepository = mock()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel() = SharedInboxViewModel(
        authRepository = authRepository,
        sharedFriendsDataSource = sharedFriendsDataSource,
        sharingRepository = sharingRepository,
        acceptSharedItemUseCase = acceptSharedItemUseCase,
        dismissSharedItemUseCase = dismissSharedItemUseCase
    )

    private fun makeItem(id: String, type: SharedItemType = SharedItemType.WORD) = SharedItem(
        itemId = id,
        fromUserId = "sender",
        fromUsername = "sender_user",
        toUserId = "user1",
        type = type,
        content = mapOf(
            "sourceText" to "hello",
            "targetText" to "hola",
            "sourceLang" to "en-US",
            "targetLang" to "es-ES"
        ),
        status = SharedItemStatus.PENDING,
        createdAt = Timestamp.now()
    )

    // ── Initial load: all pending items are marked as new ────────────────────

    @Test
    fun `initial load marks all pending items as new`() = runTest {
        val viewModel = createViewModel()

        authStateFlow.value = loggedInState
        pendingItemsFlow.value = listOf(makeItem("item1"), makeItem("item2"))

        val state = viewModel.uiState.value
        assertTrue("item1 should be marked new", "item1" in state.newItemIds)
        assertTrue("item2 should be marked new", "item2" in state.newItemIds)
        assertEquals(2, state.newItemCount)
    }

    @Test
    fun `initial load with no items has empty newItemIds`() = runTest {
        val viewModel = createViewModel()

        authStateFlow.value = loggedInState
        pendingItemsFlow.value = emptyList()

        val state = viewModel.uiState.value
        assertTrue("No items should be marked new when inbox is empty", state.newItemIds.isEmpty())
        assertEquals(0, state.newItemCount)
    }

    // ── New items after markItemsAsSeen should appear in newItemIds ───────────

    @Test
    fun `new item arriving after markItemsAsSeen appears in newItemIds`() = runTest {
        val viewModel = createViewModel()

        authStateFlow.value = loggedInState
        pendingItemsFlow.value = listOf(makeItem("item1"))

        // User views the inbox
        viewModel.markItemsAsSeen()

        // New item arrives
        pendingItemsFlow.value = listOf(makeItem("item1"), makeItem("item2"))

        // Give time for flow to update
        testScheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue("item2 should be in newItemIds", "item2" in state.newItemIds)
        assertFalse("item1 should not be in newItemIds", "item1" in state.newItemIds)
    }

    // ── markItemsAsSeen clears newItemIds ─────────────────────────────────────

    @Test
    fun `markItemsAsSeen clears newItemIds and newItemCount`() = runTest {
        val viewModel = createViewModel()

        authStateFlow.value = loggedInState
        pendingItemsFlow.value = listOf(makeItem("item1"))
        viewModel.markItemsAsSeen()

        // New item arrives → appears in newItemIds
        pendingItemsFlow.value = listOf(makeItem("item1"), makeItem("item2"))
        assertTrue("item2 should appear as new", "item2" in viewModel.uiState.value.newItemIds)

        // User opens inbox → all cleared
        viewModel.markItemsAsSeen()
        assertTrue("newItemIds should be empty after markItemsAsSeen", viewModel.uiState.value.newItemIds.isEmpty())
        assertEquals(0, viewModel.uiState.value.newItemCount)
    }

    // ── Accept word item ──────────────────────────────────────────────────────

    @Test
    fun `acceptItem calls acceptSharedItemUseCase`() = runTest {
        whenever(acceptSharedItemUseCase.invoke("item1", testUserId))
            .thenReturn(Result.success(Unit))

        val viewModel = createViewModel()

        authStateFlow.value = loggedInState
        testScheduler.advanceUntilIdle()

        viewModel.acceptItem("item1")
        testScheduler.advanceUntilIdle()

        verify(acceptSharedItemUseCase).invoke("item1", testUserId)
    }

    @Test
    fun `acceptItem ignores duplicate calls while processing`() = runTest {
        whenever(acceptSharedItemUseCase.invoke("item1", testUserId))
            .thenReturn(Result.success(Unit))

        val viewModel = createViewModel()

        authStateFlow.value = loggedInState
        testScheduler.runCurrent()

        // Call twice back-to-back before advancing virtual time.
        // Guard behavior is tied to processingJob.isActive (not the isProcessing UI flag),
        // so the second call should be ignored while the first job is still active.
        viewModel.acceptItem("item1")
        viewModel.acceptItem("item1")
        testScheduler.runCurrent()

        verify(acceptSharedItemUseCase, times(1)).invoke("item1", testUserId)
    }

    // ── Delete (learning materials) uses dismiss ──────────────────────────────

    @Test
    fun `deleteItem calls dismissSharedItemUseCase`() = runTest {
        whenever(dismissSharedItemUseCase.invoke("item2", testUserId))
            .thenReturn(Result.success(Unit))

        val viewModel = createViewModel()

        authStateFlow.value = loggedInState
        testScheduler.advanceUntilIdle()

        viewModel.deleteItem("item2")
        testScheduler.advanceUntilIdle()

        verify(dismissSharedItemUseCase).invoke("item2", testUserId)
    }

    @Test
    fun `deleteItem sets successMessage to Item deleted`() = runTest {
        whenever(dismissSharedItemUseCase.invoke("item2", testUserId))
            .thenReturn(Result.success(Unit))

        val viewModel = createViewModel()

        authStateFlow.value = loggedInState
        testScheduler.advanceUntilIdle()

        viewModel.deleteItem("item2")
        // Advance only enough to complete the deleteItem coroutine but not the 3-second delay
        testScheduler.runCurrent()

        assertEquals("Item deleted", viewModel.uiState.value.successMessage)
    }

    // ── Logout resets state ───────────────────────────────────────────────────

    @Test
    fun `logout resets newItemIds and seenState`() = runTest {
        val viewModel = createViewModel()

        authStateFlow.value = loggedInState
        pendingItemsFlow.value = listOf(makeItem("item1"))
        viewModel.markItemsAsSeen()
        pendingItemsFlow.value = listOf(makeItem("item1"), makeItem("item2"))

        // Logout
        authStateFlow.value = AuthState.LoggedOut

        val state = viewModel.uiState.value
        assertTrue("newItemIds should be empty after logout", state.newItemIds.isEmpty())
        assertEquals(0, state.newItemCount)
    }

    // ── acceptItem sets loading state ─────────────────────────────────────────

    @Test
    fun `acceptItem sets loading state for the item`() = runTest {
        whenever(acceptSharedItemUseCase.invoke("item1", testUserId))
            .thenReturn(Result.success(Unit))

        val viewModel = createViewModel()

        authStateFlow.value = loggedInState
        pendingItemsFlow.value = listOf(makeItem("item1"))
        testScheduler.runCurrent()

        viewModel.acceptItem("item1")
        testScheduler.runCurrent()

        // After accepting, should show success message
        assertNotNull(viewModel.uiState.value.successMessage)
    }

    // ── acceptItem failure sets error ─────────────────────────────────────────

    @Test
    fun `acceptItem failure sets error message`() = runTest {
        whenever(acceptSharedItemUseCase.invoke("item1", testUserId))
            .thenReturn(Result.failure(RuntimeException("Accept failed")))

        val viewModel = createViewModel()

        authStateFlow.value = loggedInState
        pendingItemsFlow.value = listOf(makeItem("item1"))
        testScheduler.runCurrent()

        viewModel.acceptItem("item1")
        testScheduler.runCurrent()

        assertNotNull(viewModel.uiState.value.error)
    }

    // ── deleteItem failure sets error ─────────────────────────────────────────

    @Test
    fun `deleteItem failure sets error message`() = runTest {
        whenever(dismissSharedItemUseCase.invoke("item1", testUserId))
            .thenReturn(Result.failure(RuntimeException("Delete failed")))

        val viewModel = createViewModel()

        authStateFlow.value = loggedInState
        testScheduler.runCurrent()

        viewModel.deleteItem("item1")
        testScheduler.runCurrent()

        assertNotNull(viewModel.uiState.value.error)
    }
}
