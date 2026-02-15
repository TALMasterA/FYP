package com.example.fyp.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.fyp.ui.theme.AppCorners
import com.example.fyp.ui.theme.AppElevation
import com.example.fyp.ui.theme.AppSpacing

/**
 * Standardized empty state component for consistent UX across the app.
 * Shows an icon, title, and message when lists or content areas are empty.
 *
 * @param icon The icon to display (e.g., Icons.Default.Translate)
 * @param title The title text (e.g., "No History")
 * @param message The descriptive message
 * @param modifier Optional modifier
 * @param action Optional action button composable
 */
@Composable
fun StandardEmptyState(
    icon: ImageVector,
    title: String,
    message: String,
    modifier: Modifier = Modifier,
    action: (@Composable () -> Unit)? = null
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(AppSpacing.xxLarge),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(AppSpacing.large)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        action?.invoke()
    }
}

/**
 * Standardized error card for consistent error display across the app.
 * Shows an error message with optional retry button.
 *
 * @param message The error message to display
 * @param modifier Optional modifier
 * @param onRetry Optional retry action
 * @param retryText Optional retry button text (defaults to "Retry")
 */
@Composable
fun StandardErrorCard(
    message: String,
    modifier: Modifier = Modifier,
    onRetry: (() -> Unit)? = null,
    retryText: String = "Retry"
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
        ),
        shape = RoundedCornerShape(AppCorners.medium)
    ) {
        Column(
            modifier = Modifier.padding(AppSpacing.large),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.medium)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(AppSpacing.small),
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    imageVector = Icons.Default.Error,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = message,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1f)
                )
            }
            onRetry?.let {
                TextButton(
                    onClick = it,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text(retryText)
                }
            }
        }
    }
}

/**
 * Standardized info card for displaying informational messages.
 *
 * @param message The info message to display
 * @param modifier Optional modifier
 * @param icon Optional icon (defaults to Info icon)
 */
@Composable
fun StandardInfoCard(
    message: String,
    modifier: Modifier = Modifier,
    icon: ImageVector = Icons.Default.Info
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        ),
        shape = RoundedCornerShape(AppCorners.medium)
    ) {
        Row(
            modifier = Modifier.padding(AppSpacing.large),
            horizontalArrangement = Arrangement.spacedBy(AppSpacing.medium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = message,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

/**
 * Standardized ElevatedCard with consistent styling.
 * Uses app-wide standard corner radius and elevation.
 *
 * @param modifier Optional modifier
 * @param onClick Optional click handler (makes card clickable)
 * @param enabled Whether the card is enabled (for clickable cards)
 * @param content The card content
 */
@Composable
fun StandardElevatedCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    content: @Composable ColumnScope.() -> Unit
) {
    if (onClick != null) {
        ElevatedCard(
            onClick = onClick,
            enabled = enabled,
            modifier = modifier,
            shape = RoundedCornerShape(AppCorners.large),
            elevation = CardDefaults.elevatedCardElevation(
                defaultElevation = AppElevation.medium,
                pressedElevation = 6.dp
            ),
            colors = CardDefaults.elevatedCardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer
            ),
            content = content
        )
    } else {
        ElevatedCard(
            modifier = modifier,
            shape = RoundedCornerShape(AppCorners.large),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = AppElevation.medium),
            colors = CardDefaults.elevatedCardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer
            ),
            content = content
        )
    }
}

/**
 * Standardized Card with consistent styling.
 * Uses app-wide standard corner radius.
 *
 * @param modifier Optional modifier
 * @param onClick Optional click handler (makes card clickable)
 * @param enabled Whether the card is enabled (for clickable cards)
 * @param containerColor Optional container color override
 * @param content The card content
 */
@Composable
fun StandardCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    containerColor: androidx.compose.ui.graphics.Color? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val colors = if (containerColor != null) {
        CardDefaults.cardColors(containerColor = containerColor)
    } else {
        CardDefaults.cardColors()
    }

    if (onClick != null) {
        Card(
            onClick = onClick,
            enabled = enabled,
            modifier = modifier,
            shape = RoundedCornerShape(AppCorners.medium),
            colors = colors,
            content = content
        )
    } else {
        Card(
            modifier = modifier,
            shape = RoundedCornerShape(AppCorners.medium),
            colors = colors,
            content = content
        )
    }
}

/**
 * Object containing standardized card defaults for the app.
 * Ensures consistent card styling across all screens.
 */
object StandardCardDefaults {
    /**
     * Standard shape for elevated cards (16dp corner radius)
     */
    val elevatedShape = RoundedCornerShape(AppCorners.large)

    /**
     * Standard shape for regular cards (12dp corner radius)
     */
    val standardShape = RoundedCornerShape(AppCorners.medium)

    /**
     * Standard elevation for elevated cards
     */
    val standardElevation = AppElevation.medium

    /**
     * Elevated elevation for pressed state
     */
    val pressedElevation = 6.dp
}

