package com.example.oryon.ui.screens.challengeDetail

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.oryon.R
import com.example.oryon.data.ChallengeData
import com.example.oryon.data.ChallengeGoal
import com.example.oryon.data.ChallengeParticipant
import com.example.oryon.data.ParticipantRanking
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
    val ranking = remember(challenge) { viewModel.getCurrentChallengeRanking() }
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
            0 -> ChallengeDetailTab(challenge, currentUser, progress, ranking)
            1 -> MemberTab(ranking)
        }
    }
}

@Composable
fun ChallengeDetailTab(challenge: ChallengeData?, currentUser:ChallengeParticipant?, progress: Float?, ranking: List<ParticipantRanking>) {
    val progressKm = currentUser?.progress ?: 0f
    val targetKm = (challenge?.goal as? ChallengeGoal.Distance)?.targetKm ?: 0f

    Column {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))

        )
        {
            Image(
                painter = painterResource(R.drawable.challenge_hero_1),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.matchParentSize()
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.2f))
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (challenge != null) {
                    Text(
                        text = challenge.name,
                        style = MaterialTheme.typography.displayMedium.copy(color = Color.White)
                    )
                }
                if (challenge != null) {
                    Text(
                        text = challenge.type,
                        style = MaterialTheme.typography.bodyMedium.copy(color = Color.White)
                    )
                }
            }
        }

        Column(modifier = Modifier.padding(16.dp)) {
            if (progress != null && challenge != null) {
                TextCard((progress * 100), progressKm.toInt(), targetKm.toInt())
            }

        }
    }
}

@Composable
fun MemberTab(ranking: List<ParticipantRanking>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        ranking.forEachIndexed { index, participant ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("${index + 1}.", style = MaterialTheme.typography.displayMedium)
                    Image(
                        painter = painterResource(R.drawable.avatar),
                        contentDescription = "Avatar",
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .size(45.dp)
                    )
                    Text(participant.name, style = MaterialTheme.typography.titleMedium)
                }

                Text("${participant.progress} km", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}



@Composable
fun TextCard(prozent: Float, progress: Int, goal: Int){
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(4.dp),
        modifier = Modifier
            .fillMaxWidth()
    ){
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ChallengeProgressIndicator(prozent)
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "${progress}KM",
                    style = MaterialTheme.typography.displayMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "von ${goal}KM",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "${prozent.toInt()}%",
                    style = MaterialTheme.typography.displayMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "geschafft",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }

}

@Composable
fun ChallengeProgressIndicator(progress: Float) {
    Box(contentAlignment = Alignment.Center) {
        CircularProgressIndicator(
            progress = { progress / 100 },
            strokeWidth = 10.dp,
            modifier = Modifier.size(90.dp),
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "${(progress).toInt()}%",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}



