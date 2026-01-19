package com.example.fyp.screens.learning

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LearningScreen(
    uiLanguages: List<Pair<String, String>>,
    appLanguageState: AppLanguageState,
    onUpdateAppLanguage: (String, Map<UiTextKey, String>) -> Unit,
    onBack: () -> Unit,
    viewModel: LearningViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    val context = LocalContext.current
    val supported = remember { AzureLanguageConfig.loadSupportedLanguages(context).toSet() }

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
            Text("Primary: ${uiState.primaryLanguageCode}")
            uiState.error?.let { Text("Error: $it") }

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 260.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.clusters, key = { it.languageCode }) { c ->
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("${c.languageCode} (${c.count})")
                        Button(
                            onClick = { viewModel.generateFor(c.languageCode) },
                            enabled = !uiState.isGenerating
                        ) { Text("Generate") }
                    }
                }
            }

            uiState.generatedContent?.let { content ->
                Text(
                    text = content,
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(vertical = 8.dp)
                )
            }
        }
    }
}