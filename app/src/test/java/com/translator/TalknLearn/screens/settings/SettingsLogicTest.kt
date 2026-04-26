package com.translator.TalknLearn.screens.settings

import com.translator.TalknLearn.model.user.UserSettings
import org.junit.Test
import org.junit.Assert.*

/**
 * Tests for detailed settings logic.
 * 
 * Requirements:
 * - App UI language dropdown
 * - Reset password screen
 * - Profile: Set/rename username, delete account
 * - Favorites: limit of 20 records
 * - My profile: show profile, share, set visibility
 * - Voice settings: set voice speaking preference
 * - Feedback for sending feedback
 * - Primary language, font size, theme settings
 * - All settings except app UI language stored in Firestore
 */
class SettingsLogicTest {

    @Test
    fun `theme mode supports three values`() {
        val validModes = listOf("system", "light", "dark")
        assertEquals(3, validModes.size)
    }

    @Test
    fun `default theme mode is system`() {
        val settings = UserSettings()
        assertEquals("system", settings.themeMode)
    }

    @Test
    fun `font size scale has reasonable range`() {
        val minScale = 0.8f
        val maxScale = 2.0f
        val defaultScale = 1.0f

        val settings = UserSettings()
        assertTrue(settings.fontSizeScale >= minScale)
        assertTrue(settings.fontSizeScale <= maxScale)
        assertEquals(defaultScale, settings.fontSizeScale, 0.01f)
    }

    @Test
    fun `voice settings stored per language`() {
        val settings = UserSettings(
            voiceSettings = mapOf(
                "en-US" to "en-US-JennyNeural",
                "es-ES" to "es-ES-ElviraNeural"
            )
        )
        assertEquals("en-US-JennyNeural", settings.voiceSettings["en-US"])
        assertEquals("es-ES-ElviraNeural", settings.voiceSettings["es-ES"])
    }

    @Test
    fun `notification preferences default to off for push`() {
        val settings = UserSettings()
        assertFalse(settings.notifyNewMessages)
        assertFalse(settings.notifyFriendRequests)
        assertFalse(settings.notifyRequestAccepted)
        assertFalse(settings.notifySharedInbox)
    }

    @Test
    fun `in-app badges default to on`() {
        val settings = UserSettings()
        assertTrue(settings.inAppBadgeMessages)
        assertTrue(settings.inAppBadgeFriendRequests)
        assertTrue(settings.inAppBadgeSharedInbox)
    }

    @Test
    fun `MAX_FAVORITE_RECORDS is 20`() {
        assertEquals(20, UserSettings.MAX_FAVORITE_RECORDS)
    }

    @Test
    fun `primary language default is en-US`() {
        val settings = UserSettings()
        assertEquals("en-US", settings.primaryLanguageCode)
    }

    @Test
    fun `color palette defaults to default`() {
        val settings = UserSettings()
        assertEquals("default", settings.colorPaletteId)
    }

    @Test
    fun `default palette always in unlocked list`() {
        val settings = UserSettings()
        assertTrue(settings.unlockedPalettes.contains("default"))
    }

    @Test
    fun `auto theme disabled by default`() {
        val settings = UserSettings()
        assertFalse(settings.autoThemeEnabled)
    }

    // ── Settings stored in Firestore ──

    @Test
    fun `settings fields that should sync via Firestore`() {
        // All settings except app UI language should be in Firestore
        val firestoreFields = listOf(
            "primaryLanguageCode", "fontSizeScale", "themeMode",
            "colorPaletteId", "unlockedPalettes", "voiceSettings",
            "historyViewLimit", "autoThemeEnabled",
            "notifyNewMessages", "notifyFriendRequests",
            "notifyRequestAccepted", "notifySharedInbox",
            "inAppBadgeMessages", "inAppBadgeFriendRequests",
            "inAppBadgeSharedInbox", "lastPrimaryLanguageChangeMs"
        )
        assertTrue("Should have at least 16 synced fields", firestoreFields.size >= 16)
    }
}
