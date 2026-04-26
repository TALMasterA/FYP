package com.translator.TalknLearn.domain.learning

import com.translator.TalknLearn.model.DeploymentName // Added
import com.translator.TalknLearn.model.LanguageCode // Added
import com.translator.TalknLearn.model.TranslationRecord
import javax.inject.Inject

class GenerateLearningMaterialsUseCase @Inject constructor(
    private val repo: LearningContentRepository
) {
    suspend operator fun invoke(
        deployment: String,
        primaryLanguageCode: String,
        targetLanguageCode: String,
        records: List<TranslationRecord>
    ): String {
        return repo.generateForLanguage(
            deployment = DeploymentName(deployment),
            primaryLanguageCode = LanguageCode(primaryLanguageCode),
            targetLanguageCode = LanguageCode(targetLanguageCode),
            records = records
        )
    }
}