package com.example.fyp.domain.friends

import com.example.fyp.data.friends.SharingRepository
import com.example.fyp.model.UserId
import com.example.fyp.model.friends.SharedItem
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

/**
 * Unit tests for ShareWordUseCase.
 * Tests sharing word bank words between friends.
 */
class ShareWordUseCaseTest {

    private lateinit var sharingRepository: SharingRepository
    private lateinit var useCase: ShareWordUseCase

    @Before
    fun setup() {
        sharingRepository = mock()
        useCase = ShareWordUseCase(sharingRepository)
    }

    @Test
    fun `share word succeeds with valid parameters`() = runTest {
        // Arrange
        val fromUserId = UserId("user1")
        val toUserId = UserId("user2")
        val sharedItem = SharedItem(itemId = "item1", fromUserId = "user1", toUserId = "user2")
        val wordData = mapOf<String, Any>(
            "sourceText" to "hello",
            "targetText" to "hola",
            "sourceLang" to "en-US",
            "targetLang" to "es-ES",
            "notes" to ""
        )

        whenever(sharingRepository.shareWord(fromUserId, "sender_user", toUserId, wordData))
            .thenReturn(Result.success(sharedItem))

        // Act
        val result = useCase(
            fromUserId = fromUserId,
            fromUsername = "sender_user",
            toUserId = toUserId,
            sourceText = "hello",
            targetText = "hola",
            sourceLang = "en-US",
            targetLang = "es-ES"
        )

        // Assert
        assertTrue(result.isSuccess)
        assertEquals("item1", result.getOrNull()?.itemId)
    }

    @Test
    fun `share word includes notes when provided`() = runTest {
        // Arrange
        val fromUserId = UserId("user1")
        val toUserId = UserId("user2")
        val sharedItem = SharedItem(itemId = "item2", fromUserId = "user1", toUserId = "user2")
        val wordData = mapOf<String, Any>(
            "sourceText" to "cat",
            "targetText" to "gato",
            "sourceLang" to "en-US",
            "targetLang" to "es-ES",
            "notes" to "Common pet"
        )

        whenever(sharingRepository.shareWord(fromUserId, "sender", toUserId, wordData))
            .thenReturn(Result.success(sharedItem))

        // Act
        val result = useCase(
            fromUserId = fromUserId,
            fromUsername = "sender",
            toUserId = toUserId,
            sourceText = "cat",
            targetText = "gato",
            sourceLang = "en-US",
            targetLang = "es-ES",
            notes = "Common pet"
        )

        // Assert
        assertTrue(result.isSuccess)
    }

    @Test
    fun `share word handles repository failure`() = runTest {
        // Arrange
        val fromUserId = UserId("user1")
        val toUserId = UserId("user2")
        val exception = Exception("Permission denied")
        val wordData = mapOf<String, Any>(
            "sourceText" to "hello",
            "targetText" to "hola",
            "sourceLang" to "en-US",
            "targetLang" to "es-ES",
            "notes" to ""
        )

        whenever(sharingRepository.shareWord(fromUserId, "sender", toUserId, wordData))
            .thenReturn(Result.failure(exception))

        // Act
        val result = useCase(
            fromUserId = fromUserId,
            fromUsername = "sender",
            toUserId = toUserId,
            sourceText = "hello",
            targetText = "hola",
            sourceLang = "en-US",
            targetLang = "es-ES"
        )

        // Assert
        assertTrue(result.isFailure)
        assertEquals("Permission denied", result.exceptionOrNull()?.message)
    }
}
