package com.example.oryon.ui.screens.runDetail

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.VerticalAlignmentLine
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.oryon.R
import com.example.oryon.data.getCalories
import com.example.oryon.ui.screens.activity.ActivityViewModel
import com.example.oryon.ui.theme.FiraSansFontFamily
import com.google.firebase.firestore.GeoPoint
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

@Composable
fun RunDetailScreen(runId: String, viewModel: ActivityViewModel, navController: NavController) {
    val sessions by viewModel.runSessions.collectAsState()
    val session = sessions.find { it.id == runId }

    if (session == null) {
        Text("Lade Daten ...")
    } else {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Image(
                    painter = painterResource(id = R.drawable.run_details_screen_bg),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f))
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(42.dp)
            ) {
                Column {
                    val date = session.date.toDate()

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                navController.navigate("activity")
                            }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Zurück",
                            tint = Color.White
                        )
                        Text(
                            text = "Laufdetails",
                            modifier = Modifier.padding(start = 8.dp),
                            style = MaterialTheme.typography.displayMedium.copy(
                                color = Color.White,
                                fontSize = 32.sp
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                        RoutePathPreview(route = session.route, modifier = Modifier
                            .height(200.dp))
                    }
                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = date.toWeekday(),
                        style = MaterialTheme.typography.displayMedium.copy(
                            color = Color.White,
                            fontSize = 32.sp
                        )
                    )
                    Text(
                        text = date.toDisplayString(),
                        style = MaterialTheme.typography.displayMedium.copy(
                            color = Color.White,
                            fontSize = 32.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "%.1f".format(session.distanceMeters / 1000f),
                                style = MaterialTheme.typography.displayMedium.copy(
                                    color = Color(0xFFFF6F00), // Orange
                                    fontSize = 100.sp
                                )
                            )
                            Text(
                                text = "Kilometer",
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    color = Color.White,
                                    fontSize = 28.sp
                                )
                            )
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${session.durationSeconds / 60}",
                                style = MaterialTheme.typography.displayMedium.copy(
                                    color = Color.White,
                                    fontSize = 100.sp
                                )
                            )
                            Text(
                                text = "Minuten",
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    color = Color.White,
                                    fontSize = 28.sp
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    HorizontalDivider(
                        color = Color.White,
                        thickness = 1.dp,
                    )
                    InfoRow("Pace", "%.2f".format(session.pace), "min/km")
                    HorizontalDivider(
                        color = Color.White,
                        thickness = 1.dp
                    )
                    InfoRow("Kalorien", "${session.getCalories()}", "kcal")
                    HorizontalDivider(
                        color = Color.White,
                        thickness = 1.dp,
                     )
                }
            }
        }
    }
}

@Composable
fun InfoRow(label: String, value: String, unit: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge.copy(color = Color.White)
        )
        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                text = value,
                style = MaterialTheme.typography.displayLarge.copy(
                    color = Color.White,
                    fontSize = 28.sp
                )
            )
            Text(
                text = unit,
                style = MaterialTheme.typography.bodyLarge.copy(color = Color.White)
            )
        }
    }
}

@Composable
fun RoutePathPreview(route: List<GeoPoint>, modifier: Modifier = Modifier) {
    if (route.size < 2) return

    BoxWithConstraints(
        modifier = modifier
            .aspectRatio(1f)
            .padding(16.dp)
    ) {
        val widthPx = constraints.maxWidth.toFloat()
        val heightPx = constraints.maxHeight.toFloat()
        val offsets: List<Offset> = scaleGeoPointsToCanvas(route, widthPx, heightPx)

        Canvas(modifier = Modifier.fillMaxSize()) {
            for (i in 0 until offsets.size - 1) {
                drawLine(
                    color = Color(0xFFFF6F00),
                    start = offsets[i],
                    end = offsets[i + 1],
                    strokeWidth = 16f,
                    cap = StrokeCap.Round
                )
            }
        }
    }
}

fun scaleGeoPointsToCanvas(points: List<GeoPoint>, width: Float, height: Float): List<Offset> {
    if (points.isEmpty()) return emptyList()

    val minLat = points.minOf { it.latitude }
    val maxLat = points.maxOf { it.latitude }
    val minLon = points.minOf { it.longitude }
    val maxLon = points.maxOf { it.longitude }

    val latRange = maxLat - minLat
    val lonRange = maxLon - minLon

    return points.map { point ->
        val x = ((point.longitude - minLon) / lonRange).toFloat() * width
        val y = height - ((point.latitude - minLat) / latRange).toFloat() * height
        Offset(x, y)
    }
}


fun Date.toDisplayString(): String =
    this.toInstant()
        .atZone(ZoneId.systemDefault())
        .toLocalDate()
        .format(DateTimeFormatter.ofPattern("dd. MMMM yyyy", Locale.GERMAN))

fun Date.toWeekday(): String =
    this.toInstant()
        .atZone(ZoneId.systemDefault())
        .dayOfWeek
        .getDisplayName(java.time.format.TextStyle.FULL, Locale.GERMAN)
