package com.example.oryon.domain

import com.example.oryon.data.location.LocationRepository
import android.location.Location
import android.util.Log
import com.example.oryon.data.firebase.FirestoreRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.*

//Use Case für die Tracking Funktion fürs Laufen der App
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

    //startet die Tracking Funktion und Timer
    //Speichert den Fluss an neuen Positionen in _routePoints Liste
    //Berechnet die Gesamtdistanz
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

    //Startet den Timer
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

    //Stopt das Traking und speichert die Daten in Firestore
    fun stopTracking() {
        val distance = _distanceMeters.value
        val duration = _elapsedTimeSeconds.value
        val routePoints = _routePoints.value

        //Berechnet die Pace
        val pace = if (distance > 0f) {
            (duration / 60f) / (distance / 1000f)
        } else {
            0f
        }

        println("Tracking gestoppt: $distance Meter, $duration Sekunden")

        //Speichert die Daten in Firestore
        scope.launch {
            try {
                firestoreRepository.saveRunSession(
                    distanceMeters = distance,
                    durationSec = duration,
                    pace = pace,
                    routePoints = routePoints
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

        //Stoppt den Timer und tracking Job
        //Setzt die Werte zurück
        trackingJob?.cancel()
        timerJob?.cancel()
        _routePoints.value = emptyList()
        _distanceMeters.value = 0f
        _elapsedTimeSeconds.value = 0
    }

    // Berechnet die Gesamtdistanz anhand der Liste von Positionen
    private fun calculateDistance(points: List<Location>): Float {
        var distance = 0f
        for (i in 1 until points.size) {
            //nutzt Androids loaction Klasse um die Distanz zwischen den Positionen zu berechnen
            //Distanz dabei in Meter
            distance += points[i].distanceTo(points[i - 1])
        }
        return distance
    }

    //Berechnet die Pace in Minuten pro Kilometer für das viewModel
    val paceMinutesPerKm: Flow<Float?> = combine(
        _elapsedTimeSeconds,
        _distanceMeters
    ) { timeSec, distMeters ->
        if (distMeters >= 10) {
            (timeSec / 60f) / (distMeters / 1000f)
        } else null
    }.stateIn(scope, SharingStarted.Lazily, null)
}
