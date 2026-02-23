package com.example.fyp.data.settings

import com.example.fyp.model.LanguageCode
import com.example.fyp.model.PaletteId
import com.example.fyp.model.UserId
import com.example.fyp.model.VoiceName
import com.example.fyp.model.user.UserSettings
import kotlinx.coroutines.flow.Flow

interface UserSettingsRepository {
    fun observeUserSettings(userId: UserId): Flow<UserSettings>

    /**
     * Fetch user settings once (on-demand).
     * More efficient than observeUserSettings when real-time updates aren't needed.
     */
    suspend fun fetchUserSettings(userId: UserId): UserSettings
    suspend fun setFontSizeScale(userId: UserId, scale: Float)
    suspend fun setPrimaryLanguage(userId: UserId, languageCode: LanguageCode)
    suspend fun setThemeMode(userId: UserId, themeMode: String)
    suspend fun setColorPalette(userId: UserId, paletteId: PaletteId)
    suspend fun unlockColorPalette(userId: UserId, paletteId: PaletteId)
    suspend fun setVoiceForLanguage(userId: UserId, languageCode: LanguageCode, voiceName: VoiceName)

    /**
     * Enable or disable automatic theme switching based on time (6 AM - 6 PM light, 6 PM - 6 AM dark).
     */
    suspend fun setAutoThemeEnabled(userId: UserId, enabled: Boolean)

    /**
     * Update a single push-notification toggle for the user.
     *
     * @param field  One of "notifyNewMessages", "notifyFriendRequests",
     *               "notifyRequestAccepted", "notifySharedInbox"
     * @param enabled  Whether the notification type should be delivered.
     */
    suspend fun setNotificationPref(userId: UserId, field: String, enabled: Boolean)

    /**
     * Expand history view limit by increment (costs coins).
     */
    suspend fun expandHistoryViewLimit(userId: UserId, newLimit: Int)
}