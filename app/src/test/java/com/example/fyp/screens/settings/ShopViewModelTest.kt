package com.example.fyp.screens.settings

import com.example.fyp.data.cloud.CloudQuizClient
import com.example.fyp.data.cloud.SpendCoinsResult
import com.example.fyp.data.user.FirebaseAuthRepository
import com.example.fyp.domain.learning.QuizRepository
import com.example.fyp.data.settings.UserSettingsRepository
import com.example.fyp.data.settings.SharedSettingsDataSource
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
import org.mockito.kotlin.*

/**
 * Unit tests for ShopViewModel.
 *
 * Coin spending is delegated to the server-side spendCoins Cloud Function
 * via CloudQuizClient. Initial coin balance is loaded from QuizRepository.
 *
 * Tests:
 * 1. Initial loading state
 * 2. Login loads coin balance and settings
 * 3. Logout resets state
 * 4. Expand history limit - server success
 * 5. Expand history limit - insufficient coins (client guard)
 * 6. Expand history limit - max limit reached (client guard)
 * 7. Expand history limit - server returns insufficient_coins error
 * 8. Unlock palette - server success
 * 9. Unlock palette - insufficient coins (client guard)
 * 10. Select palette updates state
 * 11. Clear error messages
 */
@OptIn(ExperimentalCoroutinesApi::class)
class ShopViewModelTest {

    private lateinit var authRepo: FirebaseAuthRepository
    private lateinit var settingsRepo: UserSettingsRepository
    private lateinit var quizRepo: QuizRepository
    private lateinit var cloudClient: CloudQuizClient
    private lateinit var sharedSettings: SharedSettingsDataSource
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
                historyViewLimit = 30,
                colorPaletteId = "default",
                unlockedPalettes = listOf("default")
            )
        }
        quizRepo = mock {
            onBlocking { fetchUserCoinStats(UserId(testUserId)) } doReturn UserCoinStats(coinTotal = 2000)
        }
        cloudClient = mock()
        sharedSettings = mock {
            on { settings } doReturn MutableStateFlow(UserSettings())
        }

        viewModel = ShopViewModel(
            authRepo = authRepo,
            settingsRepo = settingsRepo,
            quizRepo = quizRepo,
            cloudClient = cloudClient,
            sharedSettings = sharedSettings
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
        assertEquals(30, state.currentHistoryLimit)
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

        val expectedLimit = 30 + UserSettings.HISTORY_EXPANSION_INCREMENT
        cloudClient.stub {
            onBlocking { spendCoinsForHistoryExpansion() } doReturn SpendCoinsResult(
                success = true, newBalance = 1000, newLimit = expectedLimit
            )
        }

        viewModel.expandHistoryLimit()

        val state = viewModel.uiState.value
        assertFalse(state.isPurchasing)
        assertEquals(1000, state.coinBalance)
        assertEquals(expectedLimit, state.currentHistoryLimit)
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
        verifyNoInteractions(cloudClient)
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

        assertFalse(viewModel.uiState.value.isPurchasing)
        verifyNoInteractions(cloudClient)
    }

    @Test
    fun `expand returns insufficient when server reports insufficient_coins`() = runTest {
        authStateFlow.value = AuthState.LoggedIn(User(uid = testUserId, email = "test@test.com"))

        cloudClient.stub {
            onBlocking { spendCoinsForHistoryExpansion() } doReturn SpendCoinsResult(
                success = false, reason = "insufficient_coins"
            )
        }

        viewModel.expandHistoryLimit()

        assertEquals("Insufficient coins", viewModel.uiState.value.purchaseError)
    }

    @Test
    fun `unlock palette succeeds`() = runTest {
        authStateFlow.value = AuthState.LoggedIn(User(uid = testUserId, email = "test@test.com"))

        cloudClient.stub {
            onBlocking { spendCoinsForPaletteUnlock("ocean") } doReturn SpendCoinsResult(
                success = true, newBalance = 1990
            )
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
        verifyNoInteractions(cloudClient)
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
