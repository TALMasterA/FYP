package com.example.fyp.screens.wordbank

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fyp.data.auth.FirebaseAuthRepository
import com.example.fyp.data.wordbank.FirestoreWordBankRepository
import com.example.fyp.data.wordbank.WordBankGenerationRepository
import com.example.fyp.domain.history.ObserveUserHistoryUseCase
import com.example.fyp.domain.speech.SpeakTextUseCase
import com.example.fyp.model.AuthState
import com.example.fyp.model.SpeechResult
import com.example.fyp.model.TranslationRecord
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class WordBankViewModel @Inject constructor(
    private val authRepo: FirebaseAuthRepository,
    private val observeUserHistory: ObserveUserHistoryUseCase,
    private val wordBankRepo: FirestoreWordBankRepository,
    private val wordBankGenRepo: WordBankGenerationRepository,
    private val speakTextUseCase: SpeakTextUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(WordBankUiState())
    val uiState: StateFlow<WordBankUiState> = _uiState.asStateFlow()

    private var currentUserId: String? = null
    private var historyJob: Job? = null
    private var generationJob: Job? = null
    private var records: List<TranslationRecord> = emptyList()
    private var primaryLanguageCode: String = "en-US"

    // Minimum records needed to regenerate word bank
    companion object {
        const val MIN_RECORDS_FOR_REGEN = 20
    }

    init {
        viewModelScope.launch {
            authRepo.currentUserState.collect { auth ->
                when (auth) {
                    is AuthState.LoggedIn -> {
                        currentUserId = auth.user.uid
                        startListening(auth.user.uid)
                    }
                    AuthState.LoggedOut -> {
                        currentUserId = null
                        historyJob?.cancel()
                        _uiState.value = WordBankUiState(
                            isLoading = false,
                            error = "Not logged in"
                        )
                    }
                    AuthState.Loading -> {
                        _uiState.value = WordBankUiState(isLoading = true)
                    }
                }
            }
        }
    }

    fun setPrimaryLanguageCode(code: String) {
        if (primaryLanguageCode != code) {
            primaryLanguageCode = code
            // Clear selection when primary language changes
            _uiState.value = _uiState.value.copy(
                selectedLanguageCode = null,
                currentWordBank = null
            )
            refreshClusters()
        }
    }

    fun getPrimaryLanguageCode(): String = primaryLanguageCode

    private fun startListening(userId: String) {
        historyJob?.cancel()
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)

        historyJob = viewModelScope.launch {
            observeUserHistory(userId)
                .catch { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = e.message
                    )
                }
                .collect { list ->
                    records = list
                    refreshClusters()
                }
        }
    }

    private fun refreshClusters() {
        viewModelScope.launch {
            val uid = currentUserId ?: return@launch

            // Group records by target language (excluding primary)
            val clusters = records
                .filter { it.targetLang != primaryLanguageCode && it.targetLang.isNotBlank() }
                .groupBy { it.targetLang }
                .map { (lang, recs) ->
                    val hasWordBank = wordBankRepo.wordBankExists(uid, primaryLanguageCode, lang)
                    WordBankLanguageCluster(
                        languageCode = lang,
                        recordCount = recs.size,
                        hasWordBank = hasWordBank
                    )
                }
                .sortedByDescending { it.recordCount }

            _uiState.value = _uiState.value.copy(
                isLoading = false,
                error = null,
                languageClusters = clusters
            )
        }
    }

    fun selectLanguage(languageCode: String) {
        viewModelScope.launch {
            val uid = currentUserId ?: return@launch

            _uiState.value = _uiState.value.copy(
                selectedLanguageCode = languageCode,
                isLoading = true
            )

            try {
                val wordBank = wordBankRepo.getWordBank(uid, primaryLanguageCode, languageCode)
                _uiState.value = _uiState.value.copy(
                    currentWordBank = wordBank,
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    fun clearSelection() {
        _uiState.value = _uiState.value.copy(
            selectedLanguageCode = null,
            currentWordBank = null
        )
    }

    /**
     * Check if regeneration is allowed (need at least MIN_RECORDS_FOR_REGEN more records)
     */
    fun canRegenerate(targetLanguageCode: String): Boolean {
        val currentWordBank = _uiState.value.currentWordBank ?: return true // First gen always allowed
        val relevantRecords = records.filter {
            it.sourceLang == targetLanguageCode || it.targetLang == targetLanguageCode
        }
        val currentCount = relevantRecords.size
        val savedCount = currentWordBank.historyCountAtGenerate
        return (currentCount - savedCount) >= MIN_RECORDS_FOR_REGEN
    }

    /**
     * Get the number of new records since last generation
     */
    fun getNewRecordCount(targetLanguageCode: String): Int {
        val currentWordBank = _uiState.value.currentWordBank ?: return 0
        val relevantRecords = records.filter {
            it.sourceLang == targetLanguageCode || it.targetLang == targetLanguageCode
        }
        return relevantRecords.size - currentWordBank.historyCountAtGenerate
    }

    fun cancelGeneration() {
        generationJob?.cancel()
        generationJob = null
        _uiState.value = _uiState.value.copy(
            isGenerating = false,
            error = "Generation cancelled"
        )
    }

    fun generateWordBank(targetLanguageCode: String) {
        val uid = currentUserId ?: return

        // Cancel any existing generation
        generationJob?.cancel()

        generationJob = viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isGenerating = true, error = null)

            try {
                // Filter records for this language (same pattern as LearningViewModel)
                val relevantRecords = records.filter {
                    it.sourceLang == targetLanguageCode || it.targetLang == targetLanguageCode
                }

                if (relevantRecords.isEmpty()) {
                    _uiState.value = _uiState.value.copy(
                        isGenerating = false,
                        error = "No translation history found for this language"
                    )
                    return@launch
                }

                // Generate word bank using AI (use same deployment as learning/quiz)
                val deployment = "gpt-5-mini"
                android.util.Log.d("WordBankVM", "Generating word bank for $targetLanguageCode with ${relevantRecords.size} records")
                val rawContent = wordBankGenRepo.generateWordBank(
                    deployment = deployment,
                    primaryLanguageCode = primaryLanguageCode,
                    targetLanguageCode = targetLanguageCode,
                    records = relevantRecords
                )
                android.util.Log.d("WordBankVM", "Raw content received: ${rawContent.take(500)}")

                // Check if cancelled
                ensureActive()

                // Parse the JSON response
                val words = parseWordBankResponse(rawContent)
                android.util.Log.d("WordBankVM", "Parsed ${words.size} words")

                if (words.isEmpty()) {
                    _uiState.value = _uiState.value.copy(
                        isGenerating = false,
                        error = "Failed to generate word bank. Please try again."
                    )
                    return@launch
                }

                // Check if cancelled before saving
                ensureActive()

                // Append new words to Firestore (merges with existing, avoids duplicates)
                wordBankRepo.appendWords(
                    uid = uid,
                    primary = primaryLanguageCode,
                    target = targetLanguageCode,
                    newWords = words,
                    historyCount = relevantRecords.size
                )

                // Refresh the word bank
                val wordBank = wordBankRepo.getWordBank(uid, primaryLanguageCode, targetLanguageCode)

                // Refresh clusters to update hasWordBank status
                refreshClusters()

                _uiState.value = _uiState.value.copy(
                    isGenerating = false,
                    currentWordBank = wordBank
                )
            } catch (ce: CancellationException) {
                // Don't update state on cancellation - cancelGeneration() handles it
                throw ce
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isGenerating = false,
                    error = "Generation failed: ${e.message}"
                )
            } finally {
                generationJob = null
            }
        }
    }

    private fun parseWordBankResponse(content: String): List<WordBankItem> {
        return try {
            // Extract JSON from the response - handle various formats
            var jsonStr = content.trim()

            // Remove markdown code blocks if present
            if (jsonStr.contains("```")) {
                val startIdx = jsonStr.indexOf("{")
                val endIdx = jsonStr.lastIndexOf("}") + 1
                if (startIdx >= 0 && endIdx > startIdx) {
                    jsonStr = jsonStr.substring(startIdx, endIdx)
                }
            }

            // Find JSON object boundaries
            val firstBrace = jsonStr.indexOf("{")
            val lastBrace = jsonStr.lastIndexOf("}")
            if (firstBrace >= 0 && lastBrace > firstBrace) {
                jsonStr = jsonStr.substring(firstBrace, lastBrace + 1)
            }

            val json = Json {
                ignoreUnknownKeys = true
                isLenient = true
            }
            val jsonObject = json.parseToJsonElement(jsonStr).jsonObject
            val wordsArray = jsonObject["words"]?.jsonArray ?: return emptyList()

            wordsArray.map { element ->
                val wordObj = element.jsonObject
                WordBankItem(
                    id = UUID.randomUUID().toString(),
                    originalWord = wordObj["original"]?.jsonPrimitive?.content ?: "",
                    translatedWord = wordObj["translated"]?.jsonPrimitive?.content ?: "",
                    pronunciation = wordObj["pronunciation"]?.jsonPrimitive?.content ?: "",
                    example = wordObj["example"]?.jsonPrimitive?.content ?: "",
                    category = wordObj["category"]?.jsonPrimitive?.content ?: "",
                    difficulty = wordObj["difficulty"]?.jsonPrimitive?.content ?: ""
                )
            }.filter { it.originalWord.isNotBlank() && it.translatedWord.isNotBlank() }
        } catch (e: Exception) {
            android.util.Log.e("WordBankVM", "Failed to parse word bank: ${e.message}, content: $content")
            emptyList()
        }
    }

    fun speakWord(word: WordBankItem, type: SpeakingType) {
        val state = _uiState.value
        if (state.isSpeaking) return

        val text = when (type) {
            SpeakingType.ORIGINAL -> word.originalWord
            SpeakingType.TRANSLATED -> word.translatedWord
        }

        val languageCode = when (type) {
            SpeakingType.ORIGINAL -> state.currentWordBank?.targetLanguageCode ?: return
            SpeakingType.TRANSLATED -> state.currentWordBank?.primaryLanguageCode ?: return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isSpeaking = true,
                speakingItemId = word.id,
                speakingType = type
            )

            try {
                val result = speakTextUseCase(text, languageCode)
                when (result) {
                    is SpeechResult.Success -> {
                        delay(300)
                    }
                    is SpeechResult.Error -> {
                        // Optionally show error
                    }
                }
            } finally {
                _uiState.value = _uiState.value.copy(
                    isSpeaking = false,
                    speakingItemId = null,
                    speakingType = null
                )
            }
        }
    }

    fun speakExample(word: WordBankItem) {
        val state = _uiState.value
        if (state.isSpeaking || word.example.isBlank()) return

        val languageCode = state.currentWordBank?.targetLanguageCode ?: return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isSpeaking = true,
                speakingItemId = word.id,
                speakingType = SpeakingType.ORIGINAL
            )

            try {
                speakTextUseCase(word.example, languageCode)
            } finally {
                _uiState.value = _uiState.value.copy(
                    isSpeaking = false,
                    speakingItemId = null,
                    speakingType = null
                )
            }
        }
    }
}
