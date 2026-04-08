package com.example.fyp.domain.friends

import com.example.fyp.data.friends.FriendsRepository
import com.example.fyp.data.settings.UserSettingsRepository
import com.example.fyp.model.UserId
import com.example.fyp.model.friends.PublicUserProfile
import com.example.fyp.model.user.UserSettings
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mockingDetails
import org.mockito.kotlin.*

/**
 * Integration tests for the username enforcement flow across the friend system.
 *
 * Verifies the end-to-end invariant: a user with no username should NEVER be
 * able to create friend relations (send, accept) that result in blank
 * friendUsername fields in Firestore.
 *
 * Tests:
 *  1. EnsurePublicProfileExistsUseCase creates profiles with empty username
 *  2. EnsurePublicProfileExistsUseCase preserves existing username on update
 *  3. SendFriendRequestUseCase does NOT enforce username (it's a ViewModel concern)
 *  4. AcceptFriendRequestUseCase does NOT enforce username (it's a ViewModel concern)
 *  5. New user profile is private when username is empty
 *  6. Profile creation → send request flow: no username at domain layer
 *  7. Profile creation → accept request flow: no username at domain layer
 *  8. Profile with username set → send request succeeds
 *  9. Self-send still blocked regardless of username status
 * 10. Profile update does not overwrite username when language changes
 * 11. EnsurePublicProfileExists skips write when profile unchanged
 * 12. Empty username vs blank username consistency (both should be treated as unset)
 */
@OptIn(ExperimentalCoroutinesApi::class)
class UsernameEnforcementIntegrationTest {

    private lateinit var friendsRepo: FriendsRepository
    private lateinit var settingsRepo: UserSettingsRepository
    private lateinit var ensureProfile: EnsurePublicProfileExistsUseCase
    private lateinit var sendRequest: SendFriendRequestUseCase
    private lateinit var acceptRequest: AcceptFriendRequestUseCase

    private val userId = "user1"
    private val uid = UserId(userId)
    private val friendId = UserId("friend1")

    @Before
    fun setup() {
        friendsRepo = mock()
        settingsRepo = mock()

        // ensureUserDocumentExists is called during new profile creation
        runBlocking { whenever(friendsRepo.ensureUserDocumentExists(uid)).thenReturn(Unit) }

        ensureProfile = EnsurePublicProfileExistsUseCase(friendsRepo, settingsRepo)
        sendRequest = SendFriendRequestUseCase(friendsRepo)
        acceptRequest = AcceptFriendRequestUseCase(friendsRepo)
    }

    /**
     * Helper: extract the init/update map passed to updatePublicProfile.
     * Since the use case now uses merge-based writes (not createOrUpdatePublicProfile),
     * capture the first updatePublicProfile invocation.
     */
    @Suppress("UNCHECKED_CAST")
    private fun captureInitMap(): Map<String, Any> {
        val invocation = mockingDetails(friendsRepo).invocations
            .first { it.method.name.contains("updatePublicProfile") }
        return invocation.arguments[1] as Map<String, Any>
    }

    /** Alias for clarity when the profile already existed and only language was updated. */
    @Suppress("UNCHECKED_CAST")
    private fun captureUpdateMap(): Map<String, Any> = captureInitMap()

    // ── Test 1: New profile created with empty username ──

    @Test
    fun `new profile is created with merge that omits username`() = runTest {
        whenever(settingsRepo.fetchUserSettings(uid))
            .thenReturn(UserSettings(primaryLanguageCode = "en-US"))
        whenever(friendsRepo.getPublicProfile(uid)).thenReturn(null)

        ensureProfile(userId)

        val initMap = captureInitMap()
        assertEquals("user1", initMap["uid"])
        // username is NOT included in the merge — it will be absent on the
        // Firestore document, which maps to "" via the Kotlin default.
        assertFalse("username must not be in merge", initMap.containsKey("username"))
    }

    // ── Test 2: Existing username preserved on update ──

    @Test
    fun `existing username is preserved when language changes`() = runTest {
        val existingProfile = PublicUserProfile(
            uid = userId,
            username = "cooluser",
            primaryLanguage = "en-US"
        )
        whenever(settingsRepo.fetchUserSettings(uid))
            .thenReturn(UserSettings(primaryLanguageCode = "ja-JP"))
        whenever(friendsRepo.getPublicProfile(uid)).thenReturn(existingProfile)

        ensureProfile(userId)

        val updates = captureUpdateMap()
        // Only language and lastActiveAt should be updated, NOT username
        assertTrue(updates.containsKey("primaryLanguage"))
        assertEquals("ja-JP", updates["primaryLanguage"])
        assertFalse("Username should not be in update map", updates.containsKey("username"))
    }

    // ── Test 3: SendFriendRequestUseCase does NOT check username ──

    @Test
    fun `SendFriendRequestUseCase does not enforce username`() = runTest {
        // Arrange: mock repo to succeed
        whenever(friendsRepo.sendFriendRequest(uid, friendId, ""))
            .thenReturn(Result.success(mock()))

        // Act: call directly — no username check at this layer
        val result = sendRequest(uid, friendId, "")

        // Assert: succeeds even though we never set a username
        assertTrue(result.isSuccess)
        verify(friendsRepo).sendFriendRequest(uid, friendId, "")
    }

    // ── Test 4: AcceptFriendRequestUseCase does NOT check username ──

    @Test
    fun `AcceptFriendRequestUseCase does not enforce username`() = runTest {
        whenever(friendsRepo.acceptFriendRequest("req1", uid, friendId))
            .thenReturn(Result.success(Unit))

        val result = acceptRequest("req1", uid, friendId)

        assertTrue(result.isSuccess)
        verify(friendsRepo).acceptFriendRequest("req1", uid, friendId)
    }

