package com.example.fyp.screens.favorites

import com.example.fyp.data.settings.SharedSettingsDataSource
import com.example.fyp.data.user.FirebaseAuthRepository
import com.example.fyp.data.user.FirestoreFavoritesRepository
import com.example.fyp.domain.speech.SpeakTextUseCase
import com.example.fyp.model.FavoriteRecord
import com.example.fyp.model.FavoriteSession
import com.example.fyp.model.user.AuthState
import com.example.fyp.model.user.User
import com.example.fyp.model.user.UserSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import org.mockito.kotlin.*

/**
 * Unit tests for FavoritesViewModel.
 *
 * Tests:
 * 1. Initial state and loading favorites on auth
 * 2. Refresh functionality with isRefreshing state
 * 3. Load more favorites (lazy loading)
 * 4. Remove favorite updates state correctly
 * 5. Error handling with auto-clear
 */
@OptIn(ExperimentalCoroutinesApi::class)
class FavoritesViewModelTest {

    private lateinit var authRepo: FirebaseAuthRepository
    private lateinit var favoritesRepo: FirestoreFavoritesRepository
    private lateinit var speakTextUseCase: SpeakTextUseCase
    private lateinit var sharedSettings: SharedSettingsDataSource
    private lateinit var viewModel: FavoritesViewModel

    private val testDispatcher = UnconfinedTestDispatcher()
    private val authStateFlow = MutableStateFlow<AuthState>(AuthState.Loading)
    private val settingsFlow = MutableStateFlow(UserSettings())

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        authRepo = mock {
            on { currentUserState } doReturn authStateFlow
        }

        favoritesRepo = mock {
            onBlocking { getAllFavoriteSessionsOnce(any()) } doReturn emptyList()
        }
        speakTextUseCase = mock()

        sharedSettings = mock {
            on { settings } doReturn settingsFlow
        }

