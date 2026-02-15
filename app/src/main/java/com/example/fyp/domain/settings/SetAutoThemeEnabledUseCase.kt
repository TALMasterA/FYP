package com.example.fyp.domain.settings

import com.example.fyp.data.settings.UserSettingsRepository
import javax.inject.Inject

class SetAutoThemeEnabledUseCase @Inject constructor(
    private val repo: UserSettingsRepository
) {
    suspend operator fun invoke(uid: String, enabled: Boolean) {
        repo.setAutoThemeEnabled(uid, enabled)
    }
}

