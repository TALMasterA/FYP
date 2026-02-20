package com.example.fyp.screens.friends

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Article
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.fyp.core.StandardScreenScaffold
import com.example.fyp.core.rememberUiTextFunctions
import com.example.fyp.model.friends.SharedItemType
import com.example.fyp.model.ui.AppLanguageState
import com.example.fyp.model.ui.BaseUiTexts
import com.example.fyp.model.ui.UiTextKey
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * Full-screen view of a shared learning material or quiz.
 * Replaces the AlertDialog to provide a proper scrollable reading experience.
 */
@Composable
fun SharedMaterialDetailScreen(
    itemId: String,
    appLanguageState: AppLanguageState,
    onBack: () -> Unit,
    viewModel: SharedInboxViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val (uiText, _) = rememberUiTextFunctions(appLanguageState)
    val t: (UiTextKey) -> String = { key -> uiText(key, BaseUiTexts[key.ordinal]) }

    // Clear the red dot for this item as soon as the user opens the detail screen
    LaunchedEffect(itemId) {
        viewModel.markItemSeen(itemId)
        // Trigger fetch of full content from sub-document (async; cached after first load)
        viewModel.loadFullContent(itemId)
    }

    // Find the item in the current list
    val item = uiState.sharedItems.firstOrNull { it.itemId == itemId }

    StandardScreenScaffold(
        title = when (item?.type) {
            SharedItemType.LEARNING_SHEET -> t(UiTextKey.ShareTypeLearningSheet)
            SharedItemType.QUIZ -> t(UiTextKey.ShareTypeQuiz)
            SharedItemType.WORD -> t(UiTextKey.ShareTypeWord)
            null -> t(UiTextKey.ShareInboxTitle)
        },
        onBack = onBack
    ) { padding ->
        if (item == null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = t(UiTextKey.ShareItemNotFound),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            return@StandardScreenScaffold
        }

        val content = item.content
        val title = content["title"] as? String ?: content["sourceText"] as? String ?: ""
        // Full content from sub-document (fetched async); fall back to description preview
        val fetchedFullContent = uiState.fullContentMap[itemId]
        val contentFetchComplete = uiState.fullContentMap.containsKey(itemId)
        val inlineFullContent = content["fullContent"] as? String ?: ""
        val description = content["description"] as? String ?: ""
        // Priority: fetched sub-doc > inline > description preview
        val bodyText = when {
            !fetchedFullContent.isNullOrBlank() -> fetchedFullContent
            inlineFullContent.isNotBlank() -> inlineFullContent
            else -> description
        }
        // Show loading only while the fetch is still in-flight (key not yet in map)
        val isLoadingContent = (item.type == SharedItemType.LEARNING_SHEET || item.type == SharedItemType.QUIZ)
            && !contentFetchComplete && inlineFullContent.isBlank()
        val targetText = content["targetText"] as? String ?: ""
        val notes = content["notes"] as? String ?: ""

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Item type chip + icon
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                androidx.compose.material3.Icon(
                    imageVector = when (item.type) {
                        SharedItemType.WORD -> Icons.Default.Book
                        SharedItemType.LEARNING_SHEET -> Icons.AutoMirrored.Filled.Article
                        SharedItemType.QUIZ -> Icons.Default.Quiz
                    },
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = when (item.type) {
                        SharedItemType.WORD -> t(UiTextKey.ShareTypeWord)
                        SharedItemType.LEARNING_SHEET -> t(UiTextKey.ShareTypeLearningSheet)
                        SharedItemType.QUIZ -> t(UiTextKey.ShareTypeQuiz)
                    },
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Title
            if (title.isNotBlank()) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            }

            // Sender + timestamp
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = t(UiTextKey.ShareReceivedFrom).replace("{username}", item.fromUsername),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = formatDetailTimestamp(item.createdAt.toDate()),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            HorizontalDivider()

            // Content body
            when (item.type) {
                SharedItemType.WORD -> {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                        if (targetText.isNotBlank()) {
                            Text(
                                text = targetText,
                                style = MaterialTheme.typography.headlineMedium,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        if (notes.isNotBlank()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = notes,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                SharedItemType.LEARNING_SHEET, SharedItemType.QUIZ -> {
                    when {
                        isLoadingContent -> {
                            // Full content is being fetched from sub-document
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                                Text(
                                    text = "Loading content...",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        bodyText.isNotBlank() -> {
                            Text(
                                text = bodyText,
                                style = MaterialTheme.typography.bodyLarge,
                                lineHeight = MaterialTheme.typography.bodyLarge.lineHeight
                            )
                        }
                        else -> {
                            Text(
                                text = t(UiTextKey.ShareNoContent),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Extra bottom padding for comfortable reading
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

private fun formatDetailTimestamp(date: Date): String {
    val now = Calendar.getInstance()
    val target = Calendar.getInstance().apply { time = date }
    return when {
        now.get(Calendar.YEAR) == target.get(Calendar.YEAR) &&
        now.get(Calendar.DAY_OF_YEAR) == target.get(Calendar.DAY_OF_YEAR) ->
            "Today, " + SimpleDateFormat("HH:mm", Locale.getDefault()).format(date)
        now.get(Calendar.YEAR) == target.get(Calendar.YEAR) ->
            SimpleDateFormat("MMM d, HH:mm", Locale.getDefault()).format(date)
        else ->
            SimpleDateFormat("MMM d, yyyy HH:mm", Locale.getDefault()).format(date)
    }
}

