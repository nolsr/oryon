package com.example.oryon.ui.screens.challengeDetail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.oryon.data.ChallengeData
import com.example.oryon.ui.screens.challenge.ChallengeViewModel

@Composable
fun ChallengeDetailScreen( challengeId: String, viewModel: ChallengeViewModel) {
    val challenges by viewModel.challenges.collectAsState()
    val challenge = challenges.find { it.id == challengeId }

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
            0 -> ChallengeDetailTab(challenge)
            1 -> MemberTab(challenge)
        }
    }
}

@Composable
fun ChallengeDetailTab(challenge: ChallengeData?) {
    Text(challenge?.name ?: "Kein Challenge gefunden")
}

@Composable
fun MemberTab(challenge: ChallengeData?) {
    Text(
        text = challenge?.participants?.joinToString(", ") { it.name ?: it.uid }
            ?: "Keine Challenge gefunden"
    )}





