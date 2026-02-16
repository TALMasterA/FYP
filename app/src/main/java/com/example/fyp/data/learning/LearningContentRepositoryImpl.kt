package com.example.fyp.data.learning

import com.example.fyp.data.cloud.CloudGenAiClient
import com.example.fyp.domain.learning.LearningContentRepository
import com.example.fyp.model.TranslationRecord
import com.example.fyp.model.DeploymentName
import com.example.fyp.model.LanguageCode
import javax.inject.Inject

class LearningContentRepositoryImpl @Inject constructor(
    private val genAi: CloudGenAiClient
) : LearningContentRepository {

    override suspend fun generateForLanguage(
        deployment: DeploymentName,
        primaryLanguageCode: LanguageCode,
        targetLanguageCode: LanguageCode,
        records: List<TranslationRecord>
    ): String {
        val recent = records.takeLast(20).joinToString("\n") { r ->
            "- [${r.sourceLang}→${r.targetLang}] ${r.sourceText} => ${r.targetText}"
        }

        val prompt = """
Create learning material for target language: $targetLanguageCode.
Explain in: $primaryLanguageCode.

Show language name instead of language code.

User translation history (related):
$recent

Create STUDY MATERIAL about vocabulary and grammar from the history above, using the $primaryLanguageCode language.
Please use a beautiful format to present the study material. Change line if needed.
Include examples, explanations, and practical usage.

Please do not include question asking like "If you want...", generate a learning material instead of response-like tone.

Note: Quiz is generated separately (and this content will be referenced), so focus only on high-quality educational content.
""".trimIndent()

        return genAi.generateLearningContent(
            deployment = deployment.value,
            prompt = prompt
        )
    }
}