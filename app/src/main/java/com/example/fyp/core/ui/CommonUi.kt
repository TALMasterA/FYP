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
import com.example.fyp.data.azure.LanguageDisplayNames
import com.example.fyp.data.ui.UiLanguageCacheStore
import com.example.fyp.model.ui.AppLanguageState
import com.example.fyp.model.ui.BaseUiTexts
import com.example.fyp.model.ui.UiTextKey
import com.example.fyp.model.ui.baseUiTextsHash
import com.example.fyp.model.ui.buildUiTextMap
import com.example.fyp.model.ui.LanguageNameTranslations
import com.example.fyp.model.ui.LanguageNameKeys
import com.example.fyp.model.ui.CantoneseUiTexts
import com.google.firebase.functions.FirebaseFunctionsException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@Immutable
data class UiLanguageList(val items: List<Pair<String, String>>)

private data class UiLanguageTranslationStatus(
    val isRunning: Boolean = false,
    val targetLanguageCode: String? = null,
    val message: String? = null
)

private class UiLanguageTranslationAbort(message: String) : CancellationException(message)

internal fun toUiLanguageSwitchErrorMessage(throwable: Throwable): String {
    if (throwable is FirebaseFunctionsException) {
        val serverMessage = throwable.message?.takeIf { it.isNotBlank() }
        return when (throwable.code) {
            FirebaseFunctionsException.Code.RESOURCE_EXHAUSTED ->
                serverMessage ?: "UI language change is rate-limited right now. Please wait and try again."
            FirebaseFunctionsException.Code.DEADLINE_EXCEEDED ->
                serverMessage ?: "UI language change timed out. Please try again."
            FirebaseFunctionsException.Code.UNAVAILABLE ->
                serverMessage ?: "UI language service is temporarily unavailable. Please try again later."
            else -> serverMessage ?: "UI language change failed. Please try again."
        }
    }

    val message = throwable.message?.trim().orEmpty()
    if (message.contains("resource-exhausted", ignoreCase = true) || message.contains("429")) {
        return "UI language change is rate-limited right now. Please wait and try again."
    }
    return message.ifBlank { "UI language change failed. Please try again." }
}

private object UiLanguageTranslationCoordinator {
    private const val STATUS_AUTO_DISMISS_MS = 4000L

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private val _status = MutableStateFlow(UiLanguageTranslationStatus())
    val status: StateFlow<UiLanguageTranslationStatus> = _status.asStateFlow()
    private var clearStatusJob: Job? = null

    private fun publishStatus(status: UiLanguageTranslationStatus, autoDismiss: Boolean = false) {
        clearStatusJob?.cancel()
        _status.value = status

        if (!autoDismiss || status.message == null) return

        clearStatusJob = scope.launch {
            delay(STATUS_AUTO_DISMISS_MS)

            val current = _status.value
            // Clear only if no new translation/status replaced this one.
            if (!current.isRunning &&
                current.message == status.message &&
                current.targetLanguageCode == status.targetLanguageCode
            ) {
                _status.value = UiLanguageTranslationStatus()
            }
        }
    }

    fun launch(
        targetLanguageCode: String,
        block: suspend () -> Unit
    ): Boolean {
        if (_status.value.isRunning) return false
        publishStatus(UiLanguageTranslationStatus(
            isRunning = true,
            targetLanguageCode = targetLanguageCode,
            message = "Translating app UI to ${LanguageDisplayNames.displayName(targetLanguageCode)}..."
        ))
        scope.launch {
            try {
                block()
                publishStatus(UiLanguageTranslationStatus(
                    isRunning = false,
                    targetLanguageCode = targetLanguageCode,
                    message = "UI language updated to ${LanguageDisplayNames.displayName(targetLanguageCode)}."
                ), autoDismiss = true)
            } catch (abort: UiLanguageTranslationAbort) {
                publishStatus(UiLanguageTranslationStatus(
                    isRunning = false,
                    targetLanguageCode = targetLanguageCode,
                    message = abort.message
                ), autoDismiss = true)
            } catch (t: Throwable) {
                publishStatus(UiLanguageTranslationStatus(
                    isRunning = false,
                    targetLanguageCode = targetLanguageCode,
                    message = toUiLanguageSwitchErrorMessage(t)
                ), autoDismiss = true)
            }
        }
        return true
    }
}

/**
 * Apply predefined language name corrections to a UI text map.
 * This fixes translation API errors where language names are incorrectly translated.
 */
private fun applyLanguageNameCorrections(
    map: Map<UiTextKey, String>,
    uiLanguageCode: String
): Map<UiTextKey, String> {
    if (uiLanguageCode == "en-US") return map

    val langNameTranslations = LanguageNameTranslations[uiLanguageCode] ?: return map

    val correctedMap = map.toMutableMap()
    LanguageNameKeys.forEach { key ->
        langNameTranslations[key]?.let { correctTranslation ->
            correctedMap[key] = correctTranslation
        }
    }
    return correctedMap
}

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
    isLoggedIn: Boolean = false
) {
    AppLanguageDropdown(
        uiLanguages = UiLanguageList(uiLanguages),
        appLanguageState = appLanguageState,
        onUpdateAppLanguage = onUpdateAppLanguage,
        uiText = uiText,
        enabled = enabled,
        isLoggedIn = isLoggedIn
    )
}

