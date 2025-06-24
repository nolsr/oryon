package com.example.oryon.ui.components
/*
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.PermissionController
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.DistanceRecord
import androidx.health.connect.client.records.ExerciseSessionRecord
import androidx.health.connect.client.records.StepsRecord

@Composable
fun HealthPermissionHandler(
    onPermissionGranted: () -> Unit
) {
    val context = LocalContext.current
    val healthConnectClient = remember { HealthConnectClient.getOrCreate(context) }

    val permissions = remember {
        setOf(
            HealthPermission.getWritePermission(ExerciseSessionRecord::class),
            HealthPermission.getWritePermission(DistanceRecord::class),
            HealthPermission.getReadPermission(ExerciseSessionRecord::class),
            HealthPermission.getReadPermission(DistanceRecord::class)
        )
    }

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = PermissionController.createRequestPermissionResultContract()
    ) { granted ->
        if (granted.containsAll(permissions)) {
            onPermissionGranted()
        } else {
            // Optional: Show some UI message about denied permissions
        }
    }

    LaunchedEffect(Unit) {
        val granted = healthConnectClient.permissionController.getGrantedPermissions()
        if (!granted.containsAll(permissions)) {
            requestPermissionLauncher.launch(permissions)
        } else {
            onPermissionGranted()
        }
    }
}

 */
