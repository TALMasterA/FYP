package com.example.fyp.domain.history

import com.example.fyp.data.history.FirestoreHistoryRepository
import com.example.fyp.model.SessionId
import com.example.fyp.model.UserId
import com.example.fyp.model.LanguageCode
import com.example.fyp.model.RecordId
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

/**
 * Unit tests for history domain use cases:
 * - DeleteSessionUseCase
 * - RenameSessionUseCase
 * - DeleteHistoryRecordUseCase
 */
class HistoryUseCasesTest {

    private lateinit var repo: FirestoreHistoryRepository

    @Before
    fun setup() {
        repo = mock()
    }

    // ── DeleteSessionUseCase ────────────────────────────────────────

    @Test
    fun `DeleteSession delegates to repository with correct IDs`() = runTest {
        val useCase = DeleteSessionUseCase(repo)
        useCase("user1", "session1")

        verify(repo).deleteSession(UserId("user1"), SessionId("session1"))
    }

    @Test
    fun `DeleteSession passes different user and session IDs`() = runTest {
        val useCase = DeleteSessionUseCase(repo)
        useCase("abc123", "sess-xyz")

        verify(repo).deleteSession(UserId("abc123"), SessionId("sess-xyz"))
    }

    // ── RenameSessionUseCase ────────────────────────────────────────

    @Test
    fun `RenameSession delegates to repository with correct parameters`() = runTest {
        val useCase = RenameSessionUseCase(repo)
        useCase(UserId("user1"), SessionId("session1"), "My Travel Session")

        verify(repo).setSessionName(UserId("user1"), SessionId("session1"), "My Travel Session")
    }

    @Test
    fun `RenameSession allows empty name`() = runTest {
        val useCase = RenameSessionUseCase(repo)
        useCase(UserId("user1"), SessionId("session1"), "")

        verify(repo).setSessionName(UserId("user1"), SessionId("session1"), "")
    }

    // ── DeleteHistoryRecordUseCase ──────────────────────────────────

    @Test
    fun `DeleteHistoryRecord delegates without language codes`() = runTest {
        val useCase = DeleteHistoryRecordUseCase(repo)
        useCase("user1", "record1")

        verify(repo).delete(
            userId = UserId("user1"),
            recordId = RecordId("record1"),
            knownSourceLang = null,
            knownTargetLang = null
        )
    }

    @Test
    fun `DeleteHistoryRecord passes language codes when provided`() = runTest {
        val useCase = DeleteHistoryRecordUseCase(repo)
        useCase("user1", "record1", sourceLang = "en-US", targetLang = "ja")

        verify(repo).delete(
            userId = UserId("user1"),
            recordId = RecordId("record1"),
            knownSourceLang = LanguageCode("en-US"),
            knownTargetLang = LanguageCode("ja")
        )
    }

    @Test
    fun `DeleteHistoryRecord ignores blank language codes`() = runTest {
        val useCase = DeleteHistoryRecordUseCase(repo)
        useCase("user1", "record1", sourceLang = "", targetLang = "  ")

        verify(repo).delete(
            userId = UserId("user1"),
            recordId = RecordId("record1"),
            knownSourceLang = null,
            knownTargetLang = null
        )
    }

    @Test
    fun `DeleteHistoryRecord passes partial language codes`() = runTest {
        val useCase = DeleteHistoryRecordUseCase(repo)
        useCase("user1", "record1", sourceLang = "en-US", targetLang = null)

        verify(repo).delete(
            userId = UserId("user1"),
            recordId = RecordId("record1"),
            knownSourceLang = LanguageCode("en-US"),
            knownTargetLang = null
        )
    }

}
