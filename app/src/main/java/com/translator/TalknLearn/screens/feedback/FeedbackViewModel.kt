package com.translator.TalknLearn.screens.feedback

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.translator.TalknLearn.core.ErrorMessages
import com.translator.TalknLearn.core.security.PersistentRateLimiter
import com.translator.TalknLearn.core.security.ValidationResult
import com.translator.TalknLearn.core.security.sanitizeInput
import com.translator.TalknLearn.core.security.validateTextLength
import com.translator.TalknLearn.domain.feedback.FeedbackRepository
import com.translator.TalknLearn.model.ui.StatusText
import com.translator.TalknLearn.model.ui.UiTextKey
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
    val errorMessage: StatusText? = null
)

@HiltViewModel
class FeedbackViewModel @Inject constructor(
    private val feedbackRepository: FeedbackRepository,
    private val rateLimiter: PersistentRateLimiter
) : ViewModel() {

    companion object {
        private const val FEEDBACK_MAX = 3
        private const val FEEDBACK_WINDOW_MS = 3_600_000L
        private const val FEEDBACK_MIN_LENGTH = 10
        private const val FEEDBACK_MAX_LENGTH = 5000
    }

    private val _uiState = MutableStateFlow(FeedbackUiState())
    val uiState: StateFlow<FeedbackUiState> = _uiState.asStateFlow()

    fun submitFeedback(message: String) {
        if (message.isBlank()) {
            _uiState.value = _uiState.value.copy(
                errorMessage = StatusText(UiTextKey.FeedbackMsgEmpty),
                showErrorDialog = true
            )
            return
        }

        // Validate text length (min 10, max 5000)
        val lengthValidation = validateTextLength(
            message,
            minLength = FEEDBACK_MIN_LENGTH,
            maxLength = FEEDBACK_MAX_LENGTH,
            fieldName = "Feedback"
        )
        if (lengthValidation is ValidationResult.Invalid) {
            val lengthError = if (message.length < FEEDBACK_MIN_LENGTH) {
                StatusText(UiTextKey.FeedbackMsgTooShort, mapOf("min" to FEEDBACK_MIN_LENGTH.toString()))
            } else {
                StatusText(UiTextKey.FeedbackMsgTooLong, mapOf("max" to FEEDBACK_MAX_LENGTH.toString()))
            }
            _uiState.value = _uiState.value.copy(
                errorMessage = lengthError,
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
                errorMessage = StatusText(UiTextKey.FeedbackMsgRateLimited),
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
                    errorMessage = ErrorMessages.statusFromException(e, UiTextKey.FeedbackMsgSubmitFailed)
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

    fun setError(key: UiTextKey) {
        _uiState.value = _uiState.value.copy(errorMessage = StatusText(key))
    }
}
