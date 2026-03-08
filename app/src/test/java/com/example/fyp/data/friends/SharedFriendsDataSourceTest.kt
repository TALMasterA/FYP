package com.example.fyp.data.friends

import android.content.Context
import android.content.SharedPreferences
import com.example.fyp.model.friends.FriendRelation
import com.example.fyp.model.friends.FriendRequest
import com.example.fyp.model.friends.SharedItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

/**
 * Unit tests for [SharedFriendsDataSource] focusing on in-memory logic.
 *
 * Android-dependent persistence (SeenItemsStorage) is handled by mocking
 * the Context/SharedPreferences chain so that background IO coroutines
 * do not crash.  All assertions target the synchronous, in-memory state.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class SharedFriendsDataSourceTest {

    private lateinit var ds: SharedFriendsDataSource
    private lateinit var mockContext: Context
    private lateinit var mockFriendsRepo: FriendsRepository
    private lateinit var mockSharingRepo: SharingRepository

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        // Mock SharedPreferences chain so SeenItemsStorage calls don't crash
        val mockEditor: SharedPreferences.Editor = mock()
        whenever(mockEditor.putString(any(), any())).thenReturn(mockEditor)
        whenever(mockEditor.remove(any())).thenReturn(mockEditor)

        val mockPrefs: SharedPreferences = mock()
        whenever(mockPrefs.getString(any(), anyOrNull())).thenReturn(null)
        whenever(mockPrefs.edit()).thenReturn(mockEditor)

        mockContext = mock()
        whenever(mockContext.getSharedPreferences(any(), any<Int>())).thenReturn(mockPrefs)

        mockFriendsRepo = mock()
        mockSharingRepo = mock()

        ds = SharedFriendsDataSource(mockContext, mockFriendsRepo, mockSharingRepo)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // ── Reflection helpers ──────────────────────────────────────────────────

    @Suppress("UNCHECKED_CAST")
    private fun <T> getPrivateStateFlow(fieldName: String): MutableStateFlow<T> {
        val field = SharedFriendsDataSource::class.java.getDeclaredField(fieldName)
        field.isAccessible = true
        return field.get(ds) as MutableStateFlow<T>
    }

    private fun setCurrentUserId(userId: String?) {
        val field = SharedFriendsDataSource::class.java.getDeclaredField("currentUserId")
        field.isAccessible = true
        field.set(ds, userId)
    }

    // ── stopObserving ───────────────────────────────────────────────────────

    @Test
    fun `stopObserving clears friends list`() {
        getPrivateStateFlow<List<FriendRelation>>("_friends").value = listOf(
            FriendRelation(friendId = "f1", friendUsername = "Alice")
        )

        ds.stopObserving()

        assertTrue(ds.friends.value.isEmpty())
    }

    @Test
    fun `stopObserving clears incoming requests`() {
        getPrivateStateFlow<List<FriendRequest>>("_incomingRequests").value = listOf(
            FriendRequest(requestId = "r1")
        )

        ds.stopObserving()

        assertTrue(ds.incomingRequests.value.isEmpty())
    }

    @Test
    fun `stopObserving clears shared items`() {
        getPrivateStateFlow<List<SharedItem>>("_pendingSharedItems").value = listOf(
            SharedItem(itemId = "item1")
        )

        ds.stopObserving()

        assertTrue(ds.pendingSharedItems.value.isEmpty())
    }

    @Test
    fun `stopObserving clears seen sets`() {
        getPrivateStateFlow<Set<String>>("_seenSharedItemIds").value = setOf("s1", "s2")
        getPrivateStateFlow<Set<String>>("_seenFriendRequestIds").value = setOf("r1")
        getPrivateStateFlow<Set<String>>("_seenMessageFriendIds").value = setOf("f1")

        ds.stopObserving()

        assertTrue(getPrivateStateFlow<Set<String>>("_seenSharedItemIds").value.isEmpty())
        assertTrue(getPrivateStateFlow<Set<String>>("_seenFriendRequestIds").value.isEmpty())
        assertTrue(getPrivateStateFlow<Set<String>>("_seenMessageFriendIds").value.isEmpty())
    }

    @Test
    fun `stopObserving clears username cache`() {
        ds.cacheOwnUsername("user1", "TestUser")
        assertEquals("TestUser", ds.getCachedUsername("user1"))

        ds.stopObserving()

        assertNull(ds.getCachedUsername("user1"))
    }

    @Test
    fun `stopObserving clears rawUnreadPerFriend`() {
        getPrivateStateFlow<Map<String, Int>>("_rawUnreadPerFriend").value =
            mapOf("f1" to 3, "f2" to 1)

        ds.stopObserving()

        assertTrue(getPrivateStateFlow<Map<String, Int>>("_rawUnreadPerFriend").value.isEmpty())
    }

    // ── cacheOwnUsername / getCachedUsername ─────────────────────────────────

    @Test
    fun `cacheOwnUsername stores username`() {
        ds.cacheOwnUsername("user1", "Alice")

        assertEquals("Alice", ds.getCachedUsername("user1"))
    }

    @Test
    fun `getCachedUsername returns null for unknown user`() {
        assertNull(ds.getCachedUsername("nonexistent"))
    }

    @Test
    fun `getCachedUsername returns cached value after multiple caches`() {
        ds.cacheOwnUsername("u1", "Bob")
        ds.cacheOwnUsername("u2", "Carol")

        assertEquals("Bob", ds.getCachedUsername("u1"))
        assertEquals("Carol", ds.getCachedUsername("u2"))
    }

    @Test
    fun `cacheOwnUsername ignores blank username`() {
        ds.cacheOwnUsername("user1", "   ")

        assertNull(ds.getCachedUsername("user1"))
    }

    @Test
    fun `cacheOwnUsername ignores empty username`() {
        ds.cacheOwnUsername("user1", "")

        assertNull(ds.getCachedUsername("user1"))
    }

    @Test
    fun `cacheOwnUsername overwrites previous value`() {
        ds.cacheOwnUsername("user1", "OldName")
        ds.cacheOwnUsername("user1", "NewName")

        assertEquals("NewName", ds.getCachedUsername("user1"))
    }

    // ── isFriend ────────────────────────────────────────────────────────────

    @Test
    fun `isFriend returns false when friends list empty`() {
        assertFalse(ds.isFriend("someone"))
    }

    @Test
    fun `isFriend returns true when friend exists`() {
        getPrivateStateFlow<List<FriendRelation>>("_friends").value = listOf(
            FriendRelation(friendId = "friend1", friendUsername = "Alice"),
            FriendRelation(friendId = "friend2", friendUsername = "Bob")
        )

        assertTrue(ds.isFriend("friend1"))
        assertTrue(ds.isFriend("friend2"))
    }

    @Test
    fun `isFriend returns false when friend not in list`() {
        getPrivateStateFlow<List<FriendRelation>>("_friends").value = listOf(
            FriendRelation(friendId = "friend1", friendUsername = "Alice")
        )

        assertFalse(ds.isFriend("stranger"))
    }

    // ── applyUsernameUpdates ────────────────────────────────────────────────

    @Test
    fun `applyUsernameUpdates updates friend display names`() {
        getPrivateStateFlow<List<FriendRelation>>("_friends").value = listOf(
            FriendRelation(friendId = "f1", friendUsername = "OldName"),
            FriendRelation(friendId = "f2", friendUsername = "Unchanged")
        )

        ds.applyUsernameUpdates(mapOf("f1" to "NewName"))

        val friends = ds.friends.value
        assertEquals("NewName", friends.find { it.friendId == "f1" }?.friendUsername)
        assertEquals("Unchanged", friends.find { it.friendId == "f2" }?.friendUsername)
    }

    @Test
    fun `applyUsernameUpdates with empty map does nothing`() {
        val original = listOf(
            FriendRelation(friendId = "f1", friendUsername = "Alice")
        )
        getPrivateStateFlow<List<FriendRelation>>("_friends").value = original

        ds.applyUsernameUpdates(emptyMap())

        assertEquals(original, ds.friends.value)
    }

    @Test
    fun `applyUsernameUpdates also updates username cache`() {
        getPrivateStateFlow<List<FriendRelation>>("_friends").value = listOf(
            FriendRelation(friendId = "f1", friendUsername = "OldName")
        )

        ds.applyUsernameUpdates(mapOf("f1" to "NewName"))

        assertEquals("NewName", ds.getCachedUsername("f1"))
    }

    @Test
    fun `applyUsernameUpdates skips friend whose name has not changed`() {
        val original = listOf(
            FriendRelation(friendId = "f1", friendUsername = "SameName")
        )
        getPrivateStateFlow<List<FriendRelation>>("_friends").value = original

        ds.applyUsernameUpdates(mapOf("f1" to "SameName"))

        // The list reference should be unchanged when no actual update occurs
        assertEquals("SameName", ds.friends.value[0].friendUsername)
    }

    @Test
    fun `applyUsernameUpdates ignores updates for non-existent friends`() {
        getPrivateStateFlow<List<FriendRelation>>("_friends").value = listOf(
            FriendRelation(friendId = "f1", friendUsername = "Alice")
        )

        ds.applyUsernameUpdates(mapOf("nonexistent" to "Ghost"))

        assertEquals(1, ds.friends.value.size)
        assertEquals("Alice", ds.friends.value[0].friendUsername)
    }

    // ── updateRawUnreadPerFriend ────────────────────────────────────────────

    @Test
    fun `updateRawUnreadPerFriend removes friends from seen set when they have new messages`() {
        // Pre-populate the seen set: friend1 and friend2 marked as seen
        getPrivateStateFlow<Set<String>>("_seenMessageFriendIds").value =
            setOf("friend1", "friend2")

        // friend1 now has a new unread message
        ds.updateRawUnreadPerFriend(mapOf("friend1" to 1))

        val seenSet = getPrivateStateFlow<Set<String>>("_seenMessageFriendIds").value
        assertFalse("friend1 should be removed from seen set", seenSet.contains("friend1"))
        assertTrue("friend2 should remain in seen set", seenSet.contains("friend2"))
    }

    @Test
    fun `updateRawUnreadPerFriend keeps seen set intact when unread count is zero`() {
        getPrivateStateFlow<Set<String>>("_seenMessageFriendIds").value =
            setOf("friend1", "friend2")

        // friend1 has zero unread -- no new messages
        ds.updateRawUnreadPerFriend(mapOf("friend1" to 0))

        val seenSet = getPrivateStateFlow<Set<String>>("_seenMessageFriendIds").value
        assertTrue(seenSet.contains("friend1"))
        assertTrue(seenSet.contains("friend2"))
    }

    @Test
    fun `updateRawUnreadPerFriend with empty map leaves seen set unchanged`() {
        getPrivateStateFlow<Set<String>>("_seenMessageFriendIds").value =
            setOf("friend1")

        ds.updateRawUnreadPerFriend(emptyMap())

        val seenSet = getPrivateStateFlow<Set<String>>("_seenMessageFriendIds").value
        assertTrue(seenSet.contains("friend1"))
    }

    @Test
    fun `updateRawUnreadPerFriend removes multiple friends with new messages`() {
        getPrivateStateFlow<Set<String>>("_seenMessageFriendIds").value =
            setOf("f1", "f2", "f3")

        ds.updateRawUnreadPerFriend(mapOf("f1" to 2, "f2" to 5, "f3" to 0))

        val seenSet = getPrivateStateFlow<Set<String>>("_seenMessageFriendIds").value
        assertFalse(seenSet.contains("f1"))
        assertFalse(seenSet.contains("f2"))
        assertTrue("f3 has 0 unread, should stay seen", seenSet.contains("f3"))
    }

    // ── markSharedItemsSeen ─────────────────────────────────────────────────

    @Test
    fun `markSharedItemsSeen adds current item IDs to seen set`() {
        setCurrentUserId("testUser")
        getPrivateStateFlow<List<SharedItem>>("_pendingSharedItems").value = listOf(
            SharedItem(itemId = "item1"),
            SharedItem(itemId = "item2")
        )

        ds.markSharedItemsSeen()

        val seenIds = getPrivateStateFlow<Set<String>>("_seenSharedItemIds").value
        assertTrue(seenIds.contains("item1"))
        assertTrue(seenIds.contains("item2"))
    }

    @Test
    fun `markSharedItemsSeen does nothing when currentUserId is null`() {
        // currentUserId is null by default
        getPrivateStateFlow<List<SharedItem>>("_pendingSharedItems").value = listOf(
            SharedItem(itemId = "item1")
        )

        ds.markSharedItemsSeen()

        assertTrue(getPrivateStateFlow<Set<String>>("_seenSharedItemIds").value.isEmpty())
    }

    // ── markFriendRequestsSeen ──────────────────────────────────────────────

    @Test
    fun `markFriendRequestsSeen adds current request IDs to seen set`() {
        setCurrentUserId("testUser")
        getPrivateStateFlow<List<FriendRequest>>("_incomingRequests").value = listOf(
            FriendRequest(requestId = "req1"),
            FriendRequest(requestId = "req2")
        )

        ds.markFriendRequestsSeen()

        val seenIds = getPrivateStateFlow<Set<String>>("_seenFriendRequestIds").value
        assertTrue(seenIds.contains("req1"))
        assertTrue(seenIds.contains("req2"))
    }

    @Test
    fun `markFriendRequestsSeen does nothing when currentUserId is null`() {
        getPrivateStateFlow<List<FriendRequest>>("_incomingRequests").value = listOf(
            FriendRequest(requestId = "req1")
        )

        ds.markFriendRequestsSeen()

        assertTrue(getPrivateStateFlow<Set<String>>("_seenFriendRequestIds").value.isEmpty())
    }

    // ── markMessageFriendSeen ───────────────────────────────────────────────

    @Test
    fun `markMessageFriendSeen adds friend to seen set`() {
        setCurrentUserId("testUser")

        ds.markMessageFriendSeen("friend1")

        val seenIds = getPrivateStateFlow<Set<String>>("_seenMessageFriendIds").value
        assertTrue(seenIds.contains("friend1"))
    }

    @Test
    fun `markMessageFriendSeen does nothing when currentUserId is null`() {
        ds.markMessageFriendSeen("friend1")

        assertTrue(getPrivateStateFlow<Set<String>>("_seenMessageFriendIds").value.isEmpty())
    }

    @Test
    fun `markMessageFriendSeen accumulates multiple friend IDs`() {
        setCurrentUserId("testUser")

        ds.markMessageFriendSeen("friend1")
        ds.markMessageFriendSeen("friend2")

        val seenIds = getPrivateStateFlow<Set<String>>("_seenMessageFriendIds").value
        assertEquals(2, seenIds.size)
        assertTrue(seenIds.contains("friend1"))
        assertTrue(seenIds.contains("friend2"))
    }
}
