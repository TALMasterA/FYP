package com.example.fyp.screens.settings

import com.example.fyp.data.user.FirebaseAuthRepository
import com.example.fyp.domain.learning.QuizRepository
import com.example.fyp.data.settings.UserSettingsRepository
import com.example.fyp.model.UserId
import com.example.fyp.model.UserCoinStats
import com.example.fyp.model.user.AuthState
import com.example.fyp.model.user.User
import com.example.fyp.model.user.UserSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.stub
import org.mockito.kotlin.verify

/**
 * Unit tests for ShopViewModel.
 *
 * Tests:
 * 1. Initial loading state
 * 2. Login loads coin balance and settings
 * 3. Logout resets state
 * 4. Expand history limit succeeds
 * 5. Expand history limit fails with insufficient coins
 * 6. Expand history limit fails at max limit
 * 7. Unlock palette succeeds
 * 8. Unlock palette fails with insufficient coins
 * 9. Select palette updates state
 * 10. Clear error messages
 */
@OptIn(ExperimentalCoroutinesApi::class)
class ShopViewModelTest {

    private lateinit var authRepo: FirebaseAuthRepository
    private lateinit var settingsRepo: UserSettingsRepository
    private lateinit var quizRepo: QuizRepository
    private lateinit var viewModel: ShopViewModel

    private val testDispatcher = UnconfinedTestDispatcher()
    private val authStateFlow = MutableStateFlow<AuthState>(AuthState.Loading)
    private val testUserId = "testUser123"

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        authRepo = mock {
            on { currentUserState } doReturn authStateFlow
        }
        settingsRepo = mock {
            onBlocking { fetchUserSettings(UserId(testUserId)) } doReturn UserSettings(
                historyViewLimit = 50,
                colorPaletteId = "default",
                unlockedPalettes = listOf("default")
            )
        }
        quizRepo = mock {
            onBlocking { fetchUserCoinStats(UserId(testUserId)) } doReturn UserCoinStats(coinTotal = 2000)
        }

        viewModel = ShopViewModel(
            authRepo = authRepo,
            settingsRepo = settingsRepo,
            quizRepo = quizRepo
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is loading`() {
        assertTrue(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `login loads coin balance and settings`() = runTest {
        authStateFlow.value = AuthState.LoggedIn(User(uid = testUserId, email = "test@test.com"))

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals(2000, state.coinBalance)
        assertEquals(50, state.currentHistoryLimit)
        assertEquals("default", state.currentPaletteId)
        assertEquals(listOf("default"), state.unlockedPalettes)
    }

    @Test
    fun `logout resets state`() = runTest {
        authStateFlow.value = AuthState.LoggedIn(User(uid = testUserId, email = "test@test.com"))
        authStateFlow.value = AuthState.LoggedOut

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals(0, state.coinBalance)
    }

    @Test
    fun `expand history limit succeeds`() = runTest {
        authStateFlow.value = AuthState.LoggedIn(User(uid = testUserId, email = "test@test.com"))

        quizRepo.stub {
            onBlocking { deductCoins(UserId(testUserId), UserSettings.HISTORY_EXPANSION_COST) } doReturn 1000
        }

        viewModel.expandHistoryLimit()

        val state = viewModel.uiState.value
        assertFalse(state.isPurchasing)
        assertEquals(1000, state.coinBalance)
        assertEquals(50 + UserSettings.HISTORY_EXPANSION_INCREMENT, state.currentHistoryLimit)
        assertNull(state.purchaseError)
    }

    @Test
    fun `expand history limit fails with insufficient coins`() = runTest {
        quizRepo.stub {
            onBlocking { fetchUserCoinStats(UserId(testUserId)) } doReturn UserCoinStats(coinTotal = 5)
        }
        authStateFlow.value = AuthState.LoggedIn(User(uid = testUserId, email = "test@test.com"))

        viewModel.expandHistoryLimit()

        assertEquals("Insufficient coins", viewModel.uiState.value.purchaseError)
    }

    @Test
    fun `expand history limit fails at max limit`() = runTest {
        settingsRepo.stub {
            onBlocking { fetchUserSettings(UserId(testUserId)) } doReturn UserSettings(
                historyViewLimit = UserSettings.MAX_HISTORY_LIMIT
            )
        }
        authStateFlow.value = AuthState.LoggedIn(User(uid = testUserId, email = "test@test.com"))

        viewModel.expandHistoryLimit()

        // Should not start purchase when already at max
        assertFalse(viewModel.uiState.value.isPurchasing)
    }

    @Test
    fun `expand returns insufficient when server reports negative balance`() = runTest {
        authStateFlow.value = AuthState.LoggedIn(User(uid = testUserId, email = "test@test.com"))

        quizRepo.stub {
            onBlocking { deductCoins(UserId(testUserId), UserSettings.HISTORY_EXPANSION_COST) } doReturn -1
        }

        viewModel.expandHistoryLimit()

        assertEquals("Insufficient coins", viewModel.uiState.value.purchaseError)
    }

    @Test
    fun `unlock palette succeeds`() = runTest {
        authStateFlow.value = AuthState.LoggedIn(User(uid = testUserId, email = "test@test.com"))

        quizRepo.stub {
            onBlocking { deductCoins(UserId(testUserId), 10) } doReturn 1990
        }

        viewModel.unlockPalette("ocean", 10)

        val state = viewModel.uiState.value
        assertFalse(state.isPurchasing)
        assertEquals(1990, state.coinBalance)
        assertTrue(state.unlockedPalettes.contains("ocean"))
        assertNull(state.unlockError)
    }

    @Test
    fun `unlock palette fails with insufficient coins`() = runTest {
        quizRepo.stub {
            onBlocking { fetchUserCoinStats(UserId(testUserId)) } doReturn UserCoinStats(coinTotal = 5)
        }
        authStateFlow.value = AuthState.LoggedIn(User(uid = testUserId, email = "test@test.com"))

        viewModel.unlockPalette("premium", 10)

        assertEquals("Insufficient coins", viewModel.uiState.value.unlockError)
    }

    @Test
    fun `select palette updates state`() = runTest {
        authStateFlow.value = AuthState.LoggedIn(User(uid = testUserId, email = "test@test.com"))

        viewModel.selectPalette("ocean")

        assertEquals("ocean", viewModel.uiState.value.currentPaletteId)
    }

    @Test
    fun `clear purchase error`() {
        viewModel.clearPurchaseError()
        assertNull(viewModel.uiState.value.purchaseError)
    }

    @Test
    fun `clear unlock error`() {
        viewModel.clearUnlockError()
        assertNull(viewModel.uiState.value.unlockError)
    }

    @Test
    fun `clear purchase success`() {
        viewModel.clearPurchaseSuccess()
        assertNull(viewModel.uiState.value.purchaseSuccess)
    }
}
