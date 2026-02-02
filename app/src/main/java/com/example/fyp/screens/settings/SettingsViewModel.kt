package com.example.fyp.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fyp.core.validateScale
import com.example.fyp.data.user.FirebaseAuthRepository
import com.example.fyp.domain.settings.ObserveUserSettingsUseCase
import com.example.fyp.domain.settings.SetFontSizeScaleUseCase
import com.example.fyp.domain.settings.SetPrimaryLanguageUseCase
import com.example.fyp.domain.settings.SetThemeModeUseCase
import com.example.fyp.domain.settings.SetColorPaletteUseCase
import com.example.fyp.domain.settings.UnlockColorPaletteWithCoinsUseCase
import com.example.fyp.domain.settings.UnlockColorPaletteWithCoinsUseCase.Result as UnlockResult
import com.example.fyp.model.user.AuthState
import com.example.fyp.model.ui.UiTextKey
import com.example.fyp.model.user.UserSettings
import com.example.fyp.model.UserCoinStats
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val isLoading: Boolean = true,
    val errorKey: UiTextKey? = null,
    val errorRaw: String? = null,
    val uid: String? = null,
    val settings: UserSettings = UserSettings(),
    val coinStats: UserCoinStats = UserCoinStats(),
    val unlockingPaletteId: String? = null,
    val unlockError: String? = null,
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val authRepo: FirebaseAuthRepository,
    private val observeSettings: ObserveUserSettingsUseCase,
    private val setPrimaryLanguage: SetPrimaryLanguageUseCase,
    private val setFontSizeScale: SetFontSizeScaleUseCase,
    private val setThemeMode: SetThemeModeUseCase,
    private val setColorPalette: SetColorPaletteUseCase,
    private val unlockColorPaletteWithCoins: UnlockColorPaletteWithCoinsUseCase,
    private val quizRepo: com.example.fyp.data.learning.FirestoreQuizRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    private var settingsJob: Job? = null
    private var coinsJob: Job? = null

    init {
        viewModelScope.launch {
            authRepo.currentUserState.collect { auth ->
                when (auth) {
                    is AuthState.LoggedIn -> start(auth.user.uid)
                    AuthState.Loading -> {
                        settingsJob?.cancel()
                        coinsJob?.cancel()
                        _uiState.value = SettingsUiState(isLoading = true)
                    }
                    AuthState.LoggedOut -> {
                        settingsJob?.cancel()
                        coinsJob?.cancel()
                        _uiState.value = SettingsUiState(
                            isLoading = false,
                            errorKey = UiTextKey.SettingsNotLoggedInWarning
                        )
                    }
                }
            }
        }
    }

    private fun start(uid: String) {
        settingsJob?.cancel()
        coinsJob?.cancel()
        _uiState.value = _uiState.value.copy(
            isLoading = true,
            errorKey = null,
            errorRaw = null,
            uid = uid
        )

        settingsJob = viewModelScope.launch {
            observeSettings(uid)
                .catch { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorKey = null,
                        errorRaw = e.message ?: "Load failed"
                    )
                }
                .collect { s ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        settings = s,
                        errorKey = null,
                        errorRaw = null
                    )
                }
        }

        // Observe coin stats for color palette unlocking
        coinsJob = viewModelScope.launch {
            quizRepo.observeUserCoinStats(uid)
                .catch { /* Ignore coin stats errors */ }
                .collect { stats ->
                    _uiState.value = _uiState.value.copy(coinStats = stats)
                }
        }
    }

    fun updatePrimaryLanguage(newCode: String) {
        val uid = _uiState.value.uid ?: run {
            _uiState.value = _uiState.value.copy(errorKey = UiTextKey.SettingsNotLoggedInWarning, errorRaw = null)
            return
        }

        viewModelScope.launch {
            runCatching { setPrimaryLanguage(uid, newCode) }
                .onSuccess {
                    _uiState.value = _uiState.value.copy(
                        settings = _uiState.value.settings.copy(primaryLanguageCode = newCode),
                        errorKey = null,
                        errorRaw = null
                    )
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(
                        errorKey = null,
                        errorRaw = e.message ?: "Save failed"
                    )
                }
        }
    }

    fun updateFontSizeScale(scale: Float) {
        val uid = _uiState.value.uid ?: run {
            _uiState.value = _uiState.value.copy(errorKey = UiTextKey.SettingsNotLoggedInWarning, errorRaw = null)
            return
        }

        val validated = validateScale(scale)

        viewModelScope.launch {
            runCatching { setFontSizeScale(uid, validated) }
                .onSuccess {
                    _uiState.value = _uiState.value.copy(
                        settings = _uiState.value.settings.copy(fontSizeScale = validated),
                        errorKey = null,
                        errorRaw = null
                    )
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(
                        errorKey = null,
                        errorRaw = e.message ?: "Font size save failed"
                    )
                }
        }
    }

    fun updateThemeMode(newMode: String) {
        val uid = _uiState.value.uid ?: run {
            _uiState.value = _uiState.value.copy(errorKey = UiTextKey.SettingsNotLoggedInWarning, errorRaw = null)
            return
        }

        viewModelScope.launch {
            runCatching { setThemeMode(uid, newMode) }
                .onSuccess {
                    _uiState.value = _uiState.value.copy(
                        settings = _uiState.value.settings.copy(themeMode = newMode),
                        errorKey = null,
                        errorRaw = null
                    )
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(
                        errorKey = null,
                        errorRaw = e.message ?: "Theme save failed"
                    )
                }
        }
    }

    fun updateColorPalette(paletteId: String) {
        val uid = _uiState.value.uid ?: run {
            _uiState.value = _uiState.value.copy(errorKey = UiTextKey.SettingsNotLoggedInWarning, errorRaw = null)
            return
        }

        viewModelScope.launch {
            runCatching { setColorPalette(uid, paletteId) }
                .onSuccess {
                    _uiState.value = _uiState.value.copy(
                        settings = _uiState.value.settings.copy(colorPaletteId = paletteId),
                        errorKey = null,
                        errorRaw = null
                    )
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(
                        errorKey = null,
                        errorRaw = e.message ?: "Color palette save failed"
                    )
                }
        }
    }

    fun unlockPaletteWithCoins(paletteId: String, cost: Int) {
        val uid = _uiState.value.uid ?: run {
            _uiState.value = _uiState.value.copy(errorKey = UiTextKey.SettingsNotLoggedInWarning, errorRaw = null)
            return
        }

        _uiState.value = _uiState.value.copy(unlockingPaletteId = paletteId, unlockError = null)

        viewModelScope.launch {
            runCatching { unlockColorPaletteWithCoins(uid, paletteId, cost) }
                .onSuccess { result ->
                    when (result) {
                        UnlockResult.Success -> {
                            val currentSettings = _uiState.value.settings
                            val updated = currentSettings.unlockedPalettes + paletteId
                            _uiState.value = _uiState.value.copy(
                                settings = currentSettings.copy(unlockedPalettes = updated),
                                errorKey = null,
                                errorRaw = null,
                                unlockingPaletteId = null,
                                unlockError = null
                            )
                        }
                        UnlockResult.InsufficientCoins -> {
                            _uiState.value = _uiState.value.copy(
                                unlockError = "Insufficient coins",
                                unlockingPaletteId = null
                            )
                        }
                    }
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(
                        unlockError = e.message ?: "Failed to unlock palette",
                        unlockingPaletteId = null
                    )
                }
        }
    }
}