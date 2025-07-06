package com.example.oryon.ui.screens.challenge

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.oryon.data.ChallengeData
import com.example.oryon.ui.screens.activity.ActivityViewModel

@Composable
fun ChallengeScreen( viewModel: ChallengeViewModel) {
    val challenges by viewModel.challenges.collectAsState()

    ChallengeList(challenges)
}

@Composable
fun ChallengeList(challenges: List<ChallengeData>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(challenges) { challenge ->
            ChallengeCard(challenge)
        }
    }
}

@Composable
fun ChallengeCard(challenge: ChallengeData) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = challenge.name, style = MaterialTheme.typography.titleLarge)
            Text(text = "Typ: ${challenge.type}", style = MaterialTheme.typography.bodyMedium)

            Spacer(modifier = Modifier.height(8.dp))

            Text(text = "Teilnehmer:", style = MaterialTheme.typography.bodySmall)
            challenge.participants.forEach { participant ->
                Text(
                    text = "- ${participant.name ?: participant.uid}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

