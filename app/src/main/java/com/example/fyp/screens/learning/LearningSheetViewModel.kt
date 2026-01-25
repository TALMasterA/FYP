package com.example.fyp.screens.learning

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fyp.data.auth.FirebaseAuthRepository
import com.example.fyp.data.learning.FirestoreLearningSheetsRepository
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
    val historyCountAtGenerate: Int? = null,
    val countNow: Int = 0,
    // Quiz related
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
) : ViewModel() {

    private val primaryCode: String = savedStateHandle.get<String>("primaryCode").orEmpty()
    private val targetCode: String = savedStateHandle.get<String>("targetCode").orEmpty()

    private val _uiState = MutableStateFlow(
        LearningSheetUiState(
            primaryLanguageCode = primaryCode,
            targetLanguageCode = targetCode
        )
    )
    val uiState: StateFlow<LearningSheetUiState> = _uiState.asStateFlow()

    private var uid: String? = null
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
                        _uiState.value =
                            _uiState.value.copy(isLoading = false, error = "Not logged in")
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
        this.uid = uid
        stopJobs()
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)

        // Load sheet once when screen starts
        loadSheet()

        historyJob = viewModelScope.launch {
            observeUserHistory(uid).collect { records ->
                val countNow = countInvolvingLanguage(records, targetCode)
                _uiState.value = _uiState.value.copy(countNow = countNow)
            }
        }
    }

    private fun countInvolvingLanguage(records: List<TranslationRecord>, lang: String): Int {
        if (lang.isBlank()) return 0
        return records.count { it.sourceLang == lang || it.targetLang == lang }
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
        val content = _uiState.value.content ?: return

        Log.d("QuizDebug", "initializeQuiz called")
        Log.d("QuizDebug", "Content length: ${content.length}")

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(quizLoading = true, quizError = null)
            try {
                // Extract quiz section from content
                val quizSection = com.example.fyp.data.learning.ContentCleaner.extractQuizSection(content)
                Log.d("QuizDebug", "Quiz section length: ${quizSection.length}")

                if (quizSection.isBlank()) {
                    Log.w("QuizDebug", "No quiz section found in content")
                    _uiState.value = _uiState.value.copy(
                        quizLoading = false,
                        quizError = "No quiz section found. Please re-generate the materials."
                    )
                    return@launch
                }

                // Parse questions from quiz section
                Log.d("QuizDebug", "Starting quiz parsing")
                val questions = com.example.fyp.data.learning.QuizParser.parseQuizFromContent(quizSection)

                Log.d("QuizDebug", "Parsed ${questions.size} questions")

                // Validate that we got questions
                if (questions.isEmpty()) {
                    Log.w("QuizDebug", "No questions parsed from quiz section")
                    _uiState.value = _uiState.value.copy(
                        quizLoading = false,
                        quizError = "No quiz questions found. Please re-generate the materials."
                    )
                    return@launch
                }

                val attempt = parseAndStoreQuiz.createAttempt(
                    userId = uid ?: "",
                    primaryLanguageCode = primaryCode,
                    targetLanguageCode = targetCode,
                    questions = questions
                )

                Log.d("QuizDebug", "Quiz attempt created successfully with ${questions.size} questions")
                _uiState.value = _uiState.value.copy(
                    quizLoading = false,
                    quizQuestions = questions,
                    currentAttempt = attempt,
                    isQuizTaken = false,
                    quizError = null
                )
            } catch (e: Exception) {
                Log.e("QuizDebug", "Exception in initializeQuiz: ${e.message}", e)
                _uiState.value = _uiState.value.copy(
                    quizLoading = false,
                    quizError = "Failed to load quiz: ${e.message ?: "Unknown error"}"
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
}