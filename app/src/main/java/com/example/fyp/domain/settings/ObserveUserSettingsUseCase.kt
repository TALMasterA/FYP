package com.example.fyp.domain.settings

import com.example.fyp.data.settings.FirestoreUserSettingsRepository
import com.example.fyp.model.UserSettings
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveUserSettingsUseCase @Inject constructor(
    private val repo: FirestoreUserSettingsRepository
) {
    operator fun invoke(uid: String): Flow<UserSettings> = repo.observeUserSettings(uid)
}