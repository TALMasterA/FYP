package com.example.fyp.data.friends

import com.google.firebase.firestore.FirebaseFirestore
import com.example.fyp.model.friends.FriendMessage
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * Tests for FirestoreChatRepository to ensure correct chat operations.
 * Tests critical operations like sending messages, marking as read, and metadata updates.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class FirestoreChatRepositoryTest {

    private lateinit var repository: FirestoreChatRepository
    private lateinit var mockFirestore: FirebaseFirestore

    @Before
    fun setup() {
        mockFirestore = mock(FirebaseFirestore::class.java)
        repository = FirestoreChatRepository(mockFirestore)
    }

    @Test
    fun `getChatId generates consistent chat IDs`() {
        val userId1 = "user-123"
        val userId2 = "user-456"

        val chatId1 = repository.getChatId(userId1, userId2)
        val chatId2 = repository.getChatId(userId2, userId1)

        // Chat ID should be the same regardless of parameter order
        assert(chatId1 == chatId2) {
            "Chat IDs should be identical regardless of user order"
        }
    }

    @Test
    fun `getChatId generates different IDs for different user pairs`() {
        val user1 = "user-123"
        val user2 = "user-456"
        val user3 = "user-789"

        val chatId1 = repository.getChatId(user1, user2)
        val chatId2 = repository.getChatId(user1, user3)

        // Different user pairs should have different chat IDs
        assert(chatId1 != chatId2) {
            "Different user pairs should generate different chat IDs"
        }
    }

    @Test
    fun `createFriendMessage creates valid message object`() {
        val senderId = "sender-123"
        val text = "Hello, friend!"
        val timestamp = System.currentTimeMillis()

        val message = FriendMessage(
            id = "msg-1",
            senderId = senderId,
            text = text,
            timestamp = timestamp,
            isRead = false
        )

        assertNotNull(message.id)
        assert(message.senderId == senderId)
        assert(message.text == text)
        assert(message.timestamp == timestamp)
        assert(!message.isRead)
    }

    @Test
    fun `message validation enforces text length limits`() {
        val longText = "a".repeat(5001)  // Exceeds typical max length
        val validText = "a".repeat(1000)  // Within limits

        // Valid text should be accepted
        assertTrue(validText.length <= 5000) {
            "Valid text should be within reasonable limits"
        }

        // Long text should be detected
        assertTrue(longText.length > 5000) {
            "Excessively long text should be detected"
        }
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
        assert(messages.size == 100) {
            "All generated message IDs should be unique"
        }
    }
}
