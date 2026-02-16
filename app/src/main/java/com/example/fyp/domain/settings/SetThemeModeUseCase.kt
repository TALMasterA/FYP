package com.example.fyp.domain.settings

import com.example.fyp.data.settings.UserSettingsRepository
import com.example.fyp.model.UserId
import jakarta.inject.Inject

class SetThemeModeUseCase @Inject constructor(
    private val repo: UserSettingsRepository
) {
    suspend operator fun invoke(uid: UserId, themeMode: String) {
        repo.setThemeMode(uid, themeMode)
    }
}