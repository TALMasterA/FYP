package com.example.fyp.domain.history

import com.example.fyp.data.history.FirestoreHistoryRepository
import com.example.fyp.data.history.SharedHistoryDataSource
import com.example.fyp.model.TranslationRecord
import javax.inject.Inject

class SaveTranslationUseCase @Inject constructor(
    private val historyRepo: FirestoreHistoryRepository,
    private val sharedHistoryDataSource: SharedHistoryDataSource
) {
    suspend operator fun invoke(record: TranslationRecord) {
        historyRepo.save(record)
        // Force immediate refresh of language counts cache (bypasses debounce)
        // This ensures Learning/WordBank screens update instantly without app restart
        sharedHistoryDataSource.forceRefreshLanguageCounts("")
    }
}