package com.example.fyp.core

import android.util.Log
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.fyp.model.AppLanguageState
import com.example.fyp.model.BaseUiTexts
import com.example.fyp.data.LanguageDisplayNames
import com.example.fyp.model.SpeechResult
// com.example.fyp.data.TranslatorClient
import com.example.fyp.data.CloudTranslatorClient
import com.example.fyp.model.UiTextKey
import com.example.fyp.model.buildUiTextMap
import kotlinx.coroutines.launch
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import com.example.fyp.data.UiLanguageCacheStore
import com.example.fyp.model.baseUiTextsHash

@Composable
fun rememberUiTextFunctions(
    appLanguageState: AppLanguageState
): Pair<(UiTextKey, String) -> String, (String) -> String> {
    val uiTexts = appLanguageState.uiTexts

    val uiText: (UiTextKey, String) -> String = { key, default ->
        uiTexts[key] ?: default
    }

    val uiLanguageNameFor: (String) -> String = { code ->
        val key = when (code) {
            "en-US" -> UiTextKey.LangEnUs
            "zh-HK" -> UiTextKey.LangZhHk
            "ja-JP" -> UiTextKey.LangJaJp
            "zh-CN" -> UiTextKey.LangZhCn
            "fr-FR" -> UiTextKey.LangFrFr
            "de-DE" -> UiTextKey.LangDeDe
            "ko-KR" -> UiTextKey.LangKoKr
            "es-ES" -> UiTextKey.LangEsEs
            else -> null
        }

        if (key == null) {
            LanguageDisplayNames.displayName(code)
        } else {
            val fallback = LanguageDisplayNames.displayName(code)
            uiTexts[key] ?: fallback
        }
    }

    return uiText to uiLanguageNameFor
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppLanguageDropdown(
    uiLanguages: List<Pair<String, String>>,
    appLanguageState: AppLanguageState,
    onUpdateAppLanguage: (String, Map<UiTextKey, String>) -> Unit,
    uiText: (UiTextKey, String) -> String,
    enabled: Boolean = true
) {
    val scope = rememberCoroutineScope()
    val (_, uiLanguageNameFor) = rememberUiTextFunctions(appLanguageState)

    val context = LocalContext.current
    val cache = remember { UiLanguageCacheStore(context) }

    LanguageDropdownField(
        label = uiText(UiTextKey.AppUiLanguageLabel, "App UI language"),
        selectedCode = appLanguageState.selectedUiLanguage,
        options = uiLanguages.map { it.first },
        nameFor = { code -> uiLanguageNameFor(code) },
        onSelected = { code ->
            if (!enabled) return@LanguageDropdownField

            if (code.startsWith("en")) {
                onUpdateAppLanguage(code, emptyMap())
                scope.launch {
                    cache.setSelectedLanguage(code)
                    cache.setBaseHash(baseUiTextsHash())
                }
                return@LanguageDropdownField
            }

            scope.launch {
                try {
                    val currentHash = baseUiTextsHash()
                    val cachedHash = cache.getBaseHash()

                    if (cachedHash == currentHash) {
                        val cachedMap = cache.loadUiTexts(code)
                        if (!cachedMap.isNullOrEmpty()) {
                            onUpdateAppLanguage(code, cachedMap)
                            cache.setSelectedLanguage(code)
                            return@launch
                        }
                    }

                    val cloud = CloudTranslatorClient()
                    val translatedList = cloud.translateTexts(
                        texts = BaseUiTexts,
                        from = "en",
                        to = code
                    )

                    val joined = translatedList.joinToString("\u0001")
                    val map = buildUiTextMap(joined)

                    onUpdateAppLanguage(code, map)

                    cache.setSelectedLanguage(code)
                    cache.saveUiTexts(code, map)
                    cache.setBaseHash(currentHash)
                } catch (e: Exception) {
                    Log.e("UITranslation", "Error: ${e.message}", e)
                    onUpdateAppLanguage("en-US", emptyMap())
                }
            }
        },
        enabled = enabled
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguageDropdownField(
    label: String,
    selectedCode: String,
    options: List<String>,
    nameFor: (String) -> String,
    onSelected: (String) -> Unit,
    enabled: Boolean = true
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { if (enabled) expanded = !expanded },
        modifier = Modifier.fillMaxWidth()
    ) {
        TextField(
            value = nameFor(selectedCode),
            onValueChange = {},
            readOnly = true,
            enabled = enabled,
            label = { Text(label) },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { code ->
                DropdownMenuItem(
                    text = { Text(nameFor(code)) },
                    onClick = {
                        onSelected(code)
                        expanded = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StandardTopAppBar(
    title: String,
    onBack: (() -> Unit)?,
    backContentDescription: String = "Back",
    actions: @Composable RowScope.() -> Unit = {}
) {
    TopAppBar(
        title = { Text(title) },
        navigationIcon = {
            if (onBack != null) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = backContentDescription
                    )
                }
            }
        },
        actions = actions
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StandardScreenScaffold(
    title: String,
    onBack: (() -> Unit)? = null,
    backContentDescription: String = "Back",
    actions: @Composable RowScope.() -> Unit = {},
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            StandardTopAppBar(
                title = title,
                onBack = onBack,
                backContentDescription = backContentDescription,
                actions = actions
            )
        },
        content = content
    )
}

/**
 * This is the shared "page body" padding rule:
 * - Always apply innerPadding from Scaffold/TopAppBar
 * - Then apply 16.dp page padding
 * - Optionally enable vertical scrolling
 */

@Composable
fun StandardScreenBody(
    innerPadding: PaddingValues,
    scrollable: Boolean = true,
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(12.dp),
    content: @Composable ColumnScope.() -> Unit
) {
    val base = Modifier
        .padding(innerPadding)
        .padding(16.dp)
        .fillMaxSize()

    val finalModifier =
        if (scrollable) base.verticalScroll(rememberScrollState()) else base

    Column(
        modifier = finalModifier,
        verticalArrangement = verticalArrangement,
        content = content
    )
}