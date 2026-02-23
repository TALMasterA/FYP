package com.example.fyp.screens.friends

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Info
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
import com.example.fyp.model.friends.FriendMessage
import com.example.fyp.model.ui.AppLanguageState
import com.example.fyp.model.ui.BaseUiTexts
import com.example.fyp.model.ui.UiTextKey
import com.example.fyp.ui.components.EmptyStates
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ChatScreen(
    appLanguageState: AppLanguageState,
    onBack: () -> Unit,
    viewModel: ChatViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val (uiText) = rememberUiTextFunctions(appLanguageState)
    val t: (UiTextKey) -> String = { key -> uiText(key, BaseUiTexts[key.ordinal]) }
    
    val listState = rememberLazyListState()
    var showTranslateDialog by remember { mutableStateOf(false) }
    var showProfileDialog by remember { mutableStateOf(false) }

    // Auto-scroll to bottom when new messages arrive
    LaunchedEffect(uiState.messages.size) {
        if (uiState.messages.isNotEmpty()) {
            listState.animateScrollToItem(uiState.messages.size - 1)
        }
    }
    
    // Show translate confirmation dialog
    if (showTranslateDialog) {
        TranslateConfirmDialog(
            title = t(UiTextKey.ChatTranslateDialogTitle),
            message = t(UiTextKey.ChatTranslateDialogMessage),
            confirmText = t(UiTextKey.ChatTranslateConfirm),
            cancelText = t(UiTextKey.FriendsCancelButton),
            onConfirm = {
                viewModel.translateAllMessages()
                showTranslateDialog = false
            },
            onDismiss = { showTranslateDialog = false }
        )
    }

    // Friend profile dialog
    if (showProfileDialog) {
        val profile = uiState.friendProfile
        AlertDialog(
            onDismissRequest = { showProfileDialog = false },
            title = {
                Text(
                    text = if (uiState.friendUsername.isNotEmpty()) {
                        "@${uiState.friendUsername}"
                    } else {
                        "Friend Profile"
                    }
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    if (profile != null) {
                        // Username
                        if (profile.username.isNotEmpty()) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "Username: ",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = profile.username,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }

                        // User ID
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "User ID: ",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = profile.uid,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

                        // Learning languages
                        val learning = profile.learningLanguages.orEmpty()
                        if (learning.isNotEmpty()) {
                            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                            Row(
                                verticalAlignment = Alignment.Top,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "Learning: ",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = learning.joinToString(", "),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    } else {
                        // Fallback when profile couldn't load
                        Text(
                            text = "Username: ${uiState.friendUsername}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        if (uiState.friendDisplayName.isNotEmpty() &&
                            uiState.friendDisplayName != uiState.friendUsername) {
                            Text(
                                text = "Display name: ${uiState.friendDisplayName}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showProfileDialog = false }) {
                    Text("Close")
                }
            }
        )
    }

    StandardScreenScaffold(
        title = t(UiTextKey.ChatTitle).replace("{username}", uiState.friendUsername),
        onBack = onBack,
        backContentDescription = t(UiTextKey.NavBack),
        actions = {
            // Profile info icon — tapping opens the friend's profile dialog
            IconButton(onClick = { showProfileDialog = true }) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "View friend profile",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            // Translate button
            if (uiState.messages.isNotEmpty()) {
                if (uiState.isTranslating) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp).padding(end = 8.dp),
                        strokeWidth = 2.dp
                    )
                } else if (uiState.translatedMessages.isNotEmpty()) {
                    // Toggle between original and translated
                    TextButton(onClick = { viewModel.toggleTranslation() }) {
                        Text(
                            if (uiState.showTranslation) {
                                t(UiTextKey.ChatShowOriginal)
                            } else {
                                t(UiTextKey.ChatShowTranslation)
                            }
                        )
                    }
                } else {
                    TextButton(onClick = { showTranslateDialog = true }) {
                        Text(t(UiTextKey.ChatTranslateButton))
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Error message
            uiState.error?.let { error ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = error,
                            modifier = Modifier.weight(1f),
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                        TextButton(onClick = { viewModel.clearError() }) {
                            Text(t(UiTextKey.ActionCancel))
                        }
                    }
                }
            }
            
            // Translation error message
            uiState.translationError?.let { error ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = error,
                            modifier = Modifier.weight(1f),
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                        TextButton(onClick = { viewModel.clearTranslation() }) {
                            Text(t(UiTextKey.ActionCancel))
                        }
                    }
                }
            }

            // Messages list
            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                uiState.messages.isEmpty() -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = t(UiTextKey.ChatEmpty),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                else -> {
                    // Trigger load older messages when scrolled near the top
                    val shouldLoadOlder = remember {
                        derivedStateOf {
                            val firstVisible = listState.firstVisibleItemIndex
                            firstVisible <= 2 && !uiState.isLoadingOlder && uiState.hasMoreMessages && uiState.messages.size >= 25
                        }
                    }
                    LaunchedEffect(shouldLoadOlder.value) {
                        if (shouldLoadOlder.value) {
                            viewModel.loadOlderMessages()
                        }
                    }

                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Loading older messages indicator
                        if (uiState.isLoadingOlder) {
                            item(key = "__loading_older__") {
                                Box(
                                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                                }
                            }
                        }
                        items(uiState.messages, key = { it.messageId }) { message ->
                            MessageBubble(
                                message = message,
                                currentUserId = uiState.currentUserId ?: "",
                                translatedText = if (uiState.showTranslation) {
                                    uiState.translatedMessages[message.content]
                                } else null,
                                translatedLabel = t(UiTextKey.ChatTranslatedLabel)
                            )
                        }
                    }
                }
            }

            // Message input
            MessageInput(
                messageText = uiState.messageText,
                onMessageTextChange = { viewModel.onMessageTextChange(it) },
                onSendClick = { viewModel.sendMessage() },
                isSending = uiState.isSending,
                placeholder = t(UiTextKey.ChatInputPlaceholder),
                sendButtonText = t(UiTextKey.ChatSendButton)
            )
        }
    }
}

