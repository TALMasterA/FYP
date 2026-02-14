package com.example.fyp.domain.settings

import com.example.fyp.domain.learning.QuizRepository
import com.example.fyp.data.settings.UserSettingsRepository
import javax.inject.Inject

/**
 * Use case for unlocking color palettes using earned coins.
 *
 * This use case handles the transaction of:
 * 1. Checking if the user has sufficient coins
 * 2. Deducting the cost from the user's coin balance
 * 3. Unlocking the color palette if successful
 *
 * Free palettes (cost = 0) are unlocked without deducting coins.
 *
 * @property settingsRepo Repository for managing user settings
 * @property quizRepo Repository for managing quiz-related data including coins
 */
class UnlockColorPaletteWithCoinsUseCase @Inject constructor(
    private val settingsRepo: UserSettingsRepository,
    private val quizRepo: QuizRepository
) {
    /**
     * Attempts to unlock a color palette for the user.
     *
     * @param userId The ID of the user unlocking the palette
     * @param paletteId The ID of the palette to unlock
     * @param cost The cost in coins (0 for free palettes)
     * @return Result indicating success or insufficient coins
     */
    suspend operator fun invoke(userId: String, paletteId: String, cost: Int): Result {
        // Free palette (default), just unlock it
        if (cost == 0) {
            settingsRepo.unlockColorPalette(userId, paletteId)
            return Result.Success
        }

        // Deduct coins
        val newBalance = quizRepo.deductCoins(userId, cost)
        if (newBalance < 0) {
            return Result.InsufficientCoins
        }

        // Unlock the palette
        settingsRepo.unlockColorPalette(userId, paletteId)
        return Result.Success
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
