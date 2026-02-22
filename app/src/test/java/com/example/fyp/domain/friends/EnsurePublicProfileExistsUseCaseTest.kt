package com.example.fyp.domain.friends

import com.example.fyp.data.friends.FriendsRepository
import com.example.fyp.data.settings.UserSettingsRepository
import com.example.fyp.model.UserId
import com.example.fyp.model.friends.PublicUserProfile
import com.example.fyp.model.user.UserSettings
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mockingDetails
import org.mockito.kotlin.*

/**
 * Unit tests for EnsurePublicProfileExistsUseCase.
 * Tests profile creation and update logic on login.
 */
class EnsurePublicProfileExistsUseCaseTest {

    private lateinit var friendsRepository: FriendsRepository
    private lateinit var settingsRepository: UserSettingsRepository
    private lateinit var useCase: EnsurePublicProfileExistsUseCase

    @Before
    fun setup() {
        friendsRepository = mock()
        settingsRepository = mock()
        useCase = EnsurePublicProfileExistsUseCase(friendsRepository, settingsRepository)
    }

    /** Helper: find the profile passed to createOrUpdatePublicProfile. */
    private fun captureCreatedProfile(): PublicUserProfile {
        val invocation = mockingDetails(friendsRepository).invocations
            .single { it.method.name.contains("createOrUpdatePublicProfile") }
        @Suppress("UNCHECKED_CAST")
        return invocation.arguments[1] as PublicUserProfile
    }

    /** Helper: find the updates map passed to updatePublicProfile. */
    @Suppress("UNCHECKED_CAST")
    private fun captureUpdateMap(): Map<String, Any> {
        val invocation = mockingDetails(friendsRepository).invocations
            .single { it.method.name.contains("updatePublicProfile") }
        return invocation.arguments[1] as Map<String, Any>
    }

    @Test
    fun `creates new profile when none exists`() = runTest {
        // Arrange
        whenever(settingsRepository.fetchUserSettings(UserId("user1")))
            .thenReturn(UserSettings(primaryLanguageCode = "ja-JP"))
        whenever(friendsRepository.getPublicProfile(UserId("user1")))
            .thenReturn(null)

        // Act
        useCase("user1")

        // Assert
        val created = captureCreatedProfile()
        assertEquals("user1", created.uid)
        assertEquals("ja-JP", created.primaryLanguage)
        assertEquals("", created.username)
        assertTrue(created.isDiscoverable)
    }
    @Test
    fun `does not write when profile exists with same language`() = runTest {
        // Arrange
        val existingProfile = PublicUserProfile(
            uid = "user1",
            username = "test_user",
            primaryLanguage = "en-US"
        )

        whenever(settingsRepository.fetchUserSettings(UserId("user1")))
            .thenReturn(UserSettings(primaryLanguageCode = "en-US"))
        whenever(friendsRepository.getPublicProfile(UserId("user1")))
            .thenReturn(existingProfile)

        // Act
        useCase("user1")

        // Assert — no write should be performed
        val writeInvocations = mockingDetails(friendsRepository).invocations
            .filter {
                it.method.name.contains("createOrUpdatePublicProfile") ||
                        it.method.name.contains("updatePublicProfile")
            }
        assertTrue("Expected no write invocations", writeInvocations.isEmpty())
    }

    @Test
    fun `updates language when profile exists with different language`() = runTest {
        // Arrange
        val existingProfile = PublicUserProfile(
            uid = "user1",
            username = "test_user",
            primaryLanguage = "en-US"
        )

        whenever(settingsRepository.fetchUserSettings(UserId("user1")))
            .thenReturn(UserSettings(primaryLanguageCode = "ja-JP"))
        whenever(friendsRepository.getPublicProfile(UserId("user1")))
            .thenReturn(existingProfile)

        // Act
        useCase("user1")

        // Assert — should update with new language
        val updates = captureUpdateMap()
        assertEquals("ja-JP", updates["primaryLanguage"])
        assertTrue(updates.containsKey("lastActiveAt"))

        val createInvocations = mockingDetails(friendsRepository).invocations
            .filter { it.method.name.contains("createOrUpdatePublicProfile") }
        assertTrue("Should not create new profile", createInvocations.isEmpty())
    }

    @Test
    fun `uses knownPrimaryLanguage instead of fetching settings`() = runTest {
        // Arrange
        whenever(friendsRepository.getPublicProfile(UserId("user1")))
            .thenReturn(null)

        // Act — provide knownPrimaryLanguage directly
        useCase("user1", knownPrimaryLanguage = "fr-FR")

        // Assert — settingsRepository should NOT be called
        verifyNoInteractions(settingsRepository)
        val created = captureCreatedProfile()
        assertEquals("fr-FR", created.primaryLanguage)
    }
}
