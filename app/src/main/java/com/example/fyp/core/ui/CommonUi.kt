package com.example.fyp.core

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.fyp.data.azure.LanguageDisplayNames
import com.example.fyp.data.ui.UiLanguageCacheStore
import com.example.fyp.model.ui.AppLanguageState
import com.example.fyp.model.ui.BaseUiTexts
import com.example.fyp.model.ui.UiTextKey
import com.example.fyp.model.ui.CantoneseUiTexts
import com.example.fyp.model.ui.ZhTwUiTexts
import com.example.fyp.model.ui.ZhCnUiTexts
import com.example.fyp.model.ui.JaJpUiTexts
import com.example.fyp.model.ui.KoKrUiTexts
import com.example.fyp.model.ui.FrFrUiTexts
import com.example.fyp.model.ui.DeDeUiTexts
import com.example.fyp.model.ui.EsEsUiTexts
import com.example.fyp.model.ui.IdIdUiTexts
import com.example.fyp.model.ui.ViVnUiTexts
import com.example.fyp.model.ui.ThThUiTexts
import com.example.fyp.model.ui.FilPhUiTexts
import com.example.fyp.model.ui.MsMyUiTexts
import com.example.fyp.model.ui.PtBrUiTexts
import com.example.fyp.model.ui.ItItUiTexts
import com.example.fyp.model.ui.RuRuUiTexts
import kotlinx.coroutines.launch

@Immutable
data class UiLanguageList(val items: List<Pair<String, String>>)

/** Maps a BCP-47 language code to its hardcoded UI-text map (empty for English). */
private val hardcodedUiTexts: Map<String, Map<UiTextKey, String>> = mapOf(
    "zh-HK" to CantoneseUiTexts,
    "zh-TW" to ZhTwUiTexts,
    "zh-CN" to ZhCnUiTexts,
    "ja-JP" to JaJpUiTexts,
    "ko-KR" to KoKrUiTexts,
    "fr-FR" to FrFrUiTexts,
    "de-DE" to DeDeUiTexts,
    "es-ES" to EsEsUiTexts,
    "id-ID" to IdIdUiTexts,
    "vi-VN" to ViVnUiTexts,
    "th-TH" to ThThUiTexts,
    "fil-PH" to FilPhUiTexts,
    "ms-MY" to MsMyUiTexts,
    "pt-BR" to PtBrUiTexts,
    "it-IT" to ItItUiTexts,
    "ru-RU" to RuRuUiTexts,
)

/** Maps BCP-47 language codes to their [UiTextKey] for translated display names.
 *  Adding support for a new language requires only one entry here. */
private val languageCodeToUiTextKey: Map<String, UiTextKey> = mapOf(
    "en-US" to UiTextKey.LangEnUs,
    "zh-TW" to UiTextKey.LangZhTw,
    "zh-HK" to UiTextKey.LangZhHk,
    "ja-JP" to UiTextKey.LangJaJp,
    "zh-CN" to UiTextKey.LangZhCn,
    "fr-FR" to UiTextKey.LangFrFr,
    "de-DE" to UiTextKey.LangDeDe,
    "ko-KR" to UiTextKey.LangKoKr,
    "es-ES" to UiTextKey.LangEsEs,
    "id-ID" to UiTextKey.LangIdId,
    "vi-VN" to UiTextKey.LangViVn,
    "th-TH" to UiTextKey.LangThTh,
    "fil-PH" to UiTextKey.LangFilPh,
    "ms-MY" to UiTextKey.LangMsMy,
    "pt-BR" to UiTextKey.LangPtBr,
    "it-IT" to UiTextKey.LangItIt,
    "ru-RU" to UiTextKey.LangRuRu,
)

@Composable
fun rememberUiTextFunctions(
    appLanguageState: AppLanguageState
): Pair<(UiTextKey, String) -> String, (String) -> String> {

    val uiTexts = appLanguageState.uiTexts

    val uiText: (UiTextKey, String) -> String = { key, default ->
        uiTexts[key] ?: default
    }

    val uiLanguageNameFor: (String) -> String = { code ->
        val key = languageCodeToUiTextKey[code]
        val fallback = LanguageDisplayNames.displayName(code)
        key?.let { uiTexts[it] } ?: fallback
    }

    return uiText to uiLanguageNameFor
}

/**
 * Simplified helper that returns just the translation function.
 * Useful for dialogs and components that only need text translation.
 */
