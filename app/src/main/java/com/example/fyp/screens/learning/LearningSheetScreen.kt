package com.example.fyp.screens.learning

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.fyp.core.StandardScreenScaffold
import com.example.fyp.model.AppLanguageState
import com.example.fyp.model.UiTextKey
import com.example.fyp.model.BaseUiTexts
import com.example.fyp.core.rememberUiTextFunctions
import androidx.compose.runtime.LaunchedEffect

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LearningSheetScreen(
    uiLanguages: List<Pair<String, String>>,
    appLanguageState: AppLanguageState,
    onUpdateAppLanguage: (String, Map<UiTextKey, String>) -> Unit,
    languageCode: String,
    onBack: () -> Unit,
    learningViewModel: LearningViewModel,
    viewModel: LearningSheetViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    val (uiText, uiLanguageNameFor) = rememberUiTextFunctions(appLanguageState)
    val t: (UiTextKey) -> String = { key -> uiText(key, BaseUiTexts[key.ordinal]) }

    val learningUiState by learningViewModel.uiState.collectAsState()
    val isGeneratingAny = learningUiState.generatingLanguageCode != null
    val isGeneratingThis = learningUiState.generatingLanguageCode == languageCode

    val targetName = uiLanguageNameFor(languageCode)
    val primaryName = uiLanguageNameFor(learningUiState.primaryLanguageCode)

    LaunchedEffect(learningUiState.generatingLanguageCode) {
        if (learningUiState.generatingLanguageCode == null) {
            viewModel.loadSheet()
        }
    }

    StandardScreenScaffold(
        title = t(UiTextKey.LearningSheetTitleTemplate)
            .replace("{speclanguage}", targetName),
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
                text = t(UiTextKey.LearningSheetPrimaryTemplate)
                    .replace("{speclanguage}", primaryName),
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = t(UiTextKey.LearningSheetHistoryCountTemplate)
                    .replace("nowCount", uiState.countNow.toString())
                    .replace("savedCount", (uiState.historyCountAtGenerate?.toString() ?: "-")),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            uiState.error?.let {
                Text(t(UiTextKey.LearningErrorTemplate).format(it))
            }

            Button(
                onClick = { learningViewModel.generateFor(languageCode) },
                enabled = viewModel.canRegen() && !isGeneratingAny,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (isGeneratingThis) t(UiTextKey.LearningSheetGenerating) else t(UiTextKey.LearningSheetRegenerate))
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