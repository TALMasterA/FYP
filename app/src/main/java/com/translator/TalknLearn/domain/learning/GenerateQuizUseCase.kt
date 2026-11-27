package com.translator.TalknLearn.domain.learning

import com.translator.TalknLearn.model.GeneratedLearningContent
import com.translator.TalknLearn.model.TranslationRecord
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
    ): GeneratedLearningContent {
        return repo.generateQuiz(
            deployment = deployment,
            primaryLanguageCode = primaryLanguageCode,
            targetLanguageCode = targetLanguageCode,
            records = records,
            learningMaterial = learningMaterial
        )
    }
}
