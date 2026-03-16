package com.example.fyp.domain.history

import com.example.fyp.data.history.FirestoreHistoryRepository
import com.example.fyp.model.SessionId
import com.example.fyp.model.UserId
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

class RenameSessionUseCaseTest {

    private lateinit var repo: FirestoreHistoryRepository
    private lateinit var useCase: RenameSessionUseCase

    @Before
    fun setup() {
        repo = mock()
        useCase = RenameSessionUseCase(repo)
    }

    @Test
    fun `invoke delegates to repository setSessionName`() = runBlocking {
        useCase(UserId("user1"), SessionId("sess1"), "My Conversation")

        verify(repo).setSessionName(UserId("user1"), SessionId("sess1"), "My Conversation")
    }

    @Test
    fun `invoke with empty name is allowed`() = runBlocking {
        useCase(UserId("user1"), SessionId("sess1"), "")

        verify(repo).setSessionName(UserId("user1"), SessionId("sess1"), "")
    }

    @Test
    fun `invoke with long name`() = runBlocking {
        val longName = "A".repeat(200)
        useCase(UserId("user1"), SessionId("sess1"), longName)

        verify(repo).setSessionName(UserId("user1"), SessionId("sess1"), longName)
    }
}
