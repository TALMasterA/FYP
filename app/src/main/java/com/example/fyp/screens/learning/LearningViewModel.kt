package com.example.fyp.screens.learning

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fyp.data.auth.FirebaseAuthRepository
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
    val isGenerating: Boolean = false,
    val generatedContent: String? = null
)

@HiltViewModel
class LearningViewModel @Inject constructor(
    private val authRepo: FirebaseAuthRepository,
    private val observeUserHistory: ObserveUserHistoryUseCase,
    private val observeUserSettings: ObserveUserSettingsUseCase,
    private val generateLearningMaterials: GenerateLearningMaterialsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(LearningUiState())
    val uiState: StateFlow<LearningUiState> = _uiState.asStateFlow()

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
        val clusters = if (supportedLanguages.isEmpty()) emptyList() else
            buildLanguageClusters(s.records, s.primaryLanguageCode, supportedLanguages)
        _uiState.value = s.copy(clusters = clusters)
    }

    fun generateFor(languageCode: String) {
        val current = _uiState.value
        viewModelScope.launch {
            _uiState.value = current.copy(isGenerating = true, error = null, generatedContent = null)

            runCatching {
                generateLearningMaterials(
                    deployment = "gpt-5-mini",
                    primaryLanguageCode = current.primaryLanguageCode,
                    targetLanguageCode = languageCode,
                    records = current.records
                )
            }.onSuccess { content ->
                _uiState.value = _uiState.value.copy(isGenerating = false, generatedContent = content)
            }.onFailure { e ->
                _uiState.value = _uiState.value.copy(isGenerating = false, error = e.message ?: "Generate failed")
            }
        }
    }
}