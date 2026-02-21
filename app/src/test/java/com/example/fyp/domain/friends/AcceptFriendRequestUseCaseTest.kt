package com.example.fyp.domain.friends

import com.example.fyp.data.friends.FriendsRepository
import com.example.fyp.model.UserId
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

/**
 * Unit tests for AcceptFriendRequestUseCase.
 * Tests accepting friend requests with validation.
 */
class AcceptFriendRequestUseCaseTest {

    private lateinit var friendsRepository: FriendsRepository
    private lateinit var useCase: AcceptFriendRequestUseCase

    @Before
    fun setup() {
        friendsRepository = mock()
        useCase = AcceptFriendRequestUseCase(friendsRepository)
    }

    @Test
    fun `accept friend request succeeds`() = runTest {
        // Arrange
        val requestId = "request123"
        val currentUserId = UserId("user1")
        val friendUserId = UserId("user2")
        doAnswer { Result.success(Unit) }.whenever(friendsRepository)
            .acceptFriendRequest(any(), any(), any())

        // Act
        val result = useCase(requestId, currentUserId, friendUserId)

        // Assert
        assertTrue(result.isSuccess)
        verify(friendsRepository).acceptFriendRequest(requestId, currentUserId, friendUserId)
    }

    @Test
    fun `accept friend request handles repository failure`() = runTest {
        // Arrange
        val requestId = "request123"
        val currentUserId = UserId("user1")
        val friendUserId = UserId("user2")
        val exception = Exception("Request not found")
        doAnswer { Result.failure<Unit>(exception) }.whenever(friendsRepository)
            .acceptFriendRequest(any(), any(), any())

        // Act
        val result = useCase(requestId, currentUserId, friendUserId)

        // Assert
        assertTrue(result.isFailure)
        assertEquals("Request not found", result.exceptionOrNull()?.message)
    }

    @Test
    fun `accept multiple friend requests succeeds`() = runTest {
        // Arrange
        val currentUserId = UserId("user1")
        val requests = listOf(
            Triple("req1", currentUserId, UserId("user2")),
            Triple("req2", currentUserId, UserId("user3")),
            Triple("req3", currentUserId, UserId("user4"))
        )
        doAnswer { Result.success(Unit) }.whenever(friendsRepository)
            .acceptFriendRequest(any(), any(), any())

        // Act & Assert
        requests.forEach { (requestId, userId, friendId) ->
            val result = useCase(requestId, userId, friendId)
            assertTrue(result.isSuccess)
        }
    }
}
