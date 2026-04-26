package com.translator.TalknLearn.domain.learning

import com.translator.TalknLearn.data.learning.FirestoreQuizRepository
import com.translator.TalknLearn.model.LanguageCode
import com.translator.TalknLearn.model.QuizAnswer
import com.translator.TalknLearn.model.QuizAttempt
import com.translator.TalknLearn.model.QuizQuestion
import com.translator.TalknLearn.model.UserId
import com.google.firebase.Timestamp
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.*

/**
 * Unit tests for ParseAndStoreQuizUseCase.
 *
 * Tests:
 * 1. createAttempt sets correct fields
 * 2. recordAnswer marks correct answer as isCorrect
 * 3. recordAnswer marks incorrect answer as !isCorrect
 * 4. recordAnswer replaces existing answer for same question
 * 5. recordAnswer with invalid questionId returns attempt unchanged
 * 6. recordAnswer with negative selectedIndex marks as incorrect
 * 7. completeAttempt calculates correct score and percentage
 * 8. completeAttempt with no answers gives 0% score
 * 9. completeAttempt with all correct gives 100%
 * 10. saveAttempt delegates to repository
 * 11. getRecentAttempts delegates to repository
 */
class ParseAndStoreQuizUseCaseTest {

    private lateinit var quizRepository: FirestoreQuizRepository
    private lateinit var useCase: ParseAndStoreQuizUseCase

    private val sampleQuestions = listOf(
        QuizQuestion(
            id = "q1",
            question = "What is 'hello' in Japanese?",
            options = listOf("こんにちは", "さようなら", "ありがとう", "すみません"),
            correctOptionIndex = 0,
            explanation = "こんにちは means hello",
            type = "multiple_choice"
        ),
        QuizQuestion(
            id = "q2",
            question = "What is 'goodbye' in Japanese?",
            options = listOf("こんにちは", "さようなら", "ありがとう", "すみません"),
            correctOptionIndex = 1,
            explanation = "さようなら means goodbye",
            type = "multiple_choice"
        ),
        QuizQuestion(
            id = "q3",
            question = "What is 'thank you' in Japanese?",
            options = listOf("こんにちは", "さようなら", "ありがとう", "すみません"),
            correctOptionIndex = 2,
            explanation = "ありがとう means thank you",
            type = "multiple_choice"
        )
    )

    @Before
    fun setup() {
        quizRepository = mock()
        useCase = ParseAndStoreQuizUseCase(quizRepository)
    }

    // ── createAttempt ───────────────────────────────────────────────

    @Test
    fun `createAttempt sets correct fields`() {
        val attempt = useCase.createAttempt(
            userId = "u1",
            primaryLanguageCode = "en-US",
            targetLanguageCode = "ja",
            questions = sampleQuestions,
            generatedHistoryCountAtGenerate = 50
        )

        assertEquals("u1", attempt.userId)
        assertEquals("en-US", attempt.primaryLanguageCode)
        assertEquals("ja", attempt.targetLanguageCode)
        assertEquals(3, attempt.questions.size)
        assertTrue(attempt.answers.isEmpty())
        assertEquals(0, attempt.totalScore)
        assertEquals(3, attempt.maxScore)
        assertEquals(0f, attempt.percentage, 0.01f)
        assertNotNull(attempt.startedAt)
        assertNull(attempt.completedAt)
        assertEquals(50, attempt.generatedHistoryCountAtGenerate)
    }

    // ── recordAnswer correct ────────────────────────────────────────

    @Test
    fun `recordAnswer marks correct answer`() {
        val attempt = useCase.createAttempt("u1", "en", "ja", sampleQuestions, 10)

        val updated = useCase.recordAnswer(attempt, "q1", selectedOptionIndex = 0)

        assertEquals(1, updated.answers.size)
        assertTrue(updated.answers[0].isCorrect)
        assertEquals(0, updated.answers[0].selectedOptionIndex)
    }

    // ── recordAnswer incorrect ──────────────────────────────────────

    @Test
    fun `recordAnswer marks incorrect answer`() {
        val attempt = useCase.createAttempt("u1", "en", "ja", sampleQuestions, 10)

        val updated = useCase.recordAnswer(attempt, "q1", selectedOptionIndex = 2)

        assertEquals(1, updated.answers.size)
        assertFalse(updated.answers[0].isCorrect)
    }

    // ── recordAnswer replaces existing ──────────────────────────────

