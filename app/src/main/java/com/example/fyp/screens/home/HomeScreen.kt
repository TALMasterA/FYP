package com.example.fyp.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.fyp.core.AppLanguageDropdown
import com.example.fyp.core.StandardScreenScaffold
import com.example.fyp.core.UiLanguageList
import com.example.fyp.core.rememberUiTextFunctions
import com.example.fyp.model.ui.AppLanguageState
import com.example.fyp.model.ui.BaseUiTexts
import com.example.fyp.model.ui.UiTextKey
import com.example.fyp.model.user.AuthState
import com.example.fyp.screens.login.AuthViewModel
import com.example.fyp.ui.theme.AppCorners
import com.example.fyp.ui.theme.AppElevation
import com.example.fyp.ui.theme.AppSpacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    uiLanguages: UiLanguageList,
    appLanguageState: AppLanguageState,
    onUpdateAppLanguage: (String, Map<UiTextKey, String>) -> Unit,
    onStartSpeech: () -> Unit,
    onOpenHelp: () -> Unit,
    onStartContinuous: () -> Unit,
    onOpenLogin: () -> Unit,
    onOpenHistory: () -> Unit,
    onOpenLearning: () -> Unit,
    onOpenSettings: () -> Unit = {},
    onOpenWordBank: () -> Unit = {},
    totalNotificationCount: Int = 0,
) {
    val authViewModel: AuthViewModel = hiltViewModel()
    val authState by authViewModel.authState.collectAsStateWithLifecycle()

    val (uiText, _) = rememberUiTextFunctions(appLanguageState)
    val t: (UiTextKey) -> String = { key -> uiText(key, BaseUiTexts[key.ordinal]) }

    var showLogoutDialog by remember { mutableStateOf(false) }
    val isLoggedIn = authState is AuthState.LoggedIn

    // Alternating title logic
    val defaultTitle = t(UiTextKey.HomeTitle)
    val userName = (authState as? AuthState.LoggedIn)?.user?.displayName?.takeIf { it.isNotBlank() }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text(t(UiTextKey.DialogLogoutTitle)) },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    Text(t(UiTextKey.DialogLogoutMessage))
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        authViewModel.logout()
                        showLogoutDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError
                    )
                ) { Text(t(UiTextKey.NavLogout)) }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text(t(UiTextKey.ActionCancel))
                }
            }
        )
    }

    StandardScreenScaffold(
        title = defaultTitle,
        onBack = null,
        actions = {
            when (authState) {
                is AuthState.LoggedIn -> {
                    TextButton(onClick = onOpenHistory) { Text(t(UiTextKey.NavHistory)) }
                    TextButton(onClick = { showLogoutDialog = true }) { Text(t(UiTextKey.NavLogout)) }
                }
                AuthState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                }
                AuthState.LoggedOut -> {
                    TextButton(onClick = onOpenLogin) { Text(t(UiTextKey.NavLogin)) }
                }
            }

            IconButton(onClick = onOpenHelp) {
                Icon(
                    imageVector = Icons.Filled.Info,
                    contentDescription = t(UiTextKey.HomeTitle)
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(AppSpacing.large)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.extraLarge)
        ) {
            AppLanguageDropdown(
                uiLanguages = uiLanguages,
                appLanguageState = appLanguageState,
                onUpdateAppLanguage = onUpdateAppLanguage,
                uiText = uiText,
                enabled = true,
                isLoggedIn = isLoggedIn
            )

            // Welcome back greeting (logged-in users only)
            if (isLoggedIn && !userName.isNullOrBlank()) {
                Text(
                    text = t(UiTextKey.HomeWelcomeBack).replace("{name}", userName),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            // Guest warning messages - styled card
            if (!isLoggedIn) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
                    ),
                    shape = RoundedCornerShape(AppCorners.medium)
                ) {
                    Column(modifier = Modifier.padding(AppSpacing.large)) {
                        Text(
                            text = t(UiTextKey.DisableText),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                        Spacer(modifier = Modifier.height(AppSpacing.extraSmall))
                        Text(
                            text = t(UiTextKey.GuestTranslationLimitMessage),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }

            // Welcome message card
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(AppCorners.large),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = AppElevation.small)
            ) {
                Column(modifier = Modifier.padding(AppSpacing.extraLarge)) {
                    Text(
                        text = t(UiTextKey.HomeInstructions),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            // Speech Translation Card
            FeatureCard(
                title = t(UiTextKey.HomeStartButton),
                icon = Icons.Filled.Mic,
                enabled = isLoggedIn,
                onClick = onStartSpeech,
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )

            // Continuous Conversation Card
            FeatureCard(
                title = t(UiTextKey.ContinuousStartScreenButton),
                icon = Icons.Filled.RecordVoiceOver,
                enabled = isLoggedIn,
                onClick = onStartContinuous,
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            )

            // Learning Card
            FeatureCard(
                title = t(UiTextKey.LearningTitle),
                icon = Icons.Filled.School,
                enabled = isLoggedIn,
                onClick = onOpenLearning,
                containerColor = MaterialTheme.colorScheme.tertiaryContainer
            )
        }
    }
}

// Feature Card Component - Reusable card for main features
@Composable
private fun FeatureCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    enabled: Boolean,
    onClick: () -> Unit,
    containerColor: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(AppCorners.large),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = if (enabled) AppElevation.medium else AppElevation.none
        ),
        colors = CardDefaults.elevatedCardColors(
            containerColor = if (enabled) containerColor else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AppSpacing.extraLarge),
            horizontalArrangement = Arrangement.spacedBy(AppSpacing.large),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = if (enabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = if (enabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
        }
    }
}
