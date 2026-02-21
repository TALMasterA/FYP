package com.example.fyp.data.repositories

import com.example.fyp.data.history.FirestoreHistoryRepository
import com.example.fyp.data.history.SharedHistoryDataSource
import com.example.fyp.model.TranslationRecord
import com.example.fyp.model.UserId
import com.example.fyp.model.LanguageCode
import com.google.firebase.Timestamp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import org.mockito.kotlin.*

@OptIn(ExperimentalCoroutinesApi::class)
class SharedHistoryDataSourceLanguageCountsTest {

    private lateinit var historyRepo: FirestoreHistoryRepository
    private lateinit var dataSource: SharedHistoryDataSource

    private val testDispatcher = UnconfinedTestDispatcher()
    private val userId = UserId("user1")
    private val primaryLang = LanguageCode("en-US")

    // 50 limited display records
    private val limitedRecords = (1..50).map { i ->
        TranslationRecord(
            id = "rec$i",
            userId = userId.value,
            sourceLang = "en-US",
            targetLang = "es-ES",
            timestamp = Timestamp.now()
        )
    }

    // Total counts from Firestore cache (covers ALL 200 records)
    private val allRecordsCounts = mapOf("en-US" to 200, "es-ES" to 200, "fr-FR" to 150)

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        historyRepo = mock {
            on { getHistory(any(), any()) } doReturn flowOf(limitedRecords)
        }
        dataSource = SharedHistoryDataSource(historyRepo)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `languageCounts reflects all records not just limited display set`() = runTest {
        doAnswer { allRecordsCounts }.whenever(historyRepo).getLanguageCounts(any(), any())
        dataSource.startObserving(userId.value)
        dataSource.forceRefreshLanguageCounts(primaryLang.value)

        // languageCounts shows 200 (total), not 50 (display limit)
        assertEquals(200, dataSource.languageCounts.value["en-US"])
        assertEquals(200, dataSource.languageCounts.value["es-ES"])
        assertEquals(150, dataSource.languageCounts.value["fr-FR"])
    }

    @Test
    fun `historyRecords limited while languageCounts covers all records`() = runTest {
        doAnswer { allRecordsCounts }.whenever(historyRepo).getLanguageCounts(any(), any())
        dataSource.startObserving(userId.value)
        dataSource.forceRefreshLanguageCounts(primaryLang.value)

        assertEquals(50, dataSource.historyRecords.value.size)
        assertEquals(200, dataSource.languageCounts.value["en-US"])
    }

    @Test
    fun `getCountForLanguage uses languageCounts from Firestore cache`() = runTest {
        doAnswer { allRecordsCounts }.whenever(historyRepo).getLanguageCounts(any(), any())
        dataSource.startObserving(userId.value)
        dataSource.forceRefreshLanguageCounts(primaryLang.value)

        // Should return 200 from Firestore cache, not 50 from limited display records
        assertEquals(200, dataSource.getCountForLanguage("es-ES"))
    }

    @Test
    fun `languageCounts is empty before refresh`() = runTest {
        assertTrue(dataSource.languageCounts.value.isEmpty())
    }

    @Test
    fun `forceRefreshLanguageCounts bypasses debounce`() = runTest {
        doAnswer { mapOf("en-US" to 100) }.whenever(historyRepo).getLanguageCounts(any(), any())
        dataSource.startObserving(userId.value)
        dataSource.forceRefreshLanguageCounts(primaryLang.value)
        assertEquals(100, dataSource.languageCounts.value["en-US"])

        doAnswer { mapOf("en-US" to 150) }.whenever(historyRepo).getLanguageCounts(any(), any())
        dataSource.forceRefreshLanguageCounts(primaryLang.value)
        assertEquals(150, dataSource.languageCounts.value["en-US"])
    }

    @Test
    fun `getCountForLanguage returns zero for unknown language`() = runTest {
        assertEquals(0, dataSource.getCountForLanguage("unknown-XX"))
    }

    @Test
    fun `historyRecords cleared on stopObserving`() = runTest {
        doAnswer { allRecordsCounts }.whenever(historyRepo).getLanguageCounts(any(), any())
        dataSource.startObserving(userId.value)
        dataSource.stopObserving()
        assertTrue(dataSource.historyRecords.value.isEmpty())
    }
}
