package com.example.fyp.domain.history

import com.example.fyp.data.history.FirestoreHistoryRepository
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
        verify(repo).delete(userId, recordId)
    }

    @Test
    fun `invoke handles different user and record ids`() = runTest {
        // Arrange
        val testCases = listOf(
            Pair("user1", "record1"),
            Pair("user2", "record2"),
            Pair("user3", "record3")
        )

        // Act & Assert
        testCases.forEach { (userId, recordId) ->
            useCase(userId, recordId)
            verify(repo).delete(userId, recordId)
        }
    }
}
