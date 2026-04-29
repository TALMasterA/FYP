package com.translator.TalknLearn.screens.speech

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.translator.TalknLearn.data.azure.LanguageDisplayNames
import com.translator.TalknLearn.data.user.FirebaseAuthRepository
import com.translator.TalknLearn.data.history.FirestoreHistoryRepository
import com.translator.TalknLearn.data.settings.SharedSettingsDataSource
import android.net.Uri
import com.translator.TalknLearn.domain.speech.DetectLanguageUseCase
import com.translator.TalknLearn.domain.speech.RecognizeFromMicUseCase
import com.translator.TalknLearn.domain.speech.RecognizeWithAutoDetectUseCase
import com.translator.TalknLearn.domain.speech.SpeakTextUseCase
import com.translator.TalknLearn.domain.speech.StartContinuousConversationUseCase
import com.translator.TalknLearn.domain.speech.TranslateTextUseCase
import com.translator.TalknLearn.domain.ocr.RecognizeTextFromImageUseCase
import com.translator.TalknLearn.model.user.AuthState
import com.translator.TalknLearn.model.SpeechResult
import com.translator.TalknLearn.model.TranslationRecord
import com.translator.TalknLearn.model.OcrResult
import com.translator.TalknLearn.data.clients.DetectedLanguage
import com.translator.TalknLearn.observability.FunnelAnalyticsTracker
import com.translator.TalknLearn.observability.PerformanceTracer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import com.translator.TalknLearn.core.UiConstants
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

/**
 * ViewModel for the Speech Recognition / Translation screen.
 *
 * Coordinates microphone-based speech recognition (single-shot and
 * continuous conversation modes), text translation, TTS playback,
 * OCR from camera images, and auto-language-detection. Observed
 * user settings (primary language, voice preferences) are loaded
 * from [SharedSettingsDataSource]. Translation history is persisted
 * via [FirestoreHistoryRepository].
 */
