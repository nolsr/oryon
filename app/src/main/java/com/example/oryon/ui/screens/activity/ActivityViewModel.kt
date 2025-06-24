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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class ActivityViewModel(private val authRepository: AuthRepository,
                        private val firestoreRepository: FirestoreRepository
): ViewModel() {
    private val _runSessions = MutableStateFlow<List<RunSession>>(emptyList())
    val runSessions: StateFlow<List<RunSession>> = _runSessions

    init {
        val currentUserId = authRepository.getUID()
        if (currentUserId != null) {
            viewModelScope.launch {
                firestoreRepository.getAllRunSessionsForUser(currentUserId)
                    .onEach { sessions ->
                        _runSessions.value = sessions
                    }
                    .launchIn(viewModelScope)
            }
        }
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