package com.translator.TalknLearn.model.user

import org.junit.Assert.*
import org.junit.Test

/**
 * Extended unit tests for UserSettings.
 *
 * Tests:
 * 1. Default values are correct
 * 2. Notification defaults
 * 3. Badge defaults
 * 4. History limit constants
 * 5. Companion object constants
 */
class UserSettingsExtendedTest {

    // ── Default values ──────────────────────────────────────────────

    @Test
    fun `default primary language is en-US`() {
        val settings = UserSettings()
        assertEquals("en-US", settings.primaryLanguageCode)
    }

    @Test
    fun `default font size scale is 1_0`() {
        val settings = UserSettings()
        assertEquals(1.0f, settings.fontSizeScale)
    }

    @Test
    fun `default theme mode is system`() {
        val settings = UserSettings()
        assertEquals("system", settings.themeMode)
    }

    @Test
    fun `default color palette is default`() {
        val settings = UserSettings()
        assertEquals("default", settings.colorPaletteId)
    }

    @Test
    fun `default unlocked palettes contains only default`() {
        val settings = UserSettings()
        assertEquals(listOf("default"), settings.unlockedPalettes)
    }

    @Test
    fun `default voice settings are empty`() {
        val settings = UserSettings()
        assertTrue(settings.voiceSettings.isEmpty())
    }

    @Test
    fun `default auto theme is disabled`() {
        val settings = UserSettings()
        assertFalse(settings.autoThemeEnabled)
    }

    // ── Push notification defaults (all off) ────────────────────────

    @Test
    fun `push notifications default to off`() {
        val settings = UserSettings()
        assertFalse(settings.notifyNewMessages)
        assertFalse(settings.notifyFriendRequests)
        assertFalse(settings.notifyRequestAccepted)
        assertFalse(settings.notifySharedInbox)
    }

    // ── In-app badge defaults (all on) ──────────────────────────────

    @Test
    fun `in-app badges default to on`() {
        val settings = UserSettings()
        assertTrue(settings.inAppBadgeMessages)
        assertTrue(settings.inAppBadgeFriendRequests)
        assertTrue(settings.inAppBadgeSharedInbox)
    }

    // ── History limit constants ─────────────────────────────────────

    @Test
    fun `base history limit is 30`() {
        assertEquals(30, UserSettings.BASE_HISTORY_LIMIT)
    }

    @Test
    fun `max history limit is 60`() {
        assertEquals(60, UserSettings.MAX_HISTORY_LIMIT)
    }

    @Test
    fun `history expansion cost is 1000`() {
        assertEquals(1000, UserSettings.HISTORY_EXPANSION_COST)
    }

    @Test
    fun `history expansion increment is 10`() {
        assertEquals(10, UserSettings.HISTORY_EXPANSION_INCREMENT)
    }

    @Test
    fun `max favorite records is 20`() {
        assertEquals(20, UserSettings.MAX_FAVORITE_RECORDS)
    }

    // ── Primary language cooldown ───────────────────────────────────

    @Test
    fun `primary language cooldown is 30 days in ms`() {
        val thirtyDaysMs = 30L * 24 * 60 * 60 * 1000
        assertEquals(thirtyDaysMs, UserSettings.PRIMARY_LANGUAGE_CHANGE_COOLDOWN_MS)
    }

    @Test
    fun `default last primary language change is 0`() {
        val settings = UserSettings()
        assertEquals(0L, settings.lastPrimaryLanguageChangeMs)
    }

    @Test
    fun `default history view limit is 30`() {
        val settings = UserSettings()
        assertEquals(30, settings.historyViewLimit)
    }
}
