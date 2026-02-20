package com.example.fyp.screens.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fyp.data.user.FirebaseAuthRepository
import com.example.fyp.data.user.FirestoreFavoritesRepository
import com.example.fyp.data.history.SharedHistoryDataSource
import com.example.fyp.domain.learning.QuizRepository
import com.example.fyp.data.settings.SharedSettingsDataSource
import com.example.fyp.domain.history.DeleteHistoryRecordUseCase
import com.example.fyp.domain.history.DeleteSessionUseCase
import com.example.fyp.domain.history.ObserveSessionNamesUseCase
import com.example.fyp.domain.history.RenameSessionUseCase
import com.example.fyp.model.user.AuthState
import com.example.fyp.model.user.UserSettings
import com.example.fyp.model.TranslationRecord
import com.example.fyp.model.UserCoinStats
import com.example.fyp.model.UserId
import com.example.fyp.model.SessionId
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HistoryUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val records: List<TranslationRecord> = emptyList(),
    val sessionNames: Map<String, String> = emptyMap(),
    val coinStats: UserCoinStats = UserCoinStats(),
    val favoriteIds: Set<String> = emptySet(), // Track which record IDs are favorited (session-only)
    val favoritedTexts: Set<String> = emptySet(), // Track favorited content "sourceText|targetText" (persisted)
    val addingFavoriteId: String? = null, // Track which record is being added/removed
    val historyViewLimit: Int = UserSettings.BASE_HISTORY_LIMIT, // Current history view limit
    val hasMoreRecords: Boolean = false, // Whether there are more records to load
    val isLoadingMore: Boolean = false, // Whether we're currently loading more records
    val totalRecordsCount: Int = 0 // Total count of all records
)

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val authRepo: FirebaseAuthRepository,
    private val sharedHistoryDataSource: SharedHistoryDataSource,
    private val sharedSettings: SharedSettingsDataSource,
    private val observeSessionNames: ObserveSessionNamesUseCase,
    private val deleteHistoryRecord: DeleteHistoryRecordUseCase,
    private val renameSession: RenameSessionUseCase,
    private val deleteSession: DeleteSessionUseCase,
    private val quizRepo: QuizRepository,
    private val favoritesRepo: FirestoreFavoritesRepository,
    private val historyRepo: com.example.fyp.domain.history.HistoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    private var currentUserId: String? = null
    private var historyJob: Job? = null
    private var sessionsJob: Job? = null
    private var settingsJob: Job? = null

    init {
        viewModelScope.launch {
            authRepo.currentUserState.collect { auth ->
                when (auth) {
                    is AuthState.LoggedIn -> {
                        currentUserId = auth.user.uid
                        startListening(auth.user.uid)
                    }

                    AuthState.LoggedOut -> {
                        currentUserId = null
                        historyJob?.cancel()
                        sessionsJob?.cancel()
                        settingsJob?.cancel()
                        sharedHistoryDataSource.stopObserving()
                        _uiState.value = HistoryUiState(
                            isLoading = false,
                            error = "Not logged in",
                            records = emptyList(),
                            sessionNames = emptyMap()
                        )
                    }

                    AuthState.Loading -> {
                        currentUserId = null
                        historyJob?.cancel()
                        sessionsJob?.cancel()
                        settingsJob?.cancel()
                        _uiState.value = HistoryUiState(
                            isLoading = true,
                            error = null,
                            records = emptyList(),
                            sessionNames = emptyMap()
                        )
                    }
                }
            }
        }
    }

    fun deleteRecord(record: TranslationRecord) {
        val uid = record.userId.ifBlank { currentUserId.orEmpty() }
        if (uid.isBlank() || record.id.isBlank()) return

        viewModelScope.launch {
            // Pass language codes so the repository can skip the pre-read (saves 1 read per delete)
            runCatching { deleteHistoryRecord(uid, record.id, record.sourceLang, record.targetLang) }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(error = e.message ?: "Delete failed")
                }
        }
    }

    fun renameSession(sessionId: String, name: String) {
        val uid = currentUserId.orEmpty()
        if (uid.isBlank() || sessionId.isBlank()) return

        viewModelScope.launch {
            runCatching { renameSession(UserId(uid), SessionId(sessionId), name) }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(error = e.message ?: "Rename failed")
                }
        }
    }

    fun deleteSession(sessionId: String) {
        val uid = currentUserId.orEmpty()
        if (uid.isBlank() || sessionId.isBlank()) return

        viewModelScope.launch {
            runCatching { this@HistoryViewModel.deleteSession.invoke(uid, sessionId) }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(error = e.message ?: "Delete session failed")
                }
        }
    }

    /**
     * Load more history records (pagination).
     * Appends older records to the current list.
     */
    fun loadMoreHistory() {
        val uid = currentUserId ?: return
        val currentRecords = _uiState.value.records
        if (currentRecords.isEmpty() || _uiState.value.isLoadingMore) return

        val lastRecord = currentRecords.lastOrNull() ?: return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingMore = true)

            try {
                // Load next page (50 more records)
                val moreRecords = historyRepo.loadMoreHistory(
                    userId = UserId(uid),
                    limit = 50,
                    lastTimestamp = lastRecord.timestamp
                )

                // Append to existing records
                val allRecords = currentRecords + moreRecords
                val hasMore = moreRecords.size >= 50

                _uiState.value = _uiState.value.copy(
                    records = allRecords,
                    hasMoreRecords = hasMore,
                    isLoadingMore = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoadingMore = false,
                    error = "Failed to load more records"
                )
            }
        }
    }

    /**
     * Add a translation record to favorites (or remove if already favorited)
     */
    fun toggleFavorite(record: TranslationRecord) {
        val uid = currentUserId ?: return
        val recordKey = "${record.sourceText}|${record.targetText}"

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(addingFavoriteId = record.id)

            // Check if already favorited
            val existingFavoriteId = favoritesRepo.getFavoriteId(uid, record.sourceText, record.targetText)

            if (existingFavoriteId != null) {
                // Remove from favorites
                favoritesRepo.removeFavorite(uid, existingFavoriteId)
                    .onSuccess {
                        _uiState.value = _uiState.value.copy(
                            addingFavoriteId = null,
                            favoriteIds = _uiState.value.favoriteIds - record.id,
                            favoritedTexts = _uiState.value.favoritedTexts - recordKey
                        )
                    }
                    .onFailure {
                        _uiState.value = _uiState.value.copy(
                            addingFavoriteId = null,
                            error = "Failed to remove from favorites"
                        )
                    }
            } else {
                // Add to favorites
                favoritesRepo.addFavorite(
                    userId = uid,
                    sourceText = record.sourceText,
                    targetText = record.targetText,
                    sourceLang = record.sourceLang,
                    targetLang = record.targetLang
                ).onSuccess {
                    _uiState.value = _uiState.value.copy(
                        addingFavoriteId = null,
                        favoriteIds = _uiState.value.favoriteIds + record.id,
                        favoritedTexts = _uiState.value.favoritedTexts + recordKey
                    )
                }.onFailure {
                    _uiState.value = _uiState.value.copy(
                        addingFavoriteId = null,
                        error = "Failed to add to favorites"
                    )
                }
            }
        }
    }

    /**
     * Legacy method - redirects to toggle
     */
    fun addToFavorites(record: TranslationRecord) = toggleFavorite(record)

    /**
     * Check if a record is already favorited (by text content)
     */
    fun isRecordFavorited(record: TranslationRecord): Boolean {
        val recordKey = "${record.sourceText}|${record.targetText}"
        return _uiState.value.favoritedTexts.contains(recordKey)
    }

    /**
     * Load all favorited texts to check against history records
     */
    private fun loadFavoritedTexts(userId: String) {
        viewModelScope.launch {
            val favorites = favoritesRepo.getAllFavoritesOnce(userId)
            val favoritedTexts = favorites.map { "${it.sourceText}|${it.targetText}" }.toSet()
            _uiState.value = _uiState.value.copy(favoritedTexts = favoritedTexts)
        }
    }

    /**
     * Check if a record is already favorited
     */
    fun checkIfFavorited(record: TranslationRecord, onResult: (Boolean) -> Unit) {
        val recordKey = "${record.sourceText}|${record.targetText}"
        val isFavorited = _uiState.value.favoritedTexts.contains(recordKey)
        if (isFavorited) {
            _uiState.value = _uiState.value.copy(
                favoriteIds = _uiState.value.favoriteIds + record.id
            )
        }
        onResult(isFavorited)
    }

    /**
     * Refresh coin stats on demand (e.g., when screen becomes visible).
     * This replaces the real-time listener to reduce database reads.
     */
    fun refreshCoinStats() {
        val uid = currentUserId ?: return
        viewModelScope.launch {
            try {
                val stats = quizRepo.fetchUserCoinStats(UserId(uid)) ?: UserCoinStats()
                _uiState.value = _uiState.value.copy(coinStats = stats)
            } catch (_: Exception) {
                // Ignore coin fetch errors
            }
        }
    }

    private fun startListening(userId: String) {
        historyJob?.cancel()
        sessionsJob?.cancel()
        settingsJob?.cancel()
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)

        // Observe settings for history limit
        sharedSettings.startObserving(userId)
        settingsJob = viewModelScope.launch {
            sharedSettings.settings.collect { settings ->
                val newLimit = settings.historyViewLimit
                _uiState.value = _uiState.value.copy(historyViewLimit = newLimit)
                // Update shared history data source with new limit
                sharedHistoryDataSource.updateLimit(newLimit.toLong())
            }
        }

        // Use shared history data source (single listener shared across ViewModels)
        val initialLimit = _uiState.value.historyViewLimit.toLong()
        sharedHistoryDataSource.startObserving(userId, initialLimit)

        historyJob = viewModelScope.launch {
            // Observe from shared data source instead of creating new listener
            sharedHistoryDataSource.historyRecords
                .collect { list ->
                    // Check if there might be more records
                    val hasMore = list.size >= initialLimit
                    
                    _uiState.value = _uiState.value.copy(
                        isLoading = sharedHistoryDataSource.isLoading.value,
                        error = sharedHistoryDataSource.error.value,
                        records = list,
                        hasMoreRecords = hasMore
                    )
                }
        }

        sessionsJob = viewModelScope.launch {
            observeSessionNames(userId)
                .catch { /* optional: ignore */ }
                .collect { map ->
                    _uiState.value = _uiState.value.copy(sessionNames = map)
                }
        }

        // Fetch total count for pagination info
        viewModelScope.launch {
            try {
                val totalCount = historyRepo.getHistoryCount(UserId(userId))
                _uiState.value = _uiState.value.copy(totalRecordsCount = totalCount)
            } catch (_: Exception) {
                // Ignore count fetch errors
            }
        }

        // Fetch coin stats once instead of real-time listener
        refreshCoinStats()

        // Load favorited texts to show correct bookmark state
        loadFavoritedTexts(userId)
    }
}