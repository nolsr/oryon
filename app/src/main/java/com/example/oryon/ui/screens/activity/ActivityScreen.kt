package com.example.oryon.ui.screens.activity

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.example.oryon.data.health.HealthRepository
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import com.example.oryon.data.RunSession
import com.example.oryon.data.health.RunSessionData
import kotlinx.coroutines.launch

@Composable
fun ActivityScreen(viewModel: ActivityViewModel) {
    val runSessions by viewModel.runSessions.collectAsState()
    var selectedTabIndex by remember { mutableStateOf(0) }

    val tabTitles = listOf("Statistik", "Läufe")

    Column(modifier = Modifier.fillMaxSize()) {
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
            0 -> RunStatisticsTab(sessions = runSessions)
            1 -> RunListTab(sessions = runSessions)
        }
    }
}

@Composable
fun RunListTab(sessions: List<RunSession>) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(sessions) { session ->
            RunCard(session)
        }
    }
}

@Composable
fun RunStatisticsTab(sessions: List<RunSession>) {
    val totalDistance = sessions.fold(0.0) { acc, session -> acc + session.distanceMeters } / 1000.0

    val paceList = sessions.mapNotNull { it.pace.takeIf { pace -> pace > 0 } }
    val averagePace = if (paceList.isNotEmpty()) paceList.sum() / paceList.size else null

    val totalDuration = sessions.fold(0L) { acc, session -> acc + session.durationSeconds }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Läufe insgesamt: ${sessions.size}")
        Text("Gesamtdistanz: %.2f km".format(totalDistance))
        Text("Gesamtdauer: %d:%02d h".format(totalDuration / 3600, (totalDuration % 3600) / 60))
        Text("Ø Pace: ${averagePace?.let { "%.2f".format(it) } ?: "--"} min/km")
    }
}

@Composable
fun RunCard(session: RunSession) {
    Card(
        modifier = Modifier
            .width(250.dp)
            .height(150.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = session.date.toDate().toString(), style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Strecke: %.2f km".format(session.distanceMeters / 1000))
            Text("Zeit: %d:%02d min".format(session.durationSeconds / 60, session.durationSeconds % 60))
            Text("Pace: %.2f min/km".format(session.pace))
        }
    }
}