    @Test
    fun `recordAnswer replaces existing answer for same question`() {
        val attempt = useCase.createAttempt("u1", "en", "ja", sampleQuestions, 10)

        val first = useCase.recordAnswer(attempt, "q1", selectedOptionIndex = 2) // wrong
        assertFalse(first.answers[0].isCorrect)

        val second = useCase.recordAnswer(first, "q1", selectedOptionIndex = 0) // correct
        assertEquals(1, second.answers.size) // Still 1 answer, not 2
        assertTrue(second.answers[0].isCorrect)
    }

    // ── recordAnswer invalid questionId ─────────────────────────────

    @Test
    fun `recordAnswer with invalid questionId returns unchanged`() {
        val attempt = useCase.createAttempt("u1", "en", "ja", sampleQuestions, 10)

        val updated = useCase.recordAnswer(attempt, "nonexistent", selectedOptionIndex = 0)

        assertTrue(updated.answers.isEmpty())
        assertEquals(attempt, updated)
    }

    // ── recordAnswer negative selectedIndex ─────────────────────────

    @Test
    fun `recordAnswer with negative selectedIndex marks as incorrect`() {
        val attempt = useCase.createAttempt("u1", "en", "ja", sampleQuestions, 10)

        val updated = useCase.recordAnswer(attempt, "q1", selectedOptionIndex = -1)

        assertFalse(updated.answers[0].isCorrect)
    }

    // ── completeAttempt correct score ────────────────────────────────

    @Test
    fun `completeAttempt calculates correct score and percentage`() {
        var attempt = useCase.createAttempt("u1", "en", "ja", sampleQuestions, 10)
        attempt = useCase.recordAnswer(attempt, "q1", 0) // correct
        attempt = useCase.recordAnswer(attempt, "q2", 1) // correct
        attempt = useCase.recordAnswer(attempt, "q3", 0) // wrong

        val completed = useCase.completeAttempt(attempt)

        assertEquals(2, completed.totalScore)
        assertEquals(66.666f, completed.percentage, 1f) // 2/3 * 100
        assertNotNull(completed.completedAt)
    }

    // ── completeAttempt no answers → 0% ─────────────────────────────

    @Test
    fun `completeAttempt with no answers gives 0 percent`() {
        val attempt = useCase.createAttempt("u1", "en", "ja", sampleQuestions, 10)

        val completed = useCase.completeAttempt(attempt)

        assertEquals(0, completed.totalScore)
        assertEquals(0f, completed.percentage, 0.01f)
    }

    // ── completeAttempt all correct → 100% ──────────────────────────

    @Test
    fun `completeAttempt with all correct gives 100 percent`() {
        var attempt = useCase.createAttempt("u1", "en", "ja", sampleQuestions, 10)
        attempt = useCase.recordAnswer(attempt, "q1", 0) // correct
        attempt = useCase.recordAnswer(attempt, "q2", 1) // correct
        attempt = useCase.recordAnswer(attempt, "q3", 2) // correct

        val completed = useCase.completeAttempt(attempt)

        assertEquals(3, completed.totalScore)
        assertEquals(100f, completed.percentage, 0.01f)
    }

    // ── saveAttempt delegates to repository ────────────────────────

    @Test
    fun `saveAttempt delegates to repository`() = runTest {
        val attempt = useCase.createAttempt("u1", "en", "ja", sampleQuestions, 10)
        whenever(quizRepository.saveAttempt(UserId("u1"), attempt)).thenReturn("attempt123")

        val result = useCase.saveAttempt("u1", attempt)

        assertEquals("attempt123", result)
        verify(quizRepository).saveAttempt(UserId("u1"), attempt)
    }

    // ── getRecentAttempts delegates ──────────────────────────────────

    @Test
    fun `getRecentAttempts delegates to repository`() = runTest {
        val uid = UserId("u1")
        whenever(quizRepository.getRecentAttempts(uid, 5L))
            .thenReturn(emptyList())

        val result = useCase.getRecentAttempts("u1", limit = 5)

        assertTrue(result.isEmpty())
        verify(quizRepository).getRecentAttempts(uid, 5L)
    }

    // ── recordAnswer preserves timeSpent ────────────────────────────

    @Test
    fun `recordAnswer preserves timeSpentSeconds`() {
        val attempt = useCase.createAttempt("u1", "en", "ja", sampleQuestions, 10)

        val updated = useCase.recordAnswer(attempt, "q1", 0, timeSpentSeconds = 15)

        assertEquals(15, updated.answers[0].timeSpentSeconds)
    }
}
