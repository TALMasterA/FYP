package com.translator.TalknLearn.model

import com.google.firebase.Timestamp
import org.junit.Assert.*
import org.junit.Test

/**
 * Unit tests for Quiz attempt models and scoring logic.
 *
 * Tests:
 * 1. QuizQuestion validation
 * 2. QuizAnswer correctness checks
 * 3. QuizAttempt score calculations
 * 4. QuizStats aggregation
 */
class QuizAttemptTest {

    // ── QuizQuestion ─────────────────────────────────────────────────

    @Test
    fun `QuizQuestion default values are correct`() {
        val q = QuizQuestion()
        assertEquals("", q.id)
        assertEquals("", q.question)
        assertTrue(q.options.isEmpty())
        assertEquals(0, q.correctOptionIndex)
        assertEquals("", q.explanation)
        assertEquals("multiple_choice", q.type)
    }

    @Test
    fun `QuizQuestion stores all fields correctly`() {
        val q = QuizQuestion(
            id = "q1",
            question = "What is 1+1?",
            options = listOf("1", "2", "3", "4"),
            correctOptionIndex = 1,
            explanation = "Basic arithmetic",
            type = "multiple_choice"
        )
        assertEquals("q1", q.id)
        assertEquals("What is 1+1?", q.question)
        assertEquals(4, q.options.size)
        assertEquals("2", q.options[1])
        assertEquals(1, q.correctOptionIndex)
        assertEquals("Basic arithmetic", q.explanation)
    }

    @Test
    fun `QuizQuestion correct option index points to correct answer`() {
        val q = QuizQuestion(
            question = "Capital of Japan?",
            options = listOf("Osaka", "Tokyo", "Kyoto", "Nagoya"),
            correctOptionIndex = 1
        )
        assertEquals("Tokyo", q.options[q.correctOptionIndex])
    }

    // ── QuizAnswer ───────────────────────────────────────────────────

    @Test
    fun `QuizAnswer default values`() {
        val a = QuizAnswer()
        assertEquals("", a.questionId)
        assertEquals(-1, a.selectedOptionIndex)
        assertFalse(a.isCorrect)
        assertEquals(0, a.timeSpentSeconds)
    }

    @Test
    fun `QuizAnswer stores correct answer`() {
        val a = QuizAnswer(
            questionId = "q1",
            selectedOptionIndex = 2,
            isCorrect = true,
            timeSpentSeconds = 10
        )
        assertEquals("q1", a.questionId)
        assertEquals(2, a.selectedOptionIndex)
        assertTrue(a.isCorrect)
        assertEquals(10, a.timeSpentSeconds)
    }

    @Test
    fun `QuizAnswer stores incorrect answer`() {
        val a = QuizAnswer(
            questionId = "q1",
            selectedOptionIndex = 0,
            isCorrect = false,
            timeSpentSeconds = 5
        )
        assertFalse(a.isCorrect)
    }

    @Test
    fun `QuizAnswer negative selected index indicates unanswered`() {
        val a = QuizAnswer(questionId = "q1", selectedOptionIndex = -1)
        assertEquals(-1, a.selectedOptionIndex)
        assertFalse(a.isCorrect)
    }

    // ── QuizAttempt Scoring ──────────────────────────────────────────

    @Test
    fun `QuizAttempt default values`() {
        val attempt = QuizAttempt()
        assertEquals("", attempt.id)
        assertEquals("", attempt.userId)
        assertTrue(attempt.questions.isEmpty())
        assertTrue(attempt.answers.isEmpty())
        assertEquals(0, attempt.totalScore)
        assertEquals(0, attempt.maxScore)
        assertEquals(0f, attempt.percentage)
    }

    @Test
    fun `QuizAttempt calculates correct percentage for perfect score`() {
        val percentage = calculatePercentage(totalScore = 10, maxScore = 10)
        assertEquals(100f, percentage)
    }

    @Test
    fun `QuizAttempt calculates correct percentage for partial score`() {
        val percentage = calculatePercentage(totalScore = 7, maxScore = 10)
        assertEquals(70f, percentage)
    }

    @Test
    fun `QuizAttempt calculates correct percentage for zero score`() {
        val percentage = calculatePercentage(totalScore = 0, maxScore = 10)
        assertEquals(0f, percentage)
    }

    @Test
    fun `QuizAttempt handles zero max score gracefully`() {
        // Should not divide by zero
        val percentage = calculatePercentage(totalScore = 0, maxScore = 0)
        assertEquals(0f, percentage)
    }

