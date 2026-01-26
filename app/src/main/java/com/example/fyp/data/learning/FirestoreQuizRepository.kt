package com.example.fyp.data.learning

import com.example.fyp.model.QuizAttempt
import com.example.fyp.model.QuizAttemptDoc
import com.example.fyp.model.QuizStats
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import com.example.fyp.model.QuizQuestion
import kotlinx.serialization.decodeFromString



class FirestoreQuizRepository @Inject constructor(
    private val db: FirebaseFirestore
) {
    private fun docRef(uid: String, attemptId: String) =
        db.collection("users")
            .document(uid)
            .collection("quiz_attempts")
            .document(attemptId)

    private fun collectionRef(uid: String) =
        db.collection("users")
            .document(uid)
            .collection("quiz_attempts")

    private fun statsDocRef(uid: String, primaryCode: String, targetCode: String) =
        db.collection("users")
            .document(uid)
            .collection("quiz_stats")
            .document("${primaryCode}__${targetCode}")

    /**
     * Save a completed quiz attempt to Firestore
     */
    suspend fun saveAttempt(uid: String, attempt: QuizAttempt): String {
        val attemptId = attempt.id.ifEmpty { db.collection("dummy").document().id }

        val doc = QuizAttemptDoc(
            userId = uid,
            primaryLanguageCode = attempt.primaryLanguageCode,
            targetLanguageCode = attempt.targetLanguageCode,
            questionsJson = Json.encodeToString(attempt.questions),
            answersJson = Json.encodeToString(attempt.answers),
            startedAt = attempt.startedAt,
            completedAt = attempt.completedAt ?: Timestamp.now(),
            totalScore = attempt.totalScore,
            maxScore = attempt.maxScore,
            percentage = attempt.percentage
        )

        docRef(uid, attemptId).set(doc).await()

        // Update stats
        updateStats(uid, attempt)

        return attemptId
    }

    /**
     * Retrieve a specific quiz attempt
     */
    suspend fun getAttempt(uid: String, attemptId: String): QuizAttempt? {
        val snap = docRef(uid, attemptId).get().await()
        return if (snap.exists()) {
            val doc = snap.toObject(QuizAttemptDoc::class.java) ?: return null
            // Convert back from JSON if needed
            QuizAttempt(
                id = attemptId,
                userId = uid,
                primaryLanguageCode = doc.primaryLanguageCode,
                targetLanguageCode = doc.targetLanguageCode,
                questions = try {
                    Json.decodeFromString(doc.questionsJson)
                } catch (e: Exception) {
                    emptyList()
                },
                answers = try {
                    Json.decodeFromString(doc.answersJson)
                } catch (e: Exception) {
                    emptyList()
                },
                startedAt = doc.startedAt,
                completedAt = doc.completedAt,
                totalScore = doc.totalScore,
                maxScore = doc.maxScore,
                percentage = doc.percentage
            )
        } else {
            null
        }
    }

    /**
     * Get all quiz attempts for a specific language pair
     */
    suspend fun getAttemptsByLanguagePair(
        uid: String,
        primaryLanguageCode: String,
        targetLanguageCode: String
    ): List<QuizAttempt> {
        val snap = collectionRef(uid)
            .whereEqualTo("primaryLanguageCode", primaryLanguageCode)
            .whereEqualTo("targetLanguageCode", targetLanguageCode)
            .orderBy("completedAt")
            .get()
            .await()

        return snap.documents.mapNotNull { doc ->
            val data = doc.toObject(QuizAttemptDoc::class.java) ?: return@mapNotNull null
            QuizAttempt(
                id = doc.id,
                userId = uid,
                primaryLanguageCode = data.primaryLanguageCode,
                targetLanguageCode = data.targetLanguageCode,
                questions = try {
                    Json.decodeFromString(data.questionsJson)
                } catch (e: Exception) {
                    emptyList()
                },
                answers = try {
                    Json.decodeFromString(data.answersJson)
                } catch (e: Exception) {
                    emptyList()
                },
                startedAt = data.startedAt,
                completedAt = data.completedAt,
                totalScore = data.totalScore,
                maxScore = data.maxScore,
                percentage = data.percentage
            )
        }
    }

    /**
     * Get quiz statistics for a language pair
     */
    suspend fun getQuizStats(
        uid: String,
        primaryLanguageCode: String,
        targetLanguageCode: String
    ): QuizStats? {
        val snap = statsDocRef(uid, primaryLanguageCode, targetLanguageCode).get().await()
        return if (snap.exists()) {
            snap.toObject(QuizStats::class.java)
        } else {
            null
        }
    }

    /**
     * Get recent quiz attempts (for dashboard/history)
     */
    suspend fun getRecentAttempts(uid: String, limit: Long = 10): List<QuizAttempt> {
        val snap = collectionRef(uid)
            .orderBy("completedAt")
            .limit(limit)
            .get()
            .await()

        return snap.documents.mapNotNull { doc ->
            val data = doc.toObject(QuizAttemptDoc::class.java) ?: return@mapNotNull null
            QuizAttempt(
                id = doc.id,
                userId = uid,
                primaryLanguageCode = data.primaryLanguageCode,
                targetLanguageCode = data.targetLanguageCode,
                questions = emptyList(), // Don't load full questions for recent attempts
                answers = emptyList(),
                startedAt = data.startedAt,
                completedAt = data.completedAt,
                totalScore = data.totalScore,
                maxScore = data.maxScore,
                percentage = data.percentage
            )
        }
    }

    /**
     * Update quiz statistics after a new attempt
     */
    private suspend fun updateStats(uid: String, attempt: QuizAttempt) {
        val statsRef = statsDocRef(uid, attempt.primaryLanguageCode, attempt.targetLanguageCode)
        val currentStats = statsRef.get().await()

        val stats = if (currentStats.exists()) {
            val current = currentStats.toObject(QuizStats::class.java) ?: QuizStats()
            val newCount = current.attemptCount + 1
            val newAverage = (current.averageScore * current.attemptCount + attempt.percentage) / newCount

            current.copy(
                attemptCount = newCount,
                averageScore = newAverage,
                highestScore = maxOf(current.highestScore, attempt.totalScore),
                lowestScore = if (current.lowestScore == 0) attempt.totalScore
                else minOf(current.lowestScore, attempt.totalScore),
                lastAttemptAt = attempt.completedAt
            )
        } else {
            QuizStats(
                primaryLanguageCode = attempt.primaryLanguageCode,
                targetLanguageCode = attempt.targetLanguageCode,
                attemptCount = 1,
                averageScore = attempt.percentage,
                highestScore = attempt.totalScore,
                lowestScore = attempt.totalScore,
                lastAttemptAt = attempt.completedAt
            )
        }

        statsRef.set(stats).await()
    }

    private fun generatedQuizDocRef(uid: String, primaryCode: String, targetCode: String) =
        db.collection("users")
            .document(uid)
            .collection("generated_quizzes")
            .document("${primaryCode}__${targetCode}")

    data class GeneratedQuizDoc(
        val primaryLanguageCode: String = "",
        val targetLanguageCode: String = "",
        val questionsJson: String = "",
        val generatedAt: Timestamp = Timestamp.now(),
        val historyCountAtGenerate: Int = 0
    )

    suspend fun upsertGeneratedQuiz(
        uid: String,
        primaryLanguageCode: String,
        targetLanguageCode: String,
        questions: List<QuizQuestion>,
        historyCountAtGenerate: Int
    ) {
        val doc = GeneratedQuizDoc(
            primaryLanguageCode = primaryLanguageCode,
            targetLanguageCode = targetLanguageCode,
            questionsJson = Json.encodeToString(questions),
            generatedAt = Timestamp.now(),
            historyCountAtGenerate = historyCountAtGenerate
        )
        generatedQuizDocRef(uid, primaryLanguageCode, targetLanguageCode).set(doc).await()
    }

    suspend fun getGeneratedQuizQuestions(
        uid: String,
        primaryLanguageCode: String,
        targetLanguageCode: String
    ): List<QuizQuestion> {
        val snap = generatedQuizDocRef(uid, primaryLanguageCode, targetLanguageCode).get().await()
        if (!snap.exists()) return emptyList()
        val doc = snap.toObject(GeneratedQuizDoc::class.java) ?: return emptyList()

        return try {
            Json.decodeFromString(doc.questionsJson)
        } catch (_: Exception) {
            emptyList()
        }
    }
}