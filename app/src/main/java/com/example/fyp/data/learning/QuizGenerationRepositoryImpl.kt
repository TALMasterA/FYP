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
- Output ONLY the quiz block.
- Start with the exact header: QUIZ SECTION:
- Exactly 10 questions total.
- Questions numbered 1-10.
- Each question has exactly 4 options, one per line, formatted A) ... B) ... C) ... D) ...
- Then one line: Correct: <A|B|C|D>
- Then one line: Explanation: <one sentence>
- No extra commentary text.

QUIZ SECTION:
1. ...
A) ...
B) ...
C) ...
D) ...
Correct: A
Explanation: ...

[Continue until question 10]
""".trimIndent()

        return genAi.generateLearningContent(
            deployment = deployment,
            prompt = prompt
        )
    }
}
