package com.example.fyp.domain.history

import com.example.fyp.data.history.FirestoreHistoryRepository
import com.example.fyp.data.history.SharedHistoryDataSource
import com.example.fyp.model.TranslationRecord
import javax.inject.Inject

/**
 * Use case for saving translation records to user history.
 * Automatically refreshes language count cache to keep UI in sync.
 *
 * @param historyRepo Repository for persisting translation history
 * @param sharedHistoryDataSource Data source for managing language counts
 */
class SaveTranslationUseCase @Inject constructor(
    private val historyRepo: FirestoreHistoryRepository,
    private val sharedHistoryDataSource: SharedHistoryDataSource
) {
    /**
     * Saves a translation record and refreshes language counts.
     * This ensures Learning and WordBank screens update immediately.
     *
     * @param record The translation record to save
     */
    suspend operator fun invoke(record: TranslationRecord) {
        historyRepo.save(record)
        // Force immediate refresh of language counts cache (bypasses debounce)
        // This ensures Learning/WordBank screens update instantly without app restart
        sharedHistoryDataSource.forceRefreshLanguageCounts("")
    }
}