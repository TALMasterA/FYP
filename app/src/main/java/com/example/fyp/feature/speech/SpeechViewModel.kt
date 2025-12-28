package com.example.fyp.feature.speech

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fyp.domain.speech.RecognizeFromMicUseCase
import com.example.fyp.domain.speech.SpeakTextUseCase
import com.example.fyp.domain.speech.StartContinuousConversationUseCase
import com.example.fyp.domain.speech.TranslateTextUseCase
import com.example.fyp.model.SpeechResult
import com.microsoft.cognitiveservices.speech.SpeechRecognizer
import kotlinx.coroutines.launch

data class SpeechScreenState(
    val recognizedText: String = "",
    val translatedText: String = "",
    val ttsStatus: String = "",
    val isTtsRunning: Boolean = false
)

class SpeechViewModel(
    private val recognizeFromMic: RecognizeFromMicUseCase,
    private val translateTextUseCase: TranslateTextUseCase,
    private val speakTextUseCase: SpeakTextUseCase,
    private val continuousUseCase: StartContinuousConversationUseCase
) : ViewModel() {

    // --- main speech screen state ---
    var speechState by mutableStateOf(SpeechScreenState())
        private set

    val recognizedText: String get() = speechState.recognizedText
    val translatedText: String get() = speechState.translatedText
    val ttsStatus: String get() = speechState.ttsStatus
    val isTtsRunning: Boolean get() = speechState.isTtsRunning

    // --- continuous state ---
    var livePartialText by mutableStateOf("")
        private set

    var lastSegmentTranslation by mutableStateOf("")
        private set

    var isContinuousRunning by mutableStateOf(false)
        private set

    private var continuousRecognizer: SpeechRecognizer? = null

    // -------- chat messages --------

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

    private fun addMessageInternal(
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

    private fun addSpokenMessage(
        text: String,
        lang: String,
        isFromPersonA: Boolean
    ) = addMessageInternal(text, lang, isFromPersonA, isTranslation = false)

    private fun addTranslationMessage(
        text: String,
        lang: String,
        isFromPersonA: Boolean
    ) = addMessageInternal(text, lang, isFromPersonA, isTranslation = true)

    // -------- one‑shot speech & translation --------

    fun recognize(languageCode: String) {
        viewModelScope.launch {
            speechState = speechState.copy(
                recognizedText = "Recording with Azure, SPEAK and plz WAIT..."
            )

            when (val result = recognizeFromMic(languageCode)) {
                is SpeechResult.Success ->
                    speechState = speechState.copy(recognizedText = result.text)
                is SpeechResult.Error ->
                    speechState = speechState.copy(
                        recognizedText = speechError("Azure error", result)
                    )
            }
        }
    }

    fun translate(fromLanguage: String, toLanguage: String) {
        if (recognizedText.isBlank()) return
        viewModelScope.launch {
            speechState = speechState.copy(
                translatedText = "Translating, please wait..."
            )

            when (
                val result = translateTextUseCase(
                    text = recognizedText,
                    fromLanguage = fromLanguage,
                    toLanguage = toLanguage
                )
            ) {
                is SpeechResult.Success ->
                    speechState = speechState.copy(translatedText = result.text)
                is SpeechResult.Error ->
                    speechState = speechState.copy(
                        translatedText = "Translation error: ${result.message}"
                    )
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
            speechState = speechState.copy(
                isTtsRunning = true,
                ttsStatus = if (isTranslation)
                    "Speaking translation, please wait..."
                else
                    "Speaking original text, please wait..."
            )

            when (val result = speakTextUseCase(text, languageCode)) {
                is SpeechResult.Success -> {
                    speechState = speechState.copy(
                        ttsStatus = if (isTranslation)
                            "Finished speaking translation."
                        else
                            "Finished speaking original text."
                    )
                }
                is SpeechResult.Error -> {
                    speechState = speechState.copy(
                        ttsStatus = "TTS error: ${result.message}"
                    )
                }
            }

            speechState = speechState.copy(isTtsRunning = false)
        }
    }

    // -------- continuous conversation --------

    fun startContinuous(
        speakingLang: String,
        targetLang: String,
        isFromPersonA: Boolean
    ) {
        if (isContinuousRunning) return

        livePartialText = ""
        isContinuousRunning = true

        continuousRecognizer =
            continuousUseCase(
                languageCode = speakingLang,
                onPartial = { partial -> livePartialText = partial },
                onFinal = { text ->
                    val from = speakingLang
                    val to = targetLang

                    addSpokenMessage(
                        text = text,
                        lang = from,
                        isFromPersonA = isFromPersonA
                    )

                    viewModelScope.launch {
                        when (
                            val tr = translateTextUseCase(
                                text = text,
                                fromLanguage = from,
                                toLanguage = to
                            )
                        ) {
                            is SpeechResult.Success -> {
                                addTranslationMessage(
                                    text = tr.text,
                                    lang = to,
                                    isFromPersonA = isFromPersonA
                                )
                            }
                            is SpeechResult.Error -> {
                                speechState = speechState.copy(
                                    ttsStatus = "Continuous translation error: ${tr.message}"
                                )
                            }
                        }
                    }
                },
                onError = { msg ->
                    speechState = speechState.copy(
                        ttsStatus = "Continuous recognition error: $msg"
                    )
                    stopContinuous()
                }
            )
    }

    fun stopContinuous() {
        isContinuousRunning = false
        continuousUseCase.stop(continuousRecognizer)
        continuousRecognizer = null
    }

    fun speakText(languageCode: String, text: String) {
        speakInternal(text = text, languageCode = languageCode, isTranslation = true)
    }

    fun clearContinuousMessages() {
        continuousMessages = emptyList()
        nextId = 0L
    }

    private fun speechError(prefix: String, result: SpeechResult.Error): String =
        "$prefix: ${result.message}"

    override fun onCleared() {
        super.onCleared()
        stopContinuous()
    }
}