@HiltViewModel
class SpeechViewModel @Inject constructor(
    private val recognizeFromMic: RecognizeFromMicUseCase,
    private val autoDetectRecognizeUseCase: RecognizeWithAutoDetectUseCase,
    private val translateTextUseCase: TranslateTextUseCase,
    private val speakTextUseCase: SpeakTextUseCase,
    private val detectLanguageUseCase: DetectLanguageUseCase,
    private val recognizeTextFromImageUseCase: RecognizeTextFromImageUseCase,
    continuousUseCase: StartContinuousConversationUseCase,
    private val authRepo: FirebaseAuthRepository,
    private val historyRepo: FirestoreHistoryRepository,
    private val sharedSettings: SharedSettingsDataSource,
    private val performanceTracer: PerformanceTracer,
    private val funnelTracker: FunnelAnalyticsTracker,
) : ViewModel() {

    /**
     * Item 50 (Observability): one-shot guard so the
     * [PerformanceTracer.TraceName.TIME_TO_FIRST_TRANSLATION] custom trace
     * is started at most once per ViewModel instance.
     */
    private var firstTranslationTraceStarted: Boolean = false

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
    val isContinuousRunning: Boolean get() = continuousController.isContinuousRunning
    val isContinuousPreparing: Boolean get() = continuousController.isContinuousPreparing
    val isContinuousProcessing: Boolean get() = continuousController.isContinuousProcessing
    val continuousMessages: List<ChatMessage> get() = continuousController.continuousMessages

    // Debounce queue for continuous-mode history saves.
    // Segments are accumulated and flushed to Firestore 800 ms after the last segment arrives,
    // reducing the number of individual Firestore round-trips during rapid speech.
    // Synchronized access to prevent race conditions between the debounce coroutine and onCleared().
    private val pendingLock = Any()
    private val pendingContinuousSaves = mutableListOf<TranslationRecord>()
    private var continuousFlushJob: Job? = null
    private var detectedStatusClearJob: Job? = null
    private var ocrStatusClearJob: Job? = null

    init {
        viewModelScope.launch {
            authRepo.currentUserState.collect { authState ->
                _authState.value = authState
            }
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
        if (state !is AuthState.LoggedIn) return

        val record = TranslationRecord(
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
        )

        if (mode == "continuous") {
            // Queue continuous records and flush 800 ms after the last segment arrives.
            // This batches multiple rapid segments into fewer Firestore round-trips.
            synchronized(pendingLock) {
                pendingContinuousSaves.add(record)
            }
            continuousFlushJob?.cancel()
            continuousFlushJob = viewModelScope.launch {
                delay(CONTINUOUS_SAVE_DEBOUNCE_MS)
                flushPendingSaves()
            }
        } else {
            historyRepo.save(record)
        }
    }

    /** Flush all queued continuous records to Firestore as a single batch write. */
    private fun flushPendingSaves() {
        val toSave: List<TranslationRecord>
        synchronized(pendingLock) {
            if (pendingContinuousSaves.isEmpty()) return
            toSave = pendingContinuousSaves.toList()
            pendingContinuousSaves.clear()
        }
        viewModelScope.launch {
            historyRepo.saveBatch(toSave)
        }
    }

    // ---- Discrete mode ----

    fun recognize(languageCode: String) {
        viewModelScope.launch {
            speechState = speechState.copy(
                statusMessage = "",
                recognizePhase = RecognizePhase.Preparing,
            )
            delay(UiConstants.SPEECH_PREPARE_DELAY_MS)
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

    /**
     * Recognize speech with auto-detect language.
     * Uses Azure's auto-detect feature with a list of candidate languages.
     * @param candidateLanguages List of possible languages (max 4 for Azure)
     * @param onDetectedLanguage Callback with the detected language code
     */
    fun recognizeWithAutoDetect(
        candidateLanguages: List<String>,
        onDetectedLanguage: (String) -> Unit
    ) {
        viewModelScope.launch {
            speechState = speechState.copy(
                statusMessage = "Auto-detecting language...",
                recognizePhase = RecognizePhase.Preparing,
            )
            delay(UiConstants.SPEECH_PREPARE_DELAY_MS)
            speechState = speechState.copy(recognizePhase = RecognizePhase.Listening)

            autoDetectRecognizeUseCase(candidateLanguages)
                .onSuccess { result ->
                    speechState = speechState.copy(
                        recognizedText = result.text,
                        recognizePhase = RecognizePhase.Idle,
                        statusMessage = "Detected: ${result.detectedLanguage}",
                    )
                    scheduleDetectedStatusAutoClear()
                    onDetectedLanguage(result.detectedLanguage)
                }
                .onFailure { error ->
                    speechState = speechState.copy(
                        recognizePhase = RecognizePhase.Idle,
                        statusMessage = "Recognition error: ${error.message}",
                    )
                }
        }
    }

    fun translate(
        fromLanguage: String,
        toLanguage: String,
        onDetectedSourceLanguage: ((String) -> Unit)? = null
    ) {
        if (recognizedText.isBlank()) {
            speechState = speechState.copy(
                statusMessage = "Please enter or speak text to translate.",
            )
            return
        }

        if (!isLoggedIn()) {
            speechState = speechState.copy(
                statusMessage = "Login is required to use translation.",
            )
            return
        }

        viewModelScope.launch {
            val isAutoDetect = fromLanguage == "auto"
            // Pass empty string for auto-detect so Azure detects during translation
            val requestFromLanguage = if (isAutoDetect) "" else fromLanguage

            speechState = speechState.copy(statusMessage = "Translating...")

            // Item 50: bracket the full translate flow with a custom Performance
            // trace the first time the user successfully triggers a translation
            // in this ViewModel instance. The trace is stopped in the try/finally
            // below so it is closed on every exit path (success, error, early
            // return on auto-detect failure).
            val firstTranslationTrace: PerformanceTracer.TraceHandle? =
                if (!firstTranslationTraceStarted) {
                    firstTranslationTraceStarted = true
                    performanceTracer.start(PerformanceTracer.TraceName.TIME_TO_FIRST_TRANSLATION)
                } else {
                    null
                }

            try {
            when (val tr = translateTextUseCase(recognizedText, requestFromLanguage, toLanguage)) {
                is SpeechResult.Success -> {
                    var actualFromLanguage = fromLanguage
                    var resolvedDetection: DetectedLanguage? = null

                    if (isAutoDetect) {
                        resolvedDetection = resolveDetectedLanguage(
                            inlineDetectedLanguage = tr.detectedLanguage,
                            inlineDetectedScore = tr.detectedScore
                        )

                        if (resolvedDetection == null) {
                            speechState = speechState.copy(statusMessage = "Could not detect source language. Please select manually.")
                            return@launch
                        }

                        actualFromLanguage = LanguageDisplayNames.mapDetectedToSupportedCode(resolvedDetection.language)
                        onDetectedSourceLanguage?.invoke(actualFromLanguage)
                        updateDetectedLanguageStatus(actualFromLanguage, resolvedDetection.language, resolvedDetection.score)
                    }

                    speechState = speechState.copy(
                        translatedText = tr.text,
                        // Keep auto-detect status visible briefly, then clear it.
                        statusMessage = if (isAutoDetect) speechState.statusMessage else "",
                    )
                    // Item 51: emit the first_translation funnel event
                    // (one-shot per install). The trace is closed in the
                    // surrounding try/finally below.
                    funnelTracker.logFirstTranslation()
                    // Skip saving history when source and target languages are the
                    // same — this happens when auto-detect resolves to the target
                    // language, or when the user manually selects the same language
                    // for both fields.  Saving such records would create misleading
                    // language pairs on the learning screen.
                    if (actualFromLanguage != toLanguage) {
                        saveHistory(
                            mode = "discrete",
                            sourceText = recognizedText,
                            targetText = tr.text,
                            sourceLang = actualFromLanguage,
                            targetLang = toLanguage,
                            sessionId = "",
                        )
                    }
                }

                is SpeechResult.Error -> {
                    speechState = speechState.copy(
                        statusMessage = "Translation error: ${tr.message}",
                    )
                }
            }
            } finally {
                // Item 50: ensure the trace is stopped on every exit path
                // (success, terminal Azure error, auto-detect early-return).
                firstTranslationTrace?.stop()
            }
        }
    }

    // ---- OCR (Image Recognition) ----

    /**
     * Process an image and extract text using ML Kit OCR
     * @param imageUri URI of the image to process
     * @param languageCode Optional language hint for script selection
     */
    fun recognizeTextFromImage(imageUri: Uri, languageCode: String? = null) {
        viewModelScope.launch {
            speechState = speechState.copy(
                statusMessage = "Scanning image for text...",
                recognizePhase = RecognizePhase.Preparing
            )

            // Map "auto" to null so repository uses default/latin or we could add auto-detection logic
            val lang = if (languageCode == "auto") null else languageCode

            when (val result = recognizeTextFromImageUseCase(imageUri, lang)) {
                is OcrResult.Success -> {
                    val successMessage = "Text extracted successfully"
                    speechState = speechState.copy(
                        recognizedText = result.text,
                        recognizePhase = RecognizePhase.Idle,
                        statusMessage = successMessage
                    )
                    scheduleStatusAutoClear(successMessage, OCR_STATUS_CLEAR_MS)
                }
                is OcrResult.Error -> {
                    speechState = speechState.copy(
                        recognizePhase = RecognizePhase.Idle,
                        statusMessage = "OCR error: ${result.message}"
                    )
                }
            }
        }
    }

    // ---- TTS ----

    fun speakOriginal(languageCode: String, onDetectedSourceLanguage: ((String) -> Unit)? = null) {
        if (recognizedText.isBlank()) {
            speechState = speechState.copy(statusMessage = "Please enter or speak text first.")
            return
        }

        if (languageCode != "auto") {
            speakOriginalWithResolvedLanguage(languageCode)
            return
        }

        viewModelScope.launch {
            val detected = resolveDetectedLanguage()
            if (detected == null) {
                speechState = speechState.copy(statusMessage = "Could not detect source language for speech. Please select a source language.")
                return@launch
            }

            val mappedCode = LanguageDisplayNames.mapDetectedToSupportedCode(detected.language)
            onDetectedSourceLanguage?.invoke(mappedCode)
            updateDetectedLanguageStatus(mappedCode, detected.language, detected.score)
            speakOriginalWithResolvedLanguage(mappedCode)
        }
    }

    fun speakTranslation(languageCode: String) {
        val voiceName = sharedSettings.settings.value.voiceSettings[languageCode]
        ttsController.speak(text = translatedText, languageCode = languageCode, isTranslation = true, voiceName = voiceName)
    }

    fun speakText(languageCode: String, text: String) {
        val voiceName = sharedSettings.settings.value.voiceSettings[languageCode]
        ttsController.speak(text = text, languageCode = languageCode, isTranslation = true, voiceName = voiceName)
    }

    fun speakTextOriginal(languageCode: String, text: String) {
        val voiceName = sharedSettings.settings.value.voiceSettings[languageCode]
        ttsController.speak(text = text, languageCode = languageCode, isTranslation = false, voiceName = voiceName)
    }

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
        flushPendingSaves()
        continuousController.endSession()
    }

    fun updateSourceText(text: String) {
        speechState = speechState.copy(recognizedText = text)
    }

    fun swapTexts() {
        val recognized = speechState.recognizedText
        val translated = speechState.translatedText
        speechState = speechState.copy(
            recognizedText = translated,
            translatedText = recognized
        )
    }

    fun refreshQuickTranslateState() {
        detectedStatusClearJob?.cancel()
        speechState = speechState.copy(
            translatedText = "",
            statusMessage = "Refreshed. Auto-detect will run again on your next translate or speak action."
        )
    }

    private suspend fun resolveDetectedLanguage(
        inlineDetectedLanguage: String? = null,
        inlineDetectedScore: Double? = null,
    ): DetectedLanguage? {
        if (!inlineDetectedLanguage.isNullOrBlank()) {
            return DetectedLanguage(
                language = inlineDetectedLanguage,
                score = inlineDetectedScore ?: 0.0,
                isTranslationSupported = true
            )
        }
        return detectLanguageUseCase(recognizedText)
    }

    private fun updateDetectedLanguageStatus(mappedCode: String, rawDetectedCode: String, score: Double?) {
        val displayName = LanguageDisplayNames.displayName(mappedCode)
        val displayText = if (displayName != mappedCode) displayName else rawDetectedCode
        val confidence = score?.takeIf { it > 0.0 }?.let { (it * 100).toInt() }
        val statusSuffix = if (confidence != null) " ($confidence% confidence)" else ""
        speechState = speechState.copy(statusMessage = "Detected: $displayText$statusSuffix")
        scheduleDetectedStatusAutoClear()
    }

    private fun scheduleDetectedStatusAutoClear() {
        val snapshot = speechState.statusMessage
        if (!snapshot.startsWith("Detected:")) return
        scheduleStatusAutoClear(snapshot, DETECTED_STATUS_AUTO_CLEAR_MS)
    }

    /**
     * Schedules a coroutine to clear [speechState.statusMessage] after [delayMs] if the message
     * has not changed in the meantime. Uses [ocrStatusClearJob] for OCR clears and cancels any
     * previous pending clear for the same slot before scheduling a new one.
     *
     * For detected-language clears use [scheduleDetectedStatusAutoClear] which targets
     * [detectedStatusClearJob]. This helper is the shared implementation used by both paths.
     */
    private fun scheduleStatusAutoClear(matchMessage: String, delayMs: Long) {
        // Choose the right job slot based on which type of status is being cleared
        // so the two auto-clear paths don't cancel each other.
        val isDetected = matchMessage.startsWith("Detected:")
        if (isDetected) {
            detectedStatusClearJob?.cancel()
            detectedStatusClearJob = viewModelScope.launch {
                delay(delayMs)
                if (speechState.statusMessage == matchMessage) {
                    speechState = speechState.copy(statusMessage = "")
                }
            }
        } else {
            ocrStatusClearJob?.cancel()
            ocrStatusClearJob = viewModelScope.launch {
                delay(delayMs)
                if (speechState.statusMessage == matchMessage) {
                    speechState = speechState.copy(statusMessage = "")
                }
            }
        }
    }

    private fun speakOriginalWithResolvedLanguage(languageCode: String) {
        val voiceName = sharedSettings.settings.value.voiceSettings[languageCode]
        ttsController.speak(text = recognizedText, languageCode = languageCode, isTranslation = false, voiceName = voiceName)
    }

    override fun onCleared() {
        super.onCleared()
        stopContinuous()
        // Cancel the debounce job FIRST to prevent it from racing with our final flush.
        continuousFlushJob?.cancel()
        detectedStatusClearJob?.cancel()
        ocrStatusClearJob?.cancel()
        // Then flush remaining records. viewModelScope is still active here.
        flushPendingSaves()
    }

    companion object {
        /** Milliseconds to wait after the last continuous segment before flushing saves to Firestore. */
        private const val CONTINUOUS_SAVE_DEBOUNCE_MS = 800L
        /** Milliseconds before a "Detected:" status label is auto-cleared. */
        private const val DETECTED_STATUS_AUTO_CLEAR_MS = 3000L
        /** Milliseconds before an OCR success status label is auto-cleared. */
        private const val OCR_STATUS_CLEAR_MS = 2000L
    }
}
