package com.translator.TalknLearn.screens.friends

import com.translator.TalknLearn.data.friends.FriendsRepository
import com.translator.TalknLearn.data.friends.SharedFriendsDataSource
import com.translator.TalknLearn.data.user.FirebaseAuthRepository
import com.translator.TalknLearn.domain.friends.EnsurePublicProfileExistsUseCase
import com.translator.TalknLearn.domain.friends.GetCurrentUserProfileUseCase
import com.translator.TalknLearn.model.UserId
import com.translator.TalknLearn.model.user.AuthState
import com.translator.TalknLearn.model.user.User
import com.translator.TalknLearn.model.friends.PublicUserProfile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
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
 * Unit tests for MyProfileViewModel.
 *
 * Tests:
 *  1. LoggedIn loads profile successfully
 *  2. LoggedIn auto-creates profile when null
 *  3. LoggedIn caches own username via SharedFriendsDataSource
 *  4. LoggedIn with load error sets error state
 *  5. LoggedOut resets UI state
 *  6. Loading sets isLoading true
 *  7. updateVisibility success updates profile
 *  8. updateVisibility failure sets error
 *  9. updateVisibility with no userId does nothing
 * 10. clearError clears error state
 * 11. refreshProfile reloads via current user ID
 * 12. showSuccessMessage sets and auto-clears message
 * 13. updateVisibility blocks public mode when username is blank
 */
