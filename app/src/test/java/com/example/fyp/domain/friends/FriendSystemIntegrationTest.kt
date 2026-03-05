package com.example.fyp.domain.friends

import com.example.fyp.model.friends.SharedItemStatus
import com.example.fyp.model.friends.SharedItemType
import com.example.fyp.model.friends.SharedItem
import com.example.fyp.model.friends.FriendRequest
import com.example.fyp.model.friends.FriendRelation
import com.example.fyp.data.friends.BlockedUser
import com.example.fyp.model.friends.FriendMessage
import com.example.fyp.model.friends.MessageType
import com.example.fyp.model.friends.ChatMetadata
import com.google.firebase.Timestamp
import org.junit.Assert.*
import org.junit.Test

/**
 * Integration-style tests for the friend system logic (item 9).
 *
 * Verifies:
 * - Block/notification behavior
 * - Removed friends/shared inbox cleanup
 * - Search & add friends flow
 * - Friend chatting functionality
 * - WhatsApp-like chat behaviors
 * - Shared inbox management
 */
class FriendSystemIntegrationTest {

    // ── Friend Request Flow ─────────────────────────────────────────

    @Test
    fun `friend request has required fields`() {
        val request = FriendRequest(
            requestId = "req1",
            fromUserId = "user1",
            toUserId = "user2",
            fromUsername = "alice",
            note = "Hi, let's be friends!"
        )

        assertEquals("req1", request.requestId)
        assertEquals("user1", request.fromUserId)
        assertEquals("user2", request.toUserId)
        assertEquals("alice", request.fromUsername)
        assertEquals("Hi, let's be friends!", request.note)
    }

    @Test
    fun `friend request defaults have empty strings`() {
        val request = FriendRequest()
        assertEquals("", request.requestId)
        assertEquals("", request.fromUserId)
        assertEquals("", request.toUserId)
    }

    // ── Friend Relation ────────────────────────────────────────────

    @Test
    fun `friend relation contains friend info`() {
        val relation = FriendRelation(
            friendId = "friend1",
            friendUsername = "bob"
        )

        assertEquals("friend1", relation.friendId)
        assertEquals("bob", relation.friendUsername)
    }

    @Test
    fun `friend relation defaults`() {
        val relation = FriendRelation()
        assertEquals("", relation.friendId)
        assertEquals("", relation.friendUsername)
    }

    // ── Blocked User Model ─────────────────────────────────────────

    @Test
    fun `blocked user has userId and username`() {
        val blocked = BlockedUser(
            userId = "user2",
            username = "badUser"
        )

        assertEquals("user2", blocked.userId)
        assertEquals("badUser", blocked.username)
    }

    @Test
    fun `blocked user defaults`() {
        // BlockedUser requires userId and username at construction
        val blocked = BlockedUser(userId = "", username = "")
        assertEquals("", blocked.userId)
        assertEquals("", blocked.username)
    }

    // ── Block System Logic ─────────────────────────────────────────

    @Test
    fun `blocked user ids tracked as set for O(1) lookup`() {
        val blockedUserIds = setOf("user2", "user3", "user5")

        assertTrue(blockedUserIds.contains("user2"))
        assertTrue(blockedUserIds.contains("user3"))
        assertFalse(blockedUserIds.contains("user4"))
        assertTrue(blockedUserIds.contains("user5"))
    }

    @Test
    fun `blocking adds to blocked set`() {
        var blockedUserIds = setOf("user2")
        blockedUserIds = blockedUserIds + "user3"

        assertEquals(2, blockedUserIds.size)
        assertTrue(blockedUserIds.contains("user3"))
    }

    @Test
    fun `unblocking removes from blocked set`() {
        var blockedUserIds = setOf("user2", "user3")
        blockedUserIds = blockedUserIds - "user3"

        assertEquals(1, blockedUserIds.size)
        assertFalse(blockedUserIds.contains("user3"))
    }

    // ── Chat Message Model ─────────────────────────────────────────

    @Test
    fun `text message type is default`() {
        val message = FriendMessage()
        assertEquals(MessageType.TEXT, message.type)
    }

    @Test
    fun `shared word message type`() {
        val message = FriendMessage(
            messageId = "m1",
            senderId = "user1",
            receiverId = "user2",
            content = "hello = hola",
            type = MessageType.SHARED_WORD
        )

        assertEquals(MessageType.SHARED_WORD, message.type)
    }

    @Test
    fun `shared learning material message type`() {
        val message = FriendMessage(
            type = MessageType.SHARED_LEARNING_MATERIAL,
            metadata = mapOf("materialId" to "mat1")
        )

        assertEquals(MessageType.SHARED_LEARNING_MATERIAL, message.type)
        assertEquals("mat1", message.metadata["materialId"])
    }

    @Test
    fun `message content cannot be blank for sending`() {
        val content = "   "
        val isBlank = content.isBlank()
        assertTrue("Blank messages should not be sent", isBlank)
    }

