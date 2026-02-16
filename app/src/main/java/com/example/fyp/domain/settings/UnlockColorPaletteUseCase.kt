package com.example.fyp.domain.settings

import com.example.fyp.data.settings.UserSettingsRepository
import com.example.fyp.model.PaletteId
import com.example.fyp.model.UserId
import javax.inject.Inject

class UnlockColorPaletteUseCase @Inject constructor(
    private val settingsRepo: UserSettingsRepository
) {
    suspend operator fun invoke(userId: UserId, paletteId: PaletteId) {
        settingsRepo.unlockColorPalette(userId, paletteId)
    }
}
