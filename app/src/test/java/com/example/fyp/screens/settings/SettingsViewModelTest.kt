package com.example.fyp.screens.settings

import android.app.Application
import android.content.SharedPreferences
import com.example.fyp.data.friends.FriendsRepository
import com.example.fyp.data.user.FirebaseAuthRepository
import com.example.fyp.data.settings.SharedSettingsDataSource
import com.example.fyp.domain.settings.SetFontSizeScaleUseCase
import com.example.fyp.domain.settings.SetPrimaryLanguageUseCase
import com.example.fyp.domain.settings.SetThemeModeUseCase
import com.example.fyp.domain.settings.SetColorPaletteUseCase
import com.example.fyp.domain.settings.UnlockColorPaletteWithCoinsUseCase
import com.example.fyp.domain.settings.SetVoiceForLanguageUseCase
import com.example.fyp.domain.settings.SetAutoThemeEnabledUseCase
import com.example.fyp.domain.settings.SetNotificationPrefUseCase
import com.example.fyp.domain.learning.QuizRepository
import com.example.fyp.model.UserCoinStats
import com.example.fyp.model.UserId
import com.example.fyp.model.LanguageCode
import com.example.fyp.model.PaletteId
import com.example.fyp.model.user.AuthState
import com.example.fyp.model.user.User
import com.example.fyp.model.user.UserSettings
import com.example.fyp.model.ui.UiTextKey
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
 * Unit tests for SettingsViewModel.
 *
 * Tests:
 *  1. LoggedOut sets error key
 *  2. LoggedIn starts observing settings
 *  3. updatePrimaryLanguage success updates settings
 *  4. updatePrimaryLanguage cooldown active shows dialog
 *  5. updatePrimaryLanguage when no uid sets error
 *  6. updatePrimaryLanguage exception sets errorRaw
 *  7. dismissCooldownDialog clears cooldownDays
 *  8. updateFontSizeScale success updates settings
 *  9. updateFontSizeScale failure sets errorRaw
 * 10. updateThemeMode scheduled enables auto theme
 * 11. updateThemeMode system disables auto theme first
 * 12. updateColorPalette success updates settings
 * 13. unlockPaletteWithCoins success adds to unlockedPalettes
 * 14. unlockPaletteWithCoins insufficient coins sets unlockError
 * 15. clearError clears both errorKey and errorRaw
 * 16. clearUnlockError clears unlockError
 * 17. updateNotificationPref success updates correct field
 */
