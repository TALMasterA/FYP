package com.example.fyp.screens.login

import com.example.fyp.data.user.FirebaseAuthRepository
import com.example.fyp.model.user.AuthState
import com.example.fyp.model.user.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import com.example.fyp.core.security.RateLimiter
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.*

/**
 * Unit tests for AuthViewModel.
 *
 * Tests:
 * 1. Login success clears error and sets loading states correctly
 * 2. Login failure surfaces mapped error message
 * 3. Invalid email rejected before repo call
 * 4. Login rate limiter blocks after 5 attempts
 * 5. Password reset success sets messageKey
 * 6. Password reset failure surfaces mapped error
 * 7. Password reset invalid email rejected before repo call
 * 8. Password reset rate limiter blocks after 3 attempts
 * 9. Register always returns disabled message
 * 10. Logout resets uiState
 * 11. clearError clears error fields only
 * 12. clearMessage clears message fields only
 * 13. Login trims email whitespace
 */
@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val authStateFlow = MutableStateFlow<AuthState>(AuthState.Loading)

    private lateinit var authRepo: FirebaseAuthRepository
    private lateinit var viewModel: AuthViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        // Reset static rate limiters that persist across test instances
        listOf("loginRateLimiter", "resetPasswordRateLimiter").forEach { fieldName ->
            AuthViewModel::class.java.getDeclaredField(fieldName).apply {
                isAccessible = true
                (get(null) as RateLimiter).clear()
            }
        }
        authRepo = mock {
            on { currentUserState } doReturn authStateFlow
        }
        viewModel = AuthViewModel(authRepo)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // ── Login success ───────────────────────────────────────────────

    @Test
    fun `login success clears error`() = runTest {
        val testUser = User(uid = "uid1", email = "test@example.com")
        whenever(authRepo.login("test@example.com", "Password1"))
            .thenReturn(Result.success(testUser))

        viewModel.login("test@example.com", "Password1")

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNull(state.errorRaw)
        assertNull(state.errorKey)
    }

    // ── Login failure ───────────────────────────────────────────────

    @Test
    fun `login failure surfaces error message`() = runTest {
        whenever(authRepo.login("test@example.com", "wrong"))
            .thenReturn(Result.failure(RuntimeException("Auth failed")))

        viewModel.login("test@example.com", "wrong")

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNotNull(state.errorRaw)
    }

    // ── Invalid email rejected before repo call ─────────────────────

    @Test
    fun `login with invalid email rejects before repo call`() = runTest {
        viewModel.login("not-an-email", "Password1")

        val state = viewModel.uiState.value
        assertNotNull(state.errorRaw)
        assertTrue(state.errorRaw!!.contains("email", ignoreCase = true))
        // Repo should never be called
        verify(authRepo, never()).login(any(), any())
    }

    @Test
    fun `login with blank email rejects before repo call`() = runTest {
        viewModel.login("", "Password1")

        val state = viewModel.uiState.value
        assertNotNull(state.errorRaw)
        verify(authRepo, never()).login(any(), any())
    }

    // ── Rate limiter blocks login ───────────────────────────────────

    @Test
    fun `login rate limiter blocks after 5 attempts`() = runTest {
        whenever(authRepo.login(any(), any()))
            .thenReturn(Result.failure(RuntimeException("wrong password")))

        // Make 5 failed attempts
        repeat(5) {
            viewModel.login("rate@example.com", "wrong")
        }

        // 6th attempt should be blocked by rate limiter
        viewModel.login("rate@example.com", "wrong")

        val state = viewModel.uiState.value
        assertNotNull(state.errorRaw)
        assertTrue(state.errorRaw!!.contains("Too many", ignoreCase = true))
        // Repo should only be called 5 times (6th blocked)
        verify(authRepo, times(5)).login(eq("rate@example.com"), any())
    }

    // ── Login trims email whitespace ────────────────────────────────

    @Test
    fun `login trims email whitespace`() = runTest {
        whenever(authRepo.login("test@example.com", "Password1"))
            .thenReturn(Result.success(User(uid = "uid1")))

        viewModel.login("  test@example.com  ", "Password1")

        verify(authRepo).login("test@example.com", "Password1")
    }

    // ── Login sets loading state ────────────────────────────────────

    @Test
    fun `login sets loading false when completed`() = runTest {
        whenever(authRepo.login(any(), any()))
            .thenReturn(Result.success(User(uid = "uid1")))

        viewModel.login("user@example.com", "Password1")

        assertFalse(viewModel.uiState.value.isLoading)
    }

    // ── Password reset success ──────────────────────────────────────

    @Test
    fun `resetPassword success sets messageKey`() = runTest {
        whenever(authRepo.sendPasswordResetEmail("test@example.com"))
            .thenReturn(Result.success(Unit))

        viewModel.resetPassword("test@example.com")

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNull(state.errorRaw)
        assertNotNull(state.messageKey)
    }

    // ── Password reset failure ──────────────────────────────────────

    @Test
    fun `resetPassword failure surfaces error`() = runTest {
        whenever(authRepo.sendPasswordResetEmail("test@example.com"))
            .thenReturn(Result.failure(RuntimeException("user not found")))

        viewModel.resetPassword("test@example.com")

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNotNull(state.errorRaw)
        assertNull(state.messageKey)
    }

    // ── Password reset invalid email ────────────────────────────────

    @Test
    fun `resetPassword with invalid email rejects before repo call`() = runTest {
        viewModel.resetPassword("invalid")

        assertNotNull(viewModel.uiState.value.errorRaw)
        verify(authRepo, never()).sendPasswordResetEmail(any())
    }

    // ── Password reset rate limiter ─────────────────────────────────

    @Test
    fun `resetPassword rate limiter blocks after 3 attempts`() = runTest {
        whenever(authRepo.sendPasswordResetEmail(any()))
            .thenReturn(Result.success(Unit))

        repeat(3) {
            viewModel.resetPassword("limit@example.com")
        }

        // 4th should be blocked
        viewModel.resetPassword("limit@example.com")

        val state = viewModel.uiState.value
        assertTrue(state.errorRaw!!.contains("Too many", ignoreCase = true))
        verify(authRepo, times(3)).sendPasswordResetEmail(eq("limit@example.com"))
    }

    // ── Register disabled ───────────────────────────────────────────

    @Test
    fun `register always returns disabled message`() = runTest {
        viewModel.register("test@example.com", "Password1")

        val state = viewModel.uiState.value
        assertNotNull(state.errorKey)
        assertFalse(state.isLoading)
    }

    // ── Logout ──────────────────────────────────────────────────────

    @Test
    fun `logout resets uiState and calls repo`() = runTest {
        // First login to set some state
        whenever(authRepo.login("test@example.com", "wrong"))
            .thenReturn(Result.failure(RuntimeException("fail")))
        viewModel.login("test@example.com", "wrong")
        assertNotNull(viewModel.uiState.value.errorRaw)

        viewModel.logout()

        val state = viewModel.uiState.value
        assertNull(state.errorRaw)
        assertNull(state.errorKey)
        assertNull(state.messageKey)
        assertNull(state.messageRaw)
        assertFalse(state.isLoading)
        verify(authRepo).logout()
    }

    // ── clearError ──────────────────────────────────────────────────

    @Test
    fun `clearError only clears error fields`() = runTest {
        whenever(authRepo.sendPasswordResetEmail("test@example.com"))
            .thenReturn(Result.failure(RuntimeException("fail")))
        viewModel.resetPassword("test@example.com")
        assertNotNull(viewModel.uiState.value.errorRaw)

        viewModel.clearError()

        assertNull(viewModel.uiState.value.errorRaw)
        assertNull(viewModel.uiState.value.errorKey)
    }

    // ── clearMessage ────────────────────────────────────────────────

    @Test
    fun `clearMessage only clears message fields`() = runTest {
        whenever(authRepo.sendPasswordResetEmail("test@example.com"))
            .thenReturn(Result.success(Unit))
        viewModel.resetPassword("test@example.com")
        assertNotNull(viewModel.uiState.value.messageKey)

        viewModel.clearMessage()

        assertNull(viewModel.uiState.value.messageKey)
        assertNull(viewModel.uiState.value.messageRaw)
    }

    // ── Google Sign-In ──────────────────────────────────────────────

    @Test
    fun `signInWithGoogle success clears error and not loading`() = runTest {
        val token = "valid-id-token"
        val user = User(uid = "g-uid", email = "g@example.com")
        whenever(authRepo.signInWithGoogle(token)).thenReturn(Result.success(user))

        viewModel.signInWithGoogle(token)

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNull(state.errorRaw)
        assertNull(state.errorKey)
        verify(authRepo).signInWithGoogle(token)
    }

    @Test
    fun `signInWithGoogle failure surfaces error message`() = runTest {
        val token = "valid-id-token"
        whenever(authRepo.signInWithGoogle(token))
            .thenReturn(Result.failure(RuntimeException("network down")))

        viewModel.signInWithGoogle(token)

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNotNull(state.errorRaw)
    }

    @Test
    fun `signInWithGoogle blank token rejected before repo call`() = runTest {
        viewModel.signInWithGoogle("   ")

        val state = viewModel.uiState.value
        assertNotNull(state.errorRaw)
        assertTrue(state.errorRaw!!.contains("empty", ignoreCase = true))
        verify(authRepo, never()).signInWithGoogle(any())
    }

    @Test
    fun `reportGoogleSignInError surfaces message and stops loading`() = runTest {
        viewModel.reportGoogleSignInError("user cancelled")

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals("user cancelled", state.errorRaw)
    }
}