@OptIn(ExperimentalCoroutinesApi::class)
class MyProfileViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val authStateFlow = MutableStateFlow<AuthState>(AuthState.Loading)

    private val testUserId = "user123"
    private val testUser = User(uid = testUserId, email = "test@test.com")
    private val testProfile = PublicUserProfile(
        uid = testUserId,
        username = "testuser",
        primaryLanguage = "en-US",
        isDiscoverable = true
    )

    private lateinit var authRepo: FirebaseAuthRepository
    private lateinit var getCurrentUserProfile: GetCurrentUserProfileUseCase
    private lateinit var sharedFriends: SharedFriendsDataSource
    private lateinit var friendsRepo: FriendsRepository
    private lateinit var ensureProfile: EnsurePublicProfileExistsUseCase

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        authRepo = mock { on { currentUserState } doReturn authStateFlow }
        getCurrentUserProfile = mock()
        sharedFriends = mock()
        friendsRepo = mock()
        ensureProfile = mock()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun buildViewModel() = MyProfileViewModel(
        authRepository = authRepo,
        getCurrentUserProfileUseCase = getCurrentUserProfile,
        sharedFriendsDataSource = sharedFriends,
        friendsRepository = friendsRepo,
        ensurePublicProfileExistsUseCase = ensureProfile
    )

    // ── Test 1: LoggedIn loads profile ──

    @Test
    fun `login loads profile successfully`() = runTest {
        whenever(getCurrentUserProfile.invoke(UserId(testUserId))).thenReturn(testProfile)

        val vm = buildViewModel()
        authStateFlow.value = AuthState.LoggedIn(testUser)

        val state = vm.uiState.value
        assertFalse(state.isLoading)
        assertEquals(testProfile, state.profile)
        assertEquals(testUserId, state.userId)
        assertNull(state.error)
    }

    // ── Test 2: Auto-creates profile when null ──

    @Test
    fun `login auto-creates profile when not found`() = runTest {
        // First call returns null, second call (after creation) returns profile
        whenever(getCurrentUserProfile.invoke(UserId(testUserId)))
            .thenReturn(null)
            .thenReturn(testProfile)

        val vm = buildViewModel()
        authStateFlow.value = AuthState.LoggedIn(testUser)

        verify(ensureProfile).invoke(testUserId)
        assertEquals(testProfile, vm.uiState.value.profile)
    }

    // ── Test 3: Caches own username ──

    @Test
    fun `login caches own username`() = runTest {
        whenever(getCurrentUserProfile.invoke(UserId(testUserId))).thenReturn(testProfile)

        val vm = buildViewModel()
        authStateFlow.value = AuthState.LoggedIn(testUser)

        verify(sharedFriends).cacheOwnUsername(testUserId, "testuser")
    }

    @Test
    fun `login does not cache blank username`() = runTest {
        val blankUsernameProfile = testProfile.copy(username = "")
        whenever(getCurrentUserProfile.invoke(UserId(testUserId))).thenReturn(blankUsernameProfile)

        val vm = buildViewModel()
        authStateFlow.value = AuthState.LoggedIn(testUser)

        verify(sharedFriends, never()).cacheOwnUsername(any(), any())
    }

    // ── Test 4: Load error sets error state ──

    @Test
    fun `login with load error sets error message`() = runTest {
        whenever(getCurrentUserProfile.invoke(UserId(testUserId)))
            .thenThrow(RuntimeException("Network error"))

        val vm = buildViewModel()
        authStateFlow.value = AuthState.LoggedIn(testUser)

        val state = vm.uiState.value
        assertFalse(state.isLoading)
        assertNotNull(state.error)
        assertTrue(state.error!!.contains("Failed to load profile"))
    }

    // ── Test 5: LoggedOut resets state ──

    @Test
    fun `logout resets ui state`() = runTest {
        whenever(getCurrentUserProfile.invoke(UserId(testUserId))).thenReturn(testProfile)

        val vm = buildViewModel()
        authStateFlow.value = AuthState.LoggedIn(testUser)
        assertEquals(testProfile, vm.uiState.value.profile)

        authStateFlow.value = AuthState.LoggedOut

        val state = vm.uiState.value
        assertTrue(state.isLoading) // default MyProfileUiState has isLoading = true
        assertNull(state.profile)
        assertEquals("", state.userId)
    }

    // ── Test 6: Loading sets isLoading ──

    @Test
    fun `loading auth state sets isLoading`() = runTest {
        val vm = buildViewModel()
        // authStateFlow starts as Loading

        assertTrue(vm.uiState.value.isLoading)
    }

    // ── Test 7: updateVisibility success ──

    @Test
    fun `updateVisibility success updates profile`() = runTest {
        whenever(getCurrentUserProfile.invoke(UserId(testUserId))).thenReturn(testProfile)

        friendsRepo.stub {
            onBlocking { updatePublicProfile(UserId(testUserId), mapOf("isDiscoverable" to false)) } doReturn Result.success(Unit)
        }

        val vm = buildViewModel()
        authStateFlow.value = AuthState.LoggedIn(testUser)

        vm.updateVisibility(false)

        val state = vm.uiState.value
        assertFalse(state.isUpdatingVisibility)
        assertFalse(state.profile!!.isDiscoverable)
    }

    // ── Test 8: updateVisibility failure sets error ──

    @Test
    fun `updateVisibility failure sets error`() = runTest {
        whenever(getCurrentUserProfile.invoke(UserId(testUserId))).thenReturn(testProfile)

        friendsRepo.stub {
            onBlocking { updatePublicProfile(UserId(testUserId), mapOf("isDiscoverable" to false)) } doReturn Result.failure(RuntimeException("Firestore error"))
        }

        val vm = buildViewModel()
        authStateFlow.value = AuthState.LoggedIn(testUser)

        vm.updateVisibility(false)

        val state = vm.uiState.value
        assertFalse(state.isUpdatingVisibility)
        assertNotNull(state.error)
        assertTrue(state.error!!.contains("Failed to update visibility"))
    }

    // ── Test 9: updateVisibility with no userId is no-op ──

    @Test
    fun `updateVisibility before login does nothing`() = runTest {
        val vm = buildViewModel()
        // No login — currentUserId is null

        vm.updateVisibility(true)

        verifyNoInteractions(friendsRepo)
    }

    @Test
    fun `updateVisibility blocks public mode when username is blank`() = runTest {
        val blankUsernameProfile = testProfile.copy(username = "")
        whenever(getCurrentUserProfile.invoke(UserId(testUserId))).thenReturn(blankUsernameProfile)

        val vm = buildViewModel()
        authStateFlow.value = AuthState.LoggedIn(testUser)

        vm.updateVisibility(true)

        assertEquals("Set a username before making your profile public.", vm.uiState.value.error)
        verifyNoInteractions(friendsRepo)
    }

    // ── Test 10: clearError clears error ──

    @Test
    fun `clearError clears error state`() = runTest {
        whenever(getCurrentUserProfile.invoke(UserId(testUserId)))
            .thenThrow(RuntimeException("fail"))

        val vm = buildViewModel()
        authStateFlow.value = AuthState.LoggedIn(testUser)
        assertNotNull(vm.uiState.value.error)

        vm.clearError()

        assertNull(vm.uiState.value.error)
    }

}
