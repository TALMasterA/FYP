package com.translator.TalknLearn.domain.settings

import com.translator.TalknLearn.data.settings.UserSettingsRepository
import com.translator.TalknLearn.model.PaletteId
import com.translator.TalknLearn.model.UserId
import javax.inject.Inject

class SetColorPaletteUseCase @Inject constructor(
    private val settingsRepo: UserSettingsRepository
) {
    suspend operator fun invoke(userId: UserId, paletteId: PaletteId) {
        settingsRepo.setColorPalette(userId, paletteId)
    }
}
