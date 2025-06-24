package com.example.oryon.domain.location

import com.example.oryon.data.location.LocationRepository
import android.location.Location
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach

class TrackRunUseCase(private val locationRepository: LocationRepository) {

    private val _routePoints = mutableListOf<Location>()
    val routePoints: List<Location> get() = _routePoints

    fun startTracking(): Flow<Location> = locationRepository.getLocationUpdates()
        .onEach { location ->
            _routePoints.add(location)
        }

    fun getDistanceMeters(): Float {
        var distance = 0f
        for (i in 1 until _routePoints.size) {
            distance += _routePoints[i].distanceTo(_routePoints[i-1])
        }
        return distance
    }

    fun stopTracking() {
        _routePoints.clear()
    }
}
