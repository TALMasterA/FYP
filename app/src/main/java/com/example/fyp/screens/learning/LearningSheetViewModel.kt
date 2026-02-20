package com.example.fyp.screens.learning

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fyp.data.user.FirebaseAuthRepository
import com.example.fyp.data.friends.SharedFriendsDataSource
import com.example.fyp.data.history.SharedHistoryDataSource
import com.example.fyp.domain.friends.ShareLearningMaterialUseCase
import com.example.fyp.domain.learning.LearningSheetsRepository
import com.example.fyp.domain.learning.QuizRepository
import com.example.fyp.domain.learning.ParseAndStoreQuizUseCase
import com.example.fyp.model.friends.FriendRelation
import com.example.fyp.model.friends.SharedItemType
import com.example.fyp.model.user.AuthState
import com.example.fyp.model.QuizAttempt
import com.example.fyp.model.QuizQuestion
import com.example.fyp.model.TranslationRecord
import com.example.fyp.model.UserId
import com.example.fyp.model.LanguageCode
import com.example.fyp.core.decodeOrDefault
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
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

    // Share feature
    val friends: List<FriendRelation> = emptyList(),
    val isSharing: Boolean = false,
    val shareSuccess: String? = null,
    val shareError: String? = null,
    val showShareDialog: Boolean = false,
)

@HiltViewModel
class LearningSheetViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val authRepo: FirebaseAuthRepository,
    private val sheetsRepo: LearningSheetsRepository,
    private val sharedHistoryDataSource: SharedHistoryDataSource,
    private val parseAndStoreQuiz: ParseAndStoreQuizUseCase,
    private val quizRepo: QuizRepository,
    private val shareLearningMaterialUseCase: ShareLearningMaterialUseCase,
    private val sharedFriendsDataSource: SharedFriendsDataSource,
) : ViewModel() {

    private val primaryCode: String = savedStateHandle.get<String>("primaryCode").orEmpty()
    private val targetCode: String = savedStateHandle.get<String>("targetCode").orEmpty()

    private val json = Json { ignoreUnknownKeys = true }
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

        // Mirror friends from the shared in-memory source (no new Firestore listener).
        viewModelScope.launch {
            sharedFriendsDataSource.friends.collect { friends ->
                _uiState.value = _uiState.value.copy(friends = friends)
            }
        }

        if (pendingInitQuiz) {
            pendingInitQuiz = false
            initializeQuiz()
        }

        // Mirror history from the shared data source (no new Firestore listener).
        // countNow is exposed in uiState for use by QuizScreen / quiz logic;
        // LearningSheetScreen reads countNow from learningViewModel.clusters instead.
        historyJob = viewModelScope.launch {
            sharedHistoryDataSource.historyRecords.collect { records ->
                val related = records.filter {
                    it.sourceLang == targetCode || it.targetLang == targetCode
                }
                latestRelatedRecords = related
                // Use the authoritative total count (all records, not just the display limit)
                val count = sharedHistoryDataSource.getCountForLanguage(targetCode)
                    .takeIf { it > 0 } ?: related.size
                _uiState.value = _uiState.value.copy(countNow = count)
            }
        }
    }

    fun loadSheet() {
        val uidNow = uid ?: return
        if (primaryCode.isBlank() || targetCode.isBlank()) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            runCatching { sheetsRepo.getSheet(UserId(uidNow), LanguageCode(primaryCode), LanguageCode(targetCode)) }
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
                // Single Firestore read — parse questions from the same doc object.
                val doc = quizRepo.getGeneratedQuizDoc(
                    UserId(uidNow), LanguageCode(primaryCode), LanguageCode(targetCode)
                )
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

                // Parse questions from the already-fetched doc (no second Firestore read).
                val questions = json.decodeOrDefault<List<QuizQuestion>>(doc.questionsJson, emptyList())

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
                val attemptId = parseAndStoreQuiz.saveAttempt(uidNow, completedAttempt) // This calls ParseAndStoreQuizUseCase.saveAttempt which I fixed to take String wrapper inside. Wait, I updated ParseAndStoreQuizUseCase to take String and wrap it. So here passing String is correct!
                // ERROR: "Argument type mismatch: actual type is 'kotlin.String', but 'com.example.fyp.model.UserId' was expected."
                // Wait, did I fix ParseAndStoreQuizUseCase signature?
                // Yes, I changed `saveAttempt(uid: String, ...)` -> returns `quizRepository.saveAttempt(UserId(uid), ...)`.
                // So calling it with String is CORRECT.
                // The error was from BEFORE my fix.

                val finalAttempt = completedAttempt.copy(id = attemptId)

                // Award coins (first-attempt only) if all conditions are met
                // The latestSheetCount must match the quiz's generatedHistoryCountAtGenerate
                val latestSheetCount = _uiState.value.historyCountAtGenerate
                val coinsAwarded = quizRepo.awardCoinsIfEligible(UserId(uidNow), finalAttempt, latestSheetCount)

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

    // ============================================
    // Share Feature
    // ============================================

    fun showShareDialog() {
        _uiState.value = _uiState.value.copy(showShareDialog = true, shareError = null, shareSuccess = null)
    }

    fun dismissShareDialog() {
        _uiState.value = _uiState.value.copy(showShareDialog = false)
    }

    fun shareSheet(toUserId: UserId) {
        val fromId = uid ?: return
        val state = _uiState.value

        val title = "Learning Sheet: ${state.primaryLanguageCode} → ${state.targetLanguageCode}"
        val materialId = "${state.primaryLanguageCode}_${state.targetLanguageCode}"

        // Resolve username from in-memory cache — avoids a Firestore profile read
        val fromUsername = sharedFriendsDataSource.getCachedUsername(fromId) ?: ""

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSharing = true, shareError = null, shareSuccess = null, showShareDialog = false)
            shareLearningMaterialUseCase(
                fromUserId = UserId(fromId),
                fromUsername = fromUsername,
                toUserId = toUserId,
                type = SharedItemType.LEARNING_SHEET,
                materialId = materialId,
                title = title,
                description = state.content?.take(200) ?: ""
            ).onSuccess {
                _uiState.value = _uiState.value.copy(isSharing = false, shareSuccess = "Shared successfully!")
            }.onFailure { e ->
                _uiState.value = _uiState.value.copy(isSharing = false, shareError = e.message ?: "Failed to share")
            }
        }
    }

    fun clearShareMessages() {
        _uiState.value = _uiState.value.copy(shareSuccess = null, shareError = null)
    }
}