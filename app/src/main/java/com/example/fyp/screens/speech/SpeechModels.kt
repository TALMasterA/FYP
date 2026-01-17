package com.example.fyp.screens.speech

data class SpeechScreenState(
    val recognizedText: String = "",
    val translatedText: String = "",
    val ttsStatus: String = "",
    val statusMessage: String = "",
    val isTtsRunning: Boolean = false,
    val recognizePhase: RecognizePhase = RecognizePhase.Idle,
)

enum class RecognizePhase {
    Idle,
    Preparing,
    Listening,
}

data class ChatMessage(
    val id: Long,
    val text: String,
    val lang: String,
    val isFromPersonA: Boolean,
    val isTranslation: Boolean,
)