package com.translator.TalknLearn.screens.speech

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.coroutines.resume

/**
 * Dialog to choose image source (camera or gallery)
 */
@Composable
fun ImageSourceDialog(
    onCamera: () -> Unit,
    onGallery: () -> Unit,
    onDismiss: () -> Unit,
    accuracyWarning: String,
    languageHint: String,
    title: String,
    cameraLabel: String,
    galleryLabel: String,
    cancelLabel: String,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        onCamera()
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(cameraLabel)
                }
                
                OutlinedButton(
                    onClick = {
                        onGallery()
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Image,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(galleryLabel)
                }

                // Accuracy warning
                Text(
                    text = accuracyWarning,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 8.dp)
                )

                // Language hint for camera recognition
                Text(
                    text = languageHint,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(cancelLabel)
            }
        }
    )
}

/**
 * Simple camera capture screen using CameraX
 */
@Composable
fun CameraCaptureScreen(
    onImageCaptured: (Uri) -> Unit,
    onError: (Exception) -> Unit,
    onCancel: () -> Unit,
    captureContentDesc: String,
    cancelLabel: String
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var cameraProvider by remember { mutableStateOf<ProcessCameraProvider?>(null) }
    val previewUseCase = remember { Preview.Builder().build() }
    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }
    val previewView = remember {
        PreviewView(context).apply {
            // TextureView-backed preview is less crash-prone on some device/GPU combinations.
            implementationMode = PreviewView.ImplementationMode.COMPATIBLE
        }
    }

    // Setup camera
    LaunchedEffect(lifecycleOwner) {
        val provider = awaitCameraProvider(context)
        cameraProvider = provider

        previewUseCase.setSurfaceProvider(previewView.surfaceProvider)

        imageCapture = ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            .build()
        val activeCapture = imageCapture ?: return@LaunchedEffect

        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

        try {
            provider.unbind(previewUseCase, activeCapture)
            provider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                previewUseCase,
                activeCapture,
            )
        } catch (e: Exception) {
            onError(e)
        }
    }
    
    Box(modifier = Modifier.fillMaxSize()) {
        // Camera preview
        AndroidView(
            factory = { previewView },
            modifier = Modifier.fillMaxSize()
        )
        
        // Control buttons
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Capture button
            FloatingActionButton(
                onClick = {
                    val photoFile = File(
                        context.cacheDir,
                        SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS", Locale.US)
                            .format(System.currentTimeMillis()) + ".jpg"
                    )
                    
                    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
                    
                    imageCapture?.takePicture(
                        outputOptions,
                        ContextCompat.getMainExecutor(context),
                        object : ImageCapture.OnImageSavedCallback {
                            override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                                onImageCaptured(Uri.fromFile(photoFile))
                            }
                            
                            override fun onError(exception: ImageCaptureException) {
                                onError(exception)
                            }
                        }
                    )
                },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = captureContentDesc,
                    modifier = Modifier.size(32.dp)
                )
            }
            
            // Cancel button
            TextButton(onClick = onCancel) {
                Text(cancelLabel, color = MaterialTheme.colorScheme.onSurface)
            }
        }
    }
    
    // Cleanup
    DisposableEffect(Unit) {
        onDispose {
            cameraProvider?.let { provider ->
                imageCapture?.let { capture ->
                    runCatching { provider.unbind(previewUseCase, capture) }
                }
            }
        }
    }
}

private suspend fun awaitCameraProvider(context: android.content.Context): ProcessCameraProvider =
    withContext(Dispatchers.Main.immediate) {
        suspendCancellableCoroutine { cont ->
            val future = ProcessCameraProvider.getInstance(context)
            future.addListener(
                {
                    @Suppress("BlockingMethodInNonBlockingContext")
                    val provider = future.get()
                    if (cont.isActive) cont.resume(provider)
                },
                ContextCompat.getMainExecutor(context),
            )
        }
    }

/**
 * Image picker launcher helper
 */
@Composable
fun rememberImagePickerLauncher(
    onImageSelected: (Uri?) -> Unit
): () -> Unit {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        onImageSelected(uri)
    }
    
    return {
        launcher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }
}
