package com.example.fyp.domain.speech

import com.example.fyp.data.SpeechRepository
import com.microsoft.cognitiveservices.speech.SpeechRecognizer

class StartContinuousConversationUseCase(
    private val speechRepository: SpeechRepository
) {

    suspend operator fun invoke(
        languageCode: String,
        onPartial: (String) -> Unit,
        onFinal: (String) -> Unit,
        onError: (String) -> Unit
    ): SpeechRecognizer {
        return speechRepository.startContinuous(
            languageCode = languageCode,
            onPartial = onPartial,
            onFinal = onFinal,
            onError = onError
        )
    }

    suspend fun stop(recognizer: SpeechRecognizer?) {
        speechRepository.stopContinuous(recognizer)
    }
}