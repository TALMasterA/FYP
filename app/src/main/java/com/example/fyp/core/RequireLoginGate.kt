package com.example.fyp.core

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.fyp.model.AuthState
import com.example.fyp.screens.login.AuthViewModel

@Composable
fun RequireLoginGate(
    content: @Composable () -> Unit,
    onNeedLogin: () -> Unit,
    loading: @Composable () -> Unit = { CircularProgressIndicator() },
) {
    val authViewModel: AuthViewModel = hiltViewModel()
    val authState by authViewModel.authState.collectAsStateWithLifecycle()

    when (authState) {
        is AuthState.LoggedIn -> content()
        AuthState.Loading -> loading()
        AuthState.LoggedOut -> LaunchedEffect(authState) { onNeedLogin() }
    }
}