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

    // later you will also add continuous‑recognition helpers here
}