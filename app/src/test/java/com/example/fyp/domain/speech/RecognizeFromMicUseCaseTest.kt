package com.example.fyp.domain.speech

import com.example.fyp.data.repositories.SpeechRepository
import com.example.fyp.model.SpeechResult
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class RecognizeFromMicUseCaseTest {

    private lateinit var speechRepository: SpeechRepository
    private lateinit var useCase: RecognizeFromMicUseCase

    @Before
    fun setup() {
        speechRepository = mock()
        useCase = RecognizeFromMicUseCase(speechRepository)
    }

    @Test
    fun `invoke delegates to speech repository with correct language code`() = runTest {
        // Arrange
        val languageCode = "en-US"
        val expected = SpeechResult.Success("Hello world")
        whenever(speechRepository.recognizeOnce(languageCode))
            .thenReturn(expected)

        // Act
        val result = useCase(languageCode)

        // Assert
        assertEquals(expected, result)
        verify(speechRepository).recognizeOnce(languageCode)
    }

    @Test
    fun `invoke returns error when recognition fails`() = runTest {
        // Arrange
        val languageCode = "en-US"
        val expected = SpeechResult.Error("Recognition failed")
        whenever(speechRepository.recognizeOnce(languageCode))
            .thenReturn(expected)

        // Act
        val result = useCase(languageCode)

        // Assert
        assertEquals(expected, result)
    }

    @Test
    fun `invoke handles different language codes`() = runTest {
        // Arrange
        val languageCodes = listOf("en-US", "zh-CN", "ja-JP", "ko-KR", "fr-FR")
        
        languageCodes.forEach { code ->
            val expected = SpeechResult.Success("Text in $code")
            whenever(speechRepository.recognizeOnce(code))
                .thenReturn(expected)

            // Act
            val result = useCase(code)

            // Assert
            assertEquals(expected, result)
        }
    }
}
