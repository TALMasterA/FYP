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
 * Metadata for quiz and last awarded count (without full quiz data).
 */
data class QuizMetadata(
    val quizHistoryCount: Int?,
    val lastAwardedCount: Int?
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
    suspend fun saveAttempt(uid: UserId, attempt: QuizAttempt): String

    /**
     * Retrieves a quiz attempt by ID.
     */
    suspend fun getAttempt(uid: UserId, attemptId: String): QuizAttempt?

    /**
     * Gets all quiz attempts for a language pair.
     */
    suspend fun getAttemptsByLanguagePair(
        uid: UserId,
        primaryCode: LanguageCode,
        targetCode: LanguageCode,
        limit: Long = 50
    ): List<QuizAttempt>

    /**
     * Gets quiz statistics for a language pair.
     */
    suspend fun getQuizStats(
        uid: UserId,
        primaryCode: LanguageCode,
        targetCode: LanguageCode
    ): QuizStats?

    /**
     * Gets recent quiz attempts.
     */
    suspend fun getRecentAttempts(uid: UserId, limit: Long = 10): List<QuizAttempt>

    /**
     * Gets the generated quiz document for a language pair.
     */
    suspend fun getGeneratedQuizDoc(
        uid: UserId,
        primaryCode: LanguageCode,
        targetCode: LanguageCode
    ): GeneratedQuizDoc?

    /**
     * Batch retrieves quiz metadata for multiple language pairs.
     * This is optimized to reduce the number of Firestore reads.
     * @param uid User ID
     * @param primary Primary language code
     * @param targets List of target language codes
     * @return Map of target language code to metadata
     */
    suspend fun getBatchQuizMetadata(
        uid: UserId,
        primary: LanguageCode,
        targets: List<String>
    ): Map<String, QuizMetadata>

    /**
     * Creates or updates a generated quiz.
     */
    suspend fun upsertGeneratedQuiz(
        uid: UserId,
        primaryCode: LanguageCode,
        targetCode: LanguageCode,
        quizData: String,
        historyCountAtGenerate: Int
    )

    /**
     * Gets the questions from a generated quiz.
     */
    suspend fun getGeneratedQuizQuestions(
        uid: UserId,
        primaryCode: LanguageCode,
        targetCode: LanguageCode
    ): List<QuizQuestion>

    /**
     * Observes user coin statistics.
     */
    fun observeUserCoinStats(uid: UserId): Flow<UserCoinStats>

    /**
     * Fetches user coin statistics.
     */
    suspend fun fetchUserCoinStats(uid: UserId): UserCoinStats?

    /**
     * Gets the last awarded quiz count for anti-cheat.
     */
    suspend fun getLastAwardedQuizCount(
        uid: UserId,
        primaryCode: LanguageCode,
        targetCode: LanguageCode
    ): Int?

    /**
     * Awards coins if the user is eligible (with anti-cheat checks).
     * @return True if coins were awarded, false otherwise
     */
    suspend fun awardCoinsIfEligible(
        uid: UserId,
        attempt: QuizAttempt,
        latestHistoryCount: Int?
    ): Boolean

    /**
     * Deducts coins from user balance.
     * @return The new balance, or -1 if insufficient funds
     */
    suspend fun deductCoins(uid: UserId, amount: Int): Int
}
