package com.example.fyp.screens.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.fyp.core.AppLanguageDropdown
import com.example.fyp.core.StandardScreenBody
import com.example.fyp.core.StandardScreenScaffold
import com.example.fyp.core.rememberUiTextFunctions
import com.example.fyp.model.AppLanguageState
import com.example.fyp.model.BaseUiTexts
import com.example.fyp.model.UiTextKey
import com.example.fyp.model.AuthState

@Composable
fun ResetPasswordScreen(
    uiLanguages: List<Pair<String, String>>,
    appLanguageState: AppLanguageState,
    onUpdateAppLanguage: (String, Map<UiTextKey, String>) -> Unit,
    onBack: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val authState by viewModel.authState.collectAsStateWithLifecycle()
    val isLoggedIn = authState is AuthState.LoggedIn
    val (uiText, _) = rememberUiTextFunctions(appLanguageState)
    val t: (UiTextKey) -> String = { key -> uiText(key, BaseUiTexts[key.ordinal]) }

    var email by remember { mutableStateOf("") }

    StandardScreenScaffold(
        title = t(UiTextKey.ResetPwTitle),
        onBack = onBack,
        backContentDescription = t(UiTextKey.NavBack)
    ) { innerPadding ->
        StandardScreenBody(
            innerPadding = innerPadding,
            scrollable = true,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AppLanguageDropdown(
                uiLanguages = uiLanguages,
                appLanguageState = appLanguageState,
                onUpdateAppLanguage = onUpdateAppLanguage,
                uiText = uiText,
                isLoggedIn = isLoggedIn
            )

            Text(
                text = t(UiTextKey.ResetPwText),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text(t(UiTextKey.AuthEmailLabel)) },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = { viewModel.resetPassword(email) },
                enabled = !uiState.isLoading && email.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (uiState.isLoading) t(UiTextKey.ResetSendingText) else t(UiTextKey.ResetSendText))
            }

            val msg = uiState.messageKey?.let { t(it) } ?: uiState.messageRaw
            msg?.let { Text(text = it, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(top = 4.dp)) }

            val err = uiState.errorKey?.let { t(it) } ?: uiState.errorRaw
            err?.let { Text(text = it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 4.dp)) }
        }
    }
}