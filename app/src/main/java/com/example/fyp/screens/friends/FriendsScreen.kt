package com.example.fyp.screens.friends

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.fyp.core.StandardScreenScaffold
import com.example.fyp.core.rememberUiTextFunctions
import com.example.fyp.model.friends.FriendRelation
import com.example.fyp.model.friends.FriendRequest
import com.example.fyp.model.friends.PublicUserProfile
import com.example.fyp.model.ui.AppLanguageState
import com.example.fyp.model.ui.BaseUiTexts
import com.example.fyp.model.ui.UiTextKey
import com.example.fyp.ui.components.EmptyStateView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FriendsScreen(
    appLanguageState: AppLanguageState,
    onBack: () -> Unit,
    onOpenChat: (friendId: String, friendUsername: String, friendDisplayName: String) -> Unit = { _, _, _ -> },
    onOpenSharedInbox: () -> Unit = {},
    onOpenBlockedUsers: () -> Unit = {},
    onOpenNotifSettings: () -> Unit = {},
    hasUnseenSharedItems: Boolean = false,
    hasUnreadMessages: Boolean = false,
    viewModel: FriendsViewModel = hiltViewModel(),
    settingsViewModel: com.example.fyp.screens.settings.SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val (uiText) = rememberUiTextFunctions(appLanguageState)
    val t: (UiTextKey) -> String = { key -> uiText(key, BaseUiTexts[key.ordinal]) }

    var showSearchDialog by remember { mutableStateOf(false) }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    var showInfoDialog by remember { mutableStateOf(false) }
    var isRefreshing by remember { mutableStateOf(false) }
    // Block confirmation dialog state
    var blockTargetId by remember { mutableStateOf<String?>(null) }
    var blockTargetUsername by remember { mutableStateOf("") }

    LaunchedEffect(isRefreshing) {
        if (isRefreshing) {
            viewModel.refreshFriendsList()
            kotlinx.coroutines.delay(600)
            isRefreshing = false
        }
    }

    val snackbarHostState = remember { SnackbarHostState() }

    // Show notification when new friend requests arrive
    LaunchedEffect(uiState.newRequestCount) {
        if (uiState.newRequestCount > 0) {
            snackbarHostState.showSnackbar(
                message = t(UiTextKey.FriendsNewRequestsTemplate).replace("{count}", "${uiState.newRequestCount}"),
                duration = SnackbarDuration.Short
            )
            viewModel.clearNewRequestCount()
        }
    }

    // Auto-dismiss error/success messages after 3 seconds
    LaunchedEffect(uiState.error, uiState.successMessage) {
        if (uiState.error != null || uiState.successMessage != null) {
            kotlinx.coroutines.delay(3000)
            viewModel.clearMessages()
        }
    }

    // Info dialog
    if (showInfoDialog) {
        AlertDialog(
            onDismissRequest = { showInfoDialog = false },
            title = { Text(t(UiTextKey.FriendsInfoTitle)) },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    Text(t(UiTextKey.FriendsInfoMessage))
                }
            },
            confirmButton = {
                TextButton(onClick = { showInfoDialog = false }) {
                    Text(t(UiTextKey.FriendsInfoGotItButton))
                }
            }
        )
    }

    // Search dialog
    if (showSearchDialog) {
        SearchUsersDialog(
            searchQuery = uiState.searchQuery,
            searchResults = uiState.searchResults,
            isSearching = uiState.isSearching,
            onQueryChange = { viewModel.onSearchQueryChange(it) },
            onSendRequest = { userId, note -> viewModel.sendFriendRequest(userId, note) },
            requestStatusFor = { userId -> viewModel.getRequestStatusFor(userId) },
            onDismiss = {
                showSearchDialog = false
                viewModel.onSearchQueryChange("")
            },
            t = t
        )
    }

    // Multi-delete confirmation dialog
    if (showDeleteConfirmDialog) {
        val count = uiState.selectedFriendIds.size
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            title = { Text(t(UiTextKey.FriendsDeleteMultipleTitle)) },
            text = {
                Text(t(UiTextKey.FriendsDeleteMultipleMessage).replace("{count}", "$count"))
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.removeSelectedFriends()
                        showDeleteConfirmDialog = false
                    }
                ) {
                    Text(t(UiTextKey.FriendsRemoveConfirm), color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDeleteConfirmDialog = false
                    viewModel.exitDeleteMode()
                }) {
                    Text(t(UiTextKey.FriendsCancelButton))
                }
            }
        )
    }

    // Block user confirmation dialog
    if (blockTargetId != null) {
        AlertDialog(
            onDismissRequest = { blockTargetId = null },
            title = { Text(t(UiTextKey.BlockUserTitle)) },
            text = {
                Text(
                    t(UiTextKey.BlockUserMessage).replace("{username}", blockTargetUsername)
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        blockTargetId?.let { viewModel.blockAndRemoveFriend(it, blockTargetUsername) }
                        blockTargetId = null
                    }
                ) {
                    Text(t(UiTextKey.BlockUserConfirm), color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { blockTargetId = null }) {
                    Text(t(UiTextKey.FriendsCancelButton))
                }
            }
        )
    }

    StandardScreenScaffold(
        title = t(UiTextKey.FriendsTitle),
        onBack = onBack,
        backContentDescription = t(UiTextKey.NavBack),
        actions = {
            // Manage blocked users
            IconButton(onClick = onOpenBlockedUsers) {
                Icon(
                    imageVector = Icons.Default.Block,
                    contentDescription = t(UiTextKey.BlockedUsersManageButton),
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
            // Notification settings (bell icon)
            IconButton(onClick = onOpenNotifSettings) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = t(UiTextKey.FriendsNotifSettingsButton),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            // Info / help
            IconButton(onClick = { showInfoDialog = true }) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = t(UiTextKey.FriendsInfoTitle),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = { isRefreshing = true },
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {

                // Error/Success messages
                uiState.error?.let { error ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                    ) {
                        Text(
                            text = error,
                            modifier = Modifier.padding(16.dp),
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }

                uiState.successMessage?.let { message ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        Text(
                            text = message,
                            modifier = Modifier.padding(16.dp),
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                // Action row: Add Friends + icon buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(onClick = {
                        if (viewModel.requireUsernameForAddFriends()) {
                            showSearchDialog = true
                        }
                    }) {
                        Icon(Icons.Default.Search, contentDescription = t(UiTextKey.FriendsSearchTitle))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(t(UiTextKey.FriendsAddButton))
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Dismiss all unread dots — only shown when there are unread messages
                        val hasAnyUnread = uiState.unreadCountPerFriend.values.any { it > 0 }
                        if (hasAnyUnread) {
                            IconButton(onClick = { viewModel.dismissAllUnreadDots() }) {
                                Icon(
                                    Icons.Default.DoneAll,
                                    contentDescription = "Mark all messages as read",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }

                        // Delete friends icon button
                        IconButton(onClick = {
                            when {
                                !uiState.isDeleteMode -> viewModel.toggleDeleteMode()
                                uiState.selectedFriendIds.isNotEmpty() -> showDeleteConfirmDialog = true
                                else -> viewModel.exitDeleteMode()
                            }
                        }) {
                            Icon(
                                if (uiState.isDeleteMode) Icons.Default.DeleteForever else Icons.Default.PersonRemove,
                                contentDescription = t(UiTextKey.FriendsDeleteModeButton),
                                tint = if (uiState.isDeleteMode) MaterialTheme.colorScheme.error
                                       else MaterialTheme.colorScheme.onSurface
                            )
                        }

                        // Shared Inbox button with red-dot badge
                        IconButton(onClick = onOpenSharedInbox) {
                            BadgedBox(
                                badge = {
                                    if (hasUnseenSharedItems) {
                                        Badge(containerColor = MaterialTheme.colorScheme.error)
                                    }
                                }
                            ) {
                                Icon(
                                    Icons.Default.Inbox,
                                    contentDescription = t(UiTextKey.ShareInboxTitle)
                                )
                            }
                        }
                        // Dismiss shared-inbox dot — only shown when there are unseen items
                        if (hasUnseenSharedItems) {
                            IconButton(onClick = { viewModel.dismissSharedInboxDot() }) {
                                Icon(
                                    Icons.Default.DoneAll,
                                    contentDescription = "Dismiss inbox notifications",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }

                // Content area
                Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                    when {
                        uiState.isLoading -> {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .padding(32.dp)
                                    .align(Alignment.TopCenter)
                            )
                        }
                        uiState.friends.isEmpty() && uiState.incomingRequests.isEmpty() && uiState.outgoingRequests.isEmpty() -> {
                            // Informative empty state
                            EmptyStateView(
                                icon = Icons.Default.People,
                                title = t(UiTextKey.FriendsEmptyTitle),
                                message = t(UiTextKey.FriendsEmptyMessage),
                                modifier = Modifier.align(Alignment.Center),
                                actionLabel = t(UiTextKey.FriendsAddButton),
                                onActionClick = { showSearchDialog = true }
                            )
                        }
                        else -> {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                            ) {
                                // Friend requests section
                                if (uiState.incomingRequests.isNotEmpty()) {
                                    item {
                                        Text(
                                            text = t(UiTextKey.FriendsRequestsSection).replace("{count}", "${uiState.incomingRequests.size}"),
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(vertical = 8.dp)
                                        )
                                    }
                                    items(uiState.incomingRequests, key = { it.requestId }) { request ->
                                        FriendRequestCard(
                                            request = request,
                                            onAccept = { viewModel.acceptFriendRequest(request.requestId) },
                                            onReject = { viewModel.rejectFriendRequest(request.requestId) },
                                            acceptText = t(UiTextKey.FriendsAcceptButton),
                                            rejectText = t(UiTextKey.FriendsRejectButton)
                                        )
                                    }
                                    item { HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp)) }
                                }

                                // Outgoing (Sent) requests section
                                if (uiState.outgoingRequests.isNotEmpty()) {
                                    item {
                                        Text(
                                            text = t(UiTextKey.FriendsSentRequestsSection).replace("{count}", "${uiState.outgoingRequests.size}"),
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(vertical = 8.dp)
                                        )
                                    }
                                    items(uiState.outgoingRequests, key = { it.requestId }) { request ->
                                        OutgoingRequestCard(
                                            request = request,
                                            onCancel = { viewModel.cancelFriendRequest(request.requestId) },
                                            pendingText = t(UiTextKey.FriendsPendingStatus),
                                            cancelText = t(UiTextKey.FriendsCancelRequestButton)
                                        )
                                    }
                                    item { HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp)) }
                                }

                                // Friends section
                                if (uiState.friends.isNotEmpty()) {
                                    item {
                                        Text(
                                            text = t(UiTextKey.FriendsSectionTitle).replace("{count}", "${uiState.friends.size}"),
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(vertical = 8.dp)
                                        )
                                    }
                                    items(uiState.friends, key = { it.friendId }) { friend ->
                                        FriendCard(
                                            friend = friend,
                                            unreadCount = uiState.unreadCountPerFriend[friend.friendId] ?: 0,
                                            unreadMessagesText = t(UiTextKey.AccessibilityNewMessages),
                                            onClick = {
                                                if (uiState.isDeleteMode) {
                                                    viewModel.toggleFriendSelection(friend.friendId)
                                                } else {
                                                    onOpenChat(friend.friendId, friend.friendUsername, friend.friendUsername)
                                                }
                                            },
                                            sendMessageText = t(UiTextKey.FriendsUnreadMessageDesc),
                                            isDeleteMode = uiState.isDeleteMode,
                                            isSelected = uiState.selectedFriendIds.contains(friend.friendId),
                                            onToggleSelect = { viewModel.toggleFriendSelection(friend.friendId) },
                                            blockButtonText = t(UiTextKey.BlockUserButton),
                                            onBlock = {
                                                blockTargetId = friend.friendId
                                                blockTargetUsername = friend.friendUsername
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                } // end Box
            } // end Column
        } // end PullToRefreshBox
    } // end StandardScreenScaffold
}

@Composable
fun OutgoingRequestCard(
    request: FriendRequest,
    onCancel: () -> Unit,
    pendingText: String,
    cancelText: String
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (request.toUsername.isNotEmpty()) request.toUsername
                           else "To: ${request.toUserId.take(12)}…",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = pendingText,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = onCancel) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = cancelText,
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
fun FriendRequestCard(
    request: FriendRequest,
    onAccept: () -> Unit,
    onReject: () -> Unit,
    acceptText: String,
    rejectText: String
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = request.fromUsername,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                if (request.note.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = request.note,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Row {
                IconButton(onClick = onAccept) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = acceptText,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                IconButton(onClick = onReject) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = rejectText,
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
fun FriendCard(
    friend: FriendRelation,
    unreadCount: Int = 0,
    unreadMessagesText: String = "New messages",
    onClick: () -> Unit,
    sendMessageText: String = "Send message",
    isDeleteMode: Boolean = false,
    isSelected: Boolean = false,
    onToggleSelect: () -> Unit = {},
    blockButtonText: String = "Block",
    onBlock: () -> Unit = {}
) {
    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        ElevatedCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            onClick = onClick
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isDeleteMode) {
                    Checkbox(
                        checked = isSelected,
                        onCheckedChange = { onToggleSelect() },
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = friend.friendUsername,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    // Show "unread messages" hint text below name when there are unread messages
                    if (unreadCount > 0 && !isDeleteMode) {
                        Text(
                            text = "● $unreadMessagesText",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
                if (!isDeleteMode) {
                    // Block icon button
                    IconButton(onClick = onBlock) {
                        Icon(
                            imageVector = Icons.Default.Block,
                            contentDescription = blockButtonText,
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                    // Unread message badge — red dot + count when unread > 0
                    BadgedBox(
                        badge = {
                            if (unreadCount > 0) {
                                Badge(
                                    containerColor = MaterialTheme.colorScheme.error,
                                    contentColor = MaterialTheme.colorScheme.onError
                                ) {
                                    Text(if (unreadCount > 99) "99+" else "$unreadCount")
                                }
                            }
                        },
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.Message,
                            contentDescription = if (unreadCount > 0) "$unreadCount unread messages" else sendMessageText,
                            tint = if (unreadCount > 0) MaterialTheme.colorScheme.primary
                                   else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Red dot in top-left corner when there are unread messages
        if (unreadCount > 0 && !isDeleteMode) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(start = 4.dp, top = 2.dp)
                    .size(14.dp)
                    .background(
                        color = MaterialTheme.colorScheme.error,
                        shape = CircleShape
                    )
            )
        }
    }
}

@Composable
fun SearchUsersDialog(
    searchQuery: String,
    searchResults: List<PublicUserProfile>,
    isSearching: Boolean,
    onQueryChange: (String) -> Unit,
    onSendRequest: (String, String) -> Unit,
    requestStatusFor: (String) -> RequestStatus,
    onDismiss: () -> Unit,
    t: (UiTextKey) -> String
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(t(UiTextKey.FriendsSearchTitle)) },
        text = {
            Column {
                TextField(
                    value = searchQuery,
                    onValueChange = onQueryChange,
                    placeholder = { Text(t(UiTextKey.FriendsSearchPlaceholder)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Hint: enter full user ID for exact lookup
                Text(
                    t(UiTextKey.FriendsSearchByUserIdHint),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(8.dp))

                when {
                    isSearching -> {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                    }
                    searchQuery.trim().length < 3 -> {
                        Text(
                            t(UiTextKey.FriendsSearchMinChars3),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    searchResults.isEmpty() -> {
                        Text(t(UiTextKey.FriendsSearchNoResults))
                    }
                    else -> {
                        LazyColumn(modifier = Modifier.heightIn(min = 100.dp, max = 300.dp)) {
                            items(searchResults, key = { it.uid }) { user ->
                                SearchResultCard(
                                    user = user,
                                    onSendRequest = { note -> onSendRequest(user.uid, note) },
                                    requestStatus = requestStatusFor(user.uid),
                                    addButtonText = t(UiTextKey.FriendsSendRequestButton),
                                    noteLabel = t(UiTextKey.FriendsRequestNoteLabel),
                                    notePlaceholder = t(UiTextKey.FriendsRequestNotePlaceholder)
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(t(UiTextKey.FriendsCloseButton))
            }
        }
    )
}

@Composable
fun SearchResultCard(
    user: PublicUserProfile,
    onSendRequest: (String) -> Unit,
    requestStatus: RequestStatus,
    addButtonText: String,
    noteLabel: String = "Request Note (optional)",
    notePlaceholder: String = "Add a short note..."
) {
    var noteText by remember(user.uid) { mutableStateOf("") }

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = user.username,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    val statusText = when (requestStatus) {
                        RequestStatus.ALREADY_FRIENDS -> "Already friends"
                        RequestStatus.REQUEST_SENT    -> "✓ Request sent — awaiting reply"
                        RequestStatus.REQUEST_RECEIVED -> "This user sent you a request"
                        RequestStatus.NONE            -> null
                    }
                    if (statusText != null) {
                        Text(
                            text = statusText,
                            style = MaterialTheme.typography.bodySmall,
                            color = when (requestStatus) {
                                RequestStatus.REQUEST_SENT -> MaterialTheme.colorScheme.primary
                                else -> MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )
                    }
                }
                // Only show the Add button when there is no existing connection
                if (requestStatus == RequestStatus.NONE) {
                    Button(
                        onClick = { onSendRequest(noteText) },
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            Icons.Default.PersonAdd,
                            contentDescription = addButtonText,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
            // Note text field - only shown for users who can receive requests
            if (requestStatus == RequestStatus.NONE) {
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = noteText,
                    onValueChange = { if (it.length <= 80) noteText = it },
                    placeholder = { Text(notePlaceholder, style = MaterialTheme.typography.bodySmall) },
                    label = { Text(noteLabel, style = MaterialTheme.typography.labelSmall) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodySmall,
                    supportingText = { Text("${noteText.length}/80", style = MaterialTheme.typography.labelSmall) }
                )
            }
        }
    }
}

/** A single row with a label and a Switch for notification toggles. */
@Composable
private fun NotifToggleRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            // Minimum 48dp touch target per Material Design accessibility guidelines
            .heightIn(min = 48.dp)
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}
