package com.example.fyp

import androidx.activity.ComponentActivity
import dagger.hilt.android.AndroidEntryPoint

/**
 * An empty activity annotated with @AndroidEntryPoint so that Hilt can inject into it.
 * Used as the host activity for Compose UI tests that run in a Hilt-enabled project.
 */
@AndroidEntryPoint
class HiltTestActivity : ComponentActivity()
