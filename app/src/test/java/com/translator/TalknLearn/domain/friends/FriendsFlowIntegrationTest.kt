package com.translator.TalknLearn.domain.friends

import com.translator.TalknLearn.data.friends.ChatRepository
import com.translator.TalknLearn.data.friends.FriendsRepository
import com.translator.TalknLearn.model.UserId
import com.translator.TalknLearn.model.friends.FriendRequest
import com.translator.TalknLearn.model.friends.RequestStatus
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.*

/**
 * Integration-style tests for the friends feature flow:
 * SendFriendRequest → AcceptFriendRequest → SendMessage → MarkMessagesAsRead
 *
 * Verifies that the use cases interact with repositories correctly
 * through the complete friendship lifecycle.
 *
 * Note: UserId is a Kotlin value class (inlined to String at runtime).
 * Mockito's `any()` returns null which triggers the value class `require` check.
 * We use concrete UserId values with `eq()` or direct stubbing to avoid NPEs.
 *
 * Tests:
 *  1. Send friend request delegates to repository
 *  2. Send request to self fails with IllegalArgumentException
 *  3. Accept friend request delegates with proper IDs
 *  4. Reject friend request delegates with requestId
 *  5. Send message delegates to chat repository
 *  6. Send blank message fails
 *  7. Send whitespace-only message fails
 *  8. Send request failure propagates error
 *  9. Accept request failure propagates error
 */
@OptIn(ExperimentalCoroutinesApi::class)
class FriendsFlowIntegrationTest {

    private lateinit var friendsRepo: FriendsRepository
    private lateinit var chatRepo: ChatRepository
    private lateinit var sendFriendRequest: SendFriendRequestUseCase
    private lateinit var acceptFriendRequest: AcceptFriendRequestUseCase
    private lateinit var rejectFriendRequest: RejectFriendRequestUseCase
    private lateinit var sendMessage: SendMessageUseCase

    private val userA = UserId("userA")
    private val userB = UserId("userB")

    @Before
    fun setup() {
        friendsRepo = mock()
        chatRepo = mock()

        sendFriendRequest = SendFriendRequestUseCase(friendsRepo)
        acceptFriendRequest = AcceptFriendRequestUseCase(friendsRepo)
        rejectFriendRequest = RejectFriendRequestUseCase(friendsRepo)
        sendMessage = SendMessageUseCase(chatRepo)
    }

    // ── Test 1: Send friend request ──

    @Test
    fun `send friend request delegates to repository`() = runTest {
        val mockRequest = FriendRequest(
            requestId = "req1",
            fromUserId = "userA",
            toUserId = "userB",
            status = RequestStatus.PENDING
        )
        whenever(friendsRepo.sendFriendRequest(userA, userB, "Hi!"))
            .thenReturn(Result.success(mockRequest))

        val result = sendFriendRequest(userA, userB, "Hi!")

        assertTrue(result.isSuccess)
    }

    // ── Test 2: Send request to self fails ──

    @Test
    fun `send friend request to self fails`() = runTest {
        val result = sendFriendRequest(userA, userA, "")

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IllegalArgumentException)
    }

    // ── Test 3: Accept friend request ──

    @Test
    fun `accept friend request delegates with proper IDs`() = runTest {
        whenever(friendsRepo.acceptFriendRequest("req1", userB, userA))
            .thenReturn(Result.success(Unit))

        val result = acceptFriendRequest("req1", userB, userA)

        assertTrue(result.isSuccess)
    }

    // ── Test 4: Reject friend request ──

    @Test
    fun `reject friend request delegates with requestId`() = runTest {
        whenever(friendsRepo.rejectFriendRequest("req1"))
            .thenReturn(Result.success(Unit))

        val result = rejectFriendRequest("req1")

        assertTrue(result.isSuccess)
    }

    // ── Test 5: Send message ──

    @Test
    fun `send message delegates to chat repository`() = runTest {
        whenever(chatRepo.sendMessage("chatAB", userA, userB, "Hello!"))
            .thenReturn(Result.success(Unit))

        val result = sendMessage("chatAB", userA, userB, "Hello!")

        assertTrue(result.isSuccess)
    }

    // ── Test 6: Blank message fails ──

    @Test
    fun `send blank message fails`() = runTest {
        val result = sendMessage("chatAB", userA, userB, "")

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IllegalArgumentException)
    }

    // ── Test 7: Whitespace-only message fails ──

    @Test
    fun `send whitespace-only message fails`() = runTest {
        val result = sendMessage("chatAB", userA, userB, "   ")

        assertTrue(result.isFailure)
    }

    // ── Test 8: Send request failure propagates ──

    @Test
    fun `send request failure propagates error`() = runTest {
        whenever(friendsRepo.sendFriendRequest(userA, userB, ""))
            .thenReturn(Result.failure(RuntimeException("Blocked")))

        val result = sendFriendRequest(userA, userB, "")

        assertTrue(result.isFailure)
    }

    // ── Test 9: Accept request failure propagates ──

    @Test
    fun `accept request failure propagates error`() = runTest {
        whenever(friendsRepo.acceptFriendRequest("req1", userB, userA))
            .thenReturn(Result.failure(RuntimeException("Firestore error")))

        val result = acceptFriendRequest("req1", userB, userA)

        assertTrue(result.isFailure)
    }
}