        viewModel = FavoritesViewModel(
            authRepo = authRepo,
            favoritesRepo = favoritesRepo,
            speakTextUseCase = speakTextUseCase,
            sharedSettings = sharedSettings
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is loading`() {
        assertEquals(true, viewModel.uiState.value.isLoading)
        assertEquals(emptyList<FavoriteRecord>(), viewModel.uiState.value.favorites)
    }

    @Test
    fun `loading favorites on user login`() = runTest {
        val userId = "user123"
        val testFavorites = listOf(
            FavoriteRecord(
                id = "fav1",
                userId = userId,
                sourceText = "Hello",
                targetText = "Hola",
                sourceLang = "en-US",
                targetLang = "es-ES"
            ),
            FavoriteRecord(
                id = "fav2",
                userId = userId,
                sourceText = "Goodbye",
                targetText = "Adiós",
                sourceLang = "en-US",
                targetLang = "es-ES"
            )
        )

        favoritesRepo.stub {
            onBlocking { getAllFavoritesOnce(userId) } doReturn testFavorites
        }

        // Simulate user login
        authStateFlow.value = AuthState.LoggedIn(User(uid = userId, email = "test@test.com"))

        // Verify favorites are loaded
        assertEquals(false, viewModel.uiState.value.isLoading)
        assertEquals(2, viewModel.uiState.value.favorites.size)
        assertEquals("fav1", viewModel.uiState.value.favorites[0].id)
    }

    @Test
    fun `refresh sets isRefreshing state correctly`() = runTest {
        val userId = "user123"
        val testFavorites = listOf(
            FavoriteRecord(id = "fav1", userId = userId, sourceText = "Test", targetText = "Prueba")
        )

        favoritesRepo.stub {
            onBlocking { getAllFavoritesOnce(userId) } doReturn testFavorites
        }

        authStateFlow.value = AuthState.LoggedIn(User(uid = userId, email = "test@test.com"))

        // Call refresh
        viewModel.refresh()

        // Verify isRefreshing is eventually false (after async work completes)
        assertEquals(false, viewModel.uiState.value.isRefreshing)
        verify(favoritesRepo, times(2)).getAllFavoritesOnce(userId) // Once on login, once on refresh
    }

    @Test
    fun `loadMoreFavorites increases visible count`() = runTest {
        val userId = "user123"
        val allFavorites = (1..50).map { i ->
            FavoriteRecord(
                id = "fav$i",
                userId = userId,
                sourceText = "Source $i",
                targetText = "Target $i"
            )
        }

        favoritesRepo.stub {
            onBlocking { getAllFavoritesOnce(userId) } doReturn allFavorites
        }

        authStateFlow.value = AuthState.LoggedIn(User(uid = userId, email = "test@test.com"))

        // Initial state shows 20 items
        assertEquals(20, viewModel.uiState.value.favorites.size)
        assertEquals(true, viewModel.uiState.value.hasMore)

        // Load more
        viewModel.loadMoreFavorites()

        // Now shows 40 items
        assertEquals(40, viewModel.uiState.value.favorites.size)
        assertEquals(true, viewModel.uiState.value.hasMore)
    }

    @Test
    fun `removeFavorite updates state immediately`() = runTest {
        val userId = "user123"
        val testFavorites = listOf(
            FavoriteRecord(id = "fav1", userId = userId, sourceText = "Keep", targetText = "Mantener"),
            FavoriteRecord(id = "fav2", userId = userId, sourceText = "Delete", targetText = "Eliminar")
        )

        favoritesRepo.stub {
            onBlocking { getAllFavoritesOnce(userId) } doReturn testFavorites
            onBlocking { removeFavorite(userId, "fav2") } doReturn Result.success(Unit)
        }

        authStateFlow.value = AuthState.LoggedIn(User(uid = userId, email = "test@test.com"))

        assertEquals(2, viewModel.uiState.value.favorites.size)

        // Remove favorite
        viewModel.removeFavorite("fav2")

        // Verify state updated immediately
        assertEquals(1, viewModel.uiState.value.favorites.size)
        assertEquals("fav1", viewModel.uiState.value.favorites[0].id)
        verify(favoritesRepo).removeFavorite(userId, "fav2")
    }

    @Test
    fun `error handling displays error message`() = runTest {
        val userId = "user123"
        val errorMessage = "Network error"

        favoritesRepo.stub {
            onBlocking { getAllFavoritesOnce(userId) } doThrow RuntimeException(errorMessage)
        }

        authStateFlow.value = AuthState.LoggedIn(User(uid = userId, email = "test@test.com"))

        // Verify error is displayed
        assertEquals(false, viewModel.uiState.value.isLoading)
        assertTrue(viewModel.uiState.value.error?.contains(errorMessage) == true)
    }

    @Test
    fun `clearError removes error from state`() = runTest {
        val userId = "user123"

        favoritesRepo.stub {
            onBlocking { getAllFavoritesOnce(userId) } doThrow RuntimeException("Test error")
        }

        authStateFlow.value = AuthState.LoggedIn(User(uid = userId, email = "test@test.com"))

        assertNotNull(viewModel.uiState.value.error)

        // Clear error
        viewModel.clearError()

        // Verify error is cleared
        assertNull(viewModel.uiState.value.error)
    }

    @Test
    fun `logout clears favorites and resets state`() = runTest {
        val userId = "user123"
        val testFavorites = listOf(
            FavoriteRecord(id = "fav1", userId = userId, sourceText = "Test", targetText = "Prueba")
        )

        favoritesRepo.stub {
            onBlocking { getAllFavoritesOnce(userId) } doReturn testFavorites
        }

        // Login
        authStateFlow.value = AuthState.LoggedIn(User(uid = userId, email = "test@test.com"))
        assertEquals(1, viewModel.uiState.value.favorites.size)

        // Logout
        authStateFlow.value = AuthState.LoggedOut

        // Verify state reset
        assertEquals(false, viewModel.uiState.value.isLoading)
        assertEquals(emptyList<FavoriteRecord>(), viewModel.uiState.value.favorites)
    }
}

