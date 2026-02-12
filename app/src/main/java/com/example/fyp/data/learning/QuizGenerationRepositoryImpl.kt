package com.example.fyp.data.learning

import com.example.fyp.data.cloud.CloudGenAiClient
import com.example.fyp.domain.learning.QuizGenerationRepository
import com.example.fyp.model.TranslationRecord
import javax.inject.Inject

class QuizGenerationRepositoryImpl @Inject constructor(
    private val genAi: CloudGenAiClient
) : QuizGenerationRepository {

    /**
     * Select optimal translation records for quiz generation context.
     * Uses diverse sampling strategy to avoid duplicates and ensure variety.
     *
     * @param records All available translation records
     * @param targetSize Target number of records to select (default 20)
     * @return Optimally selected records
     */
    private fun selectOptimalContext(
        records: List<TranslationRecord>,
        targetSize: Int = 20
    ): List<TranslationRecord> {
        if (records.size <= targetSize) return records

        // 1. Deduplicate by source text (case-insensitive)
        val uniqueWords = records
            .groupBy { it.sourceText.lowercase().trim() }
            .mapValues { it.value.first() } // Keep first occurrence

        // 2. Calculate frequency for each word
        val wordFrequency = records
            .groupingBy { it.sourceText.lowercase().trim() }
            .eachCount()

        // 3. Weighted selection strategy:
        //    - 50% most frequent words (likely important/common errors)
        //    - 30% recent words (fresh in memory)
        //    - 20% random words (variety)

        val frequentCount = targetSize / 2
        val recentCount = (targetSize * 30) / 100
        val randomCount = targetSize - frequentCount - recentCount

        // Get most frequent words
        val frequent = uniqueWords.values
            .sortedByDescending { wordFrequency[it.sourceText.lowercase().trim()] ?: 0 }
            .take(frequentCount)

        // Get recent unique words (excluding already selected frequent ones)
        val recent = records
            .takeLast(targetSize * 2) // Look at more recent records
            .distinctBy { it.sourceText.lowercase().trim() }
            .filterNot { record ->
                frequent.any { it.sourceText.lowercase().trim() == record.sourceText.lowercase().trim() }
            }
            .take(recentCount)

        // Get random selection from remaining words
        val remaining = uniqueWords.values
            .filterNot { record ->
                val key = record.sourceText.lowercase().trim()
                frequent.any { it.sourceText.lowercase().trim() == key } ||
                recent.any { it.sourceText.lowercase().trim() == key }
            }

        val random = if (remaining.isNotEmpty()) {
            remaining.shuffled().take(randomCount)
        } else {
            emptyList()
        }

        // Combine all selections, sorted by timestamp (most recent first)
        return (frequent + recent + random)
            .distinctBy { it.sourceText.lowercase().trim() }
            .sortedByDescending { it.timestamp }
            .take(targetSize)
    }

    override suspend fun generateQuiz(
        deployment: String,
        primaryLanguageCode: String,
        targetLanguageCode: String,
        records: List<TranslationRecord>,
        learningMaterial: String
    ): String {
        // Use optimized context selection instead of just takeLast(20)
        val selectedRecords = selectOptimalContext(records, targetSize = 20)

        val recent = selectedRecords.joinToString("\n") { r ->
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