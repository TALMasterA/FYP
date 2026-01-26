package com.example.fyp.data.learning

import android.util.Log
import com.example.fyp.model.QuizQuestion

/**
 * Simple markdown-based quiz parser.
 * Parses "QUIZ SECTION:" followed by numbered questions with A) B) C) D) options.
 */
object QuizParser {

    private val QUESTION_NUMBER_REGEX = Regex("^\\d+\\.")
    // Accept A) A. A - A: with alternation to avoid char-class escape warnings
    private val OPTION_REGEX = Regex("^[A-D](?:\\)|\\.|\\-|:)\\s*(.+)", RegexOption.IGNORE_CASE)
    // Accept Correct: B or Answer: B or Correct - B
    private val CORRECT_REGEX = Regex("^(Correct|Answer)\\s*[:\\-]?\\s*([A-D])", RegexOption.IGNORE_CASE)
    private val EXPLANATION_PREFIX_REGEX = Regex("^Explanation\\s*[:\\-]?\\s*(.+)", RegexOption.IGNORE_CASE)

    fun parseQuizFromContent(content: String): List<QuizQuestion> {
        Log.d("QuizParser", "Parsing quiz from content (${content.length} chars)")

        // Find quiz section
        val quizIndex = content.indexOf("QUIZ SECTION", ignoreCase = true)
        if (quizIndex < 0) {
            Log.w("QuizParser", "No QUIZ SECTION found")
            return emptyList()
        }

        val quizText = content.substring(quizIndex)
        val questions = mutableListOf<QuizQuestion>()
        val lines = quizText.split("\n")

        var i = 1 // Skip the QUIZ SECTION line
        while (i < lines.size) {
            var line = lines[i].trim()
            if (line.isEmpty()) { i++; continue }

            if (!line.matches(QUESTION_NUMBER_REGEX)) {
                // If line does not start a question, skip
                i++
                continue
            }

            // Extract question text (strip leading number and dot)
            val questionText = line.replace(Regex("^\\d+\\.\\s*"), "").trim()

            val options = mutableListOf<String>()
            var explanation = ""
            var correctIndex = -1

            // read following lines for options, correct line, and explanation
            var j = i + 1
            while (j < lines.size) {
                val raw = lines[j]
                val optLine = raw.trim()

                if (optLine.isEmpty()) { j++; continue }

                // Next question begins
                if (optLine.matches(QUESTION_NUMBER_REGEX)) {
                    break
                }

                // Option line
                val optMatch = OPTION_REGEX.find(optLine)
                if (optMatch != null) {
                    val optionText = optMatch.groupValues[1].trim()
                    options.add(optionText)
                    j++
                    continue
                }

                // Correct line
                val corrMatch = CORRECT_REGEX.find(optLine)
                if (corrMatch != null) {
                    val letter = corrMatch.groupValues[2].uppercase()
                    correctIndex = letter[0] - 'A'
                    j++
                    continue
                }

                // Explanation line (possibly multi-line)
                val explMatch = EXPLANATION_PREFIX_REGEX.find(optLine)
                if (explMatch != null) {
                    explanation = explMatch.groupValues[1].trim()
                    j++
                    // capture continuation lines that are not new question or option or correct
                    while (j < lines.size) {
                        val cont = lines[j].trim()
                        if (cont.isEmpty()) { j++; continue }
                        if (cont.matches(QUESTION_NUMBER_REGEX)) break
                        if (OPTION_REGEX.matches(cont)) break
                        if (CORRECT_REGEX.matches(cont)) break
                        // append line to explanation
                        explanation += " " + cont
                        j++
                    }
                    continue
                }

                // If no pattern matched, move on
                j++
            }

            // default correct index if missing
            if (correctIndex < 0 && options.isNotEmpty()) correctIndex = 0

            // Add question if we have a question text and at least one option
            if (questionText.isNotBlank() && options.isNotEmpty()) {
                val q = QuizQuestion(
                    id = "q_${questions.size + 1}",
                    question = questionText,
                    options = options.toList(),
                    correctOptionIndex = correctIndex,
                    explanation = explanation
                )
                questions.add(q)
            } else {
                Log.w("QuizParser", "Skipping malformed question at line ${i + 1}")
            }

            // advance i to j (next question or end)
            i = j
        }

        Log.d("QuizParser", "Parsed ${questions.size} questions")
        return questions
    }
}
