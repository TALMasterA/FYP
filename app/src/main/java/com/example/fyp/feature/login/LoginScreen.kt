package com.example.fyp.feature.login

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.fyp.feature.login.AuthViewModel
import androidx.compose.ui.text.input.PasswordVisualTransformation
import com.example.fyp.model.AuthState

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    viewModel: AuthViewModel = hiltViewModel(),
    onLoginSuccess: () -> Unit = {}
) {
    val authState by viewModel.authState.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    if (authState is AuthState.LoggedIn) {
        LaunchedEffect(Unit) { onLoginSuccess() }
        return
    }

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }   // NEW
    var isLogin by remember { mutableStateOf(true) }
    var localError by remember { mutableStateOf<String?>(null) }  // NEW

    Surface(modifier = modifier.fillMaxSize()) {  // ensures theme surface color
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (isLogin) "Login" else "Register",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            if (!isLogin) {
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("Confirm password") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    localError = null
                    if (!isLogin) {
                        if (password != confirmPassword) {
                            localError = "Passwords do not match."
                            return@Button
                        }
                        if (password.length < 6) {
                            localError = "Password must be at least 6 characters."
                            return@Button
                        }
                    }
                    if (isLogin) viewModel.login(email.trim(), password)
                    else viewModel.register(email.trim(), password)
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isLoading
            ) {
                Text(if (isLogin) "Login" else "Register")
            }

            TextButton(onClick = {
                isLogin = !isLogin
                localError = null
                // reset confirm field when switching
                confirmPassword = ""
            }) {
                Text(if (isLogin) "Don't have account? Register" else "Have account? Login")
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Prefer local validation error first
            (localError ?: uiState.error)?.let {
                Text(text = it, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}