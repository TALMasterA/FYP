package com.example.fyp.domain.speech

import com.example.fyp.data.repositories.AutoDetectRecognitionResult
import com.example.fyp.data.repositories.SpeechRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

/**
 * Unit tests for RecognizeWithAutoDetectUseCase.
 */
class RecognizeWithAutoDetectUseCaseTest {

    private lateinit var speechRepository: SpeechRepository
    private lateinit var useCase: RecognizeWithAutoDetectUseCase

    @Before
    fun setup() {
        speechRepository = mock()
        useCase = RecognizeWithAutoDetectUseCase(speechRepository)
    }

    @Test
    fun `invoke delegates to repository with candidate languages`() = runTest {
        val candidates = listOf("en-US", "ja", "zh-CN")
        val result = AutoDetectRecognitionResult(text = "Hello", detectedLanguage = "en-US")
        whenever(speechRepository.recognizeOnceWithAutoDetect(candidates))
            .thenReturn(Result.success(result))

        val actual = useCase(candidates)

        assertTrue(actual.isSuccess)
        assertEquals("Hello", actual.getOrNull()?.text)
        assertEquals("en-US", actual.getOrNull()?.detectedLanguage)
        verify(speechRepository).recognizeOnceWithAutoDetect(candidates)
    }

    @Test
    fun `invoke returns failure when repository fails`() = runTest {
        val candidates = listOf("en-US", "ja")
        whenever(speechRepository.recognizeOnceWithAutoDetect(candidates))
            .thenReturn(Result.failure(RuntimeException("Microphone unavailable")))

        val actual = useCase(candidates)

        assertTrue(actual.isFailure)
        assertEquals("Microphone unavailable", actual.exceptionOrNull()?.message)
    }

    @Test
    fun `invoke works with single candidate language`() = runTest {
        val candidates = listOf("en-US")
        val result = AutoDetectRecognitionResult(text = "Test", detectedLanguage = "en-US")
        whenever(speechRepository.recognizeOnceWithAutoDetect(candidates))
            .thenReturn(Result.success(result))

        val actual = useCase(candidates)

        assertTrue(actual.isSuccess)
        assertEquals("Test", actual.getOrNull()?.text)
    }

    @Test
    fun `invoke works with maximum 4 candidate languages`() = runTest {
        val candidates = listOf("en-US", "ja", "zh-CN", "ko")
        val result = AutoDetectRecognitionResult(text = "こんにちは", detectedLanguage = "ja")
        whenever(speechRepository.recognizeOnceWithAutoDetect(candidates))
            .thenReturn(Result.success(result))

        val actual = useCase(candidates)

        assertTrue(actual.isSuccess)
        assertEquals("ja", actual.getOrNull()?.detectedLanguage)
    }

    @Test
    fun `invoke returns empty text when nothing recognized`() = runTest {
        val candidates = listOf("en-US", "ja")
        val result = AutoDetectRecognitionResult(text = "", detectedLanguage = "")
        whenever(speechRepository.recognizeOnceWithAutoDetect(candidates))
            .thenReturn(Result.success(result))

        val actual = useCase(candidates)

        assertTrue(actual.isSuccess)
        assertEquals("", actual.getOrNull()?.text)
    }
}
