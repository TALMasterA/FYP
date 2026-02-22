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
 * Unit tests for MarkMessagesAsReadUseCase.
 * Tests marking all messages in a chat as read.
 */
class MarkMessagesAsReadUseCaseTest {

    private lateinit var chatRepository: ChatRepository
    private lateinit var useCase: MarkMessagesAsReadUseCase

    @Before
    fun setup() {
        chatRepository = mock()
        useCase = MarkMessagesAsReadUseCase(chatRepository)
    }

    @Test
    fun `generates correct chat ID and marks messages as read`() = runTest {
        // Arrange
        val userId = UserId("user1")
        val friendId = UserId("user2")
        val expectedChatId = "user1_user2"

        chatRepository.stub {
            on { generateChatId(userId, friendId) } doReturn expectedChatId
            onBlocking { markAllMessagesAsRead(expectedChatId, userId) } doReturn Result.success(Unit)
        }

        // Act
        val result = useCase(userId, friendId)

        // Assert
        assertTrue(result.isSuccess)
        verify(chatRepository).generateChatId(userId, friendId)
        verify(chatRepository).markAllMessagesAsRead(expectedChatId, userId)
    }

    @Test
    fun `handles repository failure gracefully`() = runTest {
        // Arrange
        val userId = UserId("user1")
        val friendId = UserId("user2")
        val chatId = "user1_user2"
        val exception = Exception("Network error")

        chatRepository.stub {
            on { generateChatId(userId, friendId) } doReturn chatId
            onBlocking { markAllMessagesAsRead(chatId, userId) } doReturn Result.failure(exception)
        }

        // Act
        val result = useCase(userId, friendId)

        // Assert
        assertTrue(result.isFailure)
        assertEquals("Network error", result.exceptionOrNull()?.message)
    }

    @Test
    fun `uses sorted chat ID format`() = runTest {
        // Arrange - friendId is lexicographically smaller than userId
        val userId = UserId("z_user")
        val friendId = UserId("a_friend")
        val sortedChatId = "a_friend_z_user"

        chatRepository.stub {
            on { generateChatId(userId, friendId) } doReturn sortedChatId
            onBlocking { markAllMessagesAsRead(sortedChatId, userId) } doReturn Result.success(Unit)
        }

        // Act
        val result = useCase(userId, friendId)

        // Assert
        assertTrue(result.isSuccess)
        verify(chatRepository).markAllMessagesAsRead(sortedChatId, userId)
    }
}
