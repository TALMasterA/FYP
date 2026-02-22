package com.example.fyp.screens.settings

import com.example.fyp.data.friends.FriendsRepository
import com.example.fyp.data.user.FirebaseAuthRepository
import com.example.fyp.data.user.FirestoreProfileRepository
import com.example.fyp.model.UserId
import com.example.fyp.model.Username
import com.example.fyp.model.friends.PublicUserProfile
import com.example.fyp.model.user.AuthState
import com.example.fyp.model.user.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.*

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var authRepo: FirebaseAuthRepository
    private lateinit var profileRepo: FirestoreProfileRepository
    private lateinit var friendsRepo: FriendsRepository

    private val authStateFlow = MutableStateFlow<AuthState>(AuthState.Loading)
    private val loggedInState = AuthState.LoggedIn(User(uid = "user1", email = "test@example.com"))

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        authRepo = mock()
        whenever(authRepo.currentUserState).thenReturn(authStateFlow)
        whenever(authRepo.currentUser).thenReturn(null)

        profileRepo = mock()
        friendsRepo = mock()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(): ProfileViewModel {
        runBlocking {
            whenever(friendsRepo.getPublicProfile(UserId("user1")))
                .thenReturn(PublicUserProfile(uid = "user1", username = "old_user"))
        }
        return ProfileViewModel(authRepo, profileRepo, friendsRepo)
    }

    // ── updateUsername: format validation ─────────────────────────────────────

    @Test
    fun `updateUsername rejects invalid characters`() = runTest {
        val viewModel = createViewModel()
        authStateFlow.value = loggedInState
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.updateUsername("bad name!")

        assertEquals(
            "Username can only contain letters, numbers, and underscores",
            viewModel.uiState.value.error
        )
    }

    @Test
    fun `updateUsername rejects spaces`() = runTest {
        val viewModel = createViewModel()
        authStateFlow.value = loggedInState
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.updateUsername("bad name")

        assertEquals(
            "Username can only contain letters, numbers, and underscores",
            viewModel.uiState.value.error
        )
    }

    // ── updateUsername: length validation ─────────────────────────────────────

    @Test
    fun `updateUsername rejects too short username`() = runTest {
        val viewModel = createViewModel()
        authStateFlow.value = loggedInState
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.updateUsername("ab")

        assertEquals("Username must be 3-20 characters", viewModel.uiState.value.error)
    }

    @Test
    fun `updateUsername rejects too long username`() = runTest {
        val viewModel = createViewModel()
        authStateFlow.value = loggedInState
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.updateUsername("a".repeat(21))

        assertEquals("Username must be 3-20 characters", viewModel.uiState.value.error)
    }

    // ── updateUsername: already taken ─────────────────────────────────────────

    @Test
    fun `updateUsername shows error when username is taken`() = runTest {
        val viewModel = createViewModel()
        authStateFlow.value = loggedInState
        testDispatcher.scheduler.advanceUntilIdle()

        whenever(friendsRepo.isUsernameAvailable(Username("taken_u")))
            .thenReturn(false)

        viewModel.updateUsername("taken_u")
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals("Username already taken", viewModel.uiState.value.error)
    }

    // ── updateUsername: keeping same username is allowed ──────────────────────

    @Test
    fun `updateUsername allows keeping current username even if not available`() = runTest {
        val viewModel = createViewModel()
        authStateFlow.value = loggedInState
        testDispatcher.scheduler.advanceUntilIdle()

        whenever(friendsRepo.isUsernameAvailable(Username("old_user")))
            .thenReturn(false)
        whenever(friendsRepo.setUsername(UserId("user1"), Username("old_user")))
            .thenReturn(Result.success(Unit))
        whenever(friendsRepo.updatePublicProfile(UserId("user1"), mapOf("username" to "old_user")))
            .thenReturn(Result.success(Unit))

        viewModel.updateUsername("old_user")
        testDispatcher.scheduler.advanceUntilIdle()

        assertNull(viewModel.uiState.value.error)
        assertEquals("Username updated successfully", viewModel.uiState.value.successMessage)
    }

    // ── deleteAccount: blank password ────────────────────────────────────────

    @Test
    fun `deleteAccount with blank password fails reauthentication`() = runTest {
        val viewModel = createViewModel()
        authStateFlow.value = loggedInState
        testDispatcher.scheduler.advanceUntilIdle()

        whenever(profileRepo.reauthenticate("test@example.com", ""))
            .thenReturn(Result.failure(Exception("Password cannot be blank")))

        viewModel.deleteAccount("")
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals("Invalid password. Please try again.", viewModel.uiState.value.deleteError)
        assertFalse(viewModel.uiState.value.isDeletingAccount)
    }
}
