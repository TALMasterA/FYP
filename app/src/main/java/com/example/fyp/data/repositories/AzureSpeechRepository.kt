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
import com.microsoft.cognitiveservices.speech.SpeechRecognitionCanceledEventArgs
import com.microsoft.cognitiveservices.speech.SpeechRecognitionEventArgs
import com.microsoft.cognitiveservices.speech.SpeechRecognizer
import com.microsoft.cognitiveservices.speech.SpeechSynthesisCancellationDetails
import com.microsoft.cognitiveservices.speech.SpeechSynthesizer
import com.microsoft.cognitiveservices.speech.audio.AudioConfig
import com.microsoft.cognitiveservices.speech.util.EventHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

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

        /** Default timeout for Azure Speech SDK async operations (seconds) */
        const val SPEECH_OPERATION_TIMEOUT_SECONDS = 30L
    }

    private var cachedToken: String? = null
    private var cachedRegion: String? = null
    private var cachedTokenTimeMs: Long = 0L

    // Track continuous recognition resources for proper cleanup
    private val continuousSessionLock = Any()
    private var continuousSession: ContinuousSession? = null

    private data class ContinuousSession(
        val recognizer: SpeechRecognizer,
        val speechConfig: SpeechConfig,
        val audioConfig: AudioConfig,
        val recognizingListener: EventHandler<SpeechRecognitionEventArgs>,
        val recognizedListener: EventHandler<SpeechRecognitionEventArgs>,
        val canceledListener: EventHandler<SpeechRecognitionCanceledEventArgs>,
    )

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
            var speechConfig: SpeechConfig? = null
            var audioConfig: AudioConfig? = null
            var recognizer: SpeechRecognizer? = null
            try {
                speechConfig = getSpeechConfig()
                speechConfig.speechRecognitionLanguage = languageCode
                audioConfig = AudioConfig.fromDefaultMicrophoneInput()
                recognizer = SpeechRecognizer(speechConfig, audioConfig)

                val result = recognizer.recognizeOnceAsync().get(SPEECH_OPERATION_TIMEOUT_SECONDS, TimeUnit.SECONDS)
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
            } catch (ex: Exception) {
                logAndError(tag = "AzureSpeech", userMessage = ErrorMessageMapper.map(ex), ex = ex)
            } finally {
                // Close in reverse-creation order; small delay lets native threads settle
                runCatching { recognizer?.close() }
                runCatching { audioConfig?.close() }
                kotlinx.coroutines.delay(50)
                runCatching { speechConfig?.close() }
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
        var speechConfig: SpeechConfig? = null
        var audioConfig: AudioConfig? = null
        var recognizer: SpeechRecognizer? = null
        try {
            speechConfig = getSpeechConfig()
            val languages = candidateLanguages.take(MAX_AUTO_DETECT_LANGUAGES)
            if (languages.isEmpty()) {
                return@withContext Result.failure(IllegalArgumentException("At least one candidate language is required"))
            }
            val autoDetectConfig = AutoDetectSourceLanguageConfig.fromLanguages(languages)
            audioConfig = AudioConfig.fromDefaultMicrophoneInput()
            recognizer = SpeechRecognizer(speechConfig, autoDetectConfig, audioConfig)

            val result = recognizer.recognizeOnceAsync().get(SPEECH_OPERATION_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            if (result.reason == ResultReason.RecognizedSpeech) {
                val autoDetectResult = com.microsoft.cognitiveservices.speech.AutoDetectSourceLanguageResult.fromResult(result)
                val detectedLanguage = autoDetectResult.language ?: languages.first()
                Log.i("AzureSpeech", "Auto-detect recognized: ${result.text}, language: $detectedLanguage")
                Result.success(AutoDetectRecognitionResult(text = result.text, detectedLanguage = detectedLanguage))
            } else {
                val errorDetails = if (result.reason == ResultReason.Canceled) {
                    val cancellation = CancellationDetails.fromResult(result)
                    "Canceled: ${cancellation.reason}. Error details: ${cancellation.errorDetails}"
                } else {
                    result.reason.toString()
                }
                Log.e("AzureSpeech", "Auto-detect recognition failed: $errorDetails")
                Result.failure(Exception(ErrorMessageMapper.mapSpeechRecognitionError(errorDetails)))
            }
        } catch (ex: Exception) {
            Log.e("AzureSpeech", "Error in auto-detect recognition", ex)
            Result.failure(Exception(ErrorMessageMapper.map(ex)))
        } finally {
            runCatching { recognizer?.close() }
            runCatching { audioConfig?.close() }
            kotlinx.coroutines.delay(50)
            runCatching { speechConfig?.close() }
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

            var speechConfig: SpeechConfig? = null
            var synthesizer: SpeechSynthesizer? = null
            try {
                speechConfig = getSpeechConfig()
                speechConfig.speechSynthesisLanguage = languageCode
                if (!voiceName.isNullOrBlank()) {
                    speechConfig.speechSynthesisVoiceName = voiceName
                }
                synthesizer = SpeechSynthesizer(speechConfig)

                val result = synthesizer.SpeakTextAsync(text).get(SPEECH_OPERATION_TIMEOUT_SECONDS, TimeUnit.SECONDS)
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
                    else -> logAndError(tag = "AzureTTS", userMessage = "Speech synthesis failed. Please try again.")
                }
            } catch (ex: Exception) {
                logAndError(tag = "AzureTTS", userMessage = ErrorMessageMapper.map(ex), ex = ex)
            } finally {
                // Synthesizer must be closed before speechConfig to avoid native mutex crash
                runCatching { synthesizer?.close() }
                kotlinx.coroutines.delay(50)
                runCatching { speechConfig?.close() }
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
        // Ensure any previous session is fully torn down before creating another.
        val previous = synchronized(continuousSessionLock) {
            continuousSession.also { continuousSession = null }
        }
        if (previous != null) {
            closeContinuousSession(previous)
        }

        val speechConfig = getSpeechConfig()
        speechConfig.speechRecognitionLanguage = languageCode

        val audioConfig = AudioConfig.fromDefaultMicrophoneInput()
        val recognizer = SpeechRecognizer(speechConfig, audioConfig)

        val recognizingListener = EventHandler<SpeechRecognitionEventArgs> { _, e ->
            onPartial(e.result.text)
        }

        val recognizedListener = EventHandler<SpeechRecognitionEventArgs> { _, e ->
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

        val canceledListener = EventHandler<SpeechRecognitionCanceledEventArgs> { _, e ->
            val det = CancellationDetails.fromResult(e.result)
            val errorDetails = "Canceled: ${det.reason} - ${det.errorDetails}"
            onError(ErrorMessageMapper.mapSpeechRecognitionError(errorDetails))
        }

        recognizer.recognizing.addEventListener(recognizingListener)
        recognizer.recognized.addEventListener(recognizedListener)
        recognizer.canceled.addEventListener(canceledListener)

        val session = ContinuousSession(
            recognizer = recognizer,
            speechConfig = speechConfig,
            audioConfig = audioConfig,
            recognizingListener = recognizingListener,
            recognizedListener = recognizedListener,
            canceledListener = canceledListener,
        )

        try {
            recognizer.startContinuousRecognitionAsync().get(SPEECH_OPERATION_TIMEOUT_SECONDS, TimeUnit.SECONDS)
        } catch (ex: Exception) {
            closeContinuousSession(session)
            throw ex
        }

        synchronized(continuousSessionLock) {
            continuousSession = session
        }

        recognizer
    }

    /**
     * Stop continuous speech recognition and release resources.
     *
     * @param recognizer The SpeechRecognizer instance to stop
     */
    override suspend fun stopContinuous(recognizer: SpeechRecognizer?) = withContext(Dispatchers.IO) {
        if (recognizer == null) return@withContext

        val session = synchronized(continuousSessionLock) {
            val active = continuousSession
            if (active != null && active.recognizer == recognizer) {
                continuousSession = null
                active
            } else {
                null
            }
        }

        if (session != null) {
            closeContinuousSession(session)
        } else {
            runCatching { recognizer.stopContinuousRecognitionAsync().get(SPEECH_OPERATION_TIMEOUT_SECONDS, TimeUnit.SECONDS) }
            runCatching { recognizer.close() }
        }

        Unit
    }

    /**
     * Clean up continuous recognition resources (speechConfig and audioConfig).
     * Should be called when stopping continuous recognition or before starting a new session.
     */
    private fun closeContinuousSession(session: ContinuousSession) {
        runCatching { session.recognizer.recognizing.removeEventListener(session.recognizingListener) }
        runCatching { session.recognizer.recognized.removeEventListener(session.recognizedListener) }
        runCatching { session.recognizer.canceled.removeEventListener(session.canceledListener) }

        runCatching {
            session.recognizer
                .stopContinuousRecognitionAsync()
                .get(SPEECH_OPERATION_TIMEOUT_SECONDS, TimeUnit.SECONDS)
        }

        runCatching { session.recognizer.close() }
        runCatching { session.audioConfig.close() }
        kotlinx.coroutines.runBlocking { kotlinx.coroutines.delay(50) }
        runCatching { session.speechConfig.close() }
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