package com.translator.TalknLearn.screens.friends.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.translator.TalknLearn.model.friends.PublicUserProfile
import com.translator.TalknLearn.model.ui.UiTextKey
import com.translator.TalknLearn.screens.friends.RequestStatus

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
                                    notePlaceholder = t(UiTextKey.FriendsRequestNotePlaceholder),
                                    statusAlreadyFriends = t(UiTextKey.FriendsStatusAlreadyFriends),
                                    statusRequestSent = t(UiTextKey.FriendsStatusRequestSent),
                                    statusRequestReceived = t(UiTextKey.FriendsStatusRequestReceived)
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
    notePlaceholder: String = "Add a short note...",
    statusAlreadyFriends: String = "Already friends",
    statusRequestSent: String = "Request sent — awaiting reply",
    statusRequestReceived: String = "This user sent you a request"
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
                        RequestStatus.ALREADY_FRIENDS -> statusAlreadyFriends
                        RequestStatus.REQUEST_SENT    -> "✓ $statusRequestSent"
                        RequestStatus.REQUEST_RECEIVED -> statusRequestReceived
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
