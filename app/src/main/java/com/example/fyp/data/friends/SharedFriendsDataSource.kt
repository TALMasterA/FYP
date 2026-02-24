package com.example.fyp.data.friends

import android.util.Log
import com.example.fyp.model.UserId
import com.example.fyp.model.friends.FriendRelation
import com.example.fyp.model.friends.FriendRequest
import com.example.fyp.model.friends.SharedItem
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

    /** Whether the initial inbox load has finished — used to suppress badge on app start. */
    private var initialLoadComplete = false

    /**
     * Whether there are NEW shared inbox items the user hasn't seen yet.
     * On app start, all existing items are auto-marked as seen so the badge
     * does NOT reappear after restart. Only items arriving AFTER the initial
     * load trigger the red dot.
     */
    val hasUnseenSharedItems: kotlinx.coroutines.flow.Flow<Boolean> =
        kotlinx.coroutines.flow.combine(_pendingSharedItems, _seenSharedItemIds) { items, seen ->
            items.any { it.itemId !in seen }
        }

    /**
     * Call this when the user opens the Shared Inbox screen so all currently
     * pending items are considered "seen" and the notification badge clears.
     */
    fun markSharedItemsSeen() {
        val currentIds = _pendingSharedItems.value.map { it.itemId }.toSet()
        _seenSharedItemIds.value = _seenSharedItemIds.value + currentIds
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
                    _pendingSharedItems.value = list
                    val currentIds = list.map { it.itemId }.toSet()
                    if (!initialLoadComplete) {
                        // First load after app start: mark ALL existing items as seen
                        // so the badge does NOT reappear on restart.
                        _seenSharedItemIds.value = currentIds
                        initialLoadComplete = true
                    }
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
     * Stop observing and clear cached state (e.g., on logout).
     */
    fun stopObserving() {
        friendsJob?.cancel()
        requestsJob?.cancel()
        inboxJob?.cancel()
        friendsJob = null
        requestsJob = null
        inboxJob = null
        currentUserId = null
        initialLoadComplete = false
        _friends.value = emptyList()
        _incomingRequests.value = emptyList()
        _pendingSharedItems.value = emptyList()
        _seenSharedItemIds.value = emptySet()
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