    @Test
    fun `score calculation from answers - all correct`() {
        val answers = listOf(
            QuizAnswer(questionId = "q1", isCorrect = true),
            QuizAnswer(questionId = "q2", isCorrect = true),
            QuizAnswer(questionId = "q3", isCorrect = true)
        )
        val score = answers.count { it.isCorrect }
        assertEquals(3, score)
    }

    @Test
    fun `score calculation from answers - mixed results`() {
        val answers = listOf(
            QuizAnswer(questionId = "q1", isCorrect = true),
            QuizAnswer(questionId = "q2", isCorrect = false),
            QuizAnswer(questionId = "q3", isCorrect = true),
            QuizAnswer(questionId = "q4", isCorrect = false),
            QuizAnswer(questionId = "q5", isCorrect = true)
        )
        val score = answers.count { it.isCorrect }
        assertEquals(3, score)
        val percentage = calculatePercentage(score, answers.size)
        assertEquals(60f, percentage, 0.01f)
    }

    @Test
    fun `score calculation from answers - all incorrect`() {
        val answers = listOf(
            QuizAnswer(questionId = "q1", isCorrect = false),
            QuizAnswer(questionId = "q2", isCorrect = false)
        )
        val score = answers.count { it.isCorrect }
        assertEquals(0, score)
    }

    // ── QuizAttemptDoc ───────────────────────────────────────────────

    @Test
    fun `QuizAttemptDoc default values`() {
        val doc = QuizAttemptDoc()
        assertEquals("", doc.userId)
        assertEquals("", doc.questionsJson)
        assertEquals("", doc.answersJson)
        assertEquals(0, doc.totalScore)
        assertEquals(0, doc.maxScore)
        assertEquals(0f, doc.percentage)
    }

    @Test
    fun `QuizAttemptDoc stores language pair`() {
        val doc = QuizAttemptDoc(
            userId = "user1",
            primaryLanguageCode = "en-US",
            targetLanguageCode = "ja"
        )
        assertEquals("en-US", doc.primaryLanguageCode)
        assertEquals("ja", doc.targetLanguageCode)
    }

    // ── QuizStats ────────────────────────────────────────────────────

    @Test
    fun `QuizStats default values`() {
        val stats = QuizStats()
        assertEquals("", stats.primaryLanguageCode)
        assertEquals("", stats.targetLanguageCode)
        assertEquals(0, stats.attemptCount)
        assertEquals(0f, stats.averageScore)
        assertEquals(0, stats.highestScore)
        assertEquals(0, stats.lowestScore)
        assertNull(stats.lastAttemptAt)
    }

    @Test
    fun `QuizStats stores aggregated data`() {
        val stats = QuizStats(
            primaryLanguageCode = "en-US",
            targetLanguageCode = "ja",
            attemptCount = 5,
            averageScore = 75.5f,
            highestScore = 100,
            lowestScore = 50
        )
        assertEquals(5, stats.attemptCount)
        assertEquals(75.5f, stats.averageScore)
        assertEquals(100, stats.highestScore)
        assertEquals(50, stats.lowestScore)
    }

    // ── UserCoinStats ────────────────────────────────────────────────

    @Test
    fun `UserCoinStats default values`() {
        val coinStats = UserCoinStats()
        assertEquals(0, coinStats.coinTotal)
        assertTrue(coinStats.coinByLang.isEmpty())
    }

    @Test
    fun `UserCoinStats stores coin breakdown by language`() {
        val coinStats = UserCoinStats(
            coinTotal = 150,
            coinByLang = mapOf("ja" to 50, "zh-HK" to 75, "ko" to 25)
        )
        assertEquals(150, coinStats.coinTotal)
        assertEquals(50, coinStats.coinByLang["ja"])
        assertEquals(75, coinStats.coinByLang["zh-HK"])
        assertEquals(25, coinStats.coinByLang["ko"])
    }

    @Test
    fun `UserCoinStats coin total should match sum of breakdown`() {
        val breakdown = mapOf("ja" to 50, "zh-HK" to 75, "ko" to 25)
        val total = breakdown.values.sum()
        val coinStats = UserCoinStats(coinTotal = total, coinByLang = breakdown)
        assertEquals(coinStats.coinByLang.values.sum(), coinStats.coinTotal)
    }

    // ── Helper Functions ─────────────────────────────────────────────

    /**
     * Calculate percentage score from total and max.
     */
    private fun calculatePercentage(totalScore: Int, maxScore: Int): Float {
        if (maxScore == 0) return 0f
        return (totalScore.toFloat() / maxScore) * 100
    }
}
