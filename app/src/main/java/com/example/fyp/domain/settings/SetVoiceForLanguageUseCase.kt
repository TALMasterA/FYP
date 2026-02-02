package com.example.fyp.domain.settings

import com.example.fyp.data.settings.UserSettingsRepository
import javax.inject.Inject

class SetVoiceForLanguageUseCase @Inject constructor(
    private val settingsRepo: UserSettingsRepository
) {
    suspend operator fun invoke(userId: String, languageCode: String, voiceName: String) {
        settingsRepo.setVoiceForLanguage(userId, languageCode, voiceName)
    }
}
