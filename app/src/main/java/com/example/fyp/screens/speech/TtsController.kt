package com.example.fyp.screens.speech

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
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

    fun speak(text: String, languageCode: String, isTranslation: Boolean, voiceName: String? = null) {
        val state = getSpeechState()
        if (text.isBlank() || state.isTtsRunning) return

        scope.launch {
            setSpeechState(
                getSpeechState().copy(
                    isTtsRunning = true,
                    ttsStatus = if (isTranslation) "Speaking translation..." else "Speaking..."
                )
            )

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