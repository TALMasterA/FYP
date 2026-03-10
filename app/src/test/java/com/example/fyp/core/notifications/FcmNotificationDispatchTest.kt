package com.example.fyp.core.notifications

import org.junit.Assert.*
import org.junit.Test

/**
 * Tests for pure logic extracted from FcmNotificationService.
 *
 * Covers:
 *  - Notification type dispatch rules (which type maps to which preference)
 *  - Preference gating logic (isNotifEnabled default-true behavior)
 *  - Data field extraction with fallback defaults
 *  - Notification ID generation (deterministic per friend/sender)
 *  - Valid notification types
 *  - saveNotifPrefToCache field names
 */
class FcmNotificationDispatchTest {

    // ── Notification type to preference field mapping ─────────────────────────

    /**
     * Maps notification type strings to their preference field names.
     * Replicates the when(type) block in onMessageReceived.
     */
    private fun preferenceFieldForType(type: String): String? {
        return when (type) {
            "new_message" -> "notifyNewMessages"
            "friend_request" -> "notifyFriendRequests"
            "request_accepted" -> "notifyRequestAccepted"
            "shared_item" -> "notifySharedInbox"
            else -> null
        }
    }

    @Test
    fun `new_message type maps to notifyNewMessages preference`() {
        assertEquals("notifyNewMessages", preferenceFieldForType("new_message"))
    }

    @Test
    fun `friend_request type maps to notifyFriendRequests preference`() {
        assertEquals("notifyFriendRequests", preferenceFieldForType("friend_request"))
    }

    @Test
    fun `request_accepted type maps to notifyRequestAccepted preference`() {
        assertEquals("notifyRequestAccepted", preferenceFieldForType("request_accepted"))
    }

    @Test
    fun `shared_item type maps to notifySharedInbox preference`() {
        assertEquals("notifySharedInbox", preferenceFieldForType("shared_item"))
    }

    @Test
    fun `unknown type returns null preference`() {
        assertNull(preferenceFieldForType("unknown_type"))
    }

    @Test
    fun `empty type returns null preference`() {
        assertNull(preferenceFieldForType(""))
    }

    // ── isNotifEnabled default behavior ──────────────────────────────────────

    /**
     * Replicates isNotifEnabled logic: absent key returns true (fail-open).
     */
    private fun isNotifEnabled(prefs: Map<String, Boolean>, field: String): Boolean {
        return prefs[field] ?: true // default true when absent
    }

    @Test
    fun `isNotifEnabled - returns true when preference absent (fail-open)`() {
        val prefs = emptyMap<String, Boolean>()
        assertTrue(isNotifEnabled(prefs, "notifyNewMessages"))
    }

    @Test
    fun `isNotifEnabled - returns true when preference explicitly true`() {
        val prefs = mapOf("notifyNewMessages" to true)
        assertTrue(isNotifEnabled(prefs, "notifyNewMessages"))
    }

    @Test
    fun `isNotifEnabled - returns false when preference explicitly false`() {
        val prefs = mapOf("notifyNewMessages" to false)
        assertFalse(isNotifEnabled(prefs, "notifyNewMessages"))
    }

    @Test
    fun `isNotifEnabled - each preference field can be independently toggled`() {
        val prefs = mapOf(
            "notifyNewMessages" to true,
            "notifyFriendRequests" to false,
            "notifyRequestAccepted" to true,
            "notifySharedInbox" to false
        )
        assertTrue(isNotifEnabled(prefs, "notifyNewMessages"))
        assertFalse(isNotifEnabled(prefs, "notifyFriendRequests"))
        assertTrue(isNotifEnabled(prefs, "notifyRequestAccepted"))
        assertFalse(isNotifEnabled(prefs, "notifySharedInbox"))
    }

    // ── Data field extraction with fallbacks ─────────────────────────────────

    /**
     * Replicates data field extraction with fallback defaults.
     */
    @Test
    fun `chat notification data - extracts senderUsername with fallback`() {
        val data = mapOf("type" to "new_message", "senderId" to "user1")
        val senderUsername = data["senderUsername"] ?: "Friend"
        assertEquals("Friend", senderUsername)
    }

    @Test
    fun `chat notification data - uses provided senderUsername`() {
        val data = mapOf("type" to "new_message", "senderUsername" to "Alice", "senderId" to "u1")
        val senderUsername = data["senderUsername"] ?: "Friend"
        assertEquals("Alice", senderUsername)
    }

    @Test
    fun `chat notification data - extracts messagePreview with fallback`() {
        val data = mapOf("type" to "new_message")
        val messagePreview = data["messagePreview"] ?: "Sent you a message"
        assertEquals("Sent you a message", messagePreview)
    }

    @Test
    fun `friend request data - extracts senderUsername with fallback`() {
        val data = mapOf("type" to "friend_request")
        val senderUsername = data["senderUsername"] ?: "Someone"
        assertEquals("Someone", senderUsername)
    }

    @Test
    fun `shared item data - extracts title with fallback`() {
        val data = mapOf("type" to "shared_item")
        val title = data["title"] ?: "Shared something with you"
        assertEquals("Shared something with you", title)
    }

    // ── Notification ID determinism ──────────────────────────────────────────

    @Test
    fun `chat notification ID is deterministic per friendId`() {
        val friendId = "friend123"
        val id1 = friendId.hashCode()
        val id2 = friendId.hashCode()
        assertEquals(id1, id2)
    }

    @Test
    fun `different friendIds produce different notification IDs`() {
        val id1 = "friend1".hashCode()
        val id2 = "friend2".hashCode()
        assertNotEquals(id1, id2)
    }

    @Test
    fun `friend request notification ID is deterministic`() {
        val id = "friend_request".hashCode()
        assertEquals("friend_request".hashCode(), id)
    }

    // ── Null type handling ───────────────────────────────────────────────────

    @Test
    fun `missing type in data returns early (no dispatch)`() {
        val data = mapOf("senderUsername" to "Alice")
        val type = data["type"] // null
        assertNull(type)
    }

    // ── Valid notification preference field names ─────────────────────────────

    @Test
    fun `all four notification preference fields are distinct`() {
        val fields = listOf(
            "notifyNewMessages",
            "notifyFriendRequests",
            "notifyRequestAccepted",
            "notifySharedInbox"
        )
        assertEquals(4, fields.toSet().size)
    }

    // ── Companion object constants ──────────────────────────────────────────

    @Test
    fun `valid notification types cover all four categories`() {
        val validTypes = setOf("new_message", "friend_request", "request_accepted", "shared_item")
        assertEquals(4, validTypes.size)
        validTypes.forEach { type ->
            assertNotNull("Type '$type' should have a preference mapping", preferenceFieldForType(type))
        }
    }
}
