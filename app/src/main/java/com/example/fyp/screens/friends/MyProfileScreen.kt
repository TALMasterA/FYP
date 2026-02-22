package com.example.fyp.screens.friends

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.fyp.core.StandardScreenScaffold
import com.example.fyp.core.rememberUiTextFunctions
import com.example.fyp.data.azure.LanguageDisplayNames
import com.example.fyp.model.ui.AppLanguageState
import com.example.fyp.model.ui.BaseUiTexts
import com.example.fyp.model.ui.UiTextKey

@Composable
fun MyProfileScreen(
    appLanguageState: AppLanguageState,
    onBack: () -> Unit,
    viewModel: MyProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val (uiText) = rememberUiTextFunctions(appLanguageState)
    val t: (UiTextKey) -> String = { key -> uiText(key, BaseUiTexts[key.ordinal]) }
    val context = LocalContext.current

    StandardScreenScaffold(
        title = t(UiTextKey.MyProfileTitle),
        onBack = onBack,
        backContentDescription = t(UiTextKey.NavBack)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            // Success message
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
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = error,
                            modifier = Modifier.weight(1f),
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                        IconButton(onClick = { viewModel.clearError() }) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Dismiss",
                                tint = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                }
            }

            if (uiState.isLoading) {
                // Loading state
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (uiState.profile != null) {
                val profile = uiState.profile!!

                // Profile Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        // User ID Section
                        ProfileInfoItem(
                            label = t(UiTextKey.MyProfileUserId),
                            value = uiState.userId,
                            icon = Icons.Default.Person,
                            onCopy = {
                                copyToClipboard(context, "User ID", uiState.userId)
                                viewModel.showSuccessMessage(t(UiTextKey.MyProfileCopied))
                            },
                            copyButtonText = t(UiTextKey.MyProfileCopyUserId)
                        )

                        Divider(modifier = Modifier.padding(vertical = 16.dp))

                        // Username Section
                        ProfileInfoItem(
                            label = t(UiTextKey.MyProfileUsername),
                            value = profile.username.ifEmpty { "Not set" },
                            icon = Icons.Default.AccountCircle,
                            onCopy = if (profile.username.isNotEmpty()) {
                                {
                                    copyToClipboard(context, "Username", profile.username)
                                    viewModel.showSuccessMessage(t(UiTextKey.MyProfileCopied))
                                }
                            } else null,
                            copyButtonText = t(UiTextKey.MyProfileCopyUsername)
                        )

                        Divider(modifier = Modifier.padding(vertical = 16.dp))

                        // Primary Language - display human-readable name, not code
                        val primaryLangCode = profile.primaryLanguage
                        val primaryLangDisplay = if (primaryLangCode.isNotEmpty())
                            LanguageDisplayNames.displayName(primaryLangCode)
                        else
                            "Not set"

                        ProfileInfoItem(
                            label = t(UiTextKey.MyProfilePrimaryLanguage),
                            value = primaryLangDisplay,
                            icon = Icons.Default.Language,
                            onCopy = null
                        )

                        // Learning Languages - display human-readable names, not codes
                        val learningLangs = profile.learningLanguages.orEmpty()
                        if (learningLangs.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = t(UiTextKey.MyProfileLearningLanguages),
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            learningLangs.forEach { languageCode ->
                                Row(
                                    modifier = Modifier.padding(vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.School,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp),
                                        tint = MaterialTheme.colorScheme.secondary
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = LanguageDisplayNames.displayName(languageCode),
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }
                    }
                }

                // Share Profile Button
                Button(
                    onClick = {
                        shareProfile(context, uiState.userId, profile.username)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Icon(
                        Icons.Default.Share,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(t(UiTextKey.MyProfileShare))
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Profile Visibility Setting
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = t(UiTextKey.MyProfileVisibilityLabel),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = t(UiTextKey.MyProfileVisibilityDescription),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            FilterChip(
                                selected = profile.isDiscoverable,
                                onClick = { if (!profile.isDiscoverable) viewModel.updateVisibility(true) },
                                label = { Text(t(UiTextKey.MyProfileVisibilityPublic)) },
                                enabled = !uiState.isUpdatingVisibility,
                                leadingIcon = if (profile.isDiscoverable) {
                                    { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                                } else null,
                                modifier = Modifier.weight(1f)
                            )
                            FilterChip(
                                selected = !profile.isDiscoverable,
                                onClick = { if (profile.isDiscoverable) viewModel.updateVisibility(false) },
                                label = { Text(t(UiTextKey.MyProfileVisibilityPrivate)) },
                                enabled = !uiState.isUpdatingVisibility,
                                leadingIcon = if (!profile.isDiscoverable) {
                                    { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                                } else null,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileInfoItem(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onCopy: (() -> Unit)?,
    copyButtonText: String = "Copy"
) {
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            Icon(
                icon,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )

            if (onCopy != null) {
                TextButton(onClick = onCopy) {
                    Icon(
                        Icons.Default.ContentCopy,
                        contentDescription = copyButtonText,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(copyButtonText)
                }
            }
        }
    }
}

private fun copyToClipboard(context: Context, label: String, text: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText(label, text)
    clipboard.setPrimaryClip(clip)
}

private fun shareProfile(context: Context, userId: String, username: String) {
    val shareText = if (username.isNotEmpty()) {
        "Add me on FYP!\nUsername: $username\nUser ID: $userId"
    } else {
        "Add me on FYP!\nUser ID: $userId"
    }

    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, shareText)
        type = "text/plain"
    }

    val shareIntent = Intent.createChooser(sendIntent, null)
    context.startActivity(shareIntent)
}
