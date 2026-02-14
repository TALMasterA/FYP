package com.example.fyp.domain.speech

import com.example.fyp.data.repositories.TranslationRepository
import com.example.fyp.model.SpeechResult
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class TranslateTextUseCaseTest {

    private lateinit var translationRepository: TranslationRepository
    private lateinit var useCase: TranslateTextUseCase

    @Before
    fun setup() {
        translationRepository = mock()
        useCase = TranslateTextUseCase(translationRepository)
    }

    @Test
    fun `invoke delegates to translation repository with correct parameters`() = runTest {
        // Arrange
        val text = "Hello"
        val fromLanguage = "en-US"
        val toLanguage = "zh-CN"
        val expected = SpeechResult.Success("你好")
        whenever(translationRepository.translate(text, fromLanguage, toLanguage))
            .thenReturn(expected)

        // Act
        val result = useCase(text, fromLanguage, toLanguage)

        // Assert
        assertEquals(expected, result)
        verify(translationRepository).translate(text, fromLanguage, toLanguage)
    }

    @Test
    fun `invoke returns error when translation fails`() = runTest {
        // Arrange
        val text = "Hello"
        val fromLanguage = "en-US"
        val toLanguage = "invalid-lang"
        val expected = SpeechResult.Error("Translation failed")
        whenever(translationRepository.translate(text, fromLanguage, toLanguage))
            .thenReturn(expected)

        // Act
        val result = useCase(text, fromLanguage, toLanguage)

        // Assert
        assertEquals(expected, result)
    }

    @Test
    fun `invoke handles empty text`() = runTest {
        // Arrange
        val text = ""
        val fromLanguage = "en-US"
        val toLanguage = "zh-CN"
        val expected = SpeechResult.Success("")
        whenever(translationRepository.translate(text, fromLanguage, toLanguage))
            .thenReturn(expected)

        // Act
        val result = useCase(text, fromLanguage, toLanguage)

        // Assert
        assertEquals(expected, result)
    }

    @Test
    fun `invoke handles long text`() = runTest {
        // Arrange
        val text = "A".repeat(1000)
        val fromLanguage = "en-US"
        val toLanguage = "zh-CN"
        val expected = SpeechResult.Success("Translation result")
        whenever(translationRepository.translate(text, fromLanguage, toLanguage))
            .thenReturn(expected)

        // Act
        val result = useCase(text, fromLanguage, toLanguage)

        // Assert
        assertEquals(expected, result)
    }
}
