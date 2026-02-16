package com.example.fyp.domain.learning

import com.example.fyp.model.DeploymentName
import com.example.fyp.model.LanguageCode
import com.example.fyp.model.TranslationRecord

interface LearningContentRepository {
    suspend fun generateForLanguage(
        deployment: DeploymentName,
        primaryLanguageCode: LanguageCode,
        targetLanguageCode: LanguageCode,
        records: List<TranslationRecord>
    ): String
}