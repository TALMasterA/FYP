package com.example.fyp.domain.settings

import com.example.fyp.data.settings.FirestoreUserSettingsRepository
import com.example.fyp.model.LanguageCode
import com.example.fyp.model.UserId
import javax.inject.Inject

class SetPrimaryLanguageUseCase @Inject constructor(
    private val repo: FirestoreUserSettingsRepository
) {
    suspend operator fun invoke(uid: UserId, code: LanguageCode) = repo.setPrimaryLanguage(uid, code)
}