package com.example.fyp.screens.speech

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import com.example.fyp.domain.speech.SpeakTextUseCase
import com.example.fyp.model.SpeechResult
import kotlinx.coroutines.delay
import com.example.fyp.core.UiConstants

internal class TtsController(
    private val scope: CoroutineScope,
    private val speakTextUseCase: SpeakTextUseCase,
    private val getSpeechState: () -> SpeechScreenState,
    private val setSpeechState: (SpeechScreenState) -> Unit,
) {
    private val ttsMutex = Mutex()

    fun speak(text: String, languageCode: String, isTranslation: Boolean, voiceName: String? = null) {
        if (text.isBlank() || getSpeechState().isTtsRunning) return

        scope.launch {
            // Double-check under mutex to prevent concurrent speak() races
            if (!ttsMutex.tryLock()) return@launch
            try {
                if (getSpeechState().isTtsRunning) return@launch

                setSpeechState(
                    getSpeechState().copy(
                        isTtsRunning = true,
                        ttsStatus = if (isTranslation) "Speaking translation..." else "Speaking..."
                    )
                )
            } finally {
                ttsMutex.unlock()
            }

            try {
                val result = speakTextUseCase(text, languageCode, voiceName)
                when (result) {
                    is SpeechResult.Success -> {
                        setSpeechState(
                            getSpeechState().copy(ttsStatus = "✓ Spoken")
                        )
                        delay(UiConstants.TTS_START_DELAY_MS)
                    }
                    is SpeechResult.Error -> {
                        setSpeechState(
                            getSpeechState().copy(ttsStatus = "✗ ${result.message}")
                        )
                        delay(UiConstants.TTS_ERROR_WAIT_MS)
                    }
                }
            } finally {
                setSpeechState(
                    getSpeechState().copy(
                        isTtsRunning = false,
                        ttsStatus = ""
                    )
                )
            }
        }
    }
}