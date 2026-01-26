package com.example.fyp.data.learning

import com.example.fyp.model.QuizQuestion
import org.json.JSONArray
import org.json.JSONObject

object QuizParser {

    fun parseQuizFromContent(content: String): List<QuizQuestion> {
        // 1) Try JSON first (Option A)
        parseJsonQuiz(content)?.let { if (it.isNotEmpty()) return it }

        // 2) Fallback to your existing text quiz parsing
        return parseTextQuiz(content)
    }

    private fun parseJsonQuiz(raw: String): List<QuizQuestion>? {
        val s = raw.trim()
            .replace(Regex("^```(json)?", RegexOption.IGNORE_CASE), "")
            .replace(Regex("```$"), "")
            .trim()

        val startObj = s.indexOf('{')
        val startArr = s.indexOf('[')
        val start = listOf(startObj, startArr).filter { it >= 0 }.minOrNull() ?: return null
        val endObj = s.lastIndexOf('}')
        val endArr = s.lastIndexOf(']')
        val end = maxOf(endObj, endArr)
        if (end <= start) return null

        val json = s.substring(start, end + 1).trim()

        val arr: JSONArray = when {
            json.startsWith("[") -> JSONArray(json)
            json.startsWith("{") -> {
                val obj = JSONObject(json)
                obj.optJSONArray("questions") ?: return null
            }
            else -> return null
        }

        val out = mutableListOf<QuizQuestion>()
        for (i in 0 until arr.length()) {
            val qObj = arr.optJSONObject(i) ?: continue
            val question = qObj.optString("question").trim()
            val optionsArr = qObj.optJSONArray("options")
            val correctIndex = qObj.optInt("correctIndex", -1)
            val explanation = qObj.optString("explanation").trim()

            if (question.isBlank() || optionsArr == null || optionsArr.length() != 4) continue
            if (correctIndex !in 0..3) continue

            val options = List(4) { idx -> optionsArr.optString(idx).trim() }
            if (options.any { it.isBlank() }) continue

            out.add(
                QuizQuestion(
                    id = "q_${out.size + 1}",
                    question = question,
                    options = options,
                    correctOptionIndex = correctIndex,
                    explanation = explanation
                )
            )
        }
        return out
    }

    private fun parseTextQuiz(content: String): List<QuizQuestion> {
        // Keep (or improve) your current logic here.
        // IMPORTANT: this is used by LearningViewModel.generateFor() validation.
        return emptyList() // replace with your existing implementation
    }
}
