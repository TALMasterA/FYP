package com.example.fyp.domain.speech

import com.example.fyp.data.repositories.TranslationRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class TranslateBatchUseCaseTest {

    private lateinit var repository: TranslationRepository
    private lateinit var useCase: TranslateBatchUseCase

    @Before
    fun setup() {
        repository = mock()
        useCase = TranslateBatchUseCase(repository)
    }

    @Test
    fun `invoke delegates to repository with correct parameters`() = runTest {
        // Arrange
        val texts = listOf("Hello", "World", "How are you?")
        val fromLanguage = "en"
        val toLanguage = "ja"
        val expected = Result.success(
            mapOf(
                "Hello" to "こんにちは",
                "World" to "世界",
                "How are you?" to "お元気ですか？"
            )
        )
        whenever(repository.translateBatch(texts, fromLanguage, toLanguage))
            .thenReturn(expected)

        // Act
        val result = useCase(texts, fromLanguage, toLanguage)

        // Assert
        assertEquals(expected, result)
        verify(repository).translateBatch(texts, fromLanguage, toLanguage)
    }

    @Test
    fun `invoke returns error when translation fails`() = runTest {
        // Arrange
        val texts = listOf("Hello")
        val fromLanguage = "en"
        val toLanguage = "invalid"
        val expected = Result.failure<Map<String, String>>(Exception("Translation failed"))
        whenever(repository.translateBatch(texts, fromLanguage, toLanguage))
            .thenReturn(expected)

        // Act
        val result = useCase(texts, fromLanguage, toLanguage)

        // Assert
        assertTrue(result.isFailure)
    }

    @Test
    fun `invoke handles empty list`() = runTest {
        // Arrange
        val texts = emptyList<String>()
        val fromLanguage = "en"
        val toLanguage = "ja"
        val expected = Result.success(emptyMap<String, String>())
        whenever(repository.translateBatch(texts, fromLanguage, toLanguage))
            .thenReturn(expected)

        // Act
        val result = useCase(texts, fromLanguage, toLanguage)

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(0, result.getOrNull()?.size)
    }

    @Test
    fun `invoke handles single text`() = runTest {
        // Arrange
        val texts = listOf("Hello")
        val fromLanguage = "en"
        val toLanguage = "zh-Hans"
        val expected = Result.success(mapOf("Hello" to "你好"))
        whenever(repository.translateBatch(texts, fromLanguage, toLanguage))
            .thenReturn(expected)

        // Act
        val result = useCase(texts, fromLanguage, toLanguage)

        // Assert
        assertTrue(result.isSuccess)
        assertEquals("你好", result.getOrNull()?.get("Hello"))
    }

    @Test
    fun `invoke handles large batch`() = runTest {
        // Arrange
        val texts = (1..100).map { "Text $it" }
        val fromLanguage = "en"
        val toLanguage = "fr"
        val translations = texts.associateWith { "Texte ${it.substringAfter(" ")}" }
        val expected = Result.success(translations)
        whenever(repository.translateBatch(texts, fromLanguage, toLanguage))
            .thenReturn(expected)

        // Act
        val result = useCase(texts, fromLanguage, toLanguage)

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(100, result.getOrNull()?.size)
    }
}
