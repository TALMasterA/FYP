package com.example.fyp.feature.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.fyp.core.AppLanguageDropdown
import com.example.fyp.core.rememberUiTextFunctions
import com.example.fyp.feature.login.AuthViewModel
import com.example.fyp.model.AppLanguageState
import com.example.fyp.model.AuthState
import com.example.fyp.model.BaseUiTexts
import com.example.fyp.model.UiTextKey

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
    onOpenHistory: () -> Unit
) {
    val authViewModel: AuthViewModel = hiltViewModel()
    val authState by authViewModel.authState.collectAsStateWithLifecycle()
    val (uiText, _) = rememberUiTextFunctions(appLanguageState)

    var showLogoutDialog by remember { mutableStateOf(false) }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Logout?") },
            text = { Text("You will need to login again to view your history.") },
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
                ) { Text("Logout") }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) { Text("Cancel") }
            }
        )
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text(uiText(UiTextKey.HomeTitle, BaseUiTexts[UiTextKey.HomeTitle.ordinal])) },
                actions = {
                    when (authState) {
                        is AuthState.LoggedIn -> {
                            TextButton(onClick = onOpenHistory) { Text("History") }
                            TextButton(onClick = { showLogoutDialog = true }) { Text("Logout") }
                        }
                        AuthState.Loading -> {
                            CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                        }
                        AuthState.LoggedOut -> {
                            TextButton(onClick = onOpenLogin) { Text("Login") }
                        }
                    }
                    IconButton(onClick = onOpenHelp) {
                        Icon(imageVector = Icons.Filled.Info, contentDescription = "Help / instructions")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            AppLanguageDropdown(
                uiLanguages = uiLanguages,
                appLanguageState = appLanguageState,
                onUpdateAppLanguage = onUpdateAppLanguage,
                uiText = uiText
            )

            Text(
                text = uiText(UiTextKey.HomeInstructions, BaseUiTexts[UiTextKey.HomeInstructions.ordinal]),
                style = MaterialTheme.typography.bodyMedium
            )

            Button(onClick = onStartSpeech, modifier = Modifier.fillMaxWidth()) {
                Text(uiText(UiTextKey.HomeStartButton, BaseUiTexts[UiTextKey.HomeStartButton.ordinal]))
            }

            Button(onClick = onStartContinuous, modifier = Modifier.fillMaxWidth()) {
                Text(uiText(UiTextKey.ContinuousStartScreenButton, BaseUiTexts[UiTextKey.ContinuousStartScreenButton.ordinal]))
            }
        }
    }
}