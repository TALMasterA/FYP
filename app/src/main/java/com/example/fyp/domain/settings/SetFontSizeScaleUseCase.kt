package com.example.fyp.domain.settings

import com.example.fyp.core.FontSizeUtils
import com.example.fyp.data.settings.UserSettingsRepository
import javax.inject.Inject

class SetFontSizeScaleUseCase @Inject constructor(
    private val settingsRepo: UserSettingsRepository
) {
    suspend operator fun invoke(userId: String, scale: Float) {
        val validatedScale = FontSizeUtils.validateScale(scale)
        settingsRepo.setFontSizeScale(userId, validatedScale)
    }
}