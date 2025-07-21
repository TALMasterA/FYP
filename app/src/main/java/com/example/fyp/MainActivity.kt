package com.example.fyp

import android.Manifest
import android.content.Context
import android.media.MediaRecorder
import android.media.MediaPlayer
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.fyp.ui.theme.FYPTheme
import com.google.accompanist.permissions.*
import com.microsoft.cognitiveservices.speech.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONObject
import android.util.Base64
import android.util.Log
import java.io.File
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import com.example.fyp.BuildConfig

val googleApiKey = BuildConfig.GOOGLE_API_KEY
val azureSpeechKey = BuildConfig.AZURE_SPEECH_KEY

// For recording
private var mediaRecorder: MediaRecorder? = null
private var mediaPlayer: MediaPlayer? = null
private var audioFile: File? = null

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
        mediaRecorder?.release()
        mediaRecorder = null
        mediaPlayer?.release()
        mediaPlayer = null
    }
}

@Composable
fun SpeechRecognitionScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var recognizedText by remember { mutableStateOf("Tap a button to start.") }
    var isRecording by remember { mutableStateOf(false) }
    var isPlaying by remember { mutableStateOf(false) }

    // Your Google Cloud API Key
    val googleApiKey = "0bc890feea19121de80b044c92a7ebefa84fb8fe" // Replace

    RecordAudioPermissionRequest {
        Column(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = {
                    if (isRecording) {
                        stopRecording()
                        isRecording = false
                    } else {
                        audioFile = File(context.filesDir, "recorded_audio.3gp")
                        startRecording(context, audioFile?.absolutePath)
                        isRecording = true
                        recognizedText = "Recording..."
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (isRecording) "Stop Recording" else "Start Recording")
            }

            Button(
                onClick = {
                    if (isPlaying) {
                        stopPlaying()
                        isPlaying = false
                    } else {
                        startPlaying(audioFile?.absolutePath)
                        isPlaying = true
                    }
                },
                enabled = audioFile != null && !isRecording,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (isPlaying) "Stop Playing" else "Play Recording")
            }

            Button(
                onClick = {
                    scope.launch {
                        recognizedText = "Processing with Azure..."
                        val azureResult = recognizeSpeechWithAzure()
                        recognizedText = "Azure: $azureResult"
                    }
                },
                enabled = audioFile != null && !isRecording,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Use Azure Recognize")
            }

            Button(
                onClick = {
                    scope.launch {
                        recognizedText = "Processing with Google..."
                        val googleResult = recognizeSpeechWithGoogle(audioFile?.absolutePath ?: "", googleApiKey)
                        recognizedText = "Google: $googleResult"
                    }
                },
                enabled = audioFile != null && !isRecording,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Use Google Recognize")
            }

            Text(text = recognizedText, modifier = Modifier.padding(top = 16.dp))
        }
    }
}

fun startRecording(context: Context, filePath: String?) {
    if (filePath == null) {
        Log.e("Recorder", "Audio file path is null.")
        return
    }
    mediaRecorder = MediaRecorder().apply {
        setAudioSource(MediaRecorder.AudioSource.MIC)
        setAudioSamplingRate(16000) // Set sampling rate to 16kHz for better Google API compatibility
        setAudioEncodingBitRate(96000) // Adjust bit rate
        setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP) // Using 3GP, it will contain AAC audio which can be used
        setAudioEncoder(MediaRecorder.AudioEncoder.AAC) // AAC for better quality
        setOutputFile(filePath)
        try {
            prepare()
            start()
            Log.d("Recorder", "Recording started to: $filePath")
        } catch (e: IOException) {
            Log.e("Recorder", "prepare() failed: ${e.message}")
            release()
            mediaRecorder = null
        } catch (e: IllegalStateException) {
            Log.e("Recorder", "start() failed: ${e.message}")
            release()
            mediaRecorder = null
        }
    }
}

fun stopRecording() {
    mediaRecorder?.apply {
        try {
            stop()
            release()
            Log.d("Recorder", "Recording stopped.")
        } catch (e: RuntimeException) {
            Log.e("Recorder", "stop() failed: ${e.message}")
        } finally {
            mediaRecorder = null
        }
    }
}

fun startPlaying(filePath: String?) {
    if (filePath == null) {
        Log.e("Player", "Audio file path is null.")
        return
    }
    mediaPlayer = MediaPlayer().apply {
        try {
            setDataSource(filePath)
            prepare()
            start()
            Log.d("Player", "Playing started from: $filePath")
            setOnCompletionListener {
                stopPlaying()
            }
        } catch (e: IOException) {
            Log.e("Player", "prepare() failed: ${e.message}")
            release()
            mediaPlayer = null
        }
    }
}

