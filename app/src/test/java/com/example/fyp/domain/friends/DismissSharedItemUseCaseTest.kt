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
 * Unit tests for DismissSharedItemUseCase.
 * Verifies dismiss (and delete) path delegates correctly to the repository.
 */
class DismissSharedItemUseCaseTest {

    private lateinit var sharingRepository: SharingRepository
    private lateinit var useCase: DismissSharedItemUseCase

    @Before
    fun setup() {
        sharingRepository = mock()
        useCase = DismissSharedItemUseCase(sharingRepository)
    }

    @Test
    fun `dismiss shared item succeeds`() = runTest {
        // Arrange
        val itemId = "item123"
        val userId = UserId("user1")

        sharingRepository.stub {
            onBlocking { dismissSharedItem(itemId, userId) } doReturn Result.success(Unit)
        }

        // Act
        val result = useCase(itemId, userId)

        // Assert
        assertTrue(result.isSuccess)
        verify(sharingRepository).dismissSharedItem(itemId, userId)
    }

    @Test
    fun `dismiss shared item handles repository failure`() = runTest {
        // Arrange
        val itemId = "item123"
        val userId = UserId("user1")
        val exception = Exception("Item not found")

        sharingRepository.stub {
            onBlocking { dismissSharedItem(itemId, userId) } doReturn Result.failure(exception)
        }

        // Act
        val result = useCase(itemId, userId)

        // Assert
        assertTrue(result.isFailure)
        assertEquals("Item not found", result.exceptionOrNull()?.message)
    }

    @Test
    fun `dismiss learning material uses same dismiss path as word`() = runTest {
        // Arrange - learning materials and words all share the same dismiss endpoint
        val learningSheetItemId = "learning_sheet_item_1"
        val quizItemId = "quiz_item_2"
        val userId = UserId("user1")

        sharingRepository.stub {
            onBlocking { dismissSharedItem(learningSheetItemId, userId) } doReturn Result.success(Unit)
            onBlocking { dismissSharedItem(quizItemId, userId) } doReturn Result.success(Unit)
        }

        // Act
        val sheetResult = useCase(learningSheetItemId, userId)
        val quizResult = useCase(quizItemId, userId)

        // Assert
        assertTrue("Learning sheet dismiss should succeed", sheetResult.isSuccess)
        assertTrue("Quiz dismiss should succeed", quizResult.isSuccess)
        verify(sharingRepository).dismissSharedItem(learningSheetItemId, userId)
        verify(sharingRepository).dismissSharedItem(quizItemId, userId)
    }

    @Test
    fun `dismiss already dismissed item fails gracefully`() = runTest {
        // Arrange
        val itemId = "already_dismissed"
        val userId = UserId("user1")
        val exception = Exception("Item already dismissed")

        sharingRepository.stub {
            onBlocking { dismissSharedItem(itemId, userId) } doReturn Result.failure(exception)
        }

        // Act
        val result = useCase(itemId, userId)

        // Assert
        assertTrue(result.isFailure)
    }
}

