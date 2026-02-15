package com.example.fyp.data.feedback

import com.example.fyp.domain.feedback.FeedbackRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.delay
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirestoreFeedbackRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : FeedbackRepository {

    companion object {
        private const val MAX_RETRIES = 3
        private const val RETRY_DELAY_MS = 1000L
        private const val REQUEST_TIMEOUT_MS = 15000L
    }

    override suspend fun submitFeedback(message: String) {
        val userId = auth.currentUser?.uid ?: "anonymous"
        val userEmail = auth.currentUser?.email ?: "no-email"

        val feedbackData = hashMapOf(
            "userId" to userId,
            "userEmail" to userEmail,
            "message" to message.trim(),
            "timestamp" to com.google.firebase.firestore.FieldValue.serverTimestamp(),
            "status" to "new",
            "appVersion" to android.os.Build.VERSION.SDK_INT.toString(),
            "deviceModel" to android.os.Build.MODEL,
            "createdAt" to System.currentTimeMillis()
        )

        var lastException: Exception? = null

        // Retry logic for transient failures
        repeat(MAX_RETRIES) { attempt ->
            try {
                withTimeout(REQUEST_TIMEOUT_MS) {
                    firestore.collection("feedback")
                        .add(feedbackData)
                        .await()
                }
                return // Success - exit function
            } catch (e: FirebaseFirestoreException) {
                lastException = when (e.code) {
                    FirebaseFirestoreException.Code.PERMISSION_DENIED ->
                        Exception("Permission denied. Please ensure you're logged in.")
                    FirebaseFirestoreException.Code.UNAVAILABLE ->
                        Exception("Firebase service unavailable. Please check your internet connection.")
                    FirebaseFirestoreException.Code.DEADLINE_EXCEEDED ->
                        Exception("Request timeout. Please check your internet connection and try again.")
                    FirebaseFirestoreException.Code.UNAUTHENTICATED ->
                        Exception("Authentication required. Please log in and try again.")
                    FirebaseFirestoreException.Code.ABORTED,
                    FirebaseFirestoreException.Code.RESOURCE_EXHAUSTED -> {
                        // Retry these errors
                        if (attempt < MAX_RETRIES - 1) {
                            delay(RETRY_DELAY_MS * (attempt + 1))
                            return@repeat
                        }
                        Exception("Service temporarily busy. Please try again later.")
                    }
                    else ->
                        Exception("Failed to submit feedback: ${e.message}")
                }

                // Don't retry permission/auth errors
                if (e.code == FirebaseFirestoreException.Code.PERMISSION_DENIED ||
                    e.code == FirebaseFirestoreException.Code.UNAUTHENTICATED) {
                    throw lastException
                }

                // Retry for other errors
                if (attempt < MAX_RETRIES - 1) {
                    delay(RETRY_DELAY_MS * (attempt + 1))
                } else {
                    throw lastException
                }
            } catch (e: Exception) {
                lastException = if (e.message?.contains("timeout") == true) {
                    Exception("Request timeout. Please check your internet connection and try again.")
                } else {
                    Exception("Failed to submit feedback. Please check your internet connection.")
                }

                // Retry on timeout
                if (attempt < MAX_RETRIES - 1) {
                    delay(RETRY_DELAY_MS * (attempt + 1))
                } else {
                    throw lastException
                }
            }
        }

        // If we get here, all retries failed
        throw lastException ?: Exception("Failed to submit feedback after $MAX_RETRIES attempts.")
    }
}
