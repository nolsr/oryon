package com.example.oryon.data.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.Flow


class LocationRepositoryImpl() : LocationRepository {

    override fun getLocationUpdates(): Flow<Location> = LocationTrackingService.locationUpdatesFlow

    /*
    private val fusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission")
    override fun getLocationUpdates(): Flow<Location> = callbackFlow {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            close(SecurityException("Location permission not granted"))
            return@callbackFlow
        }

        val locationRequest = LocationRequest.create().apply {
            interval = 5000
            fastestInterval = 2000
            priority = Priority.PRIORITY_HIGH_ACCURACY
        }

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.lastLocation?.let {
                    println("LOCATION: ${it.latitude}, ${it.longitude}")
                    trySend(it).isSuccess
                }
            }
        }

        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )

        awaitClose {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        }
    }
    */
}

