package com.example.fyp.screens.speech

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
    private val historyRepo: FirestoreHistoryRepository,
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.LoggedOut)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private var speechState by mutableStateOf(SpeechScreenState())

    val recognizedText: String get() = speechState.recognizedText
    val translatedText: String get() = speechState.translatedText
    val ttsStatus: String get() = speechState.ttsStatus
    val statusMessage: String get() = speechState.statusMessage
    val isTtsRunning: Boolean get() = speechState.isTtsRunning
    val recognizePhase: RecognizePhase get() = speechState.recognizePhase

    private val ttsController = TtsController(
        scope = viewModelScope,
        speakTextUseCase = speakTextUseCase,
        getSpeechState = { speechState },
        setSpeechState = { speechState = it },
    )

    private val continuousController = ContinuousConversationController(
        scope = viewModelScope,
        continuousUseCase = continuousUseCase,
        translateTextUseCase = translateTextUseCase,
        setStatus = { status -> speechState = speechState.copy(statusMessage = status) },
        saveHistory = { mode, sourceText, targetText, sourceLang, targetLang, sessionId, speaker, direction, sequence ->
            saveHistory(mode, sourceText, targetText, sourceLang, targetLang, sessionId, speaker, direction, sequence)
        },
        isLoggedIn = { isLoggedIn() },
    )

    // Expose continuous state
    val livePartialText: String get() = continuousController.livePartialText
    val lastSegmentTranslation: String get() = continuousController.lastSegmentTranslation
    val isContinuousRunning: Boolean get() = continuousController.isContinuousRunning
    val isContinuousPreparing: Boolean get() = continuousController.isContinuousPreparing
    val isContinuousProcessing: Boolean get() = continuousController.isContinuousProcessing
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
        sessionId: String = "",
        speaker: String? = null,
        direction: String? = null,
        sequence: Long? = null,
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
                    sessionId = sessionId,
                    speaker = speaker,
                    direction = direction,
                    sequence = sequence,
                ),
            )
        }
    }

    // ---- Discrete mode ----

    fun recognize(languageCode: String) {
        viewModelScope.launch {
            speechState = speechState.copy(
                statusMessage = "",
                recognizePhase = RecognizePhase.Preparing,
            )
            delay(200)
            speechState = speechState.copy(recognizePhase = RecognizePhase.Listening)

            when (val result = recognizeFromMic(languageCode)) {
                is SpeechResult.Success -> {
                    speechState = speechState.copy(
                        recognizedText = result.text,
                        recognizePhase = RecognizePhase.Idle,
                        statusMessage = "",
                    )
                }

                is SpeechResult.Error -> {
                    speechState = speechState.copy(
                        recognizePhase = RecognizePhase.Idle,
                        statusMessage = "Azure error: ${result.message}",
                    )
                }
            }
        }
    }

    fun translate(fromLanguage: String, toLanguage: String) {
        if (recognizedText.isBlank()) return

        if (!isLoggedIn()) {
            speechState = speechState.copy(
                statusMessage = "Login is required to use translation.",
            )
            return
        }

        viewModelScope.launch {
            speechState = speechState.copy(statusMessage = "Translating...")

            when (val tr = translateTextUseCase(recognizedText, fromLanguage, toLanguage)) {
                is SpeechResult.Success -> {
                    speechState = speechState.copy(
                        translatedText = tr.text,
                        statusMessage = "",
                    )
                    saveHistory(
                        mode = "discrete",
                        sourceText = recognizedText,
                        targetText = tr.text,
                        sourceLang = fromLanguage,
                        targetLang = toLanguage,
                        sessionId = "",
                    )
                }

                is SpeechResult.Error -> {
                    speechState = speechState.copy(
                        statusMessage = "Translation error: ${tr.message}",
                    )
                }
            }
        }
    }

    // ---- TTS ----

    fun speakOriginal(languageCode: String) =
        ttsController.speak(text = recognizedText, languageCode = languageCode, isTranslation = false)

    fun speakTranslation(languageCode: String) =
        ttsController.speak(text = translatedText, languageCode = languageCode, isTranslation = true)

    fun speakText(languageCode: String, text: String) =
        ttsController.speak(text = text, languageCode = languageCode, isTranslation = true)

    fun speakTextOriginal(languageCode: String, text: String) =
        ttsController.speak(text = text, languageCode = languageCode, isTranslation = false)

    // ---- Continuous mode ----

    fun startContinuous(
        speakingLang: String,
        targetLang: String,
        isFromPersonA: Boolean,
        resetSession: Boolean,
    ) {
        continuousController.start(
            speakingLang = speakingLang,
            targetLang = targetLang,
            isFromPersonA = isFromPersonA,
            resetSession = resetSession,
        )
    }

    fun stopContinuous() {
        continuousController.stop()
    }

    fun endContinuousSession() {
        continuousController.endSession()
    }

    fun updateSourceText(text: String) {
        speechState = speechState.copy(recognizedText = text)
    }

    override fun onCleared() {
        super.onCleared()
        stopContinuous()
    }
}