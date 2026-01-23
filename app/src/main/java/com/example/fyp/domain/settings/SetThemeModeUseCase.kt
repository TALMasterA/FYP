package com.example.fyp.domain.settings

import com.example.fyp.data.settings.UserSettingsRepository
import jakarta.inject.Inject

class SetThemeModeUseCase @Inject constructor(
    private val repo: UserSettingsRepository
) {
    suspend operator fun invoke(uid: String, themeMode: String) {
        repo.setThemeMode(uid, themeMode)
    }
}