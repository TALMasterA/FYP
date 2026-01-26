package com.example.fyp.screens.learning

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fyp.data.auth.FirebaseAuthRepository
import com.example.fyp.data.learning.FirestoreLearningSheetsRepository
import com.example.fyp.data.learning.FirestoreQuizRepository
import com.example.fyp.data.learning.QuizParser
import com.example.fyp.domain.history.ObserveUserHistoryUseCase
import com.example.fyp.domain.learning.GenerateQuizUseCase
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
    private val generateQuiz: GenerateQuizUseCase,
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
                    questions = questions
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
                _uiState.value = _uiState.value.copy(
                    quizLoading = false,
                    currentAttempt = completedAttempt.copy(id = attemptId),
                    isQuizTaken = true
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

    fun generateQuizAndSave(force: Boolean = false) {
        if (_uiState.value.quizLoading) return

        val uidNow = uid ?: run {
            _uiState.value = _uiState.value.copy(quizError = "Not logged in")
            return
        }

        // sheet regenerated marker MUST exist
        val sheetVersion = _uiState.value.historyCountAtGenerate ?: run {
            _uiState.value = _uiState.value.copy(quizError = "Sheet not loaded yet. Please wait and try again.")
            return
        }

        val content = _uiState.value.content.orEmpty()
        if (content.isBlank()) {
            _uiState.value = _uiState.value.copy(quizError = "No learning materials found. Generate the sheet first.")
            return
        }

        val materialOnly = com.example.fyp.data.learning.ContentCleaner
            .removeQuizFromContent(content).trim()
        if (materialOnly.isBlank()) {
            _uiState.value = _uiState.value.copy(quizError = "Learning materials are empty.")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(quizLoading = true, quizError = null)

            try {
                val existing = quizRepo.getGeneratedQuizDoc(uidNow, primaryCode, targetCode)
                if (!force && existing != null && existing.historyCountAtGenerate == sheetVersion) {
                    _uiState.value = _uiState.value.copy(quizLoading = false)
                    initializeQuiz()
                    return@launch
                }

                val quizText = generateQuiz(
                    deployment = "gpt-5-mini",
                    primaryLanguageCode = primaryCode,
                    targetLanguageCode = targetCode,
                    records = latestRelatedRecords,
                    learningMaterial = materialOnly
                )

                val questions = QuizParser.parseQuizFromContent(quizText)
                if (questions.size < 10) {
                    _uiState.value = _uiState.value.copy(
                        quizLoading = false,
                        quizError = "Quiz generated but only parsed ${questions.size}/10 questions. Try regenerate."
                    )
                    return@launch
                }

                // IMPORTANT: save the SHEET version
                quizRepo.upsertGeneratedQuiz(
                    uid = uidNow,
                    primaryLanguageCode = primaryCode,
                    targetLanguageCode = targetCode,
                    questions = questions.take(10),
                    historyCountAtGenerate = sheetVersion
                )

                _uiState.value = _uiState.value.copy(quizLoading = false)
                initializeQuiz()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    quizLoading = false,
                    quizError = e.message ?: "Failed to generate quiz"
                )
            }
        }
    }
}