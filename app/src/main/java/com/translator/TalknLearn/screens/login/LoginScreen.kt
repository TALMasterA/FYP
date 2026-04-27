@file:Suppress("AssignedValueIsNeverRead")

package com.translator.TalknLearn.screens.login

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
import com.translator.TalknLearn.core.AppLanguageDropdown
import com.translator.TalknLearn.core.StandardScreenBody
import com.translator.TalknLearn.core.StandardScreenScaffold
import com.translator.TalknLearn.core.rememberUiTextFunctions
import com.translator.TalknLearn.model.ui.AppLanguageState
import com.translator.TalknLearn.model.user.AuthState
import com.translator.TalknLearn.model.ui.BaseUiTexts
import com.translator.TalknLearn.model.ui.UiTextKey
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.ui.text.input.VisualTransformation
import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import com.translator.TalknLearn.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import kotlinx.coroutines.delay
import com.translator.TalknLearn.core.UiConstants
import com.translator.TalknLearn.core.security.ValidationResult
import com.translator.TalknLearn.core.security.validatePassword
import com.translator.TalknLearn.ui.theme.AppSpacing
import com.translator.TalknLearn.ui.theme.AppCorners
import androidx.compose.foundation.shape.RoundedCornerShape

internal fun resolveGoogleWebClientId(context: Context): String? {
    val resourceId = context.resources.getIdentifier(
        "default_web_client_id",
        "string",
        context.packageName
    )
    if (resourceId == 0) return null
    return context.getString(resourceId).trim().takeIf { it.isNotEmpty() }
}

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

    // Resolve this dynamically because some CI/test Firebase configs omit the
    // generated default_web_client_id resource even though the rest of the app can compile.
    val googleWebClientId = remember(context) { resolveGoogleWebClientId(context) }
    val googleSignInClient = remember(context, googleWebClientId) {
        googleWebClientId?.let { webClientId ->
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(webClientId)
                .requestEmail()
                .build()
            GoogleSignIn.getClient(context, gso)
        }
    }

    val googleSignInLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { activityResult ->
        try {
            val task = GoogleSignIn.getSignedInAccountFromIntent(activityResult.data)
            val account = task.getResult(ApiException::class.java)
            val idToken = account?.idToken
            if (idToken.isNullOrBlank()) {
                viewModel.reportGoogleSignInError("Google sign-in failed: no ID token returned.")
            } else {
                viewModel.signInWithGoogle(idToken)
            }
        } catch (e: ApiException) {
            viewModel.reportGoogleSignInError(
                "Google sign-in failed (code ${e.statusCode})."
            )
        }
    }

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
                                    validatePassword(password) is ValidationResult.Invalid -> t(UiTextKey.AuthErrorPasswordTooShort)
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
                        if (validatePassword(password) is ValidationResult.Invalid) {
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

            // Google Sign-In button. Available in both login and register
            // modes since Google sign-in covers both flows.
            OutlinedButton(
                onClick = {
                    localError = null
                    val client = googleSignInClient
                    if (client == null) {
                        viewModel.reportGoogleSignInError(
                            "Google sign-in is not configured for this build."
                        )
                        return@OutlinedButton
                    }
                    // Sign out of any cached Google account so the chooser
                    // is shown each time, allowing the user to switch accounts.
                    client.signOut()
                    googleSignInLauncher.launch(client.signInIntent)
                },
                modifier = Modifier.fillMaxWidth().testTag("googleSignInButton"),
                enabled = !uiState.isLoading
            ) {
                Text(t(UiTextKey.AuthGoogleSignInButton))
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