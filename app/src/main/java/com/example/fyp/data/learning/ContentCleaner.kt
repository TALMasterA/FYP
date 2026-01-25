package com.example.fyp.data.learning

import android.util.Log

/**
 * Utility to clean learning content by removing quiz section.
 * Removes everything from "QUIZ SECTION:" onwards.
 */
object ContentCleaner {

    /**
     * Remove quiz section from content for display
     */
    fun removeQuizFromContent(content: String): String {
        val quizStart = content.indexOf("QUIZ SECTION:", ignoreCase = true)
        return if (quizStart > 0) {
            Log.d("ContentCleaner", "Removed quiz section from content")
            content.substring(0, quizStart).trim()
        } else {
            content
        }
    }

    /**
     * Extract only the quiz section from content
     */
    fun extractQuizSection(content: String): String {
        val quizStart = content.indexOf("QUIZ SECTION:", ignoreCase = true)
        return if (quizStart >= 0) {
            Log.d("ContentCleaner", "Extracted quiz section from content")
            content.substring(quizStart)
        } else {
            ""
        }
    }
}
