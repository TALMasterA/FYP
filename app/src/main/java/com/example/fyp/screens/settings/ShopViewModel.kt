package com.example.fyp.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fyp.data.user.FirebaseAuthRepository
import com.example.fyp.data.cloud.CloudQuizClient
import com.example.fyp.domain.learning.QuizRepository
import com.example.fyp.data.settings.UserSettingsRepository
import com.example.fyp.data.settings.SharedSettingsDataSource
import com.example.fyp.model.user.AuthState
import com.example.fyp.model.user.UserSettings
import com.example.fyp.model.UserId
import com.example.fyp.model.PaletteId
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
    private val quizRepo: QuizRepository,
    private val cloudClient: CloudQuizClient,
    private val sharedSettings: SharedSettingsDataSource
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
                val settings = settingsRepo.fetchUserSettings(UserId(userId))
                val coinStats = quizRepo.fetchUserCoinStats(UserId(userId))

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
        currentUserId ?: return
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
                // Server-side: validates balance, deducts coins, and applies new limit atomically
                val result = cloudClient.spendCoinsForHistoryExpansion()
                if (!result.success) {
                    _uiState.value = _uiState.value.copy(
                        isPurchasing = false,
                        purchaseError = when (result.reason) {
                            "max_limit_reached" -> "Maximum limit reached!"
                            "insufficient_coins" -> "Insufficient coins"
                            else -> result.reason ?: "Purchase failed"
                        }
                    )
                    return@launch
                }

                _uiState.value = _uiState.value.copy(
                    isPurchasing = false,
                    coinBalance = result.newBalance,
                    currentHistoryLimit = result.newLimit ?: _uiState.value.currentHistoryLimit,
                    purchaseSuccess = "History limit expanded to ${result.newLimit} records!",
                    purchaseError = null
                )
                // Propagate new limit immediately so HistoryViewModel can show
                // the expanded record count without waiting for Firestore listener.
                val newLimit = result.newLimit ?: _uiState.value.currentHistoryLimit
                val current = sharedSettings.settings.value
                sharedSettings.updateCache(current.copy(historyViewLimit = newLimit))
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
                settingsRepo.setColorPalette(UserId(uid), PaletteId(paletteId))
                _uiState.value = _uiState.value.copy(currentPaletteId = paletteId)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(purchaseError = e.message)
            }
        }
    }

    fun unlockPalette(paletteId: String, cost: Int) {
        currentUserId ?: return
        val current = _uiState.value

        if (current.coinBalance < cost) {
            _uiState.value = current.copy(unlockError = "Insufficient coins")
            return
        }

        _uiState.value = current.copy(isPurchasing = true, unlockError = null)

        viewModelScope.launch {
            try {
                // Server-side: validates balance, palette ID, deducts coins, and unlocks atomically
                val result = cloudClient.spendCoinsForPaletteUnlock(paletteId)
                if (!result.success) {
                    _uiState.value = _uiState.value.copy(
                        isPurchasing = false,
                        unlockError = when (result.reason) {
                            "already_unlocked" -> "Palette already unlocked"
                            "insufficient_coins" -> "Insufficient coins"
                            else -> result.reason ?: "Failed to unlock palette"
                        }
                    )
                    return@launch
                }

                _uiState.value = _uiState.value.copy(
                    isPurchasing = false,
                    coinBalance = result.newBalance,
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

    fun clearPurchaseError() {
        _uiState.value = _uiState.value.copy(purchaseError = null)
    }

    fun clearUnlockError() {
        _uiState.value = _uiState.value.copy(unlockError = null)
    }

    fun clearPurchaseSuccess() {
        _uiState.value = _uiState.value.copy(purchaseSuccess = null)
    }
}
