package com.example.oryon.data.location

import android.content.Context
import android.location.Location
import androidx.core.app.ActivityCompat
import com.example.oryon.domain.model.LocationData
import com.google.android.gms.location.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class LocationRepositoryImpl(
    private val context: Context
) {

    private val fusedClient = LocationServices.getFusedLocationProviderClient(context)


}
