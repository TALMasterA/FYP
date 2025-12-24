package com.example.fyp

import android.Manifest
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
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.microsoft.cognitiveservices.speech.CancellationDetails
import com.microsoft.cognitiveservices.speech.ResultReason
import com.microsoft.cognitiveservices.speech.SpeechConfig
import com.microsoft.cognitiveservices.speech.SpeechRecognizer
import com.microsoft.cognitiveservices.speech.SpeechSynthesisCancellationDetails
import com.microsoft.cognitiveservices.speech.SpeechSynthesizer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

// -------- network client --------

object NetworkClient {
    val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .build()
    }
}

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
    var isUiLangMenuExpanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = isUiLangMenuExpanded,
        onExpandedChange = { isUiLangMenuExpanded = !isUiLangMenuExpanded },
        modifier = Modifier.fillMaxWidth()
    ) {
        TextField(
            value = uiLanguages.first { it.first == appLanguageState.selectedUiLanguage }.second,
            onValueChange = {},
            readOnly = true,
            label = { Text(uiText(UiTextKey.AppUiLanguageLabel, "App UI language")) },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = isUiLangMenuExpanded)
            },
            modifier = Modifier
                .menuAnchor(type = MenuAnchorType.PrimaryNotEditable, enabled = true)
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = isUiLangMenuExpanded,
            onDismissRequest = { isUiLangMenuExpanded = false }
        ) {
            uiLanguages.forEach { (code, label) ->
                androidx.compose.material3.DropdownMenuItem(
                    text = { Text(label) },
                    onClick = {
                        isUiLangMenuExpanded = false

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
                                            val map =
                                                UiTextKey.entries.zip(parts).toMap()
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
        }
    }
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
                onOpenHelp = { navController.navigate(AppScreen.Help.route) }
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
    onOpenHelp: () -> Unit
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
    val scope = rememberCoroutineScope()
    val (uiText, uiLanguageNameFor) = rememberUiTextFunctions(appLanguageState)

    var recognizedText by remember { mutableStateOf("") }
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

    var translatedText by remember { mutableStateOf("") }
    var ttsStatus by remember { mutableStateOf("") }
    var isTtsRunning by remember { mutableStateOf(false) }

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
                var isRecLangMenuExpanded by remember { mutableStateOf(false) }

                ExposedDropdownMenuBox(
                    expanded = isRecLangMenuExpanded,
                    onExpandedChange = { isRecLangMenuExpanded = !isRecLangMenuExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextField(
                        value = uiLanguageNameFor(selectedLanguage),
                        onValueChange = {},
                        readOnly = true,
                        label = {
                            Text(uiText(UiTextKey.DetectLanguageLabel, "Detect language"))
                        },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = isRecLangMenuExpanded)
                        },
                        modifier = Modifier
                            .menuAnchor(type = MenuAnchorType.PrimaryNotEditable, enabled = true)
                            .fillMaxWidth()
                    )

                    ExposedDropdownMenu(
                        expanded = isRecLangMenuExpanded,
                        onDismissRequest = { isRecLangMenuExpanded = false }
                    ) {
                        supportedLanguages.forEach { code ->
                            androidx.compose.material3.DropdownMenuItem(
                                text = { Text(uiLanguageNameFor(code)) },
                                onClick = {
                                    selectedLanguage = code
                                    isRecLangMenuExpanded = false
                                }
                            )
                        }
                    }
                }

                // 3) Target language selector
                var isTargetLangMenuExpanded by remember { mutableStateOf(false) }

                ExposedDropdownMenuBox(
                    expanded = isTargetLangMenuExpanded,
                    onExpandedChange = { isTargetLangMenuExpanded = !isTargetLangMenuExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextField(
                        value = uiLanguageNameFor(selectedTargetLanguage),
                        onValueChange = {},
                        readOnly = true,
                        label = {
                            Text(uiText(UiTextKey.TranslateToLabel, "Translate to"))
                        },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = isTargetLangMenuExpanded)
                        },
                        modifier = Modifier
                            .menuAnchor(type = MenuAnchorType.PrimaryNotEditable, enabled = true)
                            .fillMaxWidth()
                    )

                    ExposedDropdownMenu(
                        expanded = isTargetLangMenuExpanded,
                        onDismissRequest = { isTargetLangMenuExpanded = false }
                    ) {
                        supportedLanguages.forEach { code ->
                            androidx.compose.material3.DropdownMenuItem(
                                text = { Text(uiLanguageNameFor(code)) },
                                onClick = {
                                    selectedTargetLanguage = code
                                    isTargetLangMenuExpanded = false
                                }
                            )
                        }
                    }
                }

                // 4) Azure recognition
                Button(
                    onClick = {
                        scope.launch {
                            recognizedText = uiText(
                                UiTextKey.RecognizingStatus,
                                "Recording with Azure, SPEAK and plz WAIT..."
                            )
                            when (val result = recognizeSpeechWithAzure(selectedLanguage)) {
                                is SpeechResult.Success -> recognizedText = result.text
                                is SpeechResult.Error -> recognizedText = "Azure error: ${result.message}"
                            }
                        }
                    },
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
                    onClick = {
                        scope.launch {
                            isTtsRunning = true
                            ttsStatus = uiText(
                                UiTextKey.SpeakingOriginalStatus,
                                "Speaking original text, please wait..."
                            )
                            when (val result = speakWithAzure(recognizedText, selectedLanguage)) {
                                is SpeechResult.Success -> {
                                    ttsStatus = uiText(
                                        UiTextKey.FinishedSpeakingOriginal,
                                        "Finished speaking original text."
                                    )
                                }

                                is SpeechResult.Error -> {
                                    val fmt = uiText(
                                        UiTextKey.TtsErrorTemplate,
                                        "TTS error: %s"
                                    )
                                    ttsStatus = String.format(fmt, result.message)
                                }
                            }
                            isTtsRunning = false
                        }
                    },
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
                        scope.launch {
                            translatedText = uiText(
                                UiTextKey.TranslatingStatus,
                                "Translating, please wait..."
                            )
                            val result = TranslatorClient.translateText(
                                text = recognizedText,
                                toLanguage = selectedTargetLanguage,
                                fromLanguage = selectedLanguage
                            )
                            translatedText = when (result) {
                                is SpeechResult.Success -> result.text
                                is SpeechResult.Error -> "Translation error: ${result.message}"
                            }
                        }
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
                    onClick = {
                        scope.launch {
                            isTtsRunning = true
                            ttsStatus = uiText(
                                UiTextKey.SpeakingTranslationStatus,
                                "Speaking translation, please wait..."
                            )
                            when (val result = speakWithAzure(translatedText, selectedTargetLanguage)) {
                                is SpeechResult.Success -> {
                                    ttsStatus = uiText(
                                        UiTextKey.FinishedSpeakingTranslation,
                                        "Finished speaking translation."
                                    )
                                }

                                is SpeechResult.Error -> {
                                    val fmt = uiText(
                                        UiTextKey.TtsErrorTemplate,
                                        "TTS error: %s"
                                    )
                                    ttsStatus = String.format(fmt, result.message)
                                }
                            }
                            isTtsRunning = false
                        }
                    },
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

// --- AZURE ---

suspend fun recognizeSpeechWithAzure(languageCode: String): SpeechResult =
    withContext(Dispatchers.IO) {
        try {
            val speechConfig: SpeechConfig = AzureSpeechProvider.speechConfig()
            speechConfig.speechRecognitionLanguage = languageCode

            val recognizer = SpeechRecognizer(speechConfig)

            try {
                val result = recognizer.recognizeOnceAsync().get()

                if (result.reason == ResultReason.RecognizedSpeech) {
                    Log.i("AzureSpeech", "Recognized: ${result.text}")
                    SpeechResult.Success(result.text)
                } else {
                    val errorDetails = if (result.reason == ResultReason.Canceled) {
                        val cancellation = CancellationDetails.fromResult(result)
                        "Canceled: ${cancellation.reason}. Error details: ${cancellation.errorDetails}"
                    } else {
                        result.reason.toString()
                    }
                    Log.e("AzureSpeech", "Speech not recognized, reason: $errorDetails")
                    SpeechResult.Error("Azure Speech not recognized: $errorDetails")
                }
            } finally {
                recognizer.close()
            }
        } catch (ex: Exception) {
            Log.e("AzureSpeech", "Error: ${ex.message}")
            SpeechResult.Error("Error recognizing speech: ${ex.message}")
        }
    }

suspend fun speakWithAzure(text: String, languageCode: String): SpeechResult =
    withContext(Dispatchers.IO) {
        if (text.isBlank()) {
            return@withContext SpeechResult.Error("No text to speak")
        }
        try {
            val speechConfig: SpeechConfig = AzureSpeechProvider.speechConfig()
            speechConfig.speechSynthesisLanguage = languageCode

            val synthesizer = SpeechSynthesizer(speechConfig)

            try {
                val result = synthesizer.SpeakTextAsync(text).get()

                if (result.reason == ResultReason.SynthesizingAudioCompleted) {
                    Log.i("AzureTTS", "Speech synthesized for text: $text")
                    SpeechResult.Success("Spoken successfully")
                } else if (result.reason == ResultReason.Canceled) {
                    val cancellation =
                        SpeechSynthesisCancellationDetails.fromResult(result)
                    val msg =
                        "TTS canceled: ${cancellation.reason}. ${cancellation.errorDetails}"
                    Log.e("AzureTTS", msg)
                    SpeechResult.Error(msg)
                } else {
                    SpeechResult.Error("TTS failed: ${result.reason}")
                }
            } finally {
                synthesizer.close()
            }
        } catch (ex: Exception) {
            Log.e("AzureTTS", "Error: ${ex.message}", ex)
            SpeechResult.Error("Error speaking text: ${ex.message}")
        }
    }

// --- PERMISSION REQUEST ---

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RecordAudioPermissionRequest(
    onPermissionGranted: @Composable () -> Unit
) {
    val permissionState = rememberPermissionState(Manifest.permission.RECORD_AUDIO)

    LaunchedEffect(Unit) {
        if (!permissionState.status.isGranted) {
            permissionState.launchPermissionRequest()
        }
    }

    when {
        permissionState.status.isGranted -> onPermissionGranted()

        permissionState.status.shouldShowRationale || !permissionState.status.isGranted -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Microphone permission is needed for speech recognition. Please grant the permission.",
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Button(onClick = { permissionState.launchPermissionRequest() }) {
                    Text("Grant Permission")
                }
            }
        }
    }
}