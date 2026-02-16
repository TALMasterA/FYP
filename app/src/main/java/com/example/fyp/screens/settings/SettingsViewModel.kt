package com.example.fyp.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fyp.core.validateScale
import com.example.fyp.data.user.FirebaseAuthRepository
import com.example.fyp.data.settings.SharedSettingsDataSource
import com.example.fyp.domain.settings.SetFontSizeScaleUseCase
import com.example.fyp.domain.settings.SetPrimaryLanguageUseCase
import com.example.fyp.domain.settings.SetThemeModeUseCase
import com.example.fyp.domain.settings.SetColorPaletteUseCase
import com.example.fyp.domain.settings.UnlockColorPaletteWithCoinsUseCase
import com.example.fyp.domain.settings.UnlockColorPaletteWithCoinsUseCase.Result as UnlockResult
import com.example.fyp.domain.settings.SetVoiceForLanguageUseCase
import com.example.fyp.domain.settings.SetAutoThemeEnabledUseCase
import com.example.fyp.model.LanguageCode
import com.example.fyp.model.PaletteId
import com.example.fyp.model.UserId
import com.example.fyp.model.VoiceName
import com.example.fyp.model.user.AuthState
import com.example.fyp.model.ui.UiTextKey
import com.example.fyp.model.user.UserSettings
import com.example.fyp.model.UserCoinStats
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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
    private val sharedSettings: SharedSettingsDataSource,
    private val setPrimaryLanguage: SetPrimaryLanguageUseCase,
    private val setFontSizeScale: SetFontSizeScaleUseCase,
    private val setThemeMode: SetThemeModeUseCase,
    private val setColorPalette: SetColorPaletteUseCase,
    private val unlockColorPaletteWithCoins: UnlockColorPaletteWithCoinsUseCase,
    private val setVoiceForLanguage: SetVoiceForLanguageUseCase,
    private val setAutoThemeEnabled: SetAutoThemeEnabledUseCase,
    private val quizRepo: com.example.fyp.data.learning.FirestoreQuizRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    private var settingsJob: Job? = null

    init {
        viewModelScope.launch {
            authRepo.currentUserState.collect { auth ->
                when (auth) {
                    is AuthState.LoggedIn -> start(auth.user.uid)
                    AuthState.Loading -> {
                        settingsJob?.cancel()
                        _uiState.value = SettingsUiState(isLoading = true)
                    }
                    AuthState.LoggedOut -> {
                        settingsJob?.cancel()
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
        _uiState.value = _uiState.value.copy(
            isLoading = true,
            errorKey = null,
            errorRaw = null,
            uid = uid
        )

        // Use shared settings instead of creating new listener
        sharedSettings.startObserving(uid)
        settingsJob = viewModelScope.launch {
            sharedSettings.settings.collect { s ->
                _uiState.value = _uiState.value.copy(
                    isLoading = sharedSettings.isLoading.value,
                    settings = s,
                    errorKey = null,
                    errorRaw = null
                )
            }
        }

        // Fetch coin stats once instead of real-time listener
        refreshCoinStats(uid)
    }

    /**
     * Refresh coin stats on demand (e.g., when screen becomes visible).
     */
    fun refreshCoinStats(uid: String? = _uiState.value.uid) {
        val userId = uid ?: return
        viewModelScope.launch {
            try {
                val stats = quizRepo.fetchUserCoinStats(UserId(userId)) ?: UserCoinStats()
                _uiState.value = _uiState.value.copy(coinStats = stats)
            } catch (_: Exception) {
                // Ignore coin fetch errors
            }
        }
    }

    fun updatePrimaryLanguage(newCode: String) {
        val uid = _uiState.value.uid ?: run {
            _uiState.value = _uiState.value.copy(errorKey = UiTextKey.SettingsNotLoggedInWarning, errorRaw = null)
            return
        }

        viewModelScope.launch {
            runCatching { setPrimaryLanguage(UserId(uid), LanguageCode(newCode)) }
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
            runCatching { setFontSizeScale(UserId(uid), validated) }
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
            // Handle "scheduled" separately
            if (newMode == "scheduled") {
                runCatching { setAutoThemeEnabled(UserId(uid), true) }
                    .onSuccess {
                        _uiState.value = _uiState.value.copy(
                            settings = _uiState.value.settings.copy(autoThemeEnabled = true),
                            errorKey = null,
                            errorRaw = null
                        )
                    }
                    .onFailure { e ->
                        _uiState.value = _uiState.value.copy(
                            errorRaw = e.message ?: "Theme save failed"
                        )
                    }
            } else {
                // For System/Light/Dark, we first disable auto theme, then set the mode
                // We do this to ensure "scheduled" logic doesn't override the manual selection

                // 1. Disable auto theme if it enabled
                if (_uiState.value.settings.autoThemeEnabled) {
                     runCatching { setAutoThemeEnabled(UserId(uid), false) }
                     // Update local state temporarily
                     _uiState.value = _uiState.value.copy(
                        settings = _uiState.value.settings.copy(autoThemeEnabled = false)
                    )
                }

                // 2. Set the mode
                runCatching { setThemeMode(UserId(uid), newMode) }
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
    }

    fun updateColorPalette(paletteId: String) {
        val uid = _uiState.value.uid ?: run {
            _uiState.value = _uiState.value.copy(errorKey = UiTextKey.SettingsNotLoggedInWarning, errorRaw = null)
            return
        }

        viewModelScope.launch {
            runCatching { setColorPalette(UserId(uid), PaletteId(paletteId)) }
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
            runCatching { unlockColorPaletteWithCoins(UserId(uid), PaletteId(paletteId), cost) }
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

    fun updateVoiceForLanguage(languageCode: String, voiceName: String) {
        val uid = _uiState.value.uid ?: run {
            _uiState.value = _uiState.value.copy(errorKey = UiTextKey.SettingsNotLoggedInWarning, errorRaw = null)
            return
        }

        viewModelScope.launch {
            runCatching { setVoiceForLanguage(UserId(uid), LanguageCode(languageCode), VoiceName(voiceName)) }
                .onSuccess {
                    // Update local state optimistically
                    val currentSettings = _uiState.value.settings
                    val updatedVoices = currentSettings.voiceSettings.toMutableMap()
                    updatedVoices[languageCode] = voiceName
                    _uiState.value = _uiState.value.copy(
                        settings = currentSettings.copy(voiceSettings = updatedVoices),
                        errorKey = null,
                        errorRaw = null
                    )
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(
                        errorKey = null,
                        errorRaw = e.message ?: "Failed to update voice"
                    )
                }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorKey = null, errorRaw = null)
    }

    fun clearUnlockError() {
        _uiState.value = _uiState.value.copy(unlockError = null)
    }

    fun updateAutoThemeEnabled(enabled: Boolean) {
        val uid = _uiState.value.uid ?: run {
            _uiState.value = _uiState.value.copy(errorKey = UiTextKey.SettingsNotLoggedInWarning, errorRaw = null)
            return
        }

        viewModelScope.launch {
            runCatching { setAutoThemeEnabled(UserId(uid), enabled) }
                .onSuccess {
                    _uiState.value = _uiState.value.copy(
                        settings = _uiState.value.settings.copy(autoThemeEnabled = enabled),
                        errorKey = null,
                        errorRaw = null
                    )
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(
                        errorKey = null,
                        errorRaw = e.message ?: "Failed to update auto theme setting"
                    )
                }
        }
    }
}