package com.example.fyp.data.cloud

import com.google.firebase.functions.FirebaseFunctions
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Result from server-side quiz coin award.
 */
data class CoinAwardResult(
    val awarded: Boolean,
    val reason: String? = null,
    val coinsAwarded: Int = 0,
    val newTotal: Int = 0
)

/**
 * Client for server-side quiz operations.
 * Uses Cloud Functions for tamper-proof coin eligibility verification.
 */
@Singleton
class CloudQuizClient @Inject constructor(
    private val functions: FirebaseFunctions
) {
    /**
     * Award coins for a quiz attempt with server-side verification.
     *
     * The server reads the current learning sheet version from Firestore
     * to prevent client-side manipulation of the eligibility check.
     *
     * @param attemptId The ID of the saved quiz attempt
     * @param primaryLanguageCode The primary language code
     * @param targetLanguageCode The target language code
     * @param generatedHistoryCountAtGenerate The history count when the quiz was generated
     * @param totalScore The total score (coins to award)
     * @return CoinAwardResult with award status and details
     */
    suspend fun awardQuizCoins(
        attemptId: String,
        primaryLanguageCode: String,
        targetLanguageCode: String,
        generatedHistoryCountAtGenerate: Int,
        totalScore: Int
    ): CoinAwardResult {
        val data = hashMapOf(
            "attemptId" to attemptId,
            "primaryLanguageCode" to primaryLanguageCode,
            "targetLanguageCode" to targetLanguageCode,
            "generatedHistoryCountAtGenerate" to generatedHistoryCountAtGenerate,
            "totalScore" to totalScore
        )

        return try {
            val result = functions
                .getHttpsCallable("awardQuizCoins")
                .call(data)
                .await()

            val map = result.data as? Map<*, *> ?: emptyMap<Any, Any>()
            CoinAwardResult(
                awarded = map["awarded"] as? Boolean ?: false,
                reason = map["reason"] as? String,
                coinsAwarded = (map["coinsAwarded"] as? Number)?.toInt() ?: 0,
                newTotal = (map["newTotal"] as? Number)?.toInt() ?: 0
            )
        } catch (e: Exception) {
            // On error, don't award coins but don't crash
            CoinAwardResult(
                awarded = false,
                reason = "error: ${e.message}"
            )
        }
    }
}