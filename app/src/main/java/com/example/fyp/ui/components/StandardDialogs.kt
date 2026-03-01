package com.example.fyp.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.example.fyp.ui.theme.AppCorners
import com.example.fyp.ui.theme.AppSpacing

/**
 * Standardized alert dialog for consistent styling across the app.
 *
 * @param title The dialog title
 * @param message The dialog message
 * @param onDismiss Called when the dialog is dismissed
 * @param confirmText Text for the confirm button
 * @param onConfirm Called when the confirm button is clicked
 * @param dismissText Optional text for the dismiss button
 * @param icon Optional icon to display
 */
@Composable
fun StandardAlertDialog(
    title: String,
    message: String,
    onDismiss: () -> Unit,
    confirmText: String,
    onConfirm: () -> Unit,
    dismissText: String? = null,
    icon: ImageVector? = null
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = icon?.let {
            {
                Icon(
                    imageVector = it,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        },
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        text = {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        confirmButton = {
            StandardPrimaryButton(
                text = confirmText,
                onClick = {
                    onConfirm()
                    onDismiss()
                }
            )
        },
        dismissButton = dismissText?.let {
            {
                StandardTextButton(
                    text = it,
                    onClick = onDismiss
                )
            }
        },
        shape = RoundedCornerShape(AppCorners.large)
    )
}

/**
 * Standardized confirmation dialog for destructive actions.
 *
 * @param title The dialog title
 * @param message The dialog message
 * @param onDismiss Called when the dialog is dismissed
 * @param confirmText Text for the confirm button (usually "Delete", "Remove", etc.)
 * @param onConfirm Called when the confirm button is clicked
 * @param cancelText Text for the cancel button
 */
@Composable
fun StandardConfirmDialog(
    title: String,
    message: String,
    onDismiss: () -> Unit,
    confirmText: String,
    onConfirm: () -> Unit,
    cancelText: String = "Cancel"
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.error
            )
        },
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        text = {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm()
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                ),
                shape = RoundedCornerShape(AppCorners.medium)
            ) {
                Text(confirmText)
            }
        },
        dismissButton = {
            StandardSecondaryButton(
                text = cancelText,
                onClick = onDismiss
            )
        },
        shape = RoundedCornerShape(AppCorners.large)
    )
}

/**
 * Standardized info dialog for displaying information.
 *
 * @param title The dialog title
 * @param message The dialog message
 * @param onDismiss Called when the dialog is dismissed
 * @param buttonText Text for the dismiss button
 */
@Composable
fun StandardInfoDialog(
    title: String,
    message: String,
    onDismiss: () -> Unit,
    buttonText: String = "OK"
) {
    StandardAlertDialog(
        title = title,
        message = message,
        onDismiss = onDismiss,
        confirmText = buttonText,
        onConfirm = {},
        icon = Icons.Default.Info
    )
}

/**
 * Standardized loading dialog for async operations.
 *
 * @param message The loading message
 * @param onDismiss Optional dismiss handler (usually null for non-dismissible loading)
 */
@Composable
fun StandardLoadingDialog(
    message: String = "Loading...",
    onDismiss: (() -> Unit)? = null
) {
    if (onDismiss != null) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = null,
            text = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(AppSpacing.large),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(AppSpacing.large))
                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            },
            confirmButton = {},
            shape = RoundedCornerShape(AppCorners.large)
        )
    } else {
        // Non-dismissible loading dialog
        AlertDialog(
            onDismissRequest = {},
            title = null,
            text = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(AppSpacing.large),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(AppSpacing.large))
                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            },
            confirmButton = {},
            shape = RoundedCornerShape(AppCorners.large),
            properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
        )
    }
}

/**
 * Standardized dialog for custom content.
 *
 * @param onDismiss Called when the dialog is dismissed
 * @param title Optional dialog title
 * @param confirmText Text for the confirm button
 * @param onConfirm Called when the confirm button is clicked
 * @param dismissText Optional text for the dismiss button
 * @param content The dialog content
 */
@Composable
fun StandardCustomDialog(
    onDismiss: () -> Unit,
    title: String? = null,
    confirmText: String,
    onConfirm: () -> Unit,
    dismissText: String? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = title?.let {
            {
                Text(
                    text = it,
                    style = MaterialTheme.typography.headlineSmall
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(AppSpacing.medium),
                content = content
            )
        },
        confirmButton = {
            StandardPrimaryButton(
                text = confirmText,
                onClick = {
                    onConfirm()
                    onDismiss()
                }
            )
        },
        dismissButton = dismissText?.let {
            {
                StandardTextButton(
                    text = it,
                    onClick = onDismiss
                )
            }
        },
        shape = RoundedCornerShape(AppCorners.large)
    )
}
