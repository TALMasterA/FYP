package com.example.fyp.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.fyp.ui.theme.AppCorners
import com.example.fyp.ui.theme.AppSpacing

/**
 * Standardized text field for consistent styling across the app.
 *
 * @param value The current text value
 * @param onValueChange Called when the text changes
 * @param label The label text
 * @param modifier Optional modifier
 * @param placeholder Optional placeholder text
 * @param leadingIcon Optional leading icon
 * @param trailingIcon Optional trailing icon
 * @param isError Whether the field has an error
 * @param supportingText Optional supporting/error text below the field
 * @param enabled Whether the field is enabled
 * @param readOnly Whether the field is read-only
 * @param singleLine Whether the field is single-line
 * @param maxLines Maximum number of lines (for multi-line fields)
 * @param keyboardOptions Keyboard configuration
 * @param keyboardActions Keyboard action handlers
 */
@Composable
fun StandardTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null,
    onTrailingIconClick: (() -> Unit)? = null,
    isError: Boolean = false,
    supportingText: String? = null,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    singleLine: Boolean = true,
    maxLines: Int = 1,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = modifier.fillMaxWidth(),
        placeholder = placeholder?.let { { Text(it) } },
        leadingIcon = leadingIcon?.let {
            {
                Icon(
                    imageVector = it,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
            }
        },
        trailingIcon = if (trailingIcon != null && onTrailingIconClick != null) {
            {
                IconButton(onClick = onTrailingIconClick) {
                    Icon(
                        imageVector = trailingIcon,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        } else if (trailingIcon != null) {
            {
                Icon(
                    imageVector = trailingIcon,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
            }
        } else null,
        isError = isError,
        supportingText = supportingText?.let { { Text(it) } },
        enabled = enabled,
        readOnly = readOnly,
        singleLine = singleLine,
        maxLines = maxLines,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        shape = RoundedCornerShape(AppCorners.medium)
    )
}

/**
 * Standardized password field with show/hide toggle.
 *
 * @param value The current password value
 * @param onValueChange Called when the password changes
 * @param label The label text
 * @param modifier Optional modifier
 * @param isError Whether the field has an error
 * @param supportingText Optional supporting/error text below the field
 * @param enabled Whether the field is enabled
 * @param keyboardActions Keyboard action handlers
 */
@Composable
fun StandardPasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    supportingText: String? = null,
    enabled: Boolean = true,
    keyboardActions: KeyboardActions = KeyboardActions.Default
) {
    var passwordVisible by remember { mutableStateOf(false) }

    StandardTextField(
        value = value,
        onValueChange = onValueChange,
        label = label,
        modifier = modifier,
        trailingIcon = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
        onTrailingIconClick = { passwordVisible = !passwordVisible },
        isError = isError,
        supportingText = supportingText,
        enabled = enabled,
        singleLine = true,
        keyboardOptions = KeyboardOptions.Default.copy(
            autoCorrect = false
        ),
        keyboardActions = keyboardActions
    )
}

/**
 * Standardized search field with clear button.
 *
 * @param value The current search query
 * @param onValueChange Called when the query changes
 * @param label The label text
 * @param modifier Optional modifier
 * @param placeholder Optional placeholder text
 * @param leadingIcon Optional leading search icon
 * @param onClear Called when the clear button is clicked
 * @param keyboardActions Keyboard action handlers
 */
@Composable
fun StandardSearchField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    leadingIcon: ImageVector? = null,
    onClear: () -> Unit = { onValueChange("") },
    keyboardActions: KeyboardActions = KeyboardActions.Default
) {
    StandardTextField(
        value = value,
        onValueChange = onValueChange,
        label = label,
        modifier = modifier,
        placeholder = placeholder,
        leadingIcon = leadingIcon,
        trailingIcon = if (value.isNotEmpty()) Icons.Default.Clear else null,
        onTrailingIconClick = if (value.isNotEmpty()) onClear else null,
        singleLine = true,
        keyboardActions = keyboardActions
    )
}

/**
 * Standardized multi-line text field for longer text input.
 *
 * @param value The current text value
 * @param onValueChange Called when the text changes
 * @param label The label text
 * @param modifier Optional modifier
 * @param placeholder Optional placeholder text
 * @param minLines Minimum number of lines
 * @param maxLines Maximum number of lines
 * @param isError Whether the field has an error
 * @param supportingText Optional supporting/error text below the field
 * @param enabled Whether the field is enabled
 */
@Composable
fun StandardMultiLineTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    minLines: Int = 3,
    maxLines: Int = 6,
    isError: Boolean = false,
    supportingText: String? = null,
    enabled: Boolean = true
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = (minLines * 24).dp),
        placeholder = placeholder?.let { { Text(it) } },
        isError = isError,
        supportingText = supportingText?.let { { Text(it) } },
        enabled = enabled,
        singleLine = false,
        maxLines = maxLines,
        shape = RoundedCornerShape(AppCorners.medium)
    )
}
