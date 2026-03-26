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
        return Exception(message, e)
    }

    suspend fun translateText(
        text: String,
        from: String?,
        to: String
    ): TranslationResult {
        return NetworkRetry.withRetry(
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
    }

    suspend fun translateTexts(
        texts: List<String>,
        from: String?,
        to: String
    ): List<String> {
        return try {
            NetworkRetry.withRetry(
                maxAttempts = 3,
                shouldRetry = NetworkRetry::isRetryableFirebaseException
            ) {
                val data = hashMapOf(
                    "texts" to texts,
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
        } catch (e: FirebaseFunctionsException) {
            throw mapFunctionsError(e)
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