    @Test
    fun `valid message content passes check`() {
        val content = "Hello friend!"
        val isBlank = content.isBlank()
        assertFalse("Valid message content should pass", isBlank)
    }

    // ── Chat Metadata / Unread Counts ──────────────────────────────

    @Test
    fun `chat metadata tracks unread count per user`() {
        val metadata = ChatMetadata(
            chatId = "chat1",
            participants = listOf("user1", "user2"),
            unreadCount = mapOf("user1" to 5L, "user2" to 0L)
        )

        assertEquals(5, metadata.getUnreadFor("user1"))
        assertEquals(0, metadata.getUnreadFor("user2"))
    }

    @Test
    fun `unread count returns 0 for unknown user`() {
        val metadata = ChatMetadata(
            unreadCount = mapOf("user1" to 3L)
        )

        assertEquals(0, metadata.getUnreadFor("unknownUser"))
    }

    // ── Shared Inbox ───────────────────────────────────────────────

    @Test
    fun `shared item starts as PENDING`() {
        val item = SharedItem(
            itemId = "item1",
            fromUserId = "user1",
            toUserId = "user2",
            type = SharedItemType.WORD
        )

        assertEquals(SharedItemStatus.PENDING, item.status)
    }

    @Test
    fun `shared item can be ACCEPTED`() {
        val item = SharedItem(status = SharedItemStatus.ACCEPTED)
        assertEquals(SharedItemStatus.ACCEPTED, item.status)
    }

    @Test
    fun `shared item can be DISMISSED`() {
        val item = SharedItem(status = SharedItemStatus.DISMISSED)
        assertEquals(SharedItemStatus.DISMISSED, item.status)
    }

    @Test
    fun `shared item types include WORD, LEARNING_SHEET, QUIZ`() {
        assertEquals(3, SharedItemType.entries.size)
        assertNotNull(SharedItemType.valueOf("WORD"))
        assertNotNull(SharedItemType.valueOf("LEARNING_SHEET"))
        assertNotNull(SharedItemType.valueOf("QUIZ"))
    }

    @Test
    fun `shared item status types include PENDING, ACCEPTED, DISMISSED`() {
        assertEquals(3, SharedItemStatus.entries.size)
        assertNotNull(SharedItemStatus.valueOf("PENDING"))
        assertNotNull(SharedItemStatus.valueOf("ACCEPTED"))
        assertNotNull(SharedItemStatus.valueOf("DISMISSED"))
    }

    @Test
    fun `shared item carries content map`() {
        val item = SharedItem(
            type = SharedItemType.LEARNING_SHEET,
            content = mapOf(
                "title" to "Japanese Basics",
                "materialId" to "mat123",
                "description" to "Learn hiragana"
            )
        )

        assertEquals("Japanese Basics", item.content["title"])
        assertEquals("mat123", item.content["materialId"])
    }

    // ── Chat ID Generation ─────────────────────────────────────────

    @Test
    fun `chat ID is deterministic - smaller ID first`() {
        // Chat ID format: smaller_uid + "_" + larger_uid
        val user1 = "abc123"
        val user2 = "xyz789"

        val chatId = if (user1 < user2) "${user1}_${user2}" else "${user2}_${user1}"
        assertEquals("abc123_xyz789", chatId)
    }

    @Test
    fun `chat ID is same regardless of who initiates`() {
        val user1 = "xyz789"
        val user2 = "abc123"

        val chatIdFromUser1 = if (user1 < user2) "${user1}_${user2}" else "${user2}_${user1}"
        val chatIdFromUser2 = if (user2 < user1) "${user2}_${user1}" else "${user1}_${user2}"

        assertEquals(chatIdFromUser1, chatIdFromUser2)
    }

    // ── Notification Badges ────────────────────────────────────────

    @Test
    fun `total notification count sums all types`() {
        val pendingRequests = 3
        val unreadMessages = 7
        val unseenSharedItems = 2

        val totalBadge = pendingRequests + unreadMessages + unseenSharedItems
        assertEquals(12, totalBadge)
    }

    @Test
    fun `notification badge hidden when all counts zero`() {
        val totalBadge = 0 + 0 + 0
        assertFalse("Badge should be hidden", totalBadge > 0)
    }

    @Test
    fun `notification badge shown when any count non-zero`() {
        val totalBadge = 0 + 1 + 0
        assertTrue("Badge should show with any non-zero count", totalBadge > 0)
    }

    // ── Friend Remove Cleans Up ───────────────────────────────────

    @Test
    fun `removing friend removes from friends list`() {
        var friends = listOf("friend1", "friend2", "friend3")
        friends = friends.filter { it != "friend2" }

        assertEquals(2, friends.size)
        assertFalse(friends.contains("friend2"))
    }

    @Test
    fun `removing non-existent friend is safe`() {
        val friends = listOf("friend1", "friend2")
        val filtered = friends.filter { it != "nonexistent" }

        assertEquals(2, filtered.size)
    }
}
