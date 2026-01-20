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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.fyp.core.StandardScreenScaffold
import com.example.fyp.model.AppLanguageState
import com.example.fyp.model.UiTextKey

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LearningSheetScreen(
    uiLanguages: List<Pair<String, String>>,
    appLanguageState: AppLanguageState,
    onUpdateAppLanguage: (String, Map<UiTextKey, String>) -> Unit,
    languageCode: String,
    onBack: () -> Unit,
    viewModel: LearningSheetViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    val languageNameMap = remember(uiLanguages) { uiLanguages.toMap() }
    fun displayName(code: String) = languageNameMap[code] ?: code

    StandardScreenScaffold(
        title = "${displayName(languageCode)} Sheet",
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
            Text(
                text = "Primary: ${displayName(uiState.primaryLanguageCode)}",
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = "History count now: ${uiState.countNow} (saved at gen: ${uiState.historyCountAtGenerate ?: "-"})",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            uiState.error?.let {
                Text(text = "Error: $it", color = MaterialTheme.colorScheme.error)
            }

            Button(
                onClick = { viewModel.regen() },
                enabled = viewModel.canRegen(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (uiState.isGenerating) "Generating..." else "Re-gen")
            }

            val content = uiState.content
            if (content.isNullOrBlank()) {
                Text("No sheet content yet.")
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