package com.example.fyp.model

import com.google.firebase.Timestamp
import kotlinx.serialization.Serializable

// Quiz question model with multiple choice answers
@Serializable
data class QuizQuestion(
    val id: String = "",
    val question: String = "",
    val options: List<String> = emptyList(),
    val correctOptionIndex: Int = 0,
    val explanation: String = "",
    val type: String = "multiple_choice" // For future expansion: essay, fill_blank, etc.
)

// User's answer to a single question
@Serializable
data class QuizAnswer(
    val questionId: String = "",
    val selectedOptionIndex: Int = -1,
    val isCorrect: Boolean = false,
    val timeSpentSeconds: Int = 0
)

// Complete quiz attempt with all questions and answers
data class QuizAttempt(
    val id: String = "",
    val userId: String = "",
    val primaryLanguageCode: String = "",
    val targetLanguageCode: String = "",
    val questions: List<QuizQuestion> = emptyList(),
    val answers: List<QuizAnswer> = emptyList(),
    val startedAt: Timestamp = Timestamp.now(),
    val completedAt: Timestamp? = null,
    val totalScore: Int = 0,
    val maxScore: Int = 0,
    val percentage: Float = 0f
)

// Simplified model for Firestore storage
data class QuizAttemptDoc(
    val userId: String = "",
    val primaryLanguageCode: String = "",
    val targetLanguageCode: String = "",
    val questionsJson: String = "", // Serialized JSON of questions
    val answersJson: String = "", // Serialized JSON of answers
    val startedAt: Timestamp = Timestamp.now(),
    val completedAt: Timestamp? = null,
    val totalScore: Int = 0,
    val maxScore: Int = 0,
    val percentage: Float = 0f
)

// Summary statistics for a language pair
data class QuizStats(
    val primaryLanguageCode: String = "",
    val targetLanguageCode: String = "",
    val attemptCount: Int = 0,
    val averageScore: Float = 0f,
    val highestScore: Int = 0,
    val lowestScore: Int = 0,
    val lastAttemptAt: Timestamp? = null
)
