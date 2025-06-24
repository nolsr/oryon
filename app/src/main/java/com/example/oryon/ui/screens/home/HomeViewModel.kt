package com.example.oryon.ui.screens.home

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.oryon.data.location.LocationRepository
import com.example.oryon.data.location.LocationRepositoryImpl
import com.example.oryon.domain.location.TrackRunUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class HomeViewModel(
    private val locationRepository: LocationRepository,
    private val trackRunUseCase: TrackRunUseCase
): ViewModel() {

    private val _location = MutableStateFlow<Location?>(null)
    val location: StateFlow<Location?> = _location.asStateFlow()

    private val _currentLocation = MutableStateFlow<Location?>(null)
    val currentLocation: StateFlow<Location?> = _currentLocation.asStateFlow()

    val elapsedTimeSeconds = trackRunUseCase.elapsedTimeSeconds
    val distanceMeters = trackRunUseCase.distanceMeters
    val paceMinutesPerKm = trackRunUseCase.paceMinutesPerKm
    val routePoints = trackRunUseCase.routePoints

    private val _isTracking = MutableStateFlow(false)
    val isTracking: StateFlow<Boolean> = _isTracking

    private val _isPaused = MutableStateFlow(false)
    val isPaused: StateFlow<Boolean> = _isPaused

    init {
        viewModelScope.launch {
            locationRepository.getLocationUpdates()
                .firstOrNull()?.let {
                    _currentLocation.value = it
                }
        }
    }

    fun startTracking() {
        if (_isTracking.value) return
        _isTracking.value = true
        _isPaused.value = false
        trackRunUseCase.startTracking()
    }

    fun pauseTracking() {
        _isPaused.value = true
        trackRunUseCase.pauseTracking()
    }

    fun resumeTracking() {
        _isPaused.value = false
        trackRunUseCase.resumeTracking()
    }

    fun stopTracking() {
        _isTracking.value = false
        _isPaused.value = false
        trackRunUseCase.stopTracking()
    }


}

class HomeViewModelFactory(
    private val locationRepository: LocationRepository,
    private val trackRunUseCase: TrackRunUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HomeViewModel(locationRepository, trackRunUseCase) as T
    }
}
