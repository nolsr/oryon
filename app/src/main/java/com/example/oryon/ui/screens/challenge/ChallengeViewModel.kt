package com.example.oryon.ui.screens.challenge

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.oryon.data.ChallengeData
import com.example.oryon.data.firebase.FirestoreRepository
import com.example.oryon.data.location.LocationRepository
import com.example.oryon.domain.TrackRunUseCase
import com.example.oryon.ui.screens.home.HomeViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class ChallengeViewModel(private val firestoreRepository: FirestoreRepository) : ViewModel() {
    private val _challenges = MutableStateFlow<List<ChallengeData>>(emptyList())
    val challenges: StateFlow<List<ChallengeData>> = _challenges

    init {
        viewModelScope.launch {
            firestoreRepository.getUserChallenges()
                .catch { e ->
                    Log.e("ChallengeViewModel", "Fehler beim Laden der Challenges", e)
                }
                .collect { result ->
                    _challenges.value = result
                }
        }
    }


}

class ChallengeViewModelFactory(
    private val firestoreRepository: FirestoreRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ChallengeViewModel(firestoreRepository) as T
    }
}