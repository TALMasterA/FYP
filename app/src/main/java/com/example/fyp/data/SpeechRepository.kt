package com.example.fyp.data

import com.example.fyp.model.SpeechResult
import com.microsoft.cognitiveservices.speech.SpeechRecognizer

interface SpeechRepository {
    suspend fun recognizeOnce(languageCode: String): SpeechResult
    suspend fun speak(text: String, languageCode: String): SpeechResult

    suspend fun startContinuous(
        languageCode: String,
        onPartial: (String) -> Unit,
        onFinal: (String) -> Unit,
        onError: (String) -> Unit
    ): SpeechRecognizer

    fun stopContinuous(recognizer: SpeechRecognizer?)
}