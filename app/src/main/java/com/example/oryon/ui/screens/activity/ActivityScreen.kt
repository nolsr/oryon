package com.example.oryon.ui.screens.activity

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.PagerSnapDistance
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.oryon.R
import com.example.oryon.data.RunSession
import com.example.oryon.data.health.RunSessionData
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun ActivityScreen(viewModel: ActivityViewModel) {
    val runSessions by viewModel.runSessions.collectAsState()
    val runSessionsThisWeek by viewModel.runSessionsThisWeek.collectAsState()
    val runSessionsThisWeekbyDay by viewModel.distanceByDay.collectAsState()
    var selectedTabIndex by remember { mutableStateOf(0) }

    val tabTitles = listOf("Statistik", "Läufe")

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
            0 -> RunStatisticsTab(sessions = runSessions, runSessionsThisWeek = runSessionsThisWeek, distanceByDay = runSessionsThisWeekbyDay)
            1 -> RunListTab(sessions = runSessions)
        }
    }
}

@Composable
fun RunListTab(sessions: List<RunSession>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(sessions) { session ->
            RunCard(session)
        }
    }
}

@Composable
fun RunStatisticsTab(sessions: List<RunSession>, runSessionsThisWeek: List<RunSession>, distanceByDay: Map<String, Float>) {
    val totalDistance = sessions.fold(0.0) { acc, session -> acc + session.distanceMeters } / 1000.0
    val thisWeekDistance = runSessionsThisWeek.fold(0.0) { acc, session -> acc + session.distanceMeters } / 1000.0

    val totalDuration = sessions.fold(0L) { acc, session -> acc + session.durationSeconds }
    val thisWeekDuration = runSessionsThisWeek.fold(0L) { acc, session -> acc + session.durationSeconds }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item {
            Text("Diese Woche", style = MaterialTheme.typography.bodyLarge)
        }

        item {
            TextCard(
                runSessionsThisWeek.size.toString(),
                "%.2f".format(thisWeekDistance),
                "%d:%02d".format(thisWeekDuration / 3600, (thisWeekDuration % 3600) / 60)
            )
        }

        item {
            Spacer(modifier = Modifier.height(10.dp))
            WeeklyDistanceChart(data = distanceByDay)
        }

        item {
            Spacer(modifier = Modifier.height(32.dp))
            Text("Läufe insgesamt", style = MaterialTheme.typography.bodyLarge)
        }

        item {
            TextCard(
                sessions.size.toString(),
                "%.2f".format(totalDistance),
                "%d:%02d".format(totalDuration / 3600, (totalDuration % 3600) / 60)
            )
        }
    }
}

@Composable
fun TextCard(runs: String, distance: String, duration: String){
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
                Text(
                    text = "${runs}.",
                    style = MaterialTheme.typography.displayMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Läufe",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "${distance}KM",
                    style = MaterialTheme.typography.displayMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "gelaufen",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "${duration}H",
                    style = MaterialTheme.typography.displayMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Laufzeit",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }

}

@Composable
fun WeeklyDistanceChart(data: Map<String, Float>, modifier: Modifier = Modifier) {
    val days = listOf("Mo", "Di", "Mi", "Do", "Fr", "Sa", "So")
    val maxDistance = (data.values.maxOrNull() ?: 1f).coerceAtLeast(1f)

    val maxBarHeight = 150.dp
    val yAxisWidth = 50.dp
    val spacingBetweenBars = 16.dp
    val barWidth = 24.dp

    Row(modifier = modifier.height(maxBarHeight + 30.dp)) {
        Column(
            modifier = Modifier
                .width(yAxisWidth)
                .height(maxBarHeight)
                .padding(end = 4.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.End
        ) {
            Text(text = "%.1f km".format(maxDistance), style = MaterialTheme.typography.bodySmall)
            Text(text = "%.1f km".format(maxDistance / 2), style = MaterialTheme.typography.bodySmall)
            Text(text = "0 km", style = MaterialTheme.typography.bodySmall)
        }

        Spacer(modifier = Modifier.width(4.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(maxBarHeight + 30.dp)
        ) {

            // Balken + Labels
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(maxBarHeight + 30.dp)
                    .align(Alignment.BottomStart),
                horizontalArrangement = Arrangement.spacedBy(spacingBetweenBars),
                verticalAlignment = Alignment.Bottom
            ) {
                days.forEach { day ->
                    val distance = data[day] ?: 0f
                    val barHeightRatio = distance / maxDistance

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom,
                        modifier = Modifier.width(barWidth)
                    ) {
                        Box(
                            modifier = Modifier
                                .height(maxBarHeight * barHeightRatio)
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(4.dp))
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(day, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}




@Composable
fun RunCard(session: RunSession) {
    val formattedDate = remember(session.date) {
        val sdf = SimpleDateFormat("d. MMMM yyyy", Locale.GERMANY)
        sdf.format(session.date.toDate())
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.lucide_route),
                contentDescription = "Laufbild",
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimary),
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(90.dp)
                    )
                    .padding(6.dp)
                    .size(32.dp),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))


            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = formattedDate,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "%.2f KM".format(session.distanceMeters / 1000),
                    style = MaterialTheme.typography.displayLarge
                )
            }

            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = "Mehr anzeigen",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
