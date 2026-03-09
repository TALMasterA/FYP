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
    val historyViewLimit: Int = 30, // Default 30 records displayed, expandable to 60
    val autoThemeEnabled: Boolean = false, // Enable time-based theme switching (6 AM - 6 PM light, 6 PM - 6 AM dark)
    val lastPrimaryLanguageChangeMs: Long = 0L, // Epoch ms of last primary language change (0 = never changed)
    val lastUsernameChangeMs: Long = 0L, // Epoch ms of last username change (0 = never changed)
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
        const val BASE_HISTORY_LIMIT = 30
        const val MAX_HISTORY_LIMIT = 60
        const val HISTORY_EXPANSION_COST = 1000
        const val HISTORY_EXPANSION_INCREMENT = 10
        const val MAX_FAVORITE_RECORDS = 20
        const val PRIMARY_LANGUAGE_CHANGE_COOLDOWN_MS = 30L * 24 * 60 * 60 * 1000 // 30 days in ms
        const val USERNAME_CHANGE_COOLDOWN_MS = 30L * 24 * 60 * 60 * 1000 // 30 days in ms

        /**
         * Check if the user can change their primary language based on cooldown.
         * First change is always allowed (lastChangeMs == 0).
         */
        fun canChangePrimaryLanguage(lastChangeMs: Long, currentTimeMs: Long): Boolean {
            if (lastChangeMs == 0L) return true
            return (currentTimeMs - lastChangeMs) >= PRIMARY_LANGUAGE_CHANGE_COOLDOWN_MS
        }

        /**
         * Calculate the remaining cooldown time in milliseconds.
         * Returns 0 if the change is allowed.
         */
        fun primaryLanguageCooldownRemainingMs(lastChangeMs: Long, currentTimeMs: Long): Long {
            if (lastChangeMs == 0L) return 0L
            val elapsed = currentTimeMs - lastChangeMs
            return (PRIMARY_LANGUAGE_CHANGE_COOLDOWN_MS - elapsed).coerceAtLeast(0L)
        }

        /**
         * Check if the user can change their username based on cooldown.
         * First change is always allowed (lastChangeMs == 0).
         */
        fun canChangeUsername(lastChangeMs: Long, currentTimeMs: Long): Boolean {
            if (lastChangeMs == 0L) return true
            return (currentTimeMs - lastChangeMs) >= USERNAME_CHANGE_COOLDOWN_MS
        }

        /**
         * Calculate the remaining username change cooldown time in milliseconds.
         * Returns 0 if the change is allowed.
         */
        fun usernameCooldownRemainingMs(lastChangeMs: Long, currentTimeMs: Long): Long {
            if (lastChangeMs == 0L) return 0L
            val elapsed = currentTimeMs - lastChangeMs
            return (USERNAME_CHANGE_COOLDOWN_MS - elapsed).coerceAtLeast(0L)
        }
    }
}