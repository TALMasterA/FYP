package com.example.fyp.screens.learning

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fyp.data.auth.FirebaseAuthRepository
import com.example.fyp.data.learning.FirestoreLearningSheetsRepository
import com.example.fyp.data.learning.FirestoreQuizRepository
import com.example.fyp.data.learning.QuizParser
import com.example.fyp.domain.history.ObserveUserHistoryUseCase
import com.example.fyp.domain.learning.GenerateLearningMaterialsUseCase
import com.example.fyp.domain.learning.GenerateQuizUseCase
import com.example.fyp.domain.settings.ObserveUserSettingsUseCase
import com.example.fyp.model.AuthState
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
)

@HiltViewModel
class LearningViewModel @Inject constructor(
    private val authRepo: FirebaseAuthRepository,
    private val sheetsRepo: FirestoreLearningSheetsRepository,
    private val observeUserHistory: ObserveUserHistoryUseCase,
    private val observeUserSettings: ObserveUserSettingsUseCase,
    private val generateLearningMaterials: GenerateLearningMaterialsUseCase,
    private val userSettingsRepo: UserSettingsRepository,
    private val generateQuizUseCase: GenerateQuizUseCase,
    private val quizRepo: FirestoreQuizRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(LearningUiState())
    val uiState: StateFlow<LearningUiState> = _uiState.asStateFlow()

    private var uid: String? = null
    private var historyJob: Job? = null
    private var settingsJob: Job? = null
    private var supportedLanguages: Set<String> = emptySet()
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
                        _uiState.value = LearningUiState(isLoading = false, error = "Not logged in")
                    }
                }
            }
        }
    }

    fun setSupportedLanguages(codes: Set<String>) {
        supportedLanguages = codes
        recomputeClusters()
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
                recomputeClusters()
            }
        }

        historyJob = viewModelScope.launch {
            observeUserHistory(uid).collect { records ->
                _uiState.value = _uiState.value.copy(records = records, isLoading = false)
                recomputeClusters()
            }
        }
    }

    private fun recomputeClusters() {
        val s = _uiState.value
        val clusters = if (supportedLanguages.isEmpty()) {
            emptyList()
        } else {
            buildLanguageClusters(s.records, s.primaryLanguageCode, supportedLanguages)
        }
        _uiState.value = s.copy(clusters = clusters)

        refreshSheetMetaForClusters()
    }

    private fun refreshSheetMetaForClusters() {
        val uid = this.uid ?: return
        val s = _uiState.value
        val primary = s.primaryLanguageCode
        val languages = s.clusters.map { it.languageCode }
        if (languages.isEmpty()) return

        viewModelScope.launch {
            val existsMap = mutableMapOf<String, Boolean>()
            val countMap = mutableMapOf<String, Int>()
            val quizCountMap = mutableMapOf<String, Int>()
            var firstError: String? = null

            for (lang in languages) {
                try {
                    val doc = sheetsRepo.getSheet(uid, primary, lang)
                    existsMap[lang] = (doc != null)
                    if (doc != null) countMap[lang] = doc.historyCountAtGenerate

                    // Also fetch quiz metadata
                    val quizDoc = quizRepo.getGeneratedQuizDoc(uid, primary, lang)
                    if (quizDoc != null) quizCountMap[lang] = quizDoc.historyCountAtGenerate
                } catch (ce: CancellationException) {
                    throw ce
                } catch (e: Exception) {
                    if (firstError == null) firstError = e.message ?: "Failed to load learning sheets."
                    existsMap[lang] = false
                }
            }

            _uiState.value = _uiState.value.copy(
                sheetExistsByLanguage = existsMap,
                sheetCountByLanguage = countMap,
                quizCountByLanguage = quizCountMap,
                error = firstError
            )
        }
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
        if (current.generatingLanguageCode != null) return

        generationJob = viewModelScope.launch {
            _uiState.value = uiState.value.copy(generatingLanguageCode = languageCode, error = null)

            try {
                val raw = generateLearningMaterials(
                    deployment = "gpt-5-mini",
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

        // Disable if count unchanged (same sheet version = same quiz)
        if (lastQuizCount != null && lastQuizCount == sheetHistoryCount) return
        if (current.generatingQuizLanguageCode != null) return

        val materialOnly = com.example.fyp.data.learning.ContentCleaner
            .removeQuizFromContent(sheetContent).trim()
        if (materialOnly.isBlank()) return

        val relatedRecords = current.records.filter { it.sourceLang == languageCode || it.targetLang == languageCode }

        quizGenerationJob = viewModelScope.launch {
            _uiState.value = uiState.value.copy(generatingQuizLanguageCode = languageCode, error = null)

            try {
                val quizText = generateQuizUseCase(
                    deployment = "gpt-5-mini",
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
                    primaryLanguageCode = primary,
                    targetLanguageCode = languageCode,
                    questions = questions.take(10),
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
}