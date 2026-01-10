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
import com.example.fyp.data.auth.FirebaseAuthRepository
import com.example.fyp.domain.auth.LoginUseCase
import com.example.fyp.domain.history.SaveTranslationUseCase
import com.example.fyp.model.AuthState
import com.example.fyp.model.TranslationRecord
import com.example.fyp.model.User

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

    init {

        // NEW: Auth state
        viewModelScope.launch {
            authRepo.currentUserState.collect { state ->
                _authState.value = state
            }
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

    // Auth
    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val loginUseCase: LoginUseCase by lazy { LoginUseCase(authRepo) }
    private val authRepo: FirebaseAuthRepository by lazy { FirebaseAuthRepository(Firebase.auth) }

    // History
    private val saveTranslationUseCase: SaveTranslationUseCase by lazy {
        SaveTranslationUseCase(FirestoreHistoryRepository(Firebase.firestore))
    }

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

    private suspend fun saveTranslation(result: SpeechResult, mode: String) {
        val userState = authState.value
        if (userState is AuthState.LoggedIn) {
            saveTranslationUseCase(
                TranslationRecord(
                    userId = userState.user.uid,
                    mode = mode,
                    sourceLang = result.detectedLanguage ?: "",
                    targetLang = appLanguageState.value.targetLanguage,
                    originalText = result.text,
                    translatedText = result.translatedText ?: ""
                )
            )
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            loginUseCase(email, password).fold(
                onSuccess = { /* handled by authState flow */ },
                onFailure = { _uiState.update { it.copy(error = it.error ?: "Login failed: ${it.message}") } }
            )
        }
    }

    fun logout() {
        authRepo.logout()
    }

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
        continuousRecognizer?.close()
        continuousRecognizer = null
    }
}