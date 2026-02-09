package com.example.fyp.screens.wordbank

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fyp.data.azure.AzureLanguageConfig
import com.example.fyp.data.user.FirebaseAuthRepository
import com.example.fyp.data.history.SharedHistoryDataSource
import com.example.fyp.data.wordbank.FirestoreCustomWordsRepository
import com.example.fyp.data.wordbank.FirestoreWordBankRepository
import com.example.fyp.data.wordbank.WordBankCacheDataStore
import com.example.fyp.data.wordbank.WordBankGenerationRepository
import com.example.fyp.domain.speech.SpeakTextUseCase
import com.example.fyp.domain.speech.TranslateTextUseCase
import com.example.fyp.data.settings.SharedSettingsDataSource
import com.example.fyp.domain.learning.GenerationEligibility
import com.example.fyp.core.AiConfig
import com.example.fyp.core.UiConstants
import com.example.fyp.model.user.AuthState
import com.example.fyp.model.SpeechResult
import com.example.fyp.model.TranslationRecord
import com.example.fyp.model.user.UserSettings
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class WordBankViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val authRepo: FirebaseAuthRepository,
    private val sharedHistoryDataSource: SharedHistoryDataSource,
    private val wordBankRepo: FirestoreWordBankRepository,
    private val wordBankGenRepo: WordBankGenerationRepository,
    private val speakTextUseCase: SpeakTextUseCase,
    private val customWordsRepo: FirestoreCustomWordsRepository,
    private val translateTextUseCase: TranslateTextUseCase,
    private val sharedSettings: SharedSettingsDataSource,
    private val wordBankCacheDataStore: WordBankCacheDataStore
) : ViewModel() {

    // Cached supported languages - loaded once and reused
    val supportedLanguages: List<Pair<String, String>> by lazy {
        AzureLanguageConfig.loadSupportedLanguages(context).toList()
    }

    private val _uiState = MutableStateFlow(WordBankUiState())
    val uiState: StateFlow<WordBankUiState> = _uiState.asStateFlow()

    private var currentUserId: String? = null
    private var userSettings = UserSettings()
    private var historyJob: Job? = null
    private var settingsJob: Job? = null
    private var generationJob: Job? = null
    private var records: List<TranslationRecord> = emptyList()
    private var primaryLanguageCode: String = "en-US"

    // In-memory cache backed by persistent DataStore
    private val wordBankExistsCache: MutableMap<String, Boolean> = mutableMapOf()
    private var lastPrimaryForCache: String? = null


    init {
        viewModelScope.launch {
            authRepo.currentUserState.collect { auth ->
                when (auth) {
                    is AuthState.LoggedIn -> {
                        currentUserId = auth.user.uid
                        // Load persisted cache on login
                        loadPersistedCache(auth.user.uid)
                        startListening(auth.user.uid)
                        // Use shared settings instead of creating new listener
                        sharedSettings.startObserving(auth.user.uid)
                        settingsJob?.cancel()
                        settingsJob = launch {
                            sharedSettings.settings.collect { settings ->
                                userSettings = settings
                            }
                        }
                    }
                    AuthState.LoggedOut -> {
                        currentUserId = null
                        userSettings = UserSettings()
                        historyJob?.cancel()
                        settingsJob?.cancel()
                        sharedHistoryDataSource.stopObserving()
                        wordBankExistsCache.clear()
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

    /**
     * Load persisted cache into memory on startup
     */
    private fun loadPersistedCache(userId: String) {
        // Cache will be loaded on-demand when checking word bank existence
        // This avoids loading all cache entries at once
    }

    /**
     * Add a custom word to the custom word bank
     */
    fun addCustomWord(
        originalWord: String,
        translatedWord: String,
        pronunciation: String = "",
        example: String = "",
        sourceLang: String,
        targetLang: String
    ) {
        val uid = currentUserId ?: return

        viewModelScope.launch {
            customWordsRepo.addCustomWord(
                userId = uid,
                originalWord = originalWord,
                translatedWord = translatedWord,
                pronunciation = pronunciation,
                example = example,
                sourceLang = sourceLang,
                targetLang = targetLang
            ).onSuccess {
                // Refresh custom words if we're viewing the custom word bank
                if (_uiState.value.isCustomWordBankSelected) {
                    loadCustomWords()
                }
            }.onFailure {
                _uiState.value = _uiState.value.copy(error = "Failed to add word")
            }
        }
    }

    /**
     * Load all custom words for the custom word bank view
     */
    private fun loadCustomWords() {
        val uid = currentUserId ?: return

        viewModelScope.launch {
            try {
                val customWords = customWordsRepo.getAllCustomWordsOnce(uid)
                val customWordItems = customWords.map { cw ->
                    WordBankItem(
                        id = "custom_${cw.id}",
                        originalWord = cw.originalWord,
                        translatedWord = cw.translatedWord,
                        pronunciation = cw.pronunciation,
                        example = cw.example,
                        category = "${cw.sourceLang} → ${cw.targetLang}",
                        difficulty = ""
                    )
                }
                _uiState.value = _uiState.value.copy(
                    customWords = customWordItems,
                    customWordsCount = customWordItems.size,
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Failed to load custom words"
                )
            }
        }
    }

    /**
     * Select the custom word bank view
     */
    fun selectCustomWordBank() {
        _uiState.value = _uiState.value.copy(
            isCustomWordBankSelected = true,
            selectedLanguageCode = null,
            currentWordBank = null,
            isLoading = true
        )
        loadCustomWords()
    }

    /**
     * Translate a word from source language to target language
     */
    fun translateCustomWord(
        text: String,
        targetLanguageCode: String,
        onResult: (String) -> Unit
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isTranslatingCustomWord = true)

            when (val result = translateTextUseCase(text, primaryLanguageCode, targetLanguageCode)) {
                is SpeechResult.Success -> {
                    onResult(result.text)
                }
                is SpeechResult.Error -> {
                    _uiState.value = _uiState.value.copy(error = "Translation failed: ${result.message}")
                }
            }

            _uiState.value = _uiState.value.copy(isTranslatingCustomWord = false)
        }
    }

    /**
     * Translate a word for custom word bank (with custom source and target languages)
     */
    fun translateForCustomWord(
        text: String,
        sourceLang: String,
        targetLang: String,
        onResult: (String) -> Unit
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isTranslatingCustomWord = true)

            when (val result = translateTextUseCase(text, sourceLang, targetLang)) {
                is SpeechResult.Success -> {
                    onResult(result.text)
                }
                is SpeechResult.Error -> {
                    _uiState.value = _uiState.value.copy(error = "Translation failed: ${result.message}")
                }
            }

            _uiState.value = _uiState.value.copy(isTranslatingCustomWord = false)
        }
    }

    /**
     * Get primary language code for display
     */
    fun getPrimaryLanguageCode(): String = primaryLanguageCode

    /**
     * Delete a custom word
     */
    fun deleteCustomWord(wordId: String) {
        val uid = currentUserId ?: return

        viewModelScope.launch {
            customWordsRepo.deleteCustomWord(uid, wordId).onSuccess {
                // Refresh the custom word bank if we're viewing it
                if (_uiState.value.isCustomWordBankSelected) {
                    loadCustomWords()
                }
            }
        }
    }

    /**
     * Delete a word from the generated word bank
     */
    fun deleteWordFromBank(wordId: String, targetLanguageCode: String) {
        val uid = currentUserId ?: return

        viewModelScope.launch {
            try {
                // Get current word bank
                val currentWordBank = _uiState.value.currentWordBank ?: return@launch

                // Filter out the word to delete
                val updatedWords = currentWordBank.words.filter { it.id != wordId }

                // Save updated word bank
                wordBankRepo.saveWordBank(
                    uid = uid,
                    primary = primaryLanguageCode,
                    target = targetLanguageCode,
                    words = updatedWords,
                    historyCount = currentWordBank.historyCountAtGenerate
                )

                // Update UI state immediately
                _uiState.value = _uiState.value.copy(
                    currentWordBank = currentWordBank.copy(words = updatedWords)
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = "Failed to delete word")
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
            // Force refresh to ensure immediate data update
            viewModelScope.launch {
                sharedHistoryDataSource.forceRefreshLanguageCounts(code)
                refreshClusters()
            }
        }
    }

    private fun startListening(userId: String) {
        historyJob?.cancel()
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)

        // Use shared history data source (single listener shared across ViewModels)
        sharedHistoryDataSource.startObserving(userId)

        historyJob = viewModelScope.launch {
            // Force refresh on initial load to ensure data is fresh when user enters screen
            sharedHistoryDataSource.forceRefreshLanguageCounts(primaryLanguageCode)
            refreshClusters()

            sharedHistoryDataSource.historyRecords
                .collect { list ->
                    records = list
                    // Refresh total language counts for word bank generation
                    sharedHistoryDataSource.refreshLanguageCounts(primaryLanguageCode)
                    refreshClusters()
                }
        }
    }

    private fun refreshClusters() {
        viewModelScope.launch {
            val uid = currentUserId ?: return@launch

            // Clear in-memory cache if primary language changed
            if (lastPrimaryForCache != primaryLanguageCode) {
                wordBankExistsCache.clear()
                lastPrimaryForCache = primaryLanguageCode
            }

            // Load custom words count
            try {
                val customWords = customWordsRepo.getAllCustomWordsOnce(uid)
                _uiState.value = _uiState.value.copy(customWordsCount = customWords.size)
            } catch (_: Exception) {
                // Ignore error, just keep previous count
            }

            // Use TOTAL language counts from all records, not limited display records
            val languageCounts = sharedHistoryDataSource.languageCounts.value
                .filter { (lang, _) -> lang != primaryLanguageCode && lang.isNotBlank() }

            // Check cache (in-memory first, then persisted DataStore, then Firestore)
            val languagesToCheck = languageCounts.keys.filter { it !in wordBankExistsCache }

            // Parallelize existence checks for better performance
            if (languagesToCheck.isNotEmpty()) {
                coroutineScope {
                    val results = languagesToCheck.map { lang ->
                        lang to async {
                            try {
                                // First check persisted cache
                                val cachedExists = wordBankCacheDataStore.getWordBankExists(uid, primaryLanguageCode, lang)
                                if (cachedExists != null) {
                                    cachedExists
                                } else {
                                    // Not in persisted cache, check Firestore
                                    val exists = wordBankRepo.wordBankExists(uid, primaryLanguageCode, lang)
                                    // Save to persisted cache
                                    wordBankCacheDataStore.cacheWordBank(
                                        userId = uid,
                                        primaryLang = primaryLanguageCode,
                                        targetLang = lang,
                                        exists = exists
                                    )
                                    exists
                                }
                            } catch (_: Exception) {
                                false
                            }
                        }
                    }
                    
                    // Collect results
                    results.forEach { (lang, deferred) ->
                        wordBankExistsCache[lang] = deferred.await()
                    }
                }
            }

            val clusters = languageCounts
                .map { (lang, count) ->
                    WordBankLanguageCluster(
                        languageCode = lang,
                        recordCount = count,
                        hasWordBank = wordBankExistsCache[lang] ?: false
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

    /**
     * Invalidate cache for a specific language (e.g., after generating word bank)
     */
    private fun invalidateWordBankCache(languageCode: String) {
        wordBankExistsCache.remove(languageCode)
        // Also invalidate persisted cache
        viewModelScope.launch {
            val uid = currentUserId ?: return@launch
            wordBankCacheDataStore.invalidate(uid, primaryLanguageCode, languageCode)
        }
    }

    fun selectLanguage(languageCode: String) {
        viewModelScope.launch {
            val uid = currentUserId ?: return@launch

            _uiState.value = _uiState.value.copy(
                selectedLanguageCode = languageCode,
                isCustomWordBankSelected = false,
                isLoading = true
            )

            try {
                // Fetch generated word bank only (no more custom word merging)
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
            currentWordBank = null,
            isCustomWordBankSelected = false
        )
    }

    /**
     * Check if regeneration is allowed (need at least 20 more records)
     * Uses GenerationEligibility domain logic for consistency across the app.
     * Uses same counting method as Learning Materials (counts per language, not language pair)
     */
    fun canRegenerate(targetLanguageCode: String): Boolean {
        val currentWordBank = _uiState.value.currentWordBank ?: return true // First gen always allowed
        val currentCount = sharedHistoryDataSource.getCountForLanguage(targetLanguageCode)
        val savedCount = currentWordBank.historyCountAtGenerate
        return GenerationEligibility.canRegenerateWordBank(currentCount, savedCount)
    }

    /**
     * Get the number of new records since last generation
     * Uses same counting method as Learning Materials (counts per language, not language pair)
     */
    fun getNewRecordCount(targetLanguageCode: String): Int {
        val currentWordBank = _uiState.value.currentWordBank ?: return 0
        // Use same counting method as Learning Materials
        val currentCount = sharedHistoryDataSource.getCountForLanguage(targetLanguageCode)
        return currentCount - currentWordBank.historyCountAtGenerate
    }

    /**
     * Get the current history count for the target language
     */
    fun getCurrentHistoryCount(targetLanguageCode: String): Int {
        return sharedHistoryDataSource.getCountForLanguage(targetLanguageCode)
    }

    fun cancelGeneration() {
        generationJob?.cancel()
        generationJob = null
        _uiState.value = _uiState.value.copy(
            isGenerating = false,
            error = null
        )
    }

    fun generateWordBank(targetLanguageCode: String) {
        val uid = currentUserId ?: return

        // ANTI-CHEAT: Verify regeneration is allowed (need 20+ more records)
        // First generation is always allowed (no existing word bank)
        val existingWordBank = _uiState.value.currentWordBank
        if (existingWordBank != null && !canRegenerate(targetLanguageCode)) {
            _uiState.value = _uiState.value.copy(
                error = "Need ${GenerationEligibility.MIN_RECORDS_FOR_REGEN} more records to refresh word bank"
            )
            return
        }

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
                val deployment = AiConfig.DEFAULT_DEPLOYMENT
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

                // Use same counting method as Learning Materials (counts per language)
                val unifiedCount = sharedHistoryDataSource.getCountForLanguage(targetLanguageCode)

                // Append new words to Firestore (merges with existing, avoids duplicates)
                wordBankRepo.appendWords(
                    uid = uid,
                    primary = primaryLanguageCode,
                    target = targetLanguageCode,
                    newWords = words,
                    historyCount = unifiedCount
                )

                // Refresh the word bank
                val wordBank = wordBankRepo.getWordBank(uid, primaryLanguageCode, targetLanguageCode)

                // Invalidate cache and refresh clusters to update hasWordBank status
                invalidateWordBankCache(targetLanguageCode)
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

    private suspend fun parseWordBankResponse(content: String): List<WordBankItem> {
        return withContext(Dispatchers.Default) {
            try {
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
                val wordsArray = jsonObject["words"]?.jsonArray ?: return@withContext emptyList()

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
    }

    fun speakWord(word: WordBankItem, type: SpeakingType) {
        val state = _uiState.value
        if (state.isSpeaking) return

        val text = when (type) {
            SpeakingType.ORIGINAL -> word.originalWord
            SpeakingType.TRANSLATED -> word.translatedWord
        }

        // For custom words, extract language codes from category field
        val languageCode = if (state.isCustomWordBankSelected && word.category.contains(" → ")) {
            // Custom word: category format is "sourceLang → targetLang"
            val parts = word.category.split(" → ")
            when (type) {
                SpeakingType.ORIGINAL -> parts.getOrNull(0)?.trim() ?: return
                SpeakingType.TRANSLATED -> parts.getOrNull(1)?.trim() ?: return
            }
        } else {
            // Generated word bank: use the word bank's language codes
            when (type) {
                SpeakingType.ORIGINAL -> state.currentWordBank?.targetLanguageCode ?: return
                SpeakingType.TRANSLATED -> state.currentWordBank?.primaryLanguageCode ?: return
            }
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isSpeaking = true,
                speakingItemId = word.id,
                speakingType = type
            )

            try {
                val voiceName = userSettings.voiceSettings[languageCode]
                val result = speakTextUseCase(text, languageCode, voiceName)
                when (result) {
                    is SpeechResult.Success -> {
                        delay(UiConstants.TTS_FINISH_DELAY_MS)
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

        // For custom words, extract language code from category field
        val languageCode = if (state.isCustomWordBankSelected && word.category.contains(" → ")) {
            // Custom word: category format is "sourceLang → targetLang"
            // Example is in the source language
            val parts = word.category.split(" → ")
            parts.getOrNull(0)?.trim() ?: return
        } else {
            // Generated word bank: example is in the target language
            state.currentWordBank?.targetLanguageCode ?: return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isSpeaking = true,
                speakingItemId = word.id,
                speakingType = SpeakingType.ORIGINAL
            )

            try {
                val voiceName = userSettings.voiceSettings[languageCode]
                speakTextUseCase(word.example, languageCode, voiceName)
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
