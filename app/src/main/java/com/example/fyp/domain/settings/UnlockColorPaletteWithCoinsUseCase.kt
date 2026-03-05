package com.example.fyp.domain.settings

import com.example.fyp.data.cloud.CloudQuizClient
import com.example.fyp.data.settings.UserSettingsRepository
import com.example.fyp.model.PaletteId
import com.example.fyp.model.UserId
import javax.inject.Inject

/**
 * Use case for unlocking color palettes using earned coins.
 *
 * Delegates to the server-side spendCoins Cloud Function which validates
 * balance, deducts coins, and unlocks the palette atomically in a
 * Firestore transaction. This prevents client-side tampering.
 *
 * Free palettes (cost = 0) are unlocked directly without the cloud function.
 *
 * @property settingsRepo Repository for managing user settings (free palette unlock only)
 * @property cloudClient Cloud Functions client for server-verified spending
 */
class UnlockColorPaletteWithCoinsUseCase @Inject constructor(
    private val settingsRepo: UserSettingsRepository,
    private val cloudClient: CloudQuizClient
) {
    /**
     * Attempts to unlock a color palette for the user.
     *
     * @param userId The ID of the user unlocking the palette
     * @param paletteId The ID of the palette to unlock
     * @param cost The cost in coins (0 for free palettes)
     * @return Result indicating success or insufficient coins
     */
    suspend operator fun invoke(userId: UserId, paletteId: PaletteId, cost: Int): Result {
        // Free palette (default), just unlock it directly
        if (cost == 0) {
            settingsRepo.unlockColorPalette(userId, paletteId)
            return Result.Success
        }

        // Server-side: validates balance, deducts coins, and unlocks atomically
        val result = cloudClient.spendCoinsForPaletteUnlock(paletteId.value)
        return if (result.success) {
            Result.Success
        } else {
            when (result.reason) {
                "insufficient_coins" -> Result.InsufficientCoins
                "already_unlocked" -> Result.Success // Already unlocked is fine
                else -> Result.InsufficientCoins
            }
        }
    }

    /**
     * Result of the unlock operation.
     */
    sealed class Result {
        /** The palette was successfully unlocked */
        data object Success : Result()

        /** The user does not have enough coins */
        data object InsufficientCoins : Result()
    }
}
