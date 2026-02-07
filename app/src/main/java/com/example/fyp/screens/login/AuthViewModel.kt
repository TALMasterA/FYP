package com.example.fyp.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fyp.data.user.FirebaseAuthRepository
import com.example.fyp.model.user.AuthState
import com.example.fyp.model.ui.UiTextKey
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AuthUiState(
    val errorKey: UiTextKey? = null,
    val errorRaw: String? = null,
    val messageKey: UiTextKey? = null,
    val messageRaw: String? = null,
    val isLoading: Boolean = false
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: FirebaseAuthRepository
) : ViewModel() {

    val authState: StateFlow<AuthState> = authRepository.currentUserState.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        AuthState.Loading
    )

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorKey = null,
                errorRaw = null,
                messageKey = null,
                messageRaw = null
            )

            val result = authRepository.login(email, password)

            _uiState.value = _uiState.value.copy(
                isLoading = false,
                errorRaw = result.exceptionOrNull()?.message
            )
        }
    }

    fun register(email: String, password: String) {
        _uiState.value = AuthUiState(
            errorKey = UiTextKey.AuthRegistrationDisabled,
            isLoading = false
        )
    }

    fun resetPassword(email: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorKey = null,
                errorRaw = null,
                messageKey = null,
                messageRaw = null
            )

            val result = authRepository.sendPasswordResetEmail(email.trim())

            _uiState.value = _uiState.value.copy(
                isLoading = false,
                errorRaw = result.exceptionOrNull()?.message,
                messageKey = if (result.isSuccess) UiTextKey.AuthResetEmailSent else null
            )
        }
    }

    fun logout() {
        authRepository.logout()
        _uiState.value = AuthUiState()
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorKey = null, errorRaw = null)
    }

    fun clearMessage() {
        _uiState.value = _uiState.value.copy(messageKey = null, messageRaw = null)
    }
}