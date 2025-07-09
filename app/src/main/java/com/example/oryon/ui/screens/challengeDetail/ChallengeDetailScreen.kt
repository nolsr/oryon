package com.example.oryon.ui.screens.challengeDetail

import android.annotation.SuppressLint
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.oryon.R
import com.example.oryon.data.ChallengeData
import com.example.oryon.data.ChallengeGoal
import com.example.oryon.data.ChallengeParticipant
import com.example.oryon.data.ParticipantRanking
import com.example.oryon.data.getChallengeTypeText
import com.example.oryon.data.getUnitLabel
import com.example.oryon.data.getUnitShort
import com.example.oryon.ui.screens.challenge.ChallengeViewModel

@Composable
fun ChallengeDetailScreen( challengeId: String, viewModel: ChallengeViewModel) {
    val challenges by viewModel.challenges.collectAsState()

    //Einzelene Challenge mit ID suchen und in viewModel speichern
    LaunchedEffect(challenges, challengeId) {
        if (challenges.isNotEmpty()) {
            viewModel.selectChallengeById(challengeId)
        }
    }

    //Challenge relevante Daten
    val challenge by viewModel.selectedChallenge.collectAsState()
    val currentUser = challenge?.participants?.find { it.uid == viewModel.getCurrentUserId() }
    val ranking = remember(challenge) { viewModel.getCurrentChallengeRanking() }
    val progress by viewModel.userProgress.collectAsState()

    //State für den Tab
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
            1 -> MemberTab(ranking, challenge, viewModel)
        }
    }
}

@Composable
fun ChallengeDetailTab(challenge: ChallengeData?, currentUser:ChallengeParticipant?, progress: Float?, ranking: List<ParticipantRanking>) {
    val progressKm = currentUser?.progress ?: 0f
    val targetKm = (challenge?.goal as? ChallengeGoal.Distance)?.targetKm ?: 0f
    val unit = challenge?.goal?.getUnitLabel(progressKm)
    val shortUnit = challenge?.goal?.getUnitShort()

    LazyColumn {

        item {
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
                            text = getChallengeTypeText(challenge.type),
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color.White)
                        )
                    }
                }
            }
        }

        item {


            Column(modifier = Modifier.padding(16.dp)) {
                Text("Deine Challenge-Statistik:")
                Spacer(modifier = Modifier.height(12.dp))
                if (progress != null && challenge != null && shortUnit != null && unit != null) {
                    TextCard((progress * 100), progressKm.toInt(), targetKm.toInt(), shortUnit, unit)
                }

                Spacer(modifier = Modifier.height(38.dp))

                if (ranking.size >= 3 ) {
                    Text("Top 3")
                    TopThreeRanking(ranking)
                }
            }
        }
    }
}

@Composable
fun MemberTab(
    ranking: List<ParticipantRanking>,
    challenge: ChallengeData?,
    viewModel: ChallengeViewModel,
) {
    var showDialog by remember { mutableStateOf(false) }


    Box(modifier = Modifier.fillMaxSize()) {

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            itemsIndexed(ranking) { index, participant ->
                val unit = challenge?.goal?.getUnitLabel(participant.progress)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("${index + 1}.", style = MaterialTheme.typography.titleMedium)
                        Image(
                            painter = painterResource(R.drawable.avatar),
                            contentDescription = "Avatar",
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .size(45.dp)
                        )
                        Text(participant.name, style = MaterialTheme.typography.titleMedium)
                    }

                    Text("${participant.progress.toInt()} $unit", style = MaterialTheme.typography.titleMedium)
                }
            }
        }

        FloatingActionButton(
            onClick = { showDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ) {
            Icon(painter = painterResource(R.drawable.user_plus), contentDescription = "Mitglied hinzufügen")
        }

        if (showDialog) {
            AddParticipantDialog(
                onDismiss = { showDialog = false },
                onAdd = { email ->
                    challenge?.id?.let { viewModel.addParticipantByEmail(it, email) }
                    showDialog = false
                }
            )
        }
    }
}



@Composable
fun AddParticipantDialog(
    onDismiss: () -> Unit,
    onAdd: (String) -> Unit
) {
    var email by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Mitglied hinzufügen") },
        text = {
            Column {
                Text("Gib die E-Mail-Adresse des Nutzers ein:")
                Spacer(Modifier.height(8.dp))
                TextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("E-Mail") }
                )
            }
        },
        confirmButton = {
            Button(onClick = { onAdd(email) }) {
                Text("Hinzufügen")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Abbrechen")
            }
        }
    )
}




@Composable
fun TextCard(prozent: Float, progress: Int, goal: Int, shortUnit: String, unit:String){
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
                    text = "${progress} $shortUnit",
                    style = MaterialTheme.typography.displayMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "von ${goal} $unit",
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

@Composable
fun TopThreeRanking(ranking: List<ParticipantRanking>) {
    val topThree = ranking.take(3)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Bottom
    ) {

        TopRankingItem(
            rank = 2,
            participant = topThree[1],
            imageSize = 64.dp,
            offsetY = 12.dp
        )

        TopRankingItem(
            rank = 1,
            participant = topThree[0],
            imageSize = 80.dp,
            offsetY = 0.dp
        )

        TopRankingItem(
            rank = 3,
            participant = topThree[2],
            imageSize = 64.dp,
            offsetY = 12.dp
        )
    }
}

@Composable
fun TopRankingItem(
    rank: Int,
    participant: ParticipantRanking,
    imageSize: Dp,
    offsetY: Dp
) {
    if (rank > 3) return
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.offset(y = offsetY)
    ) {
        Text(text = "$rank", style = MaterialTheme.typography.displayMedium)
        Spacer(modifier = Modifier.height(4.dp))
        Image(
            painter = painterResource(R.drawable.avatar),
            contentDescription = "Profilbild",
            modifier = Modifier.size(imageSize)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = participant.name,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 1
        )
        Text(
            text = "%.2f km".format(participant.progress),
            style = MaterialTheme.typography.labelSmall
        )
    }
}




