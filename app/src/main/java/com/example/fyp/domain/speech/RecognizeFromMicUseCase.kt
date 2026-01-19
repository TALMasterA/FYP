package com.example.fyp.domain.speech

import com.example.fyp.data.repositories.SpeechRepository
import com.example.fyp.model.SpeechResult

class RecognizeFromMicUseCase(
    private val speechRepository: SpeechRepository
) {
    suspend operator fun invoke(languageCode: String): SpeechResult {
        return speechRepository.recognizeOnce(languageCode)
    }
}