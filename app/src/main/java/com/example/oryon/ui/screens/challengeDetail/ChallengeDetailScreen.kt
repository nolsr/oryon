package com.example.oryon.ui.screens.challengeDetail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.oryon.data.ChallengeData
import com.example.oryon.data.ChallengeGoal
import com.example.oryon.data.ChallengeParticipant
import com.example.oryon.ui.screens.challenge.ChallengeViewModel

@Composable
fun ChallengeDetailScreen( challengeId: String, viewModel: ChallengeViewModel) {
    val challenges by viewModel.challenges.collectAsState()

    LaunchedEffect(challenges, challengeId) {
        if (challenges.isNotEmpty()) {
            viewModel.selectChallengeById(challengeId)
        }
    }
    val challenge by viewModel.selectedChallenge.collectAsState()
    val currentUser = challenge?.participants?.find { it.uid == viewModel.getCurrentUserId() }
    val progress by viewModel.userProgress.collectAsState()

    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabTitles = listOf("Challenge", "Mitglieder")

    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        TabRow(selectedTabIndex = selectedTabIndex) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = { Text(title) }
                )
            }
        }

        when (selectedTabIndex) {
            0 -> ChallengeDetailTab(challenge, currentUser, progress)
            1 -> MemberTab(challenge)
        }
    }
}

@Composable
fun ChallengeDetailTab(challenge: ChallengeData?, currentUser:ChallengeParticipant?, progress: Float?) {
    val progressKm = currentUser?.progress ?: 0f
    val targetKm = (challenge?.goal as? ChallengeGoal.Distance)?.targetKm ?: 0f

    Column {
        Text(challenge?.name ?: "Kein Challenge gefunden")
        Text("Challenge Ziel: ${targetKm.toInt()} KM")
        Text("Du bist dafür : ${progressKm} KM gelaufen")

        Text(
            text = if (progress != null) {
                "${(progress * 100).toInt()} %"
            } else {
                "Fehler beim Laden des Fortschritts"
            }
        )
    }
}

@Composable
fun MemberTab(challenge: ChallengeData?) {
    Text(
        text = challenge?.participants?.joinToString(", ") { it.name ?: it.uid }
            ?: "Keine Challenge gefunden"
    )}





