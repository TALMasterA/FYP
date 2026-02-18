package com.example.fyp.domain.friends

import com.example.fyp.data.friends.ChatRepository
import com.example.fyp.model.UserId
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.stub
import org.mockito.kotlin.verify

/**
 * Unit tests for SendMessageUseCase.
 * Tests sending chat messages between friends.
 */
class SendMessageUseCaseTest {

    private lateinit var chatRepository: ChatRepository
    private lateinit var useCase: SendMessageUseCase

    @Before
    fun setup() {
        chatRepository = mock()
        useCase = SendMessageUseCase(chatRepository)
    }

    @Test
    fun `send message succeeds with valid parameters`() = runTest {
        // Arrange
        val chatId = "chat123"
        val senderId = UserId("user1")
        val recipientId = UserId("user2")
        val text = "Hello, how are you?"

        chatRepository.stub {
            onBlocking { sendMessage(chatId, senderId, recipientId, text) } doReturn Result.success(Unit)
        }

        // Act
        val result = useCase(chatId, senderId, recipientId, text)

        // Assert
        assertTrue(result.isSuccess)
        verify(chatRepository).sendMessage(chatId, senderId, recipientId, text)
    }

    @Test
    fun `send message with empty text returns error`() = runTest {
        // Arrange
        val chatId = "chat123"
        val senderId = UserId("user1")
        val recipientId = UserId("user2")
        val text = ""

        // Act
        val result = useCase(chatId, senderId, recipientId, text)

        // Assert
        assertTrue(result.isFailure)
        assertEquals("Message text cannot be empty", result.exceptionOrNull()?.message)
    }

    @Test
    fun `send message with only whitespace returns error`() = runTest {
        // Arrange
        val chatId = "chat123"
        val senderId = UserId("user1")
        val recipientId = UserId("user2")
        val text = "   "

        // Act
        val result = useCase(chatId, senderId, recipientId, text)

        // Assert
        assertTrue(result.isFailure)
    }

    @Test
    fun `send message handles repository failure`() = runTest {
        // Arrange
        val chatId = "chat123"
        val senderId = UserId("user1")
        val recipientId = UserId("user2")
        val text = "Hello"
        val exception = Exception("Network error")

        chatRepository.stub {
            onBlocking { sendMessage(chatId, senderId, recipientId, text) } doReturn Result.failure(exception)
        }

        // Act
        val result = useCase(chatId, senderId, recipientId, text)

        // Assert
        assertTrue(result.isFailure)
        assertEquals("Network error", result.exceptionOrNull()?.message)
    }

    @Test
    fun `send long message succeeds`() = runTest {
        // Arrange
        val chatId = "chat123"
        val senderId = UserId("user1")
        val recipientId = UserId("user2")
        val text = "A".repeat(1000) // Long message

        chatRepository.stub {
            onBlocking { sendMessage(chatId, senderId, recipientId, text) } doReturn Result.success(Unit)
        }

        // Act
        val result = useCase(chatId, senderId, recipientId, text)

        // Assert
        assertTrue(result.isSuccess)
    }
}
