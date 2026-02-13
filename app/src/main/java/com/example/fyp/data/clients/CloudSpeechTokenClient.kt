package com.example.fyp.data.clients

import android.util.Log
import com.example.fyp.core.NetworkRetry
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.FirebaseFunctionsException
import kotlinx.coroutines.tasks.await

data class SpeechTokenResponse(
    val token: String,
    val region: String
)

class CloudSpeechTokenClient(
    private val functions: FirebaseFunctions = FirebaseFunctions.getInstance()
) {
    suspend fun getSpeechToken(): SpeechTokenResponse {
        Log.i("CloudSpeechToken", "getSpeechToken() called")

        return try {
            NetworkRetry.withRetry(
                maxAttempts = 3,
                shouldRetry = NetworkRetry::isRetryableFirebaseException
            ) {
                val result = functions
                    .getHttpsCallable("getSpeechToken")
                    .call()
                    .await()

                @Suppress("UNCHECKED_CAST")
                val map = result.data as? Map<*, *>
                    ?: throw IllegalStateException("Unexpected result type: ${result.data}")

                val token = map["token"] as? String
                    ?: throw IllegalStateException("Missing token in result")

                val region = map["region"] as? String
                    ?: throw IllegalStateException("Missing region in result")

                SpeechTokenResponse(token = token, region = region)
            }
        } catch (e: FirebaseFunctionsException) {
            Log.e("CloudSpeechToken", "getSpeechToken failed: code=${e.code}, message=${e.message}", e)
            throw e
        } catch (e: Exception) {
            Log.e("CloudSpeechToken", "getSpeechToken failed: ${e.message}", e)
            throw e
        }
    }
}