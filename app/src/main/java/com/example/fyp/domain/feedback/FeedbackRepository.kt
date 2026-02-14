package com.example.fyp.domain.feedback

interface FeedbackRepository {
    suspend fun submitFeedback(message: String)
}
