package com.translator.TalknLearn.screens.speech

import com.translator.TalknLearn.model.ui.StatusText

data class SpeechScreenState(
    val recognizedText: String = "",
    val translatedText: String = "",
    val ttsStatus: StatusText? = null,
    val statusMessage: StatusText? = null,
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