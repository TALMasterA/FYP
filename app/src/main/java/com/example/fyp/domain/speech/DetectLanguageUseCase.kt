package com.example.fyp.domain.speech

import com.example.fyp.data.clients.DetectedLanguage
import com.example.fyp.data.repositories.TranslationRepository

/**
 * Use case for automatic language detection from text.
 * Uses Cloud Translation API to identify the source language.
 *
 * @param translationRepository Repository handling translation and language detection
 */
class DetectLanguageUseCase(
    private val translationRepository: TranslationRepository
) {
    /**
     * Detects the language of the provided text.
     *
     * @param text The text to analyze for language detection
     * @return DetectedLanguage with language code and confidence score, or null if detection fails
     */
    suspend operator fun invoke(text: String): DetectedLanguage? {
        return translationRepository.detectLanguage(text)
    }
}
