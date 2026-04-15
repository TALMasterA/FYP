package com.example.fyp.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.fyp.ui.theme.AppCorners
import com.example.fyp.ui.theme.AppSpacing

/**
 * Standardized primary button for consistent styling across the app.
 * Use for primary actions (submit, save, confirm, etc.)
 *
 * @param text The button text
 * @param onClick The click handler
 * @param modifier Optional modifier
 * @param enabled Whether the button is enabled
 * @param icon Optional leading icon
 */
@Composable
fun StandardPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: ImageVector? = null
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        enabled = enabled,
        shape = RoundedCornerShape(AppCorners.medium),
        contentPadding = PaddingValues(horizontal = AppSpacing.large, vertical = AppSpacing.medium)
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(AppSpacing.small))
        }
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge
        )
    }
}

/**
 * Standardized text button for tertiary actions.
 * Use for less prominent actions (skip, dismiss, etc.)
 *
 * @param text The button text
 * @param onClick The click handler
 * @param modifier Optional modifier
 * @param enabled Whether the button is enabled
 * @param icon Optional leading icon
 */
@Composable
fun StandardTextButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: ImageVector? = null
) {
    TextButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        contentPadding = PaddingValues(horizontal = AppSpacing.medium, vertical = AppSpacing.small)
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(AppSpacing.extraSmall))
        }
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium
        )
    }
}
