package com.example.fyp.screens.speech

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.fyp.domain.speech.StartContinuousConversationUseCase
import com.example.fyp.domain.speech.TranslateTextUseCase
import com.example.fyp.model.SpeechResult
import com.microsoft.cognitiveservices.speech.SpeechRecognizer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.UUID
import com.example.fyp.core.UiConstants

internal class ContinuousConversationController(
    private val scope: CoroutineScope,
    private val continuousUseCase: StartContinuousConversationUseCase,
    private val translateTextUseCase: TranslateTextUseCase,
    private val setStatus: (String) -> Unit,
    private val saveHistory: suspend (
        mode: String,
        sourceText: String,
        targetText: String,
        sourceLang: String,
        targetLang: String,
        sessionId: String,
        speaker: String?,
        direction: String?,
        sequence: Long?,
    ) -> Unit,
    private val isLoggedIn: () -> Boolean,
) {
    var livePartialText by mutableStateOf("")
        private set

    var lastSegmentTranslation by mutableStateOf("")
        private set

    var isContinuousRunning by mutableStateOf(false)
        private set

    var isContinuousPreparing by mutableStateOf(false)
        private set

    var isContinuousProcessing by mutableStateOf(false)
        private set

    var continuousMessages by mutableStateOf(listOf<ChatMessage>())
        private set

    private var nextId = 0L
    private var continuousRecognizer: SpeechRecognizer? = null
    private var continuousSessionId: String? = null
    private var continuousSequence = 0L

    private fun ensureSessionId() {
        if (continuousSessionId == null) continuousSessionId = UUID.randomUUID().toString()
    }

    private fun addMessage(text: String, lang: String, isFromPersonA: Boolean, isTranslation: Boolean) {
        continuousMessages = continuousMessages + ChatMessage(
            id = nextId++,
            text = text,
            lang = lang,
            isFromPersonA = isFromPersonA,
            isTranslation = isTranslation,
        )
    }

    fun start(
        speakingLang: String,
        targetLang: String,
        isFromPersonA: Boolean,
        resetSession: Boolean,
    ) {
        if (!isLoggedIn()) {
            setStatus("Please login to use continuous translation.")
            return
        }

        if (isContinuousPreparing) return
        if (isContinuousRunning) return

        if (resetSession) {
            clearMessages()
            continuousSessionId = null
        }

        ensureSessionId()
        livePartialText = ""
        lastSegmentTranslation = ""

        scope.launch {
            isContinuousPreparing = true
            isContinuousRunning = false
            isContinuousProcessing = false
            setStatus("Preparing mic...")
            delay(UiConstants.SPEECH_PREPARE_DELAY_MS)

            val startedAt = System.currentTimeMillis()

            try {
                continuousRecognizer = continuousUseCase(
                    languageCode = speakingLang,
                    onPartial = { text ->
                        if (System.currentTimeMillis() - startedAt >= 200) {
                            scope.launch { livePartialText = text }
                        }
                    },
                    onFinal = { finalText ->
                        scope.launch {
                            // 1) show source in UI immediately
                            addMessage(finalText, speakingLang, isFromPersonA, isTranslation = false)

                            // 2) translate (lock UI while processing)
                            isContinuousProcessing = true
                            setStatus("Translating...")

                            when (val tr = translateTextUseCase(finalText, speakingLang, targetLang)) {
                                is SpeechResult.Success -> {
                                    lastSegmentTranslation = tr.text
                                    addMessage(tr.text, targetLang, isFromPersonA, isTranslation = true)

                                    // Save ONE record per segment
                                    val speaker = if (isFromPersonA) "A" else "B"
                                    val direction = if (isFromPersonA) "A_to_B" else "B_to_A"
                                    val seq = continuousSequence++

                                    saveHistory(
                                        "continuous",
                                        finalText,
                                        tr.text,
                                        speakingLang,
                                        targetLang,
                                        continuousSessionId.orEmpty(),
                                        speaker,
                                        direction,
                                        seq,
                                    )
                                }

                                is SpeechResult.Error -> {
                                    setStatus("Continuous translation error: ${tr.message}")
                                }
                            }

                            isContinuousProcessing = false
                        }
                    },
                    onError = { msg ->
                        scope.launch {
                            setStatus("Continuous recognition error: $msg")
                            stop()
                        }
                    },
                )

                isContinuousPreparing = false
                isContinuousRunning = true
                setStatus("Listening...")
            } catch (e: Exception) {
                isContinuousPreparing = false
                isContinuousRunning = false
                isContinuousProcessing = false
                setStatus("Continuous start error: ${e.message}")
                stop()
            }
        }
    }

    fun stop() {
        isContinuousPreparing = false
        isContinuousProcessing = false

        if (!isContinuousRunning && continuousRecognizer == null) return
        isContinuousRunning = false

        val recognizer = continuousRecognizer
        continuousRecognizer = null

        scope.launch {
            runCatching { continuousUseCase.stop(recognizer) }
        }
    }

    fun endSession() {
        stop()
        clearMessages()
        continuousSessionId = null
        livePartialText = ""
        lastSegmentTranslation = ""
    }

    private fun clearMessages() {
        continuousMessages = emptyList()
        nextId = 0L
        continuousSequence = 0L
    }
}