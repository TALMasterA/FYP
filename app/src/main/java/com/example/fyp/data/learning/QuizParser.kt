package com.example.fyp.data.learning

import android.util.Log
import com.example.fyp.model.QuizQuestion

/**
 * Simple markdown-based quiz parser.
 * Parses "QUIZ SECTION:" followed by numbered questions with A) B) C) D) options.
 */
object QuizParser {

    fun parseQuizFromContent(content: String): List<QuizQuestion> {
        Log.d("QuizParser", "Parsing quiz from content (${content.length} chars)")

        // Find quiz section
        val quizIndex = content.indexOf("QUIZ SECTION:", ignoreCase = true)
        if (quizIndex < 0) {
            Log.w("QuizParser", "No QUIZ SECTION found")
            return emptyList()
        }

        val quizText = content.substring(quizIndex)
        val questions = mutableListOf<QuizQuestion>()
        val lines = quizText.split("\n")

        var i = 1 // Skip "QUIZ SECTION:" line
        var qIndex = 0

        while (i < lines.size) {
            val line = lines[i].trim()

            // Look for question number (1., 2., etc.)
            if (line.matches(Regex("^\\d+\\.\\s+.+"))) {
                val questionText = line.substringAfter(".").trim()
                val options = mutableListOf<String>()
                var correct = -1
                var explanation = ""
                var j = i + 1

                // Parse options and metadata
                while (j < lines.size) {
                    val optLine = lines[j].trim()
                    if (optLine.isEmpty()) {
                        j++
                        continue
                    }
                    if (optLine.matches(Regex("^[A-D]\\)\\s*.+"))) {
                        val letter = optLine[0]
                        val text = optLine.substring(2).trim()
                        options.add(text)
                    } else if (optLine.startsWith("Correct:", ignoreCase = true)) {
                        val answer = optLine.substringAfter(":").trim().uppercase()
                        if (answer.isNotEmpty() && answer[0] in 'A'..'D') {
                            correct = answer[0] - 'A'
                        }
                    } else if (optLine.startsWith("Explanation:", ignoreCase = true)) {
                        explanation = optLine.substringAfter(":").trim()
                        // Try to capture multi-line explanation if next line doesn't start a new question
                        j++
                        while (j < lines.size) {
                            val nextLine = lines[j].trim()
                            if (nextLine.isEmpty() || nextLine.matches(Regex("^\\d+\\..+"))) {
                                break
                            }
                            if (!nextLine.matches(Regex("^[A-D]\\)\\s*.+"))) {
                                explanation += " " + nextLine
                                j++
                            } else {
                                break
                            }
                        }
                        continue
                    } else if (optLine.matches(Regex("^\\d+\\..+"))) {
                        // Next question
                        break
                    }
                    j++
                }

                // If correct answer not found, default to first option (should not happen with proper format)
                if (correct < 0) {
                    correct = 0
                }

                // Validate and add question
                if (questionText.isNotBlank() && options.size >= 2) {
                    questions.add(QuizQuestion(
                        id = "q_${qIndex++}",
                        question = questionText,
                        options = options.take(4), // Max 4 options
                        correctOptionIndex = minOf(correct, options.size - 1),
                        explanation = explanation,
                        type = "multiple_choice"
                    ))
                    Log.d("QuizParser", "Parsed question: $questionText")
                }
                i = j
            } else {
                i++
            }
        }

        Log.d("QuizParser", "Total questions parsed: ${questions.size}")
        return questions
    }
}
