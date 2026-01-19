package com.example.fyp.domain.speech

import com.example.fyp.data.repositories.TranslationRepository
import com.example.fyp.model.SpeechResult

class TranslateTextUseCase(
    private val translationRepository: TranslationRepository
) {
    suspend operator fun invoke(
        text: String,
        fromLanguage: String,
        toLanguage: String
    ): SpeechResult {
        return translationRepository.translate(
            text = text,
            fromLanguage = fromLanguage,
            toLanguage = toLanguage
        )
    }
}