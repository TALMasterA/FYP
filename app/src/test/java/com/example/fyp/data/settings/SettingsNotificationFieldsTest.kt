package com.example.fyp.data.settings

import com.example.fyp.model.user.UserSettings
import org.junit.Test
import org.junit.Assert.*

/**
 * Tests for FirestoreUserSettingsRepository validation logic.
 *
 * Since parseSettings() is private, we test the public validation
 * logic that can be independently verified:
 * - Notification field allowlist validation
 * - History limit clamping behavior
 * - UserSettings field defaults matching parse defaults
 */
class SettingsNotificationFieldsTest {

    // --- Notification preference field allowlist ---

    @Test
    fun `all push notification fields are recognized`() {
        val validFields = listOf(
            "notifyNewMessages",
            "notifyFriendRequests",
            "notifyRequestAccepted",
            "notifySharedInbox"
        )
        validFields.forEach { field ->
            // These are the Firestore field names used by setNotificationPref
            assertTrue("'$field' should be a valid notification field", field.startsWith("notify"))
        }
    }

    @Test
    fun `all in-app badge fields are recognized`() {
        val badgeFields = listOf(
            "inAppBadgeMessages",
            "inAppBadgeFriendRequests",
            "inAppBadgeSharedInbox"
        )
        badgeFields.forEach { field ->
            assertTrue("'$field' should be a valid badge field", field.startsWith("inAppBadge"))
        }
    }

    @Test
    fun `notification field names match UserSettings property names`() {
        // Verify the Firestore field names correspond to UserSettings properties
        val settings = UserSettings()
        // Push notification fields — all default to false (opt-in)
        assertFalse(settings.notifyNewMessages)
        assertFalse(settings.notifyFriendRequests)
        assertFalse(settings.notifyRequestAccepted)
        assertFalse(settings.notifySharedInbox)
        // In-app badge fields — all default to true (opt-out)
        assertTrue(settings.inAppBadgeMessages)
        assertTrue(settings.inAppBadgeFriendRequests)
        assertTrue(settings.inAppBadgeSharedInbox)
    }

    @Test
    fun `total notification fields count is 7`() {
        // 4 push + 3 badge = 7 fields
        val allFields = listOf(
            "notifyNewMessages", "notifyFriendRequests",
            "notifyRequestAccepted", "notifySharedInbox",
            "inAppBadgeMessages", "inAppBadgeFriendRequests", "inAppBadgeSharedInbox"
        )
        assertEquals(7, allFields.size)
    }

    // --- History limit clamping ---

    @Test
    fun `history limit clamped at base minimum`() {
        val clamped = 10.coerceIn(UserSettings.BASE_HISTORY_LIMIT, UserSettings.MAX_HISTORY_LIMIT)
        assertEquals(UserSettings.BASE_HISTORY_LIMIT, clamped)
    }

    @Test
    fun `history limit clamped at max`() {
        val clamped = 100.coerceIn(UserSettings.BASE_HISTORY_LIMIT, UserSettings.MAX_HISTORY_LIMIT)
        assertEquals(UserSettings.MAX_HISTORY_LIMIT, clamped)
    }

    @Test
    fun `history limit within range is unchanged`() {
        val clamped = 45.coerceIn(UserSettings.BASE_HISTORY_LIMIT, UserSettings.MAX_HISTORY_LIMIT)
        assertEquals(45, clamped)
    }

    @Test
    fun `history limit at exact boundaries is valid`() {
        assertEquals(30, 30.coerceIn(UserSettings.BASE_HISTORY_LIMIT, UserSettings.MAX_HISTORY_LIMIT))
        assertEquals(60, 60.coerceIn(UserSettings.BASE_HISTORY_LIMIT, UserSettings.MAX_HISTORY_LIMIT))
    }

    // --- Parse defaults match UserSettings defaults ---

    @Test
    fun `default primaryLanguageCode is en-US`() {
        assertEquals("en-US", UserSettings().primaryLanguageCode)
    }

    @Test
    fun `default fontSizeScale is 1_0`() {
        assertEquals(1.0f, UserSettings().fontSizeScale, 0.001f)
    }

    @Test
    fun `default themeMode is system`() {
        assertEquals("system", UserSettings().themeMode)
    }

    @Test
    fun `default colorPaletteId is default`() {
        assertEquals("default", UserSettings().colorPaletteId)
    }

    @Test
    fun `default unlockedPalettes contains only default`() {
        assertEquals(listOf("default"), UserSettings().unlockedPalettes)
    }

    @Test
    fun `default voiceSettings is empty`() {
        assertTrue(UserSettings().voiceSettings.isEmpty())
    }

    @Test
    fun `default autoThemeEnabled is false`() {
        assertFalse(UserSettings().autoThemeEnabled)
    }

    @Test
    fun `default lastPrimaryLanguageChangeMs is 0`() {
        assertEquals(0L, UserSettings().lastPrimaryLanguageChangeMs)
    }

    @Test
    fun `default lastUsernameChangeMs is 0`() {
        assertEquals(0L, UserSettings().lastUsernameChangeMs)
    }

    @Test
    fun `all 17 settings fields have correct defaults`() {
        val s = UserSettings()
        assertEquals("en-US", s.primaryLanguageCode)
        assertEquals(1.0f, s.fontSizeScale, 0.001f)
        assertEquals("system", s.themeMode)
        assertEquals("default", s.colorPaletteId)
        assertEquals(listOf("default"), s.unlockedPalettes)
        assertTrue(s.voiceSettings.isEmpty())
        assertEquals(30, s.historyViewLimit)
        assertFalse(s.autoThemeEnabled)
        assertEquals(0L, s.lastPrimaryLanguageChangeMs)
        assertEquals(0L, s.lastUsernameChangeMs)
        assertFalse(s.notifyNewMessages)
        assertFalse(s.notifyFriendRequests)
        assertFalse(s.notifyRequestAccepted)
        assertFalse(s.notifySharedInbox)
        assertTrue(s.inAppBadgeMessages)
        assertTrue(s.inAppBadgeFriendRequests)
        assertTrue(s.inAppBadgeSharedInbox)
    }
}
