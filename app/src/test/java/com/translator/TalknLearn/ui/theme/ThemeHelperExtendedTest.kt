package com.translator.TalknLearn.ui.theme

import com.translator.TalknLearn.model.user.UserSettings
import org.junit.Assert.*
import org.junit.Test

/**
 * Unit tests for ThemeHelper.
 *
 * Tests:
 * 1. Dark mode setting returns true
 * 2. Light mode setting returns false
 * 3. System mode follows system dark theme
 * 4. Auto theme enabled overrides manual setting
 */
class ThemeHelperExtendedTest {

    @Test
    fun `dark theme mode returns true regardless of system`() {
        val settings = UserSettings(themeMode = "dark")

        assertTrue(ThemeHelper.shouldUseDarkTheme(settings, systemDarkTheme = false))
        assertTrue(ThemeHelper.shouldUseDarkTheme(settings, systemDarkTheme = true))
    }

    @Test
    fun `light theme mode returns false regardless of system`() {
        val settings = UserSettings(themeMode = "light")

        assertFalse(ThemeHelper.shouldUseDarkTheme(settings, systemDarkTheme = false))
        assertFalse(ThemeHelper.shouldUseDarkTheme(settings, systemDarkTheme = true))
    }

    @Test
    fun `system theme mode follows system setting when dark`() {
        val settings = UserSettings(themeMode = "system")

        assertTrue(ThemeHelper.shouldUseDarkTheme(settings, systemDarkTheme = true))
    }

    @Test
    fun `system theme mode follows system setting when light`() {
        val settings = UserSettings(themeMode = "system")

        assertFalse(ThemeHelper.shouldUseDarkTheme(settings, systemDarkTheme = false))
    }

    @Test
    fun `auto theme enabled overrides manual theme mode`() {
        // When autoThemeEnabled is true, the manual themeMode is ignored
        val settingsDark = UserSettings(themeMode = "dark", autoThemeEnabled = true)
        val settingsLight = UserSettings(themeMode = "light", autoThemeEnabled = true)

        // Both should rely on time-based logic, not manual mode
        // We can't control the time in the test, but we verify it returns a boolean
        // and doesn't throw
        val resultDark = ThemeHelper.shouldUseDarkTheme(settingsDark, systemDarkTheme = false)
        val resultLight = ThemeHelper.shouldUseDarkTheme(settingsLight, systemDarkTheme = false)

        // Both should return the same value since both use time-based
        assertEquals(resultDark, resultLight)
    }

    @Test
    fun `default settings use system mode`() {
        val defaultSettings = UserSettings()

        assertEquals("system", defaultSettings.themeMode)
        assertFalse(defaultSettings.autoThemeEnabled)
    }
}
