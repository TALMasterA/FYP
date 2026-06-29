package com.translator.TalknLearn.screens.wordbank

import com.translator.TalknLearn.core.UiConstants
import com.translator.TalknLearn.domain.speech.SpeakTextUseCase
import com.translator.TalknLearn.model.SpeechResult
import com.translator.TalknLearn.model.user.UserSettings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

/**
 * TTS playback for word-bank items (original word, translated word, example).
 * Extracted from [WordBankViewModel] for maintainability.
 */
internal class WordBankSpeechDelegate(
    private val uiState: MutableStateFlow<WordBankUiState>,
    private val scope: CoroutineScope,
    private val speakTextUseCase: SpeakTextUseCase,
    private val getUserSettings: () -> UserSettings
) {
    fun speakWord(word: WordBankItem, type: SpeakingType) {
        val state = uiState.value
        if (state.isSpeaking) return

        val text = when (type) {
            SpeakingType.ORIGINAL -> word.originalWord
            SpeakingType.TRANSLATED -> word.translatedWord
        }

        val languageCode = if (state.isCustomWordBankSelected && word.category.contains(" → ")) {
            val parts = word.category.split(" → ")
            when (type) {
                SpeakingType.ORIGINAL -> parts.getOrNull(0)?.trim() ?: return
                SpeakingType.TRANSLATED -> parts.getOrNull(1)?.trim() ?: return
            }
        } else {
            when (type) {
                SpeakingType.ORIGINAL -> state.currentWordBank?.targetLanguageCode ?: return
                SpeakingType.TRANSLATED -> state.currentWordBank?.primaryLanguageCode ?: return
            }
        }

        scope.launch {
            uiState.value = uiState.value.copy(
                isSpeaking = true,
                speakingItemId = word.id,
                speakingType = type
            )

            try {
                val voiceName = getUserSettings().voiceSettings[languageCode]
                val result = speakTextUseCase(text, languageCode, voiceName)
                when (result) {
                    is SpeechResult.Success -> {
                        delay(UiConstants.TTS_FINISH_DELAY_MS)
                    }
                    is SpeechResult.Error -> { }
                }
            } finally {
                uiState.value = uiState.value.copy(
                    isSpeaking = false,
                    speakingItemId = null,
                    speakingType = null
                )
            }
        }
    }

    fun speakExample(word: WordBankItem) {
        val state = uiState.value
        if (state.isSpeaking || word.example.isBlank()) return

        val languageCode = if (state.isCustomWordBankSelected && word.category.contains(" → ")) {
            val parts = word.category.split(" → ")
            parts.getOrNull(0)?.trim() ?: return
        } else {
            state.currentWordBank?.targetLanguageCode ?: return
        }

        scope.launch {
            uiState.value = uiState.value.copy(
                isSpeaking = true,
                speakingItemId = word.id,
                speakingType = SpeakingType.ORIGINAL
            )

            try {
                val voiceName = getUserSettings().voiceSettings[languageCode]
                speakTextUseCase(word.example, languageCode, voiceName)
            } finally {
                uiState.value = uiState.value.copy(
                    isSpeaking = false,
                    speakingItemId = null,
                    speakingType = null
                )
            }
        }
    }
}
