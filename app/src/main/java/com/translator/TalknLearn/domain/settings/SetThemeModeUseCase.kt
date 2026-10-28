package com.translator.TalknLearn.domain.settings

import com.translator.TalknLearn.data.settings.UserSettingsRepository
import com.translator.TalknLearn.model.UserId
import javax.inject.Inject

class SetThemeModeUseCase @Inject constructor(
    private val repo: UserSettingsRepository
) {
    suspend operator fun invoke(uid: UserId, themeMode: String) {
        repo.setThemeMode(uid, themeMode)
    }
}