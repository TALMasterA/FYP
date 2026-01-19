package com.example.fyp.data.genai

import com.google.firebase.functions.FirebaseFunctions
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CloudGenAiClient @Inject constructor(
    private val functions: FirebaseFunctions
) {
    suspend fun generateLearningContent(
        deployment: String,
        prompt: String
    ): String {
        val data = hashMapOf(
            "deployment" to deployment,
            "prompt" to prompt
        )

        val result = functions
            .getHttpsCallable("generateLearningContent")
            .call(data)
            .await()

        val map = result.data as? Map<*, *> ?: emptyMap<Any, Any>()
        return map["content"] as? String ?: ""
    }
}