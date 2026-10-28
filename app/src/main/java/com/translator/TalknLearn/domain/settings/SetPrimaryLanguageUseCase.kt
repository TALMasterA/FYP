package com.translator.TalknLearn.domain.settings

import com.translator.TalknLearn.data.settings.FirestoreUserSettingsRepository
import com.translator.TalknLearn.model.LanguageCode
import com.translator.TalknLearn.model.UserId
import com.translator.TalknLearn.model.user.UserSettings
import javax.inject.Inject

class SetPrimaryLanguageUseCase @Inject constructor(
    private val repo: FirestoreUserSettingsRepository
) {
    sealed class Result {
        data object Success : Result()
        data class CooldownActive(val remainingDays: Int, val remainingHours: Int) : Result()
    }

    suspend operator fun invoke(uid: UserId, code: LanguageCode): Result {
        val settings = repo.fetchUserSettings(uid)

        // No cooldown check needed when the language is unchanged
        if (settings.primaryLanguageCode == code.value) return Result.Success

        val now = System.currentTimeMillis()
        if (!UserSettings.canChangePrimaryLanguage(settings.lastPrimaryLanguageChangeMs, now)) {
            val remainingMs = UserSettings.primaryLanguageCooldownRemainingMs(
                settings.lastPrimaryLanguageChangeMs, now
            )
            val totalHours = ((remainingMs + 3_599_999) / (60 * 60 * 1000)).toInt()
            val remainingDays = totalHours / 24
            val remainingHours = totalHours % 24
            return Result.CooldownActive(remainingDays = remainingDays, remainingHours = remainingHours)
        }

        repo.setPrimaryLanguage(uid, code)
        return Result.Success
    }
}
