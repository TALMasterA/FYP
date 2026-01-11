package com.example.fyp.feature.speech

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fyp.data.auth.FirebaseAuthRepository
import com.example.fyp.data.history.FirestoreHistoryRepository
import com.example.fyp.domain.speech.RecognizeFromMicUseCase
import com.example.fyp.domain.speech.SpeakTextUseCase
import com.example.fyp.domain.speech.StartContinuousConversationUseCase
import com.example.fyp.domain.speech.TranslateTextUseCase
import com.example.fyp.model.AuthState
import com.example.fyp.model.SpeechResult
import com.example.fyp.model.TranslationRecord
import com.microsoft.cognitiveservices.speech.SpeechRecognizer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SpeechScreenState(
    val recognizedText: String = "",
    val translatedText: String = "",
    val ttsStatus: String = "",
    val isTtsRunning: Boolean = false
)

@HiltViewModel
class SpeechViewModel @Inject constructor(
    private val recognizeFromMic: RecognizeFromMicUseCase,
    private val translateTextUseCase: TranslateTextUseCase,
    private val speakTextUseCase: SpeakTextUseCase,
    private val continuousUseCase: StartContinuousConversationUseCase,
    private val authRepo: FirebaseAuthRepository,
    private val historyRepo: FirestoreHistoryRepository
) : ViewModel() {

    // --- auth state ---
    private val _authState = MutableStateFlow<AuthState>(AuthState.LoggedOut)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    init {
        viewModelScope.launch {
            authRepo.currentUserState.collect { _authState.value = it }
        }
    }

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

    private fun addMessage(text: String, lang: String, isFromPersonA: Boolean, isTranslation: Boolean) {
        continuousMessages = continuousMessages + ChatMessage(
            id = nextId++,
            text = text,
            lang = lang,
            isFromPersonA = isFromPersonA,
            isTranslation = isTranslation
        )
    }

    private suspend fun saveHistory(
        mode: String,
        sourceText: String,
        targetText: String,
        sourceLang: String,
        targetLang: String
    ) {
        val state = _authState.value
        if (state is AuthState.LoggedIn) {
            historyRepo.save(
                TranslationRecord(
                    id = java.util.UUID.randomUUID().toString(),
                    userId = state.user.uid,
                    sourceText = sourceText,
                    targetText = targetText,
                    sourceLang = sourceLang,
                    targetLang = targetLang,
                    mode = mode
                )
            )
        }
    }

    // -------- one-shot speech & translation --------
    fun recognize(languageCode: String) {
        viewModelScope.launch {
            speechState = speechState.copy(recognizedText = "Recording... Please speak and wait.")
            when (val result = recognizeFromMic(languageCode)) {
                is SpeechResult.Success -> speechState = speechState.copy(recognizedText = result.text)
                is SpeechResult.Error -> speechState = speechState.copy(recognizedText = "Azure error: ${result.message}")
            }
        }
    }

    fun translate(fromLanguage: String, toLanguage: String) {
        if (recognizedText.isBlank()) return
        viewModelScope.launch {
            speechState = speechState.copy(translatedText = "Translating, please wait...")
            when (val tr = translateTextUseCase(recognizedText, fromLanguage, toLanguage)) {
                is SpeechResult.Success -> {
                    speechState = speechState.copy(translatedText = tr.text)
                    saveHistory(
                        mode = "discrete",
                        sourceText = recognizedText,
                        targetText = tr.text,
                        sourceLang = fromLanguage,
                        targetLang = toLanguage
                    )
                }
                is SpeechResult.Error -> speechState = speechState.copy(translatedText = "Translation error: ${tr.message}")
            }
        }
    }

    fun speakOriginal(languageCode: String) =
        speakInternal(text = recognizedText, languageCode = languageCode, isTranslation = false)

    fun speakTranslation(languageCode: String) =
        speakInternal(text = translatedText, languageCode = languageCode, isTranslation = true)

    fun speakText(languageCode: String, text: String) =
        speakInternal(text = text, languageCode = languageCode, isTranslation = true)

    private fun speakInternal(text: String, languageCode: String, isTranslation: Boolean) {
        if (text.isBlank() || isTtsRunning) return
        viewModelScope.launch {
            speechState = speechState.copy(
                isTtsRunning = true,
                ttsStatus = if (isTranslation) "Speaking translation..." else "Speaking original..."
            )

            when (val result = speakTextUseCase(text, languageCode)) {
                is SpeechResult.Success ->
                    speechState = speechState.copy(ttsStatus = "Finished speaking.")
                is SpeechResult.Error ->
                    speechState = speechState.copy(ttsStatus = "TTS error: ${result.message}")
            }

            speechState = speechState.copy(isTtsRunning = false)
        }
    }

    // -------- continuous conversation --------
    fun startContinuous(speakingLang: String, targetLang: String, isFromPersonA: Boolean) {
        if (isContinuousRunning) return
        livePartialText = ""
        lastSegmentTranslation = ""
        isContinuousRunning = true

        continuousRecognizer = continuousUseCase(
            languageCode = speakingLang,
            onPartial = { livePartialText = it },
            onFinal = { finalText ->
                addMessage(finalText, speakingLang, isFromPersonA, isTranslation = false)

                viewModelScope.launch {
                    when (val tr = translateTextUseCase(finalText, speakingLang, targetLang)) {
                        is SpeechResult.Success -> {
                            lastSegmentTranslation = tr.text
                            addMessage(tr.text, targetLang, isFromPersonA, isTranslation = true)

                            saveHistory(
                                mode = "continuous",
                                sourceText = finalText,
                                targetText = tr.text,
                                sourceLang = speakingLang,
                                targetLang = targetLang
                            )
                        }
                        is SpeechResult.Error -> {
                            speechState = speechState.copy(ttsStatus = "Continuous translation error: ${tr.message}")
                        }
                    }
                }
            },
            onError = { msg ->
                speechState = speechState.copy(ttsStatus = "Continuous recognition error: $msg")
                stopContinuous()
            }
        )
    }

    fun stopContinuous() {
        isContinuousRunning = false
        continuousUseCase.stop(continuousRecognizer)
        continuousRecognizer = null
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