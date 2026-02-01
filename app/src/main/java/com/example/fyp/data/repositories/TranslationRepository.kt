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
     * Detect the language of the given text.
     * Returns null if detection fails.
     */
    suspend fun detectLanguage(text: String): DetectedLanguage?
}