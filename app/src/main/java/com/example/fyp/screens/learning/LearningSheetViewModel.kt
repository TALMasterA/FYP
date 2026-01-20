package com.example.fyp.screens.learning

import androidx.lifecycle.SavedStateHandle
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

data class LearningSheetUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val primaryLanguageCode: String = "en-US",
    val targetLanguageCode: String = "",
    val content: String? = null,
    val historyCountAtGenerate: Int? = null,
    val countNow: Int = 0,
    val isGenerating: Boolean = false
)

@HiltViewModel
class LearningSheetViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val authRepo: FirebaseAuthRepository,
    private val sheetsRepo: FirestoreLearningSheetsRepository,
    private val observeUserHistory: ObserveUserHistoryUseCase,
    private val observeUserSettings: ObserveUserSettingsUseCase,
    private val generateLearningMaterials: GenerateLearningMaterialsUseCase
) : ViewModel() {

    private val languageCode: String = savedStateHandle.get<String>("languageCode").orEmpty()

    private val _uiState = MutableStateFlow(LearningSheetUiState(targetLanguageCode = languageCode))
    val uiState: StateFlow<LearningSheetUiState> = _uiState.asStateFlow()

    private var uid: String? = null
    private var latestRecords: List<TranslationRecord> = emptyList()
    private var settingsJob: Job? = null
    private var historyJob: Job? = null

    init {
        viewModelScope.launch {
            authRepo.currentUserState.collect { auth ->
                when (auth) {
                    is AuthState.LoggedIn -> start(auth.user.uid)
                    AuthState.Loading -> {
                        stopJobs()
                        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
                    }
                    AuthState.LoggedOut -> {
                        stopJobs()
                        _uiState.value = _uiState.value.copy(isLoading = false, error = "Not logged in")
                    }
                }
            }
        }
    }

    private fun stopJobs() {
        settingsJob?.cancel()
        historyJob?.cancel()
        settingsJob = null
        historyJob = null
    }

    private fun start(uid: String) {
        this.uid = uid
        stopJobs()

        _uiState.value = _uiState.value.copy(isLoading = true, error = null)

        settingsJob = viewModelScope.launch {
            observeUserSettings(uid).collect { s ->
                val primary = s.primaryLanguageCode.ifBlank { "en-US" }
                _uiState.value = _uiState.value.copy(primaryLanguageCode = primary, isLoading = false)
                loadSheet()
            }
        }

        historyJob = viewModelScope.launch {
            observeUserHistory(uid).collect { records ->
                latestRecords = records
                val countNow = countInvolvingLanguage(records, languageCode)
                _uiState.value = _uiState.value.copy(countNow = countNow)
                // Do not overwrite content here; content comes from Firestore
            }
        }
    }

    private fun countInvolvingLanguage(records: List<TranslationRecord>, lang: String): Int {
        if (lang.isBlank()) return 0
        return records.count { it.sourceLang == lang || it.targetLang == lang }
    }

    fun loadSheet() {
        val uid = this.uid ?: return
        val s = _uiState.value
        val primary = s.primaryLanguageCode
        val target = s.targetLanguageCode
        if (target.isBlank()) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            runCatching { sheetsRepo.getSheet(uid, primary, target) }
                .onSuccess { doc ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        content = doc?.content,
                        historyCountAtGenerate = doc?.historyCountAtGenerate
                    )
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = e.message ?: "Load sheet failed"
                    )
                }
        }
    }

    fun canRegen(): Boolean {
        val s = _uiState.value
        val last = s.historyCountAtGenerate
        if (s.isGenerating) return false
        if (s.countNow == 0) return false
        if (last != null && last == s.countNow) return false
        return true
    }

    fun regen() {
        val uid = this.uid ?: run {
            _uiState.value = _uiState.value.copy(error = "Not logged in")
            return
        }
        val current = _uiState.value
        val primary = current.primaryLanguageCode
        val target = current.targetLanguageCode

        if (!canRegen()) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isGenerating = true, error = null)

            runCatching {
                generateLearningMaterials(
                    deployment = "gpt-5-mini",
                    primaryLanguageCode = primary,
                    targetLanguageCode = target,
                    records = latestRecords
                )
            }.onSuccess { content ->
                sheetsRepo.upsertSheet(
                    uid = uid,
                    primary = primary,
                    target = target,
                    content = content,
                    historyCountAtGenerate = current.countNow
                )
                _uiState.value = _uiState.value.copy(
                    isGenerating = false,
                    content = content,
                    historyCountAtGenerate = current.countNow
                )
            }.onFailure { e ->
                _uiState.value = _uiState.value.copy(
                    isGenerating = false,
                    error = e.message ?: "Re-gen failed"
                )
            }
        }
    }
}