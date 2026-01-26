package com.example.fyp.screens.learning

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fyp.data.auth.FirebaseAuthRepository
import com.example.fyp.data.learning.FirestoreLearningSheetsRepository
import com.example.fyp.domain.history.ObserveUserHistoryUseCase
import com.example.fyp.domain.learning.ParseAndStoreQuizUseCase
import com.example.fyp.domain.learning.GenerateQuizUseCase
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
import com.example.fyp.data.learning.FirestoreQuizRepository
import com.example.fyp.data.learning.QuizParser

data class LearningSheetUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val primaryLanguageCode: String = "en-US",
    val targetLanguageCode: String = "",
    val content: String? = null,
    val historyCountAtGenerate: Int? = null,
    val countNow: Int = 0,
    val quizQuestions: List<QuizQuestion> = emptyList(),
    val isQuizTaken: Boolean = false,
    val currentAttempt: QuizAttempt? = null,
    val quizLoading: Boolean = false,
    val quizError: String? = null,
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
                            quizError = null
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

        // Load sheet once when screen starts
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
        val uid = this.uid ?: return
        if (primaryCode.isBlank() || targetCode.isBlank()) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            runCatching { sheetsRepo.getSheet(uid, primaryCode, targetCode) }
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

    // Quiz methods
    fun initializeQuiz() {
        val uidNow = this.uid
        if (uidNow == null) {
            pendingInitQuiz = true
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(quizLoading = true, quizError = null)

            try {
                val questions = quizRepo.getGeneratedQuizQuestions(
                    uid = uidNow,
                    primaryLanguageCode = primaryCode,
                    targetLanguageCode = targetCode
                )

                if (questions.isEmpty()) {
                    _uiState.value = _uiState.value.copy(
                        quizLoading = false,
                        quizError = "No quiz generated yet. Go back and press Quiz to generate."
                    )
                    return@launch
                }

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
                    quizError = null
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
        val updatedAttempt = parseAndStoreQuiz.recordAnswer(
            attempt,
            questionId,
            selectedOptionIndex
        )
        _uiState.value = _uiState.value.copy(currentAttempt = updatedAttempt)
    }

    fun submitQuiz() {
        val uid = this.uid ?: return
        val attempt = _uiState.value.currentAttempt ?: return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(quizLoading = true)
            try {
                val completedAttempt = parseAndStoreQuiz.completeAttempt(attempt)
                val attemptId = parseAndStoreQuiz.saveAttempt(uid, completedAttempt)

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

    fun generateQuizAndSave() {
        if (_uiState.value.quizLoading) return

        val uid = this.uid ?: run {
            _uiState.value = _uiState.value.copy(quizError = "Not logged in")
            return
        }

        val content = _uiState.value.content.orEmpty()
        if (content.isBlank()) {
            _uiState.value = _uiState.value.copy(quizError = "No learning materials found. Generate the sheet first.")
            return
        }

        // Use material-only text as prompt input
        val materialOnly = com.example.fyp.data.learning.ContentCleaner.removeQuizFromContent(content).trim()
        if (materialOnly.isBlank()) {
            _uiState.value = _uiState.value.copy(quizError = "Learning materials are empty.")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(quizLoading = true, quizError = null)

            try {
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
                        quizError = "Quiz generated but only parsed ${questions.size}/10 questions. Try again. (You may need to regen the learning materials)"
                    )
                    return@launch
                }

                // Save latest generated quiz for this pair
                quizRepo.upsertGeneratedQuiz(
                    uid = uid,
                    primaryLanguageCode = primaryCode,
                    targetLanguageCode = targetCode,
                    questions = questions.take(10),
                    historyCountAtGenerate = _uiState.value.countNow
                )

                _uiState.value = _uiState.value.copy(
                    quizLoading = false,
                    quizQuestions = questions.take(10),
                    quizError = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    quizLoading = false,
                    quizError = e.message ?: "Failed to generate quiz"
                )
            }
        }
    }
}