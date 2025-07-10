package com.example.oryon.ui.screens.challenge

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.oryon.data.ChallengeData
import com.example.oryon.data.ChallengeGoal
import com.example.oryon.data.ChallengeParticipant
import com.example.oryon.data.ParticipantRanking
import com.example.oryon.data.firebase.AuthRepository
import com.example.oryon.data.firebase.FirestoreRepository
import com.example.oryon.data.location.LocationRepository
import com.example.oryon.domain.TrackRunUseCase
import com.example.oryon.ui.screens.home.HomeViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class ChallengeViewModel(
    private val firestoreRepository: FirestoreRepository,
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _challenges = MutableStateFlow<List<ChallengeData>>(emptyList())
    val challenges: StateFlow<List<ChallengeData>> = _challenges

    private val _selectedChallenge = MutableStateFlow<ChallengeData?>(null)
    val selectedChallenge: StateFlow<ChallengeData?> = _selectedChallenge

    private val _userProgress = MutableStateFlow<Float?>(null)
    val userProgress: StateFlow<Float?> = _userProgress

    private val _currentUserId = authRepository.getUID()
    fun getCurrentUserId() = _currentUserId


    // Initialisiere Challenges
    init {
        viewModelScope.launch {
            firestoreRepository.getUserChallenges()
                .catch { e -> Log.e("ChallengeViewModel", "Fehler beim Laden der Challenges", e) }
                .collect { result -> _challenges.value = result }
        }
    }

    // neue Challenge erstellen
    fun addChallenge(name: String, type: String, target: Float) {
        viewModelScope.launch {
            val uid = authRepository.getUID() ?: return@launch
            try {
                firestoreRepository.addChallenge(name, type, target, uid)
            } catch (e: Exception) {
            }
        }
    }

    // Challenge mit ID suchen und in viewModel in selectedChallenge speichern
    fun selectChallengeById(challengeId: String) {
        val challenge = _challenges.value.find { it.id == challengeId }
        _selectedChallenge.value = challenge

        println("Selected challenge: $challenge $challenges $challengeId")

        if (challenge != null) {
            val goal = parseGoal(challenge.type, challenge.data)
            val currentUserId = authRepository.getUID()
            val participant = challenge.participants.find { it.uid == currentUserId }

            val progress = if (goal != null && participant != null) {
                getProgressPercentage(participant, goal)
            } else null

            _userProgress.value = progress
        } else {
            _userProgress.value = null
        }
    }

    // Schaut um welchen Challenge Typ es sich handelt
    fun parseGoal(type: String, data: Map<String, Any>): ChallengeGoal? {
        return when (type) {
            "distance" -> ChallengeGoal.Distance((data["target"] as? Number)?.toFloat() ?: return null)
            "duration" -> ChallengeGoal.Duration((data["target"] as? Number)?.toInt() ?: return null)
            "runcount" -> ChallengeGoal.RunCount((data["target"] as? Number)?.toInt() ?: return null)
            "days" -> ChallengeGoal.Days((data["target"] as? Number)?.toInt() ?: return null)
            else -> null
        }
    }

    // Berechnet Fortschritt des Nutzers
    fun getProgressPercentage(participant: ChallengeParticipant, goal: ChallengeGoal): Float {
        return when (goal) {
            is ChallengeGoal.Distance -> (participant.progress / goal.targetKm).coerceIn(0f, 1f)
            is ChallengeGoal.Duration -> (participant.progress / goal.targetMinutes).coerceIn(0f, 1f)
            is ChallengeGoal.RunCount -> (participant.progress / goal.targetRuns).coerceIn(0f, 1f)
            is ChallengeGoal.Days -> (participant.progress / goal.targetDays).coerceIn(0f, 1f)
        }
    }

    // Sortiert Nutzer nach Fortschritt inerhalb der Challenge
    // und speichert die Daten in ParticipantRanking aus dem Model
    fun getCurrentChallengeRanking(): List<ParticipantRanking> {
        val challenge = _selectedChallenge.value ?: return emptyList()
        println("Current challenge: $challenge")
        return challenge.participants
            .sortedByDescending { it.progress }
            .map {
                ParticipantRanking(
                    name = it.name ?: "Unbekannt",
                    progress = it.progress
                )
            }
    }

    // Fügt Nutzer zur Challenge hinzu
    //Ruft die Methode aus FirestoreRepository auf
    fun addParticipantByEmail(challengeId: String, email: String) {
        viewModelScope.launch {
            val user = firestoreRepository.findUserByEmail(email)
            if (user != null) {
                firestoreRepository.addUserToChallenge(challengeId, user.id)
            } else {
            }
        }
    }

}

//Juhu eine weitere Factory für das ViewModel
class ChallengeViewModelFactory(
    private val firestoreRepository: FirestoreRepository,
    private val authRepository: AuthRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ChallengeViewModel(firestoreRepository, authRepository) as T
    }
}