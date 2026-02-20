package com.example.fyp.data.settings

import com.example.fyp.model.UserId // Added
import com.example.fyp.model.user.UserSettings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Shared data source for user settings.
 * This prevents multiple ViewModels from creating separate Firestore listeners
 * for the same settings data, reducing database reads significantly.
 *
 * Instead of each ViewModel observing settings directly:
 * - SettingsViewModel
 * - WordBankViewModel
 * - FavoritesViewModel
 * - SpeechViewModel
 *
 * All now share this single data source.
 */
@Singleton
class SharedSettingsDataSource @Inject constructor(
    private val settingsRepo: UserSettingsRepository
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private var currentUserId: String? = null
    private var settingsJob: kotlinx.coroutines.Job? = null

    // Cached settings - shared across all subscribers
    private val _settings = MutableStateFlow(UserSettings())
    val settings: StateFlow<UserSettings> = _settings.asStateFlow()

    // Loading state
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    /**
     * Start observing settings for a user.
     * If already observing the same user with an active job, does nothing (reuses existing listener).
     * Re-starts if the previous job was cancelled (e.g. after an error).
     */
    fun startObserving(userId: String) {
        if (currentUserId == userId && settingsJob?.isActive == true) {
            // Already observing this user with a live listener
            return
        }

        // Cancel previous listener if any
        settingsJob?.cancel()
        currentUserId = userId
        _isLoading.value = true

        settingsJob = scope.launch {
            settingsRepo.observeUserSettings(UserId(userId))
                .catch { e ->
                    _isLoading.value = false
                    // Keep previous settings on error
                }
                .collect { newSettings ->
                    _isLoading.value = false
                    _settings.value = newSettings
                }
        }
    }

    /**
     * Fetch settings once without starting a listener.
     * Useful for screens that don't need real-time updates.
     */
    suspend fun fetchOnce(userId: String): UserSettings {
        return settingsRepo.fetchUserSettings(UserId(userId))
    }

    /**
     * Stop observing settings (call when user logs out).
     */
    fun stopObserving() {
        settingsJob?.cancel()
        settingsJob = null
        currentUserId = null
        _settings.value = UserSettings()
    }

    /**
     * Manually update cached settings after a write operation.
     * This avoids waiting for the Firestore listener to update.
     */
    fun updateCache(settings: UserSettings) {
        _settings.value = settings
    }
}
