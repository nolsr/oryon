package com.example.oryon.ui.components

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import android.content.pm.PackageManager

@Composable
fun LocationPermissionHandler(
    onAllPermissionsGranted: () -> Unit
) {
    val context = LocalContext.current

    // State für Positions permissions
    var fineGranted by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    // State für Background Position permissions
    var backgroundGranted by remember {
        mutableStateOf(
            Build.VERSION.SDK_INT < Build.VERSION_CODES.Q || ContextCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    // Launcher für permissions
    val finePermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        fineGranted = granted
    }

    val backgroundPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        backgroundGranted = granted || Build.VERSION.SDK_INT < Build.VERSION_CODES.Q
    }

    // Startet die permissionslaucher
    LaunchedEffect(Unit) {
        if (!fineGranted) {
            finePermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    LaunchedEffect(fineGranted) {
        if (fineGranted && !backgroundGranted && Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            backgroundPermissionLauncher.launch(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        }
    }

    LaunchedEffect(fineGranted, backgroundGranted) {
        if (fineGranted && backgroundGranted) {
            onAllPermissionsGranted()
        }
    }
}

