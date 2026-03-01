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
 * Standardized secondary button for alternate actions.
 * Use for secondary actions (cancel, back, etc.)
 *
 * @param text The button text
 * @param onClick The click handler
 * @param modifier Optional modifier
 * @param enabled Whether the button is enabled
 * @param icon Optional leading icon
 */
@Composable
fun StandardSecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: ImageVector? = null
) {
    OutlinedButton(
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

/**
 * Standardized icon button for actions with only an icon.
 *
 * @param icon The icon to display
 * @param contentDescription Accessibility description
 * @param onClick The click handler
 * @param modifier Optional modifier
 * @param enabled Whether the button is enabled
 */
@Composable
fun StandardIconButton(
    icon: ImageVector,
    contentDescription: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    IconButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            modifier = Modifier.size(24.dp)
        )
    }
}

/**
 * Standardized floating action button.
 *
 * @param icon The icon to display
 * @param contentDescription Accessibility description
 * @param onClick The click handler
 * @param modifier Optional modifier
 */
@Composable
fun StandardFAB(
    icon: ImageVector,
    contentDescription: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FloatingActionButton(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(AppCorners.large)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription
        )
    }
}

/**
 * Standardized extended FAB with text and icon.
 *
 * @param text The button text
 * @param icon The icon to display
 * @param onClick The click handler
 * @param modifier Optional modifier
 */
@Composable
fun StandardExtendedFAB(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ExtendedFloatingActionButton(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(AppCorners.large)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null
        )
        Spacer(modifier = Modifier.width(AppSpacing.small))
        Text(text = text)
    }
}