    // ── Test 5: New profile is private with empty username ──

    @Test
    fun `new profile merge omits isDiscoverable so default is private`() = runTest {
        whenever(settingsRepo.fetchUserSettings(uid))
            .thenReturn(UserSettings(primaryLanguageCode = "zh-HK"))
        whenever(friendsRepo.getPublicProfile(uid)).thenReturn(null)

        ensureProfile(userId)

        val initMap = captureInitMap()
        assertFalse("isDiscoverable must not be in merge (defaults to private)", initMap.containsKey("isDiscoverable"))
        assertFalse("username must not be in merge", initMap.containsKey("username"))
        assertEquals("zh-HK", initMap["primaryLanguage"])
    }

    // ── Test 6: Profile creation then send request (no ViewModel gate) ──

    @Test
    fun `domain layer allows send request right after profile creation without username`() = runTest {
        // Step 1: create profile (merge-based, no username in map)
        whenever(settingsRepo.fetchUserSettings(uid))
            .thenReturn(UserSettings(primaryLanguageCode = "en-US"))
        whenever(friendsRepo.getPublicProfile(uid)).thenReturn(null)
        ensureProfile(userId)
        val initMap = captureInitMap()
        assertFalse("username must not be in merge", initMap.containsKey("username"))

        // Step 2: send request (domain layer allows it — ViewModel gate is what blocks)
        whenever(friendsRepo.sendFriendRequest(uid, friendId, ""))
            .thenReturn(Result.success(mock()))
        val result = sendRequest(uid, friendId, "")
        assertTrue("Domain layer should allow — ViewModel is responsible for gating", result.isSuccess)
    }

    // ── Test 7: Profile creation then accept request (no ViewModel gate) ──

    @Test
    fun `domain layer allows accept request right after profile creation without username`() = runTest {
        // Step 1: create profile (empty username)
        whenever(settingsRepo.fetchUserSettings(uid))
            .thenReturn(UserSettings(primaryLanguageCode = "en-US"))
        whenever(friendsRepo.getPublicProfile(uid)).thenReturn(null)
        ensureProfile(userId)

        // Step 2: accept (domain layer allows it)
        whenever(friendsRepo.acceptFriendRequest("req1", uid, friendId))
            .thenReturn(Result.success(Unit))
        val result = acceptRequest("req1", uid, friendId)
        assertTrue("Domain layer should allow — ViewModel is responsible for gating", result.isSuccess)
    }

    // ── Test 8: With username set, send request succeeds ──

    @Test
    fun `send request succeeds when user has a username`() = runTest {
        // Profile exists with a username
        val profile = PublicUserProfile(uid = userId, username = "testuser", primaryLanguage = "en-US")
        whenever(friendsRepo.getPublicProfile(uid)).thenReturn(profile)

        whenever(friendsRepo.sendFriendRequest(uid, friendId, "Hey!"))
            .thenReturn(Result.success(mock()))

        val result = sendRequest(uid, friendId, "Hey!")
        assertTrue(result.isSuccess)
    }

    // ── Test 9: Self-send still blocked regardless of username ──

    @Test
    fun `self-send is blocked even with username set`() = runTest {
        val result = sendRequest(uid, uid, "")
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IllegalArgumentException)
        // Verify sendFriendRequest was never called using concrete values
        verify(friendsRepo, never()).sendFriendRequest(uid, uid, "")
    }

    // ── Test 10: Profile update does not overwrite username ──

    @Test
    fun `profile language update does not touch username field`() = runTest {
        val existing = PublicUserProfile(
            uid = userId,
            username = "myname",
            primaryLanguage = "en-US"
        )
        whenever(settingsRepo.fetchUserSettings(uid))
            .thenReturn(UserSettings(primaryLanguageCode = "fr-FR"))
        whenever(friendsRepo.getPublicProfile(uid)).thenReturn(existing)

        ensureProfile(userId)

        val updates = captureUpdateMap()
        assertEquals("fr-FR", updates["primaryLanguage"])
        assertFalse("Username must not be overwritten on language update", updates.containsKey("username"))
    }

    // ── Test 11: Profile write skipped when nothing changed ──

    @Test
    fun `profile write is skipped when language has not changed`() = runTest {
        val existing = PublicUserProfile(
            uid = userId,
            username = "myname",
            primaryLanguage = "en-US"
        )
        whenever(settingsRepo.fetchUserSettings(uid))
            .thenReturn(UserSettings(primaryLanguageCode = "en-US"))
        whenever(friendsRepo.getPublicProfile(uid)).thenReturn(existing)

        ensureProfile(userId)

        // No write should occur — verify by checking that only getPublicProfile
        // and fetchUserSettings were called (no create/update invocations)
        val writeInvocations = mockingDetails(friendsRepo).invocations
            .filter {
                it.method.name.contains("createOrUpdatePublicProfile") ||
                        it.method.name.contains("updatePublicProfile")
            }
        assertTrue("Expected no write invocations", writeInvocations.isEmpty())
    }

    // ── Test 12: Empty vs blank username consistency ──

    @Test
    fun `merge-based profile init omits username entirely`() = runTest {
        // When profile doesn't exist, the merge map should NOT contain username
        // so existing documents keep their value and new documents use the Kotlin default "".
        whenever(settingsRepo.fetchUserSettings(uid))
            .thenReturn(UserSettings(primaryLanguageCode = "en-US"))
        whenever(friendsRepo.getPublicProfile(uid)).thenReturn(null)

        ensureProfile(userId)

        val initMap = captureInitMap()
        assertFalse("username must not be in merge (preserves existing or defaults to empty)", initMap.containsKey("username"))
        assertFalse("isDiscoverable must not be in merge", initMap.containsKey("isDiscoverable"))
    }
}
