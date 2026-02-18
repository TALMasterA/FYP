package com.example.fyp.domain.friends

import com.example.fyp.data.friends.FriendsRepository
import com.example.fyp.model.UserId
import com.example.fyp.model.friends.PublicUserProfile
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.stub
import org.mockito.kotlin.verify

/**
 * Unit tests for SearchUsersUseCase.
 * Tests user search functionality with various query patterns.
 */
class SearchUsersUseCaseTest {

    private lateinit var friendsRepository: FriendsRepository
    private lateinit var useCase: SearchUsersUseCase

    @Before
    fun setup() {
        friendsRepository = mock()
        useCase = SearchUsersUseCase(friendsRepository)
    }

    @Test
    fun `search with valid query returns matching users`() = runTest {
        // Arrange
        val query = "john"
        val expectedProfiles = listOf(
            PublicUserProfile(
                userId = UserId("user1"),
                username = "john_doe",
                displayName = "John Doe",
                primaryLanguage = "en-US",
                learningLanguages = listOf("es-ES")
            ),
            PublicUserProfile(
                userId = UserId("user2"),
                username = "johnny",
                displayName = "Johnny Smith",
                primaryLanguage = "en-US",
                learningLanguages = listOf("fr-FR")
            )
        )

        friendsRepository.stub {
            onBlocking { searchUsersByUsername(query) } doReturn Result.success(expectedProfiles)
        }

        // Act
        val result = useCase(query)

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(expectedProfiles, result.getOrNull())
        verify(friendsRepository).searchUsersByUsername(query)
    }

    @Test
    fun `search with empty query returns error`() = runTest {
        // Arrange
        val query = ""

        // Act
        val result = useCase(query)

        // Assert
        assertTrue(result.isFailure)
    }

    @Test
    fun `search with short query returns error`() = runTest {
        // Arrange
        val query = "a" // Less than minimum 2 characters

        // Act
        val result = useCase(query)

        // Assert
        assertTrue(result.isFailure)
    }

    @Test
    fun `search with no matches returns empty list`() = runTest {
        // Arrange
        val query = "xyz123"
        val emptyList = emptyList<PublicUserProfile>()

        friendsRepository.stub {
            onBlocking { searchUsersByUsername(query) } doReturn Result.success(emptyList)
        }

        // Act
        val result = useCase(query)

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(emptyList, result.getOrNull())
    }

    @Test
    fun `search handles repository failure`() = runTest {
        // Arrange
        val query = "test"
        val exception = Exception("Network error")

        friendsRepository.stub {
            onBlocking { searchUsersByUsername(query) } doReturn Result.failure(exception)
        }

        // Act
        val result = useCase(query)

        // Assert
        assertTrue(result.isFailure)
    }
}
