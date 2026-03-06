package com.example.fyp.domain.speech

import com.example.fyp.data.repositories.SpeechRepository
import com.microsoft.cognitiveservices.speech.SpeechRecognizer
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.*

/**
 * Unit tests for StartContinuousConversationUseCase.
 *
 * Tests:
 *  1. invoke delegates to speechRepository.startContinuous with correct parameters
 *  2. stop delegates to speechRepository.stopContinuous
 *  3. stop handles null recognizer
 */
@OptIn(ExperimentalCoroutinesApi::class)
class StartContinuousConversationUseCaseTest {

    private lateinit var speechRepository: SpeechRepository
    private lateinit var useCase: StartContinuousConversationUseCase

    @Before
    fun setup() {
        speechRepository = mock()
        useCase = StartContinuousConversationUseCase(speechRepository)
    }

    // ── Test 1: invoke delegates correctly ──

    @Test
    fun `invoke delegates to speechRepository startContinuous`() = runTest {
        val mockRecognizer: SpeechRecognizer = mock()
        val onPartial: (String) -> Unit = {}
        val onFinal: (String) -> Unit = {}
        val onError: (String) -> Unit = {}

        whenever(speechRepository.startContinuous(
            languageCode = eq("en-US"),
            onPartial = any(),
            onFinal = any(),
            onError = any()
        )).thenReturn(mockRecognizer)

        val result = useCase.invoke(
            languageCode = "en-US",
            onPartial = onPartial,
            onFinal = onFinal,
            onError = onError
        )

        assertEquals(mockRecognizer, result)
        verify(speechRepository).startContinuous(
            languageCode = eq("en-US"),
            onPartial = any(),
            onFinal = any(),
            onError = any()
        )
    }

    // ── Test 2: stop delegates to speechRepository ──

    @Test
    fun `stop delegates to speechRepository stopContinuous`() = runTest {
        val mockRecognizer: SpeechRecognizer = mock()

        useCase.stop(mockRecognizer)

        verify(speechRepository).stopContinuous(mockRecognizer)
    }

    // ── Test 3: stop handles null recognizer ──

    @Test
    fun `stop handles null recognizer`() = runTest {
        useCase.stop(null)

        verify(speechRepository).stopContinuous(null)
    }
}
