package com.example.fyp.data.clients

import com.example.fyp.core.NetworkRetry
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.FirebaseFunctionsException
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit

data class DetectedLanguage(
    val language: String,
    val score: Double,
    val isTranslationSupported: Boolean,
    val alternatives: List<LanguageAlternative> = emptyList()
)

data class LanguageAlternative(
    val language: String,
    val score: Double
)

/**
 * Result of a single translation call.
 * When the source language was omitted (auto-detect), [detectedLanguage] and
 * [detectedScore] contain the language Azure detected and its confidence.
 */
data class TranslationResult(
    val translatedText: String,
    val detectedLanguage: String? = null,
    val detectedScore: Double? = null
)

class CloudTranslatorClient(
    private val functions: FirebaseFunctions = FirebaseFunctions.getInstance()
) {
    private companion object {
        const val TIMEOUT_SECONDS = 30L
        const val RATE_LIMIT_COOLDOWN_MS = 2 * 60 * 1000L

        @Volatile
        private var rateLimitedUntilMs: Long = 0L

        private fun isRateLimitMessage(message: String?): Boolean {
            val lowered = message?.lowercase().orEmpty()
            return lowered.contains("resource-exhausted") ||
                lowered.contains("rate limit") ||
                lowered.contains("too many requests") ||
                lowered.contains("http 429")
        }

        private fun remainingRateLimitSeconds(nowMs: Long = System.currentTimeMillis()): Long {
            val remainingMs = (rateLimitedUntilMs - nowMs).coerceAtLeast(0L)
            if (remainingMs == 0L) return 0L
            return ((remainingMs + 999L) / 1000L).coerceAtLeast(1L)
        }

        private fun enforceRateLimitCooldown() {
            val seconds = remainingRateLimitSeconds()
            if (seconds <= 0L) return
            throw Exception("Translation service is temporarily rate-limited. Please wait ${seconds}s and try again.")
        }

        private fun markRateLimited(message: String?): Exception {
            val nextUntil = System.currentTimeMillis() + RATE_LIMIT_COOLDOWN_MS
            if (nextUntil > rateLimitedUntilMs) {
                rateLimitedUntilMs = nextUntil
            }
            return Exception(
                message?.takeIf { it.isNotBlank() }
                    ?: "Translation service rate limit exceeded. Please wait a few minutes and try again."
            )
        }
    }

    // Reuse callable instances to avoid repeated lookup/allocation on hot paths.
    private val translateTextCallable by lazy { functions.getHttpsCallable("translateText") }
    private val translateTextsCallable by lazy { functions.getHttpsCallable("translateTexts") }
    private val detectLanguageCallable by lazy { functions.getHttpsCallable("detectLanguage") }

    private fun mapFunctionsError(e: FirebaseFunctionsException): Exception {
        val serverMessage = e.message?.takeIf { it.isNotBlank() }
        val message = when (e.code) {
            FirebaseFunctionsException.Code.RESOURCE_EXHAUSTED ->
                serverMessage ?: "Rate limit reached while changing UI language. Please wait and try again."
            FirebaseFunctionsException.Code.DEADLINE_EXCEEDED ->
                serverMessage ?: "Language translation timed out. Please try again."
            FirebaseFunctionsException.Code.UNAVAILABLE ->
                serverMessage ?: "Translation service is temporarily unavailable. Please try again later."
            FirebaseFunctionsException.Code.UNAUTHENTICATED ->
                serverMessage ?: "Authentication required. Please log in again."
            FirebaseFunctionsException.Code.PERMISSION_DENIED ->
                serverMessage ?: "Permission denied while calling translation service."
            else -> serverMessage ?: "Translation request failed. Please try again."
        }
        return if (e.code == FirebaseFunctionsException.Code.RESOURCE_EXHAUSTED || isRateLimitMessage(serverMessage)) {
            markRateLimited(message)
        } else {
            Exception(message, e)
        }
    }

    suspend fun translateText(
        text: String,
        from: String?,
        to: String
    ): TranslationResult {
        return try {
            enforceRateLimitCooldown()
            NetworkRetry.withRetry(
                maxAttempts = 3,
                shouldRetry = NetworkRetry::isRetryableFirebaseException
            ) {
                val data = hashMapOf(
                    "text" to text,
                    "to" to to
                )
                if (!from.isNullOrBlank()) data["from"] = from

                val result = translateTextCallable
                    .withTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                    .call(data)
                    .await()

                @Suppress("UNCHECKED_CAST")
                val map = result.data as? Map<String, Any?>
                    ?: throw IllegalStateException("Unexpected result type: ${result.data}")

                val translatedText = map["translatedText"] as? String
                    ?: throw IllegalStateException("Missing translatedText in result")

                @Suppress("UNCHECKED_CAST")
                val detected = map["detectedLanguage"] as? Map<String, Any?>
                TranslationResult(
                    translatedText = translatedText,
                    detectedLanguage = detected?.get("language") as? String,
                    detectedScore = (detected?.get("score") as? Number)?.toDouble()
                )
            }
        } catch (e: FirebaseFunctionsException) {
            throw mapFunctionsError(e)
        } catch (e: Exception) {
            if (isRateLimitMessage(e.message)) {
                throw markRateLimited(e.message)
            }
            throw e
        }
    }

    suspend fun translateTexts(
        texts: List<String>,
        from: String?,
        to: String
    ): List<String> {
        return try {
            enforceRateLimitCooldown()
            val allTranslatedTexts = mutableListOf<String>()
            
            // Azure Translator API limits the array size to 100 elements per request
            texts.chunked(100).forEach { chunk ->
                val chunkResult = NetworkRetry.withRetry(
                    maxAttempts = 3,
                    shouldRetry = NetworkRetry::isRetryableFirebaseException
                ) {
                    val data = hashMapOf(
                        "texts" to chunk,
                        "to" to to
                    )
                    if (!from.isNullOrBlank()) data["from"] = from

                    val result = translateTextsCallable
                        .withTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                        .call(data)
                        .await()

                    @Suppress("UNCHECKED_CAST")
                    val map = result.data as? Map<String, Any?>
                        ?: throw IllegalStateException("Unexpected result type: ${result.data}")

                    val list = map["translatedTexts"] as? List<*>
                        ?: throw IllegalStateException("Missing translatedTexts in result")

                    list.map { it as? String ?: "" }
                }
                allTranslatedTexts.addAll(chunkResult)
            }
            allTranslatedTexts
        } catch (e: FirebaseFunctionsException) {
            throw mapFunctionsError(e)
        } catch (e: Exception) {
            if (isRateLimitMessage(e.message)) {
                throw markRateLimited(e.message)
            }
            throw e
        }
    }

    /**
     * Detect the language of the given text using Azure Translator API.
     * Returns detected language code and confidence score.
     */
    suspend fun detectLanguage(text: String): DetectedLanguage {
        return NetworkRetry.withRetry(
            maxAttempts = 3,
            shouldRetry = NetworkRetry::isRetryableFirebaseException
        ) {
            val data = hashMapOf("text" to text)

            val result = detectLanguageCallable
                .withTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .call(data)
                .await()

            @Suppress("UNCHECKED_CAST")
            val map = result.data as? Map<String, Any?>
                ?: throw IllegalStateException("Unexpected result type: ${result.data}")

            val alternatives = (map["alternatives"] as? List<*>)?.mapNotNull { alt ->
                val altMap = alt as? Map<*, *> ?: return@mapNotNull null
                LanguageAlternative(
                    language = altMap["language"] as? String ?: "",
                    score = (altMap["score"] as? Number)?.toDouble() ?: 0.0
                )
            } ?: emptyList()

            DetectedLanguage(
                language = map["language"] as? String ?: "",
                score = (map["score"] as? Number)?.toDouble() ?: 0.0,
                isTranslationSupported = map["isTranslationSupported"] as? Boolean ?: false,
                alternatives = alternatives
            )
        }
    }
}