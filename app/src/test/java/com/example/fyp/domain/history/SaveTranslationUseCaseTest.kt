package com.example.fyp.domain.history

import com.example.fyp.data.history.FirestoreHistoryRepository
import com.example.fyp.data.history.SharedHistoryDataSource
import com.example.fyp.model.TranslationRecord
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions

class SaveTranslationUseCaseTest {

    private lateinit var historyRepo: FirestoreHistoryRepository
    private lateinit var sharedHistoryDataSource: SharedHistoryDataSource
    private lateinit var useCase: SaveTranslationUseCase

    @Before
    fun setup() {
        historyRepo = mock()
        sharedHistoryDataSource = mock()
        useCase = SaveTranslationUseCase(historyRepo, sharedHistoryDataSource)
    }

    @Test
    fun `invoke saves record and refreshes language counts`() = runTest {
        // Arrange
        val record = TranslationRecord(
            id = "1",
            userId = "user1",
            sourceText = "Hello",
            targetText = "你好",
            sourceLang = "en-US",
            targetLang = "zh-CN",
            timestamp = System.currentTimeMillis()
        )

        // Act
        useCase(record)

        // Assert
        verify(historyRepo).save(record)
        verify(sharedHistoryDataSource).forceRefreshLanguageCounts("")
        verifyNoMoreInteractions(historyRepo, sharedHistoryDataSource)
    }

    @Test
    fun `invoke handles different translation records`() = runTest {
        // Arrange
        val records = listOf(
            TranslationRecord(
                id = "1",
                userId = "user1",
                sourceText = "Hello",
                targetText = "Hola",
                sourceLang = "en-US",
                targetLang = "es-ES",
                timestamp = System.currentTimeMillis()
            ),
            TranslationRecord(
                id = "2",
                userId = "user2",
                sourceText = "Goodbye",
                targetText = "さようなら",
                sourceLang = "en-US",
                targetLang = "ja-JP",
                timestamp = System.currentTimeMillis()
            )
        )

        // Act & Assert
        records.forEach { record ->
            useCase(record)
            verify(historyRepo).save(record)
        }
    }
}
