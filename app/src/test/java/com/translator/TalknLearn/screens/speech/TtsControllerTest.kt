package com.translator.TalknLearn.screens.speech

import com.translator.TalknLearn.domain.speech.SpeakTextUseCase
import com.translator.TalknLearn.model.SpeechResult
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.*

/**
 * Unit tests for TtsController.
 *
 * Tests:
 * 1. Blank text does not trigger TTS
 * 2. Does not speak when TTS is already running
 * 3. Sets isTtsRunning=true during speak
 * 4. Shows "Speaking translation..." for translations
 * 5. Shows "Speaking..." for non-translations
 * 6. On success, shows "✓ Spoken" then resets
 * 7. On error, shows error message then resets
 * 8. After completion, isTtsRunning is false and ttsStatus is empty
 * 9. Passes voiceName to speakTextUseCase
 * 10. Passes null voiceName by default
 */
@OptIn(ExperimentalCoroutinesApi::class)
class TtsControllerTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    private lateinit var speakTextUseCase: SpeakTextUseCase
    private var state = SpeechScreenState()
    private val stateHistory = mutableListOf<SpeechScreenState>()

    private lateinit var controller: TtsController

    @Before
    fun setup() {
        speakTextUseCase = mock()
        state = SpeechScreenState()
        stateHistory.clear()

        controller = TtsController(
            scope = testScope,
            speakTextUseCase = speakTextUseCase,
            getSpeechState = { state },
            setSpeechState = { newState ->
                state = newState
                stateHistory.add(newState)
            },
        )
    }

    // ── 1. Blank text does not trigger TTS ──────────────────────────────

    @Test
    fun `blank text does not trigger TTS`() = testScope.runTest {
        controller.speak(text = "", languageCode = "en-US", isTranslation = false)
        advanceUntilIdle()

        assertFalse("isTtsRunning should remain false", state.isTtsRunning)
        assertEquals("ttsStatus should remain empty", "", state.ttsStatus)
        verifyNoInteractions(speakTextUseCase)
    }

    @Test
    fun `whitespace-only text does not trigger TTS`() = testScope.runTest {
        controller.speak(text = "   ", languageCode = "en-US", isTranslation = false)
        advanceUntilIdle()

        assertFalse(state.isTtsRunning)
        verifyNoInteractions(speakTextUseCase)
    }

    // ── 2. Does not speak when TTS is already running ───────────────────

    @Test
    fun `does not speak when TTS is already running`() = testScope.runTest {
        state = state.copy(isTtsRunning = true)

        controller.speak(text = "Hello", languageCode = "en-US", isTranslation = false)
        advanceUntilIdle()

        assertTrue("stateHistory should be empty since no state change occurred", stateHistory.isEmpty())
        verifyNoInteractions(speakTextUseCase)
    }

    // ── 3. Sets isTtsRunning=true during speak ──────────────────────────

    @Test
    fun `sets isTtsRunning to true during speak`() = testScope.runTest {
        whenever(speakTextUseCase.invoke(any(), any(), anyOrNull()))
            .thenReturn(SpeechResult.Success("done"))

        controller.speak(text = "Hello", languageCode = "en-US", isTranslation = false)
        advanceUntilIdle()

        // The first state update should have isTtsRunning = true
        assertTrue("First state update should set isTtsRunning=true", stateHistory.isNotEmpty())
        assertTrue("isTtsRunning should be true in first update", stateHistory[0].isTtsRunning)
    }

    // ── 4. Shows "Speaking translation..." for translations ─────────────

    @Test
    fun `shows Speaking translation for translations`() = testScope.runTest {
        whenever(speakTextUseCase.invoke(any(), any(), anyOrNull()))
            .thenReturn(SpeechResult.Success("done"))

        controller.speak(text = "Hola", languageCode = "es-ES", isTranslation = true)
        advanceUntilIdle()

        assertTrue(stateHistory.isNotEmpty())
        assertEquals("Speaking translation...", stateHistory[0].ttsStatus)
    }

    // ── 5. Shows "Speaking..." for non-translations ─────────────────────

    @Test
    fun `shows Speaking for non-translations`() = testScope.runTest {
        whenever(speakTextUseCase.invoke(any(), any(), anyOrNull()))
            .thenReturn(SpeechResult.Success("done"))

        controller.speak(text = "Hello", languageCode = "en-US", isTranslation = false)
        advanceUntilIdle()

        assertTrue(stateHistory.isNotEmpty())
        assertEquals("Speaking...", stateHistory[0].ttsStatus)
    }

    // ── 6. On success, shows "✓ Spoken" then resets ─────────────────────

    @Test
    fun `on success shows checkmark Spoken then resets`() = testScope.runTest {
        whenever(speakTextUseCase.invoke(any(), any(), anyOrNull()))
            .thenReturn(SpeechResult.Success("Speech completed"))

        controller.speak(text = "Hello", languageCode = "en-US", isTranslation = false)
        advanceUntilIdle()

        // State history: [0] = Speaking..., [1] = ✓ Spoken, [2] = reset
        assertTrue("Should have at least 3 state updates", stateHistory.size >= 3)
        assertEquals("✓ Spoken", stateHistory[1].ttsStatus)
    }

    // ── 7. On error, shows error message then resets ────────────────────

    @Test
    fun `on error shows error message then resets`() = testScope.runTest {
        whenever(speakTextUseCase.invoke(any(), any(), anyOrNull()))
            .thenReturn(SpeechResult.Error("Network error"))

        controller.speak(text = "Hello", languageCode = "en-US", isTranslation = false)
        advanceUntilIdle()

        // State history: [0] = Speaking..., [1] = ✗ Network error, [2] = reset
        assertTrue("Should have at least 3 state updates", stateHistory.size >= 3)
        assertEquals("✗ Network error", stateHistory[1].ttsStatus)
    }

    // ── 8. After completion, isTtsRunning is false and ttsStatus empty ──

    @Test
    fun `after completion isTtsRunning is false and ttsStatus is empty`() = testScope.runTest {
        whenever(speakTextUseCase.invoke(any(), any(), anyOrNull()))
            .thenReturn(SpeechResult.Success("done"))

        controller.speak(text = "Hello", languageCode = "en-US", isTranslation = false)
        advanceUntilIdle()

        assertFalse("isTtsRunning should be false after completion", state.isTtsRunning)
        assertEquals("ttsStatus should be empty after completion", "", state.ttsStatus)
    }

    @Test
    fun `after error completion isTtsRunning is false and ttsStatus is empty`() = testScope.runTest {
        whenever(speakTextUseCase.invoke(any(), any(), anyOrNull()))
            .thenReturn(SpeechResult.Error("Synthesis failed"))

        controller.speak(text = "Hello", languageCode = "en-US", isTranslation = false)
        advanceUntilIdle()

        assertFalse("isTtsRunning should be false after error", state.isTtsRunning)
        assertEquals("ttsStatus should be empty after error", "", state.ttsStatus)
    }

    // ── 9. Passes voiceName to speakTextUseCase ─────────────────────────

    @Test
    fun `passes voiceName to speakTextUseCase`() = testScope.runTest {
        whenever(speakTextUseCase.invoke(any(), any(), anyOrNull()))
            .thenReturn(SpeechResult.Success("done"))

        controller.speak(
            text = "Hello",
            languageCode = "en-US",
            isTranslation = false,
            voiceName = "en-US-JennyNeural"
        )
        advanceUntilIdle()

        verify(speakTextUseCase).invoke("Hello", "en-US", "en-US-JennyNeural")
    }

    // ── 10. Passes null voiceName by default ────────────────────────────

    @Test
    fun `passes null voiceName by default`() = testScope.runTest {
        whenever(speakTextUseCase.invoke(any(), any(), anyOrNull()))
            .thenReturn(SpeechResult.Success("done"))

        controller.speak(text = "Hello", languageCode = "en-US", isTranslation = false)
        advanceUntilIdle()

        verify(speakTextUseCase).invoke("Hello", "en-US", null)
    }
}
