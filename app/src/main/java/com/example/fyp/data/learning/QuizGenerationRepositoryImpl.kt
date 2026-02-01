package com.example.fyp.data.learning

import com.example.fyp.data.cloud.CloudGenAiClient
import com.example.fyp.domain.learning.QuizGenerationRepository
import com.example.fyp.model.TranslationRecord
import javax.inject.Inject

class QuizGenerationRepositoryImpl @Inject constructor(
    private val genAi: CloudGenAiClient
) : QuizGenerationRepository {

    override suspend fun generateQuiz(
        deployment: String,
        primaryLanguageCode: String,
        targetLanguageCode: String,
        records: List<TranslationRecord>,
        learningMaterial: String
    ): String {
        val recent = records.takeLast(20).joinToString("\n") { r ->
            "- [${r.sourceLang}→${r.targetLang}] ${r.sourceText} => ${r.targetText}"
        }

        val prompt = """
You are generating ONLY a quiz.

Testing language: $targetLanguageCode
Explain / question language: $primaryLanguageCode

Context:
- The user already has study material below.
- Use it + the recent translation history to write a quiz that tests the same vocabulary/grammar.

STUDY MATERIAL (reference):
"""
            .trimIndent() + "\n" + learningMaterial.trim() + "\n\n" + """
RECENT TRANSLATION HISTORY (reference):
$recent

OUTPUT REQUIREMENTS (STRICT):
- Output ONLY valid JSON. No markdown, no code fences, no extra text.
- Must be a JSON object with this shape:
{
  "version": 1,
  "questions": [
    {
      "question": "string",
      "options": ["string","string","string","string"],
      "correctIndex": 0,
      "explanation": "string"
    }
  ]
}
- Exactly 10 questions in the array.
- options must be exactly 4 strings.
- correctIndex must be 0,1,2,or 3.
- 5 Questions based on TRANSLATION HISTORY (use STUDY MATERIAL only if insufficient data in history)
- 5 Questions based on STUDY MATERIAL.
- Questions based on them is in random order.
- Questions based on them to create, but ensure:
    1. The answer is correct. (Because the translation history is transalte by translator API, it may contain error, please check)
    2. Questions is practical.
    3. Sentences is natural, will be use by native speaker (better in spoken, if data is not enough then written is acceptable)
""".trimIndent()

        return genAi.generateLearningContent(
            deployment = deployment,
            prompt = prompt
        )
    }
}