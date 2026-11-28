package com.translator.TalknLearn.screens.speech

import com.translator.TalknLearn.core.UiConstants
import com.translator.TalknLearn.domain.speech.SpeakTextUseCase
import com.translator.TalknLearn.model.SpeechResult
import com.translator.TalknLearn.model.ui.StatusText
import com.translator.TalknLearn.model.ui.UiTextKey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex

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
                        ttsStatus = StatusText(
                            if (isTranslation) UiTextKey.TtsSpeakingTranslation else UiTextKey.TtsSpeaking
                        )
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
                            getSpeechState().copy(ttsStatus = StatusText(UiTextKey.TtsSpoken))
                        )
                        delay(UiConstants.TTS_START_DELAY_MS)
                    }
                    is SpeechResult.Error -> {
                        setSpeechState(
                            getSpeechState().copy(
                                ttsStatus = StatusText(UiTextKey.TtsError, mapOf("detail" to result.message)),
                            )
                        )
                        delay(UiConstants.TTS_ERROR_WAIT_MS)
                    }
                }
            } finally {
                setSpeechState(
                    getSpeechState().copy(
                        isTtsRunning = false,
                        ttsStatus = null
                    )
                )
            }
        }
    }
}