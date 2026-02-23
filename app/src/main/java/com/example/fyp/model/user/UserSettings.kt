package com.example.fyp.model.user

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

@Serializable
@Immutable
data class UserSettings(
    val primaryLanguageCode: String = "en-US",
    val fontSizeScale: Float = 1.0f,
    val themeMode: String = "system", // "system" | "light" | "dark"
    val colorPaletteId: String = "default", // Color palette selection
    val unlockedPalettes: List<String> = listOf("default"), // List of unlocked color palettes
    val voiceSettings: Map<String, String> = emptyMap(), // language code -> voice name
    val historyViewLimit: Int = 50, // Default 50 records displayed, expandable to 100
    val autoThemeEnabled: Boolean = false, // Enable time-based theme switching (6 AM - 6 PM light, 6 PM - 6 AM dark)
    // --- Push notification toggles (all enabled by default) ---
    val notifyNewMessages: Boolean = true,       // Chat message notifications
    val notifyFriendRequests: Boolean = true,    // Incoming friend request notifications
    val notifyRequestAccepted: Boolean = true,   // "Your request was accepted" notifications
    val notifySharedInbox: Boolean = true,       // Shared-inbox item notifications
) {
    companion object {
        const val BASE_HISTORY_LIMIT = 50
        const val MAX_HISTORY_LIMIT = 100
        const val HISTORY_EXPANSION_COST = 1000
        const val HISTORY_EXPANSION_INCREMENT = 10
    }
}