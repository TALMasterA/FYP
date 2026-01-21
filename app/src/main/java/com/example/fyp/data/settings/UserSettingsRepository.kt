package com.example.fyp.data.settings

import com.example.fyp.model.UserSettings
import kotlinx.coroutines.flow.Flow

interface UserSettingsRepository {
    fun observeUserSettings(userId: String): Flow<UserSettings>
    suspend fun setFontSizeScale(userId: String, scale: Float)
    suspend fun setPrimaryLanguage(userId: String, languageCode: String)
}