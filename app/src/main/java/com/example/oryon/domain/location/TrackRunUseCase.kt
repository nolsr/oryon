package com.example.oryon.domain.location

import com.example.oryon.data.location.LocationRepository
import android.location.Location
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.*


class TrackRunUseCase(private val locationRepository: LocationRepository) {

    private val _routePoints = MutableStateFlow<List<Location>>(emptyList())
    val routePoints: StateFlow<List<Location>> = _routePoints.asStateFlow()

    private val _elapsedTimeSeconds = MutableStateFlow(0L)
    val elapsedTimeSeconds: StateFlow<Long> = _elapsedTimeSeconds.asStateFlow()

    private val _distanceMeters = MutableStateFlow(0f)
    val distanceMeters: StateFlow<Float> = _distanceMeters.asStateFlow()

    private var isPaused = false
    private var trackingJob: Job? = null
    private var timerJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Default)

    fun startTracking(): Flow<Location> {
        startTimer()
        trackingJob = locationRepository.getLocationUpdates()
            .onEach { location ->
                if (!isPaused) {
                    val updatedList = _routePoints.value.toMutableList()
                    updatedList.add(location)
                    _routePoints.value = updatedList
                    _distanceMeters.value = calculateDistance(updatedList)
                }
            }
            .launchIn(scope)

        return locationRepository.getLocationUpdates()
    }

    private fun startTimer() {
        timerJob = scope.launch {
            while (true) {
                delay(1000L)
                if (!isPaused) {
                    _elapsedTimeSeconds.value += 1
                }
            }
        }
    }

    fun pauseTracking() {
        isPaused = true
    }

    fun resumeTracking() {
        isPaused = false
    }

    fun stopTracking() {
        trackingJob?.cancel()
        timerJob?.cancel()
        _routePoints.value = emptyList()
        _distanceMeters.value = 0f
        _elapsedTimeSeconds.value = 0
    }

    private fun calculateDistance(route: List<Location>): Float {
        var distance = 0f
        for (i in 1 until route.size) {
            distance += route[i].distanceTo(route[i - 1])
        }
        return distance
    }

    val paceMinutesPerKm: Flow<Float?> = combine(
        _elapsedTimeSeconds,
        _distanceMeters
    ) { timeSec, distMeters ->
        if (distMeters >= 10) {
            (timeSec / 60f) / (distMeters / 1000f)
        } else null
    }.stateIn(scope, SharingStarted.Lazily, null)
}
