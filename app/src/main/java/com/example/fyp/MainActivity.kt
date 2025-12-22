package com.example.fyp

import android.Manifest
import android.os.Bundle
import android.util.Base64
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import kotlin.coroutines.resume
import okhttp3.RequestBody.Companion.toRequestBody
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit
import androidx.compose.ui.platform.LocalContext
import com.example.fyp.AudioRecorder
import com.example.fyp.BuildConfig

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

@Composable
fun SpeechRecognitionScreen() {
    val scope = rememberCoroutineScope()
    val instructions =
        "If Use Azure, just press Azure button. For Google, please start recording-stop then press Use Google button. Use Copy to copy the recognize text."

    var recognizedText by remember { mutableStateOf("") }
    var prefixText by remember { mutableStateOf("") }
    var recordedAudioData by remember { mutableStateOf<ByteArray?>(null) }
    val googleApiKey = BuildConfig.GOOGLE_API_KEY

    val audioStream = remember { ByteArrayOutputStream() }
    val clipboardManager = LocalClipboardManager.current

    val context = LocalContext.current
    var selectedLanguage by remember { mutableStateOf("en-US") }
    val supportedLanguages by remember {
        mutableStateOf(AzureLanguageConfig.loadSupportedLanguages(context))
    }

    RecordAudioPermissionRequest {
        Column(
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp, top = 48.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 1) Instruction text
            Text(
                text = instructions,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // 2) Language selector row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Language: $selectedLanguage")
                Button(onClick = {
                    val currentIndex = supportedLanguages.indexOf(selectedLanguage).takeIf { it >= 0 } ?: 0
                    val nextIndex = (currentIndex + 1) % supportedLanguages.size
                    selectedLanguage = supportedLanguages[nextIndex]
                }) {
                    Text("Change")
                }
            }

            // 3) Recording button
            Button(
                onClick = {
                    if (AudioRecorder.isRecording) {
                        recordedAudioData = AudioRecorder.stop(audioStream)
                        prefixText = "Recording stopped. Ready to recognize."
                        recognizedText = ""
                    } else {
                        recordedAudioData = null
                        AudioRecorder.start(audioStream)
                        prefixText = "Recording..."
                        recognizedText = ""
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (AudioRecorder.isRecording) "Stop Recording" else "Start Recording")
            }

            // 4) Azure button
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

            // 5) Google button
            Button(
                onClick = {
                    scope.launch {
                        recognizedText = "Processing with Google, plz WAIT..."
                        val googleResult = recognizeSpeechWithGoogle(recordedAudioData, googleApiKey)
                        recognizedText = googleResult
                    }
                },
                enabled = recordedAudioData != null && !AudioRecorder.isRecording,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Use Google Recognize")
            }

            Spacer(modifier = Modifier.height(24.dp))

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
        }
    }
}

// --- GOOGLE AND AZURE FUNCTIONS ---

private const val SAMPLE_RATE = 16000

suspend fun recognizeSpeechWithGoogle(audioData: ByteArray?, apiKey: String): String =
    suspendCancellableCoroutine { cont ->
        if (audioData == null || audioData.isEmpty()) {
            cont.resume("Error: No audio data to recognize.")
            return@suspendCancellableCoroutine
        }

        val audioBase64 = Base64.encodeToString(audioData, Base64.NO_WRAP)

        val alternativeLanguages = org.json.JSONArray(listOf("zh-HK", "ja-JP"))
        val config = JSONObject().apply {
            put("encoding", "LINEAR16")
            put("sampleRateHertz", SAMPLE_RATE)
            put("languageCode", "en-US")
            put("alternativeLanguageCodes", alternativeLanguages)
        }


        val audio = JSONObject().apply {
            put("content", audioBase64)
        }
        val requestBodyJson = JSONObject().apply {
            put("config", config)
            put("audio", audio)
        }
        val jsonString = requestBodyJson.toString()

        val client = NetworkClient.okHttpClient
        val requestBody = jsonString.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        val request = Request.Builder()
            .url("https://speech.googleapis.com/v1/speech:recognize?key=$apiKey")
            .post(requestBody)
            .build()


        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: java.io.IOException) {
                if (cont.isActive) {
                    Log.e("GoogleSpeech", "Network failure", e)
                    cont.resume("Google API Error: ${e.message}")
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (!cont.isActive) return
                response.use { resp ->
                    val body = resp.body?.string()
                    if (!resp.isSuccessful) {
                        val errorMsg = "Google API Error: HTTP ${resp.code}. Body: $body"
                        Log.e("GoogleSpeech", errorMsg)
                        cont.resume("Google recongize failed: HTTP ${resp.code}")
                        return
                    }
                    if (body.isNullOrEmpty()) {
                        cont.resume("Google API not sending")
                        return
                    }
                    try {
                        val jsonObject = JSONObject(body)
                        val results = jsonObject.optJSONArray("results")
                        if (results != null && results.length() > 0) {
                            val transcript = results.getJSONObject(0).getJSONArray("alternatives").getJSONObject(0).getString("transcript")
                            Log.i("GoogleSpeech", "Recognized: $transcript")
                            cont.resume(transcript)
                        } else {
                            Log.w("GoogleSpeech", "No recognition results found: $body")
                            cont.resume("No Google result")
                        }
                    } catch (e: Exception) {
                        Log.e("GoogleSpeech", "Error parsing Google response: ${e.message}. Raw: $body", e)
                        cont.resume("Google recongize failed: ${e.message}")
                    }
                }
            }
        })

        cont.invokeOnCancellation {
            client.dispatcher.cancelAll()
        }
    }

suspend fun recognizeSpeechWithAzure(languageCode: String): SpeechResult =
    withContext(Dispatchers.IO) {
        val azureKey = BuildConfig.AZURE_SPEECH_KEY
        val region = "eastasia"

        try {
            val speechConfig = SpeechConfig.fromSubscription(azureKey, region)

            // <<< set the chosen language here
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
                modifier = Modifier.fillMaxSize().padding(16.dp),
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