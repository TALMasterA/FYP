package com.example.fyp.model.user

import kotlinx.serialization.Serializable

@Serializable
data class UserSettings(
    val primaryLanguageCode: String = "en-US",
    val fontSizeScale: Float = 1.0f,
    val themeMode: String = "system", // "system" | "light" | "dark"
    val colorPaletteId: String = "default", // Color palette selection
    val unlockedPalettes: List<String> = listOf("default"), // List of unlocked color palettes
    val voiceSettings: Map<String, String> = emptyMap(), // language code -> voice name
    val historyViewLimit: Int = 100, // Default 100 records displayed, expandable to 150
) {
    companion object {
        const val BASE_HISTORY_LIMIT = 100
        const val MAX_HISTORY_LIMIT = 150
        const val HISTORY_EXPANSION_COST = 1000
        const val HISTORY_EXPANSION_INCREMENT = 10
    }
}