package com.example.fyp.data.friends

import android.content.Context
import android.util.Log
import com.example.fyp.model.UserId
import com.example.fyp.model.friends.FriendRelation
import com.example.fyp.model.friends.FriendRequest
import com.example.fyp.model.friends.SharedItem
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Shared data source for friend system data.
 *
 * Mirrors the SharedHistoryDataSource pattern: a single @Singleton that holds
 * one Firestore real-time listener per collection.  Multiple ViewModels
 * (FriendsViewModel, SharedInboxViewModel) AND AppViewModel (notification
 * badges) all read from the same in-memory StateFlows instead of each
 * creating their own listeners — reducing Firestore reads significantly.
 *
 * Collections observed (one listener each):
 *  - friends list
 *  - incoming friend requests
 *  - shared inbox (pending items only)
 *
 * Unread message count is NOT observed here; it is maintained via the
 * users/{userId}.totalUnreadMessages field updated atomically by
 * FirestoreChatRepository, and observed as a single-document listener
 * in that repository.
 */
@Singleton
class SharedFriendsDataSource @Inject constructor(
    @ApplicationContext private val context: Context,
    private val friendsRepository: FriendsRepository,
    private val sharingRepository: SharingRepository
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    private var currentUserId: String? = null
    private var friendsJob: Job? = null
    private var requestsJob: Job? = null
    private var inboxJob: Job? = null

    // ── Shared state ─────────────────────────────────────────────────────────

    private val _friends = MutableStateFlow<List<FriendRelation>>(emptyList())
    val friends: StateFlow<List<FriendRelation>> = _friends.asStateFlow()

    private val _incomingRequests = MutableStateFlow<List<FriendRequest>>(emptyList())
    val incomingRequests: StateFlow<List<FriendRequest>> = _incomingRequests.asStateFlow()

    private val _pendingSharedItems = MutableStateFlow<List<SharedItem>>(emptyList())
    val pendingSharedItems: StateFlow<List<SharedItem>> = _pendingSharedItems.asStateFlow()

    // ── Seen/unread tracking for notification badge ───────────────────────────

    /** IDs of shared inbox items the user has already seen (opened the inbox). */
    private val _seenSharedItemIds = MutableStateFlow<Set<String>>(emptySet())

    /** IDs of friend requests the user has already seen (opened Friends screen). */
    private val _seenFriendRequestIds = MutableStateFlow<Set<String>>(emptySet())

    /**
     * IDs of friends whose messages the user has already seen (opened their chat).
     * Persisted across app restarts so red dots don't reappear for already-read chats.
     */
    private val _seenMessageFriendIds = MutableStateFlow<Set<String>>(emptySet())

    /**
     * Raw per-friend unread counts fed from the Firestore listener in AppViewModel/FriendsViewModel.
     * Stored here so the seen-message filter can be applied centrally.
     */
    private val _rawUnreadPerFriend = MutableStateFlow<Map<String, Int>>(emptyMap())

    /**
     * Per-friend unread counts filtered by [_seenMessageFriendIds].
     * A friend's unread count is zeroed out if the user has already opened that chat
     * (i.e. their ID is in the seen set). This ensures red dots don't reappear on
     * app restart for chats the user has already read.
     */
    val unseenUnreadPerFriend: kotlinx.coroutines.flow.Flow<Map<String, Int>> =
        kotlinx.coroutines.flow.combine(_rawUnreadPerFriend, _seenMessageFriendIds) { raw, seen ->
            raw.mapValues { (friendId, count) -> if (friendId in seen) 0 else count }
                .filter { it.value > 0 }
        }

    /**
     * Whether there are unseen shared inbox items.
     * Uses persisted seen IDs, so items already viewed by the same user/device remain
     * hidden after app restart or logout/login.
     */
    val hasUnseenSharedItems: kotlinx.coroutines.flow.Flow<Boolean> =
        kotlinx.coroutines.flow.combine(_pendingSharedItems, _seenSharedItemIds) { items, seen ->
            items.any { it.itemId !in seen }
        }

    /** Count of unseen shared inbox items (for accurate badge display). */
    val unseenSharedItemsCount: kotlinx.coroutines.flow.Flow<Int> =
        kotlinx.coroutines.flow.combine(_pendingSharedItems, _seenSharedItemIds) { items, seen ->
            items.count { it.itemId !in seen }
        }

    /** Count of unseen friend requests (persisted across app restarts). */
    val unseenFriendRequestCount: kotlinx.coroutines.flow.Flow<Int> =
        kotlinx.coroutines.flow.combine(_incomingRequests, _seenFriendRequestIds) { requests, seen ->
            requests.count { it.requestId !in seen }
        }

    /**
     * Call this when the user opens the Shared Inbox screen so all currently
     * pending items are considered "seen" and the notification badge clears.
     *
     * **Persistence:** Seen item IDs are saved to SharedPreferences so the red dot
     * does not reappear on app restart for items the user has already viewed.
     */
    fun markSharedItemsSeen() {
        val userId = currentUserId ?: return
        val currentIds = _pendingSharedItems.value.map { it.itemId }.toSet()
        _seenSharedItemIds.value = _seenSharedItemIds.value + currentIds

        // Persist to storage so red dots don't reappear on app restart
        scope.launch(Dispatchers.IO) {
            SeenItemsStorage.saveSeenItemIds(context, userId, _seenSharedItemIds.value)
        }
    }

    /**
     * Call this when the user opens the Friends screen so all currently
     * visible friend requests are considered "seen" and the badge count reflects only new ones.
     *
     * **Persistence:** Seen request IDs are saved to SharedPreferences so the badge
     * shows only truly NEW requests after app restart.
     */
    fun markFriendRequestsSeen() {
        val userId = currentUserId ?: return
        val currentIds = _incomingRequests.value.map { it.requestId }.toSet()
        _seenFriendRequestIds.value = _seenFriendRequestIds.value + currentIds

        // Persist to storage so badges don't reappear on app restart
        scope.launch(Dispatchers.IO) {
            SeenItemsStorage.saveSeenFriendRequestIds(context, userId, _seenFriendRequestIds.value)
        }
    }

    /**
     * Call when the user opens a chat with [friendId].
     * That friend's messages are no longer counted as unseen; their red dot clears.
     * Persisted so the red dot does not reappear on app restart.
     */
    fun markMessageFriendSeen(friendId: String) {
        val userId = currentUserId ?: return
        _seenMessageFriendIds.value = _seenMessageFriendIds.value + friendId
        scope.launch(Dispatchers.IO) {
            SeenItemsStorage.addSeenMessageFriendId(context, userId, friendId)
        }
    }

    /**
     * Update the raw per-friend unread map from the Firestore listener.
     * Called by AppViewModel whenever the Firestore unreadPerFriend field changes.
     * [unseenUnreadPerFriend] will automatically filter using [_seenMessageFriendIds].
     *
     * If a friend now has NEW unread messages (Firestore count > 0), they are removed
     * from the seen set so their red dot reappears correctly.
     */
    fun updateRawUnreadPerFriend(unreadMap: Map<String, Int>) {
        _rawUnreadPerFriend.value = unreadMap
        val friendsWithNewMessages = unreadMap.filter { it.value > 0 }.keys
        if (friendsWithNewMessages.isNotEmpty()) {
            val currentSeen = _seenMessageFriendIds.value
            val stillSeen = currentSeen - friendsWithNewMessages
            if (stillSeen != currentSeen) {
                _seenMessageFriendIds.value = stillSeen
                val userId = currentUserId ?: return
                scope.launch(Dispatchers.IO) {
                    SeenItemsStorage.saveSeenMessageFriendIds(context, userId, stillSeen)
                }
            }
        }
    }

    /**
     * Explicitly clear all persisted notification-seen state for [userId].
     * Reserved for explicit reset flows (for example account reset/cleanup).
     */
    fun clearAllSeenStateForUser(userId: String) {
        scope.launch(Dispatchers.IO) {
            SeenItemsStorage.clearAllSeenState(context, userId)
        }
    }

    // ── In-memory username cache (avoid re-fetching sender profile on share) ──

    /** Cache: userId → username. Populated when friends list loads. */
    private val usernameCache = mutableMapOf<String, String>()

    /**
     * Cache the authenticated user's own username so shareWord can resolve it without
     * a Firestore read.  Called from MyProfileViewModel when profile loads.
     */
    fun cacheOwnUsername(userId: String, username: String) {
        if (username.isNotBlank()) usernameCache[userId] = username
    }

    /**
     * Look up a cached username by userId.  Returns null if not yet cached.
     */
    fun getCachedUsername(userId: String): String? = usernameCache[userId]

    // ── Lifecycle ────────────────────────────────────────────────────────────

    /**
     * Start observing all collections for [userId].
     * Idempotent: does nothing if already observing the same user.
     */
    fun startObserving(userId: String) {
        if (userId == currentUserId &&
            friendsJob?.isActive == true &&
            requestsJob?.isActive == true &&
            inboxJob?.isActive == true
        ) return

        stopObserving()
        currentUserId = userId
        val uid = UserId(userId)

        // Restore all persisted seen-state from SharedPreferences
        scope.launch(Dispatchers.IO) {
            _seenSharedItemIds.value = SeenItemsStorage.loadSeenItemIds(context, userId)
            _seenFriendRequestIds.value = SeenItemsStorage.loadSeenFriendRequestIds(context, userId)
            _seenMessageFriendIds.value = SeenItemsStorage.loadSeenMessageFriendIds(context, userId)
        }

        friendsJob = scope.launch {
            try {
                friendsRepository.observeFriends(uid).collect { list ->
                    _friends.value = list
                    list.forEach { rel ->
                        if (rel.friendUsername.isNotBlank()) {
                            usernameCache[rel.friendId] = rel.friendUsername
                        }
                    }
                }
            } catch (e: Exception) {
                Log.w("SharedFriendsDS", "Friends listener error, restarting in 5s", e)
                kotlinx.coroutines.delay(5000)
                if (currentUserId == userId) startObserving(userId)
            }
        }

        requestsJob = scope.launch {
            try {
                friendsRepository.observeIncomingRequests(uid).collect { list ->
                    _incomingRequests.value = list
                    val currentRequestIds = list.map { it.requestId }.toSet()
                    // Remove IDs from seenSet that are no longer pending (accepted/rejected)
                    _seenFriendRequestIds.value = _seenFriendRequestIds.value.intersect(currentRequestIds)
                }
            } catch (e: Exception) {
                Log.w("SharedFriendsDS", "Requests listener error, restarting in 5s", e)
                kotlinx.coroutines.delay(5000)
                if (currentUserId == userId) startObserving(userId)
            }
        }

        inboxJob = scope.launch {
            try {
                sharingRepository.observeSharedInbox(uid).collect { list ->
                    // Resolve missing usernames from the in-memory cache (populated from friends list).
                    // This ensures items shared by friends display the correct sender name,
                    // even if the item document didn't store fromUsername (older format).
                    val resolvedList = list.map { item ->
                        if (item.fromUsername.isBlank() && item.fromUserId.isNotBlank()) {
                            val cachedName = usernameCache[item.fromUserId]
                            if (cachedName != null) item.copy(fromUsername = cachedName) else item
                        } else {
                            item
                        }
                    }
                    _pendingSharedItems.value = resolvedList
                    val currentIds = resolvedList.map { it.itemId }.toSet()
                    // Remove IDs from seenSet that are no longer pending (accepted/dismissed)
                    _seenSharedItemIds.value = _seenSharedItemIds.value.intersect(currentIds)
                }
            } catch (e: Exception) {
                Log.w("SharedFriendsDS", "Shared inbox listener error, restarting in 5s", e)
                kotlinx.coroutines.delay(5000)
                if (currentUserId == userId) startObserving(userId)
            }
        }
    }

    /**
     * Stop observing and clear in-memory state only.
     *
     * IMPORTANT: Persisted seen-item IDs are intentionally NOT cleared here.
     * They must survive app restarts so badges don't reappear for already-viewed items.
     * Use [clearAllSeenStateForUser] only on explicit reset flows.
     */
    fun stopObserving() {
        friendsJob?.cancel()
        requestsJob?.cancel()
        inboxJob?.cancel()
        friendsJob = null
        requestsJob = null
        inboxJob = null

        // Clear only in-memory state — do NOT touch SharedPreferences here
        currentUserId = null
        _friends.value = emptyList()
        _incomingRequests.value = emptyList()
        _pendingSharedItems.value = emptyList()
        _seenSharedItemIds.value = emptySet()
        _seenFriendRequestIds.value = emptySet()
        _seenMessageFriendIds.value = emptySet()
        _rawUnreadPerFriend.value = emptyMap()
        usernameCache.clear()
    }

    /**
     * Returns true if [friendId] is in the current friends list.
     * O(n) but the friends list is small and already in memory — avoids a Firestore read.
     */
    fun isFriend(friendId: String): Boolean =
        _friends.value.any { it.friendId == friendId }

    /**
     * Update in-memory friend display names from a freshly-fetched username map.
     * Called after syncFriendUsernames() returns so the UI shows the latest names
     * without waiting for the real-time listener to pick up the Firestore write.
     */
    fun applyUsernameUpdates(updates: Map<String, String>) {
        if (updates.isEmpty()) return
        val current = _friends.value
        val updated = current.map { rel ->
            val newName = updates[rel.friendId]
            if (newName != null && newName != rel.friendUsername) rel.copy(friendUsername = newName)
            else rel
        }
        if (updated != current) {
            _friends.value = updated
            // Also update the username cache
            updates.forEach { (uid, name) -> if (name.isNotBlank()) usernameCache[uid] = name }
        }
    }
}
