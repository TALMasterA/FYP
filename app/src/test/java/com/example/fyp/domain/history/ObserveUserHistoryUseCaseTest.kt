package com.example.fyp.domain.history

import com.example.fyp.data.history.FirestoreHistoryRepository
import com.example.fyp.model.TranslationRecord
import com.example.fyp.model.UserId
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class ObserveUserHistoryUseCaseTest {

    private lateinit var repo: FirestoreHistoryRepository
    private lateinit var useCase: ObserveUserHistoryUseCase

    @Before
    fun setup() {
        repo = mock()
        useCase = ObserveUserHistoryUseCase(repo)
    }

    @Test
    fun `invoke returns flow from repository`() = runTest {
        val records = listOf(
            TranslationRecord(sourceText = "Hello", targetText = "Hola"),
            TranslationRecord(sourceText = "World", targetText = "Mundo")
        )
        whenever(repo.getHistory(UserId("user1"))).thenReturn(flowOf(records))

        val result = useCase(UserId("user1")).first()

        assertEquals(2, result.size)
        assertEquals("Hello", result[0].sourceText)
        assertEquals("World", result[1].sourceText)
    }

    @Test
    fun `invoke returns empty list when no history`() = runTest {
        whenever(repo.getHistory(UserId("user1"))).thenReturn(flowOf(emptyList()))

        val result = useCase(UserId("user1")).first()

        assertTrue(result.isEmpty())
    }

    @Test
    fun `invoke with different users returns different flows`() = runTest {
        val user1Records = listOf(TranslationRecord(sourceText = "A"))
        val user2Records = listOf(TranslationRecord(sourceText = "B"), TranslationRecord(sourceText = "C"))

        whenever(repo.getHistory(UserId("user1"))).thenReturn(flowOf(user1Records))
        whenever(repo.getHistory(UserId("user2"))).thenReturn(flowOf(user2Records))

        assertEquals(1, useCase(UserId("user1")).first().size)
        assertEquals(2, useCase(UserId("user2")).first().size)
    }
}
