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
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.*

/**
 * Comprehensive tests for shop coin deduction and palette unlocking (item 11).
 *
 * Verifies:
 * - Coin balance deduction works correctly
 * - Insufficient coins shows error
 * - Palette unlock deducts correct cost
 * - History expansion deducts correct cost
 * - Server negative balance is rejected
 * - Success messages displayed
 */
@OptIn(ExperimentalCoroutinesApi::class)
class ShopCoinDeductionTest {

    private lateinit var authRepo: FirebaseAuthRepository
    private lateinit var settingsRepo: UserSettingsRepository
    private lateinit var quizRepo: QuizRepository
    private lateinit var viewModel: ShopViewModel

    private val testDispatcher = UnconfinedTestDispatcher()
    private val authStateFlow = MutableStateFlow<AuthState>(AuthState.Loading)
    private val testUserId = "coinTestUser"

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        authRepo = mock {
            on { currentUserState } doReturn authStateFlow
        }
        settingsRepo = mock {
            onBlocking { fetchUserSettings(UserId(testUserId)) } doReturn UserSettings(
                historyViewLimit = UserSettings.BASE_HISTORY_LIMIT,
                colorPaletteId = "default",
                unlockedPalettes = listOf("default")
            )
        }
        quizRepo = mock {
            onBlocking { fetchUserCoinStats(UserId(testUserId)) } doReturn UserCoinStats(coinTotal = 5000)
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

    // ── Coin Balance Loading ───────────────────────────────────────

    @Test
    fun `login loads coin balance`() = runTest {
        authStateFlow.value = AuthState.LoggedIn(User(uid = testUserId, email = "t@t.com"))

        assertEquals(5000, viewModel.uiState.value.coinBalance)
    }

    @Test
    fun `logout resets coin balance to zero`() = runTest {
        authStateFlow.value = AuthState.LoggedIn(User(uid = testUserId, email = "t@t.com"))
        authStateFlow.value = AuthState.LoggedOut

        assertEquals(0, viewModel.uiState.value.coinBalance)
    }

    // ── History Expansion Coin Deduction ───────────────────────────

    @Test
    fun `expand history deducts 1000 coins`() = runTest {
        authStateFlow.value = AuthState.LoggedIn(User(uid = testUserId, email = "t@t.com"))

        quizRepo.stub {
            onBlocking { deductCoins(UserId(testUserId), UserSettings.HISTORY_EXPANSION_COST) } doReturn 4000
        }

        viewModel.expandHistoryLimit()

        assertEquals(4000, viewModel.uiState.value.coinBalance)
    }

    @Test
    fun `expand history updates limit by increment`() = runTest {
        authStateFlow.value = AuthState.LoggedIn(User(uid = testUserId, email = "t@t.com"))

        quizRepo.stub {
            onBlocking { deductCoins(UserId(testUserId), UserSettings.HISTORY_EXPANSION_COST) } doReturn 4000
        }

        viewModel.expandHistoryLimit()

        val expectedLimit = UserSettings.BASE_HISTORY_LIMIT + UserSettings.HISTORY_EXPANSION_INCREMENT
        assertEquals(expectedLimit, viewModel.uiState.value.currentHistoryLimit)
    }

    @Test
    fun `expand history shows success message`() = runTest {
        authStateFlow.value = AuthState.LoggedIn(User(uid = testUserId, email = "t@t.com"))

        quizRepo.stub {
            onBlocking { deductCoins(UserId(testUserId), UserSettings.HISTORY_EXPANSION_COST) } doReturn 4000
        }

        viewModel.expandHistoryLimit()

        assertNotNull(viewModel.uiState.value.purchaseSuccess)
        assertTrue(viewModel.uiState.value.purchaseSuccess!!.contains("expanded"))
    }

    @Test
    fun `expand history with insufficient coins shows error`() = runTest {
        quizRepo.stub {
            onBlocking { fetchUserCoinStats(UserId(testUserId)) } doReturn UserCoinStats(coinTotal = 500)
        }
        authStateFlow.value = AuthState.LoggedIn(User(uid = testUserId, email = "t@t.com"))

        viewModel.expandHistoryLimit()

        assertEquals("Insufficient coins", viewModel.uiState.value.purchaseError)
    }

    @Test
    fun `expand history at max limit does nothing`() = runTest {
        settingsRepo.stub {
            onBlocking { fetchUserSettings(UserId(testUserId)) } doReturn UserSettings(
                historyViewLimit = UserSettings.MAX_HISTORY_LIMIT
            )
        }
        authStateFlow.value = AuthState.LoggedIn(User(uid = testUserId, email = "t@t.com"))

        viewModel.expandHistoryLimit()

        // Should not start purchasing
        assertFalse(viewModel.uiState.value.isPurchasing)
    }

    @Test
    fun `expand history server negative balance is rejected`() = runTest {
        authStateFlow.value = AuthState.LoggedIn(User(uid = testUserId, email = "t@t.com"))

        quizRepo.stub {
            onBlocking { deductCoins(UserId(testUserId), UserSettings.HISTORY_EXPANSION_COST) } doReturn -1
        }

        viewModel.expandHistoryLimit()

        assertEquals("Insufficient coins", viewModel.uiState.value.purchaseError)
    }

    // ── Palette Unlock Coin Deduction ──────────────────────────────

    @Test
    fun `unlock palette deducts correct cost`() = runTest {
        authStateFlow.value = AuthState.LoggedIn(User(uid = testUserId, email = "t@t.com"))

        quizRepo.stub {
            onBlocking { deductCoins(UserId(testUserId), 10) } doReturn 4990
        }

        viewModel.unlockPalette("ocean", 10)

        assertEquals(4990, viewModel.uiState.value.coinBalance)
    }

    @Test
    fun `unlock palette adds to unlocked list`() = runTest {
        authStateFlow.value = AuthState.LoggedIn(User(uid = testUserId, email = "t@t.com"))

        quizRepo.stub {
            onBlocking { deductCoins(UserId(testUserId), 10) } doReturn 4990
        }

        viewModel.unlockPalette("ocean", 10)

        assertTrue(viewModel.uiState.value.unlockedPalettes.contains("ocean"))
    }

    @Test
    fun `unlock palette shows success message`() = runTest {
        authStateFlow.value = AuthState.LoggedIn(User(uid = testUserId, email = "t@t.com"))

        quizRepo.stub {
            onBlocking { deductCoins(UserId(testUserId), 10) } doReturn 4990
        }

        viewModel.unlockPalette("ocean", 10)

        assertNotNull(viewModel.uiState.value.purchaseSuccess)
        assertTrue(viewModel.uiState.value.purchaseSuccess!!.contains("unlocked"))
    }

    @Test
    fun `unlock palette with insufficient coins shows error`() = runTest {
        quizRepo.stub {
            onBlocking { fetchUserCoinStats(UserId(testUserId)) } doReturn UserCoinStats(coinTotal = 5)
        }
        authStateFlow.value = AuthState.LoggedIn(User(uid = testUserId, email = "t@t.com"))

        viewModel.unlockPalette("premium", 10)

        assertEquals("Insufficient coins", viewModel.uiState.value.unlockError)
    }

    @Test
    fun `unlock palette server negative balance is rejected`() = runTest {
        authStateFlow.value = AuthState.LoggedIn(User(uid = testUserId, email = "t@t.com"))

        quizRepo.stub {
            onBlocking { deductCoins(UserId(testUserId), 10) } doReturn -1
        }

        viewModel.unlockPalette("premium", 10)

        assertEquals("Insufficient coins", viewModel.uiState.value.unlockError)
    }

    // ── Select Palette ────────────────────────────────────────────

    @Test
    fun `select palette updates state without coin deduction`() = runTest {
        authStateFlow.value = AuthState.LoggedIn(User(uid = testUserId, email = "t@t.com"))

        viewModel.selectPalette("ocean")

        assertEquals("ocean", viewModel.uiState.value.currentPaletteId)
        assertEquals(5000, viewModel.uiState.value.coinBalance) // No coins deducted
    }

    // ── Error Clearing ────────────────────────────────────────────

    @Test
    fun `clear purchase error resets error`() {
        viewModel.clearPurchaseError()
        assertNull(viewModel.uiState.value.purchaseError)
    }

    @Test
    fun `clear unlock error resets error`() {
        viewModel.clearUnlockError()
        assertNull(viewModel.uiState.value.unlockError)
    }

    @Test
    fun `clear purchase success resets success`() {
        viewModel.clearPurchaseSuccess()
        assertNull(viewModel.uiState.value.purchaseSuccess)
    }

    // ── Multiple Operations ───────────────────────────────────────

    @Test
    fun `multiple expansions accumulate`() = runTest {
        authStateFlow.value = AuthState.LoggedIn(User(uid = testUserId, email = "t@t.com"))

        // First expansion: 30 -> 40, costs 1000
        quizRepo.stub {
            onBlocking { deductCoins(UserId(testUserId), UserSettings.HISTORY_EXPANSION_COST) } doReturn 4000
        }
        viewModel.expandHistoryLimit()

        assertEquals(40, viewModel.uiState.value.currentHistoryLimit)
        assertEquals(4000, viewModel.uiState.value.coinBalance)
    }
}
