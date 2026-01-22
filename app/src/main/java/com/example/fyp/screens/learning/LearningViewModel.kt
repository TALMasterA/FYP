package com.example.fyp.screens.learning

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fyp.data.auth.FirebaseAuthRepository
import com.example.fyp.data.learning.FirestoreLearningSheetsRepository
import com.example.fyp.domain.history.ObserveUserHistoryUseCase
import com.example.fyp.domain.learning.GenerateLearningMaterialsUseCase
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
)

@HiltViewModel
class LearningViewModel @Inject constructor(
    private val authRepo: FirebaseAuthRepository,
    private val sheetsRepo: FirestoreLearningSheetsRepository,
    private val observeUserHistory: ObserveUserHistoryUseCase,
    private val observeUserSettings: ObserveUserSettingsUseCase,
    private val generateLearningMaterials: GenerateLearningMaterialsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(LearningUiState())
    val uiState: StateFlow<LearningUiState> = _uiState.asStateFlow()

    private var uid: String? = null
    private var historyJob: Job? = null
    private var settingsJob: Job? = null
    private var supportedLanguages: Set<String> = emptySet()

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
        historyJob = null
        settingsJob = null
    }

    private fun start(uid: String) {
        this.uid = uid
        stopJobs()
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)

        settingsJob = viewModelScope.launch {
            observeUserSettings(uid).collect { s ->
                val primary = s.primaryLanguageCode.ifBlank { "en-US" }
                _uiState.value = _uiState.value.copy(primaryLanguageCode = primary, isLoading = false)
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
            var existsMap = s.sheetExistsByLanguage
            var countMap = s.sheetCountByLanguage

            for (lang in languages) {
                val doc = sheetsRepo.getSheet(uid, primary, lang)
                existsMap = existsMap + (lang to (doc != null))
                if (doc != null) countMap = countMap + (lang to doc.historyCountAtGenerate)
            }

            _uiState.value = _uiState.value.copy(
                sheetExistsByLanguage = existsMap,
                sheetCountByLanguage = countMap
            )
        }
    }

    fun generateFor(languageCode: String) {
        val uid = this.uid ?: run {
            _uiState.value = _uiState.value.copy(error = "Not logged in")
            return
        }

        val current = _uiState.value
        val primary = current.primaryLanguageCode
        val countNow = current.clusters.firstOrNull { it.languageCode == languageCode }?.count ?: 0
        val lastCount = current.sheetCountByLanguage[languageCode]

        // Disable rule: unchanged count (also disable if count==0)
        if (countNow == 0) return
        if (lastCount != null && lastCount == countNow) return

        // ADDED: prevent multiple simultaneous generations
        if (current.generatingLanguageCode != null) return

        viewModelScope.launch {
            _uiState.value = current.copy(generatingLanguageCode = languageCode, error = null)

            runCatching {
                generateLearningMaterials(
                    deployment = "gpt-5-mini",
                    primaryLanguageCode = primary,
                    targetLanguageCode = languageCode,
                    records = current.records
                )
            }.onSuccess { content ->
                sheetsRepo.upsertSheet(
                    uid = uid,
                    primary = primary,
                    target = languageCode,
                    content = content,
                    historyCountAtGenerate = countNow
                )

                _uiState.value = _uiState.value.copy(
                    generatingLanguageCode = null,
                    sheetExistsByLanguage = _uiState.value.sheetExistsByLanguage + (languageCode to true),
                    sheetCountByLanguage = _uiState.value.sheetCountByLanguage + (languageCode to countNow)
                )
            }.onFailure { e ->
                _uiState.value = _uiState.value.copy(
                    generatingLanguageCode = null,
                    error = e.message ?: "Generate failed"
                )
            }
        }
    }
}