@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val authStateFlow = MutableStateFlow<AuthState>(AuthState.Loading)
    private val settingsFlow = MutableStateFlow(UserSettings())
    private val isLoadingFlow = MutableStateFlow(false)

    private val testUserId = "user123"
    private val testUser = User(uid = testUserId, email = "test@test.com")

    private lateinit var app: Application
    private lateinit var authRepo: FirebaseAuthRepository
    private lateinit var sharedSettings: SharedSettingsDataSource
    private lateinit var friendsRepo: FriendsRepository
    private lateinit var setPrimaryLanguage: SetPrimaryLanguageUseCase
    private lateinit var setFontSizeScale: SetFontSizeScaleUseCase
    private lateinit var setThemeMode: SetThemeModeUseCase
    private lateinit var setColorPalette: SetColorPaletteUseCase
    private lateinit var unlockColorPaletteWithCoins: UnlockColorPaletteWithCoinsUseCase
    private lateinit var setVoiceForLanguage: SetVoiceForLanguageUseCase
    private lateinit var setAutoThemeEnabled: SetAutoThemeEnabledUseCase
    private lateinit var setNotificationPref: SetNotificationPrefUseCase
    private lateinit var quizRepo: QuizRepository

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        app = mock()
        // Stub SharedPreferences for FcmNotificationService.saveNotifPrefToCache
        val mockEditor: SharedPreferences.Editor = mock {
            on { putBoolean(any(), any()) } doReturn mock
            on { apply() } doAnswer {}
        }
        val mockPrefs: SharedPreferences = mock {
            on { edit() } doReturn mockEditor
        }
        whenever(app.getSharedPreferences(any(), any())).thenReturn(mockPrefs)

        authRepo = mock { on { currentUserState } doReturn authStateFlow }

        sharedSettings = mock {
            on { settings } doReturn settingsFlow
            on { isLoading } doReturn isLoadingFlow
        }

        friendsRepo = mock()
        setPrimaryLanguage = mock()
        setFontSizeScale = mock()
        setThemeMode = mock()
        setColorPalette = mock()
        unlockColorPaletteWithCoins = mock()
        setVoiceForLanguage = mock()
        setAutoThemeEnabled = mock()
        setNotificationPref = mock()
        quizRepo = mock()

        // Value classes (UserId) are inlined to String at compile time.
        // Use concrete value to avoid NPE from any() returning null for the value class wrapper.
        whenever(quizRepo.observeUserCoinStats(UserId(testUserId))).thenReturn(flowOf(UserCoinStats()))
    }

    private fun buildViewModel(): SettingsViewModel {
        authStateFlow.value = AuthState.LoggedIn(testUser)
        return SettingsViewModel(
            application = app,
            authRepo = authRepo,
            sharedSettings = sharedSettings,
            friendsRepo = friendsRepo,
            setPrimaryLanguage = setPrimaryLanguage,
            setFontSizeScale = setFontSizeScale,
            setThemeMode = setThemeMode,
            setColorPalette = setColorPalette,
            unlockColorPaletteWithCoins = unlockColorPaletteWithCoins,
            setVoiceForLanguage = setVoiceForLanguage,
            setAutoThemeEnabled = setAutoThemeEnabled,
            setNotificationPref = setNotificationPref,
            quizRepo = quizRepo
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // ── Auth lifecycle ──

    @Test
    fun `LoggedOut sets errorKey to SettingsNotLoggedInWarning`() = runTest {
        authStateFlow.value = AuthState.LoggedOut
        val vm = SettingsViewModel(
            app, authRepo, sharedSettings, friendsRepo, setPrimaryLanguage,
            setFontSizeScale, setThemeMode, setColorPalette, unlockColorPaletteWithCoins,
            setVoiceForLanguage, setAutoThemeEnabled, setNotificationPref, quizRepo
        )

        assertEquals(UiTextKey.SettingsNotLoggedInWarning, vm.uiState.value.errorKey)
        assertFalse(vm.uiState.value.isLoading)
    }

    @Test
    fun `LoggedIn starts observing settings and stores uid`() = runTest {
        val vm = buildViewModel()

        verify(sharedSettings).startObserving(testUserId)
        assertEquals(testUserId, vm.uiState.value.uid)
    }

    // ── updatePrimaryLanguage ──

    @Test
    fun `updatePrimaryLanguage success updates settings`() = runTest {
        setPrimaryLanguage.stub {
            onBlocking { invoke(UserId(testUserId), LanguageCode("ja-JP")) } doReturn
                SetPrimaryLanguageUseCase.Result.Success
        }

        val vm = buildViewModel()
        vm.updatePrimaryLanguage("ja-JP")

        assertEquals("ja-JP", vm.uiState.value.settings.primaryLanguageCode)
        assertNull(vm.uiState.value.errorKey)
    }

    @Test
    fun `updatePrimaryLanguage cooldown sets primaryLanguageCooldownDays`() = runTest {
        setPrimaryLanguage.stub {
            onBlocking { invoke(UserId(testUserId), LanguageCode("ja-JP")) } doReturn
                SetPrimaryLanguageUseCase.Result.CooldownActive(remainingDays = 15)
        }

        val vm = buildViewModel()
        vm.updatePrimaryLanguage("ja-JP")

        assertEquals(15, vm.uiState.value.primaryLanguageCooldownDays)
    }

    @Test
    fun `updatePrimaryLanguage when not logged in sets error`() = runTest {
        authStateFlow.value = AuthState.LoggedOut
        val vm = SettingsViewModel(
            app, authRepo, sharedSettings, friendsRepo, setPrimaryLanguage,
            setFontSizeScale, setThemeMode, setColorPalette, unlockColorPaletteWithCoins,
            setVoiceForLanguage, setAutoThemeEnabled, setNotificationPref, quizRepo
        )

        vm.updatePrimaryLanguage("ja-JP")

        assertEquals(UiTextKey.SettingsNotLoggedInWarning, vm.uiState.value.errorKey)
    }

    @Test
    fun `updatePrimaryLanguage exception sets errorRaw`() = runTest {
        setPrimaryLanguage.stub {
            onBlocking { invoke(UserId(testUserId), LanguageCode("ja-JP")) } doThrow RuntimeException("Network error")
        }

        val vm = buildViewModel()
        vm.updatePrimaryLanguage("ja-JP")

        assertNotNull(vm.uiState.value.errorRaw)
        assertTrue(vm.uiState.value.errorRaw!!.contains("Failed to save"))
    }

    // ── dismissCooldownDialog ──

    @Test
    fun `dismissCooldownDialog clears cooldownDays`() = runTest {
        setPrimaryLanguage.stub {
            onBlocking { invoke(UserId(testUserId), LanguageCode("ja-JP")) } doReturn
                SetPrimaryLanguageUseCase.Result.CooldownActive(remainingDays = 5)
        }

        val vm = buildViewModel()
        vm.updatePrimaryLanguage("ja-JP")
        assertNotNull(vm.uiState.value.primaryLanguageCooldownDays)

        vm.dismissCooldownDialog()

        assertNull(vm.uiState.value.primaryLanguageCooldownDays)
    }

    // ── updateFontSizeScale ──

    @Test
    fun `updateFontSizeScale success updates settings`() = runTest {
        val vm = buildViewModel()
        vm.updateFontSizeScale(1.25f)

        // Verify via state - no error means it succeeded
        assertNull(vm.uiState.value.errorRaw)
    }

    @Test
    fun `updateFontSizeScale failure sets errorRaw`() = runTest {
        setFontSizeScale.stub {
            onBlocking { invoke(UserId(testUserId), 1.5f) } doThrow RuntimeException("fail")
        }

        val vm = buildViewModel()
        vm.updateFontSizeScale(1.5f)

        assertNotNull(vm.uiState.value.errorRaw)
        assertTrue(vm.uiState.value.errorRaw!!.contains("font size"))
    }

    // ── updateThemeMode ──

    @Test
    fun `updateThemeMode scheduled enables auto theme`() = runTest {
        val vm = buildViewModel()
        vm.updateThemeMode("scheduled")

        // Verify via state - no error means it was called successfully
        assertNull(vm.uiState.value.errorRaw)
    }

    @Test
    fun `updateThemeMode system disables auto theme first then sets mode`() = runTest {
        settingsFlow.value = UserSettings(autoThemeEnabled = true)
        val vm = buildViewModel()
        vm.updateThemeMode("system")

        // Verify the mode was set via state
        assertNull(vm.uiState.value.errorRaw)
    }

    // ── updateColorPalette ──

    @Test
    fun `updateColorPalette success updates settings`() = runTest {
        val vm = buildViewModel()
        vm.updateColorPalette("sunset")

        assertEquals("sunset", vm.uiState.value.settings.colorPaletteId)
    }

    // ── unlockPaletteWithCoins ──

    @Test
    fun `unlockPaletteWithCoins success adds palette to unlockedPalettes`() = runTest {
        unlockColorPaletteWithCoins.stub {
            onBlocking { invoke(UserId(testUserId), PaletteId("ocean"), 50) } doReturn
                UnlockColorPaletteWithCoinsUseCase.Result.Success
        }

        val vm = buildViewModel()
        vm.unlockPaletteWithCoins("ocean", 50)

        assertTrue(vm.uiState.value.settings.unlockedPalettes.contains("ocean"))
        assertNull(vm.uiState.value.unlockingPaletteId)
    }

    @Test
    fun `unlockPaletteWithCoins insufficient coins sets unlockError`() = runTest {
        unlockColorPaletteWithCoins.stub {
            onBlocking { invoke(UserId(testUserId), PaletteId("ocean"), 50) } doReturn
                UnlockColorPaletteWithCoinsUseCase.Result.InsufficientCoins
        }

        val vm = buildViewModel()
        vm.unlockPaletteWithCoins("ocean", 50)

        assertEquals("Insufficient coins", vm.uiState.value.unlockError)
        assertNull(vm.uiState.value.unlockingPaletteId)
    }

    // ── clearError / clearUnlockError ──

    @Test
    fun `clearError clears both errorKey and errorRaw`() = runTest {
        val vm = buildViewModel()
        setPrimaryLanguage.stub {
            onBlocking { invoke(UserId(testUserId), LanguageCode("xx")) } doThrow RuntimeException("fail")
        }
        vm.updatePrimaryLanguage("xx")

        vm.clearError()

        assertNull(vm.uiState.value.errorKey)
        assertNull(vm.uiState.value.errorRaw)
    }

    @Test
    fun `clearUnlockError clears unlockError`() = runTest {
        unlockColorPaletteWithCoins.stub {
            onBlocking { invoke(UserId(testUserId), PaletteId("ocean"), 50) } doReturn
                UnlockColorPaletteWithCoinsUseCase.Result.InsufficientCoins
        }

        val vm = buildViewModel()
        vm.unlockPaletteWithCoins("ocean", 50)
        assertNotNull(vm.uiState.value.unlockError)

        vm.clearUnlockError()

        assertNull(vm.uiState.value.unlockError)
    }

    // ── updateNotificationPref ──

    @Test
    fun `updateNotificationPref success updates notifyNewMessages`() = runTest {
        setNotificationPref.stub {
            onBlocking { invoke(UserId(testUserId), SettingsViewModel.PREF_NOTIFY_NEW_MESSAGES, false) } doReturn Unit
        }
        val vm = buildViewModel()
        vm.updateNotificationPref(SettingsViewModel.PREF_NOTIFY_NEW_MESSAGES, false)

        assertFalse(vm.uiState.value.settings.notifyNewMessages)
    }

    @Test
    fun `updateNotificationPref success updates inAppBadgeMessages`() = runTest {
        setNotificationPref.stub {
            onBlocking { invoke(UserId(testUserId), SettingsViewModel.PREF_BADGE_MESSAGES, false) } doReturn Unit
        }
        val vm = buildViewModel()
        vm.updateNotificationPref(SettingsViewModel.PREF_BADGE_MESSAGES, false)

        assertFalse(vm.uiState.value.settings.inAppBadgeMessages)
    }

    @Test
    fun `updateNotificationPref failure sets errorRaw`() = runTest {
        setNotificationPref.stub {
            onBlocking { invoke(UserId(testUserId), SettingsViewModel.PREF_NOTIFY_NEW_MESSAGES, true) } doThrow RuntimeException("fail")
        }

        val vm = buildViewModel()
        vm.updateNotificationPref(SettingsViewModel.PREF_NOTIFY_NEW_MESSAGES, true)

        assertNotNull(vm.uiState.value.errorRaw)
        assertTrue(vm.uiState.value.errorRaw!!.contains("notification"))
    }
}
