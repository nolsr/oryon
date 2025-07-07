package com.example.oryon.ui.screens.challenge

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.oryon.R
import com.example.oryon.data.ChallengeData
import com.example.oryon.ui.screens.activity.ActivityViewModel
import com.mapbox.maps.extension.style.expressions.dsl.generated.color

@Composable
fun ChallengeScreen( viewModel: ChallengeViewModel, navController: NavController) {
    val challenges by viewModel.challenges.collectAsState()

    ChallengeList(challenges, navController)
}

@Composable
fun ChallengeList(challenges: List<ChallengeData>, navController: NavController) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(challenges) { challenge ->
            ChallengeCard(
                imageRes = R.drawable.challenge_hero_1,
                title = challenge.name,
                subtitle = challenge.type
            ) {
                Text(text = "Typ: ${challenge.type}", style = MaterialTheme.typography.bodyMedium)

                Spacer(modifier = Modifier.height(8.dp))

                Text(text = "Teilnehmer:", style = MaterialTheme.typography.bodySmall)
                challenge.participants.forEach { participant ->
                    Text(
                        text = "${participant.name ?: participant.uid}, ",
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = {
                            navController.navigate("challengeDetail/${challenge.id}")
                        }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.arrow_up_right),
                            contentDescription = "Öffnen",
                            modifier = Modifier.padding(end = 4.dp)
                        )
                        Text("Öffnen")
                    }
                }

            }

        }
    }
}

@Composable
fun ChallengeCard(challenge: ChallengeData, onCardClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onCardClick() },
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

@Composable
fun ChallengeCard(
    imageRes: Int,
    title: String,
    subtitle: String,
    content: @Composable () -> Unit
) {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Column() {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            )
            {
                Image(
                    painter = painterResource(id = imageRes),
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
                    Text(
                        text = title,
                        style = MaterialTheme.typography.displayMedium.copy(color = Color.White)
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodyMedium.copy(color = Color.White)
                    )
                }
            }

            Column(modifier = Modifier.padding(16.dp)) {
                content()
            }
        }
    }
}


