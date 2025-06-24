package com.example.oryon.data.firebase

import kotlinx.coroutines.flow.Flow

interface FirestoreRepository {
    suspend fun createUser(user: UserData)
    suspend fun getUserById(id: String): UserData?
}