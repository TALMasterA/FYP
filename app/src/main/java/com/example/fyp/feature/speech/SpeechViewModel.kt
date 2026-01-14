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
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
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

    private val _authState = MutableStateFlow<AuthState>(AuthState.LoggedOut)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    init {
        viewModelScope.launch {
            authRepo.currentUserState.collect { _authState.value = it }
        }
    }

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

    var isContinuousPreparing by mutableStateOf(false)
        private set

    private var continuousRecognizer: SpeechRecognizer? = null
    private var continuousSessionId: String? = null

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
        continuousMessages = continuousMessages + ChatMessage(
            id = nextId++,
            text = text,
            lang = lang,
            isFromPersonA = isFromPersonA,
            isTranslation = isTranslation
        )
    }

    private fun ensureContinuousSessionId() {
        if (continuousSessionId == null) {
            continuousSessionId = UUID.randomUUID().toString()
        }
    }

    private suspend fun saveHistory(
        mode: String,
        sourceText: String,
        targetText: String,
        sourceLang: String,
        targetLang: String,
        sessionId: String = ""
    ) {
        val state = _authState.value
        if (state is AuthState.LoggedIn) {
            historyRepo.save(
                TranslationRecord(
                    id = UUID.randomUUID().toString(),
                    userId = state.user.uid,
                    sourceText = sourceText,
                    targetText = targetText,
                    sourceLang = sourceLang,
                    targetLang = targetLang,
                    mode = mode,
                    sessionId = sessionId
                )
            )
        }
    }

    fun recognize(languageCode: String) {
        viewModelScope.launch {
            speechState = speechState.copy(recognizedText = "Preparing mic...")
            delay(200)
            speechState = speechState.copy(recognizedText = "Listening... Please speak now.")
            when (val result = recognizeFromMic(languageCode)) {
                is SpeechResult.Success ->
                    speechState = speechState.copy(recognizedText = result.text)
                is SpeechResult.Error ->
                    speechState = speechState.copy(recognizedText = "Azure error: ${result.message}")
            }
        }
    }

    fun translate(fromLanguage: String, toLanguage: String) {
        if (recognizedText.isBlank()) return

        if (!isLoggedIn()) {
            speechState = speechState.copy(translatedText = "Please login to use translation.")
            return
        }

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
                        targetLang = toLanguage,
                        sessionId = ""
                    )
                }

                is SpeechResult.Error -> {
                    speechState = speechState.copy(translatedText = "Translation error: ${tr.message}")
                }
            }
        }
    }

    fun speakOriginal(languageCode: String) =
        speakInternal(text = recognizedText, languageCode = languageCode, isTranslation = false)

    fun speakTranslation(languageCode: String) =
        speakInternal(text = translatedText, languageCode = languageCode, isTranslation = true)

    fun speakText(languageCode: String, text: String) =
        speakInternal(text = text, languageCode = languageCode, isTranslation = true)

    fun speakTextOriginal(languageCode: String, text: String) =
        speakInternal(text = text, languageCode = languageCode, isTranslation = false)

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

    private fun isLoggedIn(): Boolean = _authState.value is AuthState.LoggedIn

    /**
     * Same logic as before:
     * - Start button starts recognizer (resetSession can clear messages)
     * - Switching person in UI should call stop + start again (UI does that)
     *
     * Added:
     * - "Preparing mic..." + delay(500) before starting recognizer
     */
    fun startContinuous(
        speakingLang: String,
        targetLang: String,
        isFromPersonA: Boolean,
        resetSession: Boolean = false
    ) {
        if (_authState.value !is AuthState.LoggedIn) {
            speechState = speechState.copy(ttsStatus = "Please login to use continuous translation.")
            return
        }

        if (isContinuousPreparing) return
        if (isContinuousRunning) return

        if (resetSession) {
            clearContinuousMessages()
            continuousSessionId = null
        }

        ensureContinuousSessionId()
        livePartialText = ""
        lastSegmentTranslation = ""

        viewModelScope.launch {
            isContinuousPreparing = true
            isContinuousRunning = false
            speechState = speechState.copy(ttsStatus = "Preparing mic...")

            delay(200)

            val startedAt = System.currentTimeMillis()

            try {
                continuousRecognizer = continuousUseCase(
                    languageCode = speakingLang,
                    onPartial = { text ->
                        if (System.currentTimeMillis() - startedAt >= 200) {
                            viewModelScope.launch { livePartialText = text }
                        }
                    },
                    onFinal = { finalText ->
                        viewModelScope.launch {
                            addMessage(finalText, speakingLang, isFromPersonA, isTranslation = false)

                            when (val tr = translateTextUseCase(finalText, speakingLang, targetLang)) {
                                is SpeechResult.Success -> {
                                    lastSegmentTranslation = tr.text
                                    addMessage(tr.text, targetLang, isFromPersonA, isTranslation = true)

                                    saveHistory(
                                        mode = "continuous",
                                        sourceText = finalText,
                                        targetText = tr.text,
                                        sourceLang = speakingLang,
                                        targetLang = targetLang,
                                        sessionId = continuousSessionId.orEmpty()
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
                        viewModelScope.launch {
                            speechState = speechState.copy(ttsStatus = "Continuous recognition error: $msg")
                            stopContinuous()
                        }
                    }
                )

                isContinuousPreparing = false
                isContinuousRunning = true
                speechState = speechState.copy(ttsStatus = "Listening...")

            } catch (e: Exception) {
                isContinuousPreparing = false
                isContinuousRunning = false
                speechState = speechState.copy(ttsStatus = "Continuous start error: ${e.message}")
                stopContinuous()
            }
        }
    }

    fun stopContinuous() {
        isContinuousPreparing = false
        if (!isContinuousRunning && continuousRecognizer == null) return

        isContinuousRunning = false
        val recognizer = continuousRecognizer
        continuousRecognizer = null

        viewModelScope.launch {
            runCatching { continuousUseCase.stop(recognizer) }
        }
    }

    fun endContinuousSession() {
        stopContinuous()
        clearContinuousMessages()
        continuousSessionId = null
        livePartialText = ""
        lastSegmentTranslation = ""
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