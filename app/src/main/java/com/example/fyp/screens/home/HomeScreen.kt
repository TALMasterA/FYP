@file:Suppress("AssignedValueIsNeverRead")

package com.example.fyp.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.fyp.core.AppLanguageDropdown
import com.example.fyp.core.StandardScreenBody
import com.example.fyp.core.StandardScreenScaffold
import com.example.fyp.core.rememberUiTextFunctions
import com.example.fyp.screens.login.AuthViewModel
import com.example.fyp.model.AppLanguageState
import com.example.fyp.model.AuthState
import com.example.fyp.model.BaseUiTexts
import com.example.fyp.model.UiTextKey
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.FloatingActionButton
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.systemBarsPadding

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    uiLanguages: List<Pair<String, String>>,
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
        title = t(UiTextKey.HomeTitle),
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

            StandardScreenBody(
                innerPadding = innerPadding,
                scrollable = true,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                AppLanguageDropdown(
                    uiLanguages = uiLanguages,
                    appLanguageState = appLanguageState,
                    onUpdateAppLanguage = onUpdateAppLanguage,
                    uiText = uiText,
                    enabled = true,
                    isLoggedIn = isLoggedIn
                )

                if (!isLoggedIn) {
                    Text(
                        text = t(UiTextKey.DisableText),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Text(
                    text = t(UiTextKey.HomeInstructions),
                    style = MaterialTheme.typography.bodyMedium
                )

                Button(
                    onClick = onStartSpeech,
                    enabled = isLoggedIn,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(t(UiTextKey.HomeStartButton))
                }

                Button(
                    onClick = onStartContinuous,
                    enabled = isLoggedIn,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(t(UiTextKey.ContinuousStartScreenButton))
                }

                Button(
                    onClick = onOpenLearning,
                    enabled = isLoggedIn,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(t(UiTextKey.LearningTitle))
                }
            }

            FloatingActionButton(
                onClick = onOpenSettings,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .imePadding()
                    .systemBarsPadding()
                    .offset(y = (-28).dp)
                    .padding(16.dp)
            ) {
                Icon(Icons.Filled.Settings, contentDescription = "Settings")
            }

            // Word Bank FAB on the left side (only show when logged in)
            if (isLoggedIn) {
                FloatingActionButton(
                    onClick = onOpenWordBank,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .imePadding()
                        .systemBarsPadding()
                        .offset(y = (-28).dp)
                        .padding(16.dp),
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                ) {
                    Icon(Icons.AutoMirrored.Filled.MenuBook, contentDescription = "Word Bank")
                }
            }
        }
    }
}