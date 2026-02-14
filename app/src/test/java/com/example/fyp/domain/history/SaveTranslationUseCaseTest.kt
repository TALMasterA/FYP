package com.example.fyp.domain.history

import com.example.fyp.data.history.FirestoreHistoryRepository
import com.example.fyp.data.history.SharedHistoryDataSource
import com.example.fyp.model.TranslationRecord
import com.google.firebase.Timestamp
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import java.util.Date

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
            timestamp = Timestamp(Date())
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
                timestamp = Timestamp(Date())
            ),
            TranslationRecord(
                id = "2",
                userId = "user2",
                sourceText = "Goodbye",
                targetText = "さようなら",
                sourceLang = "en-US",
                targetLang = "ja-JP",
                timestamp = Timestamp(Date())
            )
        )

        // Act & Assert
        records.forEach { record ->
            useCase(record)
            verify(historyRepo).save(record)
        }
    }
}
