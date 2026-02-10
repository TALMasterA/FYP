package com.example.fyp.domain.settings

import com.example.fyp.domain.learning.QuizRepository // Added import
import com.example.fyp.data.settings.UserSettingsRepository
import javax.inject.Inject

class UnlockColorPaletteWithCoinsUseCase @Inject constructor(
    private val settingsRepo: UserSettingsRepository,
    private val quizRepo: QuizRepository
) {
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

    sealed class Result {
        data object Success : Result()
        data object InsufficientCoins : Result()
    }
}
