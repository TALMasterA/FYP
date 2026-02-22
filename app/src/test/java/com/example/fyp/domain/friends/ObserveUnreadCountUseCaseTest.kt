package com.example.fyp.domain.friends

import com.example.fyp.data.friends.ChatRepository
import com.example.fyp.model.UserId
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import org.mockito.kotlin.mock
import org.mockito.kotlin.stub
import org.mockito.kotlin.verify

/**
 * Unit tests for ObserveUnreadCountUseCase.
 * Tests fetching the total unread message count for a user.
 */
class ObserveUnreadCountUseCaseTest {

    private lateinit var chatRepository: ChatRepository
    private lateinit var useCase: ObserveUnreadCountUseCase

    @Before
    fun setup() {
        chatRepository = mock()
        useCase = ObserveUnreadCountUseCase(chatRepository)
    }

    @Test
    fun `returns total unread count from repository`() = runTest {
        // Arrange
        val userId = UserId("user1")
        chatRepository.stub {
            onBlocking { getTotalUnreadCount(userId) }.thenReturn(5)
        }

        // Act
        val count = useCase(userId)

        // Assert
        assertEquals(5, count)
        verify(chatRepository).getTotalUnreadCount(userId)
    }

    @Test
    fun `returns zero when no unread messages`() = runTest {
        // Arrange
        val userId = UserId("user1")
        chatRepository.stub {
            onBlocking { getTotalUnreadCount(userId) }.thenReturn(0)
        }

        // Act
        val count = useCase(userId)

        // Assert
        assertEquals(0, count)
    }

    @Test
    fun `returns count from correct user`() = runTest {
        // Arrange
        val user1 = UserId("user1")
        val user2 = UserId("user2")
        chatRepository.stub {
            onBlocking { getTotalUnreadCount(user1) }.thenReturn(3)
            onBlocking { getTotalUnreadCount(user2) }.thenReturn(10)
        }

        // Act & Assert
        assertEquals(3, useCase(user1))
        assertEquals(10, useCase(user2))
    }
}
