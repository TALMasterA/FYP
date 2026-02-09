package com.example.fyp.domain.learning

import com.example.fyp.model.*
import com.google.firebase.Timestamp
import kotlinx.coroutines.flow.Flow

/**
 * Data class for generated quiz document
 */
data class GeneratedQuizDoc(
    val primaryLanguageCode: String = "",
    val targetLanguageCode: String = "",
    val questionsJson: String = "",
    val generatedAt: Timestamp = Timestamp.now(),
    val historyCountAtGenerate: Int = 0
)

/**
 * Repository interface for managing quizzes and quiz-related data.
 * Abstracts the data source implementation (e.g., Firestore) from the domain layer.
 */
interface QuizRepository {
    /**
     * Saves a quiz attempt.
     * @return The attempt ID
     */
    suspend fun saveAttempt(uid: String, attempt: QuizAttempt): String

    /**
     * Retrieves a quiz attempt by ID.
     */
    suspend fun getAttempt(uid: String, attemptId: String): QuizAttempt?

    /**
     * Gets all quiz attempts for a language pair.
     */
    suspend fun getAttemptsByLanguagePair(
        uid: String,
        primaryCode: String,
        targetCode: String,
        limit: Long = 50
    ): List<QuizAttempt>

    /**
     * Gets quiz statistics for a language pair.
     */
    suspend fun getQuizStats(
        uid: String,
        primaryCode: String,
        targetCode: String
    ): QuizStats?

    /**
     * Gets recent quiz attempts.
     */
    suspend fun getRecentAttempts(uid: String, limit: Long = 10): List<QuizAttempt>

    /**
     * Gets the generated quiz document for a language pair.
     */
    suspend fun getGeneratedQuizDoc(
        uid: String,
        primaryCode: String,
        targetCode: String
    ): GeneratedQuizDoc?

    /**
     * Creates or updates a generated quiz.
     */
    suspend fun upsertGeneratedQuiz(
        uid: String,
        primaryCode: String,
        targetCode: String,
        quizData: String,
        historyCountAtGenerate: Int
    )

    /**
     * Gets the questions from a generated quiz.
     */
    suspend fun getGeneratedQuizQuestions(
        uid: String,
        primaryCode: String,
        targetCode: String
    ): List<QuizQuestion>

    /**
     * Observes user coin statistics.
     */
    fun observeUserCoinStats(uid: String): Flow<UserCoinStats>

    /**
     * Fetches user coin statistics.
     */
    suspend fun fetchUserCoinStats(uid: String): UserCoinStats?

    /**
     * Gets the last awarded quiz count for anti-cheat.
     */
    suspend fun getLastAwardedQuizCount(
        uid: String,
        primaryCode: String,
        targetCode: String
    ): Int?

    /**
     * Awards coins if the user is eligible (with anti-cheat checks).
     * @return True if coins were awarded, false otherwise
     */
    suspend fun awardCoinsIfEligible(
        uid: String,
        attempt: QuizAttempt,
        latestHistoryCount: Int?
    ): Boolean

    /**
     * Deducts coins from user balance.
     * @return The new balance, or -1 if insufficient funds
     */
    suspend fun deductCoins(uid: String, amount: Int): Int
}
