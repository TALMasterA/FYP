package com.example.fyp.domain.history

import com.example.fyp.data.history.FirestoreHistoryRepository
import com.example.fyp.model.RecordId
import com.example.fyp.model.UserId
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

class DeleteHistoryRecordUseCaseTest {

    private lateinit var repo: FirestoreHistoryRepository
    private lateinit var useCase: DeleteHistoryRecordUseCase

    @Before
    fun setup() {
        repo = mock()
        useCase = DeleteHistoryRecordUseCase(repo)
    }

    @Test
    fun `invoke deletes record from repository`() = runTest {
        // Arrange
        val userId = "user123"
        val recordId = "record456"

        // Act
        useCase(userId, recordId)

        // Assert
        verify(repo).delete(UserId(userId), RecordId(recordId))
    }

    @Test
    fun `invoke handles different user and record ids`() = runTest {
        // Arrange
        val testCases = listOf(
            "user1" to "record1",
            "user2" to "record2",
            "user3" to "record3"
        )

        // Act & Assert
        testCases.forEach { (userId, recordId) ->
            useCase(userId, recordId)
            verify(repo).delete(UserId(userId), RecordId(recordId))
        }
    }
}
