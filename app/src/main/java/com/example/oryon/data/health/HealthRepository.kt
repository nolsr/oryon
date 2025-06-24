package com.example.oryon.data.health

interface HealthRepository {
    suspend fun insertRunSession(startTimeMillis: Long, endTimeMillis: Long, distanceMeters: Float)
    suspend fun getAllRunSessions(): List<RunSessionData>
}