@Composable
fun AppLanguageDropdown(
    uiLanguages: UiLanguageList,
    appLanguageState: AppLanguageState,
    onUpdateAppLanguage: (String, Map<UiTextKey, String>) -> Unit,
    uiText: (UiTextKey, String) -> String,
    enabled: Boolean = true,
    isLoggedIn: Boolean = false
) {
    val uiScope = rememberCoroutineScope()
    val (_, uiLanguageNameFor) = rememberUiTextFunctions(appLanguageState)
    val context = LocalContext.current
    val appContext = remember(context) { context.applicationContext }
    val cache = remember { UiLanguageCacheStore(appContext) }
    val cloud = remember { CloudTranslatorClient() }
    val translationStatus by UiLanguageTranslationCoordinator.status.collectAsState()

    // State for showing guest limit dialog
    var showGuestLimitDialog by remember { mutableStateOf(false) }
    var showTranslationBusyDialog by remember { mutableStateOf(false) }

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

    if (showTranslationBusyDialog) {
        AlertDialog(
            onDismissRequest = { showTranslationBusyDialog = false },
            title = { Text("Please wait") },
            text = {
                Text(
                    translationStatus.message
                        ?: "Another UI language translation is still running."
                )
            },
            confirmButton = {
                TextButton(onClick = { showTranslationBusyDialog = false }) {
                    Text(uiText(UiTextKey.ActionConfirm, "OK"))
                }
            }
        )
    }

    LanguageDropdownField(
        label = uiText(UiTextKey.AppUiLanguageLabel, "App UI language"),
        selectedCode = appLanguageState.selectedUiLanguage,
        options = uiLanguages.items.map { it.first },
        nameFor = uiLanguageNameFor,
        enabled = enabled,
        onSelected = { code ->
            if (!enabled) return@LanguageDropdownField
            if (code == appLanguageState.selectedUiLanguage) return@LanguageDropdownField
            // English and hardcoded packs complete immediately and do not need async jobs.
            if (code.startsWith("en")) {
                uiScope.launch {
                    onUpdateAppLanguage(code, emptyMap())
                    cache.setSelectedLanguage(code)
                    cache.setBaseHash(code, baseUiTextsHash())
                }
                return@LanguageDropdownField
            }

            if (code == "zh-TW") {
                uiScope.launch {
                    onUpdateAppLanguage(code, com.example.fyp.model.ui.ZhTwUiTexts)
                    cache.setSelectedLanguage(code)
                    cache.setBaseHash(code, baseUiTextsHash())
                }
                return@LanguageDropdownField
            }

            if (code == "zh-HK") {
                uiScope.launch {
                    onUpdateAppLanguage(code, CantoneseUiTexts)
                    cache.setSelectedLanguage(code)
                    cache.setBaseHash(code, baseUiTextsHash())
                }
                return@LanguageDropdownField
            }

            val launched = UiLanguageTranslationCoordinator.launch(
                targetLanguageCode = code
            ) {
                val currentHash = baseUiTextsHash()
                val cachedHash = cache.getBaseHash(code)
                if (cachedHash == currentHash) {
                    val cachedMap = cache.loadUiTexts(code)
                    if (!cachedMap.isNullOrEmpty()) {
                        val correctedMap = applyLanguageNameCorrections(cachedMap, code)
                        onUpdateAppLanguage(code, correctedMap)
                        cache.setSelectedLanguage(code)
                        throw UiLanguageTranslationAbort("Loaded cached UI language: ${LanguageDisplayNames.displayName(code)}")
                    }
                }

                if (!isLoggedIn && cache.hasGuestUsedTranslation()) {
                    showGuestLimitDialog = true
                    throw UiLanguageTranslationAbort("Login required for another UI-language translation.")
                }

                val translatedList = cloud.translateTexts(
                    texts = BaseUiTexts,
                    from = "en-US",
                    to = code
                )

                val joined = translatedList.joinToString("\u0001")
                val map = buildUiTextMap(joined, code)
                onUpdateAppLanguage(code, map)
                cache.setSelectedLanguage(code)
                cache.saveUiTexts(code, map)
                cache.setBaseHash(code, currentHash)

                if (!isLoggedIn) {
                    cache.setGuestTranslationUsed()
                }

                Log.d("UITranslation", "Completed language switch for $code")
            }

            if (!launched) {
                showTranslationBusyDialog = true
            }
        }
    )

    val statusMessage = translationStatus.message
    if (statusMessage != null) {
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = statusMessage,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        if (translationStatus.isRunning) {
            Spacer(modifier = Modifier.height(6.dp))
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }
    }
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
