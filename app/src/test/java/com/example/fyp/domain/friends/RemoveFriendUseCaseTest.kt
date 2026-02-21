package com.example.fyp.domain.friends

import com.example.fyp.data.friends.ChatRepository
import com.example.fyp.data.friends.FriendsRepository
import com.example.fyp.model.UserId
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class RemoveFriendUseCaseTest {

    private lateinit var friendsRepository: FriendsRepository
    private lateinit var chatRepository: ChatRepository
    private lateinit var useCase: RemoveFriendUseCase

    private val currentUserId = UserId("user1")
    private val friendUserId = UserId("user2")
    private val chatId = "user1_user2"

    @Before
    fun setup() {
        friendsRepository = mock()
        chatRepository = mock()
        useCase = RemoveFriendUseCase(friendsRepository, chatRepository)
    }

    @Test
    fun `remove friend succeeds and deletes chat`() = runTest {
        whenever(friendsRepository.removeFriend(currentUserId, friendUserId))
            .thenReturn(Result.success(Unit))
        whenever(chatRepository.generateChatId(currentUserId, friendUserId))
            .thenReturn(chatId)
        whenever(chatRepository.deleteChatConversation(chatId))
            .thenReturn(Result.success(Unit))

        val result = useCase(currentUserId, friendUserId)

        assertTrue(result.isSuccess)
        verify(friendsRepository).removeFriend(currentUserId, friendUserId)
        verify(chatRepository).generateChatId(currentUserId, friendUserId)
        verify(chatRepository).deleteChatConversation(chatId)
    }

    @Test
    fun `remove friend handles repository failure and skips chat deletion`() = runTest {
        val exception = Exception("Friendship not found")
        whenever(friendsRepository.removeFriend(currentUserId, friendUserId))
            .thenReturn(Result.failure(exception))

        val result = useCase(currentUserId, friendUserId)

        assertTrue(result.isFailure)
        assertEquals("Friendship not found", result.exceptionOrNull()?.message)
        verify(chatRepository, never()).deleteChatConversation(any())
    }

    @Test
    fun `remove self as friend returns error and skips chat deletion`() = runTest {
        val userId = UserId("user1")
        whenever(friendsRepository.removeFriend(userId, userId))
            .thenReturn(Result.failure(Exception("Cannot remove yourself")))

        val result = useCase(userId, userId)

        assertTrue(result.isFailure)
        verify(chatRepository, never()).deleteChatConversation(any())
    }

    @Test
    fun `remove friend succeeds even if chat deletion fails`() = runTest {
        whenever(friendsRepository.removeFriend(currentUserId, friendUserId))
            .thenReturn(Result.success(Unit))
        whenever(chatRepository.generateChatId(currentUserId, friendUserId))
            .thenReturn(chatId)
        whenever(chatRepository.deleteChatConversation(chatId))
            .thenReturn(Result.failure(Exception("Chat deletion failed")))

        val result = useCase(currentUserId, friendUserId)

        assertTrue(result.isSuccess)
        verify(friendsRepository).removeFriend(currentUserId, friendUserId)
        verify(chatRepository).deleteChatConversation(chatId)
    }
}
