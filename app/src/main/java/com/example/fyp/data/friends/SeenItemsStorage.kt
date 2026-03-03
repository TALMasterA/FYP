package com.example.fyp.data.friends

import android.content.Context
import android.content.SharedPreferences

/**
 * Persistent storage for tracking which shared inbox items the user has already seen.
 *
 * This prevents the red dot notification badge from reappearing on app restart
 * for items the user has already viewed.
 *
 * Storage Format:
 * - Key: "seen_shared_items_{userId}"
 * - Value: Comma-separated string of item IDs (e.g., "item1,item2,item3")
 *
 * **Architecture Note:**
 * This storage mechanism is critical for badge UX consistency. See ARCHITECTURE_NOTES.md
 * section on "Red Dot Notification Persistence" for implementation details.
 */
object SeenItemsStorage {
    private const val PREFS_NAME = "shared_inbox_prefs"
    private const val KEY_PREFIX_SEEN_ITEMS = "seen_shared_items_"

    private fun getPrefs(context: Context): SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    /**
     * Load the set of item IDs that the user has already seen.
     * Returns an empty set if no items have been marked as seen.
     */
    fun loadSeenItemIds(context: Context, userId: String): Set<String> {
        val key = KEY_PREFIX_SEEN_ITEMS + userId
        val csv = getPrefs(context).getString(key, null) ?: return emptySet()
        return if (csv.isEmpty()) emptySet() else csv.split(",").toSet()
    }

    /**
     * Save the set of item IDs that the user has seen.
     * Overwrites the previous value.
     */
    fun saveSeenItemIds(context: Context, userId: String, seenIds: Set<String>) {
        val key = KEY_PREFIX_SEEN_ITEMS + userId
        val csv = seenIds.joinToString(",")
        getPrefs(context).edit().putString(key, csv).apply()
    }

    /**
     * Mark additional item IDs as seen (without overwriting existing ones).
     */
    fun addSeenItemIds(context: Context, userId: String, newSeenIds: Set<String>) {
        val existing = loadSeenItemIds(context, userId)
        val updated = existing + newSeenIds
        saveSeenItemIds(context, userId, updated)
    }

    /**
     * Clear all seen item IDs for a user (e.g., on logout).
     */
    fun clearSeenItemIds(context: Context, userId: String) {
        val key = KEY_PREFIX_SEEN_ITEMS + userId
        getPrefs(context).edit().remove(key).apply()
    }
}
