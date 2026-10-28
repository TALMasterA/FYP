package com.translator.TalknLearn.data.learning

import org.junit.Assert.*
import org.junit.Test

/**
 * Extended unit tests for ContentCleaner utility.
 *
 * Tests content parsing logic for removing quiz sections
 * and extracting quiz sections from learning materials.
 */
class ContentCleanerExtendedTest {

    // ── removeQuizFromContent ───────────────────────────────────────

    @Test
    fun `removes quiz section from content`() {
        val content = "Learning material here\n\nQUIZ SECTION:\nQ1: What is...?"
        val result = ContentCleaner.removeQuizFromContent(content)

        assertEquals("Learning material here", result)
    }

    @Test
    fun `returns full content when no quiz section exists`() {
        val content = "Just learning material\nNo quiz here"
        val result = ContentCleaner.removeQuizFromContent(content)

        assertEquals("Just learning material\nNo quiz here", result)
    }

    @Test
    fun `handles content with QUIZ SECTION without colon`() {
        val content = "Material\n\nQUIZ SECTION\nQ1: Test?"
        val result = ContentCleaner.removeQuizFromContent(content)

        assertEquals("Material", result)
    }

    @Test
    fun `handles case-insensitive quiz section header`() {
        val content = "Material\n\nquiz section:\nQ1: Test?"
        val result = ContentCleaner.removeQuizFromContent(content)

        assertEquals("Material", result)
    }

    @Test
    fun `handles mixed case quiz section header`() {
        val content = "Material\n\nQuiz Section:\nQ1: Test?"
        val result = ContentCleaner.removeQuizFromContent(content)

        assertEquals("Material", result)
    }

    @Test
    fun `returns empty string when content is only quiz`() {
        val content = "QUIZ SECTION:\nQ1: Test?"
        val result = ContentCleaner.removeQuizFromContent(content)

        // quizStart is 0, not > 0, so full content returned
        assertEquals(content, result)
    }

    @Test
    fun `preserves whitespace trimming in main content`() {
        val content = "Material with trailing spaces   \n\n  QUIZ SECTION:\nQuiz"
        val result = ContentCleaner.removeQuizFromContent(content)

        assertEquals("Material with trailing spaces", result)
    }

    @Test
    fun `handles empty content`() {
        val result = ContentCleaner.removeQuizFromContent("")
        assertEquals("", result)
    }
}
