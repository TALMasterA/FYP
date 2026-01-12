package com.example.fyp.feature.login

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.fyp.core.AppLanguageDropdown
import com.example.fyp.core.StandardScreenBody
import com.example.fyp.core.StandardScreenScaffold
import com.example.fyp.core.rememberUiTextFunctions
import com.example.fyp.model.AppLanguageState
import com.example.fyp.model.AuthState
import com.example.fyp.model.BaseUiTexts
import com.example.fyp.model.UiTextKey

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    uiLanguages: List<Pair<String, String>>,
    appLanguageState: AppLanguageState,
    onUpdateAppLanguage: (String, Map<UiTextKey, String>) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AuthViewModel = hiltViewModel(),
    onLoginSuccess: () -> Unit = {}
) {
    val authState by viewModel.authState.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val (uiText, _) = rememberUiTextFunctions(appLanguageState)
    val t: (UiTextKey) -> String = { key -> uiText(key, BaseUiTexts[key.ordinal]) }

    if (authState is AuthState.LoggedIn) {
        LaunchedEffect(Unit) { onLoginSuccess() }
        return
    }

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isLogin by remember { mutableStateOf(true) }
    var localError by remember { mutableStateOf<String?>(null) }

    StandardScreenScaffold(
        title = if (isLogin) t(UiTextKey.AuthLoginTitle) else t(UiTextKey.AuthRegisterTitle),
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
                uiText = uiText
            )

            Text(
                text = if (isLogin) t(UiTextKey.AuthLoginHint) else t(UiTextKey.AuthRegisterRules),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text(t(UiTextKey.AuthEmailLabel)) },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text(t(UiTextKey.AuthPasswordLabel)) },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            if (!isLogin) {
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text(t(UiTextKey.AuthConfirmPasswordLabel)) },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Button(
                onClick = {
                    localError = null

                    if (!isLogin) {
                        if (password != confirmPassword) {
                            localError = t(UiTextKey.AuthErrorPasswordsMismatch)
                            return@Button
                        }
                        if (password.length < 6) {
                            localError = t(UiTextKey.AuthErrorPasswordTooShort)
                            return@Button
                        }
                    }

                    if (isLogin) viewModel.login(email.trim(), password)
                    else viewModel.register(email.trim(), password)
                },
                modifier = modifier.fillMaxWidth(),
                enabled = !uiState.isLoading
            ) {
                Text(if (isLogin) t(UiTextKey.AuthLoginButton) else t(UiTextKey.AuthRegisterButton))
            }

            TextButton(onClick = {
                isLogin = !isLogin
                localError = null
                confirmPassword = ""
            }) {
                Text(if (isLogin) t(UiTextKey.AuthToggleToRegister) else t(UiTextKey.AuthToggleToLogin))
            }

            (localError ?: uiState.error)?.let {
                Text(text = it, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}