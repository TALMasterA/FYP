package com.translator.TalknLearn.domain.speech

import com.translator.TalknLearn.data.repositories.SpeechRepository
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