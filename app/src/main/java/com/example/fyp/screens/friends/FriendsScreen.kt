package com.example.fyp.screens.friends

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
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
    viewModel: FriendsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val (uiText) = rememberUiTextFunctions(appLanguageState)
    val t: (UiTextKey) -> String = { key -> uiText(key, BaseUiTexts[key.ordinal]) }

    var showSearchDialog by remember { mutableStateOf(false) }
    var showRemoveDialog by remember { mutableStateOf<FriendRelation?>(null) }

    StandardScreenScaffold(
        title = "Friends", // TODO: Add to UI text keys
        onBack = onBack,
        backContentDescription = t(UiTextKey.NavBack)
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
                    Icon(Icons.Default.Search, contentDescription = "Search users")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Add Friends")
                }

                if (uiState.incomingRequests.isNotEmpty()) {
                    BadgedBox(
                        badge = {
                            Badge {
                                Text("${uiState.incomingRequests.size}")
                            }
                        }
                    ) {
                        Icon(Icons.Default.Notifications, contentDescription = "Friend requests")
                    }
                }
            }

            // Friends list
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .padding(32.dp)
                            .align(Alignment.CenterHorizontally)
                    )
                }
                uiState.friends.isEmpty() && uiState.incomingRequests.isEmpty() -> {
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
                                    text = "Friend Requests (${uiState.incomingRequests.size})",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            }
                            items(uiState.incomingRequests) { request ->
                                FriendRequestCard(
                                    request = request,
                                    onAccept = { viewModel.acceptFriendRequest(request.requestId) },
                                    onReject = { viewModel.rejectFriendRequest(request.requestId) }
                                )
                            }
                            item {
                                Divider(modifier = Modifier.padding(vertical = 16.dp))
                            }
                        }

                        // Friends section
                        if (uiState.friends.isNotEmpty()) {
                            item {
                                Text(
                                    text = "Friends (${uiState.friends.size})",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            }
                            items(uiState.friends) { friend ->
                                FriendCard(
                                    friend = friend,
                                    onRemove = { showRemoveDialog = friend }
                                )
                            }
                        }
                    }
                }
            }
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
            onDismiss = {
                showSearchDialog = false
                viewModel.onSearchQueryChange("")
            }
        )
    }

    // Remove friend confirmation dialog
    showRemoveDialog?.let { friend ->
        AlertDialog(
            onDismissRequest = { showRemoveDialog = null },
            title = { Text("Remove Friend") },
            text = { Text("Are you sure you want to remove ${friend.friendUsername} from your friends list?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.removeFriend(friend.friendId)
                        showRemoveDialog = null
                    }
                ) {
                    Text("Remove")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRemoveDialog = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun FriendRequestCard(
    request: FriendRequest,
    onAccept: () -> Unit,
    onReject: () -> Unit
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
                if (request.fromDisplayName.isNotEmpty()) {
                    Text(
                        text = request.fromDisplayName,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Row {
                IconButton(onClick = onAccept) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = "Accept",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                IconButton(onClick = onReject) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Reject",
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
    onRemove: () -> Unit
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
                    text = friend.friendUsername,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                if (friend.friendDisplayName.isNotEmpty()) {
                    Text(
                        text = friend.friendDisplayName,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            IconButton(onClick = onRemove) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Remove friend",
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
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Search Users") },
        text = {
            Column {
                TextField(
                    value = searchQuery,
                    onValueChange = onQueryChange,
                    placeholder = { Text("Enter username...") },
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
                            "Enter at least 2 characters to search",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    searchResults.isEmpty() -> {
                        Text("No users found")
                    }
                    else -> {
                        LazyColumn(
                            modifier = Modifier.height(300.dp)
                        ) {
                            items(searchResults) { user ->
                                SearchResultCard(
                                    user = user,
                                    onSendRequest = { onSendRequest(user.uid) }
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

@Composable
fun SearchResultCard(
    user: PublicUserProfile,
    onSendRequest: () -> Unit
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
                if (user.displayName.isNotEmpty()) {
                    Text(
                        text = user.displayName,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            Button(
                onClick = onSendRequest,
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Icon(
                    Icons.Default.PersonAdd,
                    contentDescription = "Add friend",
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
