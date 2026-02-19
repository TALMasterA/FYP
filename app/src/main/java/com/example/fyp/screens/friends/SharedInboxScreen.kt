package com.example.fyp.screens.friends

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Article
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.fyp.core.StandardScreenScaffold
import com.example.fyp.core.rememberUiTextFunctions
import com.example.fyp.model.friends.SharedItem
import com.example.fyp.model.friends.SharedItemType
import com.example.fyp.model.ui.AppLanguageState
import com.example.fyp.model.ui.BaseUiTexts
import com.example.fyp.model.ui.UiTextKey
import com.example.fyp.ui.components.EmptyStates
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun SharedInboxScreen(
    viewModel: SharedInboxViewModel = hiltViewModel(),
    appLanguageState: AppLanguageState,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val (uiText, _) = rememberUiTextFunctions(appLanguageState)
    val t: (UiTextKey) -> String = { key -> uiText(key, BaseUiTexts[key.ordinal]) }

    // Auto-dismiss messages after showing
    LaunchedEffect(uiState.successMessage, uiState.error) {
        if (uiState.successMessage != null || uiState.error != null) {
            kotlinx.coroutines.delay(3000)
            viewModel.clearMessages()
        }
    }

    StandardScreenScaffold(
        title = t(UiTextKey.ShareInboxTitle),
        onBack = onBack
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                
                uiState.sharedItems.isEmpty() -> {
                    EmptyStates.NoSharedItems(t)
                }
                
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(uiState.sharedItems, key = { it.itemId }) { item ->
                            SharedItemCard(
                                item = item,
                                t = t,
                                onAccept = { viewModel.acceptItem(item.itemId) },
                                onDismiss = { viewModel.dismissItem(item.itemId) },
                                isProcessing = uiState.isProcessing
                            )
                        }
                    }
                }
            }

            // Success message
            uiState.successMessage?.let { message ->
                Card(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                        .fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            message,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }

            // Error message
            uiState.error?.let { error ->
                Card(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                        .fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Error,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onErrorContainer
                        )
                        Text(
                            error,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = { viewModel.clearMessages() }) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Dismiss",
                                tint = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SharedItemCard(
    item: SharedItem,
    t: (UiTextKey) -> String,
    onAccept: () -> Unit,
    onDismiss: () -> Unit,
    isProcessing: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Header: Type and From
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        when (item.type) {
                            SharedItemType.WORD -> Icons.Default.Book
                            SharedItemType.LEARNING_SHEET -> Icons.AutoMirrored.Filled.Article
                            SharedItemType.QUIZ -> Icons.Default.Quiz
                        },
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        when (item.type) {
                            SharedItemType.WORD -> t(UiTextKey.ShareTypeWord)
                            SharedItemType.LEARNING_SHEET -> t(UiTextKey.ShareTypeLearningSheet)
                            SharedItemType.QUIZ -> t(UiTextKey.ShareTypeQuiz)
                        },
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                
                Text(
                    formatTimestamp(item.createdAt.toDate()),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // From user
            Text(
                t(UiTextKey.ShareReceivedFrom).replace("{username}", item.fromUsername),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            HorizontalDivider()

            // Content display
            when (item.type) {
                SharedItemType.WORD -> {
                    val content = item.content
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            content["sourceText"] as? String ?: "",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            content["targetText"] as? String ?: "",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                        val notes = content["notes"] as? String
                        if (!notes.isNullOrBlank()) {
                            Text(
                                notes,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                SharedItemType.LEARNING_SHEET, SharedItemType.QUIZ -> {
                    val content = item.content
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            content["title"] as? String ?: "",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        val description = content["description"] as? String
                        if (!description.isNullOrBlank()) {
                            Text(
                                description,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    enabled = !isProcessing,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(t(UiTextKey.ShareDismissButton))
                }
                
                Button(
                    onClick = onAccept,
                    enabled = !isProcessing,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(t(UiTextKey.ShareAcceptButton))
                }
            }
        }
    }
}

private fun formatTimestamp(date: Date): String {
    val now = Calendar.getInstance()
    val target = Calendar.getInstance().apply { time = date }
    
    return when {
        // Today
        now.get(Calendar.YEAR) == target.get(Calendar.YEAR) &&
        now.get(Calendar.DAY_OF_YEAR) == target.get(Calendar.DAY_OF_YEAR) -> {
            SimpleDateFormat("HH:mm", Locale.getDefault()).format(date)
        }
        // This year
        now.get(Calendar.YEAR) == target.get(Calendar.YEAR) -> {
            SimpleDateFormat("MMM d, HH:mm", Locale.getDefault()).format(date)
        }
        // Older
        else -> {
            SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(date)
        }
    }
}
