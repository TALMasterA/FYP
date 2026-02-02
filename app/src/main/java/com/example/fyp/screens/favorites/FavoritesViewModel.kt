package com.example.fyp.screens.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fyp.data.user.FirebaseAuthRepository
import com.example.fyp.data.user.FirestoreFavoritesRepository
import com.example.fyp.domain.speech.SpeakTextUseCase
import com.example.fyp.domain.settings.ObserveUserSettingsUseCase
import com.example.fyp.model.user.AuthState
import com.example.fyp.model.FavoriteRecord
import com.example.fyp.model.SpeechResult
import com.example.fyp.model.user.UserSettings
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FavoritesUiState(
    val isLoading: Boolean = true,
    val favorites: List<FavoriteRecord> = emptyList(),
    val error: String? = null,
    val speakingId: String? = null
)

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val authRepo: FirebaseAuthRepository,
    private val favoritesRepo: FirestoreFavoritesRepository,
    private val speakTextUseCase: SpeakTextUseCase,
    private val observeSettings: ObserveUserSettingsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(FavoritesUiState())
    val uiState: StateFlow<FavoritesUiState> = _uiState.asStateFlow()

    private var favoritesJob: Job? = null
    private var currentUserId: String? = null
    private var userSettings = UserSettings()

    init {
        viewModelScope.launch {
            authRepo.currentUserState.collect { auth ->
                when (auth) {
                    is AuthState.LoggedIn -> {
                        currentUserId = auth.user.uid
                        observeFavorites(auth.user.uid)
                        // Observe user settings for voice preferences
                        launch {
                            observeSettings(auth.user.uid).collect { settings ->
                                userSettings = settings
                            }
                        }
                    }
                    AuthState.LoggedOut -> {
                        favoritesJob?.cancel()
                        currentUserId = null
                        userSettings = UserSettings()
                        _uiState.value = FavoritesUiState(isLoading = false)
                    }
                    AuthState.Loading -> {
                        _uiState.value = _uiState.value.copy(isLoading = true)
                    }
                }
            }
        }
    }

    private fun observeFavorites(userId: String) {
        favoritesJob?.cancel()
        favoritesJob = viewModelScope.launch {
            favoritesRepo.observeFavorites(userId)
                .catch { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to load favorites"
                    )
                }
                .collect { favorites ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        favorites = favorites,
                        error = null
                    )
                }
        }
    }

    fun removeFavorite(favoriteId: String) {
        val userId = currentUserId ?: return
        viewModelScope.launch {
            favoritesRepo.removeFavorite(userId, favoriteId)
        }
    }

    fun speak(text: String, languageCode: String, speakingId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(speakingId = speakingId)
            val voiceName = userSettings.voiceSettings[languageCode]
            when (val result = speakTextUseCase(text, languageCode, voiceName)) {
                is SpeechResult.Success -> {
                    _uiState.value = _uiState.value.copy(speakingId = null)
                }
                is SpeechResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        speakingId = null,
                        error = result.message
                    )
                }
            }
        }
    }
}
