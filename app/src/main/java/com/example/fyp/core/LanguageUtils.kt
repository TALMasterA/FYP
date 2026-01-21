package com.example.fyp.core

import android.content.Context
import com.example.fyp.data.config.AzureLanguageConfig

/**
 * Centralized utilities for language handling across the app.
 * Eliminates code duplication in multiple screens.
 */
object LanguageUtils {

    /**
     * Load supported languages from Azure config.
     * Used by: LearningScreen, HistoryScreen, SettingsScreen
     */
    fun loadSupportedLanguages(context: Context): List<String> {
        return AzureLanguageConfig.loadSupportedLanguages(context)
    }

    /**
     * Get display name for a language code.
     * Example: "en-US" → "English (US)"
     * Used by: LearningScreen, HistoryScreen, SettingsScreen
     */
    fun getDisplayName(
        languageCode: String,
        languageNameMap: Map<String, String>
    ): String {
        return languageNameMap[languageCode] ?: languageCode
    }

    /**
     * Filter languages by prefix (e.g., "en" for all English variants).
     * Example: ["en-US", "en-GB", "zh-CN"] with prefix "en" → ["en-US", "en-GB"]
     */
    fun filterByPrefix(
        languages: List<String>,
        prefix: String
    ): List<String> {
        return languages.filter { it.startsWith(prefix, ignoreCase = true) }
    }

    /**
     * Sort languages with primary language first, then rest alphabetically.
     * Example: primaryLanguageCode="zh-CN", languages=["en-US", "zh-CN", "ja-JP"]
     * Result: ["zh-CN", "en-US", "ja-JP"]
     */
    fun sortWithPrimaryFirst(
        languages: List<String>,
        primaryLanguageCode: String
    ): List<String> {
        return languages.sortedWith(
            compareBy<String> { it != primaryLanguageCode }
                .thenBy { it }
        )
    }

    /**
     * Validate if a language code is in the supported list.
     */
    fun isSupported(
        languageCode: String,
        supportedLanguages: List<String>
    ): Boolean {
        return supportedLanguages.contains(languageCode)
    }
}