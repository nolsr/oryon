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
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class HomeViewModel(private val locationRepository: LocationRepository): ViewModel() {

    private val _location = MutableStateFlow<Location?>(null)
    val location: StateFlow<Location?> = _location.asStateFlow()

    init {
        viewModelScope.launch {
            locationRepository.getLocationUpdates()
                .catch { it.printStackTrace() }
                .collect { _location.value = it }
        }
    }


    /*
    private val _currentLocation = MutableStateFlow<Location?>(null)
    val currentLocation: StateFlow<Location?> = _currentLocation

    private val _routePoints = MutableStateFlow<List<Location>>(emptyList())
    val routePoints: StateFlow<List<Location>> = _routePoints

    private val _distanceMeters = MutableStateFlow(0f)
    val distanceMeters: StateFlow<Float> = _distanceMeters

    private var trackingJob: Job? = null

    fun startTracking() {
        trackingJob = trackRunUseCase.startTracking()
            .onEach { location ->
                _currentLocation.value = location
                _routePoints.value = trackRunUseCase.routePoints.toList()
                _distanceMeters.value = trackRunUseCase.getDistanceMeters()
            }
            .launchIn(viewModelScope)
    }

    fun stopTracking() {
        trackingJob?.cancel()
        trackRunUseCase.stopTracking()
        _routePoints.value = emptyList()
        _distanceMeters.value = 0f
        _currentLocation.value = null
    }
    */
}

class HomeViewModelFactory(
    private val locationRepository: LocationRepository,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HomeViewModel(locationRepository) as T
    }
}