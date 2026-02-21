package com.example.fyp.domain.friends

import com.example.fyp.data.friends.FriendsRepository
import com.example.fyp.data.settings.UserSettingsRepository
import com.example.fyp.model.UserId
import com.example.fyp.model.friends.PublicUserProfile
import com.example.fyp.model.user.UserSettings
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import org.mockito.kotlin.*

/**
 * Unit tests for EnsurePublicProfileExistsUseCase.
 *
 * Verifies:
 * 1. Profile is created when it does not exist (fixes "Profile not found" and crash)
 * 2. Profile is updated (lastActiveAt + primaryLanguage) when it already exists
 * 3. Primary language is pulled from user settings
 */
class EnsurePublicProfileExistsUseCaseTest {

    private lateinit var friendsRepository: FriendsRepository
    private lateinit var settingsRepository: UserSettingsRepository
    private lateinit var useCase: EnsurePublicProfileExistsUseCase

    private val userId = "test-user-123"
    private val uid = UserId(userId)
    private val defaultSettings = UserSettings(primaryLanguageCode = "en-US")

    @Before
    fun setup() {
        friendsRepository = mock()
        settingsRepository = mock()
        useCase = EnsurePublicProfileExistsUseCase(friendsRepository, settingsRepository)
    }

    @Test
    fun `creates new profile when none exists`() = runTest {
        // Arrange - no existing profile
        doAnswer { defaultSettings }.whenever(settingsRepository).fetchUserSettings(any())
        doAnswer { null }.whenever(friendsRepository).getPublicProfile(any())
        doAnswer { Result.success(Unit) }.whenever(friendsRepository).createOrUpdatePublicProfile(any(), any())

        // Act
        useCase(userId)

        // Assert - createOrUpdatePublicProfile called with correct data
        val profileCaptor = argumentCaptor<PublicUserProfile>()
        verify(friendsRepository).createOrUpdatePublicProfile(eq(uid), profileCaptor.capture())
        val createdProfile = profileCaptor.firstValue
        assertEquals(userId, createdProfile.uid)
        assertEquals("en-US", createdProfile.primaryLanguage)
        assertEquals("", createdProfile.username)
        assertTrue(createdProfile.isDiscoverable)
    }

    @Test
    fun `does not create profile when one already exists`() = runTest {
        // Arrange - profile already exists
        val existingProfile = PublicUserProfile(
            uid = userId,
            username = "existing_user",
            primaryLanguage = "en-US"
        )
        doAnswer { defaultSettings }.whenever(settingsRepository).fetchUserSettings(any())
        doAnswer { existingProfile }.whenever(friendsRepository).getPublicProfile(any())
        doAnswer { Result.success(Unit) }.whenever(friendsRepository).updatePublicProfile(any(), any())

        // Act
        useCase(userId)

        // Assert - update called, NOT create
        verify(friendsRepository, never()).createOrUpdatePublicProfile(any(), any())
        verify(friendsRepository).updatePublicProfile(eq(uid), any())
    }

    @Test
    fun `updates lastActiveAt and primaryLanguage when profile exists`() = runTest {
        // Arrange
        val existingProfile = PublicUserProfile(uid = userId, primaryLanguage = "zh-HK")
        val settings = UserSettings(primaryLanguageCode = "en-US")
        doAnswer { settings }.whenever(settingsRepository).fetchUserSettings(any())
        doAnswer { existingProfile }.whenever(friendsRepository).getPublicProfile(any())
        doAnswer { Result.success(Unit) }.whenever(friendsRepository).updatePublicProfile(any(), any())

        // Act
        useCase(userId)

        // Assert - updates include primaryLanguage and lastActiveAt
        val updatesCaptor = argumentCaptor<Map<String, Any>>()
        verify(friendsRepository).updatePublicProfile(eq(uid), updatesCaptor.capture())
        val updates = updatesCaptor.firstValue
        assertEquals("en-US", updates["primaryLanguage"])
        assertNotNull(updates["lastActiveAt"])
    }

    @Test
    fun `uses default language when settings have blank primary language`() = runTest {
        // Arrange
        val settingsWithBlankLang = UserSettings(primaryLanguageCode = "")
        doAnswer { settingsWithBlankLang }.whenever(settingsRepository).fetchUserSettings(any())
        doAnswer { null }.whenever(friendsRepository).getPublicProfile(any())
        doAnswer { Result.success(Unit) }.whenever(friendsRepository).createOrUpdatePublicProfile(any(), any())

        // Act
        useCase(userId)

        // Assert - falls back to "en-US"
        val profileCaptor = argumentCaptor<PublicUserProfile>()
        verify(friendsRepository).createOrUpdatePublicProfile(eq(uid), profileCaptor.capture())
        assertEquals("en-US", profileCaptor.firstValue.primaryLanguage)
    }

    @Test
    fun `new profile is discoverable by default`() = runTest {
        // Arrange
        doAnswer { defaultSettings }.whenever(settingsRepository).fetchUserSettings(any())
        doAnswer { null }.whenever(friendsRepository).getPublicProfile(any())
        doAnswer { Result.success(Unit) }.whenever(friendsRepository).createOrUpdatePublicProfile(any(), any())

        // Act
        useCase(userId)

        // Assert - new profiles are discoverable
        val profileCaptor = argumentCaptor<PublicUserProfile>()
        verify(friendsRepository).createOrUpdatePublicProfile(eq(uid), profileCaptor.capture())
        assertTrue(profileCaptor.firstValue.isDiscoverable)
    }

    @Test
    fun `new profile has empty username and displayName`() = runTest {
        // Arrange
        doAnswer { defaultSettings }.whenever(settingsRepository).fetchUserSettings(any())
        doAnswer { null }.whenever(friendsRepository).getPublicProfile(any())
        doAnswer { Result.success(Unit) }.whenever(friendsRepository).createOrUpdatePublicProfile(any(), any())

        // Act
        useCase(userId)

        // Assert - username and displayName are blank until user sets them
        val profileCaptor = argumentCaptor<PublicUserProfile>()
        verify(friendsRepository).createOrUpdatePublicProfile(eq(uid), profileCaptor.capture())
        assertEquals("", profileCaptor.firstValue.username)
        assertEquals("", profileCaptor.firstValue.displayName)
    }
}
