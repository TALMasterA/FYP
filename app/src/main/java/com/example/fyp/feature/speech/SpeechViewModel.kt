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
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class SpeechViewModel @Inject constructor(
    private val recognizeFromMic: RecognizeFromMicUseCase,
    private val translateTextUseCase: TranslateTextUseCase,
    private val speakTextUseCase: SpeakTextUseCase,
    continuousUseCase: StartContinuousConversationUseCase,
    private val authRepo: FirebaseAuthRepository,
    private val historyRepo: FirestoreHistoryRepository
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.LoggedOut)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private var speechState by mutableStateOf(SpeechScreenState())
    val recognizedText: String get() = speechState.recognizedText
    val translatedText: String get() = speechState.translatedText
    val ttsStatus: String get() = speechState.ttsStatus
    val isTtsRunning: Boolean get() = speechState.isTtsRunning

    private val ttsController = TtsController(
        scope = viewModelScope,
        speakTextUseCase = speakTextUseCase,
        getSpeechState = { speechState },
        setSpeechState = { speechState = it }
    )

    private val continuousController = ContinuousConversationController(
        scope = viewModelScope,
        continuousUseCase = continuousUseCase,
        translateTextUseCase = translateTextUseCase,
        setStatus = { status -> speechState = speechState.copy(ttsStatus = status) },
        saveHistory = { mode, sourceText, targetText, sourceLang, targetLang, sessionId ->
            saveHistory(mode, sourceText, targetText, sourceLang, targetLang, sessionId)
        },
        isLoggedIn = { isLoggedIn() }
    )

    // Expose continuous state (same names used by UI)
    val livePartialText: String get() = continuousController.livePartialText
    val lastSegmentTranslation: String get() = continuousController.lastSegmentTranslation
    val isContinuousRunning: Boolean get() = continuousController.isContinuousRunning
    val isContinuousPreparing: Boolean get() = continuousController.isContinuousPreparing
    val continuousMessages: List<ChatMessage> get() = continuousController.continuousMessages

    init {
        viewModelScope.launch {
            authRepo.currentUserState.collect { _authState.value = it }
        }
    }

    private fun isLoggedIn(): Boolean = _authState.value is AuthState.LoggedIn

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

    // ---- Discrete mode ----
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

    // ---- TTS (same public API) ----
    fun speakOriginal(languageCode: String) =
        ttsController.speak(text = recognizedText, languageCode = languageCode, isTranslation = false)

    fun speakTranslation(languageCode: String) =
        ttsController.speak(text = translatedText, languageCode = languageCode, isTranslation = true)

    fun speakText(languageCode: String, text: String) =
        ttsController.speak(text = text, languageCode = languageCode, isTranslation = true)

    fun speakTextOriginal(languageCode: String, text: String) =
        ttsController.speak(text = text, languageCode = languageCode, isTranslation = false)

    // ---- Continuous mode (same public API) ----
    fun startContinuous(
        speakingLang: String,
        targetLang: String,
        isFromPersonA: Boolean,
        resetSession: Boolean
    ) {
        continuousController.start(
            speakingLang = speakingLang,
            targetLang = targetLang,
            isFromPersonA = isFromPersonA,
            resetSession = resetSession
        )
    }

    fun stopContinuous() {
        continuousController.stop()
    }

    fun endContinuousSession() {
        continuousController.endSession()
    }

    override fun onCleared() {
        super.onCleared()
        stopContinuous()
    }
}