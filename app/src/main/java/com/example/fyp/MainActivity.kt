package com.example.fyp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.mapSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.fyp.ui.theme.FYPTheme
import kotlinx.coroutines.launch
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState

// -------- activity --------

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FYPTheme {
                AppNavigation()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (AudioRecorder.isRecording) {
            AudioRecorder.stop(null)
        }
    }
}

// -------- navigation --------

sealed class AppScreen(val route: String) {
    object Home : AppScreen("home")
    object Speech : AppScreen("speech")
    object Help : AppScreen("help")
    object Continuous : AppScreen("continuous")
}

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
    uiText: (UiTextKey, String) -> String
) {
    val scope = rememberCoroutineScope()

    LanguageDropdownField(
        label = uiText(UiTextKey.AppUiLanguageLabel, "App UI language"),
        selectedCode = appLanguageState.selectedUiLanguage,
        options = uiLanguages.map { it.first },
        nameFor = { code -> uiLanguages.first { it.first == code }.second },
        onSelected = { code ->
            if (code.startsWith("en")) {
                onUpdateAppLanguage(code, emptyMap())
            } else {
                scope.launch {
                    val baseTexts = BaseUiTexts
                    when (val result = TranslatorClient.translateTexts(
                        texts = baseTexts,
                        toLanguage = code,
                        fromLanguage = "en"
                    )) {
                        is SpeechResult.Success -> {
                            val parts = result.text.split('\u0001')
                            if (parts.size == baseTexts.size) {
                                val map = UiTextKey.entries.zip(parts).toMap()
                                onUpdateAppLanguage(code, map)
                            } else {
                                onUpdateAppLanguage(code, emptyMap())
                            }
                        }

                        is SpeechResult.Error -> {
                            Log.e("UITranslation", "Error: ${result.message}")
                            onUpdateAppLanguage(code, emptyMap())
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    val uiLanguages = listOf(
        "en" to "English UI",
        "zh-HK" to "中文（香港）UI",
        "ja-JP" to "日本語 UI"
    )

    val languageSaver = mapSaver(
        save = { state ->
            mapOf(
                "code" to state.selectedUiLanguage,
                "texts" to state.uiTexts.map { it.key.name to it.value }
            )
        },
        restore = { map ->
            val code = map["code"] as String
            val textsList = map["texts"] as List<Pair<String, String>>
            AppLanguageState(
                selectedUiLanguage = code,
                uiTexts = textsList.associate { (k, v) ->
                    UiTextKey.valueOf(k) to v
                }
            )
        }
    )

    var appLanguageState by rememberSaveable(stateSaver = languageSaver) {
        mutableStateOf(
            AppLanguageState(
                selectedUiLanguage = uiLanguages[0].first,
                uiTexts = emptyMap()
            )
        )
    }

    fun updateAppLanguage(code: String, uiTexts: Map<UiTextKey, String>) {
        appLanguageState = appLanguageState.copy(
            selectedUiLanguage = code,
            uiTexts = uiTexts
        )
    }

    NavHost(
        navController = navController,
        startDestination = AppScreen.Home.route
    ) {
        composable(AppScreen.Home.route) {
            HomeScreen(
                uiLanguages = uiLanguages,
                appLanguageState = appLanguageState,
                onUpdateAppLanguage = ::updateAppLanguage,
                onStartSpeech = { navController.navigate(AppScreen.Speech.route) },
                onOpenHelp = { navController.navigate(AppScreen.Help.route) },
                onStartContinuous = { navController.navigate(AppScreen.Continuous.route) }
            )
        }
        composable(AppScreen.Speech.route) {
            SpeechRecognitionScreen(
                uiLanguages = uiLanguages,
                appLanguageState = appLanguageState,
                onUpdateAppLanguage = ::updateAppLanguage,
                onBack = { navController.popBackStack() }
            )
        }
        composable(AppScreen.Help.route) {
            HelpScreen(
                uiLanguages = uiLanguages,
                appLanguageState = appLanguageState,
                onUpdateAppLanguage = ::updateAppLanguage,
                onBack = { navController.popBackStack() }
            )
        }
        composable(AppScreen.Continuous.route) {
            ContinuousConversationScreen(
                uiLanguages = uiLanguages,
                appLanguageState = appLanguageState,
                onUpdateAppLanguage = ::updateAppLanguage,
                onBack = { navController.popBackStack() }
            )
        }
    }
}

// -------- screens --------

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
                Text("Start continuous translate & speak") // later can move this string into UiTexts
            }
        }
    }
}

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpeechRecognitionScreen(
    uiLanguages: List<Pair<String, String>>,
    appLanguageState: AppLanguageState,
    onUpdateAppLanguage: (String, Map<UiTextKey, String>) -> Unit,
    onBack: () -> Unit
) {
    val viewModel: SpeechViewModel = viewModel()

    val scope = rememberCoroutineScope()
    val (uiText, uiLanguageNameFor) = rememberUiTextFunctions(appLanguageState)

    val recognizedText = viewModel.recognizedText
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    val supportedLanguages by remember {
        mutableStateOf(AzureLanguageConfig.loadSupportedLanguages(context))
    }
    var selectedLanguage by remember {
        mutableStateOf(supportedLanguages.firstOrNull() ?: "en-US")
    }
    var selectedTargetLanguage by remember {
        mutableStateOf(supportedLanguages.getOrNull(1) ?: "zh-HK")
    }

    val translatedText = viewModel.translatedText
    val ttsStatus = viewModel.ttsStatus
    val isTtsRunning = viewModel.isTtsRunning

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

        RecordAudioPermissionRequest {
            val scrollState = rememberScrollState()

            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(start = 16.dp, end = 16.dp, top = 16.dp)
                    .fillMaxWidth()
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AppLanguageDropdown(
                    uiLanguages = uiLanguages,
                    appLanguageState = appLanguageState,
                    onUpdateAppLanguage = onUpdateAppLanguage,
                    uiText = uiText
                )

                // 1) Instruction text
                Text(
                    text = uiText(
                        UiTextKey.Instructions,
                        "Select User Interface language on top, then the detect and translate languages. " +
                                "Support languages: English, Cantonese, Japanese, Mandarin..."
                    ),
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // 2) Recognition language selector
                LanguageDropdownField(
                    label = uiText(UiTextKey.DetectLanguageLabel, "Detect language"),
                    selectedCode = selectedLanguage,
                    options = supportedLanguages,
                    nameFor = uiLanguageNameFor,
                    onSelected = { selectedLanguage = it }
                )

                // 3) Target language selector
                LanguageDropdownField(
                    label = uiText(UiTextKey.TranslateToLabel, "Translate to"),
                    selectedCode = selectedTargetLanguage,
                    options = supportedLanguages,
                    nameFor = uiLanguageNameFor,
                    onSelected = { selectedTargetLanguage = it }
                )

                // 4) Azure recognition
                Button(
                    onClick = { viewModel.recognize(selectedLanguage) },
                    enabled = !AudioRecorder.isRecording,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        uiText(
                            UiTextKey.AzureRecognizeButton,
                            "Use Azure Recognize (from Mic)"
                        )
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(text = recognizedText, modifier = Modifier.padding(top = 8.dp))

                Button(
                    onClick = {
                        clipboardManager.setText(AnnotatedString(recognizedText))
                    },
                    enabled = recognizedText.isNotEmpty()
                ) {
                    Text(uiText(UiTextKey.CopyButton, "Copy"))
                }

                // Speak original
                Button(
                    onClick = { viewModel.speakOriginal(selectedLanguage) },
                    enabled = recognizedText.isNotBlank() && !isTtsRunning,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        if (isTtsRunning)
                            uiText(UiTextKey.SpeakingLabel, "Speaking...")
                        else
                            uiText(UiTextKey.SpeakScriptButton, "Speak script")
                    )
                }

                // Translate
                Button(
                    onClick = {
                        viewModel.translate(
                            fromLanguage = selectedLanguage,
                            toLanguage = selectedTargetLanguage
                        )
                    },
                    enabled = recognizedText.isNotBlank(),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(uiText(UiTextKey.TranslateButton, "Translate"))
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(text = translatedText, modifier = Modifier.padding(top = 8.dp))

                Button(
                    onClick = {
                        clipboardManager.setText(AnnotatedString(translatedText))
                    },
                    enabled = translatedText.isNotEmpty()
                ) {
                    Text(uiText(UiTextKey.CopyTranslationButton, "Copy Translation"))
                }

                // Speak translation
                Button(
                    onClick = { viewModel.speakTranslation(selectedTargetLanguage) },
                    enabled = translatedText.isNotBlank() && !isTtsRunning,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        if (isTtsRunning)
                            uiText(UiTextKey.SpeakingLabel, "Speaking...")
                        else
                            uiText(UiTextKey.SpeakTranslationButton, "Speak Translation")
                    )
                }

                if (ttsStatus.isNotBlank()) {
                    Text(ttsStatus)
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContinuousConversationScreen(
    uiLanguages: List<Pair<String, String>>,
    appLanguageState: AppLanguageState,
    onUpdateAppLanguage: (String, Map<UiTextKey, String>) -> Unit,
    onBack: () -> Unit
) {
    val viewModel: SpeechViewModel = viewModel()
    val (uiText, uiLanguageNameFor) = rememberUiTextFunctions(appLanguageState)
    val context = LocalContext.current

    val supportedLanguages by remember {
        mutableStateOf(AzureLanguageConfig.loadSupportedLanguages(context))
    }

    // conversation languages
    var fromLanguage by remember { mutableStateOf(supportedLanguages.firstOrNull() ?: "en-US") }
    var toLanguage by remember { mutableStateOf(supportedLanguages.getOrNull(1) ?: "zh-HK") }

    // true = person A speaking (fromLanguage), false = person B speaking (toLanguage)
    var isPersonATalking by remember { mutableStateOf(true) }

    val isRunning = viewModel.isContinuousRunning
    val partial = viewModel.livePartialText
    val lastTranslation = viewModel.lastSegmentTranslation
    val messages = viewModel.continuousMessages

    val listState = rememberLazyListState()

    // auto‑restart when speaker switches
    LaunchedEffect(isPersonATalking) {
        if (isRunning) {
            viewModel.stopContinuous()
            val speakLang = if (isPersonATalking) fromLanguage else toLanguage
            val otherLang = if (isPersonATalking) toLanguage else fromLanguage
            viewModel.startContinuous(
                speakingLang = speakLang,
                targetLang = otherLang,
                isFromPersonA = isPersonATalking
            )
        }
    }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.lastIndex)
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.stopContinuous()
            viewModel.clearContinuousMessages()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Continuous conversation") },
                navigationIcon = {
                    IconButton(onClick = {
                        viewModel.stopContinuous()
                        onBack()
                    }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->

        RecordAudioPermissionRequest {
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                // controls area (scrollable if needed)
                Column(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    AppLanguageDropdown(
                        uiLanguages = uiLanguages,
                        appLanguageState = appLanguageState,
                        onUpdateAppLanguage = onUpdateAppLanguage,
                        uiText = uiText
                    )

                    LanguageDropdownField(
                        label = "Person A language",
                        selectedCode = fromLanguage,
                        options = supportedLanguages,
                        nameFor = uiLanguageNameFor,
                        onSelected = { fromLanguage = it }
                    )

                    LanguageDropdownField(
                        label = "Person B language",
                        selectedCode = toLanguage,
                        options = supportedLanguages,
                        nameFor = uiLanguageNameFor,
                        onSelected = { toLanguage = it }
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isPersonATalking)
                                "Now: Person A speaking"
                            else
                                "Now: Person B speaking"
                        )

                        androidx.compose.material3.Switch(
                            checked = isPersonATalking,
                            onCheckedChange = { checked ->
                                isPersonATalking = checked
                            }
                        )
                    }

                    Button(
                        onClick = {
                            if (isRunning) {
                                viewModel.stopContinuous()
                            } else {
                                val speakLang = if (isPersonATalking) fromLanguage else toLanguage
                                val otherLang = if (isPersonATalking) toLanguage else fromLanguage
                                viewModel.startContinuous(
                                    speakingLang = speakLang,
                                    targetLang = otherLang,
                                    isFromPersonA = isPersonATalking
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(if (isRunning) "Stop listening" else "Start conversation")
                    }

                    Spacer(Modifier.height(8.dp))

                    Text("Listening partial: $partial")
                    if (lastTranslation.isNotBlank()) {
                        Text("Last translation: $lastTranslation")
                    }
                }

                // chat list
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    state = listState
                ) {
                    items(messages, key = { it.id }) { msg ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = if (msg.isFromPersonA) {
                                Arrangement.End       // Person A on the right
                            } else {
                                Arrangement.Start      // Person B on the left
                            }
                        ) {
                            Column(
                                modifier = Modifier
                                    .widthIn(max = 280.dp)
                                    .padding(horizontal = 8.dp)
                            ) {
                                // speaker + type
                                Text(
                                    text = buildString {
                                        append(if (msg.isFromPersonA) "Person A" else "Person B")
                                        if (msg.isTranslation) append(" · translation")
                                    },
                                    style = MaterialTheme.typography.labelSmall
                                )

                                // message text
                                Text(
                                    text = msg.text,
                                    style = MaterialTheme.typography.bodyMedium
                                )

                                // speak button
                                Row(
                                    horizontalArrangement = Arrangement.End,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Button(
                                        onClick = {
                                            viewModel.speakText(
                                                languageCode = msg.lang,
                                                text = msg.text
                                            )
                                        }
                                    ) {
                                        Text("🔊")
                                    }
                                }
                            }
                        }
                    }
                }
            }
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
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier.fillMaxWidth()
    ) {
        TextField(
            value = nameFor(selectedCode),
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier
                .menuAnchor(type = MenuAnchorType.PrimaryNotEditable, enabled = true)
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { code ->
                androidx.compose.material3.DropdownMenuItem(
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