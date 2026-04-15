package com.example.fyp.data.learning

/**
 * Utility to clean learning content by removing quiz section.
 * Removes everything from "QUIZ SECTION" onwards.
 */
object ContentCleaner {

    private fun findQuizStart(content: String): Int {
        // Accept both "QUIZ SECTION:" and "QUIZ SECTION" (with or without colon)
        return content.indexOf("QUIZ SECTION", ignoreCase = true)
    }

    /**
     * Remove quiz section from content for display
     */
    fun removeQuizFromContent(content: String): String {
        val quizStart = findQuizStart(content)
        return if (quizStart > 0) {
            content.substring(0, quizStart).trim()
        } else {
            content
        }
    }

}
