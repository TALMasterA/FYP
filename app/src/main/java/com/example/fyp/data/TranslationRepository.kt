package com.example.fyp.data

import com.example.fyp.model.SpeechResult

interface TranslationRepository {
    suspend fun translate(
        text: String,
        fromLanguage: String,
        toLanguage: String
    ): SpeechResult
}