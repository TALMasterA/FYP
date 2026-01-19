package com.example.fyp.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.fyp.core.LanguageDropdownField
import com.example.fyp.core.StandardScreenScaffold
import com.example.fyp.data.config.AzureLanguageConfig
import com.example.fyp.model.AppLanguageState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    uiLanguages: List<Pair<String, String>>,
    appLanguageState: AppLanguageState,
    onUpdateAppLanguage: (String, Map<Any, String>) -> Unit, // keep your existing signature here
    onBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val supportedLanguages = remember { AzureLanguageConfig.loadSupportedLanguages(context) }

    var selected by remember(uiState.settings.primaryLanguageCode) {
        mutableStateOf(uiState.settings.primaryLanguageCode.ifBlank { "en-US" })
    }

    StandardScreenScaffold(
        title = "Settings",
        onBack = onBack,
        backContentDescription = "Back"
    ) { padding ->
        androidx.compose.foundation.layout.Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (uiState.error != null) Text("Error: ${uiState.error}")

            Text("Primary language (for learning explanations):")

            LanguageDropdownField(
                label = "Primary language",
                selectedCode = selected,
                options = supportedLanguages,
                nameFor = { code -> code }, // replace with your displayName mapper if you want
                onSelected = {
                    selected = it
                    viewModel.updatePrimaryLanguage(it)
                }
            )
        }
    }
}