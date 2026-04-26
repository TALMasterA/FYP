package com.translator.TalknLearn.domain.friends

import com.translator.TalknLearn.data.friends.FriendsRepository
import com.translator.TalknLearn.data.settings.UserSettingsRepository
import com.translator.TalknLearn.model.UserId
import com.translator.TalknLearn.model.friends.PublicUserProfile
import com.translator.TalknLearn.model.user.UserSettings
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

    /** Helper: find the updates map passed to updatePublicProfile. */
    @Suppress("UNCHECKED_CAST")
    private fun captureUpdateMap(): Map<String, Any> {
        val invocation = mockingDetails(friendsRepository).invocations
            .first { it.method.name.contains("updatePublicProfile") }
        return invocation.arguments[1] as Map<String, Any>
    }

    @Test
    fun `creates new profile via merge when none exists`() = runTest {
        // Arrange
        whenever(settingsRepository.fetchUserSettings(UserId("user1")))
            .thenReturn(UserSettings(primaryLanguageCode = "ja-JP"))
        whenever(friendsRepository.getPublicProfile(UserId("user1")))
            .thenReturn(null)

        // Act
        useCase("user1")

        // Assert: merge-based write (updatePublicProfile) is used, NOT createOrUpdatePublicProfile
        val updates = captureUpdateMap()
        assertEquals("user1", updates["uid"])
        assertEquals("ja-JP", updates["primaryLanguage"])
        assertTrue("lastActiveAt must be present", updates.containsKey("lastActiveAt"))

        // Crucially, isDiscoverable and username must NOT be in the merge
        // to preserve existing values on the server if the profile actually exists
        assertFalse("isDiscoverable must not be set (safe merge)", updates.containsKey("isDiscoverable"))
        assertFalse("username must not be set (safe merge)", updates.containsKey("username"))

        // createOrUpdatePublicProfile (full overwrite) must NOT be called
        val createInvocations = mockingDetails(friendsRepository).invocations
            .filter { it.method.name.contains("createOrUpdatePublicProfile") }
        assertTrue("Full overwrite must not be used", createInvocations.isEmpty())

        // ensureUserDocumentExists must still be called
        verify(friendsRepository).ensureUserDocumentExists(UserId("user1"))
    }

    @Test
    fun `merge does not overwrite visibility when getPublicProfile returns null for existing profile`() = runTest {
        // Arrange: Simulate a scenario where getPublicProfile returns null (transient error)
        // but the profile actually exists on the server with isDiscoverable = true.
        whenever(settingsRepository.fetchUserSettings(UserId("user1")))
            .thenReturn(UserSettings(primaryLanguageCode = "en-US"))
        whenever(friendsRepository.getPublicProfile(UserId("user1")))
            .thenReturn(null)

        // Act
        useCase("user1")

        // Assert: The merge write must NOT include isDiscoverable or username,
        // so the server-side values are preserved.
        val updates = captureUpdateMap()
        assertFalse("isDiscoverable must not be in merge", updates.containsKey("isDiscoverable"))
        assertFalse("username must not be in merge", updates.containsKey("username"))
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
    fun `keeps existing visibility choice when profile already exists`() = runTest {
        // Arrange: user had already chosen public visibility.
        val existingProfile = PublicUserProfile(
            uid = "user1",
            username = "test_user",
            primaryLanguage = "en-US",
            isDiscoverable = true
        )

        whenever(settingsRepository.fetchUserSettings(UserId("user1")))
            .thenReturn(UserSettings(primaryLanguageCode = "ja-JP"))
        whenever(friendsRepository.getPublicProfile(UserId("user1")))
            .thenReturn(existingProfile)

        // Act
        useCase("user1")

        // Assert: only language + timestamp patch is sent; visibility is not overwritten.
        val updates = captureUpdateMap()
        assertEquals("ja-JP", updates["primaryLanguage"])
        assertFalse("Visibility should remain untouched", updates.containsKey("isDiscoverable"))
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
        val updates = captureUpdateMap()
        assertEquals("fr-FR", updates["primaryLanguage"])
        assertFalse("isDiscoverable must not be set", updates.containsKey("isDiscoverable"))
    }
}
