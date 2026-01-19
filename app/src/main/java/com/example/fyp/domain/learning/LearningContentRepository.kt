package com.example.fyp.domain.learning

import com.example.fyp.model.TranslationRecord

interface LearningContentRepository {
    suspend fun generateForLanguage(
        deployment: String,
        primaryLanguageCode: String,
        targetLanguageCode: String,
        records: List<TranslationRecord>
    ): String
}