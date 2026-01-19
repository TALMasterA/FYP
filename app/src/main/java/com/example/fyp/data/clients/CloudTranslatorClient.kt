package com.example.fyp.data.clients

import com.google.firebase.functions.FirebaseFunctions
import kotlinx.coroutines.tasks.await

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
}