package com.example.fyp.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.fyp.model.AppLanguageState
import com.example.fyp.model.BaseUiTexts
import com.example.fyp.model.UiTextKey

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    uiLanguages: List<Pair<String, String>>,
    appLanguageState: AppLanguageState,
    onUpdateAppLanguage: (String, Map<UiTextKey, String>) -> Unit,
    onStartSpeech: () -> Unit,
    onOpenHelp: () -> Unit,
    onStartContinuous: () -> Unit
) {
    val (uiText, _) = rememberUiTextFunctions(appLanguageState)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(uiText(UiTextKey.HomeTitle, "FYP Translator")) },
                actions = {
                    IconButton(onClick = onOpenHelp) {
                        Icon(
                            imageVector = Icons.Filled.Info,
                            contentDescription = "Help / instructions"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            AppLanguageDropdown(
                uiLanguages = uiLanguages,
                appLanguageState = appLanguageState,
                onUpdateAppLanguage = onUpdateAppLanguage,
                uiText = uiText
            )

            Text(
                text = uiText(
                    UiTextKey.Instructions,
                    "Select User Interface language on top, then the detect and translate languages. " +
                            "Support languages: English, Cantonese, Japanese, Mandarin..."
                ),
                style = MaterialTheme.typography.bodyMedium
            )

            Button(
                onClick = onStartSpeech,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(uiText(UiTextKey.HomeStartButton, "Start speech & translation"))
            }
            Button(
                onClick = onStartContinuous,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    uiText(
                        UiTextKey.ContinuousStartScreenButton,
                        BaseUiTexts[UiTextKey.ContinuousStartScreenButton.ordinal]
                    )
                )
            }
        }
    }
}