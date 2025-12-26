package com.example.fyp.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.fyp.model.AppLanguageState
import com.example.fyp.model.BaseUiTexts
import com.example.fyp.model.UiTextKey

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpScreen(
    uiLanguages: List<Pair<String, String>>,
    appLanguageState: AppLanguageState,
    onUpdateAppLanguage: (String, Map<UiTextKey, String>) -> Unit,
    onBack: () -> Unit
) {
    val (uiText, _) = rememberUiTextFunctions(appLanguageState)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        uiText(
                            UiTextKey.SpeechTitle,
                            BaseUiTexts[UiTextKey.SpeechTitle.ordinal]
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back"
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
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AppLanguageDropdown(
                uiLanguages = uiLanguages,
                appLanguageState = appLanguageState,
                onUpdateAppLanguage = onUpdateAppLanguage,
                uiText = uiText
            )

            Text(
                text = uiText(
                    UiTextKey.HelpCurrentTitle,
                    BaseUiTexts[UiTextKey.HelpCurrentTitle.ordinal]
                ),
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = uiText(
                    UiTextKey.HelpCurrentFeatures,
                    BaseUiTexts[UiTextKey.HelpCurrentFeatures.ordinal]
                )
            )

            Text(
                text = uiText(
                    UiTextKey.HelpCautionTitle,
                    BaseUiTexts[UiTextKey.HelpCautionTitle.ordinal]
                ),
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = uiText(
                    UiTextKey.HelpCaution,
                    BaseUiTexts[UiTextKey.HelpCaution.ordinal]
                )
            )
        }
    }
}