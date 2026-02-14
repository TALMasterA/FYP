package com.example.fyp.data.repositories

import android.util.Log
import com.example.fyp.data.clients.CloudSpeechTokenClient
import com.example.fyp.data.azure.AzureSpeechProvider
import com.example.fyp.model.SpeechResult
import com.example.fyp.utils.ErrorMessageMapper
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

/**
 * Repository for Azure Speech Services integration.
 * Handles speech recognition, text-to-speech, and language detection.
 *
 * This repository manages:
 * - Token caching and refresh for Azure Speech SDK
 * - One-time and continuous speech recognition
 * - Auto-detection of spoken language
 * - Text-to-speech synthesis
 *
 * All operations run on [Dispatchers.IO] for proper thread management.
 */
class AzureSpeechRepository(
    private val tokenClient: CloudSpeechTokenClient
) : SpeechRepository {

    // Token validity constants
    private companion object {
        /** Token validity period (9 minutes) */
        const val TOKEN_VALIDITY_MS = 9 * 60 * 1000L

        /** Buffer before token expiry to trigger refresh (30 seconds) */
        const val TOKEN_REFRESH_BUFFER_MS = 30 * 1000L

        /** Maximum number of candidate languages for auto-detection */
        const val MAX_AUTO_DETECT_LANGUAGES = 4

        /** Language code prefix length for script selection */
        const val LANGUAGE_PREFIX_LENGTH = 2
    }

    private var cachedToken: String? = null
    private var cachedRegion: String? = null
    private var cachedTokenTimeMs: Long = 0L

    /**
     * Gets or refreshes the Azure Speech configuration.
     * Automatically refreshes token 30 seconds before expiry.
     *
     * @return Configured SpeechConfig instance
     */
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
                            userMessage = ErrorMessageMapper.mapSpeechRecognitionError(errorDetails),
                            logMessage = "Speech not recognized, reason: $errorDetails"
                        )
                    }
                } finally {
                    recognizer.close()
                }
            } catch (ex: Exception) {
                logAndError(
                    tag = "AzureSpeech",
                    userMessage = ErrorMessageMapper.map(ex),
                    ex = ex
                )
            }
        }

    /**
     * Recognize speech with auto-detect language from a list of candidate languages.
     * Azure Speech SDK supports up to 4 candidate languages for auto-detection.
     *
     * @param candidateLanguages List of language codes to detect from (max 4)
     * @return Result containing recognized text and detected language
     */
    override suspend fun recognizeOnceWithAutoDetect(
        candidateLanguages: List<String>
    ): Result<AutoDetectRecognitionResult> = withContext(Dispatchers.IO) {
        try {
            val speechConfig = getSpeechConfig()

            // Azure supports up to 4 languages for auto-detect
            val languages = candidateLanguages.take(MAX_AUTO_DETECT_LANGUAGES)
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
                    val userMessage = ErrorMessageMapper.mapSpeechRecognitionError(errorDetails)
                    Result.failure(Exception(userMessage))
                }
            } finally {
                recognizer.close()
            }
        } catch (ex: Exception) {
            Log.e("AzureSpeech", "Error in auto-detect recognition", ex)
            Result.failure(Exception(ErrorMessageMapper.map(ex)))
        }
    }

    /**
     * Synthesize speech from text using Azure Text-to-Speech.
     *
     * @param text The text to speak
     * @param languageCode The language code for synthesis (e.g., "en-US")
     * @param voiceName Optional specific voice name (null for default)
     * @return SpeechResult indicating success or error
     */
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
                            val errorDetails = "TTS canceled: ${cancellation.reason}. ${cancellation.errorDetails}"
                            logAndError(
                                tag = "AzureTTS",
                                userMessage = ErrorMessageMapper.mapSpeechRecognitionError(errorDetails),
                                logMessage = errorDetails
                            )
                        }

                        else -> {
                            logAndError(
                                tag = "AzureTTS",
                                userMessage = "Speech synthesis failed. Please try again."
                            )
                        }
                    }
                } finally {
                    synthesizer.close()
                }
            } catch (ex: Exception) {
                logAndError(
                    tag = "AzureTTS",
                    userMessage = ErrorMessageMapper.map(ex),
                    ex = ex
                )
            }
        }

    /**
     * Start continuous speech recognition with real-time results.
     *
     * @param languageCode The language to recognize
     * @param onPartial Callback for partial recognition results
     * @param onFinal Callback for final recognition results
     * @param onError Callback for recognition errors
     * @return SpeechRecognizer instance for controlling the session
     */
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
                    val errorDetails = "Canceled: ${det.reason} - ${det.errorDetails}"
                    onError(ErrorMessageMapper.mapSpeechRecognitionError(errorDetails))
                }

                else -> Unit
            }
        }

        recognizer.canceled.addEventListener { _, e ->
            val det = CancellationDetails.fromResult(e.result)
            val errorDetails = "Canceled: ${det.reason} - ${det.errorDetails}"
            onError(ErrorMessageMapper.mapSpeechRecognitionError(errorDetails))
        }

        recognizer.startContinuousRecognitionAsync()
        recognizer
    }

    /**
     * Stop continuous speech recognition and release resources.
     *
     * @param recognizer The SpeechRecognizer instance to stop
     */
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