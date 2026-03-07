package com.example.fyp.core

import android.Manifest
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.example.fyp.model.ui.UiTextKey
import com.example.fyp.model.ui.BaseUiTexts
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RequestCameraPermission(
    onPermissionGranted: () -> Unit,
    onPermissionDenied: () -> Unit
) {
    val cameraPermissionState = rememberPermissionState(
        permission = Manifest.permission.CAMERA
    )

    LaunchedEffect(cameraPermissionState.status) {
        if (cameraPermissionState.status.isGranted) {
            onPermissionGranted()
        }
    }

    when {
        cameraPermissionState.status.isGranted -> {
            // Permission granted, callback will be invoked via LaunchedEffect
        }
        cameraPermissionState.status.shouldShowRationale -> {
            // Show rationale dialog
            CameraPermissionRationaleDialog(
                onRequestPermission = { cameraPermissionState.launchPermissionRequest() },
                onDismiss = onPermissionDenied
            )
        }
        else -> {
            // First time, request permission
            LaunchedEffect(Unit) {
                cameraPermissionState.launchPermissionRequest()
            }
        }
    }
}

@Composable
private fun CameraPermissionRationaleDialog(
    onRequestPermission: () -> Unit,
    onDismiss: () -> Unit
) {
    val appLanguageState = LocalAppLanguageState.current
    val (uiText) = rememberUiTextFunctions(appLanguageState)
    val t: (UiTextKey) -> String = { key -> uiText(key, BaseUiTexts[key.ordinal]) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(t(UiTextKey.CameraPermissionTitle)) },
        text = { Text(t(UiTextKey.CameraPermissionMessage)) },
        confirmButton = {
            Button(onClick = onRequestPermission) {
                Text(t(UiTextKey.CameraPermissionGrant))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(t(UiTextKey.ActionCancel))
            }
        }
    )
}
