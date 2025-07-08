package com.example.oryon.data.firebase

import com.example.oryon.data.ChallengeData
import com.example.oryon.data.RunSession
import com.example.oryon.data.UserData
import kotlinx.coroutines.flow.Flow

interface FirestoreRepository {
    //User functions
    suspend fun createUser(user: UserData)
    suspend fun getUserById(id: String): UserData?
    suspend fun findUserByEmail(email: String): UserData?

    //RunSession functions
    suspend fun saveRunSession(distanceMeters: Float, durationSec: Long, pace: Float)
    suspend fun getAllRunSessionsForUser(userId: String): Flow<List<RunSession>>

    //Challenge functions
    suspend fun getUserChallenges(): Flow<List<ChallengeData>>
    suspend fun addUserToChallenge(challengeId: String, userId: String)
    suspend fun updateChallengeProgressAfterRun(distanceMeters: Float, durationSec: Long)
}