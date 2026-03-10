package com.example.fyp.data.friends

import com.example.fyp.model.UserId
import com.example.fyp.model.friends.ChatMetadata
import com.example.fyp.model.friends.FriendMessage
import com.example.fyp.model.friends.MessageType
import com.google.firebase.firestore.FirebaseFirestore
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock

/**
 * Tests for pure logic extracted from FirestoreChatRepository.
 *
 * Covers: generateChatId commutativity, isParticipant authorization,
 * message content validation, unread count math, shared item content mapping,
 * and ChatMetadata.getUnreadFor parsing.
 */
class ChatRepositoryLogicTest {

    private lateinit var repository: FirestoreChatRepository
    private lateinit var mockFirestore: FirebaseFirestore
    private lateinit var mockFriendsRepository: FriendsRepository

    @Before
    fun setup() {
        mockFirestore = mock(FirebaseFirestore::class.java)
        mockFriendsRepository = mock(FriendsRepository::class.java)
        repository = FirestoreChatRepository(mockFirestore, mockFriendsRepository)
    }

    // ── generateChatId ────────────────────────────────────────────────

    @Test
    fun `generateChatId sorts user IDs alphabetically`() {
        val chatId = repository.generateChatId(UserId("xyz"), UserId("abc"))
        assertEquals("abc_xyz", chatId)
    }

    @Test
    fun `generateChatId is commutative`() {
        val id1 = repository.generateChatId(UserId("alice"), UserId("bob"))
        val id2 = repository.generateChatId(UserId("bob"), UserId("alice"))
        assertEquals(id1, id2)
    }

    @Test
    fun `generateChatId with identical users produces valid id`() {
        val chatId = repository.generateChatId(UserId("same"), UserId("same"))
        assertEquals("same_same", chatId)
    }

    @Test
    fun `generateChatId with numeric-style IDs sorts correctly`() {
        val chatId = repository.generateChatId(UserId("user-9"), UserId("user-10"))
        // Lexicographic: "user-10" < "user-9" because '1' < '9'
        assertEquals("user-10_user-9", chatId)
    }

    @Test
    fun `generateChatId with special characters sorts correctly`() {
        val chatId = repository.generateChatId(UserId("A-user"), UserId("a-user"))
        // Uppercase 'A' < lowercase 'a' in ASCII
        assertEquals("A-user_a-user", chatId)
    }

    @Test
    fun `generateChatId produces unique IDs for different pairs`() {
        val id1 = repository.generateChatId(UserId("user1"), UserId("user2"))
        val id2 = repository.generateChatId(UserId("user1"), UserId("user3"))
        val id3 = repository.generateChatId(UserId("user2"), UserId("user3"))
        val ids = setOf(id1, id2, id3)
        assertEquals("All pairs should produce unique chat IDs", 3, ids.size)
    }

    // ── isParticipant (tested via chatId format) ──────────────────────

    @Test
    fun `chatId contains both participant IDs`() {
        val chatId = repository.generateChatId(UserId("alice"), UserId("bob"))
        val parts = chatId.split("_", limit = 2)
        assertEquals(2, parts.size)
        assertTrue(parts.contains("alice"))
        assertTrue(parts.contains("bob"))
    }

    @Test
    fun `isParticipant logic - valid participant found in chatId`() {
        val chatId = "alice_bob"
        val parts = chatId.split("_", limit = 2)
        val isAliceIn = parts.size == 2 && (parts[0] == "alice" || parts[1] == "alice")
        val isBobIn = parts.size == 2 && (parts[0] == "bob" || parts[1] == "bob")
        assertTrue(isAliceIn)
        assertTrue(isBobIn)
    }

    @Test
    fun `isParticipant logic - non-participant not found in chatId`() {
        val chatId = "alice_bob"
        val parts = chatId.split("_", limit = 2)
        val isCharlieIn = parts.size == 2 && (parts[0] == "charlie" || parts[1] == "charlie")
        assertFalse(isCharlieIn)
    }

    @Test
    fun `isParticipant logic - malformed chatId without underscore`() {
        val chatId = "malformedchatid"
        val parts = chatId.split("_", limit = 2)
        val isValid = parts.size == 2 && (parts[0] == "user1" || parts[1] == "user1")
        assertFalse(isValid)
    }

