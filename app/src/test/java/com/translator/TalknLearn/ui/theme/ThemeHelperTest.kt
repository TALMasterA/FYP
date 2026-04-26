package com.translator.TalknLearn.ui.theme

import com.translator.TalknLearn.model.user.UserSettings
import org.junit.Assert.*
import org.junit.Test

class ThemeHelperTest {

    // ── Explicit dark/light mode (autoThemeEnabled = false) ─────────

    @Test
    fun `shouldUseDarkTheme - themeMode dark returns true`() {
        val settings = UserSettings(themeMode = "dark", autoThemeEnabled = false)
        assertTrue(ThemeHelper.shouldUseDarkTheme(settings, systemDarkTheme = false))
    }

    @Test
    fun `shouldUseDarkTheme - themeMode dark ignores system theme`() {
        val settings = UserSettings(themeMode = "dark", autoThemeEnabled = false)
        assertTrue(ThemeHelper.shouldUseDarkTheme(settings, systemDarkTheme = false))
        assertTrue(ThemeHelper.shouldUseDarkTheme(settings, systemDarkTheme = true))
    }

    @Test
    fun `shouldUseDarkTheme - themeMode light returns false`() {
        val settings = UserSettings(themeMode = "light", autoThemeEnabled = false)
        assertFalse(ThemeHelper.shouldUseDarkTheme(settings, systemDarkTheme = true))
    }

    @Test
    fun `shouldUseDarkTheme - themeMode light ignores system theme`() {
        val settings = UserSettings(themeMode = "light", autoThemeEnabled = false)
        assertFalse(ThemeHelper.shouldUseDarkTheme(settings, systemDarkTheme = false))
        assertFalse(ThemeHelper.shouldUseDarkTheme(settings, systemDarkTheme = true))
    }

    // ── System mode (autoThemeEnabled = false, themeMode = system) ──

    @Test
    fun `shouldUseDarkTheme - system mode follows system dark theme true`() {
        val settings = UserSettings(themeMode = "system", autoThemeEnabled = false)
        assertTrue(ThemeHelper.shouldUseDarkTheme(settings, systemDarkTheme = true))
    }

    @Test
    fun `shouldUseDarkTheme - system mode follows system dark theme false`() {
        val settings = UserSettings(themeMode = "system", autoThemeEnabled = false)
        assertFalse(ThemeHelper.shouldUseDarkTheme(settings, systemDarkTheme = false))
    }

    @Test
    fun `shouldUseDarkTheme - default settings use system mode`() {
        // Default: themeMode = "system", autoThemeEnabled = false
        val settings = UserSettings()
        // Should follow systemDarkTheme
        assertEquals(true, ThemeHelper.shouldUseDarkTheme(settings, systemDarkTheme = true))
        assertEquals(false, ThemeHelper.shouldUseDarkTheme(settings, systemDarkTheme = false))
    }

    // ── Auto-theme mode ─────────────────────────────────────────────

    @Test
    fun `shouldUseDarkTheme - autoThemeEnabled returns time-based result`() {
        // When autoThemeEnabled is true, the result depends on time of day.
        // We can't control the system clock easily, but we can at least verify
        // it returns a boolean without crashing and ignores themeMode.
        val settingsDark = UserSettings(themeMode = "dark", autoThemeEnabled = true)
        val settingsLight = UserSettings(themeMode = "light", autoThemeEnabled = true)

        // Both should return the same value since auto overrides themeMode
        val resultDarkMode = ThemeHelper.shouldUseDarkTheme(settingsDark, systemDarkTheme = false)
        val resultLightMode = ThemeHelper.shouldUseDarkTheme(settingsLight, systemDarkTheme = false)
        assertEquals(
            "Auto theme should ignore themeMode setting",
            resultDarkMode,
            resultLightMode
        )
    }

    @Test
    fun `shouldUseDarkTheme - autoThemeEnabled ignores system dark theme`() {
        val settings = UserSettings(autoThemeEnabled = true)
        val resultWithSystemDark = ThemeHelper.shouldUseDarkTheme(settings, systemDarkTheme = true)
        val resultWithSystemLight = ThemeHelper.shouldUseDarkTheme(settings, systemDarkTheme = false)
        assertEquals(
            "Auto theme should ignore systemDarkTheme",
            resultWithSystemDark,
            resultWithSystemLight
        )
    }

    // ── Unknown theme mode ──────────────────────────────────────────

    @Test
    fun `shouldUseDarkTheme - unknown themeMode falls back to system`() {
        val settings = UserSettings(themeMode = "unknown_value", autoThemeEnabled = false)
        // Should fall through to else -> systemDarkTheme
        assertTrue(ThemeHelper.shouldUseDarkTheme(settings, systemDarkTheme = true))
        assertFalse(ThemeHelper.shouldUseDarkTheme(settings, systemDarkTheme = false))
    }

    @Test
    fun `shouldUseDarkTheme - empty themeMode falls back to system`() {
        val settings = UserSettings(themeMode = "", autoThemeEnabled = false)
        assertTrue(ThemeHelper.shouldUseDarkTheme(settings, systemDarkTheme = true))
        assertFalse(ThemeHelper.shouldUseDarkTheme(settings, systemDarkTheme = false))
    }
}
