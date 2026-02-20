package com.example.fyp.screens.friends

import com.example.fyp.data.friends.SharedFriendsDataSource
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

/**
 * Unit tests for SharedInboxViewModel.
 * Tests unread badge logic, item acceptance, dismissal, and delete behavior.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class SharedInboxViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var authRepository: FirebaseAuthRepository
    private lateinit var sharedFriendsDataSource: SharedFriendsDataSource
    private lateinit var acceptSharedItemUseCase: AcceptSharedItemUseCase
    private lateinit var dismissSharedItemUseCase: DismissSharedItemUseCase

    private val authStateFlow = MutableStateFlow<AuthState>(AuthState.Loading)
    private val pendingItemsFlow = MutableStateFlow<List<SharedItem>>(emptyList())

    private val loggedInState = AuthState.LoggedIn(User(uid = "user1"))

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        authRepository = mock {
            on { currentUserState } doReturn authStateFlow
        }
        sharedFriendsDataSource = mock {
            on { pendingSharedItems } doReturn pendingItemsFlow
        }
        acceptSharedItemUseCase = mock()
        dismissSharedItemUseCase = mock()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel() = SharedInboxViewModel(
        authRepository = authRepository,
        sharedFriendsDataSource = sharedFriendsDataSource,
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

    // ── Initial load: no new item IDs ─────────────────────────────────────────

    @Test
    fun `initial load does not mark items as new`() = runTest {
        val viewModel = createViewModel()

        authStateFlow.value = loggedInState
        pendingItemsFlow.value = listOf(makeItem("item1"), makeItem("item2"))

        val state = viewModel.uiState.value
        assertTrue("No items should be marked new on initial load", state.newItemIds.isEmpty())
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
        acceptSharedItemUseCase.stub {
            onBlocking { invoke("item1", UserId("user1")) } doReturn Result.success(Unit)
        }
        val viewModel = createViewModel()

        authStateFlow.value = loggedInState
        viewModel.acceptItem("item1")

        verify(acceptSharedItemUseCase).invoke("item1", UserId("user1"))
    }

    // ── Delete (learning materials) uses dismiss ──────────────────────────────

    @Test
    fun `deleteItem calls dismissSharedItemUseCase`() = runTest {
        dismissSharedItemUseCase.stub {
            onBlocking { invoke("item2", UserId("user1")) } doReturn Result.success(Unit)
        }
        val viewModel = createViewModel()

        authStateFlow.value = loggedInState
        viewModel.deleteItem("item2")

        verify(dismissSharedItemUseCase).invoke("item2", UserId("user1"))
    }

    @Test
    fun `deleteItem sets successMessage to Item deleted`() = runTest {
        dismissSharedItemUseCase.stub {
            onBlocking { invoke(any(), any()) } doReturn Result.success(Unit)
        }
        val viewModel = createViewModel()

        authStateFlow.value = loggedInState
        viewModel.deleteItem("item2")

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
}