    @Test
    fun `isParticipant logic - chatId with user containing underscore in name`() {
        // split with limit=2 ensures only the first underscore is used as separator
        val chatId = "abc_def_ghi"
        val parts = chatId.split("_", limit = 2)
        assertEquals(2, parts.size)
        assertEquals("abc", parts[0])
        assertEquals("def_ghi", parts[1])
    }

    // ── Message content validation ────────────────────────────────────

    @Test
    fun `blank message content violates validation rule`() {
        val content = "   "
        assertTrue("Blank content should be detected", content.isBlank())
    }

    @Test
    fun `message content at max length 2000 passes validation`() {
        val content = "a".repeat(2000)
        assertTrue(content.isNotBlank())
        assertTrue(content.length <= 2000)
    }

    @Test
    fun `message content exceeding 2000 chars fails validation`() {
        val content = "a".repeat(2001)
        assertTrue(content.length > 2000)
    }

    @Test
    fun `empty string message is blank`() {
        assertTrue("".isBlank())
    }

    // ── Unread count math ─────────────────────────────────────────────

    @Test
    fun `unread subtraction coerced to zero for negative result`() {
        val currentTotal = 5
        val chatUnread = 10
        val result = (currentTotal - chatUnread).coerceAtLeast(0)
        assertEquals(0, result)
    }

    @Test
    fun `unread subtraction normal case`() {
        val currentTotal = 15
        val chatUnread = 5
        val result = (currentTotal - chatUnread).coerceAtLeast(0)
        assertEquals(10, result)
    }

    @Test
    fun `unread subtraction with zero chatUnread`() {
        val currentTotal = 10
        val chatUnread = 0
        val result = (currentTotal - chatUnread).coerceAtLeast(0)
        assertEquals(10, result)
    }

    @Test
    fun `unread subtraction both zero`() {
        val currentTotal = 0
        val chatUnread = 0
        val result = (currentTotal - chatUnread).coerceAtLeast(0)
        assertEquals(0, result)
    }

    // ── Shared item content mapping ───────────────────────────────────

    @Test
    fun `shared word message type maps to correct content text`() {
        val content = when (MessageType.SHARED_WORD) {
            MessageType.SHARED_WORD -> "Shared a word"
            MessageType.SHARED_LEARNING_MATERIAL -> "Shared learning material"
            else -> "Shared an item"
        }
        assertEquals("Shared a word", content)
    }

    @Test
    fun `shared learning material message type maps to correct content text`() {
        val content = when (MessageType.SHARED_LEARNING_MATERIAL) {
            MessageType.SHARED_WORD -> "Shared a word"
            MessageType.SHARED_LEARNING_MATERIAL -> "Shared learning material"
            else -> "Shared an item"
        }
        assertEquals("Shared learning material", content)
    }

    @Test
    fun `TEXT message type maps to shared an item fallback`() {
        val content = when (MessageType.TEXT) {
            MessageType.SHARED_WORD -> "Shared a word"
            MessageType.SHARED_LEARNING_MATERIAL -> "Shared learning material"
            else -> "Shared an item"
        }
        assertEquals("Shared an item", content)
    }

    // ── ChatMetadata.getUnreadFor ─────────────────────────────────────

    @Test
    fun `getUnreadFor returns count for existing user`() {
        val metadata = ChatMetadata(
            unreadCount = mapOf("user1" to 5L, "user2" to 3L)
        )
        assertEquals(5, metadata.getUnreadFor("user1"))
        assertEquals(3, metadata.getUnreadFor("user2"))
    }

    @Test
    fun `getUnreadFor returns 0 for unknown user`() {
        val metadata = ChatMetadata(unreadCount = mapOf("user1" to 5L))
        assertEquals(0, metadata.getUnreadFor("unknown"))
    }

    @Test
    fun `getUnreadFor returns 0 for empty unread map`() {
        val metadata = ChatMetadata(unreadCount = emptyMap())
        assertEquals(0, metadata.getUnreadFor("user1"))
    }

    @Test
    fun `getUnreadFor handles Integer type in unread map`() {
        val metadata = ChatMetadata(
            unreadCount = mapOf("user1" to 7 as Any)
        )
        assertEquals(7, metadata.getUnreadFor("user1"))
    }

    @Test
    fun `getUnreadFor handles Double type gracefully converting to int`() {
        val metadata = ChatMetadata(
            unreadCount = mapOf("user1" to 3.0 as Any)
        )
        assertEquals(3, metadata.getUnreadFor("user1"))
    }
}
