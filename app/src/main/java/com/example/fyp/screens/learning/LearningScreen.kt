package com.example.fyp.screens.learning

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.fyp.core.ConfirmationDialog
import com.example.fyp.core.StandardScreenScaffold
import com.example.fyp.core.rememberUiTextFunctions
import com.example.fyp.data.config.AzureLanguageConfig
import com.example.fyp.model.AppLanguageState
import com.example.fyp.model.BaseUiTexts
import com.example.fyp.model.UiTextKey
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.layout.PaddingValues
import com.example.fyp.core.LanguageDropdownField
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.Row

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

    val context = LocalContext.current
    val supported = remember { AzureLanguageConfig.loadSupportedLanguages(context).toSet() }

    var pendingGenerateLang by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(supported) {
        viewModel.setSupportedLanguages(supported)
    }

    StandardScreenScaffold(
        title = t(UiTextKey.LearningTitle),
        onBack = onBack,
        backContentDescription = t(UiTextKey.NavBack)
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

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            LanguageDropdownField(
                label = t(UiTextKey.SettingsPrimaryLanguageLabel),
                selectedCode = uiState.primaryLanguageCode,
                options = supported.toList(),
                nameFor = uiLanguageNameFor,
                onSelected = { viewModel.setPrimaryLanguage(it) },
                enabled = true
            )

            Text(t(UiTextKey.LearningHintCount))
            uiState.error?.let { Text(t(UiTextKey.LearningErrorTemplate).format(it)) }

            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(uiState.clusters, key = { it.languageCode }) { c ->
                    val hasSheet = uiState.sheetExistsByLanguage[c.languageCode] == true
                    val isGeneratingThis = uiState.generatingLanguageCode == c.languageCode
                    val isGeneratingAny = uiState.generatingLanguageCode != null

                    val lastCount = uiState.sheetCountByLanguage[c.languageCode]
                    val unchanged = lastCount != null && lastCount == c.count

                    // Disable Generate when ANY language is generating, or no history, or unchanged.
                    val generateEnabled = !isGeneratingAny && c.count > 0 && !unchanged

                    val sheetEnabled = hasSheet

                    val langLabel = uiLanguageNameFor(c.languageCode)

                    OutlinedCard(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.outlinedCardColors(
                            containerColor = MaterialTheme.colorScheme.background
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Bubble header: translated language name + count
                            AssistChip(
                                onClick = { /* no-op */ },
                                label = { Text("$langLabel (${c.count})") },
                                colors = AssistChipDefaults.assistChipColors()
                            )

                            // Buttons should be inside bubble. Sheet button "right after language":
                            // put buttons on the NEXT line (not same row as language header).
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Button(
                                        onClick = { pendingGenerateLang = c.languageCode },
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
}