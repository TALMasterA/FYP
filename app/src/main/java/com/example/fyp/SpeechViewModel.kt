package com.example.fyp

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class SpeechViewModel : ViewModel() {

    var recognizedText by mutableStateOf("")
        private set

    var translatedText by mutableStateOf("")
        private set

    var ttsStatus by mutableStateOf("")
        private set

    var isTtsRunning by mutableStateOf(false)
        private set

    fun clear() {
        recognizedText = ""
        translatedText = ""
        ttsStatus = ""
        isTtsRunning = false
    }

    fun recognize(languageCode: String) {
        viewModelScope.launch {
            recognizedText = "Recording with Azure, SPEAK and plz WAIT..."
            when (val result = SpeechUseCases.recognizeSpeechWithAzure(languageCode)) {
                is SpeechResult.Success -> recognizedText = result.text
                is SpeechResult.Error -> recognizedText = "Azure error: ${result.message}"
            }
        }
    }

    fun translate(fromLanguage: String, toLanguage: String) {
        if (recognizedText.isBlank()) return
        viewModelScope.launch {
            translatedText = "Translating, please wait..."
            when (val result = TranslatorClient.translateText(
                text = recognizedText,
                toLanguage = toLanguage,
                fromLanguage = fromLanguage
            )) {
                is SpeechResult.Success -> translatedText = result.text
                is SpeechResult.Error -> translatedText = "Translation error: ${result.message}"
            }
        }
    }

    fun speakOriginal(languageCode: String) {
        speakInternal(text = recognizedText, languageCode = languageCode, isTranslation = false)
    }

    fun speakTranslation(languageCode: String) {
        speakInternal(text = translatedText, languageCode = languageCode, isTranslation = true)
    }

    private fun speakInternal(text: String, languageCode: String, isTranslation: Boolean) {
        if (text.isBlank() || isTtsRunning) return
        viewModelScope.launch {
            isTtsRunning = true
            ttsStatus =
                if (isTranslation) "Speaking translation, please wait..."
                else "Speaking original text, please wait..."

            when (val result = SpeechUseCases.speakWithAzure(text, languageCode)) {
                is SpeechResult.Success -> {
                    ttsStatus =
                        if (isTranslation) "Finished speaking translation."
                        else "Finished speaking original text."
                }
                is SpeechResult.Error -> {
                    ttsStatus = "TTS error: ${result.message}"
                }
            }
            isTtsRunning = false
        }
    }
}