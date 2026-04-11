package com.example.fyp.screens.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fyp.data.user.FirebaseAuthRepository
import com.example.fyp.data.user.FirestoreFavoritesRepository
import com.example.fyp.data.history.SharedHistoryDataSource
import com.example.fyp.core.AppLogger
import com.example.fyp.core.UiConstants
import com.example.fyp.domain.learning.QuizRepository
import com.example.fyp.data.settings.SharedSettingsDataSource
import com.example.fyp.domain.history.DeleteHistoryRecordUseCase
import com.example.fyp.domain.history.DeleteSessionUseCase
import com.example.fyp.domain.history.ObserveSessionNamesUseCase
import com.example.fyp.domain.history.RenameSessionUseCase
import com.example.fyp.domain.speech.SpeakTextUseCase
import com.example.fyp.model.SpeechResult
import com.example.fyp.model.user.AuthState
import com.example.fyp.model.user.UserSettings
import com.example.fyp.model.TranslationRecord
import com.example.fyp.model.FavoriteSessionRecord
import com.example.fyp.model.UserCoinStats
import com.example.fyp.model.UserId
import com.example.fyp.model.SessionId
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
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
    val favouritingSessionId: String? = null, // Track which session is being favourited
    val favouritedSessionIds: Set<String> = emptySet(), // Track which sessions are fully favourited
    val historyViewLimit: Int = UserSettings.BASE_HISTORY_LIMIT, // Current history view limit
    val hasMoreRecords: Boolean = false, // Whether there are more records to load
    val isLoadingMore: Boolean = false, // Whether we're currently loading more records
    val totalRecordsCount: Int = 0, // Total count of all records
    val isTtsRunning: Boolean = false,
    val ttsStatus: String = "",
    val favoriteLimitExceeded: Boolean = false
)

