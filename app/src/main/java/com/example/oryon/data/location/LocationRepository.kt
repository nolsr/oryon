package com.example.oryon.data.location

import kotlinx.coroutines.flow.Flow
import android.location.Location

interface LocationRepository {
    fun getLocationUpdates(): Flow<Location>
}
