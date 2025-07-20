package com.example.fyp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.fyp.ui.theme.FYPTheme
import com.microsoft.cognitiveservices.speech.*
import androidx.compose.material3.Button
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import android.util.Log
import com.google.accompanist.permissions.*


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
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    FYPTheme {
        Greeting("Android")
    }
}

@Composable
fun SpeechRecognitionScreen() {
    RecordAudioPermissionRequest {
        var recognizedText by remember { mutableStateOf("Tap the button and speak") }
        val scope = rememberCoroutineScope()

        Column(modifier = Modifier.padding(16.dp)) {

            Button(onClick = {
                scope.launch {
                    recognizedText = recognizeSpeech()
                }
            }) {
                Text("Start Speech Recognition")
            }

            Text(text = recognizedText)
        }
    }
}

suspend fun recognizeSpeech(): String {
    val subscriptionKey = "Speech key"
    val region = "eastasia"

    return try {
        val speechConfig = SpeechConfig.fromSubscription(subscriptionKey, region)
        val recognizer = SpeechRecognizer(speechConfig)

        val result = recognizer.recognizeOnceAsync().get()
        recognizer.close()

        if (result.reason == ResultReason.RecognizedSpeech) {
            Log.i("AzureSpeech", "Recognized: ${result.text}")
            result.text
        } else {
            Log.e("AzureSpeech", "Speech not recognized, reason: ${result.reason}")
            "Speech not recognized."
        }
    } catch (ex: Exception) {
        Log.e("AzureSpeech", "Error: ${ex.message}")
        "Error recognizing speech."
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RecordAudioPermissionRequest(
    onPermissionGranted: @Composable () -> Unit
) {
    val permissionState = rememberPermissionState(android.Manifest.permission.RECORD_AUDIO)

    when {
        permissionState.status.isGranted -> onPermissionGranted()
        permissionState.status.shouldShowRationale || !permissionState.status.isGranted -> {
            Column {
                Text("Microphone permission is needed to use speech recognition.")
                Button(onClick = { permissionState.launchPermissionRequest() }) {
                    Text("Grant permission")
                }
            }
        }
    }
}