/**
 * ViewModel for the History screen.
 *
 * Manages paginated translation history, session grouping/renaming,
 * favouriting records and sessions, TTS playback, and coin-stats
 * display. Observes [SharedHistoryDataSource] for real-time Firestore
 * updates and delegates deletion to use-case classes.
 */
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
    private val historyRepo: com.example.fyp.domain.history.HistoryRepository,
    private val speakTextUseCase: SpeakTextUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    private val favoriteMutex = Mutex()
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
                    AppLogger.e("HistoryViewModel", "deleteRecord failed", e)
                    _uiState.value = _uiState.value.copy(error = "Delete failed. Please try again.")
                }
        }
    }

    fun renameSession(sessionId: String, name: String) {
        val uid = currentUserId.orEmpty()
        if (uid.isBlank() || sessionId.isBlank()) return

        viewModelScope.launch {
            runCatching { renameSession(UserId(uid), SessionId(sessionId), name) }
                .onFailure { e ->
                    AppLogger.e("HistoryViewModel", "renameSession failed", e)
                    _uiState.value = _uiState.value.copy(error = "Rename failed. Please try again.")
                }
        }
    }

    fun deleteSession(sessionId: String) {
        val uid = currentUserId.orEmpty()
        if (uid.isBlank() || sessionId.isBlank()) return

        viewModelScope.launch {
            runCatching { this@HistoryViewModel.deleteSession.invoke(uid, sessionId) }
                .onFailure { e ->
                    AppLogger.e("HistoryViewModel", "deleteSession failed", e)
                    _uiState.value = _uiState.value.copy(error = "Delete session failed. Please try again.")
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
                AppLogger.e("HistoryViewModel", "loadMoreHistory failed", e)
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
            favoriteMutex.withLock {
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
                    // Check limit before adding (server count to avoid stale in-memory race)
                    val serverCount = favoritesRepo.getFavoriteCount(uid)
                    if (serverCount >= UserSettings.MAX_FAVORITE_RECORDS) {
                        _uiState.value = _uiState.value.copy(
                            addingFavoriteId = null,
                            favoriteLimitExceeded = true
                        )
                        return@withLock
                    }
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
    }

    /**
     * Favourite an entire live conversation session.
     * Saves the whole session as a FavoriteSession document so it can be
     * viewed later in the Favorites screen with an "Open" button.
     */
    fun favouriteSession(sessionId: String, sessionRecords: List<TranslationRecord>) {
        val uid = currentUserId ?: return
        if (sessionRecords.isEmpty()) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(favouritingSessionId = sessionId)

            val sessionName = _uiState.value.sessionNames[sessionId]
                ?: "Session ${sessionId.take(8)}"

            val sessionRecordModels = sessionRecords.map { rec ->
                FavoriteSessionRecord(
                    sourceText = rec.sourceText,
                    targetText = rec.targetText,
                    sourceLang = rec.sourceLang,
                    targetLang = rec.targetLang,
                    speaker = rec.speaker.orEmpty(),
                    direction = rec.direction.orEmpty(),
                    sequence = rec.sequence?.toInt() ?: 0
                )
            }

            // Check if adding this session's records would exceed the limit
            val currentCount = favoritesRepo.getFavoriteCount(uid)
            if (currentCount + sessionRecords.size > UserSettings.MAX_FAVORITE_RECORDS) {
                _uiState.value = _uiState.value.copy(
                    favouritingSessionId = null,
                    favoriteLimitExceeded = true
                )
                return@launch
            }

            favoritesRepo.addFavoriteSession(
                userId = uid,
                sessionId = sessionId,
                sessionName = sessionName,
                records = sessionRecordModels
            ).onSuccess {
                _uiState.value = _uiState.value.copy(
                    favouritingSessionId = null,
                    favouritedSessionIds = _uiState.value.favouritedSessionIds + sessionId
                )
            }.onFailure {
                _uiState.value = _uiState.value.copy(
                    favouritingSessionId = null,
                    error = "Failed to favourite session"
                )
            }
        }
    }

    /**
     * Unfavourite an entire live conversation session.
     * Removes the FavoriteSession document from Firestore.
     */
    fun unfavouriteSession(sessionId: String, sessionRecords: List<TranslationRecord>) {
        val uid = currentUserId ?: return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(favouritingSessionId = sessionId)

            val favSession = favoritesRepo.findFavoriteSession(uid, sessionId)
            if (favSession != null) {
                favoritesRepo.removeFavoriteSession(uid, favSession.id)
                    .onSuccess {
                        _uiState.value = _uiState.value.copy(
                            favouritingSessionId = null,
                            favouritedSessionIds = _uiState.value.favouritedSessionIds - sessionId
                        )
                    }
                    .onFailure {
                        _uiState.value = _uiState.value.copy(
                            favouritingSessionId = null,
                            error = "Failed to unfavourite session"
                        )
                    }
            } else {
                _uiState.value = _uiState.value.copy(favouritingSessionId = null)
            }
        }
    }

    /**
     * Check if a session is favourited (stored as a FavoriteSession).
     */
    fun isSessionFavourited(sessionRecords: List<TranslationRecord>): Boolean {
        if (sessionRecords.isEmpty()) return false
        val sid = sessionRecords.firstOrNull()?.sessionId ?: return false
        return _uiState.value.favouritedSessionIds.contains(sid)
    }

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
     * Load IDs of sessions that have been favourited, so the heart icon
     * shows the correct state when the user opens the History screen.
     */
    private fun loadFavouritedSessionIds(userId: String) {
        viewModelScope.launch {
            val sessions = favoritesRepo.getAllFavoriteSessionsOnce(userId)
            val sessionIds = sessions.map { it.sessionId }.toSet()
            _uiState.value = _uiState.value.copy(favouritedSessionIds = sessionIds)
        }
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
        // Note: settingsJob above will quickly update the limit to the real value from Firestore;
        // we start observing with the default so data appears immediately.
        sharedHistoryDataSource.startObserving(userId)

        historyJob = viewModelScope.launch {
            // Combine records, isLoading, and error into a single reactive stream
            // so the UI updates whenever ANY of these change (not just records).
            combine(
                sharedHistoryDataSource.historyRecords,
                sharedHistoryDataSource.isLoading,
                sharedHistoryDataSource.error
            ) { list, loading, error ->
                Triple(list, loading, error)
            }.collect { (list, loading, error) ->
                // Use current limit from uiState (not a stale captured value)
                val currentLimit = _uiState.value.historyViewLimit.toLong()
                val hasMore = list.size >= currentLimit

                _uiState.value = _uiState.value.copy(
                    isLoading = loading,
                    error = error,
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

        // No longer fetching total count since we only show recent records (no Load More)

        // Fetch coin stats once instead of real-time listener
        refreshCoinStats()

        // Load favorited texts to show correct bookmark state
        loadFavoritedTexts(userId)

        // Load favourited session IDs so heart icons show correctly on startup
        loadFavouritedSessionIds(userId)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun clearFavoriteLimitExceeded() {
        _uiState.value = _uiState.value.copy(favoriteLimitExceeded = false)
    }

    // --- TTS playback (previously in SpeechViewModel, moved here per architecture review) ---

    fun speakText(languageCode: String, text: String) {
        val voiceName = sharedSettings.settings.value.voiceSettings[languageCode]
        speak(text = text, languageCode = languageCode, isTranslation = true, voiceName = voiceName)
    }

    fun speakTextOriginal(languageCode: String, text: String) {
        val voiceName = sharedSettings.settings.value.voiceSettings[languageCode]
        speak(text = text, languageCode = languageCode, isTranslation = false, voiceName = voiceName)
    }

    private fun speak(text: String, languageCode: String, isTranslation: Boolean, voiceName: String?) {
        if (text.isBlank() || _uiState.value.isTtsRunning) return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isTtsRunning = true,
                ttsStatus = if (isTranslation) "Speaking translation..." else "Speaking..."
            )
            try {
                when (val result = speakTextUseCase(text, languageCode, voiceName)) {
                    is SpeechResult.Success -> {
                        _uiState.value = _uiState.value.copy(ttsStatus = "✓ Spoken")
                        delay(UiConstants.TTS_START_DELAY_MS)
                    }
                    is SpeechResult.Error -> {
                        _uiState.value = _uiState.value.copy(ttsStatus = "✗ ${result.message}")
                        delay(UiConstants.TTS_ERROR_WAIT_MS)
                    }
                }
            } finally {
                _uiState.value = _uiState.value.copy(isTtsRunning = false, ttsStatus = "")
            }
        }
    }

    fun retryLoad() {
        val uid = currentUserId ?: return
        startListening(uid)
    }

    override fun onCleared() {
        super.onCleared()
        historyJob?.cancel()
        sessionsJob?.cancel()
        settingsJob?.cancel()
    }
}