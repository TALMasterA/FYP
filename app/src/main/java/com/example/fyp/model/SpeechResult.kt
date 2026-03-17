package com.example.fyp.model

sealed class SpeechResult {
    data class Success(
        val text: String,
        val detectedLanguage: String? = null,
        val detectedScore: Double? = null
    ) : SpeechResult()
    data class Error(val message: String) : SpeechResult()
}