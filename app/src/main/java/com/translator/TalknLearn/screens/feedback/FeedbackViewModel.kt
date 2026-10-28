package com.translator.TalknLearn.screens.feedback

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.translator.TalknLearn.domain.feedback.FeedbackRepository
import com.translator.TalknLearn.core.security.PersistentRateLimiter
import com.translator.TalknLearn.core.security.ValidationResult
import com.translator.TalknLearn.core.security.sanitizeInput
import com.translator.TalknLearn.core.security.validateTextLength
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FeedbackUiState(
    val isSubmitting: Boolean = false,
    val showSuccessDialog: Boolean = false,
    val showErrorDialog: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class FeedbackViewModel @Inject constructor(
    private val feedbackRepository: FeedbackRepository,
    private val rateLimiter: PersistentRateLimiter
) : ViewModel() {

    companion object {
        private const val FEEDBACK_MAX = 3
        private const val FEEDBACK_WINDOW_MS = 3_600_000L
    }

    private val _uiState = MutableStateFlow(FeedbackUiState())
    val uiState: StateFlow<FeedbackUiState> = _uiState.asStateFlow()

    fun submitFeedback(message: String) {
        if (message.isBlank()) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Message cannot be empty",
                showErrorDialog = true
            )
            return
        }

        // Validate text length (min 10, max 5000)
        val lengthValidation = validateTextLength(message, minLength = 10, maxLength = 5000, fieldName = "Feedback")
        if (lengthValidation is ValidationResult.Invalid) {
            _uiState.value = _uiState.value.copy(
                errorMessage = lengthValidation.message,
                showErrorDialog = true
            )
            return
        }

        // Check rate limiter (persistent, survives process death)
        if (!rateLimiter.isAllowed(
                scope = PersistentRateLimiter.SCOPE_FEEDBACK,
                key = "device",
                maxAttempts = FEEDBACK_MAX,
                windowMillis = FEEDBACK_WINDOW_MS
            )
        ) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "You have submitted too much feedback recently. Please try again later.",
                showErrorDialog = true
            )
            return
        }

        // Sanitize the message
        val sanitizedMessage = sanitizeInput(message)

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSubmitting = true, errorMessage = null, showErrorDialog = false)

            try {
                feedbackRepository.submitFeedback(sanitizedMessage)
                _uiState.value = FeedbackUiState(showSuccessDialog = true)
            } catch (e: Exception) {
                android.util.Log.e("FeedbackViewModel", "Failed to submit feedback", e)
                _uiState.value = FeedbackUiState(
                    showErrorDialog = true,
                    errorMessage = com.translator.TalknLearn.core.ErrorMessages.fromException(e, "Failed to submit feedback. Please check your internet connection.")
                )
            }
        }
    }

    fun dismissSuccessDialog() {
        _uiState.value = _uiState.value.copy(showSuccessDialog = false)
    }

    fun dismissErrorDialog() {
        _uiState.value = _uiState.value.copy(showErrorDialog = false, isSubmitting = false)
    }

    fun setError(message: String) {
        _uiState.value = _uiState.value.copy(errorMessage = message)
    }
}
