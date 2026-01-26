package com.example.fyp.data.learning

import com.example.fyp.data.genai.CloudGenAiClient
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

Target language: $targetLanguageCode
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
- Questions 1-5 based mainly on RECENT TRANSLATION HISTORY (use STUDY MATERIAL only if insufficient data in history) ; 6-10 based mainly on STUDY MATERIAL.
""".trimIndent()

        return genAi.generateLearningContent(
            deployment = deployment,
            prompt = prompt
        )
    }
}