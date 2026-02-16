package com.example.fyp.domain.settings

import com.example.fyp.core.validateScale
import com.example.fyp.data.settings.UserSettingsRepository
import com.example.fyp.model.UserId
import javax.inject.Inject

class SetFontSizeScaleUseCase @Inject constructor(
    private val settingsRepo: UserSettingsRepository
) {
    suspend operator fun invoke(userId: UserId, scale: Float) {
        val validatedScale = validateScale(scale)
        settingsRepo.setFontSizeScale(userId, validatedScale)
    }
}