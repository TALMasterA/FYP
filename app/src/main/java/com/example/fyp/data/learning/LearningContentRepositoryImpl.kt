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

        val prompt = """
Create learning material for target language: $targetLanguageCode.
Explain in: $primaryLanguageCode.

Show language name instead of language code.

User translation history (recent):
$recent

Create TWO sections:
1. STUDY MATERIAL about vocabulary and grammar from the history above, using the $primaryLanguageCode language.
    Please use a beautiful format to present the study material. Change line if needed.
    
2. QUIZ SECTION with 10 multiple choice questions

Use this EXACT format for the QUIZ SECTION:

QUIZ SECTION: //After materials, state quiz session start (in english, no matter the $primaryLanguageCode)

1. What is the question text here?
A) Wrong answer
B) Correct answer
C) Wrong answer
D) Wrong answer
Correct: B
Explanation: Brief one-sentence explanation

2. Another question text?
A) Option A
B) Option B
C) Option C
D) Option D
Correct: C
Explanation: Explanation for this question

3. Continue with question 3...
A) Choice 1
B) Choice 2
C) Choice 3
D) Choice 4
Correct: D
Explanation: Explanation text

[Continue with questions 4-10 following the exact same format as above]
]

IMPORTANT RULES:
- Start with "QUIZ SECTION:" header
- Exactly 10 questions total (numbered 1-10)
- Questions in $primaryLanguageCode, but usinf the format
- Each question must have exactly A) B) C) D) options on separate lines
- Each option starts with A), B), C), or D) followed by space and the option text
- One "Correct: A/B/C/D" line per question (Correct: followed by the letter only)
- One "Explanation:" line per question (Explanation: followed by the explanation text)
- No extra text after question 10
- The questions should test vocabulary/grammar from the material
""".trimIndent()

        return genAi.generateLearningContent(
            deployment = deployment,
            prompt = prompt
        )
    }
}