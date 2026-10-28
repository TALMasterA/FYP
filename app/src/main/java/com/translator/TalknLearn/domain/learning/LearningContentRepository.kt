package com.translator.TalknLearn.domain.learning

import com.translator.TalknLearn.model.DeploymentName
import com.translator.TalknLearn.model.LanguageCode
import com.translator.TalknLearn.model.TranslationRecord

interface LearningContentRepository {
    suspend fun generateForLanguage(
        deployment: DeploymentName,
        primaryLanguageCode: LanguageCode,
        targetLanguageCode: LanguageCode,
        records: List<TranslationRecord>
    ): String
}