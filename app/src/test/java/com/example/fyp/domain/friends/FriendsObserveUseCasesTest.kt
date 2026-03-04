package com.example.fyp.domain.friends

import com.example.fyp.data.friends.ChatRepository
import com.example.fyp.data.friends.FriendsRepository
import com.example.fyp.data.friends.SharingRepository
import com.example.fyp.model.UserId
import com.example.fyp.model.friends.FriendMessage
import com.example.fyp.model.friends.FriendRelation
import com.example.fyp.model.friends.FriendRequest
import com.example.fyp.model.friends.SharedItem
import com.example.fyp.model.friends.SharedItemType
import com.google.firebase.Timestamp
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

/**
 * Unit tests for friends observe and sharing use cases:
 * - ObserveFriendsUseCase
 * - ObserveSharedInboxUseCase
 * - ObserveMessagesUseCase
 * - ObserveOutgoingRequestsUseCase
 * - ObserveIncomingRequestsUseCase
 * - ShareLearningMaterialUseCase
 */
class FriendsObserveUseCasesTest {

    private lateinit var friendsRepository: FriendsRepository
    private lateinit var chatRepository: ChatRepository
    private lateinit var sharingRepository: SharingRepository

    @Before
    fun setup() {
        friendsRepository = mock()
        chatRepository = mock()
        sharingRepository = mock()
    }

    // ── ObserveFriendsUseCase ───────────────────────────────────────

    @Test
    fun `ObserveFriends returns flow from repository`() = runTest {
        val friends = listOf(
            FriendRelation(friendId = "f1", friendUsername = "Alice"),
            FriendRelation(friendId = "f2", friendUsername = "Bob")
        )
        whenever(friendsRepository.observeFriends(UserId("user1")))
            .thenReturn(flowOf(friends))

        val useCase = ObserveFriendsUseCase(friendsRepository)
        val result = useCase(UserId("user1")).first()

        assertEquals(2, result.size)
        assertEquals("Alice", result[0].friendUsername)
        assertEquals("Bob", result[1].friendUsername)
    }

    @Test
    fun `ObserveFriends returns empty list when no friends`() = runTest {
        whenever(friendsRepository.observeFriends(UserId("user1")))
            .thenReturn(flowOf(emptyList()))

        val useCase = ObserveFriendsUseCase(friendsRepository)
        val result = useCase(UserId("user1")).first()

        assertTrue(result.isEmpty())
    }

    // ── ObserveSharedInboxUseCase ───────────────────────────────────

    @Test
    fun `ObserveSharedInbox returns flow from repository`() = runTest {
        val items = listOf(
            SharedItem(
                itemId = "item1",
                fromUserId = "sender1",
                toUserId = "user1",
                type = SharedItemType.WORD
            )
        )
        whenever(sharingRepository.observeSharedInbox(UserId("user1")))
            .thenReturn(flowOf(items))

        val useCase = ObserveSharedInboxUseCase(sharingRepository)
        val result = useCase(UserId("user1")).first()

        assertEquals(1, result.size)
        assertEquals("item1", result[0].itemId)
    }

    // ── ObserveMessagesUseCase ──────────────────────────────────────

    @Test
    fun `ObserveMessages generates chatId and returns messages`() = runTest {
        val messages = listOf(
            FriendMessage(messageId = "m1", chatId = "u1_u2", senderId = "u1", content = "Hi")
        )
        whenever(chatRepository.generateChatId(UserId("u1"), UserId("u2")))
            .thenReturn("u1_u2")
        whenever(chatRepository.observeMessages("u1_u2"))
            .thenReturn(flowOf(messages))

        val useCase = ObserveMessagesUseCase(chatRepository)
        val result = useCase(UserId("u1"), UserId("u2")).first()

        assertEquals(1, result.size)
        assertEquals("Hi", result[0].content)
    }

    @Test
    fun `ObserveMessages filters by clearedAt timestamp`() = runTest {
        val oldTimestamp = Timestamp(1000, 0)
        val newTimestamp = Timestamp(2000, 0)
        val clearTimestamp = Timestamp(1500, 0)

        val messages = listOf(
            FriendMessage(messageId = "m1", chatId = "u1_u2", senderId = "u1", content = "old", createdAt = oldTimestamp),
            FriendMessage(messageId = "m2", chatId = "u1_u2", senderId = "u1", content = "new", createdAt = newTimestamp)
        )
        whenever(chatRepository.generateChatId(UserId("u1"), UserId("u2")))
            .thenReturn("u1_u2")
        whenever(chatRepository.observeMessages("u1_u2"))
            .thenReturn(flowOf(messages))

        val useCase = ObserveMessagesUseCase(chatRepository)
        val result = useCase(UserId("u1"), UserId("u2"), clearedAt = clearTimestamp).first()

        assertEquals(1, result.size)
        assertEquals("new", result[0].content)
    }

