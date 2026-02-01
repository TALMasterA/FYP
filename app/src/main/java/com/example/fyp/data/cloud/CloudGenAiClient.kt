package com.example.fyp.data.cloud

import com.google.firebase.functions.FirebaseFunctions
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.collections.get

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
            .withTimeout(5, TimeUnit.MINUTES) // 5 minutes timeout for AI generation
            .call(data)
            .await()

        val map = result.data as? Map<*, *> ?: emptyMap<Any, Any>()
        return map["content"] as? String ?: ""
    }
}