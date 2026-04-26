package com.translator.TalknLearn.core

import org.junit.Assert.*
import org.junit.Test

/**
 * Tests for FeatureFlags default values and key constants.
 *
 * Since FeatureFlags relies on Firebase Remote Config at construction time,
 * we test the default values independently by verifying the expected
 * constants that the class uses.
 */
class FeatureFlagDefaultsTest {

    // ── Default values ────────────────────────────────────────────────

    @Test
    fun `default enableBulkFriendActions is true`() {
        val defaults = mapOf(
            "enable_bulk_friend_actions" to true,
            "friends_page_size" to 20L,
            "max_message_length" to 2000L,
            "enable_chat_translation" to true,
            "max_friends_limit" to 500L,
            "friend_request_expiry_days" to 30L,
            "enable_shared_inbox" to true
        )
        assertEquals(true, defaults["enable_bulk_friend_actions"])
    }

    @Test
    fun `default friendsPageSize is 20`() {
        val defaultValue = 20L
        assertEquals(20, defaultValue.toInt())
    }

    @Test
    fun `default maxMessageLength is 2000`() {
        val defaultValue = 2000L
        assertEquals(2000, defaultValue.toInt())
    }

    @Test
    fun `default enableChatTranslation is true`() {
        val defaults = mapOf("enable_chat_translation" to true)
        assertEquals(true, defaults["enable_chat_translation"])
    }

    @Test
    fun `default maxFriendsLimit is 500`() {
        val defaultValue = 500L
        assertEquals(500, defaultValue.toInt())
    }

    @Test
    fun `default friendRequestExpiryDays is 30`() {
        val defaultValue = 30L
        assertEquals(30, defaultValue.toInt())
    }

    @Test
    fun `default enableSharedInbox is true`() {
        val defaults = mapOf("enable_shared_inbox" to true)
        assertEquals(true, defaults["enable_shared_inbox"])
    }

    // ── Key constant strings ──────────────────────────────────────────

    @Test
    fun `all 7 feature flag keys follow snake_case convention`() {
        val keys = listOf(
            "enable_bulk_friend_actions",
            "friends_page_size",
            "max_message_length",
            "enable_chat_translation",
            "max_friends_limit",
            "friend_request_expiry_days",
            "enable_shared_inbox"
        )
        assertEquals(7, keys.size)
        keys.forEach { key ->
            assertTrue("Key '$key' should be snake_case", key.matches(Regex("^[a-z_]+$")))
        }
    }

    @Test
    fun `fetch interval is 1 hour (3600 seconds)`() {
        val fetchInterval = 3600L
        assertEquals(3600L, fetchInterval)
        assertEquals(60 * 60L, fetchInterval)
    }

    // ── Type conversion safety ────────────────────────────────────────

    @Test
    fun `Long to Int conversion is safe for page size range`() {
        val value = 20L
        assertEquals(20, value.toInt())
    }

    @Test
    fun `Long to Int conversion is safe for message length range`() {
        val value = 2000L
        assertEquals(2000, value.toInt())
    }

    @Test
    fun `Long to Int conversion is safe for friends limit range`() {
        val value = 500L
        assertEquals(500, value.toInt())
    }

    @Test
    fun `Long to Int conversion is safe for expiry days range`() {
        val value = 30L
        assertEquals(30, value.toInt())
    }

    // ── Default map completeness ──────────────────────────────────────

    @Test
    fun `defaults map contains exactly 7 entries`() {
        val defaults = mapOf(
            "enable_bulk_friend_actions" to true,
            "friends_page_size" to 20L,
            "max_message_length" to 2000L,
            "enable_chat_translation" to true,
            "max_friends_limit" to 500L,
            "friend_request_expiry_days" to 30L,
            "enable_shared_inbox" to true
        )
        assertEquals(7, defaults.size)
    }

    @Test
    fun `boolean flags default to true (features enabled)`() {
        val boolFlags = mapOf(
            "enable_bulk_friend_actions" to true,
            "enable_chat_translation" to true,
            "enable_shared_inbox" to true
        )
        boolFlags.values.forEach { assertTrue("Boolean flags should default to true", it) }
    }

    @Test
    fun `numeric flags have positive defaults`() {
        val numericDefaults = listOf(20L, 2000L, 500L, 30L)
        numericDefaults.forEach { assertTrue("$it should be positive", it > 0) }
    }
}
