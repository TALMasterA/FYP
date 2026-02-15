package com.example.fyp.screens.feedback

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.fyp.core.StandardScreenScaffold
import com.example.fyp.core.rememberUiTextFunctions
import com.example.fyp.model.ui.AppLanguageState
import com.example.fyp.model.ui.BaseUiTexts
import com.example.fyp.model.ui.UiTextKey

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedbackScreen(
    appLanguageState: AppLanguageState,
    onBack: () -> Unit,
    viewModel: FeedbackViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val (uiText, _) = rememberUiTextFunctions(appLanguageState)
    val t: (UiTextKey) -> String = { key -> uiText(key, BaseUiTexts[key.ordinal]) }

    var feedbackMessage by remember { mutableStateOf("") }

    // Show success dialog
    if (uiState.showSuccessDialog) {
        AlertDialog(
            onDismissRequest = {
                viewModel.dismissSuccessDialog()
                onBack()
            },
            title = { Text(t(UiTextKey.FeedbackSuccessTitle)) },
            text = { Text(t(UiTextKey.FeedbackSuccessMessage)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.dismissSuccessDialog()
                        onBack()
                    }
                ) {
                    Text(t(UiTextKey.ActionConfirm))
                }
            }
        )
    }

    // Show error dialog
    if (uiState.showErrorDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissErrorDialog() },
            title = { Text(t(UiTextKey.FeedbackErrorTitle)) },
            text = {
                Text(uiState.errorMessage ?: t(UiTextKey.FeedbackErrorMessage))
            },
            confirmButton = {
                TextButton(onClick = { viewModel.dismissErrorDialog() }) {
                    Text(t(UiTextKey.ActionConfirm))
                }
            }
        )
    }

    StandardScreenScaffold(
        title = t(UiTextKey.FeedbackTitle),
        onBack = onBack,
        backContentDescription = t(UiTextKey.NavBack)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Description
            Text(
                text = t(UiTextKey.FeedbackDesc),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Feedback input field
            OutlinedTextField(
                value = feedbackMessage,
                onValueChange = { feedbackMessage = it },
                label = { Text(t(UiTextKey.FeedbackMessagePlaceholder)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                maxLines = 10,
                enabled = !uiState.isSubmitting
            )

            // Submit button
            Button(
                onClick = {
                    if (feedbackMessage.isBlank()) {
                        viewModel.setError(t(UiTextKey.FeedbackMessageRequired))
                    } else {
                        viewModel.submitFeedback(feedbackMessage)
                        feedbackMessage = ""
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isSubmitting && feedbackMessage.isNotBlank()
            ) {
                if (uiState.isSubmitting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(t(UiTextKey.FeedbackSubmitting))
                } else {
                    Text(t(UiTextKey.FeedbackSubmitButton))
                }
            }

            // Error message
            if (uiState.errorMessage != null) {
                Text(
                    text = uiState.errorMessage!!,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
