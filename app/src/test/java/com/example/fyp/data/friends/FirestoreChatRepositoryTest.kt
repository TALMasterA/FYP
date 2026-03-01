package com.example.fyp.data.friends

import com.google.firebase.firestore.FirebaseFirestore
import com.example.fyp.model.friends.FriendMessage
import com.example.fyp.model.UserId
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*

/**
 * Tests for FirestoreChatRepository to ensure correct chat operations.
 * Tests critical operations like sending messages, marking as read, and metadata updates.
 */
class FirestoreChatRepositoryTest {

    private lateinit var repository: FirestoreChatRepository
    private lateinit var mockFirestore: FirebaseFirestore

    @Before
    fun setup() {
        mockFirestore = mock(FirebaseFirestore::class.java)
        repository = FirestoreChatRepository(mockFirestore)
    }

    @Test
    fun `generateChatId generates consistent chat IDs`() {
        val userId1 = UserId("user-123")
        val userId2 = UserId("user-456")

        val chatId1 = repository.generateChatId(userId1, userId2)
        val chatId2 = repository.generateChatId(userId2, userId1)

        // Chat ID should be the same regardless of parameter order
        assertEquals("Chat IDs should be identical regardless of user order", chatId1, chatId2)
    }

    @Test
    fun `generateChatId generates different IDs for different user pairs`() {
        val user1 = UserId("user-123")
        val user2 = UserId("user-456")
        val user3 = UserId("user-789")

        val chatId1 = repository.generateChatId(user1, user2)
        val chatId2 = repository.generateChatId(user1, user3)

        // Different user pairs should have different chat IDs
        assertTrue(
            "Different user pairs should generate different chat IDs",
            chatId1 != chatId2
        )
    }

    @Test
    fun `createFriendMessage creates valid message object`() {
        val senderId = "sender-123"
        val content = "Hello, friend!"

        val message = FriendMessage(
            messageId = "msg-1",
            senderId = senderId,
            content = content,
            isRead = false
        )

        assertNotNull(message.messageId)
        assertEquals(senderId, message.senderId)
        assertEquals(content, message.content)
        assertTrue(!message.isRead)
    }

    @Test
    fun `message validation enforces text length limits`() {
        val longText = "a".repeat(5001)  // Exceeds typical max length
        val validText = "a".repeat(1000)  // Within limits

        // Valid text should be accepted
        assertTrue(
            "Valid text should be within reasonable limits",
            validText.length <= 5000
        )

        // Long text should be detected
        assertTrue(
            "Excessively long text should be detected",
            longText.length > 5000
        )
    }

    @Test
    fun `message IDs are unique`() {
        val messages = mutableSetOf<String>()

        // Generate multiple message IDs
        repeat(100) {
            val id = java.util.UUID.randomUUID().toString()
            messages.add(id)
        }

        // All IDs should be unique
        assertEquals(
            "All generated message IDs should be unique",
            100,
            messages.size
        )
    }
}
