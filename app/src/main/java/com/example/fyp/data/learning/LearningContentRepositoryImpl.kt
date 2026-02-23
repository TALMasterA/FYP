package com.example.fyp.data.learning

import com.example.fyp.data.cloud.CloudGenAiClient
import com.example.fyp.domain.learning.LearningContentRepository
import com.example.fyp.model.TranslationRecord
import com.example.fyp.model.DeploymentName
import com.example.fyp.model.LanguageCode
import javax.inject.Inject

private const val MAX_VOCABULARY_ITEMS = 8

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
Create a concise study sheet for target language: $targetLanguageCode.
Explanation language: $primaryLanguageCode.

User's recent translation history:
$recent

Instructions:
- Select only the 5–$MAX_VOCABULARY_ITEMS most useful and representative vocabulary items or short phrases from the history above.
- For each item provide: the word/phrase in $targetLanguageCode, its $primaryLanguageCode meaning, a short pronunciation guide, and one brief example sentence.
- Add a short grammar note (2–3 sentences max) only if a clear grammar pattern appears across the items.
- Use clear headings and bullet points.
- Do NOT include more than $MAX_VOCABULARY_ITEMS vocabulary items.
- Do NOT use a question or response tone — write as a concise study sheet only.
""".trimIndent()

        return genAi.generateLearningContent(
            deployment = deployment.value,
            prompt = prompt
        )
    }
}