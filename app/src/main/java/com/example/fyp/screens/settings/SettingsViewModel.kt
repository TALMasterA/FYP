package com.example.fyp.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fyp.data.auth.FirebaseAuthRepository
import com.example.fyp.domain.settings.ObserveUserSettingsUseCase
import com.example.fyp.domain.settings.SetPrimaryLanguageUseCase
import com.example.fyp.domain.settings.SetFontSizeScaleUseCase
import com.example.fyp.model.AuthState
import com.example.fyp.model.UserSettings
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
    val error: String? = null,
    val uid: String? = null,
    val settings: UserSettings = UserSettings(),
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val authRepo: FirebaseAuthRepository,
    private val observeSettings: ObserveUserSettingsUseCase,
    private val setPrimaryLanguage: SetPrimaryLanguageUseCase,
    private val setFontSizeScale: SetFontSizeScaleUseCase
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
                        _uiState.value = SettingsUiState(isLoading = false, error = "Not logged in")
                    }
                }
            }
        }
    }

    private fun start(uid: String) {
        settingsJob?.cancel()
        _uiState.value = _uiState.value.copy(isLoading = true, error = null, uid = uid)

        settingsJob = viewModelScope.launch {
            observeSettings(uid)
                .catch { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = e.message ?: "Load failed"
                    )
                }
                .collect { s ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        settings = s,
                        error = null
                    )
                }
        }
    }

    fun updatePrimaryLanguage(newCode: String) {
        val uid = _uiState.value.uid ?: return
        viewModelScope.launch {
            runCatching {
                setPrimaryLanguage(uid, newCode)
            }.onFailure { e ->
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Save failed"
                )
            }
        }
    }

    fun updateFontSizeScale(scale: Float) {
        val uid = _uiState.value.uid ?: return
        viewModelScope.launch {
            runCatching {
                setFontSizeScale(uid, scale)
            }.onFailure { e ->
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Font size save failed"
                )
            }
        }
    }
}