package com.example.fyp.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fyp.data.user.FirebaseAuthRepository
import com.example.fyp.model.user.AuthState
import com.example.fyp.model.ui.UiTextKey
import com.example.fyp.utils.ErrorMessageMapper
import com.example.fyp.core.security.AuditLogger
import com.example.fyp.core.security.RateLimiter
import com.example.fyp.core.security.ValidationResult
import com.example.fyp.core.security.validateEmail
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

    companion object {
        private val loginRateLimiter = RateLimiter(maxAttempts = 5, windowMillis = 60_000L)
        private val resetPasswordRateLimiter = RateLimiter(maxAttempts = 3, windowMillis = 3_600_000L)
    }

    val authState: StateFlow<AuthState> = authRepository.currentUserState.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        AuthState.Loading
    )

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState

    fun login(email: String, password: String) {
        val trimmedEmail = email.trim()

        // Validate email format
        val emailValidation = validateEmail(trimmedEmail)
        if (emailValidation is ValidationResult.Invalid) {
            AuditLogger.logInvalidInput(field = "email", reason = emailValidation.message)
            _uiState.value = _uiState.value.copy(errorRaw = emailValidation.message)
            return
        }

        // Check rate limiter
        if (!loginRateLimiter.isAllowed(trimmedEmail)) {
            AuditLogger.logRateLimitExceeded(userId = trimmedEmail, operation = "login")
            _uiState.value = _uiState.value.copy(
                errorRaw = "Too many login attempts. Please try again later."
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorKey = null,
                errorRaw = null,
                messageKey = null,
                messageRaw = null
            )

            val result = authRepository.login(trimmedEmail, password)

            result.onSuccess {
                AuditLogger.logLoginSuccess(userId = trimmedEmail)
            }
            result.onFailure {
                AuditLogger.logLoginFailed(email = trimmedEmail, reason = it.message ?: "unknown")
            }

            _uiState.value = _uiState.value.copy(
                isLoading = false,
                errorRaw = result.exceptionOrNull()?.let { ErrorMessageMapper.map(it) }
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
        val trimmedEmail = email.trim()

        // Validate email format
        val emailValidation = validateEmail(trimmedEmail)
        if (emailValidation is ValidationResult.Invalid) {
            AuditLogger.logInvalidInput(field = "email", reason = emailValidation.message)
            _uiState.value = _uiState.value.copy(errorRaw = emailValidation.message)
            return
        }

        // Check rate limiter
        if (!resetPasswordRateLimiter.isAllowed(trimmedEmail)) {
            AuditLogger.logRateLimitExceeded(userId = trimmedEmail, operation = "resetPassword")
            _uiState.value = _uiState.value.copy(
                errorRaw = "Too many password reset attempts. Please try again later."
            )
            return
        }

        AuditLogger.logPasswordResetRequested(email = trimmedEmail)

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorKey = null,
                errorRaw = null,
                messageKey = null,
                messageRaw = null
            )

            val result = authRepository.sendPasswordResetEmail(trimmedEmail)

            _uiState.value = _uiState.value.copy(
                isLoading = false,
                errorRaw = result.exceptionOrNull()?.let { ErrorMessageMapper.map(it) },
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