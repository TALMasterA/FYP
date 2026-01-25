package com.example.fyp.data.learning

import com.example.fyp.data.genai.CloudGenAiClient
import com.example.fyp.domain.learning.LearningContentRepository
import com.example.fyp.model.TranslationRecord
import javax.inject.Inject

class LearningContentRepositoryImpl @Inject constructor(
    private val genAi: CloudGenAiClient
) : LearningContentRepository {

    override suspend fun generateForLanguage(
        deployment: String,
        primaryLanguageCode: String,
        targetLanguageCode: String,
        records: List<TranslationRecord>
    ): String {
        val recent = records.takeLast(20).joinToString("\n") { r ->
            "- [${r.sourceLang}→${r.targetLang}] ${r.sourceText} => ${r.targetText}"
        }

        //Prompt will type and modify here
        val prompt = """
Create learning material for target language: $targetLanguageCode.
Explain in: $primaryLanguageCode.
Display the language code in language name.

User translation history (recent):
$recent

Return concise study material.
Include  5 quiz questions about words & grammars appear in the history, and 5 for knowledge in materials, be practical, not theory.
Do not suggest the next prompt, e.g. "If you want..."
""".trimIndent()

        return genAi.generateLearningContent(
            deployment = deployment,
            prompt = prompt
        )
    }
}