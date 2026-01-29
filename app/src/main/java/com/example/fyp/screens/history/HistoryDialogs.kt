package com.example.fyp.screens.history

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import com.example.fyp.core.ConfirmationDialog

/**
 * Delete record confirmation dialog using shared ConfirmationDialog.
 */
@Composable
fun DeleteRecordDialog(
    title: String,
    message: String,
    confirmText: String,
    cancelText: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    ConfirmationDialog(
        title = title,
        message = message,
        confirmText = confirmText,
        cancelText = cancelText,
        onConfirm = onConfirm,
        onDismiss = onDismiss,
        confirmColor = MaterialTheme.colorScheme.error
    )
}

/**
 * Delete session confirmation dialog using shared ConfirmationDialog.
 */
@Composable
fun DeleteSessionDialog(
    title: String,
    message: String,
    confirmText: String,
    cancelText: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    ConfirmationDialog(
        title = title,
        message = message,
        confirmText = confirmText,
        cancelText = cancelText,
        onConfirm = onConfirm,
        onDismiss = onDismiss,
        confirmColor = MaterialTheme.colorScheme.error
    )
}

@Composable
fun RenameSessionDialog(
    title: String,
    label: String,
    value: String,
    confirmText: String,
    cancelText: String,
    onValueChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                label = { Text(label) },
                singleLine = true
            )
        },
        confirmButton = { Button(onClick = onConfirm) { Text(confirmText) } },
        dismissButton = { TextButton(onClick = onDismiss) { Text(cancelText) } }
    )
}