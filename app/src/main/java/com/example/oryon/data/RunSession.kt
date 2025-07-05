package com.example.oryon.data

import com.google.firebase.Timestamp
import java.util.Date
import kotlin.time.Duration

data class RunSession(
    val id: String = "",
    val date: Timestamp = Timestamp(Date(0)),
    val distanceMeters: Float = 0f,
    val durationSeconds: Long = 0L,
    val pace: Float = 0f
)
