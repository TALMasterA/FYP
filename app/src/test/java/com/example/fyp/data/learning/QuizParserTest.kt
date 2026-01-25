package com.example.fyp.data.learning

import org.junit.Test
import org.junit.Assert.*

class QuizParserTest {

    @Test
    fun testParseSimpleQuiz() {
        val content = """
QUIZ SECTION:

1. What is the question text here?
A) Wrong answer
B) Correct answer
C) Wrong answer
D) Wrong answer
Correct: B
Explanation: Brief one-sentence explanation

2. Another question text?
A) Option A
B) Option B
C) Option C
D) Option D
Correct: C
Explanation: Explanation for this question
        """.trimIndent()

        val questions = QuizParser.parseQuizFromContent(content)

        assertEquals(2, questions.size)

        val q1 = questions[0]
        assertEquals("What is the question text here?", q1.question)
        assertEquals(4, q1.options.size)
        assertEquals("Wrong answer", q1.options[0])
        assertEquals("Correct answer", q1.options[1])
        assertEquals(1, q1.correctOptionIndex) // B is index 1
        assertEquals("Brief one-sentence explanation", q1.explanation)

        val q2 = questions[1]
        assertEquals("Another question text?", q2.question)
        assertEquals(4, q2.options.size)
        assertEquals(2, q2.correctOptionIndex) // C is index 2
        assertEquals("Explanation for this question", q2.explanation)
    }

    @Test
    fun testParseWithEmptyLines() {
        val content = """
QUIZ SECTION:

1. Question one?
A) Answer A
B) Answer B
C) Answer C
D) Answer D
Correct: A
Explanation: This is correct

2. Question two?
A) Option 1
B) Option 2
C) Option 3
D) Option 4
Correct: D
Explanation: Another explanation
        """.trimIndent()

        val questions = QuizParser.parseQuizFromContent(content)
        assertEquals(2, questions.size)
        assertEquals(0, questions[0].correctOptionIndex) // A
        assertEquals(3, questions[1].correctOptionIndex) // D
    }

    @Test
    fun testParseQuizWithNoSection() {
        val content = "No quiz section here"
        val questions = QuizParser.parseQuizFromContent(content)
        assertEquals(0, questions.size)
    }

    @Test
    fun testParseQuizWithMissingCorrectLine() {
        // Should still parse even with missing Correct: line (defaults to first option)
        val content = """
QUIZ SECTION:

1. What is this?
A) First
B) Second
C) Third
D) Fourth
Explanation: Some explanation
        """.trimIndent()

        val questions = QuizParser.parseQuizFromContent(content)
        assertEquals(1, questions.size)
        assertEquals(0, questions[0].correctOptionIndex) // Defaults to first option
        assertEquals("Some explanation", questions[0].explanation)
    }

    @Test
    fun testParseQuizWithMultiLineExplanation() {
        // Should handle explanations that span multiple lines
        val content = """
QUIZ SECTION:

1. What is the capital?
A) Wrong
B) Correct answer
C) Wrong
D) Wrong
Correct: B
Explanation: This is the correct answer
because it is the actual capital city
of the country in question.

2. Next question?
A) Option A
B) Option B
C) Option C
D) Option D
Correct: A
Explanation: Short explanation
        """.trimIndent()

        val questions = QuizParser.parseQuizFromContent(content)
        assertEquals(2, questions.size)
        // First explanation should contain all lines
        assertTrue(questions[0].explanation.contains("This is the correct answer"))
        assertTrue(questions[0].explanation.contains("because it is the actual capital"))
        assertEquals("Short explanation", questions[1].explanation)
    }

    @Test
    fun testParseMultipleTenQuestions() {
        val content = """
QUIZ SECTION:

1. Q1?
A) A1
B) B1
C) C1
D) D1
Correct: A
Explanation: E1

2. Q2?
A) A2
B) B2
C) C2
D) D2
Correct: B
Explanation: E2

3. Q3?
A) A3
B) B3
C) C3
D) D3
Correct: C
Explanation: E3

4. Q4?
A) A4
B) B4
C) C4
D) D4
Correct: D
Explanation: E4

5. Q5?
A) A5
B) B5
C) C5
D) D5
Correct: A
Explanation: E5

6. Q6?
A) A6
B) B6
C) C6
D) D6
Correct: B
Explanation: E6

7. Q7?
A) A7
B) B7
C) C7
D) D7
Correct: C
Explanation: E7

8. Q8?
A) A8
B) B8
C) C8
D) D8
Correct: D
Explanation: E8

9. Q9?
A) A9
B) B9
C) C9
D) D9
Correct: A
Explanation: E9

10. Q10?
A) A10
B) B10
C) C10
D) D10
Correct: B
Explanation: E10
        """.trimIndent()

        val questions = QuizParser.parseQuizFromContent(content)
        assertEquals(10, questions.size)

        // Verify a few questions
        assertEquals("Q1?", questions[0].question)
        assertEquals(0, questions[0].correctOptionIndex)

        assertEquals("Q5?", questions[4].question)
        assertEquals(0, questions[4].correctOptionIndex)

        assertEquals("Q10?", questions[9].question)
        assertEquals(1, questions[9].correctOptionIndex)
    }
}
