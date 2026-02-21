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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
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
import com.example.fyp.core.StandardScreenScaffold
import com.example.fyp.core.rememberUiTextFunctions
import com.example.fyp.model.friends.SharedItemType
import com.example.fyp.model.ui.AppLanguageState
import com.example.fyp.model.ui.BaseUiTexts
import com.example.fyp.model.ui.UiTextKey

/**
 * Full-screen view of a shared learning material or quiz.
 *
 * Uses [StandardScreenScaffold] for a consistent top app bar with back arrow,
 * matching all other screens. The title shows who shared the item.
 *
 * Uses a dedicated [SharedMaterialDetailViewModel] that independently fetches
 * the item and its full content from Firestore, so it works regardless of
 * whether SharedInboxViewModel has the item in memory.
 */
@Composable
fun SharedMaterialDetailScreen(
    itemId: String,
    appLanguageState: AppLanguageState,
    onBack: () -> Unit,
    viewModel: SharedMaterialDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val (uiText, uiLanguageNameFor) = rememberUiTextFunctions(appLanguageState)
    val t: (UiTextKey) -> String = { key -> uiText(key, BaseUiTexts[key.ordinal]) }

    // ── Loading ──────────────────────────────────────────────────────────────
    if (uiState.isLoading) {
        StandardScreenScaffold(
            title = t(UiTextKey.ShareInboxTitle),
            onBack = onBack,
            backContentDescription = t(UiTextKey.NavBack)
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        return
    }

    // ── Error / not found ────────────────────────────────────────────────────
    val item = uiState.item
    if (item == null) {
        StandardScreenScaffold(
            title = t(UiTextKey.ShareInboxTitle),
            onBack = onBack,
            backContentDescription = t(UiTextKey.NavBack)
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = uiState.error ?: t(UiTextKey.ShareItemNotFound),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        return
    }

    // ── Content ──────────────────────────────────────────────────────────────
    val content = item.content
    val rawTitle = content["title"] as? String ?: content["sourceText"] as? String ?: ""

    // Derive language codes for the header
    val sourceLangCode: String
    val targetLangCode: String
    when (item.type) {
        SharedItemType.WORD -> {
            sourceLangCode = content["sourceLang"] as? String ?: ""
            targetLangCode = content["targetLang"] as? String ?: ""
        }
        SharedItemType.LEARNING_SHEET, SharedItemType.QUIZ -> {
            // Title format: "Learning Sheet: en-US → zh-CN"
            val arrow = rawTitle.indexOf(" → ")
            if (arrow != -1) {
                val colonIdx = rawTitle.indexOf(": ")
                sourceLangCode = if (colonIdx != -1) rawTitle.substring(colonIdx + 2, arrow).trim() else ""
                targetLangCode = rawTitle.substring(arrow + 3).trim()
            } else {
                sourceLangCode = ""
                targetLangCode = ""
            }
        }
    }
    val sourceLangName = if (sourceLangCode.isNotBlank()) uiLanguageNameFor(sourceLangCode) else ""
    val targetLangName = if (targetLangCode.isNotBlank()) uiLanguageNameFor(targetLangCode) else ""
    val langHeader = when {
        sourceLangName.isNotBlank() && targetLangName.isNotBlank() -> "$sourceLangName → $targetLangName"
        else -> rawTitle
    }

    // Full content: prefer fetched sub-doc, fall back to description preview
    val description = content["description"] as? String ?: ""
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

    // Screen title: "Shared by <sender>" using i18n key; fallback to item type title if no sender
    val senderName = item.fromUsername.takeIf { it.isNotBlank() }
        ?: (item.content["fromUsername"] as? String)?.takeIf { it.isNotBlank() }
    val screenTitle = if (senderName != null) {
        t(UiTextKey.ShareReceivedFrom).replace("{username}", senderName)
    } else {
        t(UiTextKey.ShareInboxTitle)
    }

    StandardScreenScaffold(
        title = screenTitle,
        onBack = onBack,
        backContentDescription = t(UiTextKey.NavBack)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Language header: sourceLang → targetLang
            if (langHeader.isNotBlank()) {
                Text(
                    text = langHeader,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
            }

            HorizontalDivider()

            // ── Body ─────────────────────────────────────────────────────────
            when (item.type) {
                SharedItemType.WORD -> {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = rawTitle,
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
}