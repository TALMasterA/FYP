package com.example.fyp.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fyp.data.user.FirebaseAuthRepository
import com.example.fyp.data.learning.FirestoreQuizRepository
import com.example.fyp.data.settings.UserSettingsRepository
import com.example.fyp.model.user.AuthState
import com.example.fyp.model.user.UserSettings
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ShopUiState(
    val isLoading: Boolean = true,
    val coinBalance: Int = 0,
    val currentHistoryLimit: Int = UserSettings.BASE_HISTORY_LIMIT,
    val currentPaletteId: String = "default",
    val unlockedPalettes: List<String> = listOf("default"),
    val isPurchasing: Boolean = false,
    val purchaseError: String? = null,
    val purchaseSuccess: String? = null,
    val unlockError: String? = null
)

@HiltViewModel
class ShopViewModel @Inject constructor(
    private val authRepo: FirebaseAuthRepository,
    private val settingsRepo: UserSettingsRepository,
    private val quizRepo: FirestoreQuizRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ShopUiState())
    val uiState: StateFlow<ShopUiState> = _uiState.asStateFlow()

    private var currentUserId: String? = null

    init {
        viewModelScope.launch {
            authRepo.currentUserState.collect { auth ->
                when (auth) {
                    is AuthState.LoggedIn -> {
                        currentUserId = auth.user.uid
                        loadData(auth.user.uid)
                    }
                    AuthState.LoggedOut -> {
                        currentUserId = null
                        _uiState.value = ShopUiState(isLoading = false)
                    }
                    AuthState.Loading -> {
                        _uiState.value = ShopUiState(isLoading = true)
                    }
                }
            }
        }
    }

    private fun loadData(userId: String) {
        viewModelScope.launch {
            try {
                val settings = settingsRepo.fetchUserSettings(userId)
                val coinStats = quizRepo.fetchUserCoinStats(userId)

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    coinBalance = coinStats?.coinTotal ?: 0,
                    currentHistoryLimit = settings.historyViewLimit,
                    currentPaletteId = settings.colorPaletteId,
                    unlockedPalettes = settings.unlockedPalettes
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    purchaseError = e.message
                )
            }
        }
    }

    fun expandHistoryLimit() {
        val uid = currentUserId ?: return
        val current = _uiState.value

        if (current.currentHistoryLimit >= UserSettings.MAX_HISTORY_LIMIT) return
        if (current.coinBalance < UserSettings.HISTORY_EXPANSION_COST) {
            _uiState.value = current.copy(purchaseError = "Insufficient coins")
            return
        }

        _uiState.value = current.copy(
            isPurchasing = true,
            purchaseError = null,
            purchaseSuccess = null
        )

        viewModelScope.launch {
            try {
                val newLimit = (current.currentHistoryLimit + UserSettings.HISTORY_EXPANSION_INCREMENT)
                    .coerceAtMost(UserSettings.MAX_HISTORY_LIMIT)

                // Deduct coins
                quizRepo.deductCoins(uid, UserSettings.HISTORY_EXPANSION_COST)

                // Update history limit
                settingsRepo.expandHistoryViewLimit(uid, newLimit)

                // Refresh balance
                val newBalance = quizRepo.fetchUserCoinStats(uid)?.coinTotal ?: 0

                _uiState.value = _uiState.value.copy(
                    isPurchasing = false,
                    coinBalance = newBalance,
                    currentHistoryLimit = newLimit,
                    purchaseSuccess = "History limit expanded to $newLimit records!",
                    purchaseError = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isPurchasing = false,
                    purchaseError = e.message ?: "Purchase failed"
                )
            }
        }
    }

    fun selectPalette(paletteId: String) {
        val uid = currentUserId ?: return

        viewModelScope.launch {
            try {
                settingsRepo.setColorPalette(uid, paletteId)
                _uiState.value = _uiState.value.copy(currentPaletteId = paletteId)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(purchaseError = e.message)
            }
        }
    }

    fun unlockPalette(paletteId: String, cost: Int) {
        val uid = currentUserId ?: return
        val current = _uiState.value

        if (current.coinBalance < cost) {
            _uiState.value = current.copy(unlockError = "Insufficient coins")
            return
        }

        _uiState.value = current.copy(isPurchasing = true, unlockError = null)

        viewModelScope.launch {
            try {
                // Deduct coins
                quizRepo.deductCoins(uid, cost)

                // Unlock palette
                settingsRepo.unlockColorPalette(uid, paletteId)

                // Refresh balance
                val newBalance = quizRepo.fetchUserCoinStats(uid)?.coinTotal ?: 0

                _uiState.value = _uiState.value.copy(
                    isPurchasing = false,
                    coinBalance = newBalance,
                    unlockedPalettes = _uiState.value.unlockedPalettes + paletteId,
                    purchaseSuccess = "Palette unlocked!",
                    unlockError = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isPurchasing = false,
                    unlockError = e.message ?: "Failed to unlock palette"
                )
            }
        }
    }
}

