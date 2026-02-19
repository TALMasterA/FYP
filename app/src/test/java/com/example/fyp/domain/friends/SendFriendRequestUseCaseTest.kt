package com.example.fyp.domain.friends

import com.example.fyp.data.friends.FriendsRepository
import com.example.fyp.model.UserId
import com.example.fyp.model.friends.FriendRequest
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.stub
import org.mockito.kotlin.verify

/**
 * Unit tests for SendFriendRequestUseCase.
 * Tests sending friend requests with validation.
 */
class SendFriendRequestUseCaseTest {

    private lateinit var friendsRepository: FriendsRepository
    private lateinit var useCase: SendFriendRequestUseCase

    @Before
    fun setup() {
        friendsRepository = mock()
        useCase = SendFriendRequestUseCase(friendsRepository)
    }

    @Test
    fun `send friend request succeeds with valid user IDs`() = runTest {
        // Arrange
        val fromUserId = UserId("user1")
        val toUserId = UserId("user2")

        friendsRepository.stub {
            onBlocking { sendFriendRequest(fromUserId, toUserId) } doReturn Result.success(FriendRequest())
        }

        // Act
        val result = useCase(fromUserId, toUserId)

        // Assert
        assertTrue(result.isSuccess)
        verify(friendsRepository).sendFriendRequest(fromUserId, toUserId)
    }

    @Test
    fun `send friend request to self returns error`() = runTest {
        // Arrange
        val userId = UserId("user1")

        // Act
        val result = useCase(userId, userId)

        // Assert
        assertTrue(result.isFailure)
        assertEquals("Cannot send friend request to yourself", result.exceptionOrNull()?.message)
    }

    @Test
    fun `send friend request handles repository failure`() = runTest {
        // Arrange
        val fromUserId = UserId("user1")
        val toUserId = UserId("user2")
        val exception = Exception("Already friends")

        friendsRepository.stub {
            onBlocking { sendFriendRequest(fromUserId, toUserId) } doReturn Result.failure(exception)
        }

        // Act
        val result = useCase(fromUserId, toUserId)

        // Assert
        assertTrue(result.isFailure)
        assertEquals("Already friends", result.exceptionOrNull()?.message)
    }

    @Test
    fun `send multiple friend requests succeeds`() = runTest {
        // Arrange
        val fromUserId = UserId("user1")
        val toUserIds = listOf(UserId("user2"), UserId("user3"), UserId("user4"))

        toUserIds.forEach { toUserId ->
            friendsRepository.stub {
                onBlocking { sendFriendRequest(fromUserId, toUserId) } doReturn Result.success(FriendRequest())
            }
        }

        // Act & Assert
        toUserIds.forEach { toUserId ->
            val result = useCase(fromUserId, toUserId)
            assertTrue(result.isSuccess)
        }
    }
}
