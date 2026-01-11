package com.example.fyp.feature.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fyp.data.auth.FirebaseAuthRepository
import com.example.fyp.model.AuthState
import com.example.fyp.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow

data class LoginUiState(
    val error: String? = null,
    val isLoading: Boolean = false
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: FirebaseAuthRepository
) : ViewModel() {
    val authState = authRepository.currentUserState.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        AuthState.LoggedOut
    )

    private val _uiState = kotlinx.coroutines.flow.MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val result = authRepository.login(email, password)
            _uiState.value = _uiState.value.copy(isLoading = false, error = result.exceptionOrNull()?.message)
        }
    }

    fun register(email: String, password: String) {
        // Similar to login, call authRepository.register()
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val result = authRepository.register(email, password)
            _uiState.value = _uiState.value.copy(isLoading = false, error = result.exceptionOrNull()?.message)
        }
    }

    fun logout() {
        authRepository.logout()
        _uiState.value = LoginUiState()
    }
}