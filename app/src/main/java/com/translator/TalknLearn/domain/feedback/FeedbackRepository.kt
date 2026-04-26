package com.translator.TalknLearn.domain.feedback

interface FeedbackRepository {
    suspend fun submitFeedback(message: String)
}
