package com.example.fyp.screens.friends

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
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
import com.example.fyp.data.friends.BlockedUser
import com.example.fyp.model.ui.AppLanguageState
import com.example.fyp.model.ui.BaseUiTexts
import com.example.fyp.model.ui.UiTextKey
import com.example.fyp.ui.components.EmptyStateView

@Composable
fun BlockedUsersScreen(
    appLanguageState: AppLanguageState,
    onBack: () -> Unit,
    viewModel: FriendsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val (uiText) = rememberUiTextFunctions(appLanguageState)
    val t: (UiTextKey) -> String = { key -> uiText(key, BaseUiTexts[key.ordinal]) }

    // Unblock confirmation dialog state
    var unblockTargetId by remember { mutableStateOf<String?>(null) }
    var unblockTargetUsername by remember { mutableStateOf("") }

    // Auto-dismiss messages
    LaunchedEffect(uiState.error, uiState.successMessage) {
        if (uiState.error != null || uiState.successMessage != null) {
            kotlinx.coroutines.delay(3000)
            viewModel.clearMessages()
        }
    }

    // Unblock confirmation dialog
    if (unblockTargetId != null) {
        AlertDialog(
            onDismissRequest = { unblockTargetId = null },
            title = { Text(t(UiTextKey.UnblockUserTitle)) },
            text = {
                Text(
                    t(UiTextKey.UnblockUserMessage).replace("{username}", unblockTargetUsername)
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        unblockTargetId?.let { viewModel.unblockUser(it) }
                        unblockTargetId = null
                    }
                ) {
                    Text(t(UiTextKey.UnblockUserButton))
                }
            },
            dismissButton = {
                TextButton(onClick = { unblockTargetId = null }) {
                    Text(t(UiTextKey.FriendsCancelButton))
                }
            }
        )
    }

    StandardScreenScaffold(
        title = t(UiTextKey.BlockedUsersTitle),
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

            if (uiState.blockedUsers.isEmpty()) {
                EmptyStateView(
                    icon = Icons.Default.Block,
                    title = t(UiTextKey.BlockedUsersTitle),
                    message = t(UiTextKey.BlockedUsersEmpty),
                    modifier = Modifier
                        .fillMaxSize()
                        .wrapContentSize(Alignment.Center)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.blockedUsers, key = { it.userId }) { blocked ->
                        BlockedUserCard(
                            blockedUser = blocked,
                            unblockText = t(UiTextKey.UnblockUserButton),
                            idTemplate = t(UiTextKey.BlockedUserIdTemplate),
                            onUnblock = {
                                unblockTargetId = blocked.userId
                                unblockTargetUsername = blocked.username
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BlockedUserCard(
    blockedUser: BlockedUser,
    unblockText: String,
    idTemplate: String,
    onUnblock: () -> Unit
) {
    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = blockedUser.username,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = idTemplate.replace("{id}", blockedUser.userId.take(12)),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            OutlinedButton(
                onClick = onUnblock,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(unblockText)
            }
        }
    }
}
