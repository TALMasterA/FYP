package com.translator.TalknLearn.screens.wordbank

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.translator.TalknLearn.core.ErrorMessages
import com.translator.TalknLearn.data.azure.AzureLanguageConfig
import com.translator.TalknLearn.data.friends.SharedFriendsDataSource
import com.translator.TalknLearn.data.history.SharedHistoryDataSource
import com.translator.TalknLearn.data.settings.SharedSettingsDataSource
import com.translator.TalknLearn.data.user.FirebaseAuthRepository
import com.translator.TalknLearn.data.wordbank.FirestoreCustomWordsRepository
import com.translator.TalknLearn.data.wordbank.FirestoreWordBankRepository
import com.translator.TalknLearn.data.wordbank.WordBankCacheDataStore
import com.translator.TalknLearn.data.wordbank.WordBankGenerationRepository
import com.translator.TalknLearn.domain.friends.ShareWordUseCase
import com.translator.TalknLearn.domain.speech.SpeakTextUseCase
import com.translator.TalknLearn.model.UserId
import com.translator.TalknLearn.model.TranslationRecord
import com.translator.TalknLearn.model.user.AuthState
import com.translator.TalknLearn.model.user.UserSettings
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject

/**
 * ViewModel for the Word Bank screen.
 *
 * Loads word banks per language, supports AI-driven word generation,
 * custom-word CRUD, TTS playback, and sharing words to friends.
 * Word banks are cached locally via [WordBankCacheDataStore] and
 * synced with Firestore.
 *
 * Heavy logic is delegated to:
 * - [WordBankGenerationDelegate] — AI generation, parsing, regen checks
 * - [WordBankSpeechDelegate] — TTS playback
 * - [WordBankShareDelegate] — sharing words with friends
 */
@HiltViewModel
class WordBankViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val authRepo: FirebaseAuthRepository,
    private val sharedHistoryDataSource: SharedHistoryDataSource,
    private val wordBankRepo: FirestoreWordBankRepository,
    private val wordBankGenRepo: WordBankGenerationRepository,
    private val speakTextUseCase: SpeakTextUseCase,
    private val customWordsRepo: FirestoreCustomWordsRepository,
    private val sharedSettings: SharedSettingsDataSource,
    private val wordBankCacheDataStore: WordBankCacheDataStore,
    private val sharedFriendsDataSource: SharedFriendsDataSource,
    private val shareWordUseCase: ShareWordUseCase
) : ViewModel() {

    // Cached supported languages - loaded once and reused
    val supportedLanguages: List<String> by lazy {
        AzureLanguageConfig.loadSupportedLanguages(context)
    }

    private val _uiState = MutableStateFlow(WordBankUiState())
    val uiState: StateFlow<WordBankUiState> = _uiState.asStateFlow()

    private var currentUserId: String? = null
    private var userSettings = UserSettings()
    private var historyJob: Job? = null
    private var settingsJob: Job? = null
    private var friendsJob: Job? = null
    private var records: List<TranslationRecord> = emptyList()
    private var primaryLanguageCode: String = "en-US"

    // In-memory cache backed by persistent DataStore
    private val wordBankExistsCache: ConcurrentHashMap<String, Boolean> = ConcurrentHashMap()
    private var lastPrimaryForCache: String? = null
    private var cachedCustomWordsCount: Int? = null

    // ── Delegates ────────────────────────────────────────────────────────────

    private val generationDelegate = WordBankGenerationDelegate(
        uiState = _uiState,
        scope = viewModelScope,
        wordBankRepo = wordBankRepo,
        wordBankGenRepo = wordBankGenRepo,
        sharedHistoryDataSource = sharedHistoryDataSource,
        getCurrentUserId = { currentUserId },
        getPrimaryLanguageCode = { primaryLanguageCode },
        getRecords = { records },
        invalidateWordBankCache = ::invalidateWordBankCache,
        refreshClusters = ::refreshClusters
    )

    private val speechDelegate = WordBankSpeechDelegate(
        uiState = _uiState,
        scope = viewModelScope,
        speakTextUseCase = speakTextUseCase,
        getUserSettings = { userSettings }
    )

    private val shareDelegate = WordBankShareDelegate(
        uiState = _uiState,
        scope = viewModelScope,
        shareWordUseCase = shareWordUseCase,
        sharedFriendsDataSource = sharedFriendsDataSource,
        getCurrentUserId = { currentUserId },
        getPrimaryLanguageCode = { primaryLanguageCode }
    )

    // ── Init ─────────────────────────────────────────────────────────────────

    init {
        viewModelScope.launch {
            authRepo.currentUserState.collect { auth ->
                when (auth) {
                    is AuthState.LoggedIn -> {
                        val switchedUser = currentUserId != null && currentUserId != auth.user.uid
                        currentUserId = auth.user.uid
                        if (switchedUser) {
                            historyJob?.cancel()
                            records = emptyList()
                            wordBankExistsCache.clear()
                            cachedCustomWordsCount = null
                            _uiState.value = _uiState.value.copy(
                                selectedLanguageCode = null,
                                currentWordBank = null,
                                languageClusters = emptyList(),
                                customWordsCount = 0,
                                error = null
                            )
                        }
                        loadPersistedCache(auth.user.uid)
                        startListening(auth.user.uid)
                        sharedSettings.startObserving(auth.user.uid)
                        settingsJob?.cancel()
                        settingsJob = launch {
                            sharedSettings.settings.collect { settings ->
                                userSettings = settings
                                val settingsPrimary = settings.primaryLanguageCode.ifBlank { "en-US" }
                                setPrimaryLanguageCode(settingsPrimary)
                            }
                        }
                        sharedFriendsDataSource.startObserving(auth.user.uid)
                        friendsJob?.cancel()
                        friendsJob = launch {
                            sharedFriendsDataSource.friends.collect { friends ->
                                _uiState.value = _uiState.value.copy(friends = friends)
                            }
                        }
                    }
                    AuthState.LoggedOut -> {
                        currentUserId = null
                        userSettings = UserSettings()
                        historyJob?.cancel()
                        settingsJob?.cancel()
                        friendsJob?.cancel()
                        friendsJob = null
                        sharedHistoryDataSource.stopObserving()
                        wordBankExistsCache.clear()
                        cachedCustomWordsCount = null
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

    // ── Private helpers ──────────────────────────────────────────────────────

    @Suppress("UNUSED_PARAMETER")
    private fun loadPersistedCache(userId: String) {
        // Cache will be loaded on-demand when checking word bank existence
    }

    fun setCustomWordBankSelected(selected: Boolean) {
        _uiState.value = _uiState.value.copy(isCustomWordBankSelected = selected)
    }

    fun invalidateCustomWordsCount() {
        cachedCustomWordsCount = null
        refreshClusters()
    }

    fun deleteWordFromBank(wordId: String, targetLanguageCode: String) {
        val uid = currentUserId ?: return
        viewModelScope.launch {
            try {
                val currentWordBank = _uiState.value.currentWordBank ?: return@launch
                val updatedWords = currentWordBank.words.filter { it.id != wordId }
                wordBankRepo.saveWordBank(
                    uid = uid,
                    primary = primaryLanguageCode,
                    target = targetLanguageCode,
                    words = updatedWords,
                    historyCount = currentWordBank.historyCountAtGenerate
                )
                _uiState.value = _uiState.value.copy(
                    currentWordBank = currentWordBank.copy(words = updatedWords)
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = ErrorMessages.fromException(e, ErrorMessages.GENERIC_RETRY))
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun setPrimaryLanguageCode(code: String) {
        if (primaryLanguageCode != code) {
            primaryLanguageCode = code
            wordBankExistsCache.clear()
            cachedCustomWordsCount = null
            _uiState.value = _uiState.value.copy(
                selectedLanguageCode = null,
                currentWordBank = null,
                languageClusters = emptyList(),
                isLoading = true,
                error = null
            )
            viewModelScope.launch {
                sharedHistoryDataSource.forceRefreshLanguageCounts(code)
                refreshClusters()
            }
        }
    }

    private fun startListening(userId: String) {
        historyJob?.cancel()
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)

        sharedHistoryDataSource.startObserving(userId)

        viewModelScope.launch {
            sharedHistoryDataSource.forceRefreshLanguageCounts(primaryLanguageCode)
            refreshClusters()
        }

        historyJob = viewModelScope.launch {
            sharedHistoryDataSource.historyRecords
                .collect { list ->
                    records = list
                    sharedHistoryDataSource.refreshLanguageCounts(primaryLanguageCode)
                    refreshClusters()
                }
        }
    }

    private fun refreshClusters() {
        viewModelScope.launch {
            val uid = currentUserId ?: return@launch

            if (lastPrimaryForCache != primaryLanguageCode) {
                wordBankExistsCache.clear()
                lastPrimaryForCache = primaryLanguageCode
            }

            if (cachedCustomWordsCount == null) {
                try {
                    val customWords = customWordsRepo.getAllCustomWordsOnce(uid)
                    cachedCustomWordsCount = customWords.size
                } catch (_: Exception) {
                    cachedCustomWordsCount = _uiState.value.customWordsCount
                }
            }
            _uiState.value = _uiState.value.copy(customWordsCount = cachedCustomWordsCount ?: 0)

            val allLanguageCounts = sharedHistoryDataSource.languageCounts.value.toMutableMap()

            val primaryHasRecords = (allLanguageCounts[primaryLanguageCode] ?: 0) > 0
            val directionalCounts = if (primaryHasRecords) {
                allLanguageCounts.filterKeys { it != primaryLanguageCode }.toMutableMap()
            } else {
                allLanguageCounts.toMutableMap()
            }

            val languagesToCheck = directionalCounts.keys.filterNot { wordBankExistsCache.containsKey(it) }
            if (languagesToCheck.isNotEmpty()) {
                coroutineScope {
                    val batchCacheResults = async {
                        languagesToCheck.associateWith { lang ->
                            wordBankCacheDataStore.getWordBankExists(uid, primaryLanguageCode, lang)
                        }
                    }.await()

                    val firestoreLangs = batchCacheResults.filterValues { it == null }.keys.toList()
                    val firestoreResults = firestoreLangs.map { lang ->
                        lang to async {
                            try {
                                val exists = wordBankRepo.wordBankExists(uid, primaryLanguageCode, lang)
                                wordBankCacheDataStore.cacheWordBank(
                                    userId = uid,
                                    primaryLang = primaryLanguageCode,
                                    targetLang = lang,
                                    exists = exists
                                )
                                exists
                            } catch (e: Exception) {
                                android.util.Log.w("WordBankViewModel", "Failed to check word bank existence for $lang", e)
                                false
                            }
                        }
                    }

                    batchCacheResults.forEach { (lang, cached) ->
                        if (cached != null) wordBankExistsCache[lang] = cached
                    }
                    firestoreResults.forEach { (lang, deferred) ->
                        wordBankExistsCache[lang] = deferred.await()
                    }
                }
            }

            val clusters = directionalCounts
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

    private fun invalidateWordBankCache(languageCode: String) {
        wordBankExistsCache.remove(languageCode)
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
                val wordBank = wordBankRepo.getWordBank(uid, primaryLanguageCode, languageCode)

                val customWords = customWordsRepo.getCustomWordsOnce(uid, primaryLanguageCode, languageCode)
                val customWordItems = customWords.map { cw ->
                    WordBankItem(
                        id = "custom_${cw.id}",
                        originalWord = cw.originalWord,
                        translatedWord = cw.translatedWord,
                        pronunciation = cw.pronunciation,
                        example = cw.example,
                        category = cw.sourceLang + " → " + cw.targetLang,
                        difficulty = ""
                    )
                }

                val mergedWordBank = if (wordBank != null) {
                    wordBank.copy(words = wordBank.words + customWordItems)
                } else if (customWordItems.isNotEmpty()) {
                    WordBank(
                        primaryLanguageCode = primaryLanguageCode,
                        targetLanguageCode = languageCode,
                        words = customWordItems
                    )
                } else {
                    null
                }

                _uiState.value = _uiState.value.copy(
                    currentWordBank = mergedWordBank,
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

    // ── Public API (delegates) ───────────────────────────────────────────────

    // Generation
    fun canRegenerate(targetLanguageCode: String) = generationDelegate.canRegenerate(targetLanguageCode)
    fun getNewRecordCount(targetLanguageCode: String) = generationDelegate.getNewRecordCount(targetLanguageCode)
    fun getCurrentHistoryCount(targetLanguageCode: String) = generationDelegate.getCurrentHistoryCount(targetLanguageCode)
    fun cancelGeneration() = generationDelegate.cancelGeneration()
    fun consumeWordBankGenerationCompleted() = generationDelegate.consumeWordBankGenerationCompleted()
    fun refreshLanguageCounts() = generationDelegate.refreshLanguageCounts()
    fun generateWordBank(targetLanguageCode: String) = generationDelegate.generateWordBank(targetLanguageCode)

    // Speech
    fun speakWord(word: WordBankItem, type: SpeakingType) = speechDelegate.speakWord(word, type)
    fun speakExample(word: WordBankItem) = speechDelegate.speakExample(word)

    // Share
    fun setPendingShareWord(word: WordBankItem?) = shareDelegate.setPendingShareWord(word)
    fun shareWord(word: WordBankItem, toUserId: UserId) = shareDelegate.shareWord(word, toUserId)
    fun clearShareMessages() = shareDelegate.clearShareMessages()

    override fun onCleared() {
        super.onCleared()
        historyJob?.cancel()
        settingsJob?.cancel()
        friendsJob?.cancel()
        generationDelegate.cancelJob()
    }
}
