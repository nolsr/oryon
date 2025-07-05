package com.example.oryon.ui.screens.activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.oryon.data.RunSession
import com.example.oryon.data.firebase.AuthRepository
import com.example.oryon.data.firebase.FirestoreRepository
import com.example.oryon.data.location.LocationRepository
import com.example.oryon.domain.TrackRunUseCase
import com.example.oryon.ui.screens.home.HomeViewModel
import com.google.firebase.Timestamp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

class ActivityViewModel(
    private val authRepository: AuthRepository,
    private val firestoreRepository: FirestoreRepository
) : ViewModel() {

    private val _runSessions = MutableStateFlow<List<RunSession>>(emptyList())
    val runSessions: StateFlow<List<RunSession>> = _runSessions.asStateFlow()

    val runSessionsThisWeek: StateFlow<List<RunSession>> =
        runSessions.map { sessions ->
            sessions.filter { isThisWeek(it.date) }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val distanceByDay: StateFlow<Map<String, Float>> =
        runSessionsThisWeek
            .map { sessions ->
                sessions.groupBy {
                    it.date.toDate().toInstant()
                        .atZone(ZoneId.systemDefault())
                        .dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.GERMAN)
                        .trimEnd('.')
                }.mapValues { (_, daySessions) ->
                    daySessions.sumOf { it.distanceMeters.toDouble() }.toFloat() / 1000f
                }
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyMap()
            )

    init {
        val currentUserId = authRepository.getUID()
        if (currentUserId != null) {
            viewModelScope.launch {
                firestoreRepository.getAllRunSessionsForUser(currentUserId)
                    .onEach { sessions ->
                        println("Sessions IDs: ${sessions.map { it.id }}")
                        _runSessions.value = sessions
                    }
                    .launchIn(viewModelScope)
            }
        }
    }

    private fun isThisWeek(timestamp: Timestamp): Boolean {
        val now = LocalDate.now()
        val startOfWeek = now.with(DayOfWeek.MONDAY)
        val endOfWeek = now.with(DayOfWeek.SUNDAY)

        val date = timestamp.toDate().toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalDate()

        return !date.isBefore(startOfWeek) && !date.isAfter(endOfWeek)
    }
}

class ActivityViewModelFactory(
    private val authRepository: AuthRepository,
    private val firestoreRepository: FirestoreRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ActivityViewModel(authRepository, firestoreRepository) as T
    }
}