package com.example.fyp.data.wordbank

import com.example.fyp.data.genai.CloudGenAiClient
import com.example.fyp.model.TranslationRecord
import javax.inject.Inject

class WordBankGenerationRepository @Inject constructor(
    private val genAi: CloudGenAiClient
) {
    suspend fun generateWordBank(
        deployment: String,
        primaryLanguageCode: String,
        targetLanguageCode: String,
        records: List<TranslationRecord>
    ): String {
        val recent = records.takeLast(30).joinToString("\n") { r ->
            "- [${r.sourceLang}→${r.targetLang}] ${r.sourceText} => ${r.targetText}"
        }

        val prompt = """
You are creating a WORD BANK from the user's translation history.

Target language to learn: $targetLanguageCode
Explain in language: $primaryLanguageCode

Show language names instead of language codes.

User translation history:
$recent

Extract 15-25 vocabulary items from the history above.

OUTPUT REQUIREMENTS (STRICT):
- Output ONLY valid JSON. No markdown, no code fences, no extra text.
- Must be a JSON object with this shape:
{
  "words": [
    {
      "original": "word in target language",
      "translated": "translation in primary language",
      "pronunciation": "romanization or phonetic guide",
      "example": "example sentence in target language",
      "category": "noun/verb/adjective/phrase/etc",
      "difficulty": "beginner/intermediate/advanced"
    }
  ]
}
- Generate 15-25 word items.
- Focus on the most useful and frequently appearing words/phrases.
- Include pronunciation guide (romanization for non-Latin scripts).
- Each example should be a practical sentence.
""".trimIndent()

        return genAi.generateLearningContent(
            deployment = deployment,
            prompt = prompt
        )
    }
}
