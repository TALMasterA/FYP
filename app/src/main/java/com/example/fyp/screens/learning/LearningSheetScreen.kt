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
    val isGeneratingAny = learningUiState.generatingLanguageCode != null
    val isGeneratingThis = learningUiState.generatingLanguageCode == targetCode

    val unchanged = uiState.historyCountAtGenerate != null && uiState.historyCountAtGenerate == uiState.countNow
    val regenEnabled = !uiState.isLoading && !isGeneratingAny && uiState.countNow > 0 && !unchanged

    var pendingOpenQuiz by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.quizLoading, uiState.quizError, uiState.quizQuestions.size) {
        if (!pendingOpenQuiz) return@LaunchedEffect

        if (!uiState.quizLoading && uiState.quizError != null) {
            pendingOpenQuiz = false
            return@LaunchedEffect
        }

        if (!uiState.quizLoading && uiState.quizError == null && uiState.quizQuestions.isNotEmpty()) {
            pendingOpenQuiz = false
            onOpenQuiz()
        }
    }

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
        backContentDescription = t(UiTextKey.NavBack)
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
                        Text(if (isGeneratingThis) t(UiTextKey.LearningSheetGenerating) else t(UiTextKey.LearningSheetRegenerate))
                    }

                    Button(
                        onClick = { learningViewModel.cancelGenerate() },
                        enabled = isGeneratingThis,
                        modifier = Modifier.weight(1f)
                    ) { Text(t(UiTextKey.ActionCancel)) }
                }

                Button(
                    onClick = {
                        pendingOpenQuiz = true
                        viewModel.generateQuizAndSave()
                    },
                    enabled = !uiState.content.isNullOrBlank() && !uiState.isLoading && !uiState.quizLoading,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (uiState.quizLoading) "Quiz Generating..." else "Quiz (Generate when materials updated)")
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