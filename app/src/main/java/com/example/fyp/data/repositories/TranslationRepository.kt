package com.example.fyp.data.repositories

import com.example.fyp.data.clients.DetectedLanguage
import com.example.fyp.model.SpeechResult

interface TranslationRepository {
    suspend fun translate(
        text: String,
        fromLanguage: String,
        toLanguage: String
    ): SpeechResult

    /**
     * Batch translate multiple texts at once.
     * More efficient than individual translate calls - reduces API calls.
     * @return Map of sourceText -> translatedText
     */
    suspend fun translateBatch(
        texts: List<String>,
        fromLanguage: String,
        toLanguage: String
    ): Result<Map<String, String>>

    /**
     * Detect the language of the given text.
     * Returns null if detection fails.
     */
    suspend fun detectLanguage(text: String): DetectedLanguage?
}