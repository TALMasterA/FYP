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
 * Unit tests for RejectFriendRequestUseCase.
 * Tests rejecting friend requests.
 */
class RejectFriendRequestUseCaseTest {

    private lateinit var friendsRepository: FriendsRepository
    private lateinit var useCase: RejectFriendRequestUseCase

    @Before
    fun setup() {
        friendsRepository = mock()
        useCase = RejectFriendRequestUseCase(friendsRepository)
    }

    @Test
    fun `reject friend request succeeds`() = runTest {
        // Arrange
        val requestId = "request123"

        friendsRepository.stub {
            onBlocking { rejectFriendRequest(requestId) } doReturn Result.success(Unit)
        }

        // Act
        val result = useCase(requestId)

        // Assert
        assertTrue(result.isSuccess)
        verify(friendsRepository).rejectFriendRequest(requestId)
    }

    @Test
    fun `reject friend request handles repository failure`() = runTest {
        // Arrange
        val requestId = "request123"
        val exception = Exception("Request not found")

        friendsRepository.stub {
            onBlocking { rejectFriendRequest(requestId) } doReturn Result.failure(exception)
        }

        // Act
        val result = useCase(requestId)

        // Assert
        assertTrue(result.isFailure)
        assertEquals("Request not found", result.exceptionOrNull()?.message)
    }
}
