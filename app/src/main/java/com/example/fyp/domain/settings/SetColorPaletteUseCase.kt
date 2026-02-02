package com.example.fyp.domain.settings

import com.example.fyp.data.settings.UserSettingsRepository
import javax.inject.Inject

class SetColorPaletteUseCase @Inject constructor(
    private val settingsRepo: UserSettingsRepository
) {
    suspend operator fun invoke(userId: String, paletteId: String) {
        settingsRepo.setColorPalette(userId, paletteId)
    }
}
