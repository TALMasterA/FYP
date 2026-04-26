package com.translator.TalknLearn.data.cloud

import com.google.firebase.functions.FirebaseFunctions
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit
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
 * Result from server-side coin spending (shop purchases).
 */
data class SpendCoinsResult(
    val success: Boolean,
    val reason: String? = null,
    val newBalance: Int = 0,
    val newLimit: Int? = null
)

/**
 * Client for server-side quiz operations.
 * Uses Cloud Functions for tamper-proof coin eligibility verification.
 */
@Singleton
class CloudQuizClient @Inject constructor(
    private val functions: FirebaseFunctions
) {
    private companion object {
        const val TIMEOUT_SECONDS = 30L
    }

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
                .withTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
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

    /**
     * Server-side coin spending for history expansion.
     * The server validates balance, deducts coins, and applies the new limit atomically.
     */
    suspend fun spendCoinsForHistoryExpansion(): SpendCoinsResult {
        val data = hashMapOf<String, Any>("purchaseType" to "history_expansion")
        return callSpendCoins(data)
    }

    /**
     * Server-side coin spending for palette unlock.
     * The server validates balance, checks palette validity, deducts coins, and unlocks atomically.
     */
    suspend fun spendCoinsForPaletteUnlock(paletteId: String): SpendCoinsResult {
        val data = hashMapOf<String, Any>(
            "purchaseType" to "palette_unlock",
            "paletteId" to paletteId
        )
        return callSpendCoins(data)
    }

    private suspend fun callSpendCoins(data: HashMap<String, Any>): SpendCoinsResult {
        return try {
            val result = functions
                .getHttpsCallable("spendCoins")
                .withTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .call(data)
                .await()

            val map = result.data as? Map<*, *> ?: emptyMap<Any, Any>()
            SpendCoinsResult(
                success = map["success"] as? Boolean ?: false,
                reason = map["reason"] as? String,
                newBalance = (map["newBalance"] as? Number)?.toInt() ?: 0,
                newLimit = (map["newLimit"] as? Number)?.toInt()
            )
        } catch (e: Exception) {
            SpendCoinsResult(
                success = false,
                reason = "error: ${e.message}"
            )
        }
    }
}