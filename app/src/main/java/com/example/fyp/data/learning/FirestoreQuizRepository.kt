package com.example.fyp.data.learning

import com.example.fyp.core.decodeOrDefault
import com.example.fyp.data.cloud.CloudQuizClient
import com.example.fyp.domain.learning.GeneratedQuizDoc
import com.example.fyp.domain.learning.QuizRepository
import com.example.fyp.domain.learning.QuizMetadata
import com.example.fyp.model.*
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.channels.awaitClose
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

class FirestoreQuizRepository @Inject constructor(
    private val db: FirebaseFirestore,
    private val cloudQuizClient: CloudQuizClient
) : QuizRepository {
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

    override suspend fun saveAttempt(uid: UserId, attempt: QuizAttempt): String {
        val attemptId = attempt.id.ifEmpty { db.collection("dummy").document().id }

        val doc = QuizAttemptDoc(
            userId = uid.value,
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

        docRef(uid.value, attemptId).set(doc).await()
        updateStats(uid.value, attempt)
        return attemptId
    }

    override suspend fun getAttempt(uid: UserId, attemptId: String): QuizAttempt? {
        val snap = docRef(uid.value, attemptId).get().await()
        if (!snap.exists()) return null

        val doc = snap.toObject(QuizAttemptDoc::class.java) ?: return null
        return QuizAttempt(
            id = attemptId,
            userId = uid.value,
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

    override suspend fun getAttemptsByLanguagePair(
        uid: UserId,
        primaryCode: LanguageCode,
        targetCode: LanguageCode,
        limit: Long
    ): List<QuizAttempt> {
        val snap = collectionRef(uid.value)
            .whereEqualTo("primaryLanguageCode", primaryCode.value)
            .whereEqualTo("targetLanguageCode", targetCode.value)
            .orderBy("completedAt")
            .limit(limit)
            .get()
            .await()

        return snap.documents.mapNotNull { docSnap ->
            val data = docSnap.toObject(QuizAttemptDoc::class.java) ?: return@mapNotNull null
            QuizAttempt(
                id = docSnap.id,
                userId = uid.value,
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

    override suspend fun getQuizStats(
        uid: UserId,
        primaryLanguageCode: LanguageCode,
        targetLanguageCode: LanguageCode
    ): QuizStats? {
        val snap = statsDocRef(uid.value, primaryLanguageCode.value, targetLanguageCode.value).get().await()
        return if (snap.exists()) snap.toObject(QuizStats::class.java) else null
    }

    override suspend fun getRecentAttempts(uid: UserId, limit: Long): List<QuizAttempt> {
        val snap = collectionRef(uid.value)
            .orderBy("completedAt")
            .limit(limit)
            .get()
            .await()

        return snap.documents.mapNotNull { docSnap ->
            val data = docSnap.toObject(QuizAttemptDoc::class.java) ?: return@mapNotNull null
            QuizAttempt(
                id = docSnap.id,
                userId = uid.value,
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

        // Use Firestore transaction for atomic updates to prevent race conditions
        db.runTransaction { transaction ->
            val snapshot = transaction.get(statsRef)

            if (snapshot.exists()) {
                val current = snapshot.toObject(QuizStats::class.java) ?: QuizStats()
                val newCount = current.attemptCount + 1
                val newAverage = (current.averageScore * current.attemptCount + attempt.percentage) / newCount

                val updates = mapOf(
                    "attemptCount" to newCount,
                    "averageScore" to newAverage,
                    "highestScore" to maxOf(current.highestScore, attempt.totalScore),
                    "lowestScore" to if (current.lowestScore == 0) attempt.totalScore
                        else minOf(current.lowestScore, attempt.totalScore),
                    "lastAttemptAt" to attempt.completedAt
                )
                transaction.update(statsRef, updates)
            } else {
                // First attempt - use set
                val stats = QuizStats(
                    primaryLanguageCode = attempt.primaryLanguageCode,
                    targetLanguageCode = attempt.targetLanguageCode,
                    attemptCount = 1,
                    averageScore = attempt.percentage,
                    highestScore = attempt.totalScore,
                    lowestScore = attempt.totalScore,
                    lastAttemptAt = attempt.completedAt
                )
                transaction.set(statsRef, stats)
            }
        }.await()
    }

    // ---- Generated quiz (cached per sheet version) ----

    private fun generatedQuizDocRef(uid: String, primaryCode: String, targetCode: String) =
        db.collection("users")
            .document(uid)
            .collection("generated_quizzes")
            .document("${primaryCode}__${targetCode}")

    override suspend fun getGeneratedQuizDoc(
        uid: UserId,
        primaryLanguageCode: LanguageCode,
        targetLanguageCode: LanguageCode
    ): GeneratedQuizDoc? {
        val snap = generatedQuizDocRef(uid.value, primaryLanguageCode.value, targetLanguageCode.value).get().await()
        if (!snap.exists()) return null
        return snap.toObject(GeneratedQuizDoc::class.java)
    }

    override suspend fun upsertGeneratedQuiz(
        uid: UserId,
        primaryCode: LanguageCode,
        targetCode: LanguageCode,
        quizData: String,
        historyCountAtGenerate: Int
    ) {
        val questions = try {
            json.decodeFromString<List<QuizQuestion>>(quizData)
        } catch (e: Exception) {
            return // Invalid quiz data
        }
        
        val doc = GeneratedQuizDoc(
            primaryLanguageCode = primaryCode.value,
            targetLanguageCode = targetCode.value,
            questionsJson = quizData,
            generatedAt = Timestamp.now(),
            historyCountAtGenerate = historyCountAtGenerate
        )
        generatedQuizDocRef(uid.value, primaryCode.value, targetCode.value).set(doc).await()
    }

    override suspend fun getGeneratedQuizQuestions(
        uid: UserId,
        primaryLanguageCode: LanguageCode,
        targetLanguageCode: LanguageCode
    ): List<QuizQuestion> {
        val doc = getGeneratedQuizDoc(uid, primaryLanguageCode, targetLanguageCode) ?: return emptyList()
        return json.decodeOrDefault<List<QuizQuestion>>(doc.questionsJson, emptyList())
    }

    /**
     * Batch retrieves quiz metadata for multiple language pairs.
     * This combines generated quiz docs and last awarded counts in optimized queries.
     */
    override suspend fun getBatchQuizMetadata(
        uid: UserId,
        primary: LanguageCode,
        targets: List<String>
    ): Map<String, QuizMetadata> {
        if (targets.isEmpty()) return emptyMap()

        val result = mutableMapOf<String, QuizMetadata>()
        
        // Build document IDs
        val quizDocIds = targets.map { "${primary.value}__${it}" }

        // Process in chunks of 10 (Firestore whereIn limit)
        targets.chunked(10).zip(quizDocIds.chunked(10)).forEach { (chunkTargets, chunkQuizIds) ->
            // Fetch quiz docs and awarded counts in parallel
            val chunkResults = coroutineScope {
                val quizDeferred = async {
                    db.collection("users")
                        .document(uid.value)
                        .collection("generated_quizzes")
                        .whereIn(com.google.firebase.firestore.FieldPath.documentId(), chunkQuizIds)
                        .get()
                        .await()
                }
                val awardedDeferred = async {
                    db.collection("users")
                        .document(uid.value)
                        .collection("last_awarded_quiz")
                        .whereIn(com.google.firebase.firestore.FieldPath.documentId(), chunkQuizIds)
                        .get()
                        .await()
                }
                listOf(quizDeferred, awardedDeferred).awaitAll()
            }
            val quizSnapshot = chunkResults[0]
            val awardedSnapshot = chunkResults[1]
            
            val foundQuizDocs = quizSnapshot.documents.associateBy { it.id }
            val foundAwardedDocs = awardedSnapshot.documents.associateBy { it.id }
            
            // Build result for this chunk
            chunkTargets.forEachIndexed { idx, target ->
                val docId = chunkQuizIds[idx]
                val quizDoc = foundQuizDocs[docId]
                val awardedDoc = foundAwardedDocs[docId]
                
                result[target] = QuizMetadata(
                    quizHistoryCount = if (quizDoc != null && quizDoc.exists()) {
                        quizDoc.getLong("historyCountAtGenerate")?.toInt()
                    } else null,
                    lastAwardedCount = if (awardedDoc != null && awardedDoc.exists()) {
                        awardedDoc.getLong("count")?.toInt()
                    } else null
                )
            }
        }

        return result
    }

    // ---- Coins (first-attempt rewards) ----

    override fun observeUserCoinStats(uid: UserId): Flow<UserCoinStats> = callbackFlow {
        val reg = coinStatsDoc(uid.value).addSnapshotListener { snap, _ ->
            if (snap != null && snap.exists()) {
                val stats = snap.toObject(UserCoinStats::class.java) ?: UserCoinStats()
                trySend(stats)
            } else {
                trySend(UserCoinStats())
            }
        }
        awaitClose { reg.remove() }
    }

    override suspend fun fetchUserCoinStats(uid: UserId): UserCoinStats? {
        val snap = coinStatsDoc(uid.value).get().await()
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
    override suspend fun getLastAwardedQuizCount(uid: UserId, primaryCode: LanguageCode, targetCode: LanguageCode): Int? {
        val snap = lastAwardedCountDoc(uid.value, primaryCode.value, targetCode.value).get().await()
        return if (snap.exists()) snap.getLong("count")?.toInt() else null
    }

    /**
     * Award coins for first attempt of a generated quiz version.
     * Returns true if coins were awarded, false otherwise.
     *
     * Anti-Cheat Rules (enforced SERVER-SIDE via Cloud Function):
     * 1. 1 coin per correct answer on first attempt only
     * 2. Quiz version must EQUAL current sheet version (read from Firestore server-side)
     * 3. Quiz count must be at least 10 HIGHER than the previous awarded quiz count
     * 4. First quiz for a language pair is always eligible (no minimum threshold)
     * 5. Each quiz version can only be awarded once (tracked by versionKey)
     *
     * Note: The latestHistoryCount parameter is ignored - the server reads the actual
     * sheet version from Firestore to prevent client-side manipulation.
     */
    override suspend fun awardCoinsIfEligible(
        uid: UserId,
        attempt: QuizAttempt,
        latestHistoryCount: Int?
    ): Boolean {
        // Use server-side Cloud Function for tamper-proof verification
        val result = cloudQuizClient.awardQuizCoins(
            attemptId = attempt.id,
            primaryLanguageCode = attempt.primaryLanguageCode,
            targetLanguageCode = attempt.targetLanguageCode,
            generatedHistoryCountAtGenerate = attempt.generatedHistoryCountAtGenerate,
            totalScore = attempt.totalScore
        )
        return result.awarded
    }

    /**
     * Deduct coins from user's balance.
     * Returns the new coin balance if successful, or -1 if insufficient coins.
     * This avoids a separate fetchUserCoinStats call after deduction.
     */
    override suspend fun deductCoins(uid: UserId, amount: Int): Int {
        return db.runTransaction { tx ->
            val statsSnap = tx.get(coinStatsDoc(uid.value))
            val current = if (statsSnap.exists()) statsSnap.toObject(UserCoinStats::class.java) ?: UserCoinStats() else UserCoinStats()

            // Check if user has enough coins
            if (current.coinTotal < amount) {
                return@runTransaction -1
            }

            // Deduct coins
            val newTotal = current.coinTotal - amount
            tx.set(coinStatsDoc(uid.value), current.copy(coinTotal = newTotal))
            newTotal
        }.await()
    }
}