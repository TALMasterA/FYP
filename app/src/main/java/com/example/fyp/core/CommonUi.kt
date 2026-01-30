package com.example.fyp.core

import android.util.Log
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
import com.example.fyp.data.clients.CloudTranslatorClient
import com.example.fyp.data.config.LanguageDisplayNames
import com.example.fyp.data.ui.UiLanguageCacheStore
import com.example.fyp.model.AppLanguageState
import com.example.fyp.model.BaseUiTexts
import com.example.fyp.model.UiTextKey
import com.example.fyp.model.baseUiTextsHash
import com.example.fyp.model.buildUiTextMap
import kotlinx.coroutines.launch

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
    isLoggedIn: Boolean = false
) {
    val scope = rememberCoroutineScope()
    val (_, uiLanguageNameFor) = rememberUiTextFunctions(appLanguageState)
    val context = LocalContext.current
    val cache = remember { UiLanguageCacheStore(context) }

    // State for showing guest limit dialog
    var showGuestLimitDialog by remember { mutableStateOf(false) }

    // Show guest limit alert dialog
    if (showGuestLimitDialog) {
        AlertDialog(
            onDismissRequest = { showGuestLimitDialog = false },
            title = { Text(uiText(UiTextKey.GuestTranslationLimitTitle, "Login Required")) },
            text = { Text(uiText(UiTextKey.GuestTranslationLimitMessage,
                "You have already changed the UI language once. Please login to change it again. Logged-in users have unlimited language changes with local cache support.")) },
            confirmButton = {
                TextButton(onClick = { showGuestLimitDialog = false }) {
                    Text(uiText(UiTextKey.ActionConfirm, "OK"))
                }
            }
        )
    }

    LanguageDropdownField(
        label = uiText(UiTextKey.AppUiLanguageLabel, "App UI language"),
        selectedCode = appLanguageState.selectedUiLanguage,
        options = uiLanguages.map { it.first },
        nameFor = uiLanguageNameFor,
        enabled = enabled,
        onSelected = { code ->
            if (!enabled) return@LanguageDropdownField

            scope.launch {
                try {
                    // English: no translated map, always fallback to BaseUiTexts (no API call needed)
                    if (code.startsWith("en")) {
                        onUpdateAppLanguage(code, emptyMap())
                        cache.setSelectedLanguage(code)
                        cache.setBaseHash(code, baseUiTextsHash())
                        return@launch
                    }

                    val currentHash = baseUiTextsHash()
                    val cachedHash = cache.getBaseHash(code)

                    // Same base version + cached map => no API call (works for both logged-in and guests)
                    if (cachedHash == currentHash) {
                        val cachedMap = cache.loadUiTexts(code)
                        if (!cachedMap.isNullOrEmpty()) {
                            onUpdateAppLanguage(code, cachedMap)
                            cache.setSelectedLanguage(code)
                            return@launch
                        }
                    }

                    // API call needed - check guest limit if not logged in
                    if (!isLoggedIn) {
                        val guestAlreadyUsed = cache.hasGuestUsedTranslation()
                        if (guestAlreadyUsed) {
                            // Show dialog and don't proceed
                            showGuestLimitDialog = true
                            return@launch
                        }
                    }

                    // Call API for translation
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
                    cache.setBaseHash(code, currentHash)

                    // Mark guest as having used their one-time translation (only if not logged in)
                    if (!isLoggedIn) {
                        cache.setGuestTranslationUsed()
                    }
                } catch (e: Exception) {
                    Log.e("UITranslation", "Error: ${e.message}", e)
                    onUpdateAppLanguage("en-US", emptyMap())
                }
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
            if (messageContent != null) {
                messageContent()
            } else {
                Text(message)
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