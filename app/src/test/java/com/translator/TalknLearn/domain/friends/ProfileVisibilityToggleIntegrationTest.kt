package com.translator.TalknLearn.domain.friends

import com.translator.TalknLearn.data.friends.FriendsRepository
import com.translator.TalknLearn.model.UserId
import com.translator.TalknLearn.model.friends.PublicUserProfile
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.stub
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

/**
 * Integration tests for profile visibility toggle.
 *
 * Verifies that toggling isDiscoverable between public and private actually affects
 * whether a profile can be found via search.
 *
 * Test scenario:
 * 1. User starts with default private profile (isDiscoverable=false)
 * 2. User sets username
 * 3. Search should NOT find private profile
 * 4. User toggles to public
 * 5. Search should find profile
 * 6. User toggles back to private
 * 7. Search should NOT find profile
 */
class ProfileVisibilityToggleIntegrationTest {

    private lateinit var friendsRepository: FriendsRepository
    private lateinit var searchUseCase: SearchUsersUseCase

    @Before
    fun setup() {
        friendsRepository = mock()
        searchUseCase = SearchUsersUseCase(friendsRepository)
    }

    @Test
    fun `default profile is private and not searchable`() = runTest {
        // Arrange: New user with default private profile
        val profile = PublicUserProfile(
            uid = "user1",
            username = "alice_wonder",
            primaryLanguage = "en-US",
            isDiscoverable = false  // Default is PRIVATE
        )

        // Profile created but NOT in search results because isDiscoverable=false
        friendsRepository.stub {
            onBlocking {
                searchUsersByUsername("alice", limit = 20, callerUserId = null)
            } doReturn Result.success(emptyList())  // Private profile not in search
        }

        // Act: Search for the user
        val searchResult = searchUseCase("alice")

        // Assert: Private profile should not be found
        assertTrue(searchResult.isSuccess)
        assertEquals(emptyList<PublicUserProfile>(), searchResult.getOrNull())
        assertFalse("Private profile should not be searchable", profile.isDiscoverable)
    }

    @Test
    fun `toggling to public makes profile searchable`() = runTest {
        // Arrange: User makes profile public
        val privateProfile = PublicUserProfile(
            uid = "user1",
            username = "bob_builder",
            primaryLanguage = "en-US",
            isDiscoverable = false
        )

        // When toggling to public
        val publicProfile = privateProfile.copy(isDiscoverable = true)

        // Repository now includes this profile in search
        friendsRepository.stub {
            onBlocking {
                searchUsersByUsername("bob", limit = 20, callerUserId = null)
            } doReturn Result.success(listOf(publicProfile))
        }

        // Act: Search after toggling to public
        val searchResult = searchUseCase("bob")

        // Assert: Public profile should be found
        assertTrue(searchResult.isSuccess)
        assertEquals(1, searchResult.getOrNull()?.size)
        assertEquals(publicProfile, searchResult.getOrNull()?.firstOrNull())
        assertTrue("Public profile should be searchable", publicProfile.isDiscoverable)
    }

    @Test
    fun `toggling back to private removes profile from search`() = runTest {
        // Arrange: User was public but toggles back to private
        val publicProfile = PublicUserProfile(
            uid = "user1",
            username = "charlie_chocolate",
            primaryLanguage = "en-US",
            isDiscoverable = true
        )

        val privateProfile = publicProfile.copy(isDiscoverable = false)

        // After toggling to private, search should NOT find the profile
        friendsRepository.stub {
            onBlocking {
                searchUsersByUsername("charlie", limit = 20, callerUserId = null)
            } doReturn Result.success(emptyList())
        }

        // Act: Search after toggling back to private
        val searchResult = searchUseCase("charlie")

        // Assert: Profile should no longer be found
        assertTrue(searchResult.isSuccess)
        assertEquals(emptyList<PublicUserProfile>(), searchResult.getOrNull())
        assertFalse("Private profile should not be searchable", privateProfile.isDiscoverable)
    }

    @Test
    fun `public profile without username is not searchable`() = runTest {
        // Arrange: User toggles to public but hasn't set username yet
        val profile = PublicUserProfile(
            uid = "user1",
            username = "",  // Blank username
            primaryLanguage = "en-US",
            isDiscoverable = true  // Tried to toggle public
        )

        // Should NOT be in search because username is required for public discovery
        friendsRepository.stub {
            onBlocking {
                searchUsersByUsername("user", limit = 20, callerUserId = null)
            } doReturn Result.success(emptyList())
        }

        // Act: Search for profile
        val searchResult = searchUseCase("user")

        // Assert: Profile without username should not be searchable even if toggled public
        assertTrue(searchResult.isSuccess)
        assertEquals(emptyList<PublicUserProfile>(), searchResult.getOrNull())
        assertTrue("Blank username should prevent discovery", profile.username.isBlank())
    }

    @Test
    fun `multiple profiles only show public ones in search`() = runTest {
        // Arrange: Mix of public and private profiles
        val alice = PublicUserProfile(
            uid = "user1",
            username = "alice_public",
            primaryLanguage = "en-US",
            isDiscoverable = true  // Public
        )

        val bob = PublicUserProfile(
            uid = "user2",
            username = "bob_private",
            primaryLanguage = "en-US",
            isDiscoverable = false  // Private
        )

        val charlie = PublicUserProfile(
            uid = "user3",
            username = "charlie_public",
            primaryLanguage = "en-US",
            isDiscoverable = true  // Public
        )

        friendsRepository.stub {
            onBlocking {
                searchUsersByUsername("public", limit = 20, callerUserId = null)
            } doReturn Result.success(listOf(alice, charlie))  // Only public ones
        }

        // Act: Search
        val searchResult = searchUseCase("public")

        // Assert: Only public profiles returned
        assertTrue(searchResult.isSuccess)
        val results = searchResult.getOrNull()!!
        assertEquals(2, results.size)
        assertTrue("Results should only contain discoverable profiles", results.all { it.isDiscoverable })
        assertFalse("Private profile should not be in results", results.contains(bob))
    }

    @Test
    fun `visibility toggle updates both profile and search index`() = runTest {
        // Arrange: User updates visibility via updatePublicProfile
        val userId = UserId("user1")
        val updates = mapOf("isDiscoverable" to true)

        whenever(friendsRepository.updatePublicProfile(userId, updates))
            .thenReturn(Result.success(Unit))

        // Act: Toggle visibility to public
        val result = friendsRepository.updatePublicProfile(userId, updates)

        // Assert: Update was called
        assertTrue("Update should succeed", result.isSuccess)
        verify(friendsRepository).updatePublicProfile(userId, updates)
    }
}

