package com.example.fyp.data.learning

import org.junit.Assert.assertTrue
import org.junit.Test

class ContentCleanerTest {

    @Test
    fun extractQuizSection_acceptsHeaderWithoutColon() {
        val content = """
STUDY MATERIAL:
Hello

QUIZ SECTION

1. Q?
A) A
B) B
C) C
D) D
Correct: A
Explanation: E
        """.trimIndent()

        val quiz = ContentCleaner.extractQuizSection(content)
        assertTrue(quiz.startsWith("1. Q?"))
    }

    @Test
    fun extractQuizSection_acceptsHeaderWithColon() {
        val content = """
STUDY MATERIAL:
Hello

QUIZ SECTION:

1. Q?
A) A
B) B
C) C
D) D
Correct: A
Explanation: E
        """.trimIndent()

        val quiz = ContentCleaner.extractQuizSection(content)
        assertTrue(quiz.startsWith("1. Q?"))
    }
}
