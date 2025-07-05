package com.example.oryon.ui.screens.detail

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.oryon.ui.screens.activity.ActivityViewModel
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

@Composable
fun RunDetailScreen(runId: String, viewModel: ActivityViewModel) {
    val sessions by viewModel.runSessions.collectAsState()

    val session = sessions.find { it.id == runId }

    if (session == null) {
        // Ladezustand oder Fehleranzeige
        Text("Lade Daten ...")
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text("Lauf vom ${session.date.toDate().toGermanString()}", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(16.dp))

            Text("Strecke: %.2f km".format(session.distanceMeters / 1000f))
            Text("Dauer: %d:%02d min".format(session.durationSeconds / 60, session.durationSeconds % 60))
            Text("Pace: %.2f min/km".format(session.pace))
        }
    }
}



fun Date.toGermanString(): String =
    this.toInstant()
        .atZone(ZoneId.systemDefault())
        .toLocalDate()
        .format(DateTimeFormatter.ofPattern("dd. MMMM yyyy", Locale.GERMAN))
