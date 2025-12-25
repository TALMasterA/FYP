package com.example.fyp

import android.util.Log
import com.microsoft.cognitiveservices.speech.CancellationDetails
import com.microsoft.cognitiveservices.speech.ResultReason
import com.microsoft.cognitiveservices.speech.SpeechConfig
import com.microsoft.cognitiveservices.speech.SpeechRecognizer
import com.microsoft.cognitiveservices.speech.SpeechSynthesisCancellationDetails
import com.microsoft.cognitiveservices.speech.SpeechSynthesizer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.microsoft.cognitiveservices.speech.PropertyId

object SpeechUseCases {

    // --- one‑shot recognition (current button) ---
    suspend fun recognizeSpeechWithAzure(languageCode: String): SpeechResult =
        withContext(Dispatchers.IO) {
            try {
                val speechConfig: SpeechConfig = AzureSpeechProvider.speechConfig()
                speechConfig.speechRecognitionLanguage = languageCode

                val recognizer = SpeechRecognizer(speechConfig)
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
                        Log.e("AzureSpeech", "Speech not recognized, reason: $errorDetails")
                        SpeechResult.Error("Azure Speech not recognized: $errorDetails")
                    }
                } finally {
                    recognizer.close()
                }
            } catch (ex: Exception) {
                Log.e("AzureSpeech", "Error: ${ex.message}")
                SpeechResult.Error("Error recognizing speech: ${ex.message}")
            }
        }

    // --- text to speech (current speak buttons) ---
    suspend fun speakWithAzure(text: String, languageCode: String): SpeechResult =
        withContext(Dispatchers.IO) {
            if (text.isBlank()) {
                return@withContext SpeechResult.Error("No text to speak")
            }
            try {
                val speechConfig: SpeechConfig = AzureSpeechProvider.speechConfig()
                speechConfig.speechSynthesisLanguage = languageCode

                val synthesizer = SpeechSynthesizer(speechConfig)
                try {
                    val result = synthesizer.SpeakTextAsync(text).get()

                    if (result.reason == ResultReason.SynthesizingAudioCompleted) {
                        Log.i("AzureTTS", "Speech synthesized for text: $text")
                        SpeechResult.Success("Spoken successfully")
                    } else if (result.reason == ResultReason.Canceled) {
                        val cancellation =
                            SpeechSynthesisCancellationDetails.fromResult(result)
                        val msg =
                            "TTS canceled: ${cancellation.reason}. ${cancellation.errorDetails}"
                        Log.e("AzureTTS", msg)
                        SpeechResult.Error(msg)
                    } else {
                        SpeechResult.Error("TTS failed: ${result.reason}")
                    }
                } finally {
                    synthesizer.close()
                }
            } catch (ex: Exception) {
                Log.e("AzureTTS", "Error: ${ex.message}", ex)
                SpeechResult.Error("Error speaking text: ${ex.message}")
            }
        }

    fun startContinuousRecognition(
        languageCode: String,
        onPartial: (String) -> Unit,
        onFinal: (String) -> Unit,
        onError: (String) -> Unit
    ): SpeechRecognizer {
        val speechConfig: SpeechConfig = AzureSpeechProvider.speechConfig().apply {
            speechRecognitionLanguage = languageCode
        }

        val recognizer = SpeechRecognizer(speechConfig)

        recognizer.recognizing.addEventListener { _, e ->
            onPartial(e.result.text)
        }

        recognizer.recognized.addEventListener { _, e ->
            val result = e.result
            if (result.reason == ResultReason.RecognizedSpeech) {
                onFinal(result.text)
            } else if (result.reason == ResultReason.Canceled) {
                val det = CancellationDetails.fromResult(result)
                onError("Canceled: ${det.reason} - ${det.errorDetails}")
            }
        }

        recognizer.canceled.addEventListener { _, e ->
            val det = CancellationDetails.fromResult(e.result)
            onError("Canceled: ${det.reason} - ${det.errorDetails}")
        }

        recognizer.startContinuousRecognitionAsync()
        return recognizer
    }

    fun stopContinuousRecognition(recognizer: SpeechRecognizer?) {
        recognizer?.stopContinuousRecognitionAsync()
        recognizer?.close()
    }
}