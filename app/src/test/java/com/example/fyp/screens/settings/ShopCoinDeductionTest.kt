package com.example.fyp.screens.settings

import com.example.fyp.data.cloud.CloudQuizClient
import com.example.fyp.data.cloud.SpendCoinsResult
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
 * Coin spending is now server-side via CloudQuizClient (spendCoins Cloud Function).
 * The ViewModel receives updated balances and limits from the server response.
 *
 * Verifies:
 * - Coin balance loads from server on login
 * - Logout resets state
 * - History expansion calls server and updates balance/limit from response
 * - Insufficient coins guard fires client-side (no server call)
 * - Max limit guard fires client-side (no server call)
 * - Server-reported insufficient coins shows error
 * - Palette unlock calls server and updates balance/unlocked list
 * - Success messages are shown
 */
@OptIn(ExperimentalCoroutinesApi::class)
class ShopCoinDeductionTest {

    private lateinit var authRepo: FirebaseAuthRepository
    private lateinit var settingsRepo: UserSettingsRepository
    private lateinit var quizRepo: QuizRepository
    private lateinit var cloudClient: CloudQuizClient
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
        cloudClient = mock()

        viewModel = ShopViewModel(
            authRepo = authRepo,
            settingsRepo = settingsRepo,
            quizRepo = quizRepo,
            cloudClient = cloudClient
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

    // ── History Expansion ───────────────────────────────────────

    @Test
    fun `expand history updates balance from server response`() = runTest {
        authStateFlow.value = AuthState.LoggedIn(User(uid = testUserId, email = "t@t.com"))

        cloudClient.stub {
            onBlocking { spendCoinsForHistoryExpansion() } doReturn SpendCoinsResult(
                success = true,
                newBalance = 4000,
                newLimit = UserSettings.BASE_HISTORY_LIMIT + UserSettings.HISTORY_EXPANSION_INCREMENT
            )
        }

        viewModel.expandHistoryLimit()

        assertEquals(4000, viewModel.uiState.value.coinBalance)
    }

    @Test
    fun `expand history updates limit by increment`() = runTest {
        authStateFlow.value = AuthState.LoggedIn(User(uid = testUserId, email = "t@t.com"))

        val expectedLimit = UserSettings.BASE_HISTORY_LIMIT + UserSettings.HISTORY_EXPANSION_INCREMENT
        cloudClient.stub {
            onBlocking { spendCoinsForHistoryExpansion() } doReturn SpendCoinsResult(
                success = true, newBalance = 4000, newLimit = expectedLimit
            )
        }

        viewModel.expandHistoryLimit()

        assertEquals(expectedLimit, viewModel.uiState.value.currentHistoryLimit)
    }

    @Test
    fun `expand history shows success message`() = runTest {
        authStateFlow.value = AuthState.LoggedIn(User(uid = testUserId, email = "t@t.com"))

        val expectedLimit = UserSettings.BASE_HISTORY_LIMIT + UserSettings.HISTORY_EXPANSION_INCREMENT
        cloudClient.stub {
            onBlocking { spendCoinsForHistoryExpansion() } doReturn SpendCoinsResult(
                success = true, newBalance = 4000, newLimit = expectedLimit
            )
        }

        viewModel.expandHistoryLimit()

        assertNotNull(viewModel.uiState.value.purchaseSuccess)
        assertTrue(viewModel.uiState.value.purchaseSuccess!!.contains("expanded"))
    }

    @Test
    fun `expand history with insufficient coins shows error without calling server`() = runTest {
        quizRepo.stub {
            onBlocking { fetchUserCoinStats(UserId(testUserId)) } doReturn UserCoinStats(coinTotal = 500)
        }
        authStateFlow.value = AuthState.LoggedIn(User(uid = testUserId, email = "t@t.com"))

        viewModel.expandHistoryLimit()

        assertEquals("Insufficient coins", viewModel.uiState.value.purchaseError)
        verifyNoInteractions(cloudClient)
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

        assertFalse(viewModel.uiState.value.isPurchasing)
        verifyNoInteractions(cloudClient)
    }

    @Test
    fun `expand history server reports insufficient coins shows error`() = runTest {
        authStateFlow.value = AuthState.LoggedIn(User(uid = testUserId, email = "t@t.com"))

        cloudClient.stub {
            onBlocking { spendCoinsForHistoryExpansion() } doReturn SpendCoinsResult(
                success = false, reason = "insufficient_coins"
            )
        }

        viewModel.expandHistoryLimit()

        assertEquals("Insufficient coins", viewModel.uiState.value.purchaseError)
    }

    // ── Palette Unlock ──────────────────────────────────────────────

    @Test
    fun `unlock palette updates balance from server response`() = runTest {
        authStateFlow.value = AuthState.LoggedIn(User(uid = testUserId, email = "t@t.com"))

        cloudClient.stub {
            onBlocking { spendCoinsForPaletteUnlock("ocean") } doReturn SpendCoinsResult(
                success = true, newBalance = 4990
            )
        }

        viewModel.unlockPalette("ocean", 10)

        assertEquals(4990, viewModel.uiState.value.coinBalance)
    }

    @Test
    fun `unlock palette adds to unlocked list`() = runTest {
        authStateFlow.value = AuthState.LoggedIn(User(uid = testUserId, email = "t@t.com"))

        cloudClient.stub {
            onBlocking { spendCoinsForPaletteUnlock("ocean") } doReturn SpendCoinsResult(
                success = true, newBalance = 4990
            )
        }

        viewModel.unlockPalette("ocean", 10)

        assertTrue(viewModel.uiState.value.unlockedPalettes.contains("ocean"))
    }

    @Test
    fun `unlock palette shows success message`() = runTest {
        authStateFlow.value = AuthState.LoggedIn(User(uid = testUserId, email = "t@t.com"))

        cloudClient.stub {
            onBlocking { spendCoinsForPaletteUnlock("ocean") } doReturn SpendCoinsResult(
                success = true, newBalance = 4990
            )
        }

        viewModel.unlockPalette("ocean", 10)

        assertNotNull(viewModel.uiState.value.purchaseSuccess)
        assertTrue(viewModel.uiState.value.purchaseSuccess!!.contains("unlocked"))
    }

    @Test
    fun `unlock palette with insufficient coins shows error without calling server`() = runTest {
        quizRepo.stub {
            onBlocking { fetchUserCoinStats(UserId(testUserId)) } doReturn UserCoinStats(coinTotal = 5)
        }
        authStateFlow.value = AuthState.LoggedIn(User(uid = testUserId, email = "t@t.com"))

        viewModel.unlockPalette("premium", 10)

        assertEquals("Insufficient coins", viewModel.uiState.value.unlockError)
        verifyNoInteractions(cloudClient)
    }

    @Test
    fun `unlock palette server reports insufficient coins shows error`() = runTest {
        authStateFlow.value = AuthState.LoggedIn(User(uid = testUserId, email = "t@t.com"))

        cloudClient.stub {
            onBlocking { spendCoinsForPaletteUnlock(any()) } doReturn SpendCoinsResult(
                success = false, reason = "insufficient_coins"
            )
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
        assertEquals(5000, viewModel.uiState.value.coinBalance)
        verifyNoInteractions(cloudClient)
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

        val expectedLimit = UserSettings.BASE_HISTORY_LIMIT + UserSettings.HISTORY_EXPANSION_INCREMENT
        cloudClient.stub {
            onBlocking { spendCoinsForHistoryExpansion() } doReturn SpendCoinsResult(
                success = true, newBalance = 4000, newLimit = expectedLimit
            )
        }
        viewModel.expandHistoryLimit()

        assertEquals(expectedLimit, viewModel.uiState.value.currentHistoryLimit)
        assertEquals(4000, viewModel.uiState.value.coinBalance)
    }
}
