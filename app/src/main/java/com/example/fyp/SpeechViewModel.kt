package com.example.fyp

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import com.microsoft.cognitiveservices.speech.SpeechRecognizer

class SpeechViewModel : ViewModel() {

    var recognizedText by mutableStateOf("")
        private set

    var translatedText by mutableStateOf("")
        private set

    var ttsStatus by mutableStateOf("")
        private set

    var isTtsRunning by mutableStateOf(false)
        private set

    var livePartialText by mutableStateOf("")
        private set

    var lastSegmentTranslation by mutableStateOf("")
        private set

    var isContinuousRunning by mutableStateOf(false)
        private set

    private var continuousRecognizer: SpeechRecognizer? = null

    fun clear() {
        recognizedText = ""
        translatedText = ""
        ttsStatus = ""
        isTtsRunning = false
    }

    data class ChatMessage(
        val id: Long,
        val text: String,
        val lang: String,
        val isFromPersonA: Boolean,
        val isTranslation: Boolean
    )

    var continuousMessages by mutableStateOf(listOf<ChatMessage>())
        private set

    private var nextId = 0L

    private fun addMessage(
        text: String,
        lang: String,
        isFromPersonA: Boolean,
        isTranslation: Boolean
    ) {
        val message = ChatMessage(
            id = nextId++,
            text = text,
            lang = lang,
            isFromPersonA = isFromPersonA,
            isTranslation = isTranslation
        )
        continuousMessages = continuousMessages + message
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

    fun startContinuous(
        speakingLang: String,
        targetLang: String,
        isFromPersonA: Boolean
    ) {
        if (isContinuousRunning) return

        livePartialText = ""
        isContinuousRunning = true

        continuousRecognizer =
            SpeechUseCases.startContinuousRecognition(
                languageCode = speakingLang,
                onPartial = { partial -> livePartialText = partial },
                onFinal = { text ->
                    val from = speakingLang
                    val to = targetLang

                    addMessage(
                        text = text,
                        lang = from,
                        isFromPersonA = isFromPersonA,
                        isTranslation = false
                    )

                    viewModelScope.launch {
                        when (
                            val tr = TranslatorClient.translateText(
                                text = text,
                                toLanguage = to,
                                fromLanguage = from
                            )
                        ) {
                            is SpeechResult.Success -> {
                                addMessage(
                                    text = tr.text,
                                    lang = to,
                                    isFromPersonA = isFromPersonA,
                                    isTranslation = true
                                )
                            }
                            is SpeechResult.Error -> {
                                ttsStatus = "Continuous translation error: ${tr.message}"
                            }
                        }
                    }
                },
                onError = { msg ->
                    ttsStatus = "Continuous recognition error: $msg"
                    stopContinuous()
                }
            )
    }

    fun stopContinuous() {
        isContinuousRunning = false
        SpeechUseCases.stopContinuousRecognition(continuousRecognizer)
        continuousRecognizer = null
    }

    fun speakText(languageCode: String, text: String) {
        speakInternal(text = text, languageCode = languageCode, isTranslation = true)
    }

    fun clearContinuousMessages() {
        continuousMessages = emptyList()
        nextId = 0L
    }

    override fun onCleared() {
        super.onCleared()
        stopContinuous()
    }
}