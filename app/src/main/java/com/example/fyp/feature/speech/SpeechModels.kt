package com.example.fyp.feature.speech

data class SpeechScreenState(
    val recognizedText: String = "",
    val translatedText: String = "",
    val ttsStatus: String = "",
    val isTtsRunning: Boolean = false
)

data class ChatMessage(
    val id: Long,
    val text: String,
    val lang: String,
    val isFromPersonA: Boolean,
    val isTranslation: Boolean
)