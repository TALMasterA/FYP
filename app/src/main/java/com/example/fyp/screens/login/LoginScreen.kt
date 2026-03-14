@file:Suppress("AssignedValueIsNeverRead")

package com.example.fyp.screens.login

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.fyp.core.AppLanguageDropdown
import com.example.fyp.core.StandardScreenBody
import com.example.fyp.core.StandardScreenScaffold
import com.example.fyp.core.rememberUiTextFunctions
import com.example.fyp.model.ui.AppLanguageState
import com.example.fyp.model.user.AuthState
import com.example.fyp.model.ui.BaseUiTexts
import com.example.fyp.model.ui.UiTextKey
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.ui.text.input.VisualTransformation
import android.content.Context
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import kotlinx.coroutines.delay
import com.example.fyp.core.UiConstants
import com.example.fyp.ui.theme.AppSpacing
import com.example.fyp.ui.theme.AppCorners
import androidx.compose.foundation.shape.RoundedCornerShape

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    uiLanguages: List<Pair<String, String>>,
    appLanguageState: AppLanguageState,
    onUpdateAppLanguage: (String, Map<UiTextKey, String>) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AuthViewModel = hiltViewModel(),
    onLoginSuccess: () -> Unit = {},
    onOpenResetPassword: () -> Unit = {},
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

    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    val context = LocalContext.current
    var updateLogoutMsg by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        val prefs = context.getSharedPreferences("app_update_prefs", Context.MODE_PRIVATE)
        val reason = prefs.getString("logout_reason", null)
        if (reason == "updated") {
            updateLogoutMsg = t(UiTextKey.AuthUpdatedLoginAgain)
            prefs.edit().remove("logout_reason").apply()
        }
    }

    // Auto-dismiss error after delay
    val vmErrorText = uiState.errorKey?.let { t(it) } ?: uiState.errorRaw
    LaunchedEffect(vmErrorText) {
        if (vmErrorText != null) {
            delay(UiConstants.ERROR_AUTO_DISMISS_MS)
            viewModel.clearError()
        }
    }

    StandardScreenScaffold(
        title = if (isLogin) t(UiTextKey.AuthLoginTitle) else t(UiTextKey.AuthRegisterTitle),
        onBack = onBack,
        backContentDescription = t(UiTextKey.NavBack)
    ) { innerPadding ->
        StandardScreenBody(
            innerPadding = innerPadding,
            scrollable = true,
            verticalArrangement = Arrangement.spacedBy(AppSpacing.extraLarge)
        ) {
            AppLanguageDropdown(
                uiLanguages = uiLanguages,
                appLanguageState = appLanguageState,
                onUpdateAppLanguage = onUpdateAppLanguage,
                uiText = uiText,
                isLoggedIn = false
            )

            updateLogoutMsg?.let {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                    ),
                    shape = RoundedCornerShape(AppCorners.medium)
                ) {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(AppSpacing.large)
                    )
                }
            }

            Text(
                text = if (isLogin) t(UiTextKey.AuthLoginHint) else t(UiTextKey.AuthRegisterRules),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Error message displayed ABOVE input fields so it's visible when keyboard is open
            val vmErrorText = uiState.errorKey?.let { t(it) } ?: uiState.errorRaw
            (localError ?: vmErrorText)?.let { errorMsg ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
                    ),
                    shape = RoundedCornerShape(AppCorners.medium)
                ) {
                    Text(
                        text = errorMsg,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(AppSpacing.large)
                    )
                }
            }

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text(t(UiTextKey.AuthEmailLabel)) },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier.fillMaxWidth().testTag("emailField")
            )

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text(t(UiTextKey.AuthPasswordLabel)) },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = if (isLogin) ImeAction.Done else ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        if (isLogin) {
                            localError = null
                            viewModel.login(email.trim(), password.trim())
                        }
                    }
                ),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                            contentDescription = if (passwordVisible) t(UiTextKey.AuthPasswordLabel) else t(UiTextKey.AuthPasswordLabel)
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth().testTag("passwordField")
            )

            if (!isLogin) {
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text(t(UiTextKey.AuthConfirmPasswordLabel)) },
                    visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            if (!isLogin) {
                                localError = when {
                                    password.trim() != confirmPassword.trim() -> t(UiTextKey.AuthErrorPasswordsMismatch)
                                    password.trim().length < 6 -> t(UiTextKey.AuthErrorPasswordTooShort)
                                    else -> null
                                }
                                if (localError == null) viewModel.register(email.trim(), password.trim())
                            }
                        }
                    ),
                    trailingIcon = {
                        IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                            Icon(
                                imageVector = if (confirmPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                contentDescription = if (confirmPasswordVisible) t(UiTextKey.AuthConfirmPasswordLabel) else t(UiTextKey.AuthConfirmPasswordLabel)
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Button(
                onClick = {
                    localError = null

                    if (!isLogin) {
                        if (password.trim() != confirmPassword.trim()) {
                            localError = t(UiTextKey.AuthErrorPasswordsMismatch)
                            return@Button
                        }
                        if (password.trim().length < 6) {
                            localError = t(UiTextKey.AuthErrorPasswordTooShort)
                            return@Button
                        }
                    }

                    if (isLogin) viewModel.login(email.trim(), password.trim())
                    else viewModel.register(email.trim(), password.trim())
                },
                modifier = modifier.fillMaxWidth().testTag("loginButton"),
                enabled = !uiState.isLoading
            ) {
                Text(if (isLogin) t(UiTextKey.AuthLoginButton) else t(UiTextKey.AuthRegisterButton))
            }

            TextButton(onClick = {
                isLogin = !isLogin
                localError = null
                confirmPassword = ""
            }, modifier = Modifier.testTag("toggleAuthMode")) {
                Text(if (isLogin) t(UiTextKey.AuthToggleToRegister) else t(UiTextKey.AuthToggleToLogin))
            }

            TextButton(onClick = onOpenResetPassword) {
                Text(t(UiTextKey.ForgotPwText))
            }
        }
    }
}