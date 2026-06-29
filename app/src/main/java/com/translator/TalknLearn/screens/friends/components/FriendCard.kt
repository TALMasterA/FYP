package com.translator.TalknLearn.screens.friends.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.filled.Block
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.translator.TalknLearn.model.friends.FriendRelation

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
                .padding(vertical = 4.dp)
                .then(
                    if (isSelected && isDeleteMode) {
                        Modifier
                            .border(
                                width = 3.dp,
                                color = MaterialTheme.colorScheme.error,
                                shape = RoundedCornerShape(12.dp)
                            )
                    } else {
                        Modifier
                    }
                ),
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
                    .semantics { contentDescription = "$unreadCount $unreadMessagesText" }
            )
        }
    }
}
