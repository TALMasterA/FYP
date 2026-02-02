package com.example.fyp.data.learning

import com.example.fyp.core.decodeOrDefault
import com.example.fyp.model.*
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.channels.awaitClose
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

    private fun coinStatsDoc(uid: String) =
        db.collection("users").document(uid)
            .collection("user_stats")
            .document("coins")

    private fun coinAwardDoc(uid: String, versionKey: String) =
        db.collection("users").document(uid)
            .collection("coin_awards")
            .document(versionKey)

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
            percentage = attempt.percentage,
            generatedHistoryCountAtGenerate = attempt.generatedHistoryCountAtGenerate
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
            questions = json.decodeOrDefault<List<QuizQuestion>>(doc.questionsJson, emptyList()),
            answers = json.decodeOrDefault<List<QuizAnswer>>(doc.answersJson, emptyList()),
            startedAt = doc.startedAt,
            completedAt = doc.completedAt,
            totalScore = doc.totalScore,
            maxScore = doc.maxScore,
            percentage = doc.percentage,
            generatedHistoryCountAtGenerate = doc.generatedHistoryCountAtGenerate
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
                questions = json.decodeOrDefault<List<QuizQuestion>>(data.questionsJson, emptyList()),
                answers = json.decodeOrDefault<List<QuizAnswer>>(data.answersJson, emptyList()),
                startedAt = data.startedAt,
                completedAt = data.completedAt,
                totalScore = data.totalScore,
                maxScore = data.maxScore,
                percentage = data.percentage,
                generatedHistoryCountAtGenerate = data.generatedHistoryCountAtGenerate
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
                percentage = data.percentage,
                generatedHistoryCountAtGenerate = data.generatedHistoryCountAtGenerate
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
        return json.decodeOrDefault<List<QuizQuestion>>(doc.questionsJson, emptyList())
    }

    // ---- Coins (first-attempt rewards) ----

    fun observeUserCoinStats(uid: String): Flow<UserCoinStats> = callbackFlow {
        val reg = coinStatsDoc(uid).addSnapshotListener { snap, _ ->
            if (snap != null && snap.exists()) {
                val stats = snap.toObject(UserCoinStats::class.java) ?: UserCoinStats()
                trySend(stats)
            } else {
                trySend(UserCoinStats())
            }
        }
        awaitClose { reg.remove() }
    }

    suspend fun fetchUserCoinStats(uid: String): UserCoinStats? {
        val snap = coinStatsDoc(uid).get().await()
        return if (snap.exists()) snap.toObject(UserCoinStats::class.java) else null
    }

    // Track last awarded quiz count per language pair
    private fun lastAwardedCountDoc(uid: String, primaryCode: String, targetCode: String) =
        db.collection("users").document(uid)
            .collection("last_awarded_quiz")
            .document("${primaryCode}__${targetCode}")

    /**
     * Get the last awarded quiz count for a language pair.
     * Returns null if no quiz has been awarded coins yet.
     */
    suspend fun getLastAwardedQuizCount(uid: String, primaryCode: String, targetCode: String): Int? {
        val snap = lastAwardedCountDoc(uid, primaryCode, targetCode).get().await()
        return if (snap.exists()) snap.getLong("count")?.toInt() else null
    }

    /**
     * Award coins for first attempt of a generated quiz version.
     * Returns true if coins were awarded, false otherwise.
     *
     * Anti-Cheat Rules:
     * 1. 1 coin per correct answer on first attempt only
     * 2. Quiz version (historyCountAtGenerate) must EQUAL current sheet version (prevents retaking old quiz after adding history)
     * 3. Quiz count must be at least 10 HIGHER than the previous awarded quiz count (prevents farming)
     * 4. First quiz for a language pair is always eligible (no minimum threshold)
     * 5. Each quiz version can only be awarded once (tracked by versionKey)
     */
    suspend fun awardCoinsIfEligible(
        uid: String,
        attempt: QuizAttempt,
        latestHistoryCount: Int?
    ): Boolean {
        if (attempt.generatedHistoryCountAtGenerate <= 0) return false
        if (attempt.totalScore <= 0) return false // No coins to award

        val versionKey = "${attempt.primaryLanguageCode}__${attempt.targetLanguageCode}__${attempt.generatedHistoryCountAtGenerate}"

        return db.runTransaction { tx ->
            // Check 1: Already awarded for this exact version?
            val awardDoc = tx.get(coinAwardDoc(uid, versionKey))
            if (awardDoc.exists()) return@runTransaction false // already awarded

            // Check 2: Quiz count must EQUAL current sheet count
            // This prevents: user takes quiz at count=50, adds history to count=60, retakes same quiz to earn coins
            val currentCount = latestHistoryCount ?: return@runTransaction false
            if (currentCount != attempt.generatedHistoryCountAtGenerate) return@runTransaction false

            // Check 3: Anti-cheat - quiz count must be at least 10 HIGHER than last awarded quiz count
            // (First quiz for a language pair is always eligible)
            val lastAwardedDoc = tx.get(lastAwardedCountDoc(uid, attempt.primaryLanguageCode, attempt.targetLanguageCode))
            if (lastAwardedDoc.exists()) {
                val lastCount = lastAwardedDoc.getLong("count")?.toInt() ?: 0
                val minRequired = lastCount + 10
                if (attempt.generatedHistoryCountAtGenerate < minRequired) {
                    // User needs at least 10 more records than previous awarded quiz to earn coins
                    return@runTransaction false
                }
            }

            // All checks passed - award coins
            val statsSnap = tx.get(coinStatsDoc(uid))
            val current = if (statsSnap.exists()) statsSnap.toObject(UserCoinStats::class.java) ?: UserCoinStats() else UserCoinStats()

            val newTotal = current.coinTotal + attempt.totalScore
            val perLang = current.coinByLang.toMutableMap()
            val langKey = attempt.targetLanguageCode
            perLang[langKey] = (perLang[langKey] ?: 0) + attempt.totalScore

            // Update coin stats
            tx.set(coinStatsDoc(uid), UserCoinStats(coinTotal = newTotal, coinByLang = perLang))

            // Mark this version as awarded
            tx.set(coinAwardDoc(uid, versionKey), mapOf(
                "awarded" to true,
                "attemptId" to attempt.id,
                "coinsAwarded" to attempt.totalScore,
                "awardedAt" to Timestamp.now()
            ))

            // Update last awarded count for this language pair (for anti-cheat)
            tx.set(lastAwardedCountDoc(uid, attempt.primaryLanguageCode, attempt.targetLanguageCode), mapOf(
                "count" to attempt.generatedHistoryCountAtGenerate,
                "lastAwardedAt" to Timestamp.now()
            ))

            true
        }.await()
    }

    /**
     * Deduct coins from user's balance
     * Returns true if successful, false if insufficient coins
     */
    suspend fun deductCoins(uid: String, amount: Int): Boolean {
        return db.runTransaction { tx ->
            val statsSnap = tx.get(coinStatsDoc(uid))
            val current = if (statsSnap.exists()) statsSnap.toObject(UserCoinStats::class.java) ?: UserCoinStats() else UserCoinStats()

            // Check if user has enough coins
            if (current.coinTotal < amount) {
                return@runTransaction false
            }

            // Deduct coins
            val newTotal = current.coinTotal - amount
            tx.set(coinStatsDoc(uid), current.copy(coinTotal = newTotal))

            true
        }.await()
    }
}