@Composable
fun rememberTranslator(appLanguageState: AppLanguageState): (UiTextKey) -> String {
    val (uiText, _) = rememberUiTextFunctions(appLanguageState)
    return { key -> uiText(key, BaseUiTexts[key.ordinal]) }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppLanguageDropdown(
    uiLanguages: List<Pair<String, String>>,
    appLanguageState: AppLanguageState,
    onUpdateAppLanguage: (String, Map<UiTextKey, String>) -> Unit,
    uiText: (UiTextKey, String) -> String,
    enabled: Boolean = true,
    @Suppress("UNUSED_PARAMETER") isLoggedIn: Boolean = false
) {
    AppLanguageDropdown(
        uiLanguages = UiLanguageList(uiLanguages),
        appLanguageState = appLanguageState,
        onUpdateAppLanguage = onUpdateAppLanguage,
        uiText = uiText,
        enabled = enabled
    )
}

@Composable
fun AppLanguageDropdown(
    uiLanguages: UiLanguageList,
    appLanguageState: AppLanguageState,
    onUpdateAppLanguage: (String, Map<UiTextKey, String>) -> Unit,
    uiText: (UiTextKey, String) -> String,
    enabled: Boolean = true,
    @Suppress("UNUSED_PARAMETER") isLoggedIn: Boolean = false
) {
    val uiScope = rememberCoroutineScope()
    val (_, uiLanguageNameFor) = rememberUiTextFunctions(appLanguageState)
    val context = LocalContext.current
    val appContext = remember(context) { context.applicationContext }
    val cache = remember { UiLanguageCacheStore(appContext) }

    LanguageDropdownField(
        label = uiText(UiTextKey.AppUiLanguageLabel, "App UI language"),
        selectedCode = appLanguageState.selectedUiLanguage,
        options = uiLanguages.items.map { it.first },
        nameFor = uiLanguageNameFor,
        enabled = enabled,
        onSelected = { code ->
            if (!enabled) return@LanguageDropdownField
            if (code == appLanguageState.selectedUiLanguage) return@LanguageDropdownField

            // All languages are now hardcoded — instant switch, no API call needed.
            val textsMap = if (code.startsWith("en")) emptyMap() else hardcodedUiTexts[code] ?: emptyMap()
            uiScope.launch {
                onUpdateAppLanguage(code, textsMap)
                cache.setSelectedLanguage(code)
            }
        }
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
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            singleLine = true, // keep height consistent when dropdowns share a row
            maxLines = 1,
            modifier = Modifier
                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
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
    snackbarHost: @Composable () -> Unit = {},
    hasBottomNav: Boolean = false,
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        // IMPORTANT: Screens WITH bottom nav should NOT add navigation bar insets here
        // (to avoid black gap between content and bottom nav). The outer AppNavigation Scaffold
        // already handles insets via its bottomBar windowInsets parameter.
        // Screens WITHOUT bottom nav (detail screens) need to add navigation bar insets
        // to prevent content from being blocked by system bars (gesture nav, 3-button nav, etc.).
        contentWindowInsets = if (hasBottomNav) WindowInsets(0) else WindowInsets.Companion.navigationBars,
        topBar = {
            StandardTopAppBar(
                title = title,
                onBack = onBack,
                backContentDescription = backContentDescription,
                actions = actions
            )
        },
        snackbarHost = snackbarHost,
        content = content
    )
}

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

    val finalModifier = if (scrollable) base.verticalScroll(rememberScrollState()) else base

    Column(
        modifier = finalModifier,
        verticalArrangement = verticalArrangement,
        content = content
    )
}

/**
 * Reusable confirmation dialog for common confirm/cancel patterns.
 * Reduces boilerplate across screens.
 * Content is scrollable to handle long translated messages.
 */
@Composable
fun ConfirmationDialog(
    title: String,
    message: String,
    confirmText: String,
    cancelText: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    confirmColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.primary,
    messageContent: (@Composable () -> Unit)? = null
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(
                modifier = Modifier
                    .heightIn(max = 300.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                if (messageContent != null) {
                    messageContent()
                } else {
                    Text(message)
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(confirmText, color = confirmColor)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(cancelText)
            }
        }
    )
}

/**
 * JSON decode helper with fallback to default value.
 * Reduces try-catch boilerplate in repositories.
 */
inline fun <reified T> kotlinx.serialization.json.Json.decodeOrDefault(
    jsonString: String,
    default: T
): T = try {
    decodeFromString<T>(jsonString)
} catch (_: Exception) {
    default
}
