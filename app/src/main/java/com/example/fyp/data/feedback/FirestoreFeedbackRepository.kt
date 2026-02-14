package com.example.fyp.data.feedback

import com.example.fyp.domain.feedback.FeedbackRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirestoreFeedbackRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : FeedbackRepository {

    override suspend fun submitFeedback(message: String) {
        val userId = auth.currentUser?.uid ?: "anonymous"
        val userEmail = auth.currentUser?.email
        
        val feedbackData = hashMapOf(
            "userId" to userId,
            "userEmail" to userEmail,
            "message" to message,
            "timestamp" to com.google.firebase.Timestamp.now(),
            "status" to "new"
        )

        firestore.collection("feedback")
            .add(feedbackData)
            .await()
    }
}
