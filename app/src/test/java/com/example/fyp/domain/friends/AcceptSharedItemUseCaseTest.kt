package com.example.fyp.domain.friends

import com.example.fyp.data.friends.SharingRepository
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
 * Unit tests for AcceptSharedItemUseCase.
 * Verifies that accepting a shared item delegates correctly to the repository.
 */
class AcceptSharedItemUseCaseTest {

    private lateinit var sharingRepository: SharingRepository
    private lateinit var useCase: AcceptSharedItemUseCase

    @Before
    fun setup() {
        sharingRepository = mock()
        useCase = AcceptSharedItemUseCase(sharingRepository)
    }

    @Test
    fun `accept shared item succeeds`() = runTest {
        // Arrange
        val itemId = "item123"
        val userId = UserId("user1")

        sharingRepository.stub {
            onBlocking { acceptSharedItem(itemId, userId) } doReturn Result.success(Unit)
        }

        // Act
        val result = useCase(itemId, userId)

        // Assert
        assertTrue(result.isSuccess)
        verify(sharingRepository).acceptSharedItem(itemId, userId)
    }

    @Test
    fun `accept shared item handles repository failure`() = runTest {
        // Arrange
        val itemId = "item123"
        val userId = UserId("user1")
        val exception = Exception("Item not found")

        sharingRepository.stub {
            onBlocking { acceptSharedItem(itemId, userId) } doReturn Result.failure(exception)
        }

        // Act
        val result = useCase(itemId, userId)

        // Assert
        assertTrue(result.isFailure)
        assertEquals("Item not found", result.exceptionOrNull()?.message)
    }

    @Test
    fun `accept shared item with different user ids`() = runTest {
        // Arrange
        val items = listOf(
            Pair("word_item_1", UserId("user1")),
            Pair("sheet_item_2", UserId("user2")),
            Pair("quiz_item_3", UserId("user3"))
        )

        items.forEach { (itemId, userId) ->
            sharingRepository.stub {
                onBlocking { acceptSharedItem(itemId, userId) } doReturn Result.success(Unit)
            }
        }

        // Act & Assert
        items.forEach { (itemId, userId) ->
            val result = useCase(itemId, userId)
            assertTrue("Expected success for item $itemId", result.isSuccess)
        }
    }

    @Test
    fun `accept shared item with authorization error`() = runTest {
        // Arrange
        val itemId = "item456"
        val userId = UserId("wrong_user")
        val exception = IllegalArgumentException("Not authorized")

        sharingRepository.stub {
            onBlocking { acceptSharedItem(itemId, userId) } doReturn Result.failure(exception)
        }

        // Act
        val result = useCase(itemId, userId)

        // Assert
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IllegalArgumentException)
        assertEquals("Not authorized", result.exceptionOrNull()?.message)
    }
}

