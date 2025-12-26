package com.example.fyp.model

sealed class SpeechResult {
    data class Success(val text: String) : SpeechResult()
    data class Error(val message: String) : SpeechResult()
}