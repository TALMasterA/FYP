package com.example.fyp.screens.history

import com.example.fyp.model.TranslationRecord
import com.example.fyp.model.user.UserSettings
import com.google.firebase.Timestamp
import org.junit.Test
import org.junit.Assert.*

/**
 * Unit tests for HistoryUiState data class.
 * Tests state management logic used by HistoryViewModel.
 */
class HistoryUiStateTest {

    private fun makeRecord(id: String, mode: String = "discrete", sourceLang: String = "en-US", targetLang: String = "zh-HK") =
        TranslationRecord(
            id = id,
            userId = "user1",
            sourceText = "Hello",
            targetText = "你好",
            sourceLang = sourceLang,
            targetLang = targetLang,
            mode = mode,
            timestamp = Timestamp.now()
        )

    // --- Initial state ---

    @Test
    fun `initial state is loading with empty records`() {
        val state = HistoryUiState()
        assertTrue(state.isLoading)
        assertTrue(state.records.isEmpty())
        assertNull(state.error)
        assertFalse(state.hasMoreRecords)
        assertEquals(UserSettings.BASE_HISTORY_LIMIT, state.historyViewLimit)
    }

    // --- History limit ---

    @Test
    fun `default history view limit is BASE_HISTORY_LIMIT`() {
        val state = HistoryUiState()
        assertEquals(UserSettings.BASE_HISTORY_LIMIT, state.historyViewLimit)
    }

    @Test
    fun `history limit can be updated`() {
        val state = HistoryUiState(historyViewLimit = 100)
        assertEquals(100, state.historyViewLimit)
    }

    // --- Filtering ---

    @Test
    fun `records can be filtered by mode`() {
        val records = listOf(
            makeRecord("1", mode = "discrete"),
            makeRecord("2", mode = "continuous"),
            makeRecord("3", mode = "discrete"),
            makeRecord("4", mode = "continuous")
        )
        val state = HistoryUiState(records = records, isLoading = false)

        val discrete = state.records.filter { it.mode == "discrete" }
        val continuous = state.records.filter { it.mode == "continuous" }

        assertEquals(2, discrete.size)
        assertEquals(2, continuous.size)
    }

    @Test
    fun `records can be filtered by language`() {
        val records = listOf(
            makeRecord("1", sourceLang = "en-US", targetLang = "zh-HK"),
            makeRecord("2", sourceLang = "en-US", targetLang = "ja-JP"),
            makeRecord("3", sourceLang = "zh-HK", targetLang = "en-US")
        )
        val state = HistoryUiState(records = records, isLoading = false)

        val withZhHK = state.records.filter { it.sourceLang == "zh-HK" || it.targetLang == "zh-HK" }
        assertEquals(2, withZhHK.size)
    }

    @Test
    fun `records can be filtered by keyword`() {
        val records = listOf(
            TranslationRecord(id = "1", sourceText = "Hello world", targetText = "你好世界", sourceLang = "en-US", targetLang = "zh-HK"),
            TranslationRecord(id = "2", sourceText = "Good morning", targetText = "早安", sourceLang = "en-US", targetLang = "zh-HK"),
            TranslationRecord(id = "3", sourceText = "Hello there", targetText = "你好啊", sourceLang = "en-US", targetLang = "zh-HK")
        )
        val state = HistoryUiState(records = records, isLoading = false)

        val keyword = "Hello"
        val filtered = state.records.filter {
            it.sourceText.contains(keyword, ignoreCase = true) ||
            it.targetText.contains(keyword, ignoreCase = true)
        }
        assertEquals(2, filtered.size)
    }

    // --- Favorites ---

    @Test
    fun `favorited texts are tracked by content key`() {
        val state = HistoryUiState(
            favoritedTexts = setOf("Hello|你好", "World|世界")
        )
        assertTrue(state.favoritedTexts.contains("Hello|你好"))
        assertFalse(state.favoritedTexts.contains("Goodbye|再見"))
    }

    @Test
    fun `favoriteIds tracks which record IDs are favorited`() {
        val state = HistoryUiState(
            favoriteIds = setOf("rec1", "rec3")
        )
        assertTrue(state.favoriteIds.contains("rec1"))
        assertFalse(state.favoriteIds.contains("rec2"))
        assertTrue(state.favoriteIds.contains("rec3"))
    }

    // --- Error handling ---

    @Test
    fun `error state preserves existing records`() {
        val records = listOf(makeRecord("1"))
        val state = HistoryUiState(
            records = records,
            error = "Network error",
            isLoading = false
        )
        assertEquals(1, state.records.size)
        assertEquals("Network error", state.error)
    }

    @Test
    fun `loading state is false when records are available`() {
        val state = HistoryUiState(
            records = listOf(makeRecord("1")),
            isLoading = false
        )
        assertFalse(state.isLoading)
    }

    // --- Coin stats ---

    @Test
    fun `coin stats default to zero`() {
        val state = HistoryUiState()
        assertEquals(0, state.coinStats.coinTotal)
    }

    // --- Session names ---

    @Test
    fun `session names map is empty by default`() {
        val state = HistoryUiState()
        assertTrue(state.sessionNames.isEmpty())
    }

    @Test
    fun `session names can be populated`() {
        val state = HistoryUiState(
            sessionNames = mapOf("session1" to "Travel", "session2" to "Restaurant")
        )
        assertEquals("Travel", state.sessionNames["session1"])
        assertEquals("Restaurant", state.sessionNames["session2"])
    }

    // --- Session Favourites ---

    @Test
    fun `favouritedSessionIds tracks which sessions are favourited`() {
        val state = HistoryUiState(
            favouritedSessionIds = setOf("session1", "session3")
        )
        assertTrue(state.favouritedSessionIds.contains("session1"))
        assertFalse(state.favouritedSessionIds.contains("session2"))
        assertTrue(state.favouritedSessionIds.contains("session3"))
    }

    @Test
    fun `favouritingSessionId tracks in-progress favouriting`() {
        val state = HistoryUiState(favouritingSessionId = "session1")
        assertEquals("session1", state.favouritingSessionId)
    }

    @Test
    fun `favouritingSessionId is null by default`() {
        val state = HistoryUiState()
        assertNull(state.favouritingSessionId)
    }

    @Test
    fun `favouritedSessionIds is empty by default`() {
        val state = HistoryUiState()
        assertTrue(state.favouritedSessionIds.isEmpty())
    }

    @Test
    fun `session favourite can be added`() {
        val state = HistoryUiState(
            favouritedSessionIds = setOf("session1")
        )
        val updated = state.copy(
            favouritedSessionIds = state.favouritedSessionIds + "session2"
        )
        assertEquals(2, updated.favouritedSessionIds.size)
        assertTrue(updated.favouritedSessionIds.contains("session2"))
    }

    @Test
    fun `session favourite can be removed`() {
        val state = HistoryUiState(
            favouritedSessionIds = setOf("session1", "session2")
        )
        val updated = state.copy(
            favouritedSessionIds = state.favouritedSessionIds - "session1"
        )
        assertEquals(1, updated.favouritedSessionIds.size)
        assertFalse(updated.favouritedSessionIds.contains("session1"))
    }
}
