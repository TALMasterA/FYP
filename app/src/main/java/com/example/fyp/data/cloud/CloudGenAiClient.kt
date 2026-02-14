package com.example.fyp.data.cloud

import com.google.firebase.functions.FirebaseFunctions
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Client for interacting with cloud-based AI generation services.
 * Uses Firebase Cloud Functions to invoke Azure OpenAI or similar AI models.
 *
 * This client handles:
 * - Learning content generation (quizzes, flashcards, etc.)
 * - Long-running AI operations with appropriate timeouts
 * - Communication with cloud function endpoints
 */
@Singleton
class CloudGenAiClient @Inject constructor(
    private val functions: FirebaseFunctions
) {
    private companion object {
        /** Timeout for AI generation operations in minutes */
        const val AI_GENERATION_TIMEOUT_MINUTES = 5L
    }

    /**
     * Generates learning content using AI models deployed in the cloud.
     *
     * This is a potentially long-running operation that can take several minutes
     * depending on the complexity of the request and model availability.
     *
     * @param deployment The AI model deployment name/identifier
     * @param prompt The prompt to send to the AI model
     * @return The generated content as a string
     * @throws Exception if generation fails or times out
     */
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
            .withTimeout(AI_GENERATION_TIMEOUT_MINUTES, TimeUnit.MINUTES)
            .call(data)
            .await()

        val map = result.data as? Map<*, *> ?: emptyMap<Any, Any>()
        return map["content"] as? String ?: ""
    }
}