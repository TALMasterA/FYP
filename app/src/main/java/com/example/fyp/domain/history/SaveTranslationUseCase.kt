package com.example.fyp.domain.history

import com.example.fyp.data.history.FirestoreHistoryRepository
import com.example.fyp.model.TranslationRecord
import javax.inject.Inject

class SaveTranslationUseCase @Inject constructor(
    private val historyRepo: FirestoreHistoryRepository
) {
    suspend operator fun invoke(record: TranslationRecord) {
        historyRepo.save(record)
    }
}