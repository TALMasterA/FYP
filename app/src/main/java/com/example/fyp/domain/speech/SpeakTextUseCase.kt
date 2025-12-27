package com.example.fyp.domain.speech

import com.example.fyp.data.SpeechRepository
import com.example.fyp.model.SpeechResult

class SpeakTextUseCase(
    private val speechRepository: SpeechRepository
) {
    suspend operator fun invoke(
        text: String,
        languageCode: String
    ): SpeechResult {
        return speechRepository.speak(
            text = text,
            languageCode = languageCode
        )
    }
}