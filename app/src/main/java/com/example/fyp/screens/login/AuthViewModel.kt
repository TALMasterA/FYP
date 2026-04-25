package com.example.fyp.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fyp.core.SessionDataCleaner
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

/**
 * ViewModel for the Login / Registration screens.
 *
 * Handles email/password authentication (login, registration,
 * password reset) with client-side rate limiting and input
 * validation. Emits [AuthState] changes from [FirebaseAuthRepository].
 */
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: FirebaseAuthRepository,
    private val sessionDataCleaner: SessionDataCleaner
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

    /**
     * Sign in to Firebase using a Google ID token obtained from the
     * Google Sign-In flow on the UI layer.
     */
    fun signInWithGoogle(idToken: String) {
        if (idToken.isBlank()) {
            _uiState.value = _uiState.value.copy(
                errorRaw = "Google sign-in failed: empty ID token."
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

            val result = authRepository.signInWithGoogle(idToken)

            result.onSuccess { user ->
                AuditLogger.logLoginSuccess(userId = user.email ?: user.uid)
            }
            result.onFailure {
                AuditLogger.logLoginFailed(
                    email = "google-sign-in",
                    reason = it.message ?: "unknown"
                )
            }

            _uiState.value = _uiState.value.copy(
                isLoading = false,
                errorRaw = result.exceptionOrNull()?.let { ErrorMessageMapper.map(it) }
            )
        }
    }

    /**
     * Surface a Google sign-in error originating from the UI layer
     * (e.g. user cancelled, no credential available).
     */
    fun reportGoogleSignInError(message: String) {
        _uiState.value = _uiState.value.copy(
            isLoading = false,
            errorRaw = message
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
        // Wipe session-scoped local caches BEFORE Firebase signOut so we still hold
        // any auth-derived context (e.g. Azure tokens). Errors inside the cleaner are
        // already swallowed individually so they cannot block logout.
        viewModelScope.launch {
            runCatching { sessionDataCleaner.clearSessionData() }
            authRepository.logout()
            _uiState.value = AuthUiState()
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorKey = null, errorRaw = null)
    }

    fun clearMessage() {
        _uiState.value = _uiState.value.copy(messageKey = null, messageRaw = null)
    }
}