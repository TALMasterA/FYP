package com.translator.TalknLearn.domain.settings

import com.translator.TalknLearn.data.settings.UserSettingsRepository
import com.translator.TalknLearn.model.LanguageCode
import com.translator.TalknLearn.model.UserId
import com.translator.TalknLearn.model.VoiceName
import javax.inject.Inject

class SetVoiceForLanguageUseCase @Inject constructor(
    private val settingsRepo: UserSettingsRepository
) {
    suspend operator fun invoke(userId: UserId, languageCode: LanguageCode, voiceName: VoiceName) {
        settingsRepo.setVoiceForLanguage(userId, languageCode, voiceName)
    }
}
