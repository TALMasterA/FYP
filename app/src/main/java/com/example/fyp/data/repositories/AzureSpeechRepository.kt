package com.example.fyp.data.repositories

import android.util.Log
import com.example.fyp.data.clients.CloudSpeechTokenClient
import com.example.fyp.data.azure.AzureSpeechProvider
import com.example.fyp.model.SpeechResult
import com.microsoft.cognitiveservices.speech.AutoDetectSourceLanguageConfig
import com.microsoft.cognitiveservices.speech.CancellationDetails
import com.microsoft.cognitiveservices.speech.ResultReason
import com.microsoft.cognitiveservices.speech.SpeechConfig
import com.microsoft.cognitiveservices.speech.SpeechRecognizer
import com.microsoft.cognitiveservices.speech.SpeechSynthesisCancellationDetails
import com.microsoft.cognitiveservices.speech.SpeechSynthesizer
import com.microsoft.cognitiveservices.speech.audio.AudioConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AzureSpeechRepository(
    private val tokenClient: CloudSpeechTokenClient
) : SpeechRepository {

    // Token validity constants
    private companion object {
        const val TOKEN_VALIDITY_MS = 9 * 60 * 1000L  // 9 minutes
        const val TOKEN_REFRESH_BUFFER_MS = 30 * 1000L  // 30 seconds buffer before expiry
    }

    private var cachedToken: String? = null
    private var cachedRegion: String? = null
    private var cachedTokenTimeMs: Long = 0L

    private suspend fun getSpeechConfig(): SpeechConfig {
        val now = System.currentTimeMillis()
        // Refresh token 30 seconds before expiry to prevent failures during use
        val tokenValid =
            cachedToken != null &&
                    cachedRegion != null &&
                    (now - cachedTokenTimeMs) < (TOKEN_VALIDITY_MS - TOKEN_REFRESH_BUFFER_MS)

        if (!tokenValid) {
            val resp = tokenClient.getSpeechToken()
            cachedToken = resp.token
            cachedRegion = resp.region
            cachedTokenTimeMs = now
        }

        return AzureSpeechProvider.speechConfigFromToken(
            cachedToken!!,
            cachedRegion!!
        )
    }

    override suspend fun recognizeOnce(languageCode: String): SpeechResult =
        withContext(Dispatchers.IO) {
            try {
                val speechConfig = getSpeechConfig()
                speechConfig.speechRecognitionLanguage = languageCode

                val audioConfig = AudioConfig.fromDefaultMicrophoneInput()
                val recognizer = SpeechRecognizer(speechConfig, audioConfig)

                try {
                    // .get() is acceptable here since we're in Dispatchers.IO
                    // The key improvement is proper cancellation support
                    val result = recognizer.recognizeOnceAsync().get()
                    if (result.reason == ResultReason.RecognizedSpeech) {
                        Log.i("AzureSpeech", "Recognized: ${result.text}")
                        SpeechResult.Success(result.text)
                    } else {
                        val errorDetails =
                            if (result.reason == ResultReason.Canceled) {
                                val cancellation = CancellationDetails.fromResult(result)
                                "Canceled: ${cancellation.reason}. Error details: ${cancellation.errorDetails}"
                            } else {
                                result.reason.toString()
                            }

                        logAndError(
                            tag = "AzureSpeech",
                            userMessage = "Azure Speech not recognized: $errorDetails",
                            logMessage = "Speech not recognized, reason: $errorDetails"
                        )
                    }
                } finally {
                    recognizer.close()
                }
            } catch (ex: Exception) {
                logAndError(
                    tag = "AzureSpeech",
                    userMessage = "Error recognizing speech: ${ex.message}",
                    ex = ex
                )
            }
        }

    /**
     * Recognize speech with auto-detect language from a list of candidate languages.
     * Azure Speech SDK supports up to 4 candidate languages for auto-detection.
     */
    override suspend fun recognizeOnceWithAutoDetect(
        candidateLanguages: List<String>
    ): Result<AutoDetectRecognitionResult> = withContext(Dispatchers.IO) {
        try {
            val speechConfig = getSpeechConfig()

            // Azure supports up to 4 languages for auto-detect
            val languages = candidateLanguages.take(4)
            if (languages.isEmpty()) {
                return@withContext Result.failure(IllegalArgumentException("At least one candidate language is required"))
            }

            val autoDetectConfig = AutoDetectSourceLanguageConfig.fromLanguages(languages)
            val audioConfig = AudioConfig.fromDefaultMicrophoneInput()

            val recognizer = SpeechRecognizer(speechConfig, autoDetectConfig, audioConfig)

            try {
                // .get() is acceptable here since we're in Dispatchers.IO
                val result = recognizer.recognizeOnceAsync().get()

                if (result.reason == ResultReason.RecognizedSpeech) {
                    // Get the detected language from the result
                    val autoDetectResult = com.microsoft.cognitiveservices.speech.AutoDetectSourceLanguageResult.fromResult(result)
                    val detectedLanguage = autoDetectResult.language ?: languages.first()

                    Log.i("AzureSpeech", "Auto-detect recognized: ${result.text}, language: $detectedLanguage")

                    Result.success(AutoDetectRecognitionResult(
                        text = result.text,
                        detectedLanguage = detectedLanguage
                    ))
                } else {
                    val errorDetails = if (result.reason == ResultReason.Canceled) {
                        val cancellation = CancellationDetails.fromResult(result)
                        "Canceled: ${cancellation.reason}. Error details: ${cancellation.errorDetails}"
                    } else {
                        result.reason.toString()
                    }

                    Log.e("AzureSpeech", "Auto-detect recognition failed: $errorDetails")
                    Result.failure(Exception("Speech not recognized: $errorDetails"))
                }
            } finally {
                recognizer.close()
            }
        } catch (ex: Exception) {
            Log.e("AzureSpeech", "Error in auto-detect recognition", ex)
            Result.failure(ex)
        }
    }

    override suspend fun speak(text: String, languageCode: String, voiceName: String?): SpeechResult =
        withContext(Dispatchers.IO) {
            if (text.isBlank()) return@withContext SpeechResult.Error("No text to speak")

            try {
                val speechConfig = getSpeechConfig()
                speechConfig.speechSynthesisLanguage = languageCode

                // Set voice name if provided, otherwise Azure will use default for the language
                if (!voiceName.isNullOrBlank()) {
                    speechConfig.speechSynthesisVoiceName = voiceName
                }

                val synthesizer = SpeechSynthesizer(speechConfig)
                try {
                    // .get() is acceptable here since we're in Dispatchers.IO
                    val result = synthesizer.SpeakTextAsync(text).get()
                    when (result.reason) {
                        ResultReason.SynthesizingAudioCompleted -> {
                            Log.i("AzureTTS", "Speech synthesized for text: $text")
                            SpeechResult.Success("Spoken successfully")
                        }

                        ResultReason.Canceled -> {
                            val cancellation = SpeechSynthesisCancellationDetails.fromResult(result)
                            val logMsg =
                                "TTS canceled: ${cancellation.reason}. ${cancellation.errorDetails}"
                            logAndError(tag = "AzureTTS", userMessage = logMsg, logMessage = logMsg)
                        }

                        else -> {
                            logAndError(
                                tag = "AzureTTS",
                                userMessage = "TTS failed: ${result.reason}"
                            )
                        }
                    }
                } finally {
                    synthesizer.close()
                }
            } catch (ex: Exception) {
                logAndError(
                    tag = "AzureTTS",
                    userMessage = "Error speaking text: ${ex.message}",
                    ex = ex
                )
            }
        }

    override suspend fun startContinuous(
        languageCode: String,
        onPartial: (String) -> Unit,
        onFinal: (String) -> Unit,
        onError: (String) -> Unit
    ): SpeechRecognizer = withContext(Dispatchers.IO) {
        val speechConfig = getSpeechConfig()
        speechConfig.speechRecognitionLanguage = languageCode

        val audioConfig = AudioConfig.fromDefaultMicrophoneInput()
        val recognizer = SpeechRecognizer(speechConfig, audioConfig)

        recognizer.recognizing.addEventListener { _, e ->
            onPartial(e.result.text)
        }

        recognizer.recognized.addEventListener { _, e ->
            val result = e.result
            when (result.reason) {
                ResultReason.RecognizedSpeech -> onFinal(result.text)
                ResultReason.Canceled -> {
                    val det = CancellationDetails.fromResult(result)
                    onError("Canceled: ${det.reason} - ${det.errorDetails}")
                }

                else -> Unit
            }
        }

        recognizer.canceled.addEventListener { _, e ->
            val det = CancellationDetails.fromResult(e.result)
            onError("Canceled: ${det.reason} - ${det.errorDetails}")
        }

        recognizer.startContinuousRecognitionAsync()
        recognizer
    }

    override suspend fun stopContinuous(recognizer: SpeechRecognizer?) = withContext(Dispatchers.IO) {
        if (recognizer == null) return@withContext

        runCatching { recognizer.stopContinuousRecognitionAsync().get() }
        runCatching { recognizer.close() }

        Unit
    }

    private fun logAndError(
        tag: String,
        userMessage: String,
        logMessage: String = userMessage,
        ex: Exception? = null
    ): SpeechResult.Error {
        if (ex != null) Log.e(tag, logMessage, ex) else Log.e(tag, logMessage)
        return SpeechResult.Error(userMessage)
    }
}