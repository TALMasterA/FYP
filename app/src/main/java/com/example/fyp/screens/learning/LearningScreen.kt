package com.example.fyp.screens.learning

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.fyp.core.StandardScreenScaffold
import com.example.fyp.data.config.AzureLanguageConfig
import com.example.fyp.model.AppLanguageState
import com.example.fyp.model.UiTextKey
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LearningScreen(
    uiLanguages: List<Pair<String, String>>,
    appLanguageState: AppLanguageState,
    onUpdateAppLanguage: (String, Map<UiTextKey, String>) -> Unit,
    onBack: () -> Unit,
    viewModel: LearningViewModel = hiltViewModel(),
    onOpenSheet: (String) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()

    val context = LocalContext.current
    val supported = remember { AzureLanguageConfig.loadSupportedLanguages(context).toSet() }

    val languageNameMap = remember(uiLanguages) { uiLanguages.toMap() }
    fun displayName(code: String) = languageNameMap[code] ?: code

    LaunchedEffect(supported) {
        viewModel.setSupportedLanguages(supported)
    }

    StandardScreenScaffold(
        title = "Learning",
        onBack = onBack,
        backContentDescription = "Back"
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Primary: ${displayName(uiState.primaryLanguageCode)}")
            Text("(*) Count = number of history records involving this language.")
            uiState.error?.let { Text("Error: $it") }

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                items(uiState.clusters, key = { it.languageCode }) { c ->
                    val hasSheet = uiState.sheetExistsByLanguage[c.languageCode] == true
                    val isGeneratingThis = uiState.generatingLanguageCode == c.languageCode
                    val isGeneratingAny = uiState.generatingLanguageCode != null
                    val lastCount = uiState.sheetCountByLanguage[c.languageCode]
                    val unchanged = (lastCount != null && lastCount == c.count)
                    val generateEnabled = !isGeneratingAny && c.count > 0 && !unchanged

                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            AssistChip(
                                onClick = { },
                                label = { Text("${displayName(c.languageCode)} (${c.count})") },
                                colors = AssistChipDefaults.assistChipColors()
                            )

                            Button(
                                onClick = { viewModel.generateFor(c.languageCode) },
                                enabled = generateEnabled
                            ) {
                                Text(
                                    when {
                                        isGeneratingThis -> "Generating..."
                                        hasSheet -> "Re-gen"
                                        else -> "Generate"
                                    }
                                )
                            }
                        }

                        if (hasSheet) {
                            Button(onClick = { onOpenSheet(c.languageCode) }) {
                                Text("${displayName(c.languageCode)} Sheet")
                            }
                        }
                    }
                }
            }
        }
    }
}