    @Test
    fun `ObserveMessages returns all when clearedAt is null`() = runTest {
        val messages = listOf(
            FriendMessage(messageId = "m1", content = "A"),
            FriendMessage(messageId = "m2", content = "B")
        )
        whenever(chatRepository.generateChatId(UserId("u1"), UserId("u2")))
            .thenReturn("u1_u2")
        whenever(chatRepository.observeMessages("u1_u2"))
            .thenReturn(flowOf(messages))

        val useCase = ObserveMessagesUseCase(chatRepository)
        val result = useCase(UserId("u1"), UserId("u2"), clearedAt = null).first()

        assertEquals(2, result.size)
    }

    // ── ObserveOutgoingRequestsUseCase ──────────────────────────────

    @Test
    fun `ObserveOutgoingRequests returns flow from repository`() = runTest {
        val requests = listOf(
            FriendRequest(requestId = "r1", fromUserId = "user1", toUserId = "target1")
        )
        whenever(friendsRepository.observeOutgoingRequests(UserId("user1")))
            .thenReturn(flowOf(requests))

        val useCase = ObserveOutgoingRequestsUseCase(friendsRepository)
        val result = useCase(UserId("user1")).first()

        assertEquals(1, result.size)
        assertEquals("target1", result[0].toUserId)
    }

    // ── ObserveIncomingRequestsUseCase ──────────────────────────────

    @Test
    fun `ObserveIncomingRequests returns flow from repository`() = runTest {
        val requests = listOf(
            FriendRequest(requestId = "r1", fromUserId = "sender1", toUserId = "user1"),
            FriendRequest(requestId = "r2", fromUserId = "sender2", toUserId = "user1")
        )
        whenever(friendsRepository.observeIncomingRequests(UserId("user1")))
            .thenReturn(flowOf(requests))

        val useCase = ObserveIncomingRequestsUseCase(friendsRepository)
        val result = useCase(UserId("user1")).first()

        assertEquals(2, result.size)
    }

    // ── ShareLearningMaterialUseCase ────────────────────────────────

    @Test
    fun `ShareLearningMaterial delegates to repository with correct data`() = runTest {
        val sharedItem = SharedItem(
            itemId = "share1",
            fromUserId = "user1",
            toUserId = "friend1",
            type = SharedItemType.LEARNING_SHEET
        )
        val expectedMaterialData = mapOf(
            "materialId" to "mat1",
            "title" to "Japanese Basics",
            "description" to "Learn Japanese",
            "fullContent" to "Full content here"
        )
        whenever(
            sharingRepository.shareLearningMaterial(
                UserId("user1"),
                "myuser",
                UserId("friend1"),
                SharedItemType.LEARNING_SHEET,
                expectedMaterialData
            )
        ).thenReturn(Result.success(sharedItem))

        val useCase = ShareLearningMaterialUseCase(sharingRepository)
        val result = useCase(
            fromUserId = UserId("user1"),
            fromUsername = "myuser",
            toUserId = UserId("friend1"),
            type = SharedItemType.LEARNING_SHEET,
            materialId = "mat1",
            title = "Japanese Basics",
            description = "Learn Japanese",
            fullContent = "Full content here"
        )

        assertTrue(result.isSuccess)
        assertEquals("share1", result.getOrNull()?.itemId)
    }

    @Test
    fun `ShareLearningMaterial handles repository failure`() = runTest {
        whenever(
            sharingRepository.shareLearningMaterial(
                UserId("user1"),
                "myuser",
                UserId("friend1"),
                SharedItemType.QUIZ,
                mapOf(
                    "materialId" to "quiz1",
                    "title" to "Quiz",
                    "description" to "",
                    "fullContent" to ""
                )
            )
        ).thenReturn(Result.failure(RuntimeException("Network error")))

        val useCase = ShareLearningMaterialUseCase(sharingRepository)
        val result = useCase(
            fromUserId = UserId("user1"),
            fromUsername = "myuser",
            toUserId = UserId("friend1"),
            type = SharedItemType.QUIZ,
            materialId = "quiz1",
            title = "Quiz"
        )

        assertTrue(result.isFailure)
        assertEquals("Network error", result.exceptionOrNull()?.message)
    }
}
