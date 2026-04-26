package com.translator.TalknLearn.domain.learning

import com.translator.TalknLearn.model.TranslationRecord

interface QuizGenerationRepository {
    suspend fun generateQuiz(
        deployment: String,
        primaryLanguageCode: String,
        targetLanguageCode: String,
        records: List<TranslationRecord>,
        learningMaterial: String
    ): String
}
