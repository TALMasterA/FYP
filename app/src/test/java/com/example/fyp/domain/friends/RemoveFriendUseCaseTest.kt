package com.example.fyp.domain.friends

import com.example.fyp.data.friends.FriendsRepository
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
 * Unit tests for RemoveFriendUseCase.
 * Tests removing friends from friends list.
 */
class RemoveFriendUseCaseTest {

    private lateinit var friendsRepository: FriendsRepository
    private lateinit var useCase: RemoveFriendUseCase

    @Before
    fun setup() {
        friendsRepository = mock()
        useCase = RemoveFriendUseCase(friendsRepository)
    }

    @Test
    fun `remove friend succeeds`() = runTest {
        // Arrange
        val currentUserId = UserId("user1")
        val friendUserId = UserId("user2")

        friendsRepository.stub {
            onBlocking { removeFriend(currentUserId, friendUserId) } doReturn Result.success(Unit)
        }

        // Act
        val result = useCase(currentUserId, friendUserId)

        // Assert
        assertTrue(result.isSuccess)
        verify(friendsRepository).removeFriend(currentUserId, friendUserId)
    }

    @Test
    fun `remove friend handles repository failure`() = runTest {
        // Arrange
        val currentUserId = UserId("user1")
        val friendUserId = UserId("user2")
        val exception = Exception("Friendship not found")

        friendsRepository.stub {
            onBlocking { removeFriend(currentUserId, friendUserId) } doReturn Result.failure(exception)
        }

        // Act
        val result = useCase(currentUserId, friendUserId)

        // Assert
        assertTrue(result.isFailure)
        assertEquals("Friendship not found", result.exceptionOrNull()?.message)
    }

    @Test
    fun `remove self as friend returns error`() = runTest {
        // Arrange
        val userId = UserId("user1")

        // Act - trying to remove self
        friendsRepository.stub {
            onBlocking { removeFriend(userId, userId) } doReturn Result.failure(Exception("Cannot remove yourself"))
        }
        val result = useCase(userId, userId)

        // Assert
        assertTrue(result.isFailure)
    }
}
