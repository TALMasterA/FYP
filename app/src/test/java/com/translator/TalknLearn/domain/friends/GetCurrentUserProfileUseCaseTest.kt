package com.translator.TalknLearn.domain.friends

import com.translator.TalknLearn.data.friends.FriendsRepository
import com.translator.TalknLearn.model.UserId
import com.translator.TalknLearn.model.friends.PublicUserProfile
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

/**
 * Unit tests for GetCurrentUserProfileUseCase.
 * Tests retrieving current user's profile information.
 */
class GetCurrentUserProfileUseCaseTest {

    private lateinit var friendsRepository: FriendsRepository
    private lateinit var useCase: GetCurrentUserProfileUseCase

    @Before
    fun setup() {
        friendsRepository = mock()
        useCase = GetCurrentUserProfileUseCase(friendsRepository)
    }

    @Test
    fun `get current user profile returns profile when exists`() = runTest {
        // Arrange
        val userId = UserId("user1")
        val expectedProfile = PublicUserProfile(
            uid = "user1",
            username = "john_doe",
            primaryLanguage = "en-US",
            learningLanguages = listOf("es-ES", "fr-FR")
        )

        whenever(friendsRepository.getPublicProfile(userId)).thenReturn(expectedProfile)

        // Act
        val result = useCase(userId)

        // Assert
        assertEquals(expectedProfile, result)
        verify(friendsRepository).getPublicProfile(userId)
    }

    @Test
    fun `get current user profile returns null when not found`() = runTest {
        // Arrange
        val userId = UserId("nonexistent")

        whenever(friendsRepository.getPublicProfile(userId)).thenReturn(null)

        // Act
        val result = useCase(userId)

        // Assert: use case returns null — callers (e.g. MyProfileViewModel) handle null gracefully
        // by showing a helpful message rather than crashing.
        assertNull(result)
        verify(friendsRepository).getPublicProfile(userId)
    }

    @Test
    fun `get profile for different users returns correct profiles`() = runTest {
        // Arrange
        val user1 = UserId("user1")
        val user2 = UserId("user2")

        val profile1 = PublicUserProfile(
            uid = "user1",
            username = "user1",
            primaryLanguage = "en-US",
            learningLanguages = listOf()
        )

        val profile2 = PublicUserProfile(
            uid = "user2",
            username = "user2",
            primaryLanguage = "es-ES",
            learningLanguages = listOf("en-US")
        )

        whenever(friendsRepository.getPublicProfile(user1)).thenReturn(profile1)
        whenever(friendsRepository.getPublicProfile(user2)).thenReturn(profile2)

        // Act
        val result1 = useCase(user1)
        val result2 = useCase(user2)

        // Assert
        assertEquals(profile1, result1)
        assertEquals(profile2, result2)
    }
}
