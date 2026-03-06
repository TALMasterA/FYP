package com.example.fyp.domain.settings

import com.example.fyp.data.settings.FirestoreUserSettingsRepository
import com.example.fyp.model.LanguageCode
import com.example.fyp.model.UserId
import com.example.fyp.model.user.UserSettings
import javax.inject.Inject

class SetPrimaryLanguageUseCase @Inject constructor(
    private val repo: FirestoreUserSettingsRepository
) {
    sealed class Result {
        data object Success : Result()
        data class CooldownActive(val remainingDays: Int) : Result()
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
            val remainingDays = ((remainingMs / (24 * 60 * 60 * 1000)) + 1).toInt()
            return Result.CooldownActive(remainingDays = remainingDays)
        }

        repo.setPrimaryLanguage(uid, code)
        return Result.Success
    }
}
