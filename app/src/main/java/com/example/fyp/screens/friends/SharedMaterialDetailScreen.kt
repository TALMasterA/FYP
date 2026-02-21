package com.example.fyp.screens.friends

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
 *
 * Uses a dedicated [SharedMaterialDetailViewModel] that independently fetches
 * the item and its full content from Firestore, so it works regardless of
 * whether SharedInboxViewModel has the item in memory.
 *
 * No top app bar header — just a back arrow inline with the content.
 */
@Composable
fun SharedMaterialDetailScreen(
    itemId: String,
    appLanguageState: AppLanguageState,
    onBack: () -> Unit,
    viewModel: SharedMaterialDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val (uiText, _) = rememberUiTextFunctions(appLanguageState)
    val t: (UiTextKey) -> String = { key -> uiText(key, BaseUiTexts[key.ordinal]) }

    // ── Loading ──────────────────────────────────────────────────────────────
    if (uiState.isLoading) {
        Box(modifier = Modifier.fillMaxSize()) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
        return
    }

    // ── Error / not found ────────────────────────────────────────────────────
    val item = uiState.item
    if (item == null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = uiState.error ?: t(UiTextKey.ShareItemNotFound),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        return
    }

    // ── Content ──────────────────────────────────────────────────────────────
    val content = item.content
    val title = content["title"] as? String ?: content["sourceText"] as? String ?: ""
    val description = content["description"] as? String ?: ""

    // Full content: prefer fetched sub-doc, fall back to description preview
    val fetchedFullContent = uiState.fullContent
    val isContentLoading = fetchedFullContent == null
        && (item.type == SharedItemType.LEARNING_SHEET || item.type == SharedItemType.QUIZ)
    val bodyText = when {
        !fetchedFullContent.isNullOrBlank() -> fetchedFullContent
        description.isNotBlank()            -> description
        else                                -> ""
    }

    val targetText = content["targetText"] as? String ?: ""
    val notes = content["notes"] as? String ?: ""

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Back button (no header/app bar)
        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back"
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

        // ── Body ─────────────────────────────────────────────────────────────
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
                    isContentLoading -> {
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

        Spacer(modifier = Modifier.height(32.dp))
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
