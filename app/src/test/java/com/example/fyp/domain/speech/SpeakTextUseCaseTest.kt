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

class SpeakTextUseCaseTest {

    private lateinit var speechRepository: SpeechRepository
    private lateinit var useCase: SpeakTextUseCase

    @Before
    fun setup() {
        speechRepository = mock()
        useCase = SpeakTextUseCase(speechRepository)
    }

    @Test
    fun `invoke delegates to speech repository with correct parameters`() = runTest {
        // Arrange
        val text = "Hello world"
        val languageCode = "en-US"
        val voiceName = "en-US-JennyNeural"
        val expected = SpeechResult.Success("Speech completed")
        whenever(speechRepository.speak(text, languageCode, voiceName))
            .thenReturn(expected)

        // Act
        val result = useCase(text, languageCode, voiceName)

        // Assert
        assertEquals(expected, result)
        verify(speechRepository).speak(text, languageCode, voiceName)
    }

    @Test
    fun `invoke works with null voice name`() = runTest {
        // Arrange
        val text = "Hello world"
        val languageCode = "en-US"
        val expected = SpeechResult.Success("Speech completed")
        whenever(speechRepository.speak(text, languageCode, null))
            .thenReturn(expected)

        // Act
        val result = useCase(text, languageCode, null)

        // Assert
        assertEquals(expected, result)
        verify(speechRepository).speak(text, languageCode, null)
    }

    @Test
    fun `invoke returns error when speech fails`() = runTest {
        // Arrange
        val text = "Hello"
        val languageCode = "en-US"
        val expected = SpeechResult.Error("Speech synthesis failed")
        whenever(speechRepository.speak(text, languageCode, null))
            .thenReturn(expected)

        // Act
        val result = useCase(text, languageCode)

        // Assert
        assertEquals(expected, result)
    }

    @Test
    fun `invoke handles empty text`() = runTest {
        // Arrange
        val text = ""
        val languageCode = "en-US"
        val expected = SpeechResult.Success("No speech")
        whenever(speechRepository.speak(text, languageCode, null))
            .thenReturn(expected)

        // Act
        val result = useCase(text, languageCode)

        // Assert
        assertEquals(expected, result)
    }

    @Test
    fun `invoke handles different languages and voices`() = runTest {
        // Arrange
        val testCases = listOf(
            Triple("Hello", "en-US", "en-US-JennyNeural"),
            Triple("你好", "zh-CN", "zh-CN-XiaoxiaoNeural"),
            Triple("こんにちは", "ja-JP", "ja-JP-NanamiNeural"),
            Triple("안녕하세요", "ko-KR", "ko-KR-SunHiNeural")
        )

        testCases.forEach { (text, lang, voice) ->
            val expected = SpeechResult.Success("Speech completed")
            whenever(speechRepository.speak(text, lang, voice))
                .thenReturn(expected)

            // Act
            val result = useCase(text, lang, voice)

            // Assert
            assertEquals(expected, result)
        }
    }
}
