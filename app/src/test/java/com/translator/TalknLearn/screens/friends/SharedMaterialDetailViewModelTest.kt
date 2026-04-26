package com.translator.TalknLearn.screens.friends

import androidx.lifecycle.SavedStateHandle
import com.translator.TalknLearn.data.friends.SharedFriendsDataSource
import com.translator.TalknLearn.data.friends.SharingRepository
import com.translator.TalknLearn.data.user.FirebaseAuthRepository
import com.translator.TalknLearn.model.UserId
import com.translator.TalknLearn.model.friends.SharedItem
import com.translator.TalknLearn.model.user.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
 * Unit tests for SharedMaterialDetailViewModel.
 *
 * Tests:
 *  1. Blank itemId sets error
 *  2. Not logged in sets error
 *  3. Item not found sets error
 *  4. Item found sets item in state
 *  5. Full content loaded successfully
 *  6. Full content null falls back to empty string
 *  7. Blank fromUsername resolved from cache
 *  8. Non-blank fromUsername kept as-is
 *  9. Exception during loadItem sets error
 */
@OptIn(ExperimentalCoroutinesApi::class)
class SharedMaterialDetailViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val testUserId = "user123"
    private val testUser = User(uid = testUserId, email = "test@test.com")

    private lateinit var authRepo: FirebaseAuthRepository
    private lateinit var sharingRepo: SharingRepository
    private lateinit var sharedFriendsDataSource: SharedFriendsDataSource

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        authRepo = mock {
            on { currentUser } doReturn testUser
        }
        sharingRepo = mock()
        sharedFriendsDataSource = mock()
    }

    private fun buildViewModel(
        itemId: String = "item1",
        authRepository: FirebaseAuthRepository = authRepo
    ): SharedMaterialDetailViewModel {
        val savedState = SavedStateHandle(mapOf("itemId" to itemId))
        return SharedMaterialDetailViewModel(
            savedStateHandle = savedState,
            authRepository = authRepository,
            sharingRepository = sharingRepo,
            sharedFriendsDataSource = sharedFriendsDataSource
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // -- Init checks --

    @Test
    fun `blank itemId sets error without loading`() = runTest {
        val vm = buildViewModel(itemId = "")

        assertEquals("No item ID", vm.uiState.value.error)
        assertFalse(vm.uiState.value.isLoading)
        verifyNoInteractions(sharingRepo)
    }

    @Test
    fun `not logged in sets error`() = runTest {
        val nullAuthRepo: FirebaseAuthRepository = mock {
            on { currentUser } doReturn null
        }
        val vm = buildViewModel(authRepository = nullAuthRepo)

        assertEquals("Not logged in", vm.uiState.value.error)
        assertFalse(vm.uiState.value.isLoading)
    }

    // -- loadItem --

    @Test
    fun `item not found sets error`() = runTest {
        sharingRepo.stub {
            onBlocking { fetchSharedItemById(UserId(testUserId), "item1") } doReturn null
        }

        val vm = buildViewModel()

        assertEquals("Item not found", vm.uiState.value.error)
    }

    @Test
    fun `item found sets item in state`() = runTest {
        val item = SharedItem(itemId = "item1", fromUserId = "sender1", fromUsername = "senderName")
        sharingRepo.stub {
            onBlocking { fetchSharedItemById(UserId(testUserId), "item1") } doReturn item
            onBlocking { fetchSharedItemFullContent(UserId(testUserId), "item1") } doReturn "full text"
        }

        val vm = buildViewModel()

        assertNotNull(vm.uiState.value.item)
        assertEquals("item1", vm.uiState.value.item?.itemId)
        assertEquals("senderName", vm.uiState.value.item?.fromUsername)
        assertFalse(vm.uiState.value.isLoading)
    }

    @Test
    fun `full content loaded successfully`() = runTest {
        val item = SharedItem(itemId = "item1", fromUserId = "sender1", fromUsername = "senderName")
        sharingRepo.stub {
            onBlocking { fetchSharedItemById(UserId(testUserId), "item1") } doReturn item
            onBlocking { fetchSharedItemFullContent(UserId(testUserId), "item1") } doReturn "Long full content here"
        }

        val vm = buildViewModel()

        assertEquals("Long full content here", vm.uiState.value.fullContent)
    }

    @Test
    fun `full content null falls back to empty string`() = runTest {
        val item = SharedItem(itemId = "item1", fromUserId = "sender1", fromUsername = "senderName")
        sharingRepo.stub {
            onBlocking { fetchSharedItemById(UserId(testUserId), "item1") } doReturn item
            onBlocking { fetchSharedItemFullContent(UserId(testUserId), "item1") } doReturn null
        }

        val vm = buildViewModel()

        assertEquals("", vm.uiState.value.fullContent)
    }

    @Test
    fun `blank fromUsername resolved from cache`() = runTest {
        val item = SharedItem(itemId = "item1", fromUserId = "sender1", fromUsername = "")
        sharingRepo.stub {
            onBlocking { fetchSharedItemById(UserId(testUserId), "item1") } doReturn item
            onBlocking { fetchSharedItemFullContent(UserId(testUserId), "item1") } doReturn "content"
        }
        sharedFriendsDataSource.stub {
            on { getCachedUsername("sender1") } doReturn "CachedSender"
        }

        val vm = buildViewModel()

        assertEquals("CachedSender", vm.uiState.value.item?.fromUsername)
    }

    @Test
    fun `non-blank fromUsername kept as-is`() = runTest {
        val item = SharedItem(itemId = "item1", fromUserId = "sender1", fromUsername = "OriginalName")
        sharingRepo.stub {
            onBlocking { fetchSharedItemById(UserId(testUserId), "item1") } doReturn item
            onBlocking { fetchSharedItemFullContent(UserId(testUserId), "item1") } doReturn "content"
        }

        val vm = buildViewModel()

        assertEquals("OriginalName", vm.uiState.value.item?.fromUsername)
        verify(sharedFriendsDataSource, never()).getCachedUsername(any())
    }

    @Test
    fun `exception during loadItem sets error`() = runTest {
        sharingRepo.stub {
            onBlocking { fetchSharedItemById(UserId(testUserId), "item1") } doThrow RuntimeException("Network fail")
        }

        val vm = buildViewModel()

        assertEquals("Failed to load shared material. Please try again.", vm.uiState.value.error)
        assertFalse(vm.uiState.value.isLoading)
    }
}
