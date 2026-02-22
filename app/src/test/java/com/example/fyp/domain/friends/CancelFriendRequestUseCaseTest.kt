package com.example.fyp.domain.friends

import com.example.fyp.data.friends.FriendsRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.stub
import org.mockito.kotlin.verify

/**
 * Unit tests for CancelFriendRequestUseCase.
 * Tests friend request cancellation delegation.
 */
class CancelFriendRequestUseCaseTest {

    private lateinit var friendsRepository: FriendsRepository
    private lateinit var useCase: CancelFriendRequestUseCase

    @Before
    fun setup() {
        friendsRepository = mock()
        useCase = CancelFriendRequestUseCase(friendsRepository)
    }

    @Test
    fun `successful cancellation returns success`() = runTest {
        // Arrange
        val requestId = "req123"

        friendsRepository.stub {
            onBlocking { cancelFriendRequest(requestId) } doReturn Result.success(Unit)
        }

        // Act
        val result = useCase(requestId)

        // Assert
        assertTrue(result.isSuccess)
        verify(friendsRepository).cancelFriendRequest(requestId)
    }

    @Test
    fun `failure propagates error from repository`() = runTest {
        // Arrange
        val requestId = "req456"
        val exception = Exception("Request not found")

        friendsRepository.stub {
            onBlocking { cancelFriendRequest(requestId) } doReturn Result.failure(exception)
        }

        // Act
        val result = useCase(requestId)

        // Assert
        assertTrue(result.isFailure)
        assertEquals("Request not found", result.exceptionOrNull()?.message)
        verify(friendsRepository).cancelFriendRequest(requestId)
    }
}
