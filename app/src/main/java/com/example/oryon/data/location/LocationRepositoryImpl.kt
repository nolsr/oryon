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

    //Ruft lediglich die LocationUpdate Func des Services auf
    override fun getLocationUpdates(): Flow<Location> = LocationTrackingService.locationUpdatesFlow

}

