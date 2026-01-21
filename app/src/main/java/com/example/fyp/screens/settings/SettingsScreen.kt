package com.example.fyp.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.fyp.core.FontSizeUtils
import com.example.fyp.core.LanguageDropdownField
import com.example.fyp.core.StandardScreenScaffold
import com.example.fyp.data.config.AzureLanguageConfig
import com.example.fyp.model.AppLanguageState
import com.example.fyp.model.UiTextKey

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    uiLanguages: List<Pair<String, String>>,
    appLanguageState: AppLanguageState,
    onUpdateAppLanguage: (String, Map<UiTextKey, String>) -> Unit,
    onBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val supportedLanguages = remember { AzureLanguageConfig.loadSupportedLanguages(context) }
    val languageNameMap = remember(uiLanguages) { uiLanguages.toMap() }

    fun displayName(code: String) = languageNameMap[code] ?: code

    var selected by remember(uiState.settings.primaryLanguageCode) {
        mutableStateOf(uiState.settings.primaryLanguageCode.ifBlank { "en-US" })
    }

    // Font size scale state (80% to 150%)
    var fontScale by remember(uiState.settings.fontSizeScale) {
        mutableStateOf(uiState.settings.fontSizeScale)
    }

    StandardScreenScaffold(
        title = "Settings",
        onBack = onBack,
        backContentDescription = "Back"
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Error state
            if (uiState.error != null) {
                Text(
                    "Error: ${uiState.error}",
                    color = MaterialTheme.colorScheme.error
                )
            }

            // ==================== SECTION 1: PRIMARY LANGUAGE ====================
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    "Primary Language",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    "Used for learning explanations and recommendations",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                LanguageDropdownField(
                    label = "Primary language",
                    selectedCode = selected,
                    options = supportedLanguages,
                    nameFor = { code -> displayName(code) },
                    onSelected = {
                        selected = it
                        viewModel.updatePrimaryLanguage(it)
                    }
                )
            }

            // ==================== SECTION 2: FONT SIZE ====================
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    "Font Size",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    "Adjust text size for better readability (synced across devices)",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Font scale display
                Text(
                    "Scale: ${(fontScale * 100).toInt()}%",
                    style = MaterialTheme.typography.bodyMedium
                )

                // Preview text with current scale
                Text(
                    "Preview: This is how text looks",
                    fontSize = (14 * fontScale).sp,
                    modifier = Modifier.padding(8.dp)
                )

                // Slider for font size adjustment
                Slider(
                    value = fontScale,
                    onValueChange = { newScale ->
                        fontScale = FontSizeUtils.validateScale(newScale)
                    },
                    onValueChangeFinished = {
                        // Save to Firestore after user finishes sliding
                        viewModel.updateFontSizeScale(fontScale)
                    },
                    valueRange = FontSizeUtils.MIN_SCALE..FontSizeUtils.MAX_SCALE,
                    steps = 6, // 7 steps total
                    modifier = Modifier.fillMaxWidth()
                )

                // Scale percentage labels
                Text(
                    "80% ——— 100% ——— 120% ——— 150%",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }

            // ==================== SECTION 3: APP INFO ====================
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    "About",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    "Talk & Learn v1.0",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    "Your preferences are automatically saved and synced to your account.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}