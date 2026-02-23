package com.example.fyp.screens.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fyp.data.user.FirebaseAuthRepository
import com.example.fyp.data.user.FirestoreFavoritesRepository
import com.example.fyp.domain.speech.SpeakTextUseCase
import com.example.fyp.data.settings.SharedSettingsDataSource
import com.example.fyp.model.user.AuthState
import com.example.fyp.model.FavoriteRecord
import com.example.fyp.model.SpeechResult
import com.example.fyp.model.user.UserSettings
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FavoritesUiState(
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val favorites: List<FavoriteRecord> = emptyList(),
    val error: String? = null,
    val speakingId: String? = null,
    val visibleCount: Int = 20, // Initial visible count for lazy loading
    val hasMore: Boolean = false // Whether there are more favorites to load
)

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val authRepo: FirebaseAuthRepository,
    private val favoritesRepo: FirestoreFavoritesRepository,
    private val speakTextUseCase: SpeakTextUseCase,
    private val sharedSettings: SharedSettingsDataSource
) : ViewModel() {

    private val _uiState = MutableStateFlow(FavoritesUiState())
    val uiState: StateFlow<FavoritesUiState> = _uiState.asStateFlow()

    private var settingsJob: Job? = null
    private var currentUserId: String? = null
    private var userSettings = UserSettings()

    init {
        viewModelScope.launch {
            authRepo.currentUserState.collect { auth ->
                when (auth) {
                    is AuthState.LoggedIn -> {
                        currentUserId = auth.user.uid
                        loadFavorites(auth.user.uid)
                        // Use shared settings instead of creating new listener
                        sharedSettings.startObserving(auth.user.uid)
                        settingsJob?.cancel()
                        settingsJob = launch {
                            sharedSettings.settings.collect { settings ->
                                userSettings = settings
                            }
                        }
                    }
                    AuthState.LoggedOut -> {
                        settingsJob?.cancel()
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

    /**
     * Load favorites with lazy loading support (Priority 2 #9: Lazy Loading for Favorites).
     * Initially loads only visibleCount items to improve performance.
     */
    private fun loadFavorites(userId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val allFavorites = favoritesRepo.getAllFavoritesOnce(userId)
                val visibleCount = _uiState.value.visibleCount
                val visibleFavorites = allFavorites.take(visibleCount)
                val hasMore = allFavorites.size > visibleCount

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    favorites = visibleFavorites,
                    hasMore = hasMore,
                    error = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to load favorites"
                )
            }
        }
    }

    /**
     * Load more favorites (for infinite scroll or "Load More" button).
     */
    fun loadMoreFavorites() {
        val userId = currentUserId ?: return
        viewModelScope.launch {
            try {
                val allFavorites = favoritesRepo.getAllFavoritesOnce(userId)
                val newVisibleCount = _uiState.value.visibleCount + 20
                val visibleFavorites = allFavorites.take(newVisibleCount)
                val hasMore = allFavorites.size > newVisibleCount

                _uiState.value = _uiState.value.copy(
                    favorites = visibleFavorites,
                    visibleCount = newVisibleCount,
                    hasMore = hasMore
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Failed to load more favorites"
                )
            }
        }
    }

    /**
     * Refresh favorites - call when screen becomes visible or user pulls to refresh.
     */
    fun refresh() {
        val userId = currentUserId ?: return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isRefreshing = true)
            try {
                val allFavorites = favoritesRepo.getAllFavoritesOnce(userId)
                val visibleCount = _uiState.value.visibleCount
                val visibleFavorites = allFavorites.take(visibleCount)
                val hasMore = allFavorites.size > visibleCount

                _uiState.value = _uiState.value.copy(
                    isRefreshing = false,
                    favorites = visibleFavorites,
                    hasMore = hasMore,
                    error = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isRefreshing = false,
                    error = e.message ?: "Failed to refresh favorites"
                )
            }
        }
    }

    fun removeFavorite(favoriteId: String) {
        val userId = currentUserId ?: return
        viewModelScope.launch {
            try {
                favoritesRepo.removeFavorite(userId, favoriteId)
                // Update local state immediately instead of waiting for listener
                _uiState.value = _uiState.value.copy(
                    favorites = _uiState.value.favorites.filter { it.id != favoriteId }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Failed to remove favorite"
                )
            }
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

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
