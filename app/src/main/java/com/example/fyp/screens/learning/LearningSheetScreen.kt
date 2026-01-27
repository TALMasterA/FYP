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
import com.example.fyp.core.StandardScreenScaffold
import com.example.fyp.core.rememberUiTextFunctions
import com.example.fyp.model.AppLanguageState
import com.example.fyp.model.BaseUiTexts
import com.example.fyp.model.UiTextKey

@Suppress("UNUSED_PARAMETER")
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

    val learningUiState by learningViewModel.uiState.collectAsState()
    val isGeneratingMaterials = learningUiState.generatingLanguageCode != null
    val isGeneratingThis = learningUiState.generatingLanguageCode == targetCode
    val isGeneratingAnyQuiz = learningUiState.generatingQuizLanguageCode != null
    val isAnyGenerationOngoing = isGeneratingMaterials || isGeneratingAnyQuiz

    val unchanged = uiState.historyCountAtGenerate != null && uiState.historyCountAtGenerate == uiState.countNow
    val regenEnabled = !uiState.isLoading && !isAnyGenerationOngoing && uiState.countNow > 0 && !unchanged


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
                    .replace("{nowCount}", uiState.countNow.toString())
                    .replace("{savedCount}", (uiState.historyCountAtGenerate?.toString() ?: "-")),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            uiState.error?.let { err ->
                Text(t(UiTextKey.LearningErrorTemplate).format(err), color = MaterialTheme.colorScheme.error)
            }

            if (showConfirm) {
                AlertDialog(
                    onDismissRequest = { showConfirm = false },
                    title = { Text(t(UiTextKey.DialogGenerateOverwriteTitle)) },
                    text = {
                        Text(
                            t(UiTextKey.DialogGenerateOverwriteMessageTemplate)
                                .replace("{speclanguage}", targetName)
                        )
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            showConfirm = false
                            learningViewModel.generateFor(targetCode)
                        }) { Text(t(UiTextKey.ActionConfirm)) }
                    },
                    dismissButton = {
                        TextButton(onClick = { showConfirm = false }) {
                            Text(t(UiTextKey.ActionCancel))
                        }
                    }
                )
            }


            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { showConfirm = true },
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
        }
    }
}