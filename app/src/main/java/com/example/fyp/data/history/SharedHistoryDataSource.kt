package com.example.fyp.data.history

import com.example.fyp.model.TranslationRecord
import com.example.fyp.model.user.UserSettings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Shared data source for history records.
 * This prevents multiple ViewModels from creating separate Firestore listeners
 * for the same data, reducing database reads significantly.
 *
 * Instead of each ViewModel observing Firestore directly:
 * - HistoryViewModel
 * - LearningViewModel
 * - WordBankViewModel
 *
 * All now share this single data source.
 */
@Singleton
class SharedHistoryDataSource @Inject constructor(
    private val historyRepo: FirestoreHistoryRepository
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private var currentUserId: String? = null
    private var currentLimit: Long = UserSettings.BASE_HISTORY_LIMIT.toLong()
    private var historyJob: kotlinx.coroutines.Job? = null

    // Cached history records - shared across all subscribers
    private val _historyRecords = MutableStateFlow<List<TranslationRecord>>(emptyList())
    val historyRecords: StateFlow<List<TranslationRecord>> = _historyRecords.asStateFlow()

    // Loading and error states
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    // Track history count separately (for learning/quiz generation eligibility)
    private val _historyCount = MutableStateFlow(0)
    val historyCount: StateFlow<Int> = _historyCount.asStateFlow()

    // Total language counts (ALL records, not limited) - used for generation eligibility
    private val _languageCounts = MutableStateFlow<Map<String, Int>>(emptyMap())
    val languageCounts: StateFlow<Map<String, Int>> = _languageCounts.asStateFlow()

    /**
     * Start observing history for a user with optional limit.
     * If already observing the same user with the same limit, does nothing (reuses existing listener).
     */
    fun startObserving(userId: String, limit: Long = UserSettings.BASE_HISTORY_LIMIT.toLong()) {
        if (userId == currentUserId && limit == currentLimit && historyJob?.isActive == true) {
            // Already observing this user with same limit, no need to create new listener
            return
        }

        // Cancel previous listener if any
        historyJob?.cancel()
        currentUserId = userId
        currentLimit = limit
        _isLoading.value = true
        _error.value = null

        historyJob = scope.launch {
            historyRepo.getHistory(userId, limit)
                .catch { e ->
                    _isLoading.value = false
                    _error.value = e.message
                    _historyRecords.value = emptyList()
                    _historyCount.value = 0
                }
                .collect { records ->
                    _isLoading.value = false
                    _error.value = null
                    _historyRecords.value = records
                    _historyCount.value = records.size
                }
        }
    }

    /**
     * Refresh total language counts (for generation eligibility).
     * This fetches counts from ALL records, not just the limited display records.
     */
    suspend fun refreshLanguageCounts(primaryLanguageCode: String) {
        val uid = currentUserId ?: return
        try {
            val counts = historyRepo.getLanguageCounts(uid, primaryLanguageCode)
            _languageCounts.value = counts
        } catch (e: Exception) {
            // Keep existing counts on error
        }
    }

    /**
     * Update the limit and restart observing if needed
     */
    fun updateLimit(newLimit: Long) {
        val uid = currentUserId ?: return
        if (newLimit != currentLimit) {
            startObserving(uid, newLimit)
        }
    }

    /**
     * Stop observing and clear data (e.g., on logout).
     */
    fun stopObserving() {
        historyJob?.cancel()
        historyJob = null
        currentUserId = null
        _historyRecords.value = emptyList()
        _historyCount.value = 0
        _isLoading.value = false
        _error.value = null
    }

    /**
     * Get records filtered by language code.
     * Useful for learning/word bank screens that only need records for specific languages.
     */
    fun getRecordsForLanguage(languageCode: String): List<TranslationRecord> {
        return _historyRecords.value.filter {
            it.sourceLang == languageCode || it.targetLang == languageCode
        }
    }

    /**
     * Get count of records for a specific language.
     */
    fun getCountForLanguage(languageCode: String): Int {
        return _historyRecords.value.count {
            it.sourceLang == languageCode || it.targetLang == languageCode
        }
    }
}
