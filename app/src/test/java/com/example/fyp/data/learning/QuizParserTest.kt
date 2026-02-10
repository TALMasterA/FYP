package com.example.fyp.data.learning

import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.Assert.*

class QuizParserTest {

    @Test
    fun testParseSimpleJsonQuiz() = runBlocking {
        val content = """
        {
            "questions": [
                {
                    "question": "What is the question text here?",
                    "options": ["Wrong answer", "Correct answer", "Wrong answer", "Wrong answer"],
                    "correctIndex": 1,
                    "explanation": "Brief one-sentence explanation"
                },
                {
                    "question": "Another question text?",
                    "options": ["Option A", "Option B", "Option C", "Option D"],
                    "correctIndex": 2,
                    "explanation": "Explanation for this question"
                }
            ]
        }
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
    fun testParseJsonArray() = runBlocking {
        val content = """
        [
            {
                "question": "Question one?",
                "options": ["Answer A", "Answer B", "Answer C", "Answer D"],
                "correctIndex": 0,
                "explanation": "This is correct"
            },
            {
                "question": "Question two?",
                "options": ["Option 1", "Option 2", "Option 3", "Option 4"],
                "correctIndex": 3,
                "explanation": "Another explanation"
            }
        ]
        """.trimIndent()

        val questions = QuizParser.parseQuizFromContent(content)
        assertEquals(2, questions.size)
        assertEquals(0, questions[0].correctOptionIndex) // A
        assertEquals(3, questions[1].correctOptionIndex) // D
    }

    @Test
    fun testParseWithNoValidJson() = runBlocking {
        val content = "No JSON content here"
        val questions = QuizParser.parseQuizFromContent(content)
        assertEquals(0, questions.size)
    }

    @Test
    fun testParseJsonWithMarkdownCodeBlock() = runBlocking {
        // Should handle code blocks like ```json ... ```
        val content = """
        ```json
        {
            "questions": [
                {
                    "question": "What is this?",
                    "options": ["First", "Second", "Third", "Fourth"],
                    "correctIndex": 0,
                    "explanation": "Some explanation"
                }
            ]
        }
        ```
        """.trimIndent()

        val questions = QuizParser.parseQuizFromContent(content)
        assertEquals(1, questions.size)
        assertEquals(0, questions[0].correctOptionIndex) 
        assertEquals("Some explanation", questions[0].explanation)
    }

    @Test
    fun testParseJsonWithInvalidCorrectIndex() = runBlocking {
        // Should skip questions with invalid correctIndex (not 0-3)
        val content = """
        {
            "questions": [
                {
                    "question": "What is the capital?",
                    "options": ["Wrong", "Correct answer", "Wrong", "Wrong"],
                    "correctIndex": 5,
                    "explanation": "Invalid index"
                },
                {
                    "question": "Valid question?",
                    "options": ["A", "B", "C", "D"],
                    "correctIndex": 2,
                    "explanation": "Valid"
                }
            ]
        }
        """.trimIndent()

        val questions = QuizParser.parseQuizFromContent(content)
        // First question should be skipped, only second is valid
        assertEquals(1, questions.size)
        assertEquals("Valid question?", questions[0].question)
        assertEquals(2, questions[0].correctOptionIndex)
    }

    @Test
    fun testParseMultipleTenQuestions() = runBlocking {
        val questions = (1..10).map { i ->
            """
                {
                    "question": "Q$i?",
                    "options": ["A$i", "B$i", "C$i", "D$i"],
                    "correctIndex": ${(i - 1) % 4},
                    "explanation": "E$i"
                }
            """.trimIndent()
        }

        val content = """
        {
            "questions": [
                ${questions.joinToString(",\n")}
            ]
        }
        """.trimIndent()

        val parsedQuestions = QuizParser.parseQuizFromContent(content)
        assertEquals(10, parsedQuestions.size)

        // Verify a few questions
        assertEquals("Q1?", parsedQuestions[0].question)
        assertEquals(0, parsedQuestions[0].correctOptionIndex)

        assertEquals("Q5?", parsedQuestions[4].question)
        assertEquals(0, parsedQuestions[4].correctOptionIndex)

        assertEquals("Q10?", parsedQuestions[9].question)
        assertEquals(1, parsedQuestions[9].correctOptionIndex)
    }

    @Test
    fun testParseJsonWithBlankFields() = runBlocking {
        // Should skip questions with blank question or options
        val content = """
        {
            "questions": [
                {
                    "question": "",
                    "options": ["A", "B", "C", "D"],
                    "correctIndex": 0,
                    "explanation": "Blank question"
                },
                {
                    "question": "Valid?",
                    "options": ["A", "", "C", "D"],
                    "correctIndex": 0,
                    "explanation": "Blank option"
                },
                {
                    "question": "Good question?",
                    "options": ["A", "B", "C", "D"],
                    "correctIndex": 1,
                    "explanation": "Valid"
                }
            ]
        }
        """.trimIndent()

        val questions = QuizParser.parseQuizFromContent(content)
        // Only last question is valid
        assertEquals(1, questions.size)
        assertEquals("Good question?", questions[0].question)
    }

    @Test
    fun testParseJsonWithWrongNumberOfOptions() = runBlocking {
        // Should skip questions without exactly 4 options
        val content = """
        {
            "questions": [
                {
                    "question": "Too few?",
                    "options": ["A", "B", "C"],
                    "correctIndex": 0,
                    "explanation": "Only 3 options"
                },
                {
                    "question": "Valid?",
                    "options": ["A", "B", "C", "D"],
                    "correctIndex": 1,
                    "explanation": "Valid"
                }
            ]
        }
        """.trimIndent()

        val questions = QuizParser.parseQuizFromContent(content)
        assertEquals(1, questions.size)
        assertEquals("Valid?", questions[0].question)
    }
}
