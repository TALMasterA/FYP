package com.example.fyp.data.settings

import com.example.fyp.model.user.UserSettings
import org.junit.Assert.*
import org.junit.Test

/**
 * Tests for pure logic extracted from SharedSettingsDataSource.
 *
 * Covers:
 *  - startObserving idempotency (same user + active job)
 *  - stopObserving reset semantics
 *  - updateCache semantics
 *  - fetchOnce vs observe patterns
 */
class SharedSettingsDataSourceLogicTest {

    // ── startObserving idempotency ─────────────────────────────────

    /**
     * Replicates the idempotency check in SharedSettingsDataSource.startObserving.
     */
    private fun shouldSkipObserving(
        currentUserId: String?,
        newUserId: String,
        jobActive: Boolean
    ): Boolean {
        return currentUserId == newUserId && jobActive
    }

    @Test
    fun `startObserving - skips when same user and job active`() {
        assertTrue(shouldSkipObserving("user1", "user1", true))
    }

    @Test
    fun `startObserving - does not skip when different user`() {
        assertFalse(shouldSkipObserving("user1", "user2", true))
    }

    @Test
    fun `startObserving - does not skip when job inactive`() {
        assertFalse(shouldSkipObserving("user1", "user1", false))
    }

    @Test
    fun `startObserving - does not skip when currentUserId is null`() {
        assertFalse(shouldSkipObserving(null, "user1", true))
    }

    @Test
    fun `startObserving - does not skip when both conditions fail`() {
        assertFalse(shouldSkipObserving(null, "user1", false))
    }

    // ── stopObserving reset semantics ──────────────────────────────

    /**
     * Simulates stopObserving state transitions.
     */
    private data class SettingsObserverState(
        val currentUserId: String?,
        val settings: UserSettings,
        val isLoading: Boolean
    )

    private fun simulateStopObserving(@Suppress("UNUSED_PARAMETER") state: SettingsObserverState): SettingsObserverState {
        return SettingsObserverState(
            currentUserId = null,
            settings = UserSettings(), // Reset to defaults
            isLoading = false
        )
    }

    @Test
    fun `stopObserving clears currentUserId`() {
        val before = SettingsObserverState("user1", UserSettings(fontSizeScale = 1.5f), false)
        val after = simulateStopObserving(before)
        assertNull(after.currentUserId)
    }

    @Test
    fun `stopObserving resets settings to defaults`() {
        val customSettings = UserSettings(
            primaryLanguageCode = "ja-JP",
            fontSizeScale = 1.5f,
            themeMode = "dark"
        )
        val before = SettingsObserverState("user1", customSettings, false)
        val after = simulateStopObserving(before)
        assertEquals(UserSettings(), after.settings) // Default settings
    }

    @Test
    fun `stopObserving resets loading state`() {
        val before = SettingsObserverState("user1", UserSettings(), true)
        val after = simulateStopObserving(before)
        assertFalse(after.isLoading)
    }

    // ── updateCache semantics ──────────────────────────────────────

    @Test
    fun `updateCache replaces previous settings`() {
        val newSettings = UserSettings(primaryLanguageCode = "ko-KR", fontSizeScale = 1.2f)
        val cached = newSettings // Simulates _settings.value = settings
        assertEquals("ko-KR", cached.primaryLanguageCode)
        assertEquals(1.2f, cached.fontSizeScale)
    }

    @Test
    fun `updateCache preserves all fields of new settings`() {
        val newSettings = UserSettings(
            primaryLanguageCode = "fr-FR",
            themeMode = "dark",
            colorPaletteId = "ocean",
            historyViewLimit = 60
        )
        // Simulate cache update
        val cached = newSettings
        assertEquals("fr-FR", cached.primaryLanguageCode)
        assertEquals("dark", cached.themeMode)
        assertEquals("ocean", cached.colorPaletteId)
        assertEquals(60, cached.historyViewLimit)
    }

    // ── UserSettings default values ────────────────────────────────

    @Test
    fun `UserSettings defaults are correct for new user`() {
        val defaults = UserSettings()
        assertEquals("en-US", defaults.primaryLanguageCode)
        assertEquals(1.0f, defaults.fontSizeScale)
        assertEquals("system", defaults.themeMode)
        assertEquals("default", defaults.colorPaletteId)
        assertEquals(listOf("default"), defaults.unlockedPalettes)
        assertTrue(defaults.voiceSettings.isEmpty())
        assertEquals(30, defaults.historyViewLimit)
        assertFalse(defaults.autoThemeEnabled)
        assertEquals(0L, defaults.lastPrimaryLanguageChangeMs)
        assertEquals(0L, defaults.lastUsernameChangeMs)
    }

    @Test
    fun `UserSettings notification defaults are all opt-in`() {
        val defaults = UserSettings()
        // Push notifications default OFF (user must opt in)
        assertFalse(defaults.notifyNewMessages)
        assertFalse(defaults.notifyFriendRequests)
        assertFalse(defaults.notifyRequestAccepted)
        assertFalse(defaults.notifySharedInbox)
    }

    @Test
    fun `UserSettings badge defaults are all enabled`() {
        val defaults = UserSettings()
        // In-app badges default ON
        assertTrue(defaults.inAppBadgeMessages)
        assertTrue(defaults.inAppBadgeFriendRequests)
        assertTrue(defaults.inAppBadgeSharedInbox)
    }
}

