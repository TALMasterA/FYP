package com.example.fyp.screens.learning

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.fyp.core.ConfirmationDialog
import com.example.fyp.core.StandardScreenScaffold
import com.example.fyp.core.rememberUiTextFunctions
import com.example.fyp.model.ui.AppLanguageState
import com.example.fyp.model.ui.BaseUiTexts
import com.example.fyp.model.ui.UiTextKey

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

    val learningUiState by learningViewModel.uiState.collectAsState()
    val isGeneratingMaterials = learningUiState.generatingLanguageCode != null
    val isGeneratingThis = learningUiState.generatingLanguageCode == targetCode
    val isGeneratingAnyQuiz = learningUiState.generatingQuizLanguageCode != null
    val isAnyGenerationOngoing = isGeneratingMaterials || isGeneratingAnyQuiz

    // Get count from learning view model clusters (same source as LearningScreen) instead of separate listener
    val countNowFromCluster = learningUiState.clusters.firstOrNull { it.languageCode == targetCode }?.count ?: 0

    // Previous sheet count (null = first time)
    val previousSheetCount = uiState.historyCountAtGenerate
    // Minimum 5 more records for regeneration
    val minRecordsForRegen = 5
    // Check if count is higher than previous (or first time)
    val isFirstTime = previousSheetCount == null
    val hasEnoughNewRecords = isFirstTime || countNowFromCluster >= (previousSheetCount ?: 0) + minRecordsForRegen
    val countHigherThanPrevious = isFirstTime || countNowFromCluster > (previousSheetCount ?: 0)

    val unchanged = previousSheetCount != null && previousSheetCount == countNowFromCluster
    val regenEnabled = !uiState.isLoading && !isAnyGenerationOngoing && countNowFromCluster > 0 && !unchanged && countHigherThanPrevious && hasEnoughNewRecords


    // Load when entering
    LaunchedEffect(primaryCode, targetCode) {
        viewModel.loadSheet()
    }

    // Reload after generation finishes for this target (so content updates immediately)
    val lastSavedCount = learningUiState.sheetCountByLanguage[targetCode]
    LaunchedEffect(primaryCode, targetCode, lastSavedCount) {
        viewModel.loadSheet()
    }

    StandardScreenScaffold(
        title = t(UiTextKey.LearningSheetTitleTemplate).replace("{speclanguage}", targetName),
        onBack = onBack,
        backContentDescription = t(UiTextKey.NavBack),
        actions = {
            TextButton(
                onClick = { onOpenQuiz() },
                enabled = !uiState.content.isNullOrBlank() && !uiState.isLoading
            ) {
                Text(t(UiTextKey.QuizOpenButton))
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
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
                    val required = previousSheetCount + minRecordsForRegen
                    required - countNowFromCluster
                } else 0

                AlertDialog(
                    onDismissRequest = { showRegenBlockedAlert = false },
                    title = { Text(t(UiTextKey.LearningRegenBlockedTitle)) },
                    text = {
                        Text(
                            t(UiTextKey.LearningRegenBlockedMessage)
                                .replace("{minRecords}", minRecordsForRegen.toString())
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
                // Show hint when regeneration requires more records
                if (!isFirstTime && !hasEnoughNewRecords && countNowFromCluster > 0 && countHigherThanPrevious) {
                    val recordsNeeded = (previousSheetCount ?: 0) + minRecordsForRegen - countNowFromCluster
                    Text(
                        text = t(UiTextKey.LearningRegenNeedMoreRecords)
                            .replace("{needed}", recordsNeeded.toString()),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                // Show hint when count is not higher than previous
                if (!isFirstTime && !countHigherThanPrevious && countNowFromCluster > 0) {
                    Text(
                        text = t(UiTextKey.LearningRegenCountNotHigher),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Allow button to be clickable to show alert when conditions are partially met
                    val canShowBlockedAlert = !isFirstTime && countNowFromCluster > 0 &&
                        (!hasEnoughNewRecords || !countHigherThanPrevious) &&
                        !isAnyGenerationOngoing && !uiState.isLoading

                    Button(
                        onClick = {
                            if (regenEnabled) {
                                showConfirm = true
                            } else if (canShowBlockedAlert) {
                                showRegenBlockedAlert = true
                            }
                        },
                        enabled = regenEnabled || canShowBlockedAlert,
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
        }
    }
}