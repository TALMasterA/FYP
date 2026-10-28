package com.translator.TalknLearn.domain.history

import com.translator.TalknLearn.data.history.FirestoreHistoryRepository
import com.translator.TalknLearn.model.SessionId
import com.translator.TalknLearn.model.UserId
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

class DeleteSessionUseCaseTest {

    private lateinit var repo: FirestoreHistoryRepository
    private lateinit var useCase: DeleteSessionUseCase

    @Before
    fun setup() {
        repo = mock()
        useCase = DeleteSessionUseCase(repo)
    }

    @Test
    fun `invoke delegates to repository deleteSession`() = runBlocking {
        useCase("user123", "session456")

        verify(repo).deleteSession(UserId("user123"), SessionId("session456"))
    }

    @Test
    fun `invoke with different userId and sessionId`() = runBlocking {
        useCase("anotherUser", "anotherSession")

        verify(repo).deleteSession(UserId("anotherUser"), SessionId("anotherSession"))
    }

    @Test(expected = IllegalArgumentException::class)
    fun `invoke with blank userId throws`() = runBlocking {
        useCase("", "session1")
    }

    @Test(expected = IllegalArgumentException::class)
    fun `invoke with blank sessionId throws`() = runBlocking {
        useCase("user1", "")
    }
}
