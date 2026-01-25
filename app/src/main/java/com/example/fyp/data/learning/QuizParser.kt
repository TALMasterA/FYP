package com.example.fyp.data.learning

import com.example.fyp.model.QuizQuestion
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonPrimitive

@Serializable
data class JsonQuizFormat(
    val questions: List<JsonQuizQuestion> = emptyList()
)

@Serializable
data class JsonQuizQuestion(
    val question: String = "",
    val options: List<String> = emptyList(),
    val correctOptionIndex: Int = 0,
    val explanation: String = ""
)

/**
 * Utility to parse quiz questions from AI-generated learning content
 * Supports both markdown format and JSON format
 */
object QuizParser {

    private val json = Json { ignoreUnknownKeys = true }

    /**
     * Try to parse quiz from AI-generated content
     * First attempts JSON parsing, then falls back to markdown pattern matching
     */
    fun parseQuizFromContent(content: String): List<QuizQuestion> {
        // Try JSON parsing first
        val jsonQuestions = tryParseJsonFormat(content)
        if (jsonQuestions.isNotEmpty()) {
            return jsonQuestions
        }

        // Fall back to markdown format parsing
        return parseMarkdownFormat(content)
    }

    /**
     * Try to parse JSON-formatted questions
     */
    private fun tryParseJsonFormat(content: String): List<QuizQuestion> {
        return try {
            // Try to find and parse JSON block
            val jsonStr = if (content.contains("{")) {
                content.substringAfter("{")
                    .substringBeforeLast("}")
                    .let { "{$it}" }
            } else {
                return emptyList()
            }

            val parsed = json.decodeFromString<JsonQuizFormat>(jsonStr)
            parsed.questions.mapIndexed { index, q ->
                QuizQuestion(
                    id = "q_$index",
                    question = q.question,
                    options = q.options,
                    correctOptionIndex = q.correctOptionIndex.coerceIn(0, maxOf(0, q.options.size - 1)),
                    explanation = q.explanation,
                    type = "multiple_choice"
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    /**
     * Parse markdown format questions
     * Expected format:
     * **Question 1:** What is...?
     * A) Option 1
     * B) Option 2
     * C) Option 3
     * **Answer:** B
     * **Explanation:** Because...
     */
    private fun parseMarkdownFormat(content: String): List<QuizQuestion> {
        val questions = mutableListOf<QuizQuestion>()

        // Split by question markers
        val questionBlocks = content.split(Regex("(?i)\\*\\*Question\\s+\\d+:|Question\\s+\\d+:"))
            .filter { it.trim().isNotEmpty() }
            .drop(1) // Drop the part before first question

        var questionIndex = 0
        for (block in questionBlocks) {
            val lines = block.trim().split("\n")
            if (lines.isEmpty()) continue

            var questionText = ""
            val options = mutableListOf<String>()
            var correctIndex = -1
            var explanation = ""

            var i = 0
            while (i < lines.size) {
                val line = lines[i].trim()

                // Extract question text (usually first non-empty line)
                if (questionText.isEmpty() && line.isNotEmpty() && !line.matches(Regex("[A-D]\\).*"))) {
                    questionText = line.removeSuffix("?").trim()
                    if (!questionText.endsWith("?")) questionText += "?"
                }

                // Extract options (A), B), C), D))
                if (line.matches(Regex("[A-D]\\).*"))) {
                    val optionText = line.substring(2).trim()
                    val optionIndex = line[0] - 'A'
                    options.add(optionText)

                    // Check if this is marked as the answer
                    if (i + 1 < lines.size) {
                        val nextLine = lines[i + 1].trim().lowercase()
                        if (nextLine.contains("✓") || nextLine.contains("correct")) {
                            correctIndex = optionIndex
                        }
                    }
                }

                // Extract answer (marked by "**Answer:**" or "Answer:")
                if (line.matches(Regex("(?i)(\\*\\*)?Answer(\\*\\*)?:?\\s*[A-D]"))) {
                    val answerChar = line.filter { it in 'A'..'D' }.firstOrNull()
                    if (answerChar != null) {
                        correctIndex = answerChar - 'A'
                    }
                }

                // Extract explanation
                if (line.matches(Regex("(?i)(\\*\\*)?Explanation(\\*\\*)?:.*"))) {
                    explanation = line.replaceFirst(Regex("(?i)(\\*\\*)?Explanation(\\*\\*)?:"), "").trim()
                    // Combine multi-line explanation
                    val explanationLines = mutableListOf(explanation)
                    i++
                    while (i < lines.size && !lines[i].trim().matches(Regex("(?i)\\*\\*Question.*"))) {
                        val nextLine = lines[i].trim()
                        if (nextLine.isNotEmpty() && !nextLine.matches(Regex("[A-D]\\).*")) &&
                            !nextLine.matches(Regex("(?i)(\\*\\*)?Answer.*"))) {
                            explanationLines.add(nextLine)
                        }
                        i++
                    }
                    explanation = explanationLines.joinToString(" ")
                    continue
                }

                i++
            }

            // Only add if we have at least question and options
            if (questionText.isNotEmpty() && options.size >= 2) {
                if (correctIndex < 0 || correctIndex >= options.size) {
                    correctIndex = 0 // Default to first option if not found
                }

                questions.add(
                    QuizQuestion(
                        id = "q_$questionIndex",
                        question = questionText,
                        options = options,
                        correctOptionIndex = correctIndex,
                        explanation = explanation,
                        type = "multiple_choice"
                    )
                )
                questionIndex++
            }
        }

        return questions
    }
}


