package com.example.fyp.data.clients

import com.google.firebase.functions.FirebaseFunctions
import kotlinx.coroutines.tasks.await

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

class CloudTranslatorClient(
    private val functions: FirebaseFunctions = FirebaseFunctions.getInstance()
) {
    suspend fun translateText(
        text: String,
        from: String?,
        to: String
    ): String {
        val data = hashMapOf(
            "text" to text,
            "to" to to
        )
        if (!from.isNullOrBlank()) data["from"] = from

        val result = functions
            .getHttpsCallable("translateText")
            .call(data)
            .await()

        @Suppress("UNCHECKED_CAST")
        val map = result.data as? Map<String, Any?>
            ?: throw IllegalStateException("Unexpected result type: ${result.data}")

        return map["translatedText"] as? String
            ?: throw IllegalStateException("Missing translatedText in result")
    }

    suspend fun translateTexts(
        texts: List<String>,
        from: String?,
        to: String
    ): List<String> {
        val data = hashMapOf(
            "texts" to texts,
            "to" to to
        )
        if (!from.isNullOrBlank()) data["from"] = from

        val result = functions
            .getHttpsCallable("translateTexts")
            .call(data)
            .await()

        @Suppress("UNCHECKED_CAST")
        val map = result.data as? Map<String, Any?>
            ?: throw IllegalStateException("Unexpected result type: ${result.data}")

        val list = map["translatedTexts"] as? List<*>
            ?: throw IllegalStateException("Missing translatedTexts in result")

        return list.map { it as? String ?: "" }
    }

    /**
     * Detect the language of the given text using Azure Translator API.
     * Returns detected language code and confidence score.
     */
    suspend fun detectLanguage(text: String): DetectedLanguage {
        val data = hashMapOf("text" to text)

        val result = functions
            .getHttpsCallable("detectLanguage")
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

        return DetectedLanguage(
            language = map["language"] as? String ?: "",
            score = (map["score"] as? Number)?.toDouble() ?: 0.0,
            isTranslationSupported = map["isTranslationSupported"] as? Boolean ?: false,
            alternatives = alternatives
        )
    }
}