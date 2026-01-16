package com.example.fyp.screens.help

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.example.fyp.core.AppLanguageDropdown
import com.example.fyp.core.StandardScreenBody
import com.example.fyp.core.StandardScreenScaffold
import com.example.fyp.core.rememberUiTextFunctions
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
    val t: (UiTextKey) -> String = { key -> uiText(key, BaseUiTexts[key.ordinal]) }

    StandardScreenScaffold(
        title = t(UiTextKey.HelpTitle),
        onBack = onBack,
        backContentDescription = t(UiTextKey.NavBack)
    ) { innerPadding ->
        StandardScreenBody(
            innerPadding = innerPadding,
            scrollable = true,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AppLanguageDropdown(
                uiLanguages = uiLanguages,
                appLanguageState = appLanguageState,
                onUpdateAppLanguage = onUpdateAppLanguage,
                uiText = uiText
            )

            Text(text = t(UiTextKey.HelpCautionTitle), style = MaterialTheme.typography.titleMedium)
            Text(text = t(UiTextKey.HelpCaution))

            Text(text = t(UiTextKey.HelpCurrentTitle), style = MaterialTheme.typography.titleMedium)
            Text(text = t(UiTextKey.HelpCurrentFeatures))

            Text(text = t(UiTextKey.HelpNotesTitle), style = MaterialTheme.typography.titleMedium)
            Text(text = t(UiTextKey.HelpNotes))
        }
    }
}