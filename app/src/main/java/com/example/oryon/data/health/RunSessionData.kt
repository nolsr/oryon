package com.example.oryon.data.health

import java.time.Instant

data class RunSessionData(
    val startTime: Instant,
    val endTime: Instant,
    val distanceMeters: Double,
    val elapsedSeconds: Long,
    val title: String
)
