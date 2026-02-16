package com.example.fyp.domain.settings

import com.example.fyp.data.settings.UserSettingsRepository
import com.example.fyp.model.UserId
import javax.inject.Inject

class SetAutoThemeEnabledUseCase @Inject constructor(
    private val repo: UserSettingsRepository
) {
    suspend operator fun invoke(uid: UserId, enabled: Boolean) {
        repo.setAutoThemeEnabled(uid, enabled)
    }
}

