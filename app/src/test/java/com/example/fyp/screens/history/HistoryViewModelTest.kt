package com.example.fyp.screens.history

import com.example.fyp.data.history.SharedHistoryDataSource
import com.example.fyp.data.settings.SharedSettingsDataSource
import com.example.fyp.data.user.FirebaseAuthRepository
import com.example.fyp.data.user.FirestoreFavoritesRepository
import com.example.fyp.domain.history.DeleteHistoryRecordUseCase
import com.example.fyp.domain.history.DeleteSessionUseCase
import com.example.fyp.domain.history.HistoryRepository
import com.example.fyp.domain.history.ObserveSessionNamesUseCase
import com.example.fyp.domain.history.RenameSessionUseCase
import com.example.fyp.domain.learning.QuizRepository
import com.example.fyp.domain.speech.SpeakTextUseCase
import com.example.fyp.model.FavoriteRecord
import com.example.fyp.model.FavoriteSession
import com.example.fyp.model.TranslationRecord
import com.example.fyp.model.UserCoinStats
import com.example.fyp.model.UserId
import com.example.fyp.model.user.AuthState
import com.example.fyp.model.user.User
import com.example.fyp.model.user.UserSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.*

/**
 * Unit tests for HistoryViewModel - session favourite, delete, rename, error handling.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class HistoryViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val authStateFlow = MutableStateFlow<AuthState>(AuthState.Loading)
    private val settingsFlow = MutableStateFlow(UserSettings())
    private val historyRecordsFlow = MutableStateFlow<List<TranslationRecord>>(emptyList())
    private val isLoadingFlow = MutableStateFlow(false)
    private val errorFlow = MutableStateFlow<String?>(null)
    private val testUserId = "testUser123"
    private val testUser = User(uid = testUserId, email = "test@test.com")

    // Mocks
    private lateinit var authRepo: FirebaseAuthRepository
    private lateinit var sharedHistoryDataSource: SharedHistoryDataSource
    private lateinit var sharedSettings: SharedSettingsDataSource
    private lateinit var observeSessionNames: ObserveSessionNamesUseCase
    private lateinit var deleteHistoryRecord: DeleteHistoryRecordUseCase
    private lateinit var renameSessionUseCase: RenameSessionUseCase
    private lateinit var deleteSessionUseCase: DeleteSessionUseCase
    private lateinit var quizRepo: QuizRepository
    private lateinit var favoritesRepo: FirestoreFavoritesRepository
    private lateinit var historyRepo: HistoryRepository
    private lateinit var speakTextUseCase: SpeakTextUseCase

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        authRepo = mock { on { currentUserState } doReturn authStateFlow }

        sharedHistoryDataSource = mock {
            on { historyRecords } doReturn historyRecordsFlow
            on { isLoading } doReturn isLoadingFlow
            on { error } doReturn errorFlow
        }

        sharedSettings = mock { on { settings } doReturn settingsFlow }

        observeSessionNames = mock()
        whenever(observeSessionNames.invoke(testUserId)).thenReturn(flowOf(emptyMap()))

        deleteHistoryRecord = mock()
        renameSessionUseCase = mock()
        deleteSessionUseCase = mock()
        speakTextUseCase = mock()

        quizRepo = mock {
            onBlocking { fetchUserCoinStats(UserId(testUserId)) } doReturn UserCoinStats()
        }

        favoritesRepo = mock {
            onBlocking { getAllFavoritesOnce(testUserId) } doReturn emptyList()
            onBlocking { getAllFavoriteSessionsOnce(testUserId) } doReturn emptyList()
        }

        historyRepo = mock()
    }

    private fun buildViewModel(): HistoryViewModel = HistoryViewModel(
        authRepo = authRepo,
        sharedHistoryDataSource = sharedHistoryDataSource,
        sharedSettings = sharedSettings,
        observeSessionNames = observeSessionNames,
        deleteHistoryRecord = deleteHistoryRecord,
        renameSession = renameSessionUseCase,
        deleteSession = deleteSessionUseCase,
        quizRepo = quizRepo,
        favoritesRepo = favoritesRepo,
        historyRepo = historyRepo,
        speakTextUseCase = speakTextUseCase
    )

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // ── Auth lifecycle ──

    @Test
    fun `initial loading state`() {
        val vm = buildViewModel()
        assertEquals(true, vm.uiState.value.isLoading)
    }

    @Test
    fun `logged out shows not logged in error`() = runTest {
        val vm = buildViewModel()
        authStateFlow.value = AuthState.LoggedOut
        assertEquals("Not logged in", vm.uiState.value.error)
        assertTrue(vm.uiState.value.records.isEmpty())
    }

    @Test
    fun `login starts observing history`() = runTest {
        val vm = buildViewModel()
        authStateFlow.value = AuthState.LoggedIn(testUser)
        verify(sharedHistoryDataSource).startObserving(eq(testUserId), any())
    }

    @Test
    fun `login loads favourited session ids`() = runTest {
        favoritesRepo.stub {
            onBlocking { getAllFavoriteSessionsOnce(testUserId) } doReturn listOf(
                FavoriteSession(id = "f1", sessionId = "s1")
            )
        }
        val vm = buildViewModel()
        authStateFlow.value = AuthState.LoggedIn(testUser)

        assertTrue(vm.uiState.value.favouritedSessionIds.contains("s1"))
    }

    @Test
    fun `login loads favorited texts`() = runTest {
        favoritesRepo.stub {
            onBlocking { getAllFavoritesOnce(testUserId) } doReturn listOf(
                FavoriteRecord(id = "f1", sourceText = "Hi", targetText = "Hola")
            )
        }
        val vm = buildViewModel()
        authStateFlow.value = AuthState.LoggedIn(testUser)

        assertTrue(vm.uiState.value.favoritedTexts.contains("Hi|Hola"))
    }

    // ── Session favourite ──

    @Test
    fun `favouriteSession stores session and updates state`() = runTest {
        favoritesRepo.stub {
            onBlocking { addFavoriteSession(any(), any(), any(), any()) } doReturn
                Result.success(FavoriteSession(id = "fav1", sessionId = "sess1"))
        }

        val vm = buildViewModel()
        authStateFlow.value = AuthState.LoggedIn(testUser)

        val records = listOf(
            TranslationRecord(id = "r1", sourceText = "Hello", targetText = "Hola",
                speaker = "A", direction = "A_to_B", sequence = 1)
        )
        vm.favouriteSession("sess1", records)

        assertTrue(vm.uiState.value.favouritedSessionIds.contains("sess1"))
        assertNull(vm.uiState.value.favouritingSessionId)
    }

    @Test
    fun `favouriteSession with empty records is no-op`() = runTest {
        val vm = buildViewModel()
        authStateFlow.value = AuthState.LoggedIn(testUser)

        vm.favouriteSession("sess1", emptyList())

        verify(favoritesRepo, never()).addFavoriteSession(any(), any(), any(), any())
    }

    @Test
    fun `favouriteSession failure sets error`() = runTest {
        favoritesRepo.stub {
            onBlocking { addFavoriteSession(any(), any(), any(), any()) } doReturn
                Result.failure(RuntimeException("Network error"))
        }

        val vm = buildViewModel()
        authStateFlow.value = AuthState.LoggedIn(testUser)

        vm.favouriteSession("sess1", listOf(TranslationRecord(id = "r1")))

        assertEquals("Failed to favourite session", vm.uiState.value.error)
        assertFalse(vm.uiState.value.favouritedSessionIds.contains("sess1"))
    }

    @Test
    fun `unfavouriteSession removes session`() = runTest {
        // Pre-populate as favourited
        favoritesRepo.stub {
            onBlocking { getAllFavoriteSessionsOnce(testUserId) } doReturn listOf(
                FavoriteSession(id = "fav1", sessionId = "sess1")
            )
            onBlocking { findFavoriteSession(testUserId, "sess1") } doReturn
                FavoriteSession(id = "fav1", sessionId = "sess1")
            onBlocking { removeFavoriteSession(testUserId, "fav1") } doReturn Result.success(Unit)
        }

        val vm = buildViewModel()
        authStateFlow.value = AuthState.LoggedIn(testUser)
        assertTrue(vm.uiState.value.favouritedSessionIds.contains("sess1"))

        vm.unfavouriteSession("sess1", listOf(TranslationRecord(id = "r1", sessionId = "sess1")))

        assertFalse(vm.uiState.value.favouritedSessionIds.contains("sess1"))
    }

    @Test
    fun `unfavouriteSession when not found is no-op`() = runTest {
        favoritesRepo.stub {
            onBlocking { findFavoriteSession(testUserId, "sess1") } doReturn null
        }

        val vm = buildViewModel()
        authStateFlow.value = AuthState.LoggedIn(testUser)

        vm.unfavouriteSession("sess1", listOf(TranslationRecord(id = "r1")))

        verify(favoritesRepo, never()).removeFavoriteSession(any(), any())
    }

    @Test
    fun `isSessionFavourited returns correct state`() = runTest {
        favoritesRepo.stub {
            onBlocking { getAllFavoriteSessionsOnce(testUserId) } doReturn listOf(
                FavoriteSession(id = "f1", sessionId = "sess1")
            )
        }

        val vm = buildViewModel()
        authStateFlow.value = AuthState.LoggedIn(testUser)

        assertTrue(vm.isSessionFavourited(listOf(TranslationRecord(sessionId = "sess1"))))
        assertFalse(vm.isSessionFavourited(listOf(TranslationRecord(sessionId = "sess999"))))
        assertFalse(vm.isSessionFavourited(emptyList()))
    }

    // ── Record favourite ──

    @Test
    fun `isRecordFavorited checks text key`() = runTest {
        favoritesRepo.stub {
            onBlocking { getAllFavoritesOnce(testUserId) } doReturn listOf(
                FavoriteRecord(sourceText = "Hi", targetText = "Hola")
            )
        }

        val vm = buildViewModel()
        authStateFlow.value = AuthState.LoggedIn(testUser)

        assertTrue(vm.isRecordFavorited(TranslationRecord(sourceText = "Hi", targetText = "Hola")))
        assertFalse(vm.isRecordFavorited(TranslationRecord(sourceText = "Bye", targetText = "Adiós")))
    }

    @Test
    fun `toggleFavorite adds when not favorited`() = runTest {
        favoritesRepo.stub {
            onBlocking { getFavoriteId(testUserId, "Hello", "Hola") } doReturn null
            onBlocking {
                addFavorite(
                    userId = testUserId,
                    sourceText = "Hello",
                    targetText = "Hola",
                    sourceLang = "en",
                    targetLang = "es"
                )
            } doReturn Result.success(FavoriteRecord(id = "f1"))
        }

        val vm = buildViewModel()
        authStateFlow.value = AuthState.LoggedIn(testUser)

        vm.toggleFavorite(TranslationRecord(
            id = "r1", sourceText = "Hello", targetText = "Hola",
            sourceLang = "en", targetLang = "es"
        ))

        verify(favoritesRepo).addFavorite(
            userId = testUserId,
            sourceText = "Hello",
            targetText = "Hola",
            sourceLang = "en",
            targetLang = "es"
        )
    }

    @Test
    fun `toggleFavorite removes when already favorited`() = runTest {
        favoritesRepo.stub {
            onBlocking { getFavoriteId(testUserId, "Hello", "Hola") } doReturn "existFav"
            onBlocking { removeFavorite(testUserId, "existFav") } doReturn Result.success(Unit)
        }

        val vm = buildViewModel()
        authStateFlow.value = AuthState.LoggedIn(testUser)

        vm.toggleFavorite(TranslationRecord(
            id = "r1", sourceText = "Hello", targetText = "Hola"
        ))

        verify(favoritesRepo).removeFavorite(testUserId, "existFav")
    }

    // ── Delete / Rename ──

    @Test
    fun `deleteRecord calls use case`() = runTest {
        val vm = buildViewModel()
        authStateFlow.value = AuthState.LoggedIn(testUser)

        vm.deleteRecord(TranslationRecord(
            id = "rec1", userId = testUserId,
            sourceLang = "en", targetLang = "es"
        ))

        verify(deleteHistoryRecord).invoke(testUserId, "rec1", "en", "es")
    }

    @Test
    fun `deleteRecord with blank id is no-op`() = runTest {
        val vm = buildViewModel()
        authStateFlow.value = AuthState.LoggedIn(testUser)

        vm.deleteRecord(TranslationRecord(id = "", userId = testUserId))

        verifyNoInteractions(deleteHistoryRecord)
    }

    @Test
    fun `deleteSession calls use case`() = runTest {
        val vm = buildViewModel()
        authStateFlow.value = AuthState.LoggedIn(testUser)

        vm.deleteSession("sess1")

        verify(deleteSessionUseCase).invoke(testUserId, "sess1")
    }

    @Test
    fun `renameSession calls use case`() = runTest {
        val vm = buildViewModel()
        authStateFlow.value = AuthState.LoggedIn(testUser)

        vm.renameSession("sess1", "My Session")

        verify(renameSessionUseCase).invoke(
            UserId(testUserId),
            com.example.fyp.model.SessionId("sess1"),
            "My Session"
        )
    }

    // ── Error handling ──

    @Test
    fun `clearError removes error`() = runTest {
        val vm = buildViewModel()
        authStateFlow.value = AuthState.LoggedOut
        assertNotNull(vm.uiState.value.error)

        vm.clearError()
        assertNull(vm.uiState.value.error)
    }

    @Test
    fun `deleteRecord failure sets error`() = runTest {
        deleteHistoryRecord.stub {
            onBlocking { invoke(any(), any(), anyOrNull(), anyOrNull()) } doThrow RuntimeException("err")
        }

        val vm = buildViewModel()
        authStateFlow.value = AuthState.LoggedIn(testUser)

        vm.deleteRecord(TranslationRecord(
            id = "rec1", userId = testUserId,
            sourceLang = "en", targetLang = "es"
        ))

        assertEquals("Delete failed. Please try again.", vm.uiState.value.error)
    }

    // ── Retry ──

    @Test
    fun `retryLoad restarts listening`() = runTest {
        val vm = buildViewModel()
        authStateFlow.value = AuthState.LoggedIn(testUser)

        vm.retryLoad()

        // Called twice: once on init (AuthState.LoggedIn), once on retryLoad
        verify(sharedHistoryDataSource, times(2)).startObserving(eq(testUserId), any())
    }
}
