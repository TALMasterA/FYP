package com.example.fyp.data.friends

import android.content.Context
import android.content.SharedPreferences
import androidx.annotation.VisibleForTesting
import com.example.fyp.core.security.SecureStorage

/**
 * Persistent storage for tracking which notifications the user has already seen.
 *
 * This prevents red dot notification badges from reappearing on app restart
 * for items the user has already viewed. Supports three types of notifications:
 * 1. Shared inbox items (sheets, quizzes, words shared by friends)
 * 2. Friend requests (incoming friend requests)
 * 3. Chat messages (unread messages per friend)
 *
 * Storage Format:
 * - Shared Items: "seen_shared_items_{userId}" → "item1,item2,item3"
 * - Friend Requests: "seen_friend_requests_{userId}" → "requestId1,requestId2"
 * - Messages: "seen_message_friends_{userId}" → "friendId1,friendId2"
 *
 * **Architecture Note:**
 * This storage mechanism is critical for badge UX consistency across app restarts.
 * Users should only see red dot badges for truly NEW items they haven't yet viewed.
 */
object SeenItemsStorage {
    private const val KEY_PREFIX_SEEN_ITEMS = "seen_shared_items_"
    private const val KEY_PREFIX_SEEN_REQUESTS = "seen_friend_requests_"
    private const val KEY_PREFIX_SEEN_MESSAGE_FRIENDS = "seen_message_friends_"

    /**
     * Override for unit tests to supply a mock [SharedPreferences].
     * When null (production), [SecureStorage]-backed encrypted prefs are used.
     */
    @VisibleForTesting
    internal var prefsProvider: ((Context) -> SharedPreferences)? = null

    private fun getPrefs(context: Context): SharedPreferences =
        prefsProvider?.invoke(context)
            ?: SecureStorage.forContext(context).prefs

    // ── Shared Inbox Items ───────────────────────────────────────────────────

    /**
     * Load the set of shared inbox item IDs that the user has already seen.
     */
    fun loadSeenItemIds(context: Context, userId: String): Set<String> {
        val key = KEY_PREFIX_SEEN_ITEMS + userId
        val csv = getPrefs(context).getString(key, null) ?: return emptySet()
        return if (csv.isEmpty()) emptySet() else csv.split(",").toSet()
    }

    /**
     * Save the set of shared inbox item IDs that the user has seen.
     */
    fun saveSeenItemIds(context: Context, userId: String, seenIds: Set<String>) {
        val key = KEY_PREFIX_SEEN_ITEMS + userId
        val csv = seenIds.joinToString(",")
        getPrefs(context).edit().putString(key, csv).apply()
    }

    /**
     * Mark additional shared inbox item IDs as seen.
     */
    fun addSeenItemIds(context: Context, userId: String, newSeenIds: Set<String>) {
        val existing = loadSeenItemIds(context, userId)
        val updated = existing + newSeenIds
        saveSeenItemIds(context, userId, updated)
    }

    /**
     * Clear all seen shared inbox item IDs for a user.
     */
    fun clearSeenItemIds(context: Context, userId: String) {
        val key = KEY_PREFIX_SEEN_ITEMS + userId
        getPrefs(context).edit().remove(key).apply()
    }

    // ── Friend Requests ───────────────────────────────────────────────────────

    /**
     * Load the set of friend request IDs that the user has already seen.
     * Returns empty set if no requests have been marked as seen.
     */
    fun loadSeenFriendRequestIds(context: Context, userId: String): Set<String> {
        val key = KEY_PREFIX_SEEN_REQUESTS + userId
        val csv = getPrefs(context).getString(key, null) ?: return emptySet()
        return if (csv.isEmpty()) emptySet() else csv.split(",").toSet()
    }

    /**
     * Save the set of friend request IDs that the user has seen.
     */
    fun saveSeenFriendRequestIds(context: Context, userId: String, seenIds: Set<String>) {
        val key = KEY_PREFIX_SEEN_REQUESTS + userId
        val csv = seenIds.joinToString(",")
        getPrefs(context).edit().putString(key, csv).apply()
    }

    /**
     * Mark additional friend request IDs as seen.
     */
    fun addSeenFriendRequestIds(context: Context, userId: String, newSeenIds: Set<String>) {
        val existing = loadSeenFriendRequestIds(context, userId)
        val updated = existing + newSeenIds
        saveSeenFriendRequestIds(context, userId, updated)
    }

    /**
     * Clear all seen friend request IDs for a user.
     */
    fun clearSeenFriendRequestIds(context: Context, userId: String) {
        val key = KEY_PREFIX_SEEN_REQUESTS + userId
        getPrefs(context).edit().remove(key).apply()
    }

    // ── Chat Messages ─────────────────────────────────────────────────────────

    /**
     * Load the set of friend IDs whose messages the user has already seen.
     * Used to track which friends' chat messages have been viewed since app start.
     */
    fun loadSeenMessageFriendIds(context: Context, userId: String): Set<String> {
        val key = KEY_PREFIX_SEEN_MESSAGE_FRIENDS + userId
        val csv = getPrefs(context).getString(key, null) ?: return emptySet()
        return if (csv.isEmpty()) emptySet() else csv.split(",").toSet()
    }

    /**
     * Save the set of friend IDs whose messages have been seen.
     */
    fun saveSeenMessageFriendIds(context: Context, userId: String, seenIds: Set<String>) {
        val key = KEY_PREFIX_SEEN_MESSAGE_FRIENDS + userId
        val csv = seenIds.joinToString(",")
        getPrefs(context).edit().putString(key, csv).apply()
    }

    /**
     * Mark a friend's messages as seen (when user opens their chat).
     */
    fun addSeenMessageFriendId(context: Context, userId: String, friendId: String) {
        val existing = loadSeenMessageFriendIds(context, userId)
        val updated = existing + friendId
        saveSeenMessageFriendIds(context, userId, updated)
    }

    /**
     * Clear all seen message friend IDs for a user.
     */
    fun clearSeenMessageFriendIds(context: Context, userId: String) {
        val key = KEY_PREFIX_SEEN_MESSAGE_FRIENDS + userId
        getPrefs(context).edit().remove(key).apply()
    }

    // ── Bulk Clear ────────────────────────────────────────────────────────────

    /**
     * Clear all notification seen state for a user (e.g., on logout).
     */
    fun clearAllSeenState(context: Context, userId: String) {
        clearSeenItemIds(context, userId)
        clearSeenFriendRequestIds(context, userId)
        clearSeenMessageFriendIds(context, userId)
    }
}
