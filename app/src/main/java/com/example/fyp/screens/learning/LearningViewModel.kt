package com.example.fyp.screens.learning

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fyp.data.user.FirebaseAuthRepository
import com.example.fyp.data.history.SharedHistoryDataSource
import com.example.fyp.domain.learning.LearningSheetsRepository
import com.example.fyp.domain.learning.QuizRepository
import com.example.fyp.data.learning.QuizParser
import com.example.fyp.domain.learning.GenerateLearningMaterialsUseCase
import com.example.fyp.domain.learning.GenerateQuizUseCase
import com.example.fyp.domain.learning.GenerationEligibility
import com.example.fyp.core.AiConfig
import com.example.fyp.domain.settings.ObserveUserSettingsUseCase
import com.example.fyp.model.user.AuthState
import com.example.fyp.model.TranslationRecord
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.fyp.data.settings.UserSettingsRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

data class LearningUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val primaryLanguageCode: String = "en-US",
    val records: List<TranslationRecord> = emptyList(),
    val clusters: List<LanguageClusterUi> = emptyList(),
    val generatingLanguageCode: String? = null,

    // languageCode -> whether a sheet exists in Firestore for current primaryLanguageCode
    val sheetExistsByLanguage: Map<String, Boolean> = emptyMap(),

    // languageCode -> count saved in Firestore when sheet was generated (for unchanged-count rule)
    val sheetCountByLanguage: Map<String, Int> = emptyMap(),

    // Quiz generation state (survives navigation)
    val generatingQuizLanguageCode: String? = null,
    // languageCode -> history count when quiz was generated
    val quizCountByLanguage: Map<String, Int> = emptyMap(),
    // languageCode -> history count when last coins were awarded (for anti-cheat)
    val lastAwardedQuizCountByLanguage: Map<String, Int> = emptyMap(),
)

