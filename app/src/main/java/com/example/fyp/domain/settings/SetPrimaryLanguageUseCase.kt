package com.example.fyp.domain.settings

import com.example.fyp.data.settings.FirestoreUserSettingsRepository
import javax.inject.Inject

class SetPrimaryLanguageUseCase @Inject constructor(
    private val repo: FirestoreUserSettingsRepository
) {
    suspend operator fun invoke(uid: String, code: String) = repo.setPrimaryLanguage(uid, code)
}