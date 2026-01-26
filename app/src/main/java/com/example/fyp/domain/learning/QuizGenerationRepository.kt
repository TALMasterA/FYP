package com.example.fyp.domain.learning

import com.example.fyp.model.TranslationRecord

interface QuizGenerationRepository {
    suspend fun generateQuiz(
        deployment: String,
        primaryLanguageCode: String,
        targetLanguageCode: String,
        records: List<TranslationRecord>,
        learningMaterial: String
    ): String
}
