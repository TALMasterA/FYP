package com.example.fyp.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/**
 * Enhanced empty state component with icon, title, message, and optional action.
 * Provides better UX than simple text messages.
 */
@Composable
fun EmptyStateView(
    icon: ImageVector,
    title: String,
    message: String,
    modifier: Modifier = Modifier,
    actionLabel: String? = null,
    onActionClick: (() -> Unit)? = null
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Icon
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Title
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Message
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        // Optional action button
        if (actionLabel != null && onActionClick != null) {
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onActionClick,
                modifier = Modifier.widthIn(min = 120.dp)
            ) {
                Text(actionLabel)
            }
        }
    }
}

/**
 * Preset empty states for common scenarios
 */
object EmptyStates {
    @Composable
    fun NoHistory(
        message: String,
        modifier: Modifier = Modifier
    ) {
        EmptyStateView(
            icon = Icons.Filled.History,
            title = "No History Yet",
            message = message,
            modifier = modifier
        )
    }

    @Composable
    fun NoFavorites(
        message: String,
        modifier: Modifier = Modifier,
        actionLabel: String? = null,
        onActionClick: (() -> Unit)? = null
    ) {
        EmptyStateView(
            icon = Icons.Filled.FavoriteBorder,
            title = "No Favorites",
            message = message,
            modifier = modifier,
            actionLabel = actionLabel,
            onActionClick = onActionClick
        )
    }

    @Composable
    fun NoWords(
        message: String,
        modifier: Modifier = Modifier,
        actionLabel: String? = null,
        onActionClick: (() -> Unit)? = null
    ) {
        EmptyStateView(
            icon = Icons.Filled.LibraryBooks,
            title = "No Words Yet",
            message = message,
            modifier = modifier,
            actionLabel = actionLabel,
            onActionClick = onActionClick
        )
    }

    @Composable
    fun NoLearningSheets(
        message: String,
        modifier: Modifier = Modifier,
        actionLabel: String? = null,
        onActionClick: (() -> Unit)? = null
    ) {
        EmptyStateView(
            icon = Icons.Filled.School,
            title = "No Learning Materials",
            message = message,
            modifier = modifier,
            actionLabel = actionLabel,
            onActionClick = onActionClick
        )
    }

    @Composable
    fun NoRecords(
        title: String,
        message: String,
        icon: ImageVector = Icons.Filled.Info,
        modifier: Modifier = Modifier
    ) {
        EmptyStateView(
            icon = icon,
            title = title,
            message = message,
            modifier = modifier
        )
    }
}