fun stopPlaying() {
    mediaPlayer?.apply {
        try {
            stop()
            release()
            Log.d("Player", "Playing stopped.")
        } catch (e: IllegalStateException) {
            Log.e("Player", "stop() failed: ${e.message}")
        } finally {
            mediaPlayer = null
        }
    }
}

suspend fun recognizeSpeechWithAzure(): String {
    val subscriptionKey = "***REMOVED***" //Replace
    val region = "eastasia"

    return try {
        val speechConfig = SpeechConfig.fromSubscription(subscriptionKey, region)
        // file input for Azure, use:
        // val audioConfig = AudioConfig.fromWavFileInput(audioFile?.absolutePath)
        // val recognizer = SpeechRecognizer(speechConfig, audioConfig)
        val recognizer = SpeechRecognizer(speechConfig)

        val result = recognizer.recognizeOnceAsync().get()
        recognizer.close()

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

suspend fun recognizeSpeechWithGoogle(audioFilePath: String, apiKey: String): String =
    suspendCancellableCoroutine { cont ->
        if (audioFilePath.isEmpty() || !File(audioFilePath).exists()) {
            cont.resume("Error: Audio file not found or path is empty.")
            return@suspendCancellableCoroutine
        }

        val audioFile = File(audioFilePath)
        val audioBytes: ByteArray
        try {
            audioBytes = audioFile.readBytes()
        } catch (e: IOException) {
            cont.resume("Error reading audio file: ${e.message}")
            return@suspendCancellableCoroutine
        }

        val audioBase64 = Base64.encodeToString(audioBytes, Base64.NO_WRAP)

        val config = JSONObject().apply {
            put("encoding", "LINEAR16")
            put("sampleRateHertz", 16000)
            put("languageCode", "en-UK")
        }
        val audio = JSONObject().apply {
            put("content", audioBase64)
        }
        val requestBodyJson = JSONObject().apply {
            put("config", config)
            put("audio", audio)
        }
        val jsonString = requestBodyJson.toString()

        val client = OkHttpClient()
        val requestBody = RequestBody.create("application/json; charset=utf-8".toMediaTypeOrNull(), jsonString)
        val request = Request.Builder()
            .url("https://speech.googleapis.com/v1/speech:recognize?key=$apiKey")
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: java.io.IOException) {
                if (cont.isActive) {
                    cont.resumeWithException(e)
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (!cont.isActive) return

                response.use { resp ->
                    val body = resp.body?.string()
                    if (!resp.isSuccessful) {
                        val errorMsg = "Google API Error: HTTP ${resp.code}. Body: $body"
                        Log.e("GoogleSpeech", errorMsg)
                        cont.resume("Google辨識失敗: HTTP ${resp.code}")
                        return
                    }

                    if (body.isNullOrEmpty()) {
                        cont.resume("Google API未回傳辨識結果")
                        return
                    }

                    try {
                        val jsonObject = JSONObject(body)
                        val results = jsonObject.optJSONArray("results")
                        if (results != null && results.length() > 0) {
                            val transcript = results.getJSONObject(0)
                                .getJSONArray("alternatives")
                                .getJSONObject(0)
                                .getString("transcript")
                            Log.i("GoogleSpeech", "Recognized: $transcript")
                            cont.resume(transcript)
                        } else {
                            Log.w("GoogleSpeech", "No recognition results found in response: $body")
                            cont.resume("無Google辨識結果")
                        }
                    } catch (e: Exception) {
                        Log.e("GoogleSpeech", "Error parsing Google response: ${e.message}. Raw: $body", e)
                        cont.resume("解析Google結果失敗: ${e.message}")
                    }
                }
            }
        })

        cont.invokeOnCancellation {
            client.dispatcher.cancelAll()
        }
    }

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RecordAudioPermissionRequest(
    onPermissionGranted: @Composable () -> Unit
) {
    val permissionState = rememberPermissionState(Manifest.permission.RECORD_AUDIO)

    LaunchedEffect(Unit) {
        // Request permission
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
                horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
            ) {
                Text("Microphone permission is needed for speech recognition. Please grant the permission.", modifier = Modifier.padding(bottom = 8.dp))
                Button(onClick = { permissionState.launchPermissionRequest() }) {
                    Text("Grant Permission")
                }
            }
        }
    }
}
