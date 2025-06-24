package com.example.oryon.ui.screens.home

import android.content.Intent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.core.content.ContextCompat
import com.example.oryon.R
import com.example.oryon.data.location.LocationTrackingService
import com.mapbox.geojson.Point
import com.mapbox.maps.Style
import com.mapbox.maps.dsl.cameraOptions
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.annotation.generated.PointAnnotation
import com.mapbox.maps.extension.compose.annotation.rememberIconImage
import com.mapbox.maps.extension.compose.style.MapStyle



@Composable
fun HomeScreen( viewModel: HomeViewModel ) {

    val context = LocalContext.current
    var hasPermission by remember { mutableStateOf(false) }

    LocationPermissionHandler {
        hasPermission = true
    }

    LaunchedEffect(hasPermission) {
        if (hasPermission) {
            val intent = Intent(context, LocationTrackingService::class.java)
            ContextCompat.startForegroundService(context, intent)
        }
    }

    val location by viewModel.location.collectAsState()

    /*
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        if (!hasPermission) {
            Text("Warte auf Standortberechtigung...")
        } else if (location == null) {
            Text("Standort wird abgerufen...")
        } else {
            Text("Latitude: ${location!!.latitude}")
            Text("Longitude: ${location!!.longitude}")
            Text("Speed: ${location!!.speed} m/s")
            Text("Genauigkeit: ±${location!!.accuracy} m")
        }
    }
    */


    val viewportState = rememberMapViewportState()

    val markerIcon = rememberIconImage(
        key = "marker",
        painter = painterResource(R.drawable.location)
    )

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


    MapboxMap(
        modifier = Modifier.fillMaxSize(),
        mapViewportState = viewportState,
        style = { MapStyle(style = Style.DARK) },
    ) {
        if (hasPermission && location != null) {
            PointAnnotation(point = Point.fromLngLat(location!!.longitude, location!!.latitude)) {
                iconImage = markerIcon
                iconSize = 2.0
            }
        }
    }


}