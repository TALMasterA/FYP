package com.example.fyp.screens.friends

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
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
import com.example.fyp.ui.components.EmptyStates

@Composable
fun FriendsScreen(
    appLanguageState: AppLanguageState,
    onBack: () -> Unit,
    onOpenChat: (friendId: String, friendUsername: String, friendDisplayName: String) -> Unit = { _, _, _ -> },
    onOpenSharedInbox: () -> Unit = {},
    pendingSharedItemCount: Int = 0,
    viewModel: FriendsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val (uiText) = rememberUiTextFunctions(appLanguageState)
    val t: (UiTextKey) -> String = { key -> uiText(key, BaseUiTexts[key.ordinal]) }

    var showSearchDialog by remember { mutableStateOf(false) }
    var showRemoveDialog by remember { mutableStateOf<FriendRelation?>(null) }

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

    StandardScreenScaffold(
        title = t(UiTextKey.FriendsTitle),
        onBack = onBack,
        backContentDescription = t(UiTextKey.NavBack),
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Error/Success messages
            uiState.error?.let { error ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
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
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Text(
                        text = message,
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            // Search button and friend requests badge
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = { showSearchDialog = true }
                ) {
                    Icon(Icons.Default.Search, contentDescription = t(UiTextKey.FriendsSearchTitle))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(t(UiTextKey.FriendsAddButton))
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Shared Inbox button with red-dot badge
                    BadgedBox(
                        badge = {
                            if (pendingSharedItemCount > 0) {
                                Badge(
                                    containerColor = MaterialTheme.colorScheme.error,
                                    contentColor = MaterialTheme.colorScheme.onError
                                ) {
                                    Text(if (pendingSharedItemCount > 99) "99+" else "$pendingSharedItemCount")
                                }
                            }
                        }
                    ) {
                        IconButton(onClick = onOpenSharedInbox) {
                            Icon(
                                Icons.Default.Inbox,
                                contentDescription = t(UiTextKey.ShareInboxTitle)
                            )
                        }
                    }

                    if (uiState.incomingRequests.isNotEmpty()) {
                        BadgedBox(
                            badge = {
                                Badge {
                                    Text("${uiState.incomingRequests.size}")
                                }
                            }
                        ) {
                            Icon(Icons.Default.Notifications, contentDescription = t(UiTextKey.FriendsRequestsSection).replace("{count}", "${uiState.incomingRequests.size}"))
                        }
                    }
                }
            }

            // Friends list
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
                    EmptyStates.NoFriends()
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
                            items(uiState.incomingRequests) { request ->
                                FriendRequestCard(
                                    request = request,
                                    onAccept = { viewModel.acceptFriendRequest(request.requestId) },
                                    onReject = { viewModel.rejectFriendRequest(request.requestId) },
                                    acceptText = t(UiTextKey.FriendsAcceptButton),
                                    rejectText = t(UiTextKey.FriendsRejectButton)
                                )
                            }
                            item {
                                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
                            }
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
                            items(uiState.outgoingRequests) { request ->
                                OutgoingRequestCard(
                                    request = request,
                                    onCancel = { viewModel.cancelFriendRequest(request.requestId) },
                                    pendingText = t(UiTextKey.FriendsPendingStatus),
                                    cancelText = t(UiTextKey.FriendsCancelRequestButton)
                                )
                            }
                            item {
                                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
                            }
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
                            items(uiState.friends) { friend ->
                                FriendCard(
                                    friend = friend,
                                    unreadCount = uiState.unreadCountPerFriend[friend.friendId] ?: 0,
                                    onRemove = { showRemoveDialog = friend },
                                    onClick = { onOpenChat(friend.friendId, friend.friendUsername, friend.friendDisplayName) },
                                    removeText = t(UiTextKey.FriendsRemoveButton),
                                    sendMessageText = t(UiTextKey.FriendsUnreadMessageDesc)
                                )
                            }
                        }
                    }
                }
            }
            } // end Box
        }
    }

    // Search dialog
    if (showSearchDialog) {
        SearchUsersDialog(
            searchQuery = uiState.searchQuery,
            searchResults = uiState.searchResults,
            isSearching = uiState.isSearching,
            onQueryChange = { viewModel.onSearchQueryChange(it) },
            onSendRequest = { viewModel.sendFriendRequest(it) },
            canSendRequestTo = { userId -> viewModel.canSendRequestTo(userId) },
            onDismiss = {
                showSearchDialog = false
                viewModel.onSearchQueryChange("")
            },
            t = t
        )
    }

    // Remove friend confirmation dialog
    showRemoveDialog?.let { friend ->
        AlertDialog(
            onDismissRequest = { showRemoveDialog = null },
            title = { Text(t(UiTextKey.FriendsRemoveDialogTitle)) },
            text = { Text(t(UiTextKey.FriendsRemoveDialogMessage).replace("{username}", friend.friendUsername)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.removeFriend(friend.friendId)
                        showRemoveDialog = null
                    }
                ) {
                    Text(t(UiTextKey.FriendsRemoveConfirm))
                }
            },
            dismissButton = {
                TextButton(onClick = { showRemoveDialog = null }) {
                    Text(t(UiTextKey.FriendsCancelButton))
                }
            }
        )
    }
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
fun FriendRequestCard(    request: FriendRequest,
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
    onRemove: () -> Unit,
    onClick: () -> Unit,
    removeText: String,
    sendMessageText: String = "Send message"
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
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = friend.friendUsername,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            // Unread message badge — always show message icon, red dot only when unread > 0
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
                modifier = Modifier
                    .padding(end = 12.dp)
                    .wrapContentSize()
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.Message,
                    contentDescription = if (unreadCount > 0) "$unreadCount unread messages" else sendMessageText,
                    tint = if (unreadCount > 0) MaterialTheme.colorScheme.primary
                           else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = onRemove) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = removeText,
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
fun SearchUsersDialog(
    searchQuery: String,
    searchResults: List<PublicUserProfile>,
    isSearching: Boolean,
    onQueryChange: (String) -> Unit,
    onSendRequest: (String) -> Unit,
    canSendRequestTo: (String) -> Boolean,
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
                
                Spacer(modifier = Modifier.height(16.dp))
                
                when {
                    isSearching -> {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                    }
                    searchQuery.length < 2 -> {
                        Text(
                            t(UiTextKey.FriendsSearchMinChars),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    searchResults.isEmpty() -> {
                        Text(t(UiTextKey.FriendsSearchNoResults))
                    }
                    else -> {
                        LazyColumn(
                            modifier = Modifier.height(300.dp)
                        ) {
                            items(searchResults) { user ->
                                SearchResultCard(
                                    user = user,
                                    onSendRequest = { onSendRequest(user.uid) },
                                    canSendRequest = canSendRequestTo(user.uid),
                                    addButtonText = t(UiTextKey.FriendsSendRequestButton)
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
    onSendRequest: () -> Unit,
    canSendRequest: Boolean,
    addButtonText: String
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = user.username,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                if (!canSendRequest) {
                    Text(
                        text = "Already connected or pending",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Button(
                onClick = onSendRequest,
                enabled = canSendRequest,
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
}
