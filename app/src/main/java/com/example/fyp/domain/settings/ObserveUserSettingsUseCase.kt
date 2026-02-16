package com.example.fyp.domain.settings

import com.example.fyp.data.settings.FirestoreUserSettingsRepository
import com.example.fyp.model.UserId
import com.example.fyp.model.user.UserSettings
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveUserSettingsUseCase @Inject constructor(
    private val repo: FirestoreUserSettingsRepository
) {
    operator fun invoke(uid: UserId): Flow<UserSettings> = repo.observeUserSettings(uid)
}