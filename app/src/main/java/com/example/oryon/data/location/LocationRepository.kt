package com.example.oryon.data.location

import com.example.oryon.domain.model.LocationData
import kotlinx.coroutines.flow.Flow

interface LocationRepository {
    fun observeLocation(): Flow<LocationData>
}