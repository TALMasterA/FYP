package com.example.fyp.data.learning

import com.example.fyp.model.QuizAnswer
import com.example.fyp.model.QuizAttempt
import com.example.fyp.model.QuizAttemptDoc
import com.example.fyp.model.QuizQuestion
import com.example.fyp.model.QuizStats
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

class FirestoreQuizRepository @Inject constructor(
    private val db: FirebaseFirestore
) {
    private val json = Json { ignoreUnknownKeys = true }

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

    suspend fun saveAttempt(uid: String, attempt: QuizAttempt): String {
        val attemptId = attempt.id.ifEmpty { db.collection("dummy").document().id }

        val doc = QuizAttemptDoc(
            userId = uid,
            primaryLanguageCode = attempt.primaryLanguageCode,
            targetLanguageCode = attempt.targetLanguageCode,
            questionsJson = json.encodeToString<List<QuizQuestion>>(attempt.questions),
            answersJson = json.encodeToString<List<QuizAnswer>>(attempt.answers),
            startedAt = attempt.startedAt,
            completedAt = attempt.completedAt ?: Timestamp.now(),
            totalScore = attempt.totalScore,
            maxScore = attempt.maxScore,
            percentage = attempt.percentage
        )

        docRef(uid, attemptId).set(doc).await()
        updateStats(uid, attempt)
        return attemptId
    }

    suspend fun getAttempt(uid: String, attemptId: String): QuizAttempt? {
        val snap = docRef(uid, attemptId).get().await()
        if (!snap.exists()) return null

        val doc = snap.toObject(QuizAttemptDoc::class.java) ?: return null
        return QuizAttempt(
            id = attemptId,
            userId = uid,
            primaryLanguageCode = doc.primaryLanguageCode,
            targetLanguageCode = doc.targetLanguageCode,
            questions = try {
                json.decodeFromString<List<QuizQuestion>>(doc.questionsJson)
            } catch (_: Exception) {
                emptyList()
            },
            answers = try {
                json.decodeFromString<List<QuizAnswer>>(doc.answersJson)
            } catch (_: Exception) {
                emptyList()
            },
            startedAt = doc.startedAt,
            completedAt = doc.completedAt,
            totalScore = doc.totalScore,
            maxScore = doc.maxScore,
            percentage = doc.percentage
        )
    }

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

        return snap.documents.mapNotNull { docSnap ->
            val data = docSnap.toObject(QuizAttemptDoc::class.java) ?: return@mapNotNull null
            QuizAttempt(
                id = docSnap.id,
                userId = uid,
                primaryLanguageCode = data.primaryLanguageCode,
                targetLanguageCode = data.targetLanguageCode,
                questions = try {
                    json.decodeFromString<List<QuizQuestion>>(data.questionsJson)
                } catch (_: Exception) {
                    emptyList()
                },
                answers = try {
                    json.decodeFromString<List<QuizAnswer>>(data.answersJson)
                } catch (_: Exception) {
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

    suspend fun getQuizStats(
        uid: String,
        primaryLanguageCode: String,
        targetLanguageCode: String
    ): QuizStats? {
        val snap = statsDocRef(uid, primaryLanguageCode, targetLanguageCode).get().await()
        return if (snap.exists()) snap.toObject(QuizStats::class.java) else null
    }

    suspend fun getRecentAttempts(uid: String, limit: Long = 10): List<QuizAttempt> {
        val snap = collectionRef(uid)
            .orderBy("completedAt")
            .limit(limit)
            .get()
            .await()

        return snap.documents.mapNotNull { docSnap ->
            val data = docSnap.toObject(QuizAttemptDoc::class.java) ?: return@mapNotNull null
            QuizAttempt(
                id = docSnap.id,
                userId = uid,
                primaryLanguageCode = data.primaryLanguageCode,
                targetLanguageCode = data.targetLanguageCode,
                questions = emptyList(),
                answers = emptyList(),
                startedAt = data.startedAt,
                completedAt = data.completedAt,
                totalScore = data.totalScore,
                maxScore = data.maxScore,
                percentage = data.percentage
            )
        }
    }

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

    // ---- Generated quiz (cached per sheet version) ----

    data class GeneratedQuizDoc(
        val primaryLanguageCode: String = "",
        val targetLanguageCode: String = "",
        val questionsJson: String = "",
        val generatedAt: Timestamp = Timestamp.now(),
        val historyCountAtGenerate: Int = 0
    )

    private fun generatedQuizDocRef(uid: String, primaryCode: String, targetCode: String) =
        db.collection("users")
            .document(uid)
            .collection("generated_quizzes")
            .document("${primaryCode}__${targetCode}")

    suspend fun getGeneratedQuizDoc(
        uid: String,
        primaryLanguageCode: String,
        targetLanguageCode: String
    ): GeneratedQuizDoc? {
        val snap = generatedQuizDocRef(uid, primaryLanguageCode, targetLanguageCode).get().await()
        if (!snap.exists()) return null
        return snap.toObject(GeneratedQuizDoc::class.java)
    }

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
            questionsJson = json.encodeToString<List<QuizQuestion>>(questions),
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
        val doc = getGeneratedQuizDoc(uid, primaryLanguageCode, targetLanguageCode) ?: return emptyList()
        return try {
            json.decodeFromString<List<QuizQuestion>>(doc.questionsJson)
        } catch (_: Exception) {
            emptyList()
        }
    }
}