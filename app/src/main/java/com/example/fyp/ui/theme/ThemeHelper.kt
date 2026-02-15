package com.example.fyp.ui.theme

import com.example.fyp.model.user.UserSettings
import java.util.Calendar

/**
 * Helper functions for automatic theme switching based on time of day.
 * Simplified: Dark mode 6 PM - 6 AM, Light mode 6 AM - 6 PM
 */
object ThemeHelper {

    private const val DARK_MODE_START_HOUR = 18  // 6 PM
    private const val LIGHT_MODE_START_HOUR = 6   // 6 AM

    /**
     * Determine if dark theme should be active based on current time and user settings.
     *
     * @param settings User settings containing auto-theme preferences
     * @param systemDarkTheme Whether the system is in dark mode
     * @return true if dark theme should be active, false otherwise
     */
    fun shouldUseDarkTheme(settings: UserSettings, systemDarkTheme: Boolean): Boolean {
        return when {
            // If auto theme is enabled, use time-based switching (6 AM - 6 PM = light)
            settings.autoThemeEnabled -> {
                isCurrentlyNightTime()
            }
            // Otherwise use the theme mode setting
            settings.themeMode == "dark" -> true
            settings.themeMode == "light" -> false
            else -> systemDarkTheme // "system" mode
        }
    }

    /**
     * Check if current time is night time (6 PM - 6 AM).
     * Light mode: 6 AM - 6 PM
     * Dark mode: 6 PM - 6 AM
     */
    private fun isCurrentlyNightTime(): Boolean {
        val calendar = Calendar.getInstance()
        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)

        // Dark mode from 18:00 (6 PM) to 06:00 (6 AM)
        return currentHour >= DARK_MODE_START_HOUR || currentHour < LIGHT_MODE_START_HOUR
    }
}

