package com.example.fyp.data.settings

import com.example.fyp.model.user.UserSettings
import kotlinx.coroutines.flow.Flow

interface UserSettingsRepository {
    fun observeUserSettings(userId: String): Flow<UserSettings>
    suspend fun setFontSizeScale(userId: String, scale: Float)
    suspend fun setPrimaryLanguage(userId: String, languageCode: String)
    suspend fun setThemeMode(userId: String, themeMode: String)
    suspend fun setColorPalette(userId: String, paletteId: String)
    suspend fun unlockColorPalette(userId: String, paletteId: String)
    suspend fun setVoiceForLanguage(userId: String, languageCode: String, voiceName: String)
}