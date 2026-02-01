package com.example.fyp.data.repositories

import com.example.fyp.model.SpeechResult
import com.microsoft.cognitiveservices.speech.SpeechRecognizer

/**
 * Result of auto-detect speech recognition
 */
data class AutoDetectRecognitionResult(
    val text: String,
    val detectedLanguage: String
)

interface SpeechRepository {

    suspend fun recognizeOnce(languageCode: String): SpeechResult

    /**
     * Recognize speech with auto-detect language from a list of candidate languages.
     * Returns both the recognized text and the detected language code.
     */
    suspend fun recognizeOnceWithAutoDetect(candidateLanguages: List<String>): Result<AutoDetectRecognitionResult>

    suspend fun speak(text: String, languageCode: String): SpeechResult

    suspend fun startContinuous(
        languageCode: String,
        onPartial: (String) -> Unit,
        onFinal: (String) -> Unit,
        onError: (String) -> Unit
    ): SpeechRecognizer

    suspend fun stopContinuous(recognizer: SpeechRecognizer?)
}