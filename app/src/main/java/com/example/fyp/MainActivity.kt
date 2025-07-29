package com.example.fyp

import android.Manifest
import android.annotation.SuppressLint
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
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

// --- AudioRecord State ---
private var audioRecord: AudioRecord? = null
private var recordingThread: Thread? = null
private var isRecording by mutableStateOf(false)

// --- Audio Recording Parameters ---
private const val SAMPLE_RATE = 16000
private const val ENCODING = AudioFormat.ENCODING_PCM_16BIT
private const val CHANNEL_MASK = AudioFormat.CHANNEL_IN_MONO

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
        // Ensure recording is stopped and resources are released if the app is destroyed
        if (isRecording) {
            // *** FIXED: Call stopRecording with null because we only need to clean up ***
            stopRecording(null)
        }
    }
}

@Composable
fun SpeechRecognitionScreen() {
    val scope = rememberCoroutineScope()
    // Static instruction text, shown all the time at the top
    val instructions = "If Use Azure, just press Azure button. For Google, please start recording-stop then press Use Google button. Use Copy to copy the recognize text."

    var recognizedText by remember { mutableStateOf("") }
    var prefixText by remember { mutableStateOf("") }
    var recordedAudioData by remember { mutableStateOf<ByteArray?>(null) }
    val googleApiKey = BuildConfig.GOOGLE_API_KEY

    val audioStream = remember { ByteArrayOutputStream() }

    val clipboardManager = LocalClipboardManager.current

    RecordAudioPermissionRequest {
        Column(
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp, top = 48.dp)  // top padding to avoid camera
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = instructions,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Button(
                onClick = {
                    if (isRecording) {
                        recordedAudioData = stopRecording(audioStream)
                        prefixText = "Recording stopped. Ready to recognize."
                        recognizedText = ""
                    } else {
                        recordedAudioData = null
                        startRecording(audioStream)
                        prefixText = "Recording..."
                        recognizedText = ""
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (isRecording) "Stop Recording" else "Start Recording")
            }

            Button(
                onClick = {
                    scope.launch {
                        recognizedText = "Recording with Azure,SPEAK and plz WAIT..."
                        val azureResult = recognizeSpeechWithAzure()
                        recognizedText = azureResult
                    }
                },
                enabled = !isRecording,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Use Azure Recognize (from Mic)")
            }

            Button(
                onClick = {
                    scope.launch {
                        recognizedText = "Processing with Google, plz WAIT..."
                        val googleResult = recognizeSpeechWithGoogle(recordedAudioData, googleApiKey)
                        recognizedText = googleResult
                    }
                },
                enabled = recordedAudioData != null && !isRecording,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Use Google Recognize")
            }

            Spacer(modifier = Modifier.height(24.dp)) // Add some space before the recognition result text

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


// --- STABLE RECORDING LOGIC ---

@SuppressLint("MissingPermission") // Permission is handled by the Composable
fun startRecording(stream: ByteArrayOutputStream) {
    val bufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_MASK, ENCODING)
    if (bufferSize == AudioRecord.ERROR_BAD_VALUE) {
        Log.e("AudioRecord", "Invalid parameters for AudioRecord.")
        return
    }

    audioRecord = AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_RATE, CHANNEL_MASK, ENCODING, bufferSize)

    stream.reset() // Clear any previous data from the stream
    isRecording = true
    audioRecord?.startRecording()

    recordingThread = Thread {
        val data = ByteArray(bufferSize)
        while (isRecording) {
            val read = audioRecord?.read(data, 0, bufferSize) ?: 0
            if (read > 0) {
                stream.write(data, 0, read)
            }
        }
    }
    recordingThread?.start()
    Log.d("AudioRecord", "Recording started.")
}

// *** FIXED: The stream parameter is now nullable to allow cleanup calls ***
fun stopRecording(stream: ByteArrayOutputStream?): ByteArray? {
    if (isRecording) {
        isRecording = false
        try {
            recordingThread?.join() // Wait for the recording thread to finish
        } catch (e: InterruptedException) {
            Log.e("AudioRecord", "Recording thread interrupted", e)
        }
        recordingThread = null

        audioRecord?.stop()
        audioRecord?.release()
        audioRecord = null
        Log.d("AudioRecord", "Recording stopped and resources released.")
    }
    // Only return the byte array if a stream was provided
    return stream?.toByteArray()
}


// --- GOOGLE AND AZURE FUNCTIONS ---

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

suspend fun recognizeSpeechWithAzure(): String = withContext(Dispatchers.IO) {
    val azureKey = BuildConfig.AZURE_SPEECH_KEY
    val region = "eastasia"

    return@withContext try {
        val speechConfig = SpeechConfig.fromSubscription(azureKey, region)
        val autoDetectSourceLanguageConfig = AutoDetectSourceLanguageConfig.fromLanguages(listOf("en-US", "zh-HK", "ja-JP"))

        val recognizer = SpeechRecognizer(speechConfig, autoDetectSourceLanguageConfig)

        val result = recognizer.recognizeOnceAsync().get()
        val detectedLanguage = result.properties.getProperty("SpeechServiceConnection_AutoDetectedSourceLanguageCode")

        Log.i("AzureSpeech", "Detected language: $detectedLanguage")


        if (result.reason == ResultReason.RecognizedSpeech) {
            Log.i("AzureSpeech", "Recognized: ${result.text}")
            result.text
        } else {
            val errorDetails = if (result.reason == ResultReason.Canceled) {
                val cancellation = CancellationDetails.fromResult(result)
                "Canceled: ${cancellation.reason}. Error details: ${cancellation.errorDetails}"
            } else {
                result.reason.toString()
            }
            Log.e("AzureSpeech", "Speech not recognized, reason: $errorDetails")
            "Azure Speech not recognized: $errorDetails"
        }
    } catch (ex: Exception) {
        Log.e("AzureSpeech", "Error: ${ex.message}")
        "Error recognizing speech: ${ex.message}"
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
