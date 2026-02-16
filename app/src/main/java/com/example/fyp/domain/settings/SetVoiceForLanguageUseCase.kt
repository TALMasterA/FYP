package com.example.fyp.domain.settings

import com.example.fyp.data.settings.UserSettingsRepository
import com.example.fyp.model.LanguageCode
import com.example.fyp.model.UserId
import com.example.fyp.model.VoiceName
import javax.inject.Inject

class SetVoiceForLanguageUseCase @Inject constructor(
    private val settingsRepo: UserSettingsRepository
) {
    suspend operator fun invoke(userId: UserId, languageCode: LanguageCode, voiceName: VoiceName) {
        settingsRepo.setVoiceForLanguage(userId, languageCode, voiceName)
    }
}
