package com.example.fyp.domain.speech

import com.example.fyp.data.clients.DetectedLanguage
import com.example.fyp.data.repositories.TranslationRepository

class DetectLanguageUseCase(
    private val translationRepository: TranslationRepository
) {
    suspend operator fun invoke(text: String): DetectedLanguage? {
        return translationRepository.detectLanguage(text)
    }
}
