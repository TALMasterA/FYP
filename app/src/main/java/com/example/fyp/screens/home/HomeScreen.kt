package com.example.fyp.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.fyp.core.AppLanguageDropdown
import com.example.fyp.core.StandardScreenScaffold
import com.example.fyp.core.rememberUiTextFunctions
import com.example.fyp.core.UiLanguageList
import com.example.fyp.screens.login.AuthViewModel
import com.example.fyp.model.ui.AppLanguageState
import com.example.fyp.model.user.AuthState
import com.example.fyp.model.ui.BaseUiTexts
import com.example.fyp.model.ui.UiTextKey
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.FloatingActionButton
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.navigationBarsPadding
import kotlinx.coroutines.delay

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
    onOpenSettings: () -> Unit,
    onOpenWordBank: () -> Unit,
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
            text = { Text(t(UiTextKey.DialogLogoutMessage)) },
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
                    contentDescription = "Help / instructions"
                )
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {

            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(16.dp)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                AppLanguageDropdown(
                    uiLanguages = uiLanguages,
                    appLanguageState = appLanguageState,
                    onUpdateAppLanguage = onUpdateAppLanguage,
                    uiText = uiText,
                    enabled = true,
                    isLoggedIn = isLoggedIn
                )

                // Guest warning messages - styled card
                if (!isLoggedIn) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = t(UiTextKey.DisableText),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                            Spacer(modifier = Modifier.height(4.dp))
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
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = t(UiTextKey.HomeInstructions),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                // Main Features Section
                Text(
                    text = t(UiTextKey.HomeFeaturesTitle),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                // Speech Translation Card
                FeatureCard(
                    title = t(UiTextKey.HomeStartButton),
                    description = t(UiTextKey.HomeDiscreteDescription),
                    icon = Icons.Filled.Mic,
                    enabled = isLoggedIn,
                    onClick = onStartSpeech,
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )

                // Continuous Conversation Card
                FeatureCard(
                    title = t(UiTextKey.ContinuousStartScreenButton),
                    description = t(UiTextKey.HomeContinuousDescription),
                    icon = Icons.Filled.RecordVoiceOver,
                    enabled = isLoggedIn,
                    onClick = onStartContinuous,
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )

                // Learning Card
                FeatureCard(
                    title = t(UiTextKey.LearningTitle),
                    description = t(UiTextKey.HomeLearningDescription),
                    icon = Icons.Filled.School,
                    enabled = isLoggedIn,
                    onClick = onOpenLearning,
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer
                )
                
                // Extra bottom spacing to ensure content isn't blocked by FABs
                // Accounts for FAB height (56dp) + padding (16dp) + navigation bar padding + extra clearance
                Spacer(modifier = Modifier.height(100.dp))
            }

            // FABs positioned to avoid overlap on all screen sizes
            FloatingActionButton(
                onClick = onOpenSettings,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
                    .navigationBarsPadding()
            ) {
                Icon(Icons.Filled.Settings, contentDescription = "Settings")
            }

            // Word Bank FAB on the left side (only show when logged in)
            if (isLoggedIn) {
                FloatingActionButton(
                    onClick = onOpenWordBank,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp)
                        .navigationBarsPadding(),
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                ) {
                    Icon(Icons.AutoMirrored.Filled.MenuBook, contentDescription = "Word Bank")
                }
            }
        }
    }
}

// Feature Card Component - Reusable card for main features
@Composable
private fun FeatureCard(
    title: String,
    description: String,
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
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = if (enabled) 4.dp else 1.dp
        ),
        colors = CardDefaults.elevatedCardColors(
            containerColor = if (enabled) containerColor else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = if (enabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )

            // Text content
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (enabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.labelMedium,
                    color = if (enabled) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                )
            }
        }
    }
}

@Composable
private fun GreetingTitle(
    defaultTitle: String,
    userName: String?
) {
    var showUserName by remember { mutableStateOf(false) }

    // Toggle between title and username every 3 seconds if user has a display name
    LaunchedEffect(userName) {
        if (!userName.isNullOrBlank()) {
            while (true) {
                delay(3000)
                showUserName = !showUserName
            }
        } else {
            showUserName = false
        }
    }

    val displayTitle = if (!userName.isNullOrBlank() && showUserName) {
        "👋 $userName"
    } else {
        defaultTitle
    }

    Text(displayTitle)
}
