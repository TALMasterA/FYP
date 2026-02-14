package com.example.fyp.domain.speech

import com.example.fyp.data.repositories.TranslationRepository
import com.example.fyp.model.SpeechResult

/**
 * Use case for translating text from one language to another.
 * Leverages caching in the repository layer to reduce API calls.
 *
 * @param translationRepository Repository handling translation logic and caching
 */
class TranslateTextUseCase(
    private val translationRepository: TranslationRepository
) {
    /**
     * Translates text from source language to target language.
     *
     * @param text The text to translate
     * @param fromLanguage Source language code (e.g., "en-US", "zh-CN")
     * @param toLanguage Target language code (e.g., "ja-JP", "fr-FR")
     * @return SpeechResult.Success with translated text or SpeechResult.Error with error message
     */
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