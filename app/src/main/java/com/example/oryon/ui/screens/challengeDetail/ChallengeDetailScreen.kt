package com.example.oryon.ui.screens.challengeDetail

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.oryon.ui.screens.challenge.ChallengeList
import com.example.oryon.ui.screens.challenge.ChallengeViewModel

@Composable
fun ChallengeDetailScreen( challengeId: String, viewModel: ChallengeViewModel) {
    val challenges by viewModel.challenges.collectAsState()
    val challenge = challenges.find { it.id == challengeId }

    Text(challenge?.name ?: "Challenge not found")


}