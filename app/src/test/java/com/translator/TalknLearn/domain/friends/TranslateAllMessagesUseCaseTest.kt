package com.translator.TalknLearn.domain.friends

import com.translator.TalknLearn.data.repositories.TranslationRepository
import com.translator.TalknLearn.model.friends.FriendMessage
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.*

/**
 * Unit tests for TranslateAllMessagesUseCase.
 *
 * Tests:
 * 1. Only translates friend's messages (filters out current user's)
 * 2. Deduplicates identical message texts
 * 3. Empty messages returns empty map
 * 4. All messages from current user returns empty map
 * 5. Batch translation failure returns Result.failure
 * 6. Passes correct target language and auto-detect source
 */
class TranslateAllMessagesUseCaseTest {

    private lateinit var translationRepository: TranslationRepository
    private lateinit var useCase: TranslateAllMessagesUseCase

    @Before
    fun setup() {
        translationRepository = mock()
        useCase = TranslateAllMessagesUseCase(translationRepository)
    }

    // ── Filters out current user's messages ─────────────────────────

    @Test
    fun `only translates friend messages not current user`() = runTest {
        val messages = listOf(
            FriendMessage(messageId = "m1", senderId = "me", receiverId = "friend1", content = "Hello"),
            FriendMessage(messageId = "m2", senderId = "friend1", receiverId = "me", content = "こんにちは"),
            FriendMessage(messageId = "m3", senderId = "me", receiverId = "friend1", content = "How are you?"),
        )

        whenever(translationRepository.translateBatch(any(), any(), any()))
            .thenReturn(Result.success(mapOf("こんにちは" to "Hello")))

        val result = useCase(messages, "en", "me")

        assertTrue(result.isSuccess)
        // Only "こんにちは" should be translated (friend's message)
        verify(translationRepository).translateBatch(
            eq(listOf("こんにちは")),
            eq(""),
            eq("en")
        )
    }

    // ── Deduplicates identical texts ────────────────────────────────

    @Test
    fun `deduplicates identical message contents`() = runTest {
        val messages = listOf(
            FriendMessage(messageId = "m1", senderId = "friend1", receiverId = "me", content = "Hello"),
            FriendMessage(messageId = "m2", senderId = "friend1", receiverId = "me", content = "Hello"),
            FriendMessage(messageId = "m3", senderId = "friend1", receiverId = "me", content = "Goodbye"),
        )

        whenever(translationRepository.translateBatch(any(), any(), any()))
            .thenReturn(Result.success(mapOf("Hello" to "こんにちは", "Goodbye" to "さようなら")))

        val result = useCase(messages, "ja", "me")

        assertTrue(result.isSuccess)
        // Should only send 2 unique texts, not 3
        verify(translationRepository).translateBatch(
            eq(listOf("Hello", "Goodbye")),
            eq(""),
            eq("ja")
        )
    }

    // ── Empty messages ──────────────────────────────────────────────

    @Test
    fun `empty messages returns success with empty map`() = runTest {
        val result = useCase(emptyList(), "en", "me")

        assertTrue(result.isSuccess)
        assertTrue(result.getOrNull()!!.isEmpty())
        verifyNoInteractions(translationRepository)
    }

    // ── All messages from current user ──────────────────────────────

    @Test
    fun `all messages from current user returns empty map`() = runTest {
        val messages = listOf(
            FriendMessage(messageId = "m1", senderId = "me", receiverId = "friend1", content = "Hello"),
            FriendMessage(messageId = "m2", senderId = "me", receiverId = "friend1", content = "World"),
        )

        val result = useCase(messages, "ja", "me")

        assertTrue(result.isSuccess)
        assertTrue(result.getOrNull()!!.isEmpty())
        verifyNoInteractions(translationRepository)
    }

    // ── Translation failure ─────────────────────────────────────────

    @Test
    fun `translation failure returns Result failure`() = runTest {
        val messages = listOf(
            FriendMessage(messageId = "m1", senderId = "friend1", receiverId = "me", content = "Hello"),
        )

        whenever(translationRepository.translateBatch(any(), any(), any()))
            .thenThrow(RuntimeException("Network error"))

        val result = useCase(messages, "ja", "me")

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message?.contains("Network error") == true)
    }

    // ── Passes correct parameters ───────────────────────────────────

    @Test
    fun `passes correct target language and empty source for auto-detect`() = runTest {
        val messages = listOf(
            FriendMessage(messageId = "m1", senderId = "friend1", receiverId = "me", content = "Bonjour"),
        )

        whenever(translationRepository.translateBatch(any(), any(), any()))
            .thenReturn(Result.success(mapOf("Bonjour" to "Hello")))

        useCase(messages, "en-US", "me")

        verify(translationRepository).translateBatch(
            texts = eq(listOf("Bonjour")),
            fromLanguage = eq(""), // auto-detect
            toLanguage = eq("en-US")
        )
    }
}
