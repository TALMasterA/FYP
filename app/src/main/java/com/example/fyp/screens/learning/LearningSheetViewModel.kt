package com.example.fyp.screens.learning

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fyp.data.auth.FirebaseAuthRepository
import com.example.fyp.data.learning.FirestoreLearningSheetsRepository
import com.example.fyp.data.learning.FirestoreQuizRepository
import com.example.fyp.domain.history.ObserveUserHistoryUseCase
import com.example.fyp.domain.learning.ParseAndStoreQuizUseCase
import com.example.fyp.model.AuthState
import com.example.fyp.model.QuizAttempt
import com.example.fyp.model.QuizQuestion
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

    // Sheet regeneration marker (version)
    val historyCountAtGenerate: Int? = null,

    val countNow: Int = 0,

    val quizQuestions: List<QuizQuestion> = emptyList(),
    val isQuizTaken: Boolean = false,
    val currentAttempt: QuizAttempt? = null,
    val quizLoading: Boolean = false,
    val quizError: String? = null,

    // Generated quiz metadata
    val generatedQuizHistoryCountAtGenerate: Int? = null,
    val isQuizOutdated: Boolean = false,
)

@HiltViewModel
class LearningSheetViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val authRepo: FirebaseAuthRepository,
    private val sheetsRepo: FirestoreLearningSheetsRepository,
    private val observeUserHistory: ObserveUserHistoryUseCase,
    private val parseAndStoreQuiz: ParseAndStoreQuizUseCase,
    private val quizRepo: FirestoreQuizRepository,
) : ViewModel() {

    private val primaryCode: String = savedStateHandle.get<String>("primaryCode").orEmpty()
    private val targetCode: String = savedStateHandle.get<String>("targetCode").orEmpty()

    private var latestRelatedRecords: List<TranslationRecord> = emptyList()

    private val _uiState = MutableStateFlow(
        LearningSheetUiState(
            primaryLanguageCode = primaryCode,
            targetLanguageCode = targetCode
        )
    )
    val uiState: StateFlow<LearningSheetUiState> = _uiState.asStateFlow()

    private var uid: String? = null
    private var historyJob: Job? = null
    private var pendingInitQuiz: Boolean = false

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
                        uid = null
                        pendingInitQuiz = false
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = "Not logged in",
                            quizQuestions = emptyList(),
                            currentAttempt = null,
                            isQuizTaken = false,
                            quizLoading = false,
                            quizError = null,
                            generatedQuizHistoryCountAtGenerate = null,
                            isQuizOutdated = false
                        )
                    }
                }
            }
        }
    }

    private fun stopJobs() {
        historyJob?.cancel()
        historyJob = null
    }

    private fun start(uid: String) {
        stopJobs()
        this.uid = uid
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)

        loadSheet()

        if (pendingInitQuiz) {
            pendingInitQuiz = false
            initializeQuiz()
        }

        historyJob = viewModelScope.launch {
            observeUserHistory(uid).collect { records ->
                val related = records.filter { it.sourceLang == targetCode || it.targetLang == targetCode }
                latestRelatedRecords = related
                _uiState.value = _uiState.value.copy(countNow = related.size)
            }
        }
    }

    fun loadSheet() {
        val uidNow = uid ?: return
        if (primaryCode.isBlank() || targetCode.isBlank()) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            runCatching { sheetsRepo.getSheet(uidNow, primaryCode, targetCode) }
                .onSuccess { doc ->
                    val newSheetVersion = doc?.historyCountAtGenerate
                    val generatedVersion = _uiState.value.generatedQuizHistoryCountAtGenerate
                    val newOutdated =
                        (newSheetVersion != null && generatedVersion != null && generatedVersion != newSheetVersion)

                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        content = doc?.content,
                        historyCountAtGenerate = newSheetVersion,
                        isQuizOutdated = newOutdated
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

    fun initializeQuiz() {
        val uidNow = uid
        if (uidNow == null) {
            pendingInitQuiz = true
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(quizLoading = true, quizError = null)

            try {
                val doc = quizRepo.getGeneratedQuizDoc(uidNow, primaryCode, targetCode)
                if (doc == null) {
                    _uiState.value = _uiState.value.copy(
                        quizLoading = false,
                        quizError = "No quiz generated yet.",
                        quizQuestions = emptyList(),
                        currentAttempt = null,
                        generatedQuizHistoryCountAtGenerate = null,
                        isQuizOutdated = false
                    )
                    return@launch
                }

                val questions = quizRepo.getGeneratedQuizQuestions(uidNow, primaryCode, targetCode)
                if (questions.isEmpty()) {
                    _uiState.value = _uiState.value.copy(
                        quizLoading = false,
                        quizError = "Stored quiz is corrupted. Please regenerate.",
                        quizQuestions = emptyList(),
                        currentAttempt = null,
                        generatedQuizHistoryCountAtGenerate = doc.historyCountAtGenerate,
                        isQuizOutdated = true
                    )
                    return@launch
                }

                val sheetVersion = _uiState.value.historyCountAtGenerate
                val isOutdated = (sheetVersion != null && doc.historyCountAtGenerate != sheetVersion)

                val attempt = parseAndStoreQuiz.createAttempt(
                    userId = uidNow,
                    primaryLanguageCode = primaryCode,
                    targetLanguageCode = targetCode,
                    questions = questions,
                    generatedHistoryCountAtGenerate = doc.historyCountAtGenerate
                )

                _uiState.value = _uiState.value.copy(
                    quizLoading = false,
                    quizQuestions = questions,
                    currentAttempt = attempt,
                    isQuizTaken = false,
                    quizError = null,
                    generatedQuizHistoryCountAtGenerate = doc.historyCountAtGenerate,
                    isQuizOutdated = isOutdated
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    quizLoading = false,
                    quizError = e.message ?: "Failed to load quiz"
                )
            }
        }
    }

    fun recordQuizAnswer(questionId: String, selectedOptionIndex: Int) {
        val attempt = _uiState.value.currentAttempt ?: return
        val updatedAttempt = parseAndStoreQuiz.recordAnswer(attempt, questionId, selectedOptionIndex)
        _uiState.value = _uiState.value.copy(currentAttempt = updatedAttempt)
    }

    fun submitQuiz() {
        val uidNow = uid ?: return
        val attempt = _uiState.value.currentAttempt ?: return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(quizLoading = true)
            try {
                val completedAttempt = parseAndStoreQuiz.completeAttempt(attempt)
                val attemptId = parseAndStoreQuiz.saveAttempt(uidNow, completedAttempt)
                val finalAttempt = completedAttempt.copy(id = attemptId)

                // Award coins (first-attempt only) if all conditions are met
                // The latestSheetCount must match the quiz's generatedHistoryCountAtGenerate
                val latestSheetCount = _uiState.value.historyCountAtGenerate
                val coinsAwarded = quizRepo.awardCoinsIfEligible(uidNow, finalAttempt, latestSheetCount)

                // Only show coins alert if coins were actually awarded
                val coinMessage = if (coinsAwarded && finalAttempt.totalScore > 0) {
                    "✨ You earned ${finalAttempt.totalScore} coins!"
                } else {
                    null
                }

                _uiState.value = _uiState.value.copy(
                    quizLoading = false,
                    currentAttempt = finalAttempt,
                    isQuizTaken = true,
                    quizError = coinMessage
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    quizLoading = false,
                    quizError = e.message ?: "Failed to submit quiz"
                )
            }
        }
    }

    fun resetQuiz() {
        _uiState.value = _uiState.value.copy(
            quizQuestions = emptyList(),
            currentAttempt = null,
            isQuizTaken = false,
            quizError = null
        )
    }
}