@HiltViewModel
class LearningViewModel @Inject constructor(
    private val authRepo: FirebaseAuthRepository,
    private val sheetsRepo: LearningSheetsRepository,
    private val sharedHistoryDataSource: SharedHistoryDataSource,
    private val observeUserSettings: ObserveUserSettingsUseCase,
    private val generateLearningMaterials: GenerateLearningMaterialsUseCase,
    private val userSettingsRepo: UserSettingsRepository,
    private val generateQuizUseCase: GenerateQuizUseCase,
    private val quizRepo: QuizRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(LearningUiState())
    val uiState: StateFlow<LearningUiState> = _uiState.asStateFlow()

    private val json = Json { ignoreUnknownKeys = true }
    private var uid: String? = null
    private var historyJob: Job? = null

    // Prefetch flag to avoid redundant prefetching (Priority 2 #14)
    private var isPrefetched = false
    private var settingsJob: Job? = null
    private var generationJob: Job? = null
    private var quizGenerationJob: Job? = null

    init {
        viewModelScope.launch {
            authRepo.currentUserState.collect { auth ->
                when (auth) {
                    is AuthState.LoggedIn -> start(auth.user.uid)
                    AuthState.Loading -> {
                        stopJobs()
                        _uiState.value = LearningUiState(isLoading = true)
                    }
                    AuthState.LoggedOut -> {
                        stopJobs()
                        sharedHistoryDataSource.stopObserving()
                        _uiState.value = LearningUiState(isLoading = false, error = "Not logged in")
                    }
                }
            }
        }
    }

    private fun stopJobs() {
        historyJob?.cancel()
        settingsJob?.cancel()
        generationJob?.cancel()
        quizGenerationJob?.cancel()
        historyJob = null
        settingsJob = null
        generationJob = null
        quizGenerationJob = null
    }

    private fun start(uid: String) {
        this.uid = uid
        stopJobs()
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)

        settingsJob = viewModelScope.launch {
            observeUserSettings(uid).collect { s ->
                val primary = s.primaryLanguageCode.ifBlank { "en-US" }

                val prevPrimary = _uiState.value.primaryLanguageCode
                val primaryChanged = prevPrimary != primary

                _uiState.value = _uiState.value.copy(
                    primaryLanguageCode = primary,
                    isLoading = false,
                    // Critical: reset meta when switching primary language
                    generatingLanguageCode = if (primaryChanged) null else _uiState.value.generatingLanguageCode,
                    generatingQuizLanguageCode = if (primaryChanged) null else _uiState.value.generatingQuizLanguageCode,
                    sheetExistsByLanguage = if (primaryChanged) emptyMap() else _uiState.value.sheetExistsByLanguage,
                    sheetCountByLanguage = if (primaryChanged) emptyMap() else _uiState.value.sheetCountByLanguage,
                    quizCountByLanguage = if (primaryChanged) emptyMap() else _uiState.value.quizCountByLanguage,
                )

                // When primary language changes, force refresh (bypass debounce)
                if (primaryChanged) {
                    sharedHistoryDataSource.forceRefreshLanguageCounts(primary)
                    refreshClusters()
                }
            }
        }

        // Use shared history data source (single listener shared across ViewModels)
        sharedHistoryDataSource.startObserving(uid)

        // Same pattern as WordBank: refresh counts then clusters in same collect block
        historyJob = viewModelScope.launch {
            // Force refresh on initial load to ensure data is fresh when user enters screen
            sharedHistoryDataSource.forceRefreshLanguageCounts(_uiState.value.primaryLanguageCode)
            refreshClusters()

            sharedHistoryDataSource.historyRecords.collect { records ->
                _uiState.value = _uiState.value.copy(records = records, isLoading = false)
                // Refresh total language counts for generation eligibility
                sharedHistoryDataSource.refreshLanguageCounts(_uiState.value.primaryLanguageCode)
                // Then refresh clusters (same as WordBank pattern)
                refreshClusters()
            }
        }
    }

    private fun refreshClusters() {
        viewModelScope.launch {
            val s = _uiState.value
            // Use TOTAL language counts from all records, not limited display records
            val languageCounts = sharedHistoryDataSource.languageCounts.value
            val clusters = buildLanguageClustersFromCounts(languageCounts, s.primaryLanguageCode)
            _uiState.value = s.copy(clusters = clusters)

            refreshSheetMetaForClusters()
        }
    }

    /**
     * Build language clusters from total counts (not limited records).
     * Excludes the primary language (like WordBank does).
     */
    private fun buildLanguageClustersFromCounts(
        languageCounts: Map<String, Int>,
        primaryLanguageCode: String
    ): List<LanguageClusterUi> {
        return languageCounts
            .filter { (code, _) ->
                // Show all languages from history except the primary language
                code != primaryLanguageCode && code.isNotBlank()
            }
            .map { (code, count) -> LanguageClusterUi(code, count) }
            .sortedWith(compareByDescending<LanguageClusterUi> { it.count }.thenBy { it.languageCode })
    }

    // Cache for sheet metadata to avoid repeated Firestore reads
    // Using LRU cache with max 50 entries to prevent memory leaks
    private val sheetMetaCache = object : LinkedHashMap<String, SheetMetaCache>(
        16,  // Initial capacity
        0.75f,  // Load factor
        true  // Access order (for LRU)
    ) {
        override fun removeEldestEntry(eldest: MutableMap.MutableEntry<String, SheetMetaCache>): Boolean {
            return size > 50  // Max 50 entries to prevent unbounded growth
        }
    }
    private var lastPrimaryForCache: String? = null

    private data class SheetMetaCache(
        val exists: Boolean,
        val sheetCount: Int?,
        val quizCount: Int?,
        val lastAwardedCount: Int?
    )

    private fun refreshSheetMetaForClusters() {
        val uid = this.uid ?: return
        val s = _uiState.value
        val primary = s.primaryLanguageCode
        val languages = s.clusters.map { it.languageCode }
        if (languages.isEmpty()) return

        // Clear cache if primary language changed
        if (lastPrimaryForCache != primary) {
            sheetMetaCache.clear()
            lastPrimaryForCache = primary
        }

        viewModelScope.launch {
            val existsMap = mutableMapOf<String, Boolean>()
            val countMap = mutableMapOf<String, Int>()
            val quizCountMap = mutableMapOf<String, Int>()
            val lastAwardedCountMap = mutableMapOf<String, Int>()
            var firstError: String? = null

            // Only fetch metadata for languages not in cache
            val languagesToFetch = languages.filter { it !in sheetMetaCache }

            // OPTIMIZATION: Batch all reads together instead of sequential queries
            if (languagesToFetch.isNotEmpty()) {
                try {
                    // Fetch all sheets, quizzes, and awards concurrently using coroutineScope
                    coroutineScope {
                        val results = languagesToFetch.map { lang ->
                            lang to async {
                                try {
                                    val doc = sheetsRepo.getSheet(uid, primary, lang)
                                    val quizDoc = quizRepo.getGeneratedQuizDoc(uid, primary, lang)
                                    val lastAwarded = quizRepo.getLastAwardedQuizCount(uid, primary, lang)
                                    SheetMetaCache(
                                        exists = doc != null,
                                        sheetCount = doc?.historyCountAtGenerate,
                                        quizCount = quizDoc?.historyCountAtGenerate,
                                        lastAwardedCount = lastAwarded
                                    )
                                } catch (ce: CancellationException) {
                                    throw ce
                                } catch (e: Exception) {
                                    if (firstError == null) firstError = e.message ?: "Failed to load learning sheets."
                                    SheetMetaCache(false, null, null, null)
                                }
                            }
                        }

                        // Await all results
                        results.forEach { pair ->
                            sheetMetaCache[pair.first] = pair.second.await()
                        }
                    }
                } catch (ce: CancellationException) {
                    throw ce
                } catch (e: Exception) {
                    if (firstError == null) firstError = e.message ?: "Failed to load learning sheets."
                }
            }

            // Build result maps from cache
            for (lang in languages) {
                val cached = sheetMetaCache[lang]
                if (cached != null) {
                    existsMap[lang] = cached.exists
                    cached.sheetCount?.let { countMap[lang] = it }
                    cached.quizCount?.let { quizCountMap[lang] = it }
                    cached.lastAwardedCount?.let { lastAwardedCountMap[lang] = it }
                }
            }

            _uiState.value = _uiState.value.copy(
                sheetExistsByLanguage = existsMap,
                sheetCountByLanguage = countMap,
                quizCountByLanguage = quizCountMap,
                lastAwardedQuizCountByLanguage = lastAwardedCountMap,
                error = firstError
            )
        }
    }

    /**
     * Invalidate cache for a specific language (e.g., after generating materials/quiz)
     */
    fun invalidateSheetCache(languageCode: String) {
        sheetMetaCache.remove(languageCode)
    }

    fun generateFor(languageCode: String) {
        val uid = this.uid ?: run {
            _uiState.value = uiState.value.copy(error = "Not logged in")
            return
        }

        val current = uiState.value
        val primary = current.primaryLanguageCode
        val countNow = current.clusters.firstOrNull { it.languageCode == languageCode }?.count ?: 0
        val lastCount = current.sheetCountByLanguage[languageCode]

        if (countNow == 0) return
        if (lastCount != null && lastCount == countNow) return

        // ANTI-CHEAT: Use domain logic for eligibility check
        // First generation always allowed (lastCount == null)
        if (lastCount != null && !GenerationEligibility.canRegenerateLearningSheet(countNow, lastCount)) return

        if (current.generatingLanguageCode != null) return

        generationJob = viewModelScope.launch {
            _uiState.value = uiState.value.copy(generatingLanguageCode = languageCode, error = null)

            try {
                val raw = generateLearningMaterials(
                    deployment = AiConfig.DEFAULT_DEPLOYMENT,
                    primaryLanguageCode = primary,
                    targetLanguageCode = languageCode,
                    records = current.records
                )

                ensureActive()

                val materialOnly = com.example.fyp.data.learning.ContentCleaner.removeQuizFromContent(raw)

                sheetsRepo.upsertSheet(
                    uid = uid,
                    primary = primary,
                    target = languageCode,
                    content = materialOnly,
                    historyCountAtGenerate = countNow
                )

                // Invalidate cache so next refresh fetches fresh data
                invalidateSheetCache(languageCode)

                _uiState.value = uiState.value.copy(
                    generatingLanguageCode = null,
                    sheetExistsByLanguage = uiState.value.sheetExistsByLanguage + (languageCode to true),
                    sheetCountByLanguage = uiState.value.sheetCountByLanguage + (languageCode to countNow),
                    error = null
                )
            } catch (ce: CancellationException) {
                _uiState.value = uiState.value.copy(
                    generatingLanguageCode = null,
                    error = ce.message ?: "Generate cancelled"
                )
            } catch (e: Exception) {
                _uiState.value = uiState.value.copy(
                    generatingLanguageCode = null,
                    error = e.message ?: "Generate failed"
                )
            } finally {
                generationJob = null
            }
        }
    }

    /** Generate quiz in background (survives navigation like materials generation) */
    fun generateQuizFor(languageCode: String, sheetContent: String, sheetHistoryCount: Int) {
        val uid = this.uid ?: run {
            _uiState.value = uiState.value.copy(error = "Not logged in")
            return
        }

        val current = uiState.value
        val primary = current.primaryLanguageCode
        val lastQuizCount = current.quizCountByLanguage[languageCode]

        // ANTI-CHEAT Rule 1: Quiz can ONLY be generated when material version changes
        // If a quiz already exists for the current material version, NO regen allowed
        if (lastQuizCount != null && lastQuizCount == sheetHistoryCount) {
            _uiState.value = current.copy(
                generatingQuizLanguageCode = null
            )
            return
        }
        if (current.generatingQuizLanguageCode != null) return

        val materialOnly = com.example.fyp.data.learning.ContentCleaner
            .removeQuizFromContent(sheetContent).trim()
        if (materialOnly.isBlank()) return

        val relatedRecords = current.records.filter { it.sourceLang == languageCode || it.targetLang == languageCode }

        quizGenerationJob = viewModelScope.launch {
            _uiState.value = uiState.value.copy(generatingQuizLanguageCode = languageCode, error = null)

            try {
                val quizText = generateQuizUseCase(
                    deployment = AiConfig.DEFAULT_DEPLOYMENT,
                    primaryLanguageCode = primary,
                    targetLanguageCode = languageCode,
                    records = relatedRecords,
                    learningMaterial = materialOnly
                )

                ensureActive()

                val questions = QuizParser.parseQuizFromContent(quizText)
                if (questions.size < 10) {
                    _uiState.value = uiState.value.copy(
                        generatingQuizLanguageCode = null,
                        error = "Quiz generated but only parsed ${questions.size}/10 questions. Try regenerate."
                    )
                    return@launch
                }

                quizRepo.upsertGeneratedQuiz(
                    uid = uid,
                    primaryCode = primary,
                    targetCode = languageCode,
                    quizData = json.encodeToString(questions.take(10)),
                    historyCountAtGenerate = sheetHistoryCount
                )

                _uiState.value = uiState.value.copy(
                    generatingQuizLanguageCode = null,
                    quizCountByLanguage = uiState.value.quizCountByLanguage + (languageCode to sheetHistoryCount),
                    error = null
                )
            } catch (ce: CancellationException) {
                _uiState.value = uiState.value.copy(
                    generatingQuizLanguageCode = null,
                    error = "Quiz generation cancelled"
                )
            } catch (e: Exception) {
                _uiState.value = uiState.value.copy(
                    generatingQuizLanguageCode = null,
                    error = e.message ?: "Quiz generation failed"
                )
            } finally {
                quizGenerationJob = null
            }
        }
    }

    fun cancelQuizGenerate() {
        quizGenerationJob?.cancel()
        quizGenerationJob = null
        _uiState.value = uiState.value.copy(generatingQuizLanguageCode = null)
    }

    /**
     * Pre-fetch learning sheet metadata in background (Priority 2 #14).
     * Called from MainActivity or HomeScreen after user logs in.
     * Ensures instant learning screen display when user navigates to it.
     */
    fun prefetchSheetMetadata() {
        if (isPrefetched) return
        isPrefetched = true
        
        viewModelScope.launch {
            try {
                refreshSheetMetaForClusters()
            } catch (e: Exception) {
                // Ignore errors on prefetch - silent background operation
                android.util.Log.d("LearningVM", "Prefetch failed (non-critical): ${e.message}")
            }
        }
    }

    fun setPrimaryLanguage(languageCode: String) {
        val uid = this.uid ?: return
        viewModelScope.launch {
            userSettingsRepo.setPrimaryLanguage(uid, languageCode)
        }
    }

    fun cancelGenerate() {
        generationJob?.cancel()
        generationJob = null
        _uiState.value = uiState.value.copy(generatingLanguageCode = null)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}