@Composable
fun MessageBubble(
    message: FriendMessage,
    currentUserId: String,
    translatedText: String? = null,
    translatedLabel: String = "Translated"
) {
    val isCurrentUser = message.senderId == currentUserId
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isCurrentUser) Arrangement.End else Arrangement.Start
    ) {
        Card(
            modifier = Modifier
                .widthIn(max = 280.dp),
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isCurrentUser) 16.dp else 4.dp,
                bottomEnd = if (isCurrentUser) 4.dp else 16.dp
            ),
            colors = CardDefaults.cardColors(
                containerColor = if (isCurrentUser) {
                    MaterialTheme.colorScheme.primaryContainer
                } else {
                    MaterialTheme.colorScheme.secondaryContainer
                }
            )
        ) {
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                Text(
                    text = translatedText ?: message.content,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isCurrentUser) {
                        MaterialTheme.colorScheme.onPrimaryContainer
                    } else {
                        MaterialTheme.colorScheme.onSecondaryContainer
                    }
                )
                // Show translation indicator if text is translated
                if (translatedText != null && translatedText != message.content) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = translatedLabel,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (isCurrentUser) {
                            MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f)
                        } else {
                            MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.6f)
                        }
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = formatTimestamp(message.createdAt),
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isCurrentUser) {
                        MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    } else {
                        MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                    }
                )
            }
        }
    }
}

@Composable
fun MessageInput(
    messageText: String,
    onMessageTextChange: (String) -> Unit,
    onSendClick: () -> Unit,
    isSending: Boolean,
    placeholder: String,
    sendButtonText: String
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = messageText,
                onValueChange = onMessageTextChange,
                modifier = Modifier.weight(1f),
                placeholder = { Text(placeholder) },
                maxLines = 4,
                enabled = !isSending
            )
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                onClick = onSendClick,
                enabled = messageText.isNotBlank() && !isSending
            ) {
                if (isSending) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Icon(
                        Icons.AutoMirrored.Filled.Send,
                        contentDescription = sendButtonText,
                        tint = if (messageText.isNotBlank()) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.38f)
                        }
                    )
                }
            }
        }
    }
}

private fun formatTimestamp(timestamp: Timestamp): String {
    val date = timestamp.toDate()
    val now = Calendar.getInstance()
    val messageTime = Calendar.getInstance().apply { time = date }
    
    return when {
        // Today - show time only
        now.get(Calendar.YEAR) == messageTime.get(Calendar.YEAR) &&
        now.get(Calendar.DAY_OF_YEAR) == messageTime.get(Calendar.DAY_OF_YEAR) -> {
            SimpleDateFormat("HH:mm", Locale.getDefault()).format(date)
        }
        // Yesterday
        now.get(Calendar.YEAR) == messageTime.get(Calendar.YEAR) &&
        now.get(Calendar.DAY_OF_YEAR) - messageTime.get(Calendar.DAY_OF_YEAR) == 1 -> {
            "Yesterday ${SimpleDateFormat("HH:mm", Locale.getDefault()).format(date)}"
        }
        // This year - show date without year
        now.get(Calendar.YEAR) == messageTime.get(Calendar.YEAR) -> {
            SimpleDateFormat("MMM d, HH:mm", Locale.getDefault()).format(date)
        }
        // Different year - show full date
        else -> {
            SimpleDateFormat("MMM d, yyyy HH:mm", Locale.getDefault()).format(date)
        }
    }
}

@Composable
fun TranslateConfirmDialog(
    title: String,
    message: String,
    confirmText: String,
    cancelText: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                Text(message)
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(confirmText)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(cancelText)
            }
        }
    )
}
