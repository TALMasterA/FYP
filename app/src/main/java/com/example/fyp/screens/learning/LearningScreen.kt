package com.example.fyp.screens.learning

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.fyp.domain.learning.GenerationEligibility
import com.example.fyp.core.ConfirmationDialog
import com.example.fyp.core.StandardScreenScaffold
import com.example.fyp.core.rememberUiTextFunctions
import com.example.fyp.model.ui.AppLanguageState
import com.example.fyp.model.ui.BaseUiTexts
import com.example.fyp.model.ui.UiTextKey
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.foundation.shape.RoundedCornerShape
import com.example.fyp.core.LanguageDropdownField
import androidx.compose.foundation.layout.Row
import androidx.compose.ui.text.font.FontWeight
import kotlinx.coroutines.delay
import com.example.fyp.core.UiConstants
import com.example.fyp.ui.components.LearningSheetSkeleton

@Suppress("UNUSED_PARAMETER", "SENSELESS_COMPARISON")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LearningScreen(
    uiLanguages: List<Pair<String, String>>,
    appLanguageState: AppLanguageState,
    onUpdateAppLanguage: (String, Map<UiTextKey, String>) -> Unit,
    onBack: () -> Unit,
    viewModel: LearningViewModel = hiltViewModel(),
    onOpenSheet: (primaryCode: String, targetCode: String) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()

    val (uiText, uiLanguageNameFor) = rememberUiTextFunctions(appLanguageState)
    val t: (UiTextKey) -> String = { key -> uiText(key, BaseUiTexts[key.ordinal]) }

    val supported = remember { viewModel.supportedLanguages.toSet() }

    var pendingGenerateLang by remember { mutableStateOf<String?>(null) }
    var showRegenInfo by remember { mutableStateOf(false) }

    // Auto-dismiss error after delay
    LaunchedEffect(uiState.error) {
        if (uiState.error != null) {
            delay(UiConstants.ERROR_AUTO_DISMISS_MS)
            viewModel.clearError()
        }
    }

    StandardScreenScaffold(
        title = t(UiTextKey.LearningTitle),
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
        }
    ) { padding ->
        pendingGenerateLang?.let { langCode ->
            val langName = uiLanguageNameFor(langCode)

            ConfirmationDialog(
                title = t(UiTextKey.DialogGenerateOverwriteTitle),
                message = t(UiTextKey.DialogGenerateOverwriteMessageTemplate)
                    .replace("{speclanguage}", langName),
                confirmText = t(UiTextKey.ActionConfirm),
                cancelText = t(UiTextKey.ActionCancel),
                onConfirm = {
                    pendingGenerateLang = null
                    viewModel.generateFor(langCode)
                },
                onDismiss = { pendingGenerateLang = null }
            )
        }

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

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item {
                LanguageDropdownField(
                    label = t(UiTextKey.SettingsPrimaryLanguageLabel),
                    selectedCode = uiState.primaryLanguageCode,
                    options = supported.toList(),
                    nameFor = uiLanguageNameFor,
                    onSelected = { viewModel.setPrimaryLanguage(it) },
                    enabled = true
                )
            }

            item {
                Text(
                    t(UiTextKey.LearningHintCount),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
            }

            if (uiState.isLoading) {
                items(3) {
                    LearningSheetSkeleton()
                }
            }

            uiState.error?.let { errorMsg ->
                item {
                    androidx.compose.material3.Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = t(UiTextKey.LearningErrorTemplate).format(errorMsg),
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }

            items(uiState.clusters, key = { it.languageCode }) { c ->
                    val hasSheet = uiState.sheetExistsByLanguage[c.languageCode] == true
                    val isGeneratingThis = uiState.generatingLanguageCode == c.languageCode
                    val isGeneratingAny = uiState.generatingLanguageCode != null

                    val lastCount = uiState.sheetCountByLanguage[c.languageCode]
                    val unchanged = lastCount != null && lastCount == c.count

                    // Regeneration constraints: use domain logic for consistency
                    val isFirstTime = lastCount == null
                    val hasEnoughNewRecords = isFirstTime || GenerationEligibility.canRegenerateLearningSheet(c.count, lastCount ?: 0)
                    val countHigherThanPrevious = isFirstTime || c.count > (lastCount ?: 0)

                    // Disable Generate when ANY language is generating, or no history, or unchanged,
                    // or count not higher than previous, or not enough new records for regen
                    val generateEnabled = !isGeneratingAny && c.count > 0 && !unchanged &&
                        countHigherThanPrevious && hasEnoughNewRecords

                    val sheetEnabled = hasSheet

                    val langLabel = uiLanguageNameFor(c.languageCode)

                    ElevatedCard(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 3.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.elevatedCardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Language name + count chip
                            AssistChip(
                                onClick = { /* no-op */ },
                                label = {
                                    Text(
                                        "$langLabel (${c.count})",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                },
                                colors = AssistChipDefaults.assistChipColors(
                                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                                )
                            )

                            // Buttons
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Button(
                                        onClick = {
                                            if (generateEnabled) {
                                                pendingGenerateLang = c.languageCode
                                            }
                                        },
                                        enabled = generateEnabled,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(
                                            when {
                                                isGeneratingThis -> t(UiTextKey.LearningGenerating)
                                                hasSheet -> t(UiTextKey.LearningRegenerate)
                                                else -> t(UiTextKey.LearningGenerate)
                                            }
                                        )
                                    }

                                    Button(
                                        onClick = { viewModel.cancelGenerate() },
                                        enabled = isGeneratingThis,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(t(UiTextKey.ActionCancel))
                                    }
                                }

                                Button(
                                    onClick = { onOpenSheet(uiState.primaryLanguageCode, c.languageCode) },
                                    enabled = sheetEnabled,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        t(UiTextKey.LearningOpenSheetTemplate)
                                            .replace("{speclanguage}", langLabel)
                                    )
                                }
                            }
                        }
                    }
            }
        }
    }
}