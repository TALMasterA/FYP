package com.example.fyp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.fyp.core.AudioRecorder
import com.example.fyp.ui.theme.FYPTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FYPTheme { AppNavigation() }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        AudioRecorder.stopIfRecording()
    }
}