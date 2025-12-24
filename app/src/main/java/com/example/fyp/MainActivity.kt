package com.example.fyp

import android.Manifest
import android.os.Bundle
import android.util.Base64
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import com.example.fyp.ui.theme.FYPTheme
import com.google.accompanist.permissions.*
import com.microsoft.cognitiveservices.speech.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.TextField
import androidx.compose.foundation.clickable
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MenuAnchorType

object NetworkClient {
    val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .build()
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FYPTheme {
                SpeechRecognitionScreen()
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpeechRecognitionScreen() {
    val scope = rememberCoroutineScope()
    val instructions =
        "Press Azure button of recognition. Use Copy to copy the recognize text. " +
                "Support languages: English, Cantonese, Japanese, Mandarin..."

    var recognizedText by remember { mutableStateOf("") }

    val audioStream = remember { ByteArrayOutputStream() }
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

    RecordAudioPermissionRequest {
        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp, top = 48.dp)
                .fillMaxWidth()
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 1) Instruction text
            Text(
                text = instructions,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            /* 2a) Recognition language selector (Button)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Detect Language: ${LanguageDisplayNames.displayName(selectedLanguage)}")
                Button(onClick = {
                    val currentIndex = supportedLanguages.indexOf(selectedLanguage)
                        .takeIf { it >= 0 } ?: 0
                    val nextIndex = (currentIndex + 1) % supportedLanguages.size
                    selectedLanguage = supportedLanguages[nextIndex]
                }) {
                    Text("Change")
                }
            }
            */

            // 2b) Recognition language selector (dropdown)
            var isRecLangMenuExpanded by remember { mutableStateOf(false) }

            ExposedDropdownMenuBox(
                expanded = isRecLangMenuExpanded,
                onExpandedChange = { isRecLangMenuExpanded = !isRecLangMenuExpanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                TextField(
                    value = LanguageDisplayNames.displayName(selectedLanguage),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Detect language") },
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
                            text = { Text(LanguageDisplayNames.displayName(code)) },
                            onClick = {
                                selectedLanguage = code
                                isRecLangMenuExpanded = false
                            }
                        )
                    }
                }
            }

            /* 3a) Translation target language selector (Button)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Translate to: ${LanguageDisplayNames.displayName(selectedTargetLanguage)}")
                Button(onClick = {
                    val currentIndex = supportedLanguages.indexOf(selectedTargetLanguage)
                        .takeIf { it >= 0 } ?: 0
                    val nextIndex = (currentIndex + 1) % supportedLanguages.size
                    selectedTargetLanguage = supportedLanguages[nextIndex]
                }) {
                    Text("Change")
                }
            }
            */

            // 3b) Translation target language selector (dropdown)
            var isTargetLangMenuExpanded by remember { mutableStateOf(false) }

            ExposedDropdownMenuBox(
                expanded = isTargetLangMenuExpanded,
                onExpandedChange = { isTargetLangMenuExpanded = !isTargetLangMenuExpanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                TextField(
                    value = LanguageDisplayNames.displayName(selectedTargetLanguage),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Translate to") },
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
                            text = { Text(LanguageDisplayNames.displayName(code)) },
                            onClick = {
                                selectedTargetLanguage = code
                                isTargetLangMenuExpanded = false
                            }
                        )
                    }
                }
            }

            // 4) Azure recognition button
            Button(
                onClick = {
                    scope.launch {
                        recognizedText = "Recording with Azure, SPEAK and plz WAIT..."
                        when (val result = recognizeSpeechWithAzure(selectedLanguage)) {
                            is SpeechResult.Success -> recognizedText = result.text
                            is SpeechResult.Error -> recognizedText = "Azure error: ${result.message}"
                        }
                    }
                },
                enabled = !AudioRecorder.isRecording,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Use Azure Recognize (from Mic)")
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Original text
            Text(
                text = recognizedText,
                modifier = Modifier.padding(top = 8.dp)
            )

            Button(
                onClick = {
                    clipboardManager.setText(AnnotatedString(recognizedText))
                },
                enabled = recognizedText.isNotEmpty()
            ) {
                Text("Copy")
            }

            // Speak original
            Button(
                onClick = {
                    scope.launch {
                        isTtsRunning = true
                        ttsStatus = "Speaking original text, please wait..."
                        when (val result = speakWithAzure(recognizedText, selectedLanguage)) {
                            is SpeechResult.Success -> {
                                ttsStatus = "Finished speaking original text."
                            }
                            is SpeechResult.Error -> {
                                ttsStatus = "TTS error: ${result.message}"
                            }
                        }
                        isTtsRunning = false
                    }
                },
                enabled = recognizedText.isNotBlank() && !isTtsRunning,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (isTtsRunning) "Speaking..." else "Speak script")
            }

            // Translate
            Button(
                onClick = {
                    scope.launch {
                        translatedText = "Translating, please wait..."
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
                Text("Translate")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Translated text
            Text(
                text = translatedText,
                modifier = Modifier.padding(top = 8.dp)
            )

            Button(
                onClick = {
                    clipboardManager.setText(AnnotatedString(translatedText))
                },
                enabled = translatedText.isNotEmpty()
            ) {
                Text("Copy Translation")
            }

            // Speak translation
            Button(
                onClick = {
                    scope.launch {
                        isTtsRunning = true
                        ttsStatus = "Speaking translation, please wait..."
                        when (val result = speakWithAzure(translatedText, selectedTargetLanguage)) {
                            is SpeechResult.Success -> {
                                ttsStatus = "Finished speaking translation."
                            }
                            is SpeechResult.Error -> {
                                ttsStatus = "TTS error: ${result.message}"
                            }
                        }
                        isTtsRunning = false
                    }
                },
                enabled = translatedText.isNotBlank() && !isTtsRunning,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (isTtsRunning) "Speaking..." else "Speak Translation")
            }

            if (ttsStatus.isNotBlank()) {
                Text(ttsStatus)
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

// --- AZURE ---

private const val SAMPLE_RATE = 16000

// Azure recognition
suspend fun recognizeSpeechWithAzure(languageCode: String): SpeechResult =
    withContext(Dispatchers.IO) {
        val azureKey = BuildConfig.AZURE_SPEECH_KEY
        val region = BuildConfig.AZURE_SPEECH_REGION

        try {
            val speechConfig = SpeechConfig.fromSubscription(azureKey, region)
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

// Azure text-to-speech
suspend fun speakWithAzure(text: String, languageCode: String): SpeechResult =
    withContext(Dispatchers.IO) {
        if (text.isBlank()) {
            return@withContext SpeechResult.Error("No text to speak")
        }

        val azureKey = BuildConfig.AZURE_SPEECH_KEY
        val region = BuildConfig.AZURE_SPEECH_REGION

        try {
            val speechConfig = SpeechConfig.fromSubscription(azureKey, region)
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

// --- PERMISSION REQUEST

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