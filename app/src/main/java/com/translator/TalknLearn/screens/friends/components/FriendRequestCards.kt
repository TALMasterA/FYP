package com.translator.TalknLearn.screens.friends.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.translator.TalknLearn.model.friends.FriendRequest

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
