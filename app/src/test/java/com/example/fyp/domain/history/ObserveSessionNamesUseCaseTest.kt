package com.example.fyp.domain.history

import com.example.fyp.data.history.FirestoreHistoryRepository
import com.example.fyp.model.HistorySession
import com.example.fyp.model.UserId
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.*

/**
 * Unit tests for ObserveSessionNamesUseCase.
 *
 * Tests:
 *  1. Returns map of sessionId to name from repository flow
 *  2. Returns empty map when no sessions exist
 *  3. Handles multiple sessions with distinct IDs
 */
@OptIn(ExperimentalCoroutinesApi::class)
class ObserveSessionNamesUseCaseTest {

    private lateinit var repo: FirestoreHistoryRepository
    private lateinit var useCase: ObserveSessionNamesUseCase

    @Before
    fun setup() {
        repo = mock()
        useCase = ObserveSessionNamesUseCase(repo)
    }

    @Test
    fun `returns map of sessionId to name`() = runTest {
        val sessions = listOf(
            HistorySession(sessionId = "s1", name = "Session One"),
            HistorySession(sessionId = "s2", name = "Session Two")
        )
        whenever(repo.listenSessions(UserId("user1"))).thenReturn(flowOf(sessions))

        val result = useCase("user1").first()

        assertEquals(2, result.size)
        assertEquals("Session One", result["s1"])
        assertEquals("Session Two", result["s2"])
    }

    @Test
    fun `returns empty map when no sessions`() = runTest {
        whenever(repo.listenSessions(UserId("user1"))).thenReturn(flowOf(emptyList()))

        val result = useCase("user1").first()

        assertTrue(result.isEmpty())
    }

    @Test
    fun `handles multiple sessions correctly`() = runTest {
        val sessions = listOf(
            HistorySession(sessionId = "a", name = "Alpha"),
            HistorySession(sessionId = "b", name = "Beta"),
            HistorySession(sessionId = "c", name = "Gamma")
        )
        whenever(repo.listenSessions(UserId("user1"))).thenReturn(flowOf(sessions))

        val result = useCase("user1").first()

        assertEquals(3, result.size)
        assertEquals("Alpha", result["a"])
        assertEquals("Beta", result["b"])
        assertEquals("Gamma", result["c"])
    }
}
