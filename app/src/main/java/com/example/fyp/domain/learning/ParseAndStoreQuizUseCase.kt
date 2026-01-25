package com.example.fyp.domain.learning

import com.example.fyp.data.learning.FirestoreQuizRepository
import com.example.fyp.data.learning.QuizParser
import com.example.fyp.model.QuizAnswer
import com.example.fyp.model.QuizAttempt
import com.example.fyp.model.QuizQuestion
import com.google.firebase.Timestamp
import javax.inject.Inject

/**
 * Use case to handle quiz operations:
 * - Parse quiz questions from learning content
 * - Calculate scores
 * - Save quiz attempts
 */
class ParseAndStoreQuizUseCase @Inject constructor(
    private val quizRepository: FirestoreQuizRepository
) {

    /**
     * Parse questions from AI-generated learning content
     */
    fun parseQuestionsFromContent(content: String): List<QuizQuestion> {
        return QuizParser.parseQuizFromContent(content)
    }

    /**
     * Create a quiz attempt from parsed questions
     */
    fun createAttempt(
        userId: String,
        primaryLanguageCode: String,
        targetLanguageCode: String,
        questions: List<QuizQuestion>
    ): QuizAttempt {
        return QuizAttempt(
            id = "", // Will be generated on save
            userId = userId,
            primaryLanguageCode = primaryLanguageCode,
            targetLanguageCode = targetLanguageCode,
            questions = questions,
            answers = emptyList(),
            startedAt = Timestamp.now(),
            completedAt = null,
            totalScore = 0,
            maxScore = questions.size,
            percentage = 0f
        )
    }

    /**
     * Record user's answer to a question
     */
    fun recordAnswer(
        attempt: QuizAttempt,
        questionId: String,
        selectedOptionIndex: Int,
        timeSpentSeconds: Int = 0
    ): QuizAttempt {
        val question = attempt.questions.find { it.id == questionId } ?: return attempt
        val isCorrect = selectedOptionIndex == question.correctOptionIndex && selectedOptionIndex >= 0

        val answer = QuizAnswer(
            questionId = questionId,
            selectedOptionIndex = selectedOptionIndex,
            isCorrect = isCorrect,
            timeSpentSeconds = timeSpentSeconds
        )

        val updatedAnswers = attempt.answers.toMutableList()
        // Replace if answer already exists, otherwise add
        val existingIndex = updatedAnswers.indexOfFirst { it.questionId == questionId }
        if (existingIndex >= 0) {
            updatedAnswers[existingIndex] = answer
        } else {
            updatedAnswers.add(answer)
        }

        return attempt.copy(answers = updatedAnswers)
    }

    /**
     * Calculate final score and mark attempt as completed
     */
    fun completeAttempt(attempt: QuizAttempt): QuizAttempt {
        val correctCount = attempt.answers.count { it.isCorrect }
        val percentage = if (attempt.maxScore > 0) {
            (correctCount.toFloat() / attempt.maxScore.toFloat()) * 100f
        } else {
            0f
        }

        return attempt.copy(
            completedAt = Timestamp.now(),
            totalScore = correctCount,
            percentage = percentage
        )
    }

    /**
     * Save completed quiz attempt to repository
     */
    suspend fun saveAttempt(uid: String, attempt: QuizAttempt): String {
        return quizRepository.saveAttempt(uid, attempt)
    }

    /**
     * Get attempt history for a language pair
     */
    suspend fun getAttemptHistory(
        uid: String,
        primaryLanguageCode: String,
        targetLanguageCode: String
    ): List<QuizAttempt> {
        return quizRepository.getAttemptsByLanguagePair(uid, primaryLanguageCode, targetLanguageCode)
    }

    /**
     * Get quiz statistics
     */
    suspend fun getQuizStats(
        uid: String,
        primaryLanguageCode: String,
        targetLanguageCode: String
    ) = quizRepository.getQuizStats(uid, primaryLanguageCode, targetLanguageCode)

    /**
     * Get recent quiz attempts across all language pairs
     */
    suspend fun getRecentAttempts(uid: String, limit: Long = 10) =
        quizRepository.getRecentAttempts(uid, limit)
}
