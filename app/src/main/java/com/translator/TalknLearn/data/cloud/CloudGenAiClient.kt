package com.translator.TalknLearn.data.cloud

import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.FirebaseFunctionsException
import com.google.firebase.functions.FirebaseFunctionsException.Code
import com.translator.TalknLearn.model.GeneratedLearningContent
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Client for AI generation through Firebase Cloud Functions.
 * Uses a longer timeout for learning sheets, quizzes, and Word Bank.
 */
@Singleton
class CloudGenAiClient @Inject constructor(
    private val functions: FirebaseFunctions
) {
    private companion object {
        const val AI_GENERATION_TIMEOUT_MINUTES = 5L
        const val DEFAULT_ERROR = "Generation failed. Please try again."

        val fallbackErrors = mapOf(
            Code.UNAUTHENTICATED to "Authentication required. Please log in again.",
            Code.PERMISSION_DENIED to "Permission denied. Please check your account status.",
            Code.RESOURCE_EXHAUSTED to "Rate limit exceeded. Please wait before trying again.",
            Code.DEADLINE_EXCEEDED to "Request timed out. The generation took too long. Please try again.",
            Code.UNAVAILABLE to "Service temporarily unavailable. Please try again later.",
            Code.INTERNAL to "An internal server error occurred. Please try again later.",
            Code.NOT_FOUND to "AI model deployment not found. Please contact support.",
            Code.FAILED_PRECONDITION to "AI service is not configured. Please contact support.",
            Code.INVALID_ARGUMENT to "Invalid request parameters. Please try again."
        )
    }

    /**
     * Returns the generated text and the model reported by the provider, when available.
     */
    suspend fun generateLearningContent(
        deployment: String,
        prompt: String
    ): GeneratedLearningContent {
        val data = hashMapOf(
            "deployment" to deployment,
            "prompt" to prompt
        )

        try {
            val result = functions
                .getHttpsCallable("generateLearningContent")
                .withTimeout(AI_GENERATION_TIMEOUT_MINUTES, TimeUnit.MINUTES)
                .call(data)
                .await()
            return parseContent(result.data)
        } catch (e: FirebaseFunctionsException) {
            val errorMessage = e.message?.takeIf { it.isNotBlank() } ?: fallbackErrors[e.code] ?: DEFAULT_ERROR
            throw IllegalStateException(errorMessage, e)
        } catch (e: Exception) {
            throw IllegalStateException(e.message?.takeIf { it.isNotBlank() } ?: DEFAULT_ERROR, e)
        }
    }

    private fun parseContent(data: Any?): GeneratedLearningContent {
        val map = data as? Map<*, *> ?: error("Invalid response format from server")
        val content = map["content"] as? String
        if (content.isNullOrBlank()) error("No content generated. Please try again.")
        val model = (map["model"] as? String)?.trim()?.takeIf { it.isNotEmpty() }
        return GeneratedLearningContent(content = content, model = model)
    }
}
