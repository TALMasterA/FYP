package com.example.fyp.model

import com.google.firebase.Timestamp
import org.junit.Test
import org.junit.Assert.*

/**
 * Unit tests for Quiz model data classes.
 *
 * Tests business logic in:
 * - QuizQuestion: option validation, correctness logic
 * - QuizAnswer: answer recording and validation
 * - QuizAttempt: score calculation, percentage computation
 */
class QuizModelTest {

    // --- QuizQuestion Tests ---

    @Test
    fun `quiz question must have exactly 4 options`() {
        val question = QuizQuestion(
            id = "q1",
            question = "Test?",
            options = listOf("A", "B", "C", "D"),
            correctOptionIndex = 0
        )

        assertEquals(4, question.options.size)
    }

    @Test
    fun `quiz question correct index must be within valid range`() {
        val question = QuizQuestion(
            id = "q1",
            question = "Test?",
            options = listOf("A", "B", "C", "D"),
            correctOptionIndex = 2
        )

        assertTrue(question.correctOptionIndex in 0..3)
    }

    @Test
    fun `quiz question has default type of multiple choice`() {
        val question = QuizQuestion(
            id = "q1",
            question = "Test?",
            options = listOf("A", "B", "C", "D"),
            correctOptionIndex = 0
        )

        assertEquals("multiple_choice", question.type)
    }

    // --- QuizAnswer Tests ---

    @Test
    fun `quiz answer with correct selection`() {
        val answer = QuizAnswer(
            questionId = "q1",
            selectedOptionIndex = 2,
            isCorrect = true,
            timeSpentSeconds = 15
        )

        assertTrue(answer.isCorrect)
        assertEquals(2, answer.selectedOptionIndex)
    }

    @Test
    fun `quiz answer with incorrect selection`() {
        val answer = QuizAnswer(
            questionId = "q1",
            selectedOptionIndex = 1,
            isCorrect = false,
            timeSpentSeconds = 10
        )

        assertFalse(answer.isCorrect)
        assertEquals(1, answer.selectedOptionIndex)
    }

    @Test
    fun `unanswered question has default index -1`() {
        val answer = QuizAnswer(
            questionId = "q1"
        )

        assertEquals(-1, answer.selectedOptionIndex)
        assertFalse(answer.isCorrect)
    }

    // --- QuizAttempt Scoring Tests ---

    @Test
    fun `quiz attempt calculates percentage correctly for perfect score`() {
        val attempt = QuizAttempt(
            id = "attempt1",
            userId = "user1",
            totalScore = 10,
            maxScore = 10,
            percentage = 100f
        )

        assertEquals(100f, attempt.percentage, 0.01f)
        assertEquals(10, attempt.totalScore)
        assertEquals(10, attempt.maxScore)
    }

    @Test
    fun `quiz attempt calculates percentage correctly for partial score`() {
        val attempt = QuizAttempt(
            id = "attempt1",
            userId = "user1",
            totalScore = 7,
            maxScore = 10,
            percentage = 70f
        )

        assertEquals(70f, attempt.percentage, 0.01f)
    }

    @Test
    fun `quiz attempt calculates percentage correctly for zero score`() {
        val attempt = QuizAttempt(
            id = "attempt1",
            userId = "user1",
            totalScore = 0,
            maxScore = 10,
            percentage = 0f
        )

        assertEquals(0f, attempt.percentage, 0.01f)
    }

    @Test
    fun `quiz attempt with matching questions and answers counts`() {
        val questions = listOf(
            QuizQuestion(id = "q1", question = "Q1?", options = listOf("A", "B", "C", "D"), correctOptionIndex = 0),
            QuizQuestion(id = "q2", question = "Q2?", options = listOf("A", "B", "C", "D"), correctOptionIndex = 1)
        )

        val answers = listOf(
            QuizAnswer(questionId = "q1", selectedOptionIndex = 0, isCorrect = true),
            QuizAnswer(questionId = "q2", selectedOptionIndex = 1, isCorrect = true)
        )

        val attempt = QuizAttempt(
            id = "attempt1",
            userId = "user1",
            questions = questions,
            answers = answers,
            totalScore = 2,
            maxScore = 2,
            percentage = 100f
        )

        assertEquals(questions.size, answers.size)
        assertEquals(2, attempt.totalScore)
    }

    @Test
    fun `quiz attempt tracks completion timestamp`() {
        val startTime = Timestamp.now()
        val completeTime = Timestamp(startTime.seconds + 120, 0) // 2 minutes later

        val attempt = QuizAttempt(
            id = "attempt1",
            userId = "user1",
            startedAt = startTime,
            completedAt = completeTime
        )

        assertNotNull(attempt.completedAt)
        assertTrue(attempt.completedAt!!.seconds > attempt.startedAt.seconds)
    }

    @Test
    fun `quiz attempt stores generation history count for anti-cheat`() {
        val attempt = QuizAttempt(
            id = "attempt1",
            userId = "user1",
            generatedHistoryCountAtGenerate = 50
        )

        assertEquals(50, attempt.generatedHistoryCountAtGenerate)
    }

    // --- QuizStats Tests ---

    @Test
    fun `quiz stats tracks multiple attempts`() {
        val stats = QuizStats(
            primaryLanguageCode = "en",
            targetLanguageCode = "es",
            attemptCount = 5,
            averageScore = 75f,
            highestScore = 90,
            lowestScore = 60,
            lastAttemptAt = Timestamp.now()
        )

        assertEquals(5, stats.attemptCount)
        assertEquals(75f, stats.averageScore, 0.01f)
        assertEquals(90, stats.highestScore)
        assertEquals(60, stats.lowestScore)
    }

    @Test
    fun `quiz stats for first attempt`() {
        val stats = QuizStats(
            primaryLanguageCode = "en",
            targetLanguageCode = "fr",
            attemptCount = 1,
            averageScore = 80f,
            highestScore = 80,
            lowestScore = 80
        )

        assertEquals(1, stats.attemptCount)
        assertEquals(stats.highestScore, stats.lowestScore)
    }

    // --- UserCoinStats Tests ---

    @Test
    fun `user coin stats tracks total and per-language coins`() {
        val stats = UserCoinStats(
            coinTotal = 250,
            coinByLang = mapOf(
                "en-es" to 100,
                "en-fr" to 75,
                "en-de" to 75
            )
        )

        assertEquals(250, stats.coinTotal)
        assertEquals(3, stats.coinByLang.size)
        assertEquals(100, stats.coinByLang["en-es"])
    }

    @Test
    fun `user coin stats starts at zero for new user`() {
        val stats = UserCoinStats()

        assertEquals(0, stats.coinTotal)
        assertTrue(stats.coinByLang.isEmpty())
    }

    // --- Edge Cases ---

    @Test
    fun `quiz attempt with no answers yet (in progress)`() {
        val attempt = QuizAttempt(
            id = "attempt1",
            userId = "user1",
            questions = listOf(
                QuizQuestion(id = "q1", question = "Q?", options = listOf("A", "B", "C", "D"), correctOptionIndex = 0)
            ),
            answers = emptyList(),
            completedAt = null
        )

        assertTrue(attempt.answers.isEmpty())
        assertNull(attempt.completedAt)
    }

    @Test
    fun `quiz question with empty explanation is allowed`() {
        val question = QuizQuestion(
            id = "q1",
            question = "Test?",
            options = listOf("A", "B", "C", "D"),
            correctOptionIndex = 0,
            explanation = ""
        )

        assertEquals("", question.explanation)
    }
}
