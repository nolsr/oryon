package com.example.oryon.ui.screens.home

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.oryon.R
import com.example.oryon.data.location.LocationTrackingService
import com.example.oryon.ui.components.LocationPermissionHandler
import com.example.oryon.ui.theme.FiraSansFontFamily
import com.mapbox.geojson.Point
import com.mapbox.maps.Style
import com.mapbox.maps.dsl.cameraOptions
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.annotation.generated.PointAnnotation
import com.mapbox.maps.extension.compose.annotation.generated.PolylineAnnotation
import com.mapbox.maps.extension.compose.annotation.rememberIconImage
import com.mapbox.maps.extension.compose.style.MapStyle




@Composable
fun HomeScreen( viewModel: HomeViewModel ) {


    /* Debugging

    if (liveLocation != null) {
                Text("Live-Position: ${liveLocation!!.latitude}, ${liveLocation!!.longitude}")
            }

            Button(onClick = {
                if (isTracking) {
                    viewModel.stopTracking()
                    context.stopService(Intent(context, LocationTrackingService::class.java))
                } else {
                    ContextCompat.startForegroundService(
                        context,
                        Intent(context, LocationTrackingService::class.java)
                    )
                    viewModel.startTracking()
                }
                isTracking = !isTracking
            }) {
                Text(if (isTracking) "Stop Tracking" else "Start Tracking")
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (currentLocation != null) {
                Text("Aktuelle Position: ${currentLocation!!.latitude}, ${currentLocation!!.longitude}")
                Text("Distanz: ${"%.2f".format(distance / 1000)} km")
                Text("Punkte: ${route.size}")
            } else {
                Text("Warte auf Standortdaten...")
            }


     */


    val context = LocalContext.current
    var hasPermission by remember { mutableStateOf(false) }
    var healthPermissionGranted by remember { mutableStateOf(false) }


    // Location permissions (fehlt hier, bitte dein LocationPermissionHandler einbinden)
    LocationPermissionHandler {
        hasPermission = true
    }

    //HealthPermissionHandler {
    //    healthPermissionGranted = true
    //}

    LaunchedEffect(hasPermission) {
        if (hasPermission) {
            val intent = Intent(context, LocationTrackingService::class.java)
            ContextCompat.startForegroundService(context, intent)
        }
    }

    // States aus ViewModel
    val location by viewModel.currentLocation.collectAsState()
    val routePoints by viewModel.routePoints.collectAsState()
    val distance by viewModel.distanceMeters.collectAsState()
    val elapsedTime by viewModel.elapsedTimeSeconds.collectAsState()
    val pace by viewModel.paceMinutesPerKm.collectAsState(initial = null)
    val isTracking by viewModel.isTracking.collectAsState(initial = false)
    val isPaused by viewModel.isPaused.collectAsState(initial = false)

    val viewportState = rememberMapViewportState()
    val markerIcon = rememberIconImage(key = "marker", painter = painterResource(R.drawable.location))

    LaunchedEffect(location) {
        if (hasPermission && location != null) {
            viewportState.flyTo(
                cameraOptions {
                    center(Point.fromLngLat(location!!.longitude, location!!.latitude))
                    zoom(15.5)
                }
            )
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {

        if (isTracking) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "%.2f km".format(distance / 1000),
                    style = MaterialTheme.typography.headlineMedium
                )
                Spacer(modifier = Modifier.padding(4.dp))
                Text(
                    text = "%02d:%02d min".format(elapsedTime / 60, elapsedTime % 60),
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = pace?.let { "%.2f min/km".format(it) } ?: "--",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        Box(modifier = Modifier.fillMaxSize()){
            // Karte
            MapboxMap(
                modifier = Modifier.fillMaxSize(),
                mapViewportState = viewportState,
                style = { MapStyle(style = Style.DARK) }
            ) {
                // Nutzer-Position
                if (location != null) {
                    PointAnnotation(point = Point.fromLngLat(location!!.longitude, location!!.latitude)) {
                        iconImage = markerIcon
                        iconSize = 2.0
                    }
                }

                // Route zeichnen
                if (routePoints.size > 1) {
                    PolylineAnnotation(
                        points = routePoints.map { Point.fromLngLat(it.longitude, it.latitude) }
                    ) {
                        lineColor = Color(0xFFFF6F00)
                        lineWidth = 5.0
                    }
                }
            }

            if (!isTracking) {
                Button(
                    onClick = { viewModel.startTracking() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .align(Alignment.BottomEnd)
                        .height(75.dp),
                ) {
                    Text("Start Tracking",
                        fontFamily = FiraSansFontFamily,
                        fontWeight = FontWeight.Bold)
                }
            }

            if (isTracking){
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .align(Alignment.BottomEnd),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (!isPaused) {
                        Button(
                            onClick = { viewModel.pauseTracking() },
                            modifier = Modifier.weight(1f).height(75.dp)
                        ) { Text("Pause") }
                    } else {
                        Button(
                            onClick = { viewModel.resumeTracking() },
                            modifier = Modifier.weight(1f).height(75.dp)
                        ) { Text("Weiter") }
                    }

                    Button(
                        onClick = { viewModel.stopTracking() },
                        modifier = Modifier.weight(1f).height(75.dp)
                    ) { Text("Stop") }
                }
            }
        }

    }

}