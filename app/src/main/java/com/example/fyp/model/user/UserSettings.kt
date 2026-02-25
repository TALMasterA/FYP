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
    // --- Push notification toggles (all off by default — user opts in) ---
    val notifyNewMessages: Boolean = false,      // Chat message notifications (default off)
    val notifyFriendRequests: Boolean = false,   // Incoming friend request notifications
    val notifyRequestAccepted: Boolean = false,  // "Your request was accepted" notifications
    val notifySharedInbox: Boolean = false,      // Shared-inbox item notifications
    // --- In-app badge (red dot) toggles (all enabled by default) ---
    val inAppBadgeMessages: Boolean = true,      // Red dot for unread chat messages
    val inAppBadgeFriendRequests: Boolean = true,// Red dot for pending friend requests
    val inAppBadgeSharedInbox: Boolean = true,   // Red dot for unseen shared-inbox items
) {
    companion object {
        const val BASE_HISTORY_LIMIT = 50
        const val MAX_HISTORY_LIMIT = 100
        const val HISTORY_EXPANSION_COST = 1000
        const val HISTORY_EXPANSION_INCREMENT = 10
    }
}