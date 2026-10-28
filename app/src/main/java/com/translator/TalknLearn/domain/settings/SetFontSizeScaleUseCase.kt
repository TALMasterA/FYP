package com.translator.TalknLearn.domain.settings

import com.translator.TalknLearn.core.validateScale
import com.translator.TalknLearn.data.settings.UserSettingsRepository
import com.translator.TalknLearn.model.UserId
import javax.inject.Inject

class SetFontSizeScaleUseCase @Inject constructor(
    private val settingsRepo: UserSettingsRepository
) {
    suspend operator fun invoke(userId: UserId, scale: Float) {
        val validatedScale = validateScale(scale)
        settingsRepo.setFontSizeScale(userId, validatedScale)
    }
}