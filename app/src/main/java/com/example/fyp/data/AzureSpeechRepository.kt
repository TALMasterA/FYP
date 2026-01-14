package com.example.fyp.data

import android.util.Log
import com.example.fyp.model.SpeechResult
import com.microsoft.cognitiveservices.speech.CancellationDetails
import com.microsoft.cognitiveservices.speech.ResultReason
import com.microsoft.cognitiveservices.speech.SpeechRecognizer
import com.microsoft.cognitiveservices.speech.SpeechSynthesisCancellationDetails
import com.microsoft.cognitiveservices.speech.SpeechSynthesizer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.microsoft.cognitiveservices.speech.SpeechConfig
import com.microsoft.cognitiveservices.speech.audio.AudioConfig

class AzureSpeechRepository(
    private val tokenClient: CloudSpeechTokenClient
) : SpeechRepository {

    private suspend fun getSpeechConfig(): SpeechConfig {
        val resp = tokenClient.getSpeechToken()
        return AzureSpeechProvider.speechConfigFromToken(resp.token, resp.region)
    }

    override suspend fun recognizeOnce(languageCode: String): SpeechResult =
        withContext(Dispatchers.IO) {
            try {
                val speechConfig = getSpeechConfig()
                speechConfig.speechRecognitionLanguage = languageCode
                val audioConfig = AudioConfig.fromDefaultMicrophoneInput()
                val recognizer = SpeechRecognizer(speechConfig, audioConfig)
                try {
                    val result = recognizer.recognizeOnceAsync().get()
                    if (result.reason == ResultReason.RecognizedSpeech) {
                        Log.i("AzureSpeech", "Recognized: ${result.text}")
                        SpeechResult.Success(result.text)
                    } else {
                        val errorDetails = if (result.reason == ResultReason.Canceled) {
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

    override suspend fun speak(text: String, languageCode: String): SpeechResult =
        withContext(Dispatchers.IO) {
            if (text.isBlank()) {
                return@withContext SpeechResult.Error("No text to speak")
            }

            try {
                val speechConfig = getSpeechConfig()
                speechConfig.speechSynthesisLanguage = languageCode
                val synthesizer = SpeechSynthesizer(speechConfig)
                try {
                    val result = synthesizer.SpeakTextAsync(text).get()
                    when {
                        result.reason == ResultReason.SynthesizingAudioCompleted -> {
                            Log.i("AzureTTS", "Speech synthesized for text: $text")
                            SpeechResult.Success("Spoken successfully")
                        }

                        result.reason == ResultReason.Canceled -> {
                            val cancellation =
                                SpeechSynthesisCancellationDetails.fromResult(result)
                            val logMsg =
                                "TTS canceled: ${cancellation.reason}. ${cancellation.errorDetails}"
                            logAndError(
                                tag = "AzureTTS",
                                userMessage = logMsg,
                                logMessage = logMsg
                            )
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

    override fun stopContinuous(recognizer: SpeechRecognizer?) {
        recognizer?.stopContinuousRecognitionAsync()
        recognizer?.close()
    }

    private fun logAndError(
        tag: String,
        userMessage: String,
        logMessage: String = userMessage,
        ex: Exception? = null
    ): SpeechResult.Error {
        if (ex != null) {
            Log.e(tag, logMessage, ex)
        } else {
            Log.e(tag, logMessage)
        }
        return SpeechResult.Error(userMessage)
    }

    fun closeContinuousRecognizer(recognizer: SpeechRecognizer?) {
        recognizer?.stopContinuousRecognitionAsync()
        recognizer?.close()
    }
}