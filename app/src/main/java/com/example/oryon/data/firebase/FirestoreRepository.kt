package com.example.oryon.data.firebase

import com.example.oryon.data.RunSession
import com.example.oryon.data.UserData
import kotlinx.coroutines.flow.Flow

interface FirestoreRepository {
    suspend fun createUser(user: UserData)
    suspend fun getUserById(id: String): UserData?

    suspend fun saveRunSession(distanceMeters: Float, durationSec: Long, pace: Float)
    suspend fun getAllRunSessionsForUser(userId: String): Flow<List<RunSession>>
}