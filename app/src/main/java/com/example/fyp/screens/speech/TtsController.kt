package com.example.fyp.screens.speech

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import com.example.fyp.domain.speech.SpeakTextUseCase
import com.example.fyp.model.SpeechResult

internal class TtsController(
    private val scope: CoroutineScope,
    private val speakTextUseCase: SpeakTextUseCase,
    private val getSpeechState: () -> SpeechScreenState,
    private val setSpeechState: (SpeechScreenState) -> Unit,
) {
    fun speak(
        text: String,
        languageCode: String,
        isTranslation: Boolean
    ) {
        val state = getSpeechState()
        if (text.isBlank() || state.isTtsRunning) return

        scope.launch {
            setSpeechState(
                getSpeechState().copy(
                    isTtsRunning = true,
                    ttsStatus = if (isTranslation) "Speaking translation..." else "Speaking original..."
                )
            )

            when (val result = speakTextUseCase(text, languageCode)) {
                is SpeechResult.Success -> {
                    setSpeechState(getSpeechState().copy(ttsStatus = "Finished speaking."))
                }
                is SpeechResult.Error -> {
                    setSpeechState(getSpeechState().copy(ttsStatus = "TTS error: ${result.message}"))
                }
            }

            setSpeechState(getSpeechState().copy(isTtsRunning = false))
        }
    }
}