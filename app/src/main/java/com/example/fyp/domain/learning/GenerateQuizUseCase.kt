package com.example.fyp.domain.learning

import com.example.fyp.model.TranslationRecord
import javax.inject.Inject

class GenerateQuizUseCase @Inject constructor(
    private val repo: QuizGenerationRepository
) {
    suspend operator fun invoke(
        deployment: String,
        primaryLanguageCode: String,
        targetLanguageCode: String,
        records: List<TranslationRecord>,
        learningMaterial: String
    ): String {
        return repo.generateQuiz(
            deployment = deployment,
            primaryLanguageCode = primaryLanguageCode,
            targetLanguageCode = targetLanguageCode,
            records = records,
            learningMaterial = learningMaterial
        )
    }
}
