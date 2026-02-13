package com.example.fyp.screens.favorites

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.fyp.core.PaginationRow
import com.example.fyp.core.StandardScreenScaffold
import com.example.fyp.core.pageCount
import com.example.fyp.core.rememberUiTextFunctions
import com.example.fyp.core.UiConstants
import com.example.fyp.core.rememberHapticFeedback
import com.example.fyp.model.ui.AppLanguageState
import com.example.fyp.model.ui.BaseUiTexts
import com.example.fyp.model.FavoriteRecord
import com.example.fyp.model.ui.UiTextKey
import kotlinx.coroutines.delay

@Composable
fun FavoritesScreen(
    appLanguageState: AppLanguageState,
    onBack: () -> Unit,
    viewModel: FavoritesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val (uiText, uiLanguageNameFor) = rememberUiTextFunctions(appLanguageState)
    val t: (UiTextKey) -> String = { key -> uiText(key, BaseUiTexts[key.ordinal]) }
    val haptic = rememberHapticFeedback()

    // Pagination state
    var currentPage by remember { mutableIntStateOf(0) }
    val pageSize = 10
    val totalPages = pageCount(uiState.favorites.size, pageSize)
    val pagedFavorites = uiState.favorites
        .drop(currentPage * pageSize)
        .take(pageSize)

    // Auto-dismiss error after delay
    LaunchedEffect(uiState.error) {
        if (uiState.error != null) {
            delay(UiConstants.ERROR_AUTO_DISMISS_MS)
            viewModel.clearError()
        }
    }

    StandardScreenScaffold(
        title = t(UiTextKey.FavoritesTitle),
        onBack = onBack,
        backContentDescription = t(UiTextKey.NavBack)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Error message card (auto-dismisses after 3 seconds)
            uiState.error?.let { errorMsg ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = errorMsg,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            if (uiState.favorites.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = t(UiTextKey.FavoritesEmpty),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                // Favorites list with pagination
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(pagedFavorites, key = { it.id }) { favorite ->
                        FavoriteCard(
                            favorite = favorite,
                            languageNameFor = uiLanguageNameFor,
                            onSpeakSource = {
                                haptic.click()
                                viewModel.speak(favorite.sourceText, favorite.sourceLang, favorite.id + "_source")
                            },
                            onSpeakTarget = {
                                haptic.click()
                                viewModel.speak(favorite.targetText, favorite.targetLang, favorite.id + "_target")
                            },
                            onDelete = {
                                haptic.reject()
                                viewModel.removeFavorite(favorite.id)
                            },
                            isSpeakingSource = uiState.speakingId == (favorite.id + "_source"),
                            isSpeakingTarget = uiState.speakingId == (favorite.id + "_target"),
                            t = t
                        )
                    }
                }

                // Show "Load More" button if there are more favorites (Lazy Loading - Priority 2 #9)
                if (uiState.hasMore && currentPage == totalPages - 1) {
                    Button(
                        onClick = {
                            viewModel.loadMoreFavorites()
                            // Reset to first page to show newly loaded items
                            currentPage = 0
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(text = "Load More")
                    }
                }

                // Pagination controls
                if (totalPages > 1) {
                    PaginationRow(
                        page = currentPage,
                        totalPages = totalPages,
                        prevLabel = uiText(UiTextKey.PaginationPrevLabel, BaseUiTexts[UiTextKey.PaginationPrevLabel.ordinal]),
                        nextLabel = uiText(UiTextKey.PaginationNextLabel, BaseUiTexts[UiTextKey.PaginationNextLabel.ordinal]),
                        pageLabelTemplate = uiText(
                            UiTextKey.PaginationPageLabelTemplate,
                            BaseUiTexts[UiTextKey.PaginationPageLabelTemplate.ordinal]
                        ),
                        onPrev = { if (currentPage > 0) currentPage-- },
                        onNext = { if (currentPage < totalPages - 1) currentPage++ },
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun FavoriteCard(
    favorite: FavoriteRecord,
    languageNameFor: (String) -> String,
    onSpeakSource: () -> Unit,
    onSpeakTarget: () -> Unit,
    onDelete: () -> Unit,
    isSpeakingSource: Boolean,
    isSpeakingTarget: Boolean,
    t: (UiTextKey) -> String
) {
    var showDeleteConfirm by remember { mutableStateOf(false) }

    // Delete confirmation dialog
    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text(t(UiTextKey.ActionDelete)) },
            text = { Text("Are you sure you want to delete this favorite?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete()
                        showDeleteConfirm = false
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text(t(UiTextKey.ActionDelete))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text(t(UiTextKey.ActionCancel))
                }
            }
        )
    }

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 3.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header row with language info and delete button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = "${languageNameFor(favorite.sourceLang)} → ${languageNameFor(favorite.targetLang)}",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f)
                )
                IconButton(
                    onClick = { showDeleteConfirm = true },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Source text with speak button
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = favorite.sourceText,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onSpeakSource) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                        contentDescription = "Speak source",
                        tint = if (isSpeakingSource) MaterialTheme.colorScheme.primary
                               else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Target text with speak button
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = favorite.targetText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onSpeakTarget) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                        contentDescription = "Speak translation",
                        tint = if (isSpeakingTarget) MaterialTheme.colorScheme.primary
                               else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Note if present
            if (favorite.note.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "📝 ${favorite.note}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}
