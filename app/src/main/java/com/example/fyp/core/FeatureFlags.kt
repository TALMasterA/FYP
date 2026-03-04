package com.example.fyp.core

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Feature flags backed by Firebase Remote Config.
 *
 * Provides safe defaults so the app works even when Remote Config
 * hasn't fetched yet. Call [activate] early (e.g. in Application.onCreate)
 * to pull the latest values from the server.
 */
@Singleton
class FeatureFlags @Inject constructor() {

    private val remoteConfig: FirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()

    init {
        val configSettings = FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds(3600) // 1 hour in production
            .build()
        remoteConfig.setConfigSettingsAsync(configSettings)

        // Safe defaults – used until the first successful fetch
        remoteConfig.setDefaultsAsync(
            mapOf(
                KEY_ENABLE_BULK_FRIEND_ACTIONS to true,
                KEY_FRIENDS_PAGE_SIZE to 20L,
                KEY_MAX_MESSAGE_LENGTH to 2000L,
                KEY_ENABLE_CHAT_TRANSLATION to true,
                KEY_MAX_FRIENDS_LIMIT to 500L,
                KEY_FRIEND_REQUEST_EXPIRY_DAYS to 30L,
                KEY_ENABLE_SHARED_INBOX to true
            )
        )
    }

    // ── Public Flags ────────────────────────────────────────────────

    /** Whether bulk accept / reject all friend requests is enabled. */
    val enableBulkFriendActions: Boolean
        get() = remoteConfig.getBoolean(KEY_ENABLE_BULK_FRIEND_ACTIONS)

    /** Number of friends to load per page. */
    val friendsPageSize: Int
        get() = remoteConfig.getLong(KEY_FRIENDS_PAGE_SIZE).toInt()

    /** Maximum length of a single chat message. */
    val maxMessageLength: Int
        get() = remoteConfig.getLong(KEY_MAX_MESSAGE_LENGTH).toInt()

    /** Whether in-chat translation feature is enabled. */
    val enableChatTranslation: Boolean
        get() = remoteConfig.getBoolean(KEY_ENABLE_CHAT_TRANSLATION)

    /** Maximum number of friends a user can have. */
    val maxFriendsLimit: Int
        get() = remoteConfig.getLong(KEY_MAX_FRIENDS_LIMIT).toInt()

    /** How many days before a friend request expires. */
    val friendRequestExpiryDays: Int
        get() = remoteConfig.getLong(KEY_FRIEND_REQUEST_EXPIRY_DAYS).toInt()

    /** Whether the shared inbox feature is enabled. */
    val enableSharedInbox: Boolean
        get() = remoteConfig.getBoolean(KEY_ENABLE_SHARED_INBOX)

    // ── Lifecycle ───────────────────────────────────────────────────

    /**
     * Fetches the latest config values and activates them.
     * Call once during app startup (fire-and-forget is fine).
     */
    fun activate() {
        remoteConfig.fetchAndActivate()
    }

    // ── Keys ────────────────────────────────────────────────────────

    companion object {
        private const val KEY_ENABLE_BULK_FRIEND_ACTIONS = "enable_bulk_friend_actions"
        private const val KEY_FRIENDS_PAGE_SIZE = "friends_page_size"
        private const val KEY_MAX_MESSAGE_LENGTH = "max_message_length"
        private const val KEY_ENABLE_CHAT_TRANSLATION = "enable_chat_translation"
        private const val KEY_MAX_FRIENDS_LIMIT = "max_friends_limit"
        private const val KEY_FRIEND_REQUEST_EXPIRY_DAYS = "friend_request_expiry_days"
        private const val KEY_ENABLE_SHARED_INBOX = "enable_shared_inbox"
    }
}
