package com.example.oryon.data

import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint
import java.util.Date
import kotlin.time.Duration

data class RunSession(
    val id: String = "",
    val date: Timestamp = Timestamp(Date(0)),
    val distanceMeters: Float = 0f,
    val durationSeconds: Long = 0L,
    val pace: Float = 0f,
    val route: List<GeoPoint> = emptyList()
)

fun RunSession.getCalories(weightKg: Int = 70): Int {
    val km = distanceMeters / 1000f
    return (km * weightKg).toInt()
}
