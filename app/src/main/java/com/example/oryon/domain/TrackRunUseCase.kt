package com.example.oryon.domain

import com.example.oryon.data.location.LocationRepository
import android.location.Location
import android.util.Log
import com.example.oryon.data.firebase.FirestoreRepository
import com.example.oryon.data.health.HealthRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.*


class TrackRunUseCase(private val locationRepository: LocationRepository, private val firestoreRepository: FirestoreRepository) {

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
    private var startTimeMillis: Long = 0L
    private var endTimeMillis: Long = 0L

    fun startTracking(): Flow<Location> {
        startTimeMillis = System.currentTimeMillis()
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
        val distance = _distanceMeters.value
        val duration = _elapsedTimeSeconds.value

        val pace = if (distance > 0f) {
            (duration / 60f) / (distance / 1000f)
        } else {
            0f
        }

        println("Tracking gestoppt: $distance Meter, $duration Sekunden")

        scope.launch {
            try {
                firestoreRepository.saveRunSession(
                    distanceMeters = distance,
                    durationSec = duration,
                    pace = pace
                )

                firestoreRepository.updateChallengeProgressAfterRun(
                    distanceMeters = distance,
                    durationSec = duration
                )

                Log.i("Tracking", "Lauf gespeichert und Fortschritt aktualisiert")
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("Firestore", "Fehler beim Aktualisieren der Challenge", e)
            }
        }

        trackingJob?.cancel()
        timerJob?.cancel()
        _routePoints.value = emptyList()
        _distanceMeters.value = 0f
        _elapsedTimeSeconds.value = 0
    }

    private fun calculateDistance(points: List<Location>): Float {
        var distance = 0f
        for (i in 1 until points.size) {
            distance += points[i].distanceTo(points[i - 1])
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
