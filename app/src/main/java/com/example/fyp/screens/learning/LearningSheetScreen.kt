package com.example.fyp.screens.learning

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.fyp.domain.learning.GenerationEligibility
import com.example.fyp.core.ConfirmationDialog
import com.example.fyp.core.StandardScreenScaffold
import com.example.fyp.core.rememberUiTextFunctions
import com.example.fyp.model.ui.AppLanguageState
import com.example.fyp.model.ui.BaseUiTexts
import com.example.fyp.model.ui.UiTextKey
import com.example.fyp.ui.components.FriendSelectorDialog

@Suppress("UNUSED_PARAMETER", "SENSELESS_COMPARISON")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LearningSheetScreen(
    uiLanguages: List<Pair<String, String>>,
    appLanguageState: AppLanguageState,
    onUpdateAppLanguage: (String, Map<UiTextKey, String>) -> Unit,
    primaryCode: String,
    targetCode: String,
    onBack: () -> Unit,
    learningViewModel: LearningViewModel,
    onOpenQuiz: () -> Unit,
    viewModel: LearningSheetViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    val (uiText, uiLanguageNameFor) = rememberUiTextFunctions(appLanguageState)
    val t: (UiTextKey) -> String = { key -> uiText(key, BaseUiTexts[key.ordinal]) }

    val targetName = uiLanguageNameFor(targetCode)
    val primaryName = uiLanguageNameFor(primaryCode)

    var showConfirm by remember { mutableStateOf(false) }
    var showRegenBlockedAlert by remember { mutableStateOf(false) }
    var showRegenInfo by remember { mutableStateOf(false) }

    val learningUiState by learningViewModel.uiState.collectAsState()
    val isGeneratingMaterials = learningUiState.generatingLanguageCode != null
    val isGeneratingThis = learningUiState.generatingLanguageCode == targetCode
    val isGeneratingAnyQuiz = learningUiState.generatingQuizLanguageCode != null
    val isAnyGenerationOngoing = isGeneratingMaterials || isGeneratingAnyQuiz

    // Get count from learning view model clusters (same source as LearningScreen) instead of separate listener
    val countNowFromCluster = learningUiState.clusters.firstOrNull { it.languageCode == targetCode }?.count ?: 0

    // Previous sheet count (null = first time)
    val previousSheetCount = uiState.historyCountAtGenerate

    // Check if regeneration is allowed using domain logic
    val isFirstTime = previousSheetCount == null
    val hasEnoughNewRecords = isFirstTime || GenerationEligibility.canRegenerateLearningSheet(countNowFromCluster, previousSheetCount ?: 0)
    val countHigherThanPrevious = isFirstTime || countNowFromCluster > (previousSheetCount ?: 0)

    val unchanged = previousSheetCount != null && previousSheetCount == countNowFromCluster
    val regenEnabled = !uiState.isLoading && !isAnyGenerationOngoing && countNowFromCluster > 0 && !unchanged && countHigherThanPrevious && hasEnoughNewRecords


    // Reload the sheet whenever: the language pair changes, OR generation just completed
    // (lastSavedCount advances). A single LaunchedEffect is enough — no need for two separate
    // effects that would both fire on initial composition and cause a double read.
    val lastSavedCount = learningUiState.sheetCountByLanguage[targetCode]
    LaunchedEffect(primaryCode, targetCode, lastSavedCount) {
        viewModel.loadSheet()
    }

    StandardScreenScaffold(
        title = t(UiTextKey.LearningSheetTitleTemplate).replace("{speclanguage}", targetName),
        onBack = onBack,
        backContentDescription = t(UiTextKey.NavBack),
        actions = {
            IconButton(onClick = { showRegenInfo = true }) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Regeneration Rules",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            // Share button – only show when there is content to share
            if (!uiState.content.isNullOrBlank()) {
                IconButton(onClick = { viewModel.showShareDialog() }) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = t(UiTextKey.ShareMaterialButton),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
            TextButton(
                onClick = { onOpenQuiz() },
                enabled = !uiState.content.isNullOrBlank() && !uiState.isLoading
            ) {
                Text(t(UiTextKey.QuizOpenButton))
            }
        }
    ) { padding ->

        // Friend selector dialog for sharing
        if (uiState.showShareDialog) {
            FriendSelectorDialog(
                friends = uiState.friends,
                isLoading = uiState.isSharing,
                t = t,
                onFriendSelected = { friendId -> viewModel.shareSheet(friendId) },
                onDismiss = { viewModel.dismissShareDialog() }
            )
        }

        androidx.compose.foundation.layout.Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
            Text(
                text = t(UiTextKey.LearningSheetPrimaryTemplate).replace("{speclanguage}", primaryName),
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = t(UiTextKey.LearningSheetHistoryCountTemplate)
                    .replace("{nowCount}", countNowFromCluster.toString())
                    .replace("{savedCount}", (uiState.historyCountAtGenerate?.toString() ?: "-")),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            uiState.error?.let { err ->
                Text(t(UiTextKey.LearningErrorTemplate).format(err), color = MaterialTheme.colorScheme.error)
            }

            if (showConfirm) {
                ConfirmationDialog(
                    title = t(UiTextKey.DialogGenerateOverwriteTitle),
                    message = t(UiTextKey.DialogGenerateOverwriteMessageTemplate)
                        .replace("{speclanguage}", targetName),
                    confirmText = t(UiTextKey.ActionConfirm),
                    cancelText = t(UiTextKey.ActionCancel),
                    onConfirm = {
                        showConfirm = false
                        learningViewModel.generateFor(targetCode)
                    },
                    onDismiss = { showConfirm = false }
                )
            }

            // Alert dialog when regeneration is blocked due to insufficient records
            if (showRegenBlockedAlert) {
                val recordsNeeded = if (previousSheetCount != null) {
                    val required = previousSheetCount + GenerationEligibility.MIN_RECORDS_FOR_LEARNING_SHEET
                    (required - countNowFromCluster).coerceAtLeast(0)
                } else 0

                AlertDialog(
                    onDismissRequest = { showRegenBlockedAlert = false },
                    title = { Text(t(UiTextKey.LearningRegenBlockedTitle)) },
                    text = {
                        Text(
                            t(UiTextKey.LearningRegenBlockedMessage)
                                .replace("{needed}", recordsNeeded.toString())
                        )
                    },
                    confirmButton = {
                        Button(onClick = { showRegenBlockedAlert = false }) {
                            Text(t(UiTextKey.ActionConfirm))
                        }
                    }
                )
            }


            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Info dialog explaining regeneration rules
                if (showRegenInfo) {
                    AlertDialog(
                        onDismissRequest = { showRegenInfo = false },
                        title = { Text(t(UiTextKey.LearningRegenInfoTitle)) },
                        text = { Text(t(UiTextKey.LearningRegenInfoMessage)) },
                        confirmButton = {
                            Button(onClick = { showRegenInfo = false }) {
                                Text(t(UiTextKey.ActionConfirm))
                            }
                        }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            if (regenEnabled) {
                                showConfirm = true
                            }
                        },
                        enabled = regenEnabled,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            when {
                                isGeneratingThis -> t(UiTextKey.LearningSheetGenerating)
                                isAnyGenerationOngoing -> "⏳ Wait..."
                                else -> t(UiTextKey.LearningSheetRegenerate)
                            }
                        )
                    }

                    Button(
                        onClick = { learningViewModel.cancelGenerate() },
                        enabled = isGeneratingThis,
                        modifier = Modifier.weight(1f)
                    ) { Text(t(UiTextKey.ActionCancel)) }
                }


                uiState.quizError?.let { err ->
                    Text(err, color = MaterialTheme.colorScheme.error)
                }
            }

            val content = uiState.content
            if (content.isNullOrBlank()) {
                Text(t(UiTextKey.LearningSheetNoContent))
            } else {
                Text(
                    text = content,
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                )
            }
        } // end Column

        // Share feedback snackbar
        uiState.shareSuccess?.let { msg ->
            LaunchedEffect(msg) {
                kotlinx.coroutines.delay(3000)
                viewModel.clearShareMessages()
            }
            Snackbar(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
            ) { Text(msg) }
        }
        uiState.shareError?.let { err ->
            LaunchedEffect(err) {
                kotlinx.coroutines.delay(3000)
                viewModel.clearShareMessages()
            }
            Snackbar(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp),
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.onErrorContainer
            ) { Text(err) }
        }
    } // end